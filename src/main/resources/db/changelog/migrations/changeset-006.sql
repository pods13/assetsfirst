--liquibase formatted sql
--changeset ASSETS.1.00.00.006-001 failOnError:true splitStatements:true context:update runOnChange: false

ALTER TABLE portfolio_holding
    DROP FOREIGN KEY fk__portfolio_holding__portfolio_id__portfolio;
ALTER TABLE portfolio_holding    add constraint fk__portfolio_position__portfolio_id__portfolio
        foreign key (portfolio_id)
            references portfolio (id);

--changeset ASSETS.1.00.00.006-002 failOnError:true splitStatements:true context:update runOnChange: false
ALTER TABLE portfolio_holding
    DROP FOREIGN KEY fk__portfolio_holding__instrument_id__instrument;

ALTER TABLE portfolio_holding    add constraint fk__portfolio_position__instrument_id__instrument
        foreign key (instrument_id)
            references instrument (id);

--changeset ASSETS.1.00.00.006-003 failOnError:true splitStatements:false context:update runOnChange: false
ALTER TABLE trade CHANGE portfolio_holding_id portfolio_position_id bigint  not null;

--changeset ASSETS.1.00.00.006-004 failOnError:true splitStatements:true context:update runOnChange: false
ALTER TABLE trade
    DROP FOREIGN KEY fk__trade__portfolio_holding_id__portfolio_holding;

ALTER TABLE trade add constraint fk__trade__portfolio_position_id__portfolio_position
        foreign key (portfolio_position_id)
            references portfolio_holding (id);

--changeset ASSETS.1.00.00.006-005 failOnError:true splitStatements:false context:update runOnChange: false
ALTER TABLE portfolio_holding RENAME portfolio_position;
