package ru.cloudproject.cloud.cloudtest.utils;
public class FileProcessingException extends RuntimeException{
    public FileProcessingException() {
        super("File saving error");
    }

    public FileProcessingException(String message) {
        super(message);
    }

    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
