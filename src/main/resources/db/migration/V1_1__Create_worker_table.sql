create table if not exists worker
(
    code        varchar
        constraint worker_pk primary key,
    name        varchar not null,
    worker_type varchar not null
);
