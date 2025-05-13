package ru.anikeeva.petprojcets.tasktracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import ru.anikeeva.petprojcets.tasktracker.models.Comment;
import ru.anikeeva.petprojcets.tasktracker.models.Project;
import ru.anikeeva.petprojcets.tasktracker.models.Tag;
import ru.anikeeva.petprojcets.tasktracker.models.User;
import ru.anikeeva.petprojcets.tasktracker.models.enums.EnumPriority;
import ru.anikeeva.petprojcets.tasktracker.models.enums.EnumStatus;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record TaskDTO(
        @NotNull @JsonProperty(access = JsonProperty.Access.READ_ONLY) UUID id,
        @NotNull String title,
        @NotNull String description,
        @NotNull EnumPriority priority,
        @NotNull @JsonProperty(access = JsonProperty.Access.READ_ONLY) LocalDateTime createdAt,
        @NotNull LocalDateTime startDate,
        @NotNull LocalDateTime endDate,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) LocalDateTime closeDate,
        Project project,
        Set<Tag> tags,
        @NotNull @JsonProperty(access = JsonProperty.Access.READ_ONLY) User creator,
        User executor,
        @NotNull EnumStatus status
) {
}
