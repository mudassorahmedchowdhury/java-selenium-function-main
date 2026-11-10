package com.uikit.core.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.uikit.core.config.WaitConfig;
import com.uikit.core.exception.ElementNotFoundException;
import com.uikit.core.exception.UiKitException;
import com.uikit.core.exception.UiKitTimeoutException;
import com.uikit.core.support.ChromeFixtureSupport;
import java.time.Duration;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

class FormComponentsIT extends ChromeFixtureSupport {
    private UiContext ctx;

    @BeforeEach
    void load() {
        open("forms.html");
        ctx = UiContext.of(driver).withWaitConfig(WaitConfig.of(Duration.ofMillis(600), Duration.ofMillis(50)));
    }

    @Nested
    class TextInputTests {
        @Test
        void typeReplacesAppendKeepsAndClearEmpties() {
            TextInput name = new TextInput(ctx, By.id("name"));
            assertEquals("initial", name.value());
            assertEquals("Alice", name.type("Alice").value());
            assertEquals("Alice B", name.append(" B").value());
            assertEquals("", name.clear().value());
        }

        @Test
        void worksOnTextarea() {
            assertEquals("line", new TextInput(ctx, By.id("notes")).type("line").value());
        }

        @Test
        void readonlyAndDisabledFieldsTimeOutInsteadOfSilentlyFailing() {
            assertThrows(UiKitTimeoutException.class, () -> new TextInput(ctx, By.id("readonly")).type("x"));
            assertThrows(UiKitTimeoutException.class, () -> new TextInput(ctx, By.id("disabled-text")).type("x"));
        }

        @Test
        void hiddenOrMissingFieldIsNotFound() {
            assertThrows(ElementNotFoundException.class, () -> new TextInput(ctx, By.id("hidden-text")).type("x"));
            assertThrows(ElementNotFoundException.class, () -> new TextInput(ctx, By.id("nope")).value());
        }

        @Test
        void nullTextIsRejected() {
            assertThrows(IllegalArgumentException.class, () -> new TextInput(ctx, By.id("name")).type(null));
        }
    }

    @Nested
    class DropdownTests {
        @Test
        void selectsByTextAndValueAndListsOptions() {
            Dropdown country = new Dropdown(ctx, By.id("country"));
            assertEquals(Arrays.asList("Choose", "Bangladesh", "United States"), country.options());
            assertEquals("Bangladesh", country.selectByText("Bangladesh").selectedText());
            assertEquals("United States", country.selectByValue("us").selectedText());
        }

        @Test
        void unknownOptionThrowsTypedException() {
            Dropdown country = new Dropdown(ctx, By.id("country"));
            assertThrows(ElementNotFoundException.class, () -> country.selectByText("Mars"));
            assertThrows(ElementNotFoundException.class, () -> country.selectByValue("zz"));
        }

        @Test
        void nonSelectAndDisabledSelectAreRejected() {
            assertThrows(UiKitException.class, () -> new Dropdown(ctx, By.id("not-select")).options());
            assertThrows(UiKitTimeoutException.class,
                    () -> new Dropdown(ctx, By.id("disabled-select")).selectByText("One"));
        }
    }

    @Nested
    class CheckboxTests {
        @Test
        void checkAndUncheckAreIdempotent() {
            Checkbox terms = new Checkbox(ctx, By.id("terms"));
            assertFalse(terms.isChecked());
            assertTrue(terms.check().check().isChecked());
            assertFalse(terms.uncheck().uncheck().isChecked());
            assertTrue(terms.set(true).isChecked());
        }

        @Test
        void preCheckedBoxCanBeUnchecked() {
            assertFalse(new Checkbox(ctx, By.id("news")).uncheck().isChecked());
        }

        @Test
        void radioCheckSwitchesGroupButUncheckIsRejected() {
            Checkbox red = new Checkbox(ctx, By.id("red"));
            Checkbox blue = new Checkbox(ctx, By.id("blue"));
            assertTrue(red.check().isChecked());
            assertFalse(blue.isChecked());
            assertThrows(UiKitException.class, red::uncheck);
        }

        @Test
        void disabledBoxTimesOutAndHiddenBoxIsNotFound() {
            assertThrows(UiKitTimeoutException.class, () -> new Checkbox(ctx, By.id("disabled-check")).check());
            assertThrows(ElementNotFoundException.class, () -> new Checkbox(ctx, By.id("hidden-check")).check());
        }
    }
}
