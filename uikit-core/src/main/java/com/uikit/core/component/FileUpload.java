package com.uikit.core.component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * An {@code <input type="file">} identified by a caller-supplied locator. The input may be visually
 * hidden (common with styled upload buttons); it only has to be present in the DOM.
 */
public final class FileUpload {
    private final UiContext ctx;
    private final By by;

    /**
     * Creates a file upload with default waits.
     *
     * @param driver the WebDriver
     * @param by     locator of the file input
     */
    public FileUpload(WebDriver driver, By by) {
        this(UiContext.of(driver), by);
    }

    /**
     * Creates a file upload with an explicit context.
     *
     * @param ctx shared context (waits)
     * @param by  locator of the file input
     */
    public FileUpload(UiContext ctx, By by) {
        this.ctx = Objects.requireNonNull(ctx, "ctx");
        this.by = Objects.requireNonNull(by, "by");
    }

    /**
     * Selects a local file for upload.
     *
     * @param file path of an existing regular file
     * @return this upload
     * @throws IllegalArgumentException                       if the file does not exist
     * @throws com.uikit.core.exception.UiKitTimeoutException if the input is not present in time
     */
    public FileUpload upload(Path file) {
        Objects.requireNonNull(file, "file");
        if (!Files.isRegularFile(file)) {
            throw new IllegalArgumentException("File does not exist: " + file);
        }
        ctx.waits().present(by).sendKeys(file.toAbsolutePath().toString());
        return this;
    }
}
