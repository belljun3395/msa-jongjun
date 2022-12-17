drop table if exists member CASCADE;

create table member
(
    member_id       bigint NOT NULL AUTO_INCREMENT,
    member_role     varchar(255),
    member_email    varchar(255),
    member_name     varchar(255),
    member_password varchar(255),
    primary key (member_id)
);