# Menu Permission Guide (Frontend)

This document defines how frontend should render menus based on backend permissions.

## Source of truth

- Backend endpoint: `GET /api/v1/users/me/menus`
- Response type: list of menu permissions derived from current user roles/authorities.
- Do not hardcode role-to-menu mapping in frontend. Always use backend response.

## API contract

### Request

```http
GET /api/v1/users/me/menus
Authorization: Bearer <access_token>
```

### Success response (example)

```json
{
  "success": true,
  "data": [
    { "code": "MENU_HOME", "name": "Home" },
    { "code": "MENU_CATALOG", "name": "Catalog" },
    { "code": "MENU_PROFILE", "name": "Profile" }
  ],
  "requestId": "..."
}
```

## Menu codes

- `MENU_HOME`
- `MENU_CATALOG`
- `MENU_ENROLLMENTS`
- `MENU_PROGRESS`
- `MENU_LEARNING_PATHS`
- `MENU_ASSESSMENTS`
- `MENU_ASSIGNMENTS`
- `MENU_COMMUNITY`
- `MENU_EVENTS`
- `MENU_CAREER`
- `MENU_NOTIFICATIONS`
- `MENU_PROFILE`
- `MENU_SETTINGS`

## Product rules

- `STUDENT` is default role after register.
- `INSTRUCTOR` can only be assigned by `ADMIN` (promotion endpoint).
- `Settings` is system-level: only `ADMIN`.
- `Career` is admin-only.
- `Community` is available to all, but moderation actions are only `ADMIN` + `INSTRUCTOR`.
- Instructor data scope is only own classes (enforced by backend services).

## Expected menu visibility by role

### ADMIN

Show all menu codes.

### INSTRUCTOR

Show:
- `MENU_HOME`
- `MENU_CATALOG`
- `MENU_ENROLLMENTS`
- `MENU_PROGRESS`
- `MENU_LEARNING_PATHS`
- `MENU_ASSESSMENTS`
- `MENU_ASSIGNMENTS`
- `MENU_COMMUNITY`
- `MENU_EVENTS`
- `MENU_NOTIFICATIONS`
- `MENU_PROFILE`

Do not show:
- `MENU_CAREER`
- `MENU_SETTINGS`

### STUDENT

Show:
- `MENU_HOME`
- `MENU_CATALOG`
- `MENU_ENROLLMENTS`
- `MENU_PROGRESS`
- `MENU_LEARNING_PATHS`
- `MENU_ASSESSMENTS`
- `MENU_ASSIGNMENTS`
- `MENU_COMMUNITY`
- `MENU_EVENTS`
- `MENU_NOTIFICATIONS`
- `MENU_PROFILE`

Do not show:
- `MENU_CAREER`
- `MENU_SETTINGS`

## Frontend implementation pattern

1. On app bootstrap (after login/refresh), call `/users/me/menus`.
2. Convert response to a set:
   - Example: `const menuSet = new Set(data.map(x => x.code));`
3. Render sidebar/topnav by checking membership in `menuSet`.
4. Protect route navigation with the same menu checks.
5. On `401/403`, redirect to login or unauthorized page.
6. Re-fetch menu permissions when token changes or user re-authenticates.

## Suggested route guard mapping

- `/home` -> `MENU_HOME`
- `/catalog` -> `MENU_CATALOG`
- `/enrollments` -> `MENU_ENROLLMENTS`
- `/progress` -> `MENU_PROGRESS`
- `/learning-paths` -> `MENU_LEARNING_PATHS`
- `/assessments` -> `MENU_ASSESSMENTS`
- `/assignments` -> `MENU_ASSIGNMENTS`
- `/community` -> `MENU_COMMUNITY`
- `/events` -> `MENU_EVENTS`
- `/career` -> `MENU_CAREER`
- `/notifications` -> `MENU_NOTIFICATIONS`
- `/profile` -> `MENU_PROFILE`
- `/settings` -> `MENU_SETTINGS`

## Important security note

Menu hiding is UX only. Backend authorization is the real security control.  
Frontend must still handle `403 Forbidden` even if a menu is not displayed.
