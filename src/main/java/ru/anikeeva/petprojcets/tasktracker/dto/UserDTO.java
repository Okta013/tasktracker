package ru.anikeeva.petprojcets.tasktracker.dto;

import jakarta.validation.constraints.NotNull;
import ru.anikeeva.petprojcets.tasktracker.models.Position;
import ru.anikeeva.petprojcets.tasktracker.models.enums.EnumRole;

import java.time.LocalDate;
import java.util.UUID;

public record UserDTO(
        @NotNull UUID id,
        @NotNull String username,
        @NotNull String email,
        @NotNull String phone,
        @NotNull LocalDate birthday,
        @NotNull String imageUrl,
        @NotNull boolean isEnabled,
        @NotNull boolean isDeleted,
        @NotNull EnumRole role,
        @NotNull Position position
        )
{}