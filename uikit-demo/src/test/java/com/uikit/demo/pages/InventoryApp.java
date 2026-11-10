package com.uikit.demo.pages;

import com.uikit.core.component.ButtonClicker;
import com.uikit.core.component.Sidebar;
import com.uikit.core.component.UiContext;
import com.uikit.core.locator.ButtonLocator;
import com.uikit.core.locator.SidebarLocators;
import com.uikit.core.locator.XPaths;
import org.openqa.selenium.By;

/**
 * Page object for the logged-in demo app. All application-specific locators live here, supplied to
 * the generic components; the library itself knows nothing about this markup.
 */
public final class InventoryApp {
    private static final By MODAL = By.cssSelector("#modal.show");
    private static final By HELP_FRAME = By.id("help");

    private final UiContext ctx;
    private final ButtonClicker buttons;
    private final Sidebar sidebar;

    public InventoryApp(UiContext ctx) {
        this.ctx = ctx;
        this.buttons = new ButtonClicker(ctx, ButtonLocator.exactText());
        this.sidebar = new Sidebar(ctx, SidebarLocators.of(
                name -> By.xpath("//ul[contains(@class,'menu-parent')]/li[span[normalize-space()=" + XPaths.literal(name) + "]]"),
                name -> By.xpath(".//ul/li[normalize-space()=" + XPaths.literal(name) + "]")));
    }

    public InventoryApp open(String parent, String child) {
        sidebar.clickItem(parent, child);
        return this;
    }

    public InventoryApp open(String parent) {
        sidebar.clickItem(parent);
        return this;
    }

    public InventoryApp addItem() {
        buttons.clickButtonByText("Add item");
        buttons.clickButtonInContainer(MODAL, "Save");
        return this;
    }

    /** Clicks Save in the modal without opening it first; fails if no modal is showing. */
    public InventoryApp saveInOpenModal() {
        buttons.clickButtonInContainer(MODAL, "Save");
        return this;
    }

    public InventoryApp cancelAddItem() {
        buttons.clickButtonByText("Add item");
        buttons.clickButtonInContainer(MODAL, "Cancel");
        return this;
    }

    public InventoryApp contactSupport() {
        buttons.clickButtonInFrame(HELP_FRAME, "Contact support");
        return this;
    }

    public String pageTitle() {
        return ctx.waits().until("page title text", d -> {
            String t = d.findElement(By.id("page-title")).getText();
            return t.isEmpty() ? null : t;
        });
    }

    public String status() {
        return ctx.waits().until("status text", d -> {
            String t = d.findElement(By.id("status")).getText();
            return t.isEmpty() ? null : t;
        });
    }
}
