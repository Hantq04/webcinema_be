package vi.wbca.webcinema.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import vi.wbca.webcinema.validation.validator.UploadImagePathValidator;

@Constraint(validatedBy = UploadImagePathValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUploadImagePath {
    String message() default "Invalid upload image path";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
