package de.moneygoal;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Liest den Kontostand aus einer Chat-Nachricht des Servers.
 */
public final class MoneyGoalParser {

    private static final Pattern NUMBER = Pattern.compile("[0-9][0-9.,']{0,18}");

    private MoneyGoalParser() {
    }

    public static Double parse(String message, MoneyGoalConfig config) {
        if (message == null || message.isEmpty()) {
            return null;
        }
        // Farbcodes des Servers entfernen.
        String text = message.replaceAll("\u00A7.", "");
        if (text.isEmpty()) {
            return null;
        }

        for (String pattern : config.patterns) {
            try {
                Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(text);
                if (matcher.find()) {
                    String group = matcher.groupCount() >= 1 ? matcher.group(1) : matcher.group();
                    Double value = toNumber(group);
                    if (value != null && value > 0.0d) {
                        return value;
                    }
                }
            } catch (RuntimeException ignored) {
                // Ein kaputtes Muster darf die Mod nicht stoppen.
            }
        }

        String lower = text.toLowerCase(Locale.ROOT);
        boolean relevant = config.keywords.isEmpty();
        for (String keyword : config.keywords) {
            if (lower.contains(keyword.toLowerCase(Locale.ROOT))) {
                relevant = true;
                break;
            }
        }
        if (!relevant) {
            return null;
        }

        Double best = null;
        Matcher matcher = NUMBER.matcher(text);
        while (matcher.find()) {
            Double value = toNumber(matcher.group());
            if (value != null && (best == null || value > best)) {
                best = value;
            }
        }
        return best;
    }

    /** Wandelt "12.500,50" oder "12,500.50" in eine Zahl um. */
    public static Double toNumber(String raw) {
        if (raw == null) {
            return null;
        }
        String text = raw.trim().replace("'", "").replace(" ", "");
        if (text.isEmpty()) {
            return null;
        }
        int dot = text.lastIndexOf('.');
        int comma = text.lastIndexOf(',');
        int separator = Math.max(dot, comma);
        if (separator >= 0) {
            String after = text.substring(separator + 1);
            boolean bothPresent = dot >= 0 && comma >= 0;
            boolean decimal = bothPresent || after.length() != 3;
            if (decimal) {
                text = text.substring(0, separator).replace(".", "").replace(",", "") + "." + after;
            } else {
                text = text.substring(0, separator).replace(".", "").replace(",", "");
            }
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException error) {
            return null;
        }
    }
}
