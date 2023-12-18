package ru.yandex.practicum.filmorate.model;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DataValid implements ConstraintValidator<DataValidAnnotation, LocalDate> {
    private static final  LocalDate before = LocalDate.of(1895, 12, 28);
    @Override
    public void initialize(DataValidAnnotation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if(value==null){
            return false;
        }
        boolean a = value.isAfter(before);
        return a;
    }
}
