package com.uikit.core.exception;

/** Thrown when an element was found but could not be clicked. */
public class ClickFailedException extends UiKitException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param description description of the element
     * @param cause       the underlying Selenium failure
     */
    public ClickFailedException(String description, Throwable cause) {
        super("Failed to click: " + description + " (" + cause.getClass().getSimpleName()
                + "; enable UiContext.withJsClickFallback() to opt in to JavaScript clicks)", cause);
    }
}
