package ru.anikeeva.petprojcets.tasktracker.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на сброс пароля")
public class ResetPasswordRequest {
    @Schema(description = "Новый пароль", example = "12345password")
    @Size(min = 8, max = 255, message = "Длина пароля должна быть от 8 до 255 символов")
    @NotBlank(message = "Пароль не может быть пустым")
    private String newPassword;

    @Schema(description = "Подтверждение нового пароля", example = "12345password")
    @Size(min = 8, max = 255, message = "Длина пароля должна быть от 8 до 255 символов")
    @NotBlank(message = "Пароль не может быть пустым")
    private String confirmPassword;
}
