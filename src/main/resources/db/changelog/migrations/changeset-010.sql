--liquibase formatted sql
--changeset ASSETS.1.00.00.010-001 failOnError:true splitStatements:true context:update runOnChange: false
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
