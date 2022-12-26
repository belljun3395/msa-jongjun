drop table if exists group_member_table CASCADE;

create table group_member_table
(
    group_member_id bigint NOT NULL AUTO_INCREMENT,
    member_id       bigint,
    group_id        bigint,
    primary key (group_member_id)
);