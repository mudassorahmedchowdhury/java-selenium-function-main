package com.uikit.core.component;

import com.uikit.core.exception.UiKitException;
import java.util.Objects;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * A checkbox or radio button identified by a caller-supplied locator. State-changing methods only
 * click when the current state differs, so they are safe to call repeatedly. Radio buttons can be
 * checked but never unchecked.
 */
public final class Checkbox {
    private final UiContext ctx;
    private final By by;
    private final String description;

    /**
     * Creates a checkbox with default waits.
     *
     * @param driver the WebDriver
     * @param by     locator of the input
     */
    public Checkbox(WebDriver driver, By by) {
        this(UiContext.of(driver), by);
    }

    /**
     * Creates a checkbox with an explicit context.
     *
     * @param ctx shared context (waits, click policy)
     * @param by  locator of the input
     */
    public Checkbox(UiContext ctx, By by) {
        this.ctx = Objects.requireNonNull(ctx, "ctx");
        this.by = Objects.requireNonNull(by, "by");
        this.description = "checkbox [" + by + "]";
    }

    /**
     * Ensures the input is checked.
     *
     * @return this checkbox
     * @throws com.uikit.core.exception.ElementNotFoundException if it is not displayed
     * @throws UiKitException                                    if the click did not change the state
     */
    public Checkbox check() {
        return set(true);
    }

    /**
     * Ensures the input is unchecked.
     *
     * @return this checkbox
     * @throws com.uikit.core.exception.ElementNotFoundException if it is not displayed
     * @throws UiKitException                                    if it is a checked radio button, or
     *                                                           the click did not change the state
     */
    public Checkbox uncheck() {
        return set(false);
    }

    /**
     * Ensures the input is in the given state.
     *
     * @param checked the desired state
     * @return this checkbox
     * @throws com.uikit.core.exception.ElementNotFoundException if it is not displayed
     * @throws UiKitException                                    if it is a checked radio button and
     *                                                           {@code checked} is false, or the click
     *                                                           did not change the state
     */
    public Checkbox set(boolean checked) {
        WebElement el = ctx.findUnique(ctx.driver(), by, description);
        if (el.isSelected() == checked) {
            return this;
        }
        if (!checked && "radio".equalsIgnoreCase(el.getAttribute("type"))) {
            throw new UiKitException("A radio button cannot be unchecked: " + description);
        }
        ctx.click(el, description);
        if (el.isSelected() != checked) {
            throw new UiKitException("Clicking did not change the state to " + checked + ": " + description);
        }
        return this;
    }

    /**
     * Reports whether the input is currently checked.
     *
     * @return true if checked
     * @throws com.uikit.core.exception.ElementNotFoundException if it is not displayed
     */
    public boolean isChecked() {
        return ctx.findUnique(ctx.driver(), by, description).isSelected();
    }
}
