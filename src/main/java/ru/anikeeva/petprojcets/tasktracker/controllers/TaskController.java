package ru.anikeeva.petprojcets.tasktracker.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.anikeeva.petprojcets.tasktracker.dto.TaskDTO;
import ru.anikeeva.petprojcets.tasktracker.models.impl.UserDetailsImpl;
import ru.anikeeva.petprojcets.tasktracker.services.TaskService;

import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/create")
    public TaskDTO createTask(@AuthenticationPrincipal UserDetailsImpl currentUser, @RequestBody TaskDTO taskDTO) {
        return taskService.createTask(currentUser, taskDTO);
    }

    @GetMapping("/{id}")
    public TaskDTO showTask(@AuthenticationPrincipal UserDetailsImpl currentUser, @PathVariable UUID id) {
        return taskService.showTask(currentUser, id);
    }

    @GetMapping
}
