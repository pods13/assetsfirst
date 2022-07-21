package com.topably.assets.integration.base;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.springframework.test.context.MergedContextConfiguration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

public class EnableMySqlTestContainerContextCustomizerFactory implements ContextCustomizerFactory {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Inherited
    public @interface EnabledMySqlTestContainer {
    }

    @Override
    public ContextCustomizer createContextCustomizer(Class<?> testClass,
                                                     List<ContextConfigurationAttributes> configAttributes) {
        if (!(AnnotatedElementUtils.hasAnnotation(testClass, EnabledMySqlTestContainer.class))) {
            return null;
        }
        return new MySqlTestContainerContextCustomizer();
    }

    @Slf4j
    @EqualsAndHashCode // See ContextCustomizer java doc
    private static class MySqlTestContainerContextCustomizer implements ContextCustomizer {

        private static final DockerImageName image = DockerImageName
                .parse("mysql")
                .withTag("5.7.21");

        @Override
        public void customizeContext(ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
            var mySqlContainer = new MySQLContainer<>(image);
            mySqlContainer.start();
            log.warn(mySqlContainer.getJdbcUrl());
            var properties = Map.<String, Object>of(
                    "spring.datasource.url", mySqlContainer.getJdbcUrl(),
                    "spring.datasource.username", mySqlContainer.getUsername(),
                    "spring.datasource.password", mySqlContainer.getPassword(),
                    // Prevent any in memory db from replacing the data source
                    // See @AutoConfigureTestDatabase
                    "spring.test.database.replace", "NONE"
            );
            var propertySource = new MapPropertySource("MySQLContainer Test Properties", properties);
            context.getEnvironment().getPropertySources().addFirst(propertySource);
        }

    }
}
