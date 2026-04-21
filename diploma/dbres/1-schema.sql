DROP TABLE IF EXISTS public.users;

create table IF NOT EXISTS public.users(
id bigserial not null,
email varchar(50) not null,
pass varchar(20) not null,

CONSTRAINT pk_users PRIMARY KEY (email)
);
