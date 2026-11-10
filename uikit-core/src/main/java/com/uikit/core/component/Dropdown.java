package com.uikit.core.component;

import com.uikit.core.exception.ElementNotFoundException;
import com.uikit.core.exception.UiKitException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.UnexpectedTagNameException;

/**
 * A native HTML {@code <select>} identified by a caller-supplied locator. Custom (non-{@code
 * select}) dropdown widgets are not supported.
 */
public final class Dropdown {
    private final UiContext ctx;
    private final By by;
    private final String description;

    /**
     * Creates a dropdown with default waits.
     *
     * @param driver the WebDriver
     * @param by     locator of the {@code <select>}
     */
    public Dropdown(WebDriver driver, By by) {
        this(UiContext.of(driver), by);
    }

    /**
     * Creates a dropdown with an explicit context.
     *
     * @param ctx shared context (waits, click policy)
     * @param by  locator of the {@code <select>}
     */
    public Dropdown(UiContext ctx, By by) {
        this.ctx = Objects.requireNonNull(ctx, "ctx");
        this.by = Objects.requireNonNull(by, "by");
        this.description = "dropdown [" + by + "]";
    }

    /**
     * Selects the option whose visible text equals {@code text}.
     *
     * @param text visible option text
     * @return this dropdown
     * @throws ElementNotFoundException if the dropdown or the option does not exist
     * @throws UiKitException           if the element is not a {@code <select>}
     */
    public Dropdown selectByText(String text) {
        requireArg(text, "text");
        try {
            select().selectByVisibleText(text);
        } catch (NoSuchElementException e) {
            throw new ElementNotFoundException("option with text '" + text + "' in " + description, e);
        }
        return this;
    }

    /**
     * Selects the option whose {@code value} attribute equals {@code value}.
     *
     * @param value option value
     * @return this dropdown
     * @throws ElementNotFoundException if the dropdown or the option does not exist
     * @throws UiKitException           if the element is not a {@code <select>}
     */
    public Dropdown selectByValue(String value) {
        requireArg(value, "value");
        try {
            select().selectByValue(value);
        } catch (NoSuchElementException e) {
            throw new ElementNotFoundException("option with value '" + value + "' in " + description, e);
        }
        return this;
    }

    /**
     * Returns the visible text of the selected option.
     *
     * @return the selected option's text
     * @throws ElementNotFoundException if the dropdown is not displayed
     * @throws UiKitException           if the element is not a {@code <select>}
     */
    public String selectedText() {
        return select().getFirstSelectedOption().getText();
    }

    /**
     * Returns the visible text of every option, in document order.
     *
     * @return the option texts
     * @throws ElementNotFoundException if the dropdown is not displayed
     * @throws UiKitException           if the element is not a {@code <select>}
     */
    public List<String> options() {
        return select().getOptions().stream().map(WebElement::getText).collect(Collectors.toList());
    }

    private Select select() {
        WebElement el = ctx.findUnique(ctx.driver(), by, description);
        ctx.waits().until("enabled " + description, d -> el.isEnabled() ? el : null);
        try {
            return new Select(el);
        } catch (UnexpectedTagNameException e) {
            throw new UiKitException(description + " is not a <select> element", e);
        }
    }

    private static void requireArg(String arg, String name) {
        if (arg == null) {
            throw new IllegalArgumentException(name + " cannot be null");
        }
    }
}
