package com.uikit.core.component;

import java.util.Objects;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * A text field (input or textarea) identified by a caller-supplied locator. The element is looked
 * up on every call, so the object stays valid across page re-renders.
 */
public final class TextInput {
    private final UiContext ctx;
    private final By by;
    private final String description;

    /**
     * Creates a text input with default waits.
     *
     * @param driver the WebDriver
     * @param by     locator of the field
     */
    public TextInput(WebDriver driver, By by) {
        this(UiContext.of(driver), by);
    }

    /**
     * Creates a text input with an explicit context.
     *
     * @param ctx shared context (waits, click policy)
     * @param by  locator of the field
     */
    public TextInput(UiContext ctx, By by) {
        this.ctx = Objects.requireNonNull(ctx, "ctx");
        this.by = Objects.requireNonNull(by, "by");
        this.description = "text input [" + by + "]";
    }

    /**
     * Replaces the field's content with {@code text}.
     *
     * @param text the text, must not be null (use {@link #clear()} to empty the field)
     * @return this input
     * @throws com.uikit.core.exception.ElementNotFoundException if the field is not displayed
     * @throws com.uikit.core.exception.UiKitTimeoutException    if it stays disabled or read-only
     */
    public TextInput type(String text) {
        requireText(text);
        WebElement el = editable();
        el.clear();
        el.sendKeys(text);
        return this;
    }

    /**
     * Appends {@code text} to the existing content.
     *
     * @param text the text, must not be null
     * @return this input
     * @throws com.uikit.core.exception.ElementNotFoundException if the field is not displayed
     * @throws com.uikit.core.exception.UiKitTimeoutException    if it stays disabled or read-only
     */
    public TextInput append(String text) {
        requireText(text);
        editable().sendKeys(text);
        return this;
    }

    /**
     * Clears the field.
     *
     * @return this input
     * @throws com.uikit.core.exception.ElementNotFoundException if the field is not displayed
     * @throws com.uikit.core.exception.UiKitTimeoutException    if it stays disabled or read-only
     */
    public TextInput clear() {
        editable().clear();
        return this;
    }

    /**
     * Reads the current value.
     *
     * @return the field's {@code value} property (empty string if unset)
     * @throws com.uikit.core.exception.ElementNotFoundException if the field is not displayed
     */
    public String value() {
        String v = ctx.findUnique(ctx.driver(), by, description).getAttribute("value");
        return v == null ? "" : v;
    }

    private WebElement editable() {
        WebElement el = ctx.findUnique(ctx.driver(), by, description);
        return ctx.waits().until("editable " + description,
                d -> el.isEnabled() && el.getAttribute("readonly") == null ? el : null);
    }

    private static void requireText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
    }
}
