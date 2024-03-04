--liquibase formatted sql
--changeset ASSETS.1.00.00.003-001 failOnError:true splitStatements:true context:update runOnChange: false

INSERT INTO tag_category (user_id, name, color)
select id, 'Broker', '#252aad'
from user;

INSERT INTO tag (name, category_id)
SELECT 'Interactive Brokers LLC', id
FROM tag_category
where name = 'Broker';

INSERT INTO tag (name, category_id)
SELECT 'Tinkoff Investments', id
FROM tag_category
where name = 'Broker';

INSERT INTO tag (name, category_id)
SELECT 'VTB Investments', id
FROM tag_category
where name = 'Broker';

INSERT INTO tag (name, category_id)
SELECT 'Finam', id
FROM tag_category
where name = 'Broker';

INSERT INTO tag (name, category_id)
SELECT 'Alfa Direct', id
FROM tag_category
where name = 'Broker';

INSERT INTO tag (name, category_id)
SELECT 'BCS Investments', id
FROM tag_category
where name = 'Broker';

ALTER TABLE trade
    ADD COLUMN intermediary_id bigint;

create temporary table trade_id_by_intermediary_tag_id
select t.id trade_id, tag.id tag_id
from trade t
         left join broker b on b.id = t.broker_id
         left join portfolio_position pp on pp.id = t.portfolio_position_id
         left join portfolio p on p.id = pp.portfolio_id
         left join tag_category tc on tc.user_id = p.user_id and tc.name = 'Broker'
         left join tag tag on tc.id = tag.category_id and tag.name = b.name;

update trade trade
inner join trade_id_by_intermediary_tag_id temp on temp.trade_id = trade.id
set trade.intermediary_id = temp.tag_id;

ALTER TABLE trade
    add constraint fk__trade__intermediary_id__tag
        foreign key (intermediary_id)
            references tag (id);

ALTER TABLE trade CHANGE intermediary_id intermediary_id bigint  not null;

DROP TABLE trade_id_by_intermediary_tag_id;

--changeset ASSETS.1.00.00.004-002 failOnError:true splitStatements:true context:update runOnChange: false
ALTER TABLE trade
    DROP FOREIGN KEY fk__trade__broker_id__broker;

ALTER TABLE trade
    DROP COLUMN broker_id;

DROP TABLE IF EXISTS broker;
