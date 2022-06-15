package com.topably.assets.core.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

public interface UpsertRepository<T, ID extends Serializable> {

    @Transactional
    @Modifying
    T upsert(T entity);

    @Transactional
    @Modifying
    List<T> upsertAll(List<T> entities);
}
