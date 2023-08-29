--liquibase formatted sql
--changeset ASSETS.1.00.00.000-001 failOnError:true splitStatements:true context:update runOnChange: false

create table IF NOT EXISTS authority
(
    id   bigint not null auto_increment,
    role varchar(255),
    primary key (id)
) engine = InnoDB;

create table IF NOT EXISTS broker
(
    id   bigint not null auto_increment,
    name varchar(255),
    primary key (id)
) engine = InnoDB;

create table IF NOT EXISTS company
(
    id          bigint not null auto_increment,
    name        varchar(255),
    industry_id bigint,
    primary key (id)
) engine = InnoDB;

create table IF NOT EXISTS dividend
(
    id            bigint not null auto_increment,
    amount        decimal(19, 2),
    declare_date  date,
    pay_date      date,
    record_date   date,
    instrument_id bigint not null,
    primary key (id)
) engine = InnoDB;

create table IF NOT EXISTS exchange
(
    id           bigint not null auto_increment,
    code         varchar(255),
    country_code varchar(255),
    currency     varchar(255),
    name         varchar(255),
    primary key (id)
) engine = InnoDB;

create table IF NOT EXISTS exchange_rate
(
    id                   bigint not null auto_increment,
    conversion_rate      decimal(19, 2),
    date                 date,
    destination_currency varchar(255),
    source_currency      varchar(255),
    primary key (id)
) engine = InnoDB;

create table IF NOT EXISTS industry
(
    id        bigint not null auto_increment,
    name      varchar(255),
    sector_id bigint,
    primary key (id)
) engine = InnoDB;

create table IF NOT EXISTS instrument
(
    instrument_type varchar(31) not null,
    id              bigint      not null auto_increment,
    attributes      json,
    ticker          varchar(255),
    exchange_id     bigint      not null,
    company_id      bigint,
    primary key (id)
) engine = InnoDB;

create table IF NOT EXISTS portfolio
(
    id           bigint not null auto_increment,
    dashboard_id bigint not null,
    user_id      bigint not null,
    primary key (id)
) engine = InnoDB;

create table IF NOT EXISTS portfolio_dashboard
(
    id    bigint not null auto_increment,
    cards json,
    primary key (id)
) engine = InnoDB;

create table IF NOT EXISTS portfolio_holding
(
    id            bigint not null auto_increment,
    average_price decimal(20, 4),
    quantity      decimal(12, 0),
    instrument_id bigint not null,
    portfolio_id  bigint not null,
    primary key (id)
) engine = InnoDB;

create table IF NOT EXISTS sector
(
    id   bigint not null auto_increment,
    name varchar(255),
    primary key (id)
) engine = InnoDB;

create table IF NOT EXISTS trade
(
    id                   bigint  not null auto_increment,
    date                 datetime(6),
    operation            tinyint not null,
    price                decimal(20, 4),
    quantity             decimal(12, 0),
    broker_id            bigint  not null,
    portfolio_holding_id bigint  not null,
    primary key (id)
) engine = InnoDB;

create table IF NOT EXISTS user
(
    id                      bigint not null auto_increment,
    account_non_expired     bit,
    account_non_locked      bit,
    credentials_non_expired bit,
    enabled                 bit,
    password                varchar(255),
    username                varchar(255),
    primary key (id)
) engine = InnoDB;

create table IF NOT EXISTS user_authority
(
    user_id      bigint not null,
    authority_id bigint not null,
    primary key (user_id, authority_id)
) engine = InnoDB;

alter table company
    add constraint uq_company_name unique (name);

alter table dividend
    add constraint uq_dividend_record_date unique (record_date);

alter table exchange_rate
    add constraint uq_exchange_rate unique (source_currency, destination_currency, date);

alter table industry
    add constraint uq_industry_name unique (name);

alter table instrument
    add constraint instrument_ticker_exchange_id_key unique (ticker, exchange_id);

alter table portfolio
    add constraint uq_portfolio_user_id unique (user_id);

alter table portfolio
    add constraint uq_portfolio_dashboard_id unique (dashboard_id);

alter table sector
    add constraint uq_sector_name unique (name);

alter table company
    add constraint fk__company__industry_id__industry
        foreign key (industry_id)
            references industry (id);

alter table dividend
    add constraint fk__dividend__instrument_id__instrument
        foreign key (instrument_id)
            references instrument (id);

alter table industry
    add constraint fk__industry__sector_id__sector
        foreign key (sector_id)
            references sector (id);

alter table instrument
    add constraint fk__instrument__exchange_id__exchange
        foreign key (exchange_id)
            references exchange (id);

alter table instrument
    add constraint fk__instrument__company_id__company
        foreign key (company_id)
            references company (id);

alter table portfolio
    add constraint fk__portfolio__dashboard_id__portfolio_dashboard
        foreign key (dashboard_id)
            references portfolio_dashboard (id);

alter table portfolio
    add constraint fk__portfolio__user_id__user
        foreign key (user_id)
            references user (id);

alter table portfolio_holding
    add constraint fk__portfolio_holding__instrument_id__instrument
        foreign key (instrument_id)
            references instrument (id);

alter table portfolio_holding
    add constraint fk__portfolio_holding__portfolio_id__portfolio
        foreign key (portfolio_id)
            references portfolio (id);

alter table trade
    add constraint fk__trade__broker_id__broker
        foreign key (broker_id)
            references broker (id);

