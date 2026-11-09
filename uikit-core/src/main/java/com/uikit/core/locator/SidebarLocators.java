package com.uikit.core.locator;

import java.util.Objects;
import java.util.function.Function;
import org.openqa.selenium.By;

/**
 * Locators describing a sidebar's markup. There are no defaults because sidebar markup is
 * application specific; you provide the locators.
 */
public final class SidebarLocators {
    private final Function<String, By> parent;
    private final Function<String, By> child;

    private SidebarLocators(Function<String, By> parent, Function<String, By> child) {
        this.parent = Objects.requireNonNull(parent, "parent");
        this.child = Objects.requireNonNull(child, "child");
    }

    /**
     * Creates sidebar locators.
     *
     * @param parent maps a parent item name to a locator searched on the whole page
     * @param child  maps a child item name to a locator searched <em>within the clicked parent
     *               element</em>, so XPath expressions should start with {@code .//}
     * @return the locators
     */
    public static SidebarLocators of(Function<String, By> parent, Function<String, By> child) {
        return new SidebarLocators(parent, child);
    }

    /**
     * Locator for a parent item.
     *
     * @param name parent item name
     * @return the locator
     */
    public By parent(String name) {
        return parent.apply(requireName(name));
    }

    /**
     * Locator for a child item, relative to its parent element.
     *
     * @param name child item name
     * @return the locator
     */
    public By child(String name) {
        return child.apply(requireName(name));
    }

    private static String requireName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be null or blank");
        }
        return name;
    }
}
