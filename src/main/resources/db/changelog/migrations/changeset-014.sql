--liquibase formatted sql
--changeset ASSETS.1.00.00.014-001 failOnError:true splitStatements:true context:update runOnChange: false
ALTER TABLE portfolio_position
    ADD realized_pnl decimal(20, 2);
