package com.uikit.testing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/** Saves browser screenshots to disk. */
public final class Screenshots {
    private Screenshots() {
    }

    /**
     * Saves a PNG screenshot.
     *
     * @param driver the driver; must support {@link TakesScreenshot}
     * @param dir    target directory, created if missing
     * @param name   base file name; unsafe characters are replaced with underscores
     * @return the written file
     * @throws IOException if the file cannot be written
     */
    public static Path save(WebDriver driver, Path dir, String name) throws IOException {
        Objects.requireNonNull(driver, "driver");
        Objects.requireNonNull(dir, "dir");
        if (!(driver instanceof TakesScreenshot)) {
            throw new IllegalArgumentException("Driver does not support screenshots: " + driver.getClass());
        }
        byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Files.createDirectories(dir);
        Path file = dir.resolve(sanitize(name) + ".png");
        Files.write(file, png);
        return file;
    }

    /**
     * Replaces every character outside {@code [A-Za-z0-9._-]} with an underscore.
     *
     * @param name raw name
     * @return a file-system-safe name
     */
    static String sanitize(String name) {
        return name.replaceAll("[^A-Za-z0-9._-]", "_");
    }
}
