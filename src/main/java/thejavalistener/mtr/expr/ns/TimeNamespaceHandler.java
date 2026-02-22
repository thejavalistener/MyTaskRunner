package thejavalistener.mtr.expr.ns;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import thejavalistener.mtr.expr.NamespaceHandler;

public class TimeNamespaceHandler implements NamespaceHandler
{
    @Override
    public String getNamespace()
    {
        return "time";
    }

    @Override
    public String resolve(String payload)
    {
        if (payload == null || payload.isBlank())
            throw new RuntimeException("time namespace requires arguments");

        String[] parts = payload.split(":");

        // special case
        if ("epochMillis".equals(parts[0]))
            return String.valueOf(System.currentTimeMillis());

        if (parts.length < 2)
            throw new RuntimeException("Invalid time expression: " + payload);

        String base = parts[0];

        LocalDateTime dt;

        switch (base)
        {
            case "now":
                dt = LocalDateTime.now();
                break;

            case "today":
                dt = LocalDate.now().atStartOfDay();
                break;

            case "tomorrow":
                dt = LocalDate.now().plusDays(1).atStartOfDay();
                break;

            case "yesterday":
                dt = LocalDate.now().minusDays(1).atStartOfDay();
                break;

            default:
                throw new RuntimeException("Unknown time base: " + base);
        }

        // soporta: time:today:format:yyyyMMdd
        if ("format".equals(parts[1]))
        {
            if (parts.length < 3)
                throw new RuntimeException("Missing format pattern in time expression: " + payload);

            String pattern = payload.substring(
                    payload.indexOf("format:") + 7
            );

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return dt.format(formatter);
        }

        throw new RuntimeException("Unknown time operation: " + parts[1]);
    }
}