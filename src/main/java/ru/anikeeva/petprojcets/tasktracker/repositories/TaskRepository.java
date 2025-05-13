package ru.anikeeva.petprojcets.tasktracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.anikeeva.petprojcets.tasktracker.models.Task;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByCreatorId(UUID creatorId);
    List<Task> findByExecutorId(UUID executorId);
}
