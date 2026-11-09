package com.uikit.core.support;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/** Base class for integration tests: headless Chrome loading local HTML fixtures. */
public abstract class ChromeFixtureSupport {
    protected WebDriver driver;

    @BeforeEach
    void startBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-gpu", "--window-size=1280,800");
        driver = new ChromeDriver(options);
    }

    @AfterEach
    void stopBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Opens a fixture from {@code src/test/resources/fixtures}.
     *
     * @param name file name, e.g. {@code waits.html}
     */
    protected void open(String name) {
        URL url = getClass().getResource("/fixtures/" + name);
        if (url == null) {
            throw new IllegalArgumentException("Missing fixture: " + name);
        }
        try {
            Path path = Paths.get(url.toURI());
            driver.get(path.toUri().toString());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
