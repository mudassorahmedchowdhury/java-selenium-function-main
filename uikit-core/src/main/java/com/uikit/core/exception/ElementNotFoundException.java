package com.uikit.core.exception;

/** Thrown when no element matching a locator is found. */
public class ElementNotFoundException extends UiKitException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param description human-readable description of what was searched for
     */
    public ElementNotFoundException(String description) {
        super("Element not found: " + description);
    }

    /**
     * Creates the exception with a cause.
     *
     * @param description human-readable description of what was searched for
     * @param cause       underlying failure
     */
    public ElementNotFoundException(String description, Throwable cause) {
        super("Element not found: " + description, cause);
    }
}
