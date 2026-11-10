package com.uikit.core.component;

import java.util.Objects;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Handles native JavaScript dialogs ({@code alert}, {@code confirm}, {@code prompt}). */
public final class Alerts {
    private final UiContext ctx;

    /**
     * Creates the helper.
     *
     * @param ctx shared context (waits)
     */
    public Alerts(UiContext ctx) {
        this.ctx = Objects.requireNonNull(ctx, "ctx");
    }

    /**
     * Reports whether a dialog is currently open, without waiting.
     *
     * @return true if a dialog is open
     */
    public boolean isPresent() {
        try {
            ctx.driver().switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    /**
     * Waits for a dialog and returns its message without closing it.
     *
     * @return the dialog text
     * @throws com.uikit.core.exception.UiKitTimeoutException if no dialog appears in time
     */
    public String text() {
        return alert().getText();
    }

    /**
     * Waits for a dialog and accepts it (OK).
     *
     * @return this helper
     * @throws com.uikit.core.exception.UiKitTimeoutException if no dialog appears in time
     */
    public Alerts accept() {
        alert().accept();
        return this;
    }

    /**
     * Waits for a dialog and dismisses it (Cancel).
     *
     * @return this helper
     * @throws com.uikit.core.exception.UiKitTimeoutException if no dialog appears in time
     */
    public Alerts dismiss() {
        alert().dismiss();
        return this;
    }

    /**
     * Waits for a prompt dialog, types {@code text} and accepts it.
     *
     * @param text the text to enter, must not be null
     * @return this helper
     * @throws com.uikit.core.exception.UiKitTimeoutException if no dialog appears in time
     */
    public Alerts typeAndAccept(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        Alert a = alert();
        a.sendKeys(text);
        a.accept();
        return this;
    }

    private Alert alert() {
        return ctx.waits().until("a JavaScript dialog", ExpectedConditions.alertIsPresent());
    }
}
