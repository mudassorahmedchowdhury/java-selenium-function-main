package com.uikit.testing;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Enables UI Kit for a JUnit 5 test class. A fresh driver is created before each test, can be
 * injected as a {@code WebDriver} or {@code UiContext} parameter (in test and lifecycle methods),
 * and is quit afterwards. A screenshot is saved when a test fails.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith(UiKitExtension.class)
public @interface UiKitTest {

    /**
     * Whether to run the browser headless.
     *
     * @return true for headless (default)
     */
    boolean headless() default true;

    /**
     * Browser window width in pixels.
     *
     * @return the width
     */
    int windowWidth() default 1280;

    /**
     * Browser window height in pixels.
     *
     * @return the height
     */
    int windowHeight() default 800;

    /**
     * Wait timeout for the injected {@code UiContext}, in milliseconds.
     *
     * @return the timeout
     */
    long timeoutMillis() default 10_000;

    /**
     * Wait polling interval for the injected {@code UiContext}, in milliseconds.
     *
     * @return the polling interval
     */
    long pollingMillis() default 250;

    /**
     * Whether the injected {@code UiContext} enables the opt-in JavaScript click fallback.
     *
     * @return false by default
     */
    boolean jsClickFallback() default false;

    /**
     * Directory (relative to the working directory) for failure screenshots; empty disables them.
     *
     * @return the directory
     */
    String screenshotDir() default "target/uikit-screenshots";

    /**
     * Factory creating the driver.
     *
     * @return the factory class, which needs a public no-argument constructor
     */
    Class<? extends DriverFactory> factory() default ChromeDriverFactory.class;
}
