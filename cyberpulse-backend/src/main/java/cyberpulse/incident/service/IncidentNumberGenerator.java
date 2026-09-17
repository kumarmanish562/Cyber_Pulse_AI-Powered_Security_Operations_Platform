package cyberpulse.incident.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
@RequiredArgsConstructor
public class IncidentNumberGenerator {

    private final JdbcTemplate jdbcTemplate;

    public String next() {

        Long sequence =
                jdbcTemplate.queryForObject(
                        "SELECT nextval('incident_number_seq')",
                        Long.class
                );

        return String.format(
                "INC-%d-%06d",
                Year.now().getValue(),
                sequence
        );
    }
}