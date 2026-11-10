package com.uikit.core.component;

import java.util.Objects;
import java.util.function.Function;
import org.openqa.selenium.By;

/**
 * A non-native dropdown (a trigger that opens a list of options, typically built from {@code div},
 * {@code ul} or framework widgets). You describe the markup with a trigger locator and an
 * option-by-text locator; use {@link Dropdown} for native {@code <select>} elements.
 */
public final class CustomDropdown {
    private final UiContext ctx;
    private final By trigger;
    private final Function<String, By> option;

    /**
     * Creates a custom dropdown.
     *
     * @param ctx     shared context (waits, click policy)
     * @param trigger locator of the element that opens the list
     * @param option  maps an option's text to a locator, searched on the whole page because lists
     *                are often rendered outside the trigger's DOM subtree
     */
    public CustomDropdown(UiContext ctx, By trigger, Function<String, By> option) {
        this.ctx = Objects.requireNonNull(ctx, "ctx");
        this.trigger = Objects.requireNonNull(trigger, "trigger");
        this.option = Objects.requireNonNull(option, "option");
    }

    /**
     * Opens the dropdown and clicks the option with the given text.
     *
     * @param text option text, must not be blank
     * @return this dropdown
     * @throws com.uikit.core.exception.ElementNotFoundException if the trigger or option is not displayed
     */
    public CustomDropdown select(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Option text cannot be null or blank");
        }
        ctx.click(ctx.findUnique(ctx.driver(), trigger, "dropdown trigger"), "dropdown trigger");
        String desc = "dropdown option '" + text + "'";
        ctx.click(ctx.findUnique(ctx.driver(), option.apply(text), desc), desc);
        return this;
    }
}
