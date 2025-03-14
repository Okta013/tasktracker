package ru.anikeeva.petprojcets.tasktracker.observer;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.anikeeva.petprojcets.tasktracker.models.User;

import java.util.Locale;

@Getter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private User user;

    public OnRegistrationCompleteEvent(Locale locale, String appUrl, User user) {
        super(user);
        this.locale = locale;
        this.appUrl = appUrl;
        this.user = user;
    }
}