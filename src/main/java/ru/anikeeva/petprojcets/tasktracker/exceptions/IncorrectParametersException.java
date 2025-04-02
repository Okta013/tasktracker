package ru.anikeeva.petprojcets.tasktracker.exceptions;

public class IncorrectParametersException extends RuntimeException {
    public IncorrectParametersException(String message) {
        super(message);
    }
}
