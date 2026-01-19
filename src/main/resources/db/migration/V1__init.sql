create table users (
    id bigserial primary key,
    email varchar(255) not null unique,
    password_hash varchar(255) not null,
    full_name varchar(120) not null,
    status varchar(30) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    created_by bigint,
    updated_by bigint,
    version bigint not null
);

create table roles (
    id bigserial primary key,
    code varchar(60) not null unique,
    name varchar(120) not null
);

create table permissions (
    id bigserial primary key,
    code varchar(80) not null unique,
    name varchar(120) not null
);

create table role_permissions (
    id bigserial primary key,
    role_id bigint not null references roles(id),
    permission_id bigint not null references permissions(id),
    unique (role_id, permission_id)
);

create table user_roles (
    id bigserial primary key,
    user_id bigint not null references users(id),
    role_id bigint not null references roles(id),
    assigned_at timestamp not null,
    unique (user_id, role_id)
);

create table courses (
    id bigserial primary key,
    code varchar(50) not null unique,
    title varchar(200) not null,
    description varchar(4000),
    status varchar(30) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    created_by bigint,
    updated_by bigint,
    version bigint not null
);

create table enrollments (
    id bigserial primary key,
    user_id bigint not null references users(id),
    course_id bigint not null references courses(id),
    status varchar(30) not null,
    enrolled_at timestamp not null,
    unique (user_id, course_id)
);

create table verification_tokens (
    id bigserial primary key,
    token varchar(120) not null unique,
    user_id bigint not null references users(id),
    expires_at timestamp not null,
    created_at timestamp not null
);

create table password_reset_tokens (
    id bigserial primary key,
    token varchar(120) not null unique,
    user_id bigint not null references users(id),
    expires_at timestamp not null,
    created_at timestamp not null
);

create table refresh_tokens (
    id bigserial primary key,
    token varchar(200) not null unique,
    user_id bigint not null references users(id),
    expires_at timestamp not null,
    created_at timestamp not null,
    revoked_at timestamp
);

create table audit_logs (
    id bigserial primary key,
    event varchar(120) not null,
    actor_user_id bigint not null,
    created_at timestamp not null,
    details_json varchar(4000)
);

create table activity_logs (
    id bigserial primary key,
    event varchar(120) not null,
    actor_user_id bigint not null,
    created_at timestamp not null,
    details_json varchar(4000)
);

insert into roles (code, name) values ('ADMIN', 'Administrator');
insert into roles (code, name) values ('STUDENT', 'Student');

insert into permissions (code, name) values ('USER_READ', 'Read users');
insert into permissions (code, name) values ('USER_WRITE', 'Manage users');

insert into role_permissions (role_id, permission_id)
select r.id, p.id from roles r cross join permissions p where r.code = 'ADMIN';
