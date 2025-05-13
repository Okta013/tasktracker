package ru.anikeeva.petprojcets.tasktracker.exceptions;

public class WrongTasksParameterException extends RuntimeException{
    public WrongTasksParameterException(String message){
        super(message);
    }
}
