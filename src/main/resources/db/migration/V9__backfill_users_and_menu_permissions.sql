-- Backfill legacy users/roles and ensure menu permissions are mapped by role.
-- This migration is idempotent and safe to re-run.

-- Ensure baseline roles exist.
INSERT INTO roles (code, name) VALUES ('ADMIN', 'Administrator')
ON CONFLICT (code) DO NOTHING;
INSERT INTO roles (code, name) VALUES ('INSTRUCTOR', 'Instructor')
ON CONFLICT (code) DO NOTHING;
INSERT INTO roles (code, name) VALUES ('STUDENT', 'Student')
ON CONFLICT (code) DO NOTHING;

-- Ensure menu permissions exist.
INSERT INTO permissions (code, name) VALUES ('MENU_HOME', 'Home') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('MENU_CATALOG', 'Catalog') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('MENU_ENROLLMENTS', 'Enrollments') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('MENU_PROGRESS', 'Progress') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('MENU_LEARNING_PATHS', 'Learning Paths') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('MENU_ASSESSMENTS', 'Assessments') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('MENU_ASSIGNMENTS', 'Assignments') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('MENU_COMMUNITY', 'Community') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('MENU_EVENTS', 'Events') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('MENU_CAREER', 'Career') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('MENU_NOTIFICATIONS', 'Notifications') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('MENU_PROFILE', 'Profile') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('MENU_SETTINGS', 'Settings') ON CONFLICT (code) DO NOTHING;

-- Ensure action/scope permissions used by menu policy exist.
INSERT INTO permissions (code, name) VALUES ('COMMUNITY_MODERATE', 'Moderate community') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('CAREER_MANAGE', 'Manage career module') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('SYSTEM_SETTINGS_MANAGE', 'Manage system settings') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('ROLE_PROMOTE_INSTRUCTOR', 'Promote user to instructor') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('USER_READ_ALL', 'Read all users') ON CONFLICT (code) DO NOTHING;

-- Assign menu permissions to ADMIN.
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
  'MENU_HOME','MENU_CATALOG','MENU_ENROLLMENTS','MENU_PROGRESS','MENU_LEARNING_PATHS',
  'MENU_ASSESSMENTS','MENU_ASSIGNMENTS','MENU_COMMUNITY','MENU_EVENTS','MENU_CAREER',
  'MENU_NOTIFICATIONS','MENU_PROFILE','MENU_SETTINGS',
  'COMMUNITY_MODERATE','CAREER_MANAGE','SYSTEM_SETTINGS_MANAGE','ROLE_PROMOTE_INSTRUCTOR','USER_READ_ALL'
)
WHERE r.code = 'ADMIN'
ON CONFLICT DO NOTHING;

-- Assign menu permissions to INSTRUCTOR.
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
  'MENU_HOME','MENU_CATALOG','MENU_ENROLLMENTS','MENU_PROGRESS','MENU_LEARNING_PATHS',
  'MENU_ASSESSMENTS','MENU_ASSIGNMENTS','MENU_COMMUNITY','MENU_EVENTS','MENU_NOTIFICATIONS',
  'MENU_PROFILE','COMMUNITY_MODERATE'
)
WHERE r.code = 'INSTRUCTOR'
ON CONFLICT DO NOTHING;

-- Assign menu permissions to STUDENT.
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
  'MENU_HOME','MENU_CATALOG','MENU_ENROLLMENTS','MENU_PROGRESS','MENU_LEARNING_PATHS',
  'MENU_ASSESSMENTS','MENU_ASSIGNMENTS','MENU_COMMUNITY','MENU_EVENTS','MENU_NOTIFICATIONS',
  'MENU_PROFILE'
)
WHERE r.code = 'STUDENT'
ON CONFLICT DO NOTHING;

-- Backfill: users without any role become STUDENT.
INSERT INTO user_roles (user_id, role_id, assigned_at)
SELECT u.id, r.id, now()
FROM users u
JOIN roles r ON r.code = 'STUDENT'
LEFT JOIN user_roles ur ON ur.user_id = u.id
WHERE ur.id IS NULL
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Cleanup: remove STUDENT role when user is already ADMIN or INSTRUCTOR.
DELETE FROM user_roles student_ur
USING roles student_role, user_roles privileged_ur, roles privileged_role
WHERE student_ur.role_id = student_role.id
  AND student_role.code = 'STUDENT'
  AND student_ur.user_id = privileged_ur.user_id
  AND privileged_ur.role_id = privileged_role.id
  AND privileged_role.code IN ('ADMIN', 'INSTRUCTOR');
