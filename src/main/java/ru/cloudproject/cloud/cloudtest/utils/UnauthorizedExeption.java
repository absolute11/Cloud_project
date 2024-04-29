package ru.cloudproject.cloud.cloudtest.utils;

public class UnauthorizedExeption extends RuntimeException{
    public UnauthorizedExeption(String message) {
        super(message);
    }
}
