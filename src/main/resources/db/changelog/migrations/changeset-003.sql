--liquibase formatted sql
--changeset ASSETS.1.00.00.003-001 failOnError:true splitStatements:true context:update runOnChange: false
create table IF NOT EXISTS instrument_price
(
    id   bigint not null auto_increment,
    symbol        varchar(16) not null,
    datetime datetime not null,
    value decimal(12, 2) not null,
    currency     char(3) not null,
    primary key (id)
) engine = InnoDB;

