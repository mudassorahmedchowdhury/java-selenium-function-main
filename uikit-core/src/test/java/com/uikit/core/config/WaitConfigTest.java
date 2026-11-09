package com.uikit.core.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class WaitConfigTest {

    @Test
    void defaultsAreCentralized() {
        WaitConfig c = WaitConfig.defaults();
        assertEquals(WaitConfig.DEFAULT_TIMEOUT, c.timeout());
        assertEquals(WaitConfig.DEFAULT_POLLING, c.polling());
    }

    @Test
    void shortTimeoutCapsPolling() {
        WaitConfig c = WaitConfig.of(Duration.ofMillis(100));
        assertEquals(Duration.ofMillis(100), c.polling());
    }

    @Test
    void withersReturnModifiedCopies() {
        WaitConfig c = WaitConfig.defaults().withTimeout(Duration.ofSeconds(3)).withPolling(Duration.ofMillis(50));
        assertEquals(Duration.ofSeconds(3), c.timeout());
        assertEquals(Duration.ofMillis(50), c.polling());
    }

    @Test
    void rejectsInvalidValues() {
        assertThrows(IllegalArgumentException.class, () -> WaitConfig.of(Duration.ZERO));
        assertThrows(IllegalArgumentException.class, () -> WaitConfig.of(Duration.ofSeconds(1), Duration.ofSeconds(2)));
        assertThrows(IllegalArgumentException.class, () -> WaitConfig.of(Duration.ofSeconds(1), Duration.ofSeconds(-1)));
        assertThrows(NullPointerException.class, () -> WaitConfig.of(null));
    }
}
