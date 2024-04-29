package ru.cloudproject.cloud.cloudtest.utils;

public class FilenameNotFoundException extends RuntimeException{
    public FilenameNotFoundException() {
        super("Filename not found");
    }

    public FilenameNotFoundException(String message) {
        super(message);
    }

    public FilenameNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
