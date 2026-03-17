--liquibase formatted sql
--changeset ASSETS.1.00.00.010-001 failOnError:true splitStatements:true context:update runOnChange: false
CREATE INDEX instrument_price__instrument_id_datetime__key
    ON instrument_price (instrument_id, datetime DESC);