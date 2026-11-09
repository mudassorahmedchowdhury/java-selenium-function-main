package com.uikit.core.locator;

import java.util.Objects;
import java.util.function.Function;
import org.openqa.selenium.By;

/**
 * Strategy that turns a button's visible label into a Selenium {@link By}. Supply your own to match
 * any markup; the built-in factories are generic and contain no site-specific selectors.
 */
public final class ButtonLocator {
    private final Function<String, By> byLabel;

    private ButtonLocator(Function<String, By> byLabel) {
        this.byLabel = Objects.requireNonNull(byLabel, "byLabel");
    }

    /**
     * Uses a custom label-to-locator function. The returned {@link By} is searched relative to the
     * current scope, so XPath expressions should start with {@code .//}.
     *
     * @param byLabel function mapping a label to a locator
     * @return the locator strategy
     */
    public static ButtonLocator of(Function<String, By> byLabel) {
        return new ButtonLocator(byLabel);
    }

    /**
     * Matches {@code <button>}, {@code <input type=button|submit|reset>} (by value) and
     * {@code role="button"} elements whose normalized text equals the label.
     *
     * @return the default strategy
     */
    public static ButtonLocator exactText() {
        return new ButtonLocator(label -> {
            String l = XPaths.literal(label.trim());
            return By.xpath(".//button[normalize-space(.)=" + l + "]"
                    + " | .//input[(@type='button' or @type='submit' or @type='reset') and normalize-space(@value)=" + l + "]"
                    + " | .//*[@role='button'][normalize-space(.)=" + l + "]");
        });
    }

    /**
     * Same elements as {@link #exactText()} but compares labels ignoring case.
     *
     * @return the case-insensitive strategy
     */
    public static ButtonLocator exactTextIgnoreCase() {
        return new ButtonLocator(label -> {
            String ci = XPaths.textEqualsIgnoreCase(label);
            String value = "translate(normalize-space(@value), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = "
                    + XPaths.literal(label.trim().toLowerCase(java.util.Locale.ROOT));
            return By.xpath(".//button[" + ci + "]"
                    + " | .//input[(@type='button' or @type='submit' or @type='reset') and " + value + "]"
                    + " | .//*[@role='button'][" + ci + "]");
        });
    }

    /**
     * Resolves a label to a locator.
     *
     * @param label the visible button label, must not be blank
     * @return the locator
     * @throws IllegalArgumentException if the label is null or blank
     */
    public By forLabel(String label) {
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("Button label cannot be null or blank");
        }
        return byLabel.apply(label);
    }
}
