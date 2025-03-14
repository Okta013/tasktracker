package ru.anikeeva.petprojcets.tasktracker.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtConfirmResponse {
    @Schema(description = "Сообщение ответа", example = "Регистрация успешно подтверждена")
    private String message;

    @Schema(description = "Флаг успеха операции потдверждения", example = "false")
    private boolean success;
}