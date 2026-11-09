package com.uikit.core.exception;

import java.time.Duration;

/** Thrown when a centralized wait condition is not met within the configured timeout. */
public class UiKitTimeoutException extends UiKitException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param condition description of the awaited condition
     * @param timeout   the timeout that elapsed
     * @param cause     the underlying Selenium timeout
     */
    public UiKitTimeoutException(String condition, Duration timeout, Throwable cause) {
        super("Timed out after " + timeout.toMillis() + " ms waiting for: " + condition, cause);
    }
}
