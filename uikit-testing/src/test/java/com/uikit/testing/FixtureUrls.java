package com.uikit.testing;

import java.net.URISyntaxException;
import java.nio.file.Paths;

final class FixtureUrls {
    private FixtureUrls() {
    }

    static String of(String name) {
        try {
            return Paths.get(FixtureUrls.class.getResource("/fixtures/" + name).toURI()).toUri().toString();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
