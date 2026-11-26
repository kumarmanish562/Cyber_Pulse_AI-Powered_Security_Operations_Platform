package cyberpulse.common.web;

import java.util.List;
import java.util.function.Function;

/**
 * RFC 4180 CSV serializer with formula-injection neutralization for compliance exports.
 */
public final class CsvExportUtil {

    private CsvExportUtil() {
    }

    public static <T> String toCsv(
            List<String> headers,
            List<T> rows,
            Function<T, List<Object>> rowMapper
    ) {
        StringBuilder sb = new StringBuilder();
        appendRow(sb, headers.stream().<Object>map(h -> h).toList());
        for (T row : rows) {
            appendRow(sb, rowMapper.apply(row));
        }
        return sb.toString();
    }

    private static void appendRow(StringBuilder sb, List<Object> cells) {
        for (int i = 0; i < cells.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(escapeCell(cells.get(i)));
        }
        sb.append("\r\n");
    }

    private static String escapeCell(Object value) {
        if (value == null) {
            return "";
        }
        String str = value.toString();
        if (!str.isEmpty()) {
            char first = str.charAt(0);
            if (first == '=' || first == '+' || first == '-' || first == '@') {
                str = "'" + str;
            }
        }
        boolean needsQuotes = str.contains(",") || str.contains("\"")
                || str.contains("\n") || str.contains("\r");
        if (!needsQuotes) {
            return str;
        }
        return "\"" + str.replace("\"", "\"\"") + "\"";
    }
}
