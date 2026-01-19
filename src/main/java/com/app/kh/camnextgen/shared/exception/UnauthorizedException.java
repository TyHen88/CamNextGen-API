package com.app.kh.camnextgen.shared.exception;

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException(String message) {
        super("UNAUTHORIZED", message);
    }
}
