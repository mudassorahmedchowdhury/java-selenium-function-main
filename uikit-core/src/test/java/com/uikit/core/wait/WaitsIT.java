package com.uikit.core.wait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.uikit.core.config.WaitConfig;
import com.uikit.core.exception.UiKitTimeoutException;
import com.uikit.core.support.ChromeFixtureSupport;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

class WaitsIT extends ChromeFixtureSupport {

    private Waits waits(long timeoutMs) {
        return new Waits(driver, WaitConfig.of(Duration.ofMillis(timeoutMs), Duration.ofMillis(50)));
    }

    @Test
    void presentAndVisibleFindExistingElement() {
        open("waits.html");
        assertEquals("Present", waits(2000).visible(By.id("present")).getText());
        assertTrue(waits(2000).present(By.id("hidden")) != null);
    }

    @Test
    void visibleWaitsForDelayedReveal() {
        open("waits.html");
        driver.findElement(By.id("reveal")).click();
        assertEquals("Hidden", waits(3000).visible(By.id("hidden")).getText());
    }

    @Test
    void invisibleWaitsForDelayedHide() {
        open("waits.html");
        driver.findElement(By.id("hide")).click();
        waits(3000).invisible(By.id("vanishing"));
    }

    @Test
    void clickableTimesOutOnDisabledElementWithTypedException() {
        open("waits.html");
        UiKitTimeoutException ex =
                assertThrows(UiKitTimeoutException.class, () -> waits(500).clickable(By.id("disabled")));
        assertTrue(ex.getMessage().contains("clickability"));
    }

    @Test
    void missingElementTimesOutWithTypedException() {
        open("waits.html");
        assertThrows(UiKitTimeoutException.class, () -> waits(500).present(By.id("nope")));
    }
}
