package cyberpulse.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.InetAddress;

public class IpAddressValidator
        implements ConstraintValidator<IpAddress, String> {

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context
    ) {

        if (value == null || value.isBlank()) {
            return true;
        }

        String ip = value.trim();

        // Prevent hostnames such as:
        // localhost
        // example.com
        // my-server
        if (!ip.matches("^[0-9a-fA-F:.]+$")) {
            return false;
        }

        try {
            InetAddress.getByName(ip);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}