--liquibase formatted sql
--changeset ASSETS.1.00.00.009-001 failOnError:true splitStatements:true context:update runOnChange: false
create table IF NOT EXISTS split(
    id            bigint not null auto_increment,
    ratio        varchar(100), --1:100 or 100:1
    payable_on  date not null,
    ex_date      date not null,
    announced   date,
    instrument_id bigint not null,
    primary key (id)
    );

alter table split
    add constraint fk__split__instrument_id__instrument
        foreign key (instrument_id)
            references instrument (id);

ALTER TABLE dividend
    MODIFY amount decimal(19, 5);
ALTER TABLE dividend
    ADD COLUMN unadjusted_amount decimal(19, 5) NOT NULL DEFAULT '0';
UPDATE dividend set unadjusted_amount = amount;

ALTER TABLE dividend
    ADD COLUMN last_split_applied date;