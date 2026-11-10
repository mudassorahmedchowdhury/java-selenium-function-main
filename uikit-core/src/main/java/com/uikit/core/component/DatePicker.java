package com.uikit.core.component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * A date field that accepts typed text, using a caller-supplied format. Native
 * {@code <input type="date">} controls and calendar pop-ups are not supported because their
 * keyboard handling depends on the browser locale.
 */
public final class DatePicker {
    private final TextInput input;
    private final DateTimeFormatter format;

    /**
     * Creates a date picker with default waits.
     *
     * @param driver the WebDriver
     * @param by     locator of the text field
     * @param format the format the application expects, for example {@code ofPattern("dd/MM/yyyy")}
     */
    public DatePicker(WebDriver driver, By by, DateTimeFormatter format) {
        this(UiContext.of(driver), by, format);
    }

    /**
     * Creates a date picker with an explicit context.
     *
     * @param ctx    shared context (waits)
     * @param by     locator of the text field
     * @param format the format the application expects
     */
    public DatePicker(UiContext ctx, By by, DateTimeFormatter format) {
        this.input = new TextInput(ctx, by);
        this.format = Objects.requireNonNull(format, "format");
    }

    /**
     * Types a date in the configured format.
     *
     * @param date the date, must not be null
     * @return this picker
     */
    public DatePicker setDate(LocalDate date) {
        input.type(format.format(Objects.requireNonNull(date, "date")));
        return this;
    }

    /**
     * Reads the field and parses it with the configured format.
     *
     * @return the date
     * @throws java.time.format.DateTimeParseException if the field is empty or in another format
     */
    public LocalDate getDate() {
        return LocalDate.parse(input.value(), format);
    }
}
