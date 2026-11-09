package com.uikit.core.locator;

import java.util.Objects;

/** Helpers for building safe XPath expressions from user-supplied text. */
public final class XPaths {
    private XPaths() {
    }

    /**
     * Converts text into an XPath string literal, handling embedded single and double quotes.
     *
     * @param text the raw text
     * @return an XPath expression evaluating to exactly {@code text}
     */
    public static String literal(String text) {
        Objects.requireNonNull(text, "text");
        if (!text.contains("'")) {
            return "'" + text + "'";
        }
        if (!text.contains("\"")) {
            return "\"" + text + "\"";
        }
        StringBuilder sb = new StringBuilder("concat(");
        String[] parts = text.split("'", -1);
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                sb.append(", \"'\", ");
            }
            sb.append('\'').append(parts[i]).append('\'');
        }
        return sb.append(')').toString();
    }

    /**
     * XPath predicate matching an element whose normalized text equals {@code text}, ignoring case.
     *
     * @param text the expected text
     * @return a predicate body (without brackets)
     */
    public static String textEqualsIgnoreCase(String text) {
        return "translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = "
                + literal(text.toLowerCase(java.util.Locale.ROOT).trim());
    }
}
