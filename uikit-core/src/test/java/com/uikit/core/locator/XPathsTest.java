package com.uikit.core.locator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class XPathsTest {
    @Test
    void literalPicksSafeQuoting() {
        assertEquals("'abc'", XPaths.literal("abc"));
        assertEquals("\"it's\"", XPaths.literal("it's"));
        assertTrue(XPaths.literal("a'b\"c").startsWith("concat("));
    }
}
