--liquibase formatted sql
--changeset ASSETS.1.00.00.001-001 failOnError:true splitStatements:true context:update runOnChange: false
alter table dividend DROP INDEX uq_dividend_record_date;
alter table dividend
    add constraint uq_dividend_instrument_id_record_date unique (instrument_id, record_date);
