package ru.anikeeva.petprojcets.tasktracker.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtConfirmResponse {
    private String message;
    private boolean success;
}