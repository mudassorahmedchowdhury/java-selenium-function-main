package com.uikit.testing;

import com.uikit.core.component.UiContext;
import com.uikit.core.config.WaitConfig;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit 5 extension behind {@link UiKitTest}. Use the annotation rather than registering this
 * class directly.
 */
public final class UiKitExtension
        implements BeforeEachCallback, AfterEachCallback, ParameterResolver, TestExecutionExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(UiKitExtension.class);
    private static final Namespace NS = Namespace.create(UiKitExtension.class);
    private static final String DRIVER = "driver";
    private static final String CONTEXT = "context";

    /** Used when the extension is registered without the annotation. */
    @UiKitTest
    private static final class Defaults {
    }

    /** Creates the extension (invoked by JUnit). */
    public UiKitExtension() {
        // JUnit instantiates extensions reflectively
    }

    @Override
    public void beforeEach(ExtensionContext ec) throws Exception {
        UiKitTest settings = settings(ec);
        DriverFactory factory = settings.factory().getDeclaredConstructor().newInstance();
        WebDriver driver = factory.create(settings);
        UiContext ctx = UiContext.of(driver)
                .withWaitConfig(WaitConfig.of(
                        Duration.ofMillis(settings.timeoutMillis()), Duration.ofMillis(settings.pollingMillis())));
        if (settings.jsClickFallback()) {
            ctx = ctx.withJsClickFallback();
        }
        ExtensionContext.Store store = ec.getStore(NS);
        store.put(DRIVER, driver);
        store.put(CONTEXT, ctx);
    }

    @Override
    public void afterEach(ExtensionContext ec) {
        WebDriver driver = ec.getStore(NS).remove(DRIVER, WebDriver.class);
        ec.getStore(NS).remove(CONTEXT);
        if (driver != null) {
            driver.quit();
        }
    }

    @Override
    public void handleTestExecutionException(ExtensionContext ec, Throwable failure) throws Throwable {
        WebDriver driver = ec.getStore(NS).get(DRIVER, WebDriver.class);
        String dir = settings(ec).screenshotDir();
        if (driver != null && !dir.isEmpty()) {
            try {
                String name = ec.getRequiredTestClass().getSimpleName() + "." + ec.getRequiredTestMethod().getName();
                Path file = Screenshots.save(driver, Paths.get(dir), name);
                LOG.info("Saved failure screenshot: {}", file.toAbsolutePath());
            } catch (Exception e) {
                LOG.warn("Could not save failure screenshot", e);
            }
        }
        throw failure;
    }

    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        Class<?> type = pc.getParameter().getType();
        return type == WebDriver.class || type == UiContext.class;
    }

    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
        ExtensionContext.Store store = ec.getStore(NS);
        Class<?> type = pc.getParameter().getType();
        Object value = type == WebDriver.class ? store.get(DRIVER) : store.get(CONTEXT);
        if (value == null) {
            throw new ParameterResolutionException(
                    "No driver available for " + type.getSimpleName() + "; is the parameter used outside a test?");
        }
        return value;
    }

    private static UiKitTest settings(ExtensionContext ec) {
        return AnnotationSupport.findAnnotation(ec.getRequiredTestClass(), UiKitTest.class)
                .orElseGet(() -> Defaults.class.getAnnotation(UiKitTest.class));
    }
}
