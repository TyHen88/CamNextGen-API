-- Add instructor role and role/permission matrix for ADMIN, INSTRUCTOR, STUDENT.

INSERT INTO roles (code, name) VALUES ('INSTRUCTOR', 'Instructor')
ON CONFLICT (code) DO NOTHING;

-- Menu permissions
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

-- Scope and operation permissions
INSERT INTO permissions (code, name) VALUES ('ENROLLMENT_READ_OWN', 'Read own enrollments') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('ENROLLMENT_READ_ALL', 'Read all enrollments') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('PROGRESS_READ_OWN', 'Read own progress') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('PROGRESS_READ_ALL', 'Read all progress') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('LEARNING_PATH_READ_OWN', 'Read own learning paths') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('LEARNING_PATH_READ_ALL', 'Read all learning paths') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('ASSESSMENT_MANAGE_OWN', 'Manage own class assessments') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('ASSESSMENT_MANAGE_ALL', 'Manage all assessments') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('ASSIGNMENT_MANAGE_OWN', 'Manage own class assignments') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('ASSIGNMENT_MANAGE_ALL', 'Manage all assignments') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('COMMUNITY_MODERATE', 'Moderate community') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('CAREER_MANAGE', 'Manage career module') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('SYSTEM_SETTINGS_MANAGE', 'Manage system settings') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('ROLE_PROMOTE_INSTRUCTOR', 'Promote user to instructor') ON CONFLICT (code) DO NOTHING;
INSERT INTO permissions (code, name) VALUES ('USER_READ_ALL', 'Read all users') ON CONFLICT (code) DO NOTHING;

-- Preserve existing coarse permissions and grant to non-admin roles where needed by current endpoints.
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
  'MENU_HOME','MENU_CATALOG','MENU_ENROLLMENTS','MENU_PROGRESS','MENU_LEARNING_PATHS','MENU_ASSESSMENTS',
  'MENU_ASSIGNMENTS','MENU_COMMUNITY','MENU_EVENTS','MENU_CAREER','MENU_NOTIFICATIONS','MENU_PROFILE',
  'MENU_SETTINGS','ENROLLMENT_READ_OWN','ENROLLMENT_READ_ALL','PROGRESS_READ_OWN','PROGRESS_READ_ALL',
  'LEARNING_PATH_READ_OWN','LEARNING_PATH_READ_ALL','ASSESSMENT_MANAGE_OWN','ASSESSMENT_MANAGE_ALL',
  'ASSIGNMENT_MANAGE_OWN','ASSIGNMENT_MANAGE_ALL','COMMUNITY_MODERATE','CAREER_MANAGE',
  'SYSTEM_SETTINGS_MANAGE','ROLE_PROMOTE_INSTRUCTOR','USER_READ_ALL',
  'USER_READ','USER_WRITE','COURSE_READ','COURSE_WRITE','ENROLLMENT_READ','ENROLLMENT_WRITE','AUDIT_READ','ACTIVITY_READ'
)
WHERE r.code = 'ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
  'MENU_HOME','MENU_CATALOG','MENU_ENROLLMENTS','MENU_PROGRESS','MENU_LEARNING_PATHS','MENU_ASSESSMENTS',
  'MENU_ASSIGNMENTS','MENU_COMMUNITY','MENU_EVENTS','MENU_NOTIFICATIONS','MENU_PROFILE',
  'ENROLLMENT_READ_OWN','PROGRESS_READ_ALL','LEARNING_PATH_READ_ALL','ASSESSMENT_MANAGE_OWN',
  'ASSIGNMENT_MANAGE_OWN','COMMUNITY_MODERATE',
  'COURSE_READ','COURSE_WRITE','ENROLLMENT_READ','ENROLLMENT_WRITE'
)
WHERE r.code = 'INSTRUCTOR'
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
  'MENU_HOME','MENU_CATALOG','MENU_ENROLLMENTS','MENU_PROGRESS','MENU_LEARNING_PATHS','MENU_ASSESSMENTS',
  'MENU_ASSIGNMENTS','MENU_COMMUNITY','MENU_EVENTS','MENU_NOTIFICATIONS','MENU_PROFILE',
  'ENROLLMENT_READ_OWN','PROGRESS_READ_OWN','LEARNING_PATH_READ_OWN',
  'COURSE_READ','ENROLLMENT_READ','ENROLLMENT_WRITE'
)
WHERE r.code = 'STUDENT'
ON CONFLICT DO NOTHING;
