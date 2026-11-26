package cyberpulse.common.web;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvExportUtilTest {

    @Test
    void shouldEscapeQuotesCommasAndFormulaPrefixes() {
        String csv = CsvExportUtil.toCsv(
                List.of("id", "note"),
                List.of(List.of("1", "hello, \"world\""), List.of("2", "=SUM(A1:A2)")),
                row -> row
        );

        assertTrue(csv.contains("\"hello, \"\"world\"\"\""));
        assertTrue(csv.contains("'=SUM(A1:A2)"));
    }
}
