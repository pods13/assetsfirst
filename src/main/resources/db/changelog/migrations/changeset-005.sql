--liquibase formatted sql
--changeset ASSETS.1.00.00.005-001 failOnError:true splitStatements:true context:update runOnChange: false

ALTER TABLE instrument
    ADD COLUMN name varchar(255);

UPDATE instrument i
    set i.name = JSON_EXTRACT(i.attributes, '$.name')
WHERE i.instrument_type = 'ETF';

UPDATE instrument i
    inner join company c on c.id = i.company_id
set i.name = c.name
WHERE i.instrument_type = 'STOCK';


UPDATE instrument i
set i.name = i.symbol
WHERE i.instrument_type = 'FX';

ALTER TABLE instrument MODIFY name varchar(255) NOT NULL;
