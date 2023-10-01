--liquibase formatted sql
--changeset ASSETS.1.00.00.002-001 failOnError:true splitStatements:true context:update runOnChange: false
ALTER TABLE exchange_rate
    MODIFY conversion_rate decimal(19, 10);
