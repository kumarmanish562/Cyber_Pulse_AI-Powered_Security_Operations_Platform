package cyberpulse.audit.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditDetailsSerializer {

    private final ObjectMapper objectMapper;

    public String serialize(
            Object details
    ) {

        try {

            return objectMapper.writeValueAsString(
                    details
            );

        } catch (JsonProcessingException exception) {

            throw new IllegalStateException(
                    "Failed to serialize audit details",
                    exception
            );
        }
    }
}