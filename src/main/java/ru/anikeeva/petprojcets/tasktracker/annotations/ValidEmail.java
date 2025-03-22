package ru.anikeeva.petprojcets.tasktracker.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.anikeeva.petprojcets.tasktracker.validators.EmailValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
@Documented
public @interface ValidEmail {
    String message() default "Недействительный адрес электронной почты";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}