package ru.anikeeva.petprojcets.tasktracker.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.anikeeva.petprojcets.tasktracker.exceptions.IncorrectParametersException;
import ru.anikeeva.petprojcets.tasktracker.exceptions.NoRightException;
import ru.anikeeva.petprojcets.tasktracker.exceptions.UserNotFoundException;
import ru.anikeeva.petprojcets.tasktracker.models.SupportiveToken;
import ru.anikeeva.petprojcets.tasktracker.models.User;
import ru.anikeeva.petprojcets.tasktracker.models.enums.EnumResetPasswordParameter;
import ru.anikeeva.petprojcets.tasktracker.models.impl.UserDetailsImpl;
import ru.anikeeva.petprojcets.tasktracker.payload.request.ChangePasswordRequest;
import ru.anikeeva.petprojcets.tasktracker.payload.request.ResetPasswordRequest;
import ru.anikeeva.petprojcets.tasktracker.payload.response.ChangeForgottenPasswordResponse;
import ru.anikeeva.petprojcets.tasktracker.repositories.SupportiveTokenRepository;
import ru.anikeeva.petprojcets.tasktracker.repositories.UserRepository;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SupportiveTokenRepository supportiveTokenRepository;
    private final MessageSource messages;
    private final JavaMailSender mailSender;
    private final int EXPIRY_TIME_MINUTES = 15;
    private final String APP_URL = "http://localhost:8080";

    public void changePassword(final UserDetailsImpl currentUser, final UUID userId, final ChangePasswordRequest request) {
        if (!currentUser.getId().equals(userId)) {
            throw new NoRightException("У вас нет прав на изменение профиля этого пользователя");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IncorrectParametersException("Старый пароль неверный");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IncorrectParametersException("Новый пароль и его подтверждение не совпадают");
        }
        String encodeNewPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodeNewPassword);
        userRepository.save(user);
    }

    public void resetPassword(final EnumResetPasswordParameter parameter, final String value) throws MessagingException {
        User user = findUserByParameter(parameter, value);
        if (user == null) {
            throw new IncorrectParametersException("Введены некорректные параметры для поиска пользователя");
        }
        if (!user.isEnabled() || !user.isDeleted()) {
            throw new IncorrectParametersException("Нельзя сбросить пароль у неактивного пользователя");
        }
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[24];
        random.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        createResetPasswordToken(user, token);
        String recipientAddress = user.getEmail();
        String subject = "Восстановление пароля";
        String resetPasswordUrl = APP_URL + "/signin/reset_password?token=" + token;
        String message = messages.getMessage("message.resetPass", null, Locale.getDefault());
        MimeMessage mailMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage, "UTF-8");
        mailMessage.setHeader("Content-Type", "text/html; charset=UTF-8");
        helper.setTo(recipientAddress);
        helper.setSubject(subject);
        helper.setText(message + "<br><a href='" + resetPasswordUrl + "'>Сбросить пароль</a>", true);
        mailSender.send(mailMessage);
    }

    public ChangeForgottenPasswordResponse changeForgottenPassword(final ResetPasswordRequest request, final String token) {
        if (request == null) {
            throw new IncorrectParametersException("Пустой запрос");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IncorrectParametersException("Введенные пароли не совпадают");
        }
        SupportiveToken resetPasswordToken = supportiveTokenRepository.findByToken(token).orElseThrow(() ->
                new IncorrectParametersException("Токен не найден"));
        if (!isResetTokenValid(token)) { throw new IncorrectParametersException("Токен недействителен"); }
        User user = supportiveTokenRepository.findUserByToken(token);
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        supportiveTokenRepository.delete(resetPasswordToken);
        return new ChangeForgottenPasswordResponse("Пароль успешно изменен", true);
    }

    public boolean isResetTokenValid(String token) {
        Optional<SupportiveToken> resetToken = supportiveTokenRepository.findByToken(token);
        return resetToken.isPresent() &&
                resetToken.get().getExpiryDate().after(new Date()) &&
                MessageDigest.isEqual(token.getBytes(), resetToken.get().getToken().getBytes());
    }

    private User findUserByParameter(final EnumResetPasswordParameter parameter, final String value) {
        User user = new User();
        switch (parameter) {
            case USERNAME -> user = userRepository.findByUsername(value).orElseThrow(() ->
                    new UserNotFoundException("Пользователь с таким именем не найден"));
            case PHONE -> user = userRepository.findByPhone(value).orElseThrow(() ->
                    new UserNotFoundException("Пользователь с таким номером телефона не найден"));
            case EMAIL -> user = userRepository.findByEmail(value).orElseThrow(() ->
                    new UserNotFoundException("Пользователь с таким адресом электронной почты не найден"));
        }
        return user;
    }

    private void createResetPasswordToken(final User user, final String resetPasswordToken) {
        SupportiveToken token = new SupportiveToken(resetPasswordToken, user, EXPIRY_TIME_MINUTES);
        supportiveTokenRepository.save(token);
    }
}