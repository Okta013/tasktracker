package ru.anikeeva.petprojcets.tasktracker.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.anikeeva.petprojcets.tasktracker.dto.TaskDTO;
import ru.anikeeva.petprojcets.tasktracker.models.Task;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {
    TaskDTO toTaskDTO(Task task);
    Task toTask(TaskDTO taskDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "creator", ignore = true)
    void updateTaskFromTaskDTO(TaskDTO taskDTO, @MappingTarget Task task);
}
