package com.uikit.core.component;

import com.uikit.core.locator.SidebarLocators;
import java.util.Objects;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Navigates a two-level sidebar described by caller-supplied {@link SidebarLocators}. Unlike the
 * legacy implementation, a missing item throws a typed exception instead of being logged.
 */
public final class Sidebar {
    private final UiContext ctx;
    private final SidebarLocators locators;

    /**
     * Creates a sidebar with default waits and native clicks.
     *
     * @param driver   the WebDriver
     * @param locators the sidebar markup description
     */
    public Sidebar(WebDriver driver, SidebarLocators locators) {
        this(UiContext.of(driver), locators);
    }

    /**
     * Creates a sidebar with an explicit context.
     *
     * @param ctx      shared context (waits, click policy)
     * @param locators the sidebar markup description
     */
    public Sidebar(UiContext ctx, SidebarLocators locators) {
        this.ctx = Objects.requireNonNull(ctx, "ctx");
        this.locators = Objects.requireNonNull(locators, "locators");
    }

    /**
     * Clicks a top-level item.
     *
     * @param parentName parent item name
     * @return this sidebar
     * @throws com.uikit.core.exception.ElementNotFoundException if the item is missing
     */
    public Sidebar clickItem(String parentName) {
        parentElement(parentName);
        return this;
    }

    /**
     * Clicks a parent item, then a child item found within that parent.
     *
     * @param parentName parent item name
     * @param childName  child item name
     * @return this sidebar
     * @throws com.uikit.core.exception.ElementNotFoundException if either item is missing
     */
    public Sidebar clickItem(String parentName, String childName) {
        WebElement parent = parentElement(parentName);
        String desc = "sidebar child '" + childName + "' under '" + parentName + "'";
        ctx.click(ctx.findUnique(parent, locators.child(childName), desc), desc);
        return this;
    }

    private WebElement parentElement(String parentName) {
        String desc = "sidebar parent '" + parentName + "'";
        WebElement parent = ctx.findUnique(ctx.driver(), locators.parent(parentName), desc);
        ctx.click(parent, desc);
        return parent;
    }
}
