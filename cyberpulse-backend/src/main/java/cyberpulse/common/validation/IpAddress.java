package cyberpulse.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IpAddressValidator.class)
@Target({
        ElementType.FIELD,
        ElementType.PARAMETER,
        ElementType.RECORD_COMPONENT
})
@Retention(RetentionPolicy.RUNTIME)
public @interface IpAddress {

    String message() default "Invalid IP address";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}