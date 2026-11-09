package com.uikit.core.wait;

import com.uikit.core.config.WaitConfig;
import com.uikit.core.exception.UiKitTimeoutException;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized explicit waits. All components obtain elements through this class so that timeout,
 * polling and failure reporting behave identically everywhere.
 */
public final class Waits {
    private static final Logger LOG = LoggerFactory.getLogger(Waits.class);

    private final WebDriver driver;
    private final WaitConfig config;

    /**
     * Creates waits using {@link WaitConfig#defaults()}.
     *
     * @param driver the driver to wait on
     */
    public Waits(WebDriver driver) {
        this(driver, WaitConfig.defaults());
    }

    /**
     * Creates waits with explicit configuration.
     *
     * @param driver the driver to wait on
     * @param config timeout and polling settings
     */
    public Waits(WebDriver driver, WaitConfig config) {
        this.driver = Objects.requireNonNull(driver, "driver");
        this.config = Objects.requireNonNull(config, "config");
    }

    /**
     * Returns the active configuration.
     *
     * @return the wait configuration
     */
    public WaitConfig config() {
        return config;
    }

    /**
     * Waits until an element is present in the DOM.
     *
     * @param locator element locator
     * @return the element
     * @throws UiKitTimeoutException if not present in time
     */
    public WebElement present(By locator) {
        return until("presence of " + locator, ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Waits until an element is displayed.
     *
     * @param locator element locator
     * @return the visible element
     * @throws UiKitTimeoutException if not visible in time
     */
    public WebElement visible(By locator) {
        return until("visibility of " + locator, ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Waits until an element is displayed and enabled.
     *
     * @param locator element locator
     * @return the clickable element
     * @throws UiKitTimeoutException if not clickable in time
     */
    public WebElement clickable(By locator) {
        return until("clickability of " + locator, ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Waits until no displayed element matches the locator.
     *
     * @param locator element locator
     * @throws UiKitTimeoutException if the element is still visible after the timeout
     */
    public void invisible(By locator) {
        until("invisibility of " + locator, ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Waits until at least one element matches the locator.
     *
     * @param locator element locator
     * @return all matching elements
     * @throws UiKitTimeoutException if none appear in time
     */
    public List<WebElement> allPresent(By locator) {
        return until("presence of any " + locator, ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    /**
     * Waits for an arbitrary condition. Stale-element and no-such-element errors are retried.
     *
     * @param description description used in the failure message
     * @param condition   the condition to evaluate repeatedly
     * @param <T>         result type
     * @return the condition's non-null, non-false result
     * @throws UiKitTimeoutException if the condition is not met in time
     */
    public <T> T until(String description, Function<? super WebDriver, T> condition) {
        WebDriverWait wait = new WebDriverWait(driver, config.timeout(), config.polling());
        wait.ignoring(NoSuchElementException.class, StaleElementReferenceException.class);
        try {
            LOG.debug("Waiting for {} (timeout={})", description, config.timeout());
            return wait.until(condition);
        } catch (TimeoutException e) {
            throw new UiKitTimeoutException(description, config.timeout(), e);
        }
    }
}
