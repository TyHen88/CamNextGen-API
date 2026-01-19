insert into permissions (code, name) values ('COURSE_READ', 'Read courses') on conflict do nothing;
insert into permissions (code, name) values ('COURSE_WRITE', 'Manage courses') on conflict do nothing;
insert into permissions (code, name) values ('ENROLLMENT_READ', 'Read enrollments') on conflict do nothing;
insert into permissions (code, name) values ('ENROLLMENT_WRITE', 'Manage enrollments') on conflict do nothing;
insert into permissions (code, name) values ('AUDIT_READ', 'Read audit logs') on conflict do nothing;
insert into permissions (code, name) values ('ACTIVITY_READ', 'Read activity logs') on conflict do nothing;

insert into role_permissions (role_id, permission_id)
select r.id, p.id from roles r
join permissions p on p.code in (
  'COURSE_READ','COURSE_WRITE','ENROLLMENT_READ','ENROLLMENT_WRITE','AUDIT_READ','ACTIVITY_READ'
)
where r.code = 'ADMIN'
on conflict do nothing;
