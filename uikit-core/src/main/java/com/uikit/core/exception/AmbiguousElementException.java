package com.uikit.core.exception;

/** Thrown when a locator is expected to match exactly one element but matches several. */
public class AmbiguousElementException extends UiKitException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param description human-readable description of the locator
     * @param count       number of matching elements
     */
    public AmbiguousElementException(String description, int count) {
        super("Expected exactly one element but found " + count + ": " + description);
    }
}
