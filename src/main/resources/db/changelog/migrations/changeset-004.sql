--liquibase formatted sql
--changeset ASSETS.1.00.00.004-001 failOnError:true splitStatements:true context:update runOnChange: false
ALTER TABLE trade
    MODIFY COLUMN date date;
