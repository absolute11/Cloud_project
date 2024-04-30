package ru.cloudproject.cloud.cloudtest.utils;

public class TokenNotFoundException extends RuntimeException{
    public TokenNotFoundException() {
        super("Token not found");
    }

    public TokenNotFoundException(String message) {
        super(message);
    }

    public TokenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
