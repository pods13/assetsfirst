--liquibase formatted sql
--changeset ASSETS.1.00.00.009-001 failOnError:true splitStatements:true context:update runOnChange: false
ALTER TABLE user
    ADD first_login boolean default false;
