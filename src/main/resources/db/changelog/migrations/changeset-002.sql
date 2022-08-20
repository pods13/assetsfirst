--liquibase formatted sql
--changeset ASSETS.1.00.00.002-001 failOnError:true splitStatements:true context:update runOnChange: false
update portfolio_dashboard SET CARDS = JSON_ARRAY()
where JSON_CONTAINS(cards, '{"containerType": "DIVIDENDS"}');
