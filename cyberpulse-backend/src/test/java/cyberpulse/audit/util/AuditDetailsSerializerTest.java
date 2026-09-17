package cyberpulse.audit.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AuditDetailsSerializerTest {

    private final AuditDetailsSerializer serializer =
            new AuditDetailsSerializer(
                    new ObjectMapper()
            );

    @Test
    void shouldSerializeDetails() {

        String result =
                serializer.serialize(
                        Map.of(
                                "oldStatus",
                                "OPEN",

                                "newStatus",
                                "RESOLVED"
                        )
                );


        assertNotNull(result);


        assertTrue(
                result.contains(
                        "\"oldStatus\":\"OPEN\""
                )
        );


        assertTrue(
                result.contains(
                        "\"newStatus\":\"RESOLVED\""
                )
        );
    }
}