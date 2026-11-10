package com.uikit.core.component;

import com.uikit.core.exception.UiKitTimeoutException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.openqa.selenium.WebDriver;

/** Switches between browser windows and tabs. */
public final class Windows {
    private final UiContext ctx;

    /**
     * Creates the helper.
     *
     * @param ctx shared context (waits)
     */
    public Windows(UiContext ctx) {
        this.ctx = Objects.requireNonNull(ctx, "ctx");
    }

    /**
     * Returns the handle of the window the driver is currently focused on.
     *
     * @return the current window handle
     */
    public String current() {
        return ctx.driver().getWindowHandle();
    }

    /**
     * Returns the number of open windows and tabs.
     *
     * @return the window count
     */
    public int count() {
        return ctx.driver().getWindowHandles().size();
    }

    /**
     * Runs {@code openAction} (for example a click that opens a new tab) and switches to the window
     * it opened.
     *
     * @param openAction action expected to open exactly one new window
     * @return this helper
     * @throws UiKitTimeoutException if no new window appears in time
     */
    public Windows switchToNewWindow(Runnable openAction) {
        Objects.requireNonNull(openAction, "openAction");
        Set<String> before = new HashSet<>(ctx.driver().getWindowHandles());
        openAction.run();
        String handle = ctx.waits().until("a new window to open", d -> {
            Set<String> now = new HashSet<>(d.getWindowHandles());
            now.removeAll(before);
            return now.isEmpty() ? null : now.iterator().next();
        });
        ctx.driver().switchTo().window(handle);
        return this;
    }

    /**
     * Switches to the window whose title equals {@code title}. If none matches in time the driver
     * is returned to the window it started on.
     *
     * @param title exact window title
     * @return this helper
     * @throws UiKitTimeoutException if no window has that title in time
     */
    public Windows switchToTitle(String title) {
        Objects.requireNonNull(title, "title");
        WebDriver driver = ctx.driver();
        String original = driver.getWindowHandle();
        try {
            String handle = ctx.waits().until("a window titled '" + title + "'", d -> {
                for (String h : d.getWindowHandles()) {
                    d.switchTo().window(h);
                    if (title.equals(d.getTitle())) {
                        return h;
                    }
                }
                return null;
            });
            driver.switchTo().window(handle);
        } catch (UiKitTimeoutException e) {
            if (driver.getWindowHandles().contains(original)) {
                driver.switchTo().window(original);
            }
            throw e;
        }
        return this;
    }

    /**
     * Switches to a known window handle.
     *
     * @param handle a handle from {@link #current()}
     * @return this helper
     */
    public Windows switchTo(String handle) {
        ctx.driver().switchTo().window(Objects.requireNonNull(handle, "handle"));
        return this;
    }

    /**
     * Closes the current window and switches to {@code handle}.
     *
     * @param handle the window to focus afterwards
     * @return this helper
     */
    public Windows closeCurrentAndSwitchTo(String handle) {
        ctx.driver().close();
        return switchTo(handle);
    }
}
