package ru.anikeeva.petprojcets.tasktracker.exceptions;

public class BadTokenException extends RuntimeException {
    public BadTokenException(String message) {
        super(message);
    }
}
