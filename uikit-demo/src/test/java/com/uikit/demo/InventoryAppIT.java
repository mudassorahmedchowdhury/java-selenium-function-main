package com.uikit.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.uikit.core.component.UiContext;
import com.uikit.core.exception.ElementNotFoundException;
import com.uikit.demo.pages.InventoryApp;
import com.uikit.demo.pages.LoginPage;
import com.uikit.testing.UiKitTest;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

/** End-to-end example: a consumer test using page objects over uikit-core, run by uikit-testing. */
@UiKitTest(timeoutMillis = 3000, pollingMillis = 50)
class InventoryAppIT {

    @BeforeEach
    void openApp(WebDriver driver) throws URISyntaxException {
        driver.get(Paths.get(getClass().getResource("/app/index.html").toURI()).toUri().toString());
    }

    private InventoryApp loggedIn(UiContext ctx) {
        new LoginPage(ctx).login("admin", "secret");
        return new InventoryApp(ctx);
    }

    @Test
    void invalidLoginShowsError(UiContext ctx) {
        assertEquals("Invalid credentials", new LoginPage(ctx).login("admin", "wrong").errorMessage());
    }

    @Test
    void sidebarNavigationReachesChildPage(UiContext ctx) {
        InventoryApp app = loggedIn(ctx);
        assertEquals("Dashboard page", app.pageTitle());
        app.open("Inventory", "Items");
        assertEquals("Items page", app.pageTitle());
    }

    @Test
    void modalButtonsAreScopedToTheOpenModal(UiContext ctx) {
        InventoryApp app = loggedIn(ctx).addItem();
        assertEquals("Item saved", app.status());
    }

    @Test
    void iframeButtonWorks(UiContext ctx) {
        assertEquals("Support contacted", loggedIn(ctx).contactSupport().status());
    }

    @Test
    void closedModalCannotBeUsed(UiContext ctx) {
        InventoryApp app = loggedIn(ctx).cancelAddItem();
        assertThrows(ElementNotFoundException.class, app::saveInOpenModal);
    }
}
