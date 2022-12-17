drop table if exists login_log CASCADE;

create table login_log
(
    id            bigint NOT NULL AUTO_INCREMENT,
    client_type   varchar(255),
    location      varchar(255),
    logout        boolean,
    member_id     bigint,
    refresh_token varchar(255),
    role          varchar(255),
    timestamp     timestamp,
    primary key (id)
);
