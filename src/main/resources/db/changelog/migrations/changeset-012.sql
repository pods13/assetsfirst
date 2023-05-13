--liquibase formatted sql
--changeset ASSETS.1.00.00.012-001 failOnError:true splitStatements:true context:update runOnChange: false
ALTER TABLE portfolio_position_tag DROP FOREIGN KEY fk__portfolio_position__tag_id__tag;
ALTER TABLE portfolio_position_tag ADD CONSTRAINT fk__portfolio_position__tag_id__tag
    FOREIGN KEY (tag_id) REFERENCES tag (id)
    ON DELETE CASCADE;
