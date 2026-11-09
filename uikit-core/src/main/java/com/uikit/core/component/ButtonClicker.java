package com.uikit.core.component;

import com.uikit.core.locator.ButtonLocator;
import java.util.Objects;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Clicks buttons by label on the page, inside a caller-defined container (modal, dialog, panel) or
 * inside an iframe. Containers are passed as locators, so nothing here is tied to a framework.
 */
public final class ButtonClicker {
    private final UiContext ctx;
    private final ButtonLocator buttons;

    /**
     * Creates a clicker with default waits, native clicks and {@link ButtonLocator#exactText()}.
     *
     * @param driver the WebDriver
     */
    public ButtonClicker(WebDriver driver) {
        this(UiContext.of(driver), ButtonLocator.exactText());
    }

    /**
     * Creates a clicker with explicit context and button locator strategy.
     *
     * @param ctx     shared context (waits, click policy)
     * @param buttons how labels map to locators
     */
    public ButtonClicker(UiContext ctx, ButtonLocator buttons) {
        this.ctx = Objects.requireNonNull(ctx, "ctx");
        this.buttons = Objects.requireNonNull(buttons, "buttons");
    }

    /**
     * Clicks the single visible button with the given label anywhere on the page.
     *
     * @param label visible button label
     * @return this clicker
     * @throws com.uikit.core.exception.ElementNotFoundException  if none is found
     * @throws com.uikit.core.exception.AmbiguousElementException if several match
     */
    public ButtonClicker clickButtonByText(String label) {
        By by = buttons.forLabel(label);
        ctx.click(ctx.findUnique(ctx.driver(), by, "button '" + label + "'"), "button '" + label + "'");
        return this;
    }

    /**
     * Clicks a button inside the container identified by {@code container}.
     *
     * @param container locator of the container (for example a modal), supplied by the caller
     * @param label     visible button label
     * @return this clicker
     * @throws com.uikit.core.exception.ElementNotFoundException  if the container or button is missing
     * @throws com.uikit.core.exception.AmbiguousElementException if several match
     */
    public ButtonClicker clickButtonInContainer(By container, String label) {
        Objects.requireNonNull(container, "container");
        By by = buttons.forLabel(label);
        WebElement scope = ctx.findUnique(ctx.driver(), container, "container");
        String desc = "button '" + label + "' in container";
        ctx.click(ctx.findUnique(scope, by, desc), desc);
        return this;
    }

    /**
     * Clicks a button inside an iframe, then always switches back to the default content.
     *
     * @param frame locator of the iframe
     * @param label visible button label
     * @return this clicker
     * @throws com.uikit.core.exception.UiKitTimeoutException if the frame never becomes available
     */
    public ButtonClicker clickButtonInFrame(By frame, String label) {
        Objects.requireNonNull(frame, "frame");
        By by = buttons.forLabel(label);
        try {
            ctx.waits().until("frame " + frame, ExpectedConditions.frameToBeAvailableAndSwitchToIt(frame));
            String desc = "button '" + label + "' in frame";
            ctx.click(ctx.findUnique(ctx.driver(), by, desc), desc);
        } finally {
            ctx.driver().switchTo().defaultContent();
        }
        return this;
    }
}
