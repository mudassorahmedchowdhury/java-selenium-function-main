package com.uikit.core.component;

import com.uikit.core.exception.ElementNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Reads a standard HTML {@code <table>} identified by a caller-supplied locator. Header cells are
 * {@code <th>}; data rows are {@code <tr>} elements containing {@code <td>}. Row and column indexes
 * are zero-based.
 */
public final class Table {
    private final UiContext ctx;
    private final By by;
    private final String description;

    /**
     * Creates a table with default waits.
     *
     * @param driver the WebDriver
     * @param by     locator of the table
     */
    public Table(WebDriver driver, By by) {
        this(UiContext.of(driver), by);
    }

    /**
     * Creates a table with an explicit context.
     *
     * @param ctx shared context (waits)
     * @param by  locator of the table
     */
    public Table(UiContext ctx, By by) {
        this.ctx = Objects.requireNonNull(ctx, "ctx");
        this.by = Objects.requireNonNull(by, "by");
        this.description = "table [" + by + "]";
    }

    /**
     * Returns the header texts of the first row that contains {@code <th>} cells.
     *
     * @return the headers, empty if the table has none
     * @throws ElementNotFoundException if the table is not displayed
     */
    public List<String> headers() {
        return texts(root().findElements(By.xpath(".//tr[th][1]/th")));
    }

    /**
     * Counts data rows.
     *
     * @return the number of rows containing {@code <td>} cells
     * @throws ElementNotFoundException if the table is not displayed
     */
    public int rowCount() {
        return dataRows().size();
    }

    /**
     * Returns the text of every data row.
     *
     * @return rows as lists of cell texts
     * @throws ElementNotFoundException if the table is not displayed
     */
    public List<List<String>> rows() {
        List<List<String>> out = new ArrayList<>();
        for (WebElement row : dataRows()) {
            out.add(texts(row.findElements(By.xpath("./td"))));
        }
        return out;
    }

    /**
     * Returns the cell element at a row and column, e.g. to click a link inside it.
     *
     * @param row    zero-based data row
     * @param column zero-based column
     * @return the cell
     * @throws ElementNotFoundException if the row or column does not exist
     */
    public WebElement cell(int row, int column) {
        List<WebElement> rows = dataRows();
        if (row < 0 || row >= rows.size()) {
            throw new ElementNotFoundException("row " + row + " of " + rows.size() + " in " + description);
        }
        List<WebElement> cells = rows.get(row).findElements(By.xpath("./td"));
        if (column < 0 || column >= cells.size()) {
            throw new ElementNotFoundException(
                    "column " + column + " of " + cells.size() + " in row " + row + " of " + description);
        }
        return cells.get(column);
    }

    /**
     * Returns the cell element at a row under the named header.
     *
     * @param row    zero-based data row
     * @param header header text (whitespace-normalized, exact match)
     * @return the cell
     * @throws ElementNotFoundException if the header or row does not exist
     */
    public WebElement cell(int row, String header) {
        return cell(row, columnIndex(header));
    }

    /**
     * Returns the text of a cell.
     *
     * @param row    zero-based data row
     * @param header header text
     * @return the cell text
     * @throws ElementNotFoundException if the header or row does not exist
     */
    public String cellText(int row, String header) {
        return cell(row, header).getText();
    }

    /**
     * Finds the first data row whose cell under {@code header} equals {@code text}.
     *
     * @param header header text
     * @param text   expected cell text (exact, after trimming)
     * @return the zero-based row index
     * @throws ElementNotFoundException if the header does not exist or no row matches
     */
    public int findRow(String header, String text) {
        Objects.requireNonNull(text, "text");
        int column = columnIndex(header);
        List<WebElement> rows = dataRows();
        for (int i = 0; i < rows.size(); i++) {
            List<WebElement> cells = rows.get(i).findElements(By.xpath("./td"));
            if (column < cells.size() && cells.get(column).getText().trim().equals(text.trim())) {
                return i;
            }
        }
        throw new ElementNotFoundException("row with '" + text + "' under '" + header + "' in " + description);
    }

    private int columnIndex(String header) {
        Objects.requireNonNull(header, "header");
        List<String> headers = headers();
        String wanted = header.trim().replaceAll("\\s+", " ");
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).replaceAll("\\s+", " ").equals(wanted)) {
                return i;
            }
        }
        throw new ElementNotFoundException("header '" + header + "' among " + headers + " in " + description);
    }

    private List<WebElement> dataRows() {
        return root().findElements(By.xpath(".//tr[td]"));
    }

    private WebElement root() {
        return ctx.findUnique(ctx.driver(), by, description);
    }

    private static List<String> texts(List<WebElement> elements) {
        return elements.stream().map(e -> e.getText().trim()).collect(Collectors.toList());
    }
}
