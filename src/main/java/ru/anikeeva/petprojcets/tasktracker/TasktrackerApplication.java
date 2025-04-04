package ru.anikeeva.petprojcets.tasktracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TasktrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TasktrackerApplication.class, args);
    }

}
