package ru.anikeeva.petprojcets.tasktracker.services;

import ru.anikeeva.petprojcets.tasktracker.dto.UserDTO;
import ru.anikeeva.petprojcets.tasktracker.exceptions.DuplicateAccountException;
import ru.anikeeva.petprojcets.tasktracker.models.User;
import ru.anikeeva.petprojcets.tasktracker.payload.request.SignUpRequest;
import ru.anikeeva.petprojcets.tasktracker.payload.response.JwtConfirmResponse;

public interface IUserService {
    UserDTO registerUser(SignUpRequest user) throws DuplicateAccountException;
    void createVerificationToken(User user, String verificationToken);
    JwtConfirmResponse confirmEmail(String verificationToken);
}