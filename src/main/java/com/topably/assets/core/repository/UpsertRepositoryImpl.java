package com.topably.assets.core.repository;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.util.Assert;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class UpsertRepositoryImpl<T, ID extends Serializable> implements UpsertRepository<T, ID> {

    private static final String COMMA = ",";
    private static final String OPEN_BRACKET = "(";
    private static final String CLOSE_BRACKET = ")";
    private static final int MAX_BIND_VARIABLE_LIMIT = 32767 - 1;

    @PersistenceContext
    private EntityManager entityManager;

    private List<Field> getColumnFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields.stream().filter(field -> field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(JoinColumn.class)
            || field.isAnnotationPresent(EmbeddedId.class)).collect(Collectors.toList());
    }

    private String populateTableColumns(List<Field> columnFields) {
        StringBuilder queryBuilder = new StringBuilder();
        for (Field field : columnFields) {
            String columnName;
            if (field.isAnnotationPresent(Column.class))
                columnName = field.getAnnotation(Column.class).name();
            else if (field.isAnnotationPresent(JoinColumn.class))
                columnName = field.getAnnotation(JoinColumn.class).name();
            else {
                queryBuilder.append(populateTableColumns(getColumnFields(field.getType()))).append(COMMA);
                continue;
            }
            queryBuilder.append(columnName).append(COMMA);
        }
        queryBuilder.deleteCharAt(queryBuilder.lastIndexOf(COMMA));
        return queryBuilder.toString();
    }

    private String formInitialQuery(List<Field> columnFields, String tableName) {
        return "INSERT IGNORE INTO " +
            tableName +
            OPEN_BRACKET +
            populateTableColumns(columnFields) +
            CLOSE_BRACKET +
            " VALUES ";
    }

    private String populateColumnParams(List<Field> columnFields, int index) {
        StringBuilder valuesBuilder = new StringBuilder();
        for (Field field : columnFields) {
            if (field.isAnnotationPresent(EmbeddedId.class)) {
                valuesBuilder.append(populateColumnParams(getColumnFields(field.getType()), index)).append(COMMA);
            } else {
                valuesBuilder.append(":");
                valuesBuilder.append(field.getName().trim()).append(index);
                valuesBuilder.append(COMMA);
            }
        }
        valuesBuilder.deleteCharAt(valuesBuilder.lastIndexOf(COMMA));
        return valuesBuilder.toString();
    }

    @SneakyThrows
    private <K> void populateColumnValues(List<Field> columnFields, K entity, Query nativeQuery, int index) {
        for (Field field : columnFields) {
            try {
                field.setAccessible(true);
                if (field.isAnnotationPresent(EmbeddedId.class)) {
                    var primaryKey = (ID) entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
                    populateColumnValues(getColumnFields(field.getType()), primaryKey, nativeQuery, index);
                } else if(field.isAnnotationPresent(JoinColumn.class)) {
                    var foreignKey = (ID) entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(field.get(entity));
                    nativeQuery.setParameter(field.getName() + index, foreignKey);
                } else {
                    nativeQuery.setParameter(field.getName() + index, field.get(entity));
                }
                field.setAccessible(false);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Exception while upserting a record", e);
            }
        }
    }

    @Override
    public T upsert(T entity) {
        Assert.notNull(entity, "Entity must not be null.");
        final List<Field> columnFields = getColumnFields(entity.getClass());
        final String query = formInitialQuery(columnFields, entity.getClass().getAnnotation(Table.class).name()) + OPEN_BRACKET +
            populateColumnParams(columnFields, 0) + CLOSE_BRACKET;
        final Query nativeQuery = entityManager.createNativeQuery(query, entity.getClass());
        populateColumnValues(columnFields, entity, nativeQuery, 0);
        nativeQuery.executeUpdate();
        return entity;
    }

    @Override
    @SneakyThrows
    public List<T> upsertAll(List<T> entities) {
        Assert.notNull(entities, "Entity must not be null.");
        if (entities.isEmpty()) {
            return Collections.emptyList();
        }
        final T sampleEntity = entities.get(0);
        final List<Field> columnFields = getColumnFields(sampleEntity.getClass());
        Assert.notEmpty(columnFields, "Define the entity with proper JPA annotations");

        final int bindVariableCount = columnFields.size() * entities.size();
        final int entitiesPerStatement = bindVariableCount > MAX_BIND_VARIABLE_LIMIT ? MAX_BIND_VARIABLE_LIMIT / columnFields.size() : entities.size();
        List<List<T>> partitionedEntityList = ListUtils.partition(entities, entitiesPerStatement);
        partitionedEntityList.forEach(partitionedEntities -> {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append(formInitialQuery(columnFields, sampleEntity.getClass().getAnnotation(Table.class).name()));
            AtomicInteger index = new AtomicInteger(0);
            partitionedEntities.forEach(entity -> queryBuilder.append(OPEN_BRACKET).append(populateColumnParams(columnFields, index.getAndIncrement()))
                .append(CLOSE_BRACKET).append(COMMA));
            queryBuilder.deleteCharAt(queryBuilder.lastIndexOf(COMMA));
            final Query nativeQuery = entityManager.createNativeQuery(queryBuilder.toString(), sampleEntity.getClass());
            index.set(0);
            partitionedEntities.forEach(entity -> populateColumnValues(columnFields, entity, nativeQuery, index.getAndIncrement()));
            nativeQuery.executeUpdate();
        });
        return entities;
    }
}
