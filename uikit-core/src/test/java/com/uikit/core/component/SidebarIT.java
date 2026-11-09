package com.uikit.core.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.uikit.core.config.WaitConfig;
import com.uikit.core.exception.ElementNotFoundException;
import com.uikit.core.locator.SidebarLocators;
import com.uikit.core.locator.XPaths;
import com.uikit.core.support.ChromeFixtureSupport;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

class SidebarIT extends ChromeFixtureSupport {
    private Sidebar sidebar;

    @BeforeEach
    void load() {
        open("sidebar.html");
        SidebarLocators locators = SidebarLocators.of(
                name -> By.xpath("//li[contains(@class,'nav-parent')][span[normalize-space()=" + XPaths.literal(name) + "]]"),
                name -> By.xpath(".//li[contains(@class,'nav-child')][normalize-space()=" + XPaths.literal(name) + "]"));
        UiContext ctx = UiContext.of(driver).withWaitConfig(WaitConfig.of(Duration.ofMillis(800), Duration.ofMillis(50)));
        sidebar = new Sidebar(ctx, locators);
    }

    private String result() {
        return driver.findElement(By.id("result")).getText();
    }

    @Test
    void clicksParentOnly() {
        sidebar.clickItem("Dashboard");
        assertEquals("parent:Dashboard", result());
    }

    @Test
    void expandsParentThenClicksChild() {
        sidebar.clickItem("Inventory", "Items");
        assertEquals("child:Items", result());
    }

    @Test
    void missingItemsThrowInsteadOfSilentlyPassing() {
        assertThrows(ElementNotFoundException.class, () -> sidebar.clickItem("Nope"));
        assertThrows(ElementNotFoundException.class, () -> sidebar.clickItem("Dashboard", "Items"));
    }
}
