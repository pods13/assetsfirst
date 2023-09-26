--liquibase formatted sql
--changeset ASSETS.1.00.00.001-001 failOnError:true splitStatements:true context:update runOnChange: false

DROP INDEX instrument_ticker_exchange_id_key on instrument;
ALTER TABLE instrument CHANGE ticker symbol varchar(255) not null;
ALTER TABLE instrument
    add constraint instrument_symbol_exchange_id_key unique (symbol, exchange_id);

ALTER TABLE portfolio
    ADD COLUMN currency char(3) default 'RUB';
