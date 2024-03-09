--liquibase formatted sql
--changeset ASSETS.1.00.00.004-001 failOnError:true splitStatements:true context:update runOnChange: false

ALTER TABLE instrument
    DROP FOREIGN KEY fk__instrument__exchange_id__exchange;
ALTER TABLE instrument
    DROP INDEX instrument_symbol_exchange_id_key;

ALTER TABLE instrument
    ADD COLUMN exchange_code varchar(255);

update instrument i
    inner join exchange e on e.id = i.exchange_id
set i.exchange_code = e.code;

ALTER TABLE instrument MODIFY exchange_code varchar(255) NOT NULL;

ALTER TABLE instrument
    add constraint instrument_symbol_exchange_code_key unique (symbol, exchange_code);

ALTER TABLE instrument
    DROP COLUMN exchange_id;

DROP TABLE IF EXISTS exchange;

