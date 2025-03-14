package ru.anikeeva.petprojcets.tasktracker.observer;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import ru.anikeeva.petprojcets.tasktracker.models.User;
import ru.anikeeva.petprojcets.tasktracker.services.IUserService;

import java.security.SecureRandom;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    private final IUserService userService;
    private final MessageSource messages;
    private final JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        try {
            this.confirmRegistration(event);
        } catch (MessagingException e) {
            throw new RuntimeException("Ошибка при отправке email" + e.getMessage(), e);
        }
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) throws MessagingException {
        User user = event.getUser();
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[24];
        random.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        userService.createVerificationToken(user, token);
        String recipientAddress = user.getEmail();
        String subject = "Подтверждение регистрации";
        String confirmationUrl = event.getAppUrl() + "/confirm?token=" + token;
        String message = messages.getMessage("message.regSucc", null, event.getLocale());
        MimeMessage mailMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage, "utf-8");
        helper.setTo(recipientAddress);
        helper.setSubject(subject);
        helper.setText(message + "<br><a href='" + confirmationUrl + "'>Подтвердить регистрацию</a>", true);
        mailSender.send(mailMessage);
    }
}