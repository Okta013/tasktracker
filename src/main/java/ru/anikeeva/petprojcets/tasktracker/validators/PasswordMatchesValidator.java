package ru.anikeeva.petprojcets.tasktracker.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.anikeeva.petprojcets.tasktracker.annotations.PasswordMatches;
import ru.anikeeva.petprojcets.tasktracker.payload.request.SignUpRequest;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        SignUpRequest req = (SignUpRequest) obj;
        return req.getPassword().equals(req.getConfirmPassword());
    }
}
