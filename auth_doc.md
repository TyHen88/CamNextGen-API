# Auth API Documentation

Base URL: `/api/v1/auth`  
Content-Type: `application/json`

## Common Success Response
```json
{
  "success": true,
  "code": "OK",
  "message": "success",
  "data": {},
  "timestamp": "2026-03-01T12:00:00Z",
  "requestId": "..."
}
```

## Common Error Response
```json
{
  "code": "VALIDATION_ERROR",
  "message": "Validation failed",
  "errors": [
    { "field": "email", "message": "must be a well-formed email address" }
  ],
  "timestamp": "2026-03-01T12:00:00Z",
  "requestId": "..."
}
```

## 1) Register
Endpoint: `POST /register`

Request:
```json
{
  "email": "user@example.com",
  "password": "123456",
  "fullName": "John Doe"
}
```

Response:
```json
{
  "success": true,
  "code": "OK",
  "message": "success",
  "data": {
    "accessToken": null,
    "refreshToken": null
  },
  "timestamp": "2026-03-01T12:00:00Z",
  "requestId": "..."
}
```

Note: User is created as `PENDING`; OTP is sent for verification.

## 2) Login
Endpoint: `POST /login`

Request:
```json
{
  "email": "user@example.com",
  "password": "123456"
}
```

Response:
```json
{
  "success": true,
  "code": "OK",
  "message": "success",
  "data": {
    "accessToken": "jwt-access-token",
    "refreshToken": "jwt-refresh-token"
  },
  "timestamp": "2026-03-01T12:00:00Z",
  "requestId": "..."
}
```

## 3) Refresh Token
Endpoint: `POST /refresh`

Request:
```json
{
  "refreshToken": "jwt-refresh-token"
}
```

Response:
```json
{
  "success": true,
  "code": "OK",
  "message": "success",
  "data": {
    "accessToken": "new-access-token",
    "refreshToken": "new-refresh-token"
  },
  "timestamp": "2026-03-01T12:00:00Z",
  "requestId": "..."
}
```

## 4) Logout
Endpoint: `POST /logout`

Headers:
```text
Authorization: Bearer <accessToken>
```

Request: empty body

Response:
```json
{
  "success": true,
  "code": "OK",
  "message": "success",
  "data": null,
  "timestamp": "2026-03-01T12:00:00Z",
  "requestId": "..."
}
```

## 5) Send OTP
Endpoint: `POST /send-otp`

Request:
```json
{
  "email": "user@example.com",
  "purpose": "EMAIL_VERIFICATION"
}
```

`purpose` enum:
- `EMAIL_VERIFICATION`
- `PASSWORD_RESET`
- `LOGIN_2FA`

Response:
```json
{
  "success": true,
  "code": "OK",
  "message": "success",
  "data": null,
  "timestamp": "2026-03-01T12:00:00Z",
  "requestId": "..."
}
```

## 6) Verify OTP
Endpoint: `POST /verify-otp`

Request:
```json
{
  "email": "user@example.com",
  "otp": "123456",
  "purpose": "EMAIL_VERIFICATION"
}
```

Response:
```json
{
  "success": true,
  "code": "OK",
  "message": "success",
  "data": null,
  "timestamp": "2026-03-01T12:00:00Z",
  "requestId": "..."
}
```

## 7) Forgot Password (OTP-based)
Endpoint: `POST /forgot-password`

Request:
```json
{
  "email": "user@example.com"
}
```

Response:
```json
{
  "success": true,
  "code": "OK",
  "message": "success",
  "data": null,
  "timestamp": "2026-03-01T12:00:00Z",
  "requestId": "..."
}
```

Note: Sends OTP with purpose `PASSWORD_RESET`.

## 8) Reset Password (OTP-based)
Endpoint: `POST /reset-password`

Request:
```json
{
  "email": "user@example.com",
  "otp": "123456",
  "newPassword": "newStrongPassword123"
}
```

Response:
```json
{
  "success": true,
  "code": "OK",
  "message": "success",
  "data": null,
  "timestamp": "2026-03-01T12:00:00Z",
  "requestId": "..."
}
```

## 9) Legacy Verify by Token (Still Available)
Endpoint: `POST /verify`

Request:
```json
{
  "token": "verification-token"
}
```

Response:
```json
{
  "success": true,
  "code": "OK",
  "message": "success",
  "data": null,
  "timestamp": "2026-03-01T12:00:00Z",
  "requestId": "..."
}
```
