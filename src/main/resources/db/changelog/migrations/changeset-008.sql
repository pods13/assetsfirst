--liquibase formatted sql
--changeset ASSETS.1.00.00.008-001 failOnError:true splitStatements:true context:update runOnChange: false

create table IF NOT EXISTS tag_category
(
    id            bigint not null auto_increment,
    user_id       bigint not null,
    name          varchar(255) not null,
    color         char(7) not null,
    primary key (id),
    constraint fk__tag_category__user_id__user
        foreign key (user_id)
            references user (id)
) engine = InnoDB;

create table IF NOT EXISTS tag
(
    id            bigint not null auto_increment,
    name          varchar(255) not null,
    category_id       bigint not null,
    primary key (id),
    CONSTRAINT fk__tag__category_id__tag_category
        FOREIGN KEY (category_id) REFERENCES tag (id)
) engine = InnoDB;

--changeset ASSETS.1.00.00.008-002 failOnError:true splitStatements:false context:update runOnChange: false
create table IF NOT EXISTS portfolio_position_tag
(
    position_id      bigint not null,
    tag_id bigint not null,
    primary key (position_id, tag_id),
    constraint fk__portfolio_position__position_id__portfolio_position
        foreign key (position_id)
            references portfolio_position (id),
    constraint fk__portfolio_position__tag_id__tag
        foreign key (tag_id)
            references tag (id)
) engine = InnoDB;

