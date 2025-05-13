package ru.anikeeva.petprojcets.tasktracker.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.anikeeva.petprojcets.tasktracker.dto.TaskDTO;
import ru.anikeeva.petprojcets.tasktracker.exceptions.NoRightException;
import ru.anikeeva.petprojcets.tasktracker.exceptions.UserNotFoundException;
import ru.anikeeva.petprojcets.tasktracker.exceptions.WrongTasksParameterException;
import ru.anikeeva.petprojcets.tasktracker.mappers.TaskMapper;
import ru.anikeeva.petprojcets.tasktracker.models.Comment;
import ru.anikeeva.petprojcets.tasktracker.models.Task;
import ru.anikeeva.petprojcets.tasktracker.models.User;
import ru.anikeeva.petprojcets.tasktracker.models.enums.EnumStatus;
import ru.anikeeva.petprojcets.tasktracker.models.impl.UserDetailsImpl;
import ru.anikeeva.petprojcets.tasktracker.repositories.CommentRepository;
import ru.anikeeva.petprojcets.tasktracker.repositories.TaskRepository;
import ru.anikeeva.petprojcets.tasktracker.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public TaskDTO createTask(final UserDetailsImpl currentUser, final TaskDTO taskDTO) {
        isEmptyTaskDTO(taskDTO);
        Task newTask = taskMapper.toTask(taskDTO);
        newTask.setCreatedAt(LocalDateTime.now());
        User creator = userService.findUserByUsername(currentUser.getUsername());
        newTask.setCreator(creator);
        if (!isUserIsAdmin(currentUser)) {
            newTask.setExecutor(creator);
        }
        return taskMapper.toTaskDTO(taskRepository.save(newTask));
    }

    public TaskDTO showTask(final UserDetailsImpl currentUser, final UUID taskId) {
        return taskMapper.toTaskDTO(getTaskForShow(taskId));
    }

    public List<TaskDTO> showAllTasks(final UserDetailsImpl currentUser, final UUID userId, final String position) {
        switch (position) {
            case "creator": return taskRepository.findByCreatorId(userId).stream().map(taskMapper::toTaskDTO).toList();
            case "executor": return taskRepository.findByExecutorId(userId).stream().map(taskMapper::toTaskDTO).toList();
            default: throw new WrongTasksParameterException("Задачи не найдены");
        }
    }

    public TaskDTO updateTask(final UserDetailsImpl currentUser, final UUID taskId, final TaskDTO taskDTO) {
        isEmptyTaskDTO(taskDTO);
        Task updatedTask = getTaskForShow (taskId);
        if (updatedTask.getStatus().equals(EnumStatus.CANCELLED) || updatedTask.getStatus().equals(EnumStatus.CLOSED)) {
            throw new WrongTasksParameterException("Нельзя изменить отмененную или закрытую задачу");
        }
        taskMapper.updateTaskFromTaskDTO(taskDTO, updatedTask);
        return taskMapper.toTaskDTO(taskRepository.save(updatedTask));
    }

    public void deleteTask(final UserDetailsImpl currentUser, final UUID taskId) {
        Task task = getTaskForShow(taskId);
        TaskDTO taskDTO = taskMapper.toTaskDTO(task);
        isEmptyTaskDTO(taskDTO);
        task.setStatus(EnumStatus.CANCELLED);
    }

    public TaskDTO recoverTask(final UserDetailsImpl currentUser, final UUID taskId) {
        Task task = getTaskForShow(taskId);
        task.setStatus(EnumStatus.NEW);
        return taskMapper.toTaskDTO(taskRepository.save(task));
    }

    public void addCommentToTask(final UserDetailsImpl currentUser, final UUID taskId, final String commentContent) {
        User user = userService.findUserByUsername(currentUser.getUsername());
        Task task = getTaskForShow(taskId);
        Comment comment = new Comment(commentContent, user, LocalDateTime.now(), task);
        commentRepository.save(comment);
    }
    public TaskDTO takeTaskToWork(final UserDetailsImpl currentUser, final UUID userId, final UUID taskId) {
        Task task = getTaskForShow(taskId);
        if (task.getCreator() != null) {
            throw new WrongTasksParameterException("Нельзя взять в работу задачу, у которой уже есть исполнитель");
        }
        if (isUserIsAdmin(currentUser)) {
            User user = userRepository.findById(taskId).orElseThrow(() ->
                    new UserNotFoundException("Пользователь не найден"));
            task.setExecutor(user);
        }
        else {
            task.setExecutor(userService.findUserByUsername(currentUser.getUsername()));
        }
        task.setStatus(EnumStatus.AT_WORK);
        return taskMapper.toTaskDTO(taskRepository.save(task));
    }

    public TaskDTO closeTask(final UserDetailsImpl currentUser, final UUID taskId) {
        Task task = getTaskForShow(taskId);
        User user = userService.findUserByUsername(currentUser.getUsername());
        if (!currentUser.getAuthority().toString().equals("ADMIN") &&
                user != task.getExecutor() && user != task.getCreator()) {
            throw new NoRightException("У вас нет прав для закрытия этой задачи");
        }
        task.setStatus(EnumStatus.CLOSED);
        task.setCloseDate(LocalDateTime.now());
        return taskMapper.toTaskDTO(taskRepository.save(task));
    }

    private void isEmptyTaskDTO (final TaskDTO taskDTO) {
        if (taskDTO == null) {
            throw new WrongTasksParameterException("Объект задачи пустой");
        }
    }

    private boolean isUserIsAdmin(final UserDetailsImpl currentUser) {
        return currentUser.getAuthority().toString().equals("ADMIN");
    }

    private Task getTaskForShow(final UUID taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
                new WrongTasksParameterException("Задача не найдена"));
        return task;
    }
}