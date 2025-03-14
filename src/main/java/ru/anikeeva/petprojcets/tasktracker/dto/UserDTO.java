package ru.anikeeva.petprojcets.tasktracker.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserDTO(
        @NotNull UUID id,
        @NotNull String username,
        @NotNull String phone,
        @NotNull boolean isDeleted
        )
{}