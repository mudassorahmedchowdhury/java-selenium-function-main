package com.uikit.core.component;

import com.uikit.core.config.WaitConfig;
import com.uikit.core.exception.AmbiguousElementException;
import com.uikit.core.exception.ClickFailedException;
import com.uikit.core.exception.ElementNotFoundException;
import com.uikit.core.exception.UiKitTimeoutException;
import com.uikit.core.wait.Waits;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shared, immutable context for all components: the driver, centralized {@link Waits} and the
 * click policy. Native Selenium clicks are used unless JavaScript fallback is explicitly enabled.
 */
public final class UiContext {
    private static final Logger LOG = LoggerFactory.getLogger(UiContext.class);

    private final WebDriver driver;
    private final Waits waits;
    private final boolean jsClickFallback;

    private UiContext(WebDriver driver, Waits waits, boolean jsClickFallback) {
        this.driver = Objects.requireNonNull(driver, "driver");
        this.waits = waits;
        this.jsClickFallback = jsClickFallback;
    }

    /**
     * Creates a context with default waits and native clicks only.
     *
     * @param driver the WebDriver, must not be null
     * @return the context
     */
    public static UiContext of(WebDriver driver) {
        return new UiContext(driver, new Waits(driver), false);
    }

    /**
     * Returns a copy using different wait settings.
     *
     * @param config timeout and polling
     * @return the new context
     */
    public UiContext withWaitConfig(WaitConfig config) {
        return new UiContext(driver, new Waits(driver, config), jsClickFallback);
    }

    /**
     * Returns a copy that retries failed native clicks (intercepted or not interactable) using
     * JavaScript {@code element.click()}. This bypasses real user-interaction checks, so it is off
     * by default.
     *
     * @return the new context
     */
    public UiContext withJsClickFallback() {
        return new UiContext(driver, waits, true);
    }

    /**
     * The driver.
     *
     * @return the WebDriver
     */
    public WebDriver driver() {
        return driver;
    }

    /**
     * The centralized waits.
     *
     * @return the waits
     */
    public Waits waits() {
        return waits;
    }

    /**
     * Finds the single displayed element matching {@code by}, waiting for it to appear.
     *
     * @param scope       where to search (driver or a parent element)
     * @param by          the locator
     * @param description description for error messages
     * @return the element
     * @throws ElementNotFoundException  if no displayed element appears in time
     * @throws AmbiguousElementException if more than one displayed element matches
     */
    public WebElement findUnique(SearchContext scope, By by, String description) {
        List<WebElement> found;
        try {
            found = waits.until(description, d -> {
                List<WebElement> shown = scope.findElements(by).stream()
                        .filter(WebElement::isDisplayed)
                        .collect(Collectors.toList());
                return shown.isEmpty() ? null : shown;
            });
        } catch (UiKitTimeoutException e) {
            throw new ElementNotFoundException(description + " [" + by + "]", e);
        }
        if (found.size() > 1) {
            throw new AmbiguousElementException(description + " [" + by + "]", found.size());
        }
        return found.get(0);
    }

    /**
     * Waits for the element to be clickable and clicks it natively; falls back to JavaScript only
     * if {@link #withJsClickFallback()} was enabled.
     *
     * @param element     the element
     * @param description description for error messages
     * @throws ClickFailedException if the click fails
     * @throws UiKitTimeoutException if the element never becomes clickable
     */
    public void click(WebElement element, String description) {
        waits.until("clickability of " + description, ExpectedConditions.elementToBeClickable(element));
        try {
            element.click();
        } catch (ElementNotInteractableException e) {
            if (!jsClickFallback) {
                throw new ClickFailedException(description, e);
            }
            LOG.warn("Native click failed for {}; using JavaScript fallback", description);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }
}
