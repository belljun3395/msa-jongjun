drop table if exists group_table CASCADE;

create table group_table
(
    group_id   bigint NOT NULL AUTO_INCREMENT,
    group_name varchar(255),
    max_member integer,
    owner_id   bigint,
    primary key (group_id)
);
