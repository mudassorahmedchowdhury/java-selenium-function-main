package com.uikit.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import com.uikit.core.component.ButtonClicker;
import com.uikit.core.component.UiContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

@UiKitTest
@TestMethodOrder(MethodOrderer.MethodName.class)
class UiKitExtensionIT {
    private static WebDriver firstDriver;
    private static final String SHOT_DIR = "target/uikit-test-shots";

    private WebDriver injectedInBeforeEach;

    @BeforeEach
    void captureLifecycleInjection(WebDriver driver) {
        injectedInBeforeEach = driver;
    }

    @Test
    void a_injectsWorkingDriverAndContextIntoLifecycleAndTest(WebDriver driver, UiContext ctx) {
        assertSame(driver, injectedInBeforeEach);
        assertSame(driver, ctx.driver());
        driver.get(FixtureUrls.of("page.html"));
        new ButtonClicker(ctx, com.uikit.core.locator.ButtonLocator.exactText()).clickButtonByText("Go");
        assertEquals("clicked", driver.findElement(By.id("result")).getText());
        firstDriver = driver;
    }

    @Test
    void b_previousDriverWasQuitAndNewTestGetsFreshDriver(WebDriver driver) {
        assertNotNull(firstDriver, "test a must run first");
        assertNotSame(firstDriver, driver);
        assertThrows(RuntimeException.class, firstDriver::getTitle);
        assertTrue(((RemoteWebDriver) driver).getSessionId() != null);
    }

    @Test
    void screenshotsSavePngToDirectory(WebDriver driver) throws IOException {
        driver.get(FixtureUrls.of("page.html"));
        Path dir = Paths.get("target/uikit-test-shots-direct");
        Path file = Screenshots.save(driver, dir, "my test/name");
        assertTrue(Files.size(file) > 0);
        assertEquals("my_test_name.png", file.getFileName().toString());
    }

    @Test
    void failingTestProducesScreenshotAndStillFails() throws IOException {
        deleteDir(Paths.get(SHOT_DIR));
        System.setProperty("uikit.runFailingFixture", "true");
        try {
            Launcher launcher = LauncherFactory.create();
            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(selectClass(FailingFixture.class))
                    .build();
            SummaryGeneratingListener listener = new SummaryGeneratingListener();
            launcher.execute(request, listener);
            assertEquals(1, listener.getSummary().getTestsFailedCount());
        } finally {
            System.clearProperty("uikit.runFailingFixture");
        }
        try (Stream<Path> files = Files.list(Paths.get(SHOT_DIR))) {
            assertTrue(files.anyMatch(p -> p.getFileName().toString().equals("FailingFixture.fails.png")));
        }
    }

    private static void deleteDir(Path dir) throws IOException {
        if (Files.exists(dir)) {
            try (Stream<Path> files = Files.list(dir)) {
                for (Path p : (Iterable<Path>) files::iterator) {
                    Files.delete(p);
                }
            }
        }
    }

    /** Only executed through the launcher above, never by the normal build. */
    @UiKitTest(screenshotDir = SHOT_DIR)
    @EnabledIfSystemProperty(named = "uikit.runFailingFixture", matches = "true")
    static class FailingFixture {
        @Test
        void fails(WebDriver driver) {
            driver.get(FixtureUrls.of("page.html"));
            throw new AssertionError("expected failure");
        }
    }
}
