package com.uikit.testing;

import org.openqa.selenium.WebDriver;

/**
 * Creates the {@link WebDriver} for each test. Implementations need a public no-argument
 * constructor. Provide your own via {@link UiKitTest#factory()} to use another browser or a grid.
 */
@FunctionalInterface
public interface DriverFactory {

    /**
     * Creates a new driver.
     *
     * @param settings the resolved {@link UiKitTest} settings
     * @return a ready-to-use driver; the extension quits it after the test
     */
    WebDriver create(UiKitTest settings);
}
