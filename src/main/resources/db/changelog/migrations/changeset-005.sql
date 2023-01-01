--liquibase formatted sql
--changeset ASSETS.1.00.00.005-001 failOnError:true splitStatements:true context:update runOnChange: false
DROP TABLE IF EXISTS instrument_price;


CREATE TABLE IF NOT EXISTS instrument_price
(
    id            bigint         NOT NULL AUTO_INCREMENT,
    instrument_id bigint         NOT NULL,
    datetime      datetime       NOT NULL,
    value         decimal(16, 5) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;


ALTER TABLE instrument_price
    ADD CONSTRAINT fk__instrument_price__instrument_id__instrument
        FOREIGN KEY (instrument_id) REFERENCES instrument (id);