alter table trade
    add constraint fk__trade__portfolio_holding_id__portfolio_holding
        foreign key (portfolio_holding_id)
            references portfolio_holding (id);

alter table user_authority
    add constraint fk__user__authority_id__authority
        foreign key (authority_id)
            references authority (id);

alter table user_authority
    add constraint fk__user__user_id__authority
        foreign key (user_id)
            references user (id);

alter table dividend DROP INDEX uq_dividend_record_date;
alter table dividend
    add constraint uq_dividend_instrument_id_record_date unique (instrument_id, record_date);

update portfolio_dashboard SET CARDS = JSON_ARRAY()
where JSON_CONTAINS(cards, '{"containerType": "DIVIDENDS"}');

create table IF NOT EXISTS instrument_price
(
    id   bigint not null auto_increment,
    symbol        varchar(16) not null,
    datetime datetime not null,
    value decimal(12, 2) not null,
    currency     char(3) not null,
    primary key (id)
) engine = InnoDB;

ALTER TABLE trade
    MODIFY COLUMN date date;

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

ALTER TABLE portfolio_holding
    DROP FOREIGN KEY fk__portfolio_holding__portfolio_id__portfolio;
ALTER TABLE portfolio_holding    add constraint fk__portfolio_position__portfolio_id__portfolio
    foreign key (portfolio_id)
        references portfolio (id);

ALTER TABLE portfolio_holding
    DROP FOREIGN KEY fk__portfolio_holding__instrument_id__instrument;

ALTER TABLE portfolio_holding    add constraint fk__portfolio_position__instrument_id__instrument
    foreign key (instrument_id)
        references instrument (id);

ALTER TABLE trade CHANGE portfolio_holding_id portfolio_position_id bigint  not null;

ALTER TABLE trade
    DROP FOREIGN KEY fk__trade__portfolio_holding_id__portfolio_holding;

ALTER TABLE trade add constraint fk__trade__portfolio_position_id__portfolio_position
    foreign key (portfolio_position_id)
        references portfolio_holding (id);

ALTER TABLE portfolio_holding RENAME portfolio_position;

ALTER TABLE instrument
    ADD COLUMN currency char(3);

UPDATE instrument
SET currency = (select e.currency from exchange e where e.id = instrument.exchange_id)
where currency is null;

ALTER TABLE instrument
    MODIFY currency char(3) NOT NULL;


create table IF NOT EXISTS tag_category
(
    id            bigint not null auto_increment,
    user_id       bigint not null,
    name          varchar(255) not null,
    color         char(7) not null,
    primary key (id),
    constraint fk__tag_category__user_id__user
        foreign key (user_id)
            references user (id)
) engine = InnoDB;

create table IF NOT EXISTS tag
(
    id            bigint not null auto_increment,
    name          varchar(255) not null,
    category_id       bigint not null,
    primary key (id),
    CONSTRAINT fk__tag__category_id__tag_category
        FOREIGN KEY (category_id) REFERENCES tag (id)
) engine = InnoDB;

create table IF NOT EXISTS portfolio_position_tag
(
    position_id      bigint not null,
    tag_id bigint not null,
    primary key (position_id, tag_id),
    constraint fk__portfolio_position__position_id__portfolio_position
        foreign key (position_id)
            references portfolio_position (id),
    constraint fk__portfolio_position__tag_id__tag
        foreign key (tag_id)
            references tag (id)
) engine = InnoDB;

ALTER TABLE user
    ADD first_login boolean default false;

INSERT INTO tag_category (user_id, name, color)
select id, 'Region', '#5470c6' from user;

INSERT INTO tag (name, category_id)
SELECT 'Russia', id FROM tag_category where name = 'Region';

INSERT INTO tag (name, category_id)
SELECT 'USA', id FROM tag_category where name = 'Region';

INSERT INTO tag (name, category_id)
SELECT 'Europe', id FROM tag_category where name = 'Region';

INSERT INTO tag (name, category_id)
SELECT 'Earth', id FROM tag_category where name = 'Region';


INSERT INTO tag_category (user_id, name, color)
select id, 'Risk', '#91cc75' from user;

INSERT INTO tag (name, category_id)
SELECT 'Currency', id FROM tag_category where name = 'Risk';

INSERT INTO tag (name, category_id)
SELECT 'Sectoral', id FROM tag_category where name = 'Risk';

INSERT INTO tag (name, category_id)
SELECT 'Political', id FROM tag_category where name = 'Risk';

INSERT INTO tag (name, category_id)
SELECT 'Sanctions', id FROM tag_category where name = 'Risk';

ALTER TABLE tag DROP FOREIGN KEY fk__tag__category_id__tag_category;
ALTER TABLE tag ADD CONSTRAINT fk__tag__category_id__tag_category
    FOREIGN KEY (category_id) REFERENCES tag_category (id);

ALTER TABLE portfolio_position_tag DROP FOREIGN KEY fk__portfolio_position__tag_id__tag;
ALTER TABLE portfolio_position_tag ADD CONSTRAINT fk__portfolio_position__tag_id__tag
    FOREIGN KEY (tag_id) REFERENCES tag (id)
        ON DELETE CASCADE;

ALTER TABLE portfolio_position
    ADD open_date date;


UPDATE portfolio_position
SET open_date = (select t.date from trade t where t.portfolio_position_id = portfolio_position.id order by t.date asc limit 1)
where open_date is null;

ALTER TABLE portfolio_position
    ADD realized_pnl decimal(20, 2);


