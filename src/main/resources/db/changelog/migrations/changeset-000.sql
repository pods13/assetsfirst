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
