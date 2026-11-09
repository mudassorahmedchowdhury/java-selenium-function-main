package com.uikit.testing;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Default factory: local Chrome, headless unless {@link UiKitTest#headless()} is false or the JVM
 * property {@code -Duikit.headed=true} is set (handy for debugging).
 */
public final class ChromeDriverFactory implements DriverFactory {

    /** Creates the factory. */
    public ChromeDriverFactory() {
        // public no-arg constructor required by the extension
    }

    @Override
    public WebDriver create(UiKitTest settings) {
        ChromeOptions options = new ChromeOptions();
        boolean headless = settings.headless() && !Boolean.getBoolean("uikit.headed");
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments(
                "--no-sandbox",
                "--disable-gpu",
                "--window-size=" + settings.windowWidth() + "," + settings.windowHeight());
        return new ChromeDriver(options);
    }
}
