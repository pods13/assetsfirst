--liquibase formatted sql
--changeset ASSETS.1.00.00.008-001 failOnError:true splitStatements:true context:update runOnChange: false
ALTER TABLE tag_category
ADD INDEX tag_category__code__key (code);
