package com.uikit.core.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.uikit.core.config.WaitConfig;
import com.uikit.core.exception.ElementNotFoundException;
import com.uikit.core.exception.UiKitTimeoutException;
import com.uikit.core.locator.XPaths;
import com.uikit.core.support.ChromeFixtureSupport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openqa.selenium.By;

class WidgetComponentsIT extends ChromeFixtureSupport {
    private UiContext ctx;

    @BeforeEach
    void load() {
        open("widgets.html");
        ctx = UiContext.of(driver).withWaitConfig(WaitConfig.of(Duration.ofMillis(700), Duration.ofMillis(50)));
    }

    private String result() {
        return driver.findElement(By.id("result")).getText();
    }

    private void click(String id) {
        driver.findElement(By.id(id)).click();
    }

    @Nested
    class AlertsTests {
        @Test
        void readsTextAndAccepts() {
            Alerts alerts = new Alerts(ctx);
            assertFalse(alerts.isPresent());
            click("b-alert");
            assertTrue(alerts.isPresent());
            assertEquals("Hello alert", alerts.text());
            alerts.accept();
            assertFalse(alerts.isPresent());
        }

        @Test
        void confirmCanBeAcceptedOrDismissed() {
            click("b-confirm");
            new Alerts(ctx).accept();
            assertEquals("confirmed", result());
            click("b-confirm");
            new Alerts(ctx).dismiss();
            assertEquals("cancelled", result());
        }

        @Test
        void promptTakesText() {
            click("b-prompt");
            new Alerts(ctx).typeAndAccept("Zed");
            assertEquals("prompt:Zed", result());
        }

        @Test
        void missingDialogTimesOut() {
            assertThrows(UiKitTimeoutException.class, () -> new Alerts(ctx).accept());
        }
    }

    @Nested
    class WindowsTests {
        @Test
        void switchesToNewWindowAndBack() {
            Windows windows = new Windows(ctx);
            String opener = windows.current();
            windows.switchToNewWindow(() -> click("b-window"));
            assertEquals(2, windows.count());
            assertEquals("Child window", driver.getTitle());
            windows.closeCurrentAndSwitchTo(opener);
            assertEquals(1, windows.count());
            assertEquals("Widgets fixture", driver.getTitle());
        }

        @Test
        void switchesByTitleAndRestoresOriginalWhenMissing() {
            Windows windows = new Windows(ctx);
            String opener = windows.current();
            windows.switchToNewWindow(() -> click("b-window"));
            windows.switchToTitle("Widgets fixture");
            assertEquals(opener, windows.current());
            assertThrows(UiKitTimeoutException.class, () -> windows.switchToTitle("No such window"));
            assertEquals(opener, windows.current());
        }

        @Test
        void noNewWindowTimesOut() {
            assertThrows(UiKitTimeoutException.class, () -> new Windows(ctx).switchToNewWindow(() -> { }));
        }
    }

    @Nested
    class FileUploadTests {
        @Test
        void uploadsToVisibleAndHiddenInputs(@TempDir Path dir) throws IOException {
            Path file = Files.write(dir.resolve("report.txt"), "data".getBytes());
            new FileUpload(ctx, By.id("file")).upload(file);
            assertEquals("file:report.txt", result());
            new FileUpload(ctx, By.id("file-hidden")).upload(file);
            assertEquals("hidden-file:report.txt", result());
        }

        @Test
        void missingFileAndMissingInputAreRejected(@TempDir Path dir) throws IOException {
            assertThrows(IllegalArgumentException.class,
                    () -> new FileUpload(ctx, By.id("file")).upload(dir.resolve("nope.txt")));
            Path file = Files.write(dir.resolve("a.txt"), "x".getBytes());
            assertThrows(UiKitTimeoutException.class, () -> new FileUpload(ctx, By.id("nope")).upload(file));
        }
    }

    @Nested
    class TableTests {
        private final By people = By.id("people");

        @Test
        void readsHeadersRowsAndCells() {
            Table t = new Table(ctx, people);
            assertEquals(Arrays.asList("Name", "Role Title", "Action"), t.headers().stream()
                    .map(h -> h.replaceAll("\\s+", " ")).collect(java.util.stream.Collectors.toList()));
            assertEquals(2, t.rowCount());
            List<List<String>> rows = t.rows();
            assertEquals(Arrays.asList("Bob", "Tester", "Edit"), rows.get(1));
            assertEquals("Engineer", t.cellText(0, "Role Title"));
        }

        @Test
        void findsRowByValueAndClicksElementInCell() {
            Table t = new Table(ctx, people);
            int row = t.findRow("Name", "Bob");
            assertEquals(1, row);
            t.cell(row, "Action").findElement(By.tagName("a")).click();
            assertEquals("edit:Bob", result());
        }

        @Test
        void unknownHeaderRowOrColumnThrowTypedExceptions() {
            Table t = new Table(ctx, people);
            assertThrows(ElementNotFoundException.class, () -> t.cellText(0, "Salary"));
            assertThrows(ElementNotFoundException.class, () -> t.cell(5, 0));
            assertThrows(ElementNotFoundException.class, () -> t.cell(0, 9));
            assertThrows(ElementNotFoundException.class, () -> t.findRow("Name", "Zed"));
        }

        @Test
        void emptyTableHasNoRows() {
            Table t = new Table(ctx, By.id("empty"));
            assertEquals(0, t.rowCount());
            assertEquals(List.of("Only"), t.headers());
        }
    }

    @Nested
    class DatePickerTests {
        private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        @Test
        void typesAndParsesConfiguredFormat() {
            DatePicker dob = new DatePicker(ctx, By.id("dob"), fmt);
            dob.setDate(LocalDate.of(1999, 12, 31));
            assertEquals("31/12/1999", driver.findElement(By.id("dob")).getAttribute("value"));
            assertEquals(LocalDate.of(1999, 12, 31), dob.getDate());
        }

        @Test
        void emptyFieldFailsToParse() {
            assertThrows(DateTimeParseException.class, () -> new DatePicker(ctx, By.id("dob"), fmt).getDate());
        }
    }

    @Nested
    class CustomDropdownTests {
        private CustomDropdown fruits() {
            return new CustomDropdown(ctx, By.id("dd-trigger"),
                    text -> By.xpath("//li[@class='opt'][normalize-space()=" + XPaths.literal(text) + "]"));
        }

        @Test
        void opensListAndPicksOption() {
            fruits().select("Banana");
            assertEquals("fruit:Banana", result());
        }

        @Test
        void unknownOptionIsNotFoundAndBlankIsRejected() {
            assertThrows(ElementNotFoundException.class, () -> fruits().select("Mango"));
            assertThrows(IllegalArgumentException.class, () -> fruits().select(" "));
        }
    }
}
