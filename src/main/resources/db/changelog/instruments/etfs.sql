--liquibase formatted sql
--changeset ASSETS.1.00.00.etfs-001 failOnError:true splitStatements:true context:update runOnChange: true

insert ignore instrument
(instrument_type, attributes, symbol, company_id, currency, exchange_code)
values ('ETF', '{"name": "ZPIF PARUS-LOGISTICS"}', 'RU000A105328', null, 'RUB', 'MCX');

insert ignore instrument
(instrument_type, attributes, symbol, company_id, currency, exchange_code)
values ('ETF', '{"name": "ZPIF PARUS-NORD"}', 'RU000A104KU3', null, 'RUB', 'MCX');

insert ignore instrument
(instrument_type, attributes, symbol, company_id, currency, exchange_code)
values ('ETF', '{"name": "BPIF Gold"}', 'GOLD', null, 'RUB', 'MCX');

insert ignore instrument
(instrument_type, attributes, symbol, company_id, currency, exchange_code)
values ('ETF', '{"name": "ZPIF PARUS-DVN"}', 'RU000A1068X9', null, 'RUB', 'MCX');