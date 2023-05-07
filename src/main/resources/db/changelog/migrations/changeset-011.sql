--liquibase formatted sql
--changeset ASSETS.1.00.00.011-001 failOnError:true splitStatements:true context:update runOnChange: false
ALTER TABLE tag DROP FOREIGN KEY fk__tag__category_id__tag_category;
ALTER TABLE tag ADD CONSTRAINT fk__tag__category_id__tag_category
    FOREIGN KEY (category_id) REFERENCES tag_category (id);
