--liquibase formatted sql
--changeset ASSETS.1.00.00.013-001 failOnError:true splitStatements:true context:update runOnChange: false
ALTER TABLE portfolio_position
    ADD open_date date;


UPDATE portfolio_position
SET open_date = (select t.date from trade t where t.portfolio_position_id = portfolio_position.id order by t.date asc limit 1)
where open_date is null;
