package com.uikit.core.config;

import java.time.Duration;
import java.util.Objects;

/**
 * Immutable timeout and polling settings used by every UI Kit wait.
 *
 * <p>This is the single place where wait timing is defined; components never hard-code durations.
 */
public final class WaitConfig {
    /** Default timeout: 10 seconds. */
    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
    /** Default polling interval: 250 milliseconds. */
    public static final Duration DEFAULT_POLLING = Duration.ofMillis(250);

    private final Duration timeout;
    private final Duration polling;

    private WaitConfig(Duration timeout, Duration polling) {
        this.timeout = Objects.requireNonNull(timeout, "timeout");
        this.polling = Objects.requireNonNull(polling, "polling");
        if (timeout.isNegative() || timeout.isZero()) {
            throw new IllegalArgumentException("timeout must be positive: " + timeout);
        }
        if (polling.isNegative() || polling.isZero()) {
            throw new IllegalArgumentException("polling must be positive: " + polling);
        }
        if (polling.compareTo(timeout) > 0) {
            throw new IllegalArgumentException("polling must not exceed timeout");
        }
    }

    /**
     * Returns the default configuration.
     *
     * @return 10 s timeout, 250 ms polling
     */
    public static WaitConfig defaults() {
        return new WaitConfig(DEFAULT_TIMEOUT, DEFAULT_POLLING);
    }

    /**
     * Creates a configuration with a custom timeout and the default polling interval
     * (reduced to the timeout if the timeout is shorter).
     *
     * @param timeout maximum wait duration, must be positive
     * @return the configuration
     */
    public static WaitConfig of(Duration timeout) {
        Objects.requireNonNull(timeout, "timeout");
        Duration poll = DEFAULT_POLLING.compareTo(timeout) > 0 ? timeout : DEFAULT_POLLING;
        return new WaitConfig(timeout, poll);
    }

    /**
     * Creates a configuration with explicit timeout and polling interval.
     *
     * @param timeout maximum wait duration, must be positive
     * @param polling interval between condition checks, positive and not above the timeout
     * @return the configuration
     */
    public static WaitConfig of(Duration timeout, Duration polling) {
        return new WaitConfig(timeout, polling);
    }

    /**
     * Returns a copy with a different timeout.
     *
     * @param newTimeout new timeout
     * @return the new configuration
     */
    public WaitConfig withTimeout(Duration newTimeout) {
        return new WaitConfig(newTimeout, polling);
    }

    /**
     * Returns a copy with a different polling interval.
     *
     * @param newPolling new polling interval
     * @return the new configuration
     */
    public WaitConfig withPolling(Duration newPolling) {
        return new WaitConfig(timeout, newPolling);
    }

    /**
     * Maximum wait duration.
     *
     * @return the timeout
     */
    public Duration timeout() {
        return timeout;
    }

    /**
     * Interval between condition checks.
     *
     * @return the polling interval
     */
    public Duration polling() {
        return polling;
    }

    @Override
    public String toString() {
        return "WaitConfig{timeout=" + timeout + ", polling=" + polling + '}';
    }
}
