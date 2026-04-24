DROP TABLE IF EXISTS public.users;
DROP TABLE IF EXISTS public.userfiles;

create table IF NOT EXISTS public.users(
id bigserial not null,
email varchar(50) not null,
pass varchar(20) not null,

CONSTRAINT pk_users PRIMARY KEY (email)
);

create table IF NOT EXISTS public.userfiles(
id bigserial not null,
userid bigint not null,
filename varchar(50) not null,
filedata bytea not null,

CONSTRAINT pk_userfiles PRIMARY KEY(filename)
);

