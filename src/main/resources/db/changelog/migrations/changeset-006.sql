--liquibase formatted sql
--changeset ASSETS.1.00.00.006-001 failOnError:true splitStatements:true context:update runOnChange: false

create table IF NOT EXISTS user_tag_category
(
    user_id      bigint not null,
    tag_category_id bigint not null,
    primary key (user_id, tag_category_id),
    constraint fk__user_tag_category__user_id__user
    foreign key (user_id)
    references user (id),
    constraint fk__user_tag_category__tag_category_id__tag_category
    foreign key (tag_category_id)
    references tag_category (id)
    ) engine = InnoDB;

INSERT INTO user_tag_category (user_id, tag_category_id)
SELECT user_id, id  FROM tag_category;

ALTER TABLE tag_category
    DROP FOREIGN KEY fk__tag_category__user_id__user;
ALTER TABLE tag_category
    DROP INDEX tag_category__user_id_code__key;
ALTER TABLE tag_category
    DROP COLUMN user_id;

create table IF NOT EXISTS instrument_tag
(
    instrument_id      bigint not null,
    tag_id bigint not null,
    primary key (instrument_id, tag_id),
    constraint fk__instrument_tag__instrument_id__instrument
        foreign key (instrument_id)
            references instrument (id),
    constraint fk__instrument_tag__tag_id__tag
        foreign key (tag_id)
            references tag (id)
) engine = InnoDB;

