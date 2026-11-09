package com.uikit.core.exception;

/** Base type for every unchecked exception thrown by UI Kit. */
public class UiKitException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a message.
     *
     * @param message description of the failure
     */
    public UiKitException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a message and cause.
     *
     * @param message description of the failure
     * @param cause   underlying Selenium or runtime failure
     */
    public UiKitException(String message, Throwable cause) {
        super(message, cause);
    }
}
