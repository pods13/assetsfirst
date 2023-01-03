--liquibase formatted sql
--changeset ASSETS.1.00.00.007-001 failOnError:true splitStatements:true context:update runOnChange: false
ALTER TABLE instrument
    ADD COLUMN currency char(3);

UPDATE instrument
SET currency = (select e.currency from exchange e where e.id = instrument.exchange_id)
where currency is null;

ALTER TABLE instrument
    MODIFY currency char(3) NOT NULL;
