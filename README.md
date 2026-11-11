# Java Selenium UI Kit

[![Java CI with Maven](https://github.com/mudassorahmedchowdhury/java-selenium-function-main/actions/workflows/maven.yml/badge.svg)](https://github.com/mudassorahmedchowdhury/java-selenium-function-main/actions/workflows/maven.yml)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)
[![](https://jitpack.io/v/mudassorahmedchowdhury/java-selenium-function-main.svg)](https://jitpack.io/#mudassorahmedchowdhury/java-selenium-function-main)

A reusable Java library of Selenium UI interactions with configurable locators, centralized waits and typed exceptions. It is a plain Maven library: no frontend, REST API, backend or database.

> **Status:** Phases 1 (foundation), 2 (`ButtonClicker`, `Sidebar`), 3 (JUnit 5 integration), 4 (demo), 5 (install verification) 6 (form components) and 7 (alerts, windows, upload, table, date, custom dropdown) are complete.

## Requirements

- Java 17+, Maven 3.9+
- Google Chrome (integration tests run headless Chrome; Selenium Manager fetches the driver)

## Build

```
mvn clean verify
```

Unit tests (`*Test`) run in `surefire`; fixture-based browser tests (`*IT`) run in `failsafe`. Javadoc is checked in `verify` and fails on warnings.

## Install into your own project

The artifacts are not on Maven Central. Use them through [JitPack](https://jitpack.io/#mudassorahmedchowdhury/java-selenium-function-main), which builds the tagged release straight from GitHub:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.mudassorahmedchowdhury.java-selenium-function-main</groupId>
    <artifactId>uikit-core</artifactId>
    <version>v0.1.1</version>
</dependency>
```

Or install it into your local repository yourself and use the `io.github.mudassorahmedchowdhury` coordinates:

```
git clone https://github.com/mudassorahmedchowdhury/java-selenium-function-main.git
cd java-selenium-function-main
mvn clean install        # or: mvn install -DskipTests
```

```xml
<dependency>
    <groupId>io.github.mudassorahmedchowdhury</groupId>
    <artifactId>uikit-core</artifactId>
    <version>0.2.0</version>
</dependency>
```

This was verified with a separate Maven project that depends only on `uikit-core` (plus Selenium's Chrome driver and JUnit for its test) and clicks a button via `new ButtonClicker(driver).clickButtonByText("Go")`.
`uikit-core` brings Selenium API and support transitively; add `selenium-chrome-driver` (or another driver) yourself. SLF4J output needs a binding such as `slf4j-simple`.

## Modules

| Module | Status |
|---|---|
| `uikit-core` | Waits, typed exceptions, `ButtonClicker`, `Sidebar`, `TextInput`, `Dropdown`, `Checkbox`, `Alerts`, `Windows`, `FileUpload`, `Table`, `DatePicker`, `CustomDropdown` |
| `uikit-testing` | Phase 3: `@UiKitTest` JUnit 5 extension |
| `uikit-demo` | Phase 4: page objects and example tests |

## Dependency scopes (`uikit-core`)

| Dependency | Scope | Why |
|---|---|---|
| `selenium-api`, `selenium-support` | compile | Selenium types are part of the public API |
| `slf4j-api` | compile | Logging facade only; consumers pick the binding |
| `selenium-java`, `junit-jupiter`, `slf4j-simple` | test | Headless Chrome tests only |

## Usage (Phase 1)

```java
Waits waits = new Waits(driver, WaitConfig.of(Duration.ofSeconds(5), Duration.ofMillis(100)));
WebElement el = waits.clickable(By.id("submit"));   // locator is always supplied by you
```

Failures throw `UiKitTimeoutException` (a `UiKitException`). Also available: `ElementNotFoundException`, `AmbiguousElementException`.

## Components (Phase 2)

All components share a `UiContext` (driver + centralized `Waits` + click policy). Clicks are native Selenium by default; JavaScript fallback is **opt-in** via `withJsClickFallback()`.

```java
UiContext ctx = UiContext.of(driver)
        .withWaitConfig(WaitConfig.of(Duration.ofSeconds(5)));   // optional
        // .withJsClickFallback();                               // optional, opt-in

ButtonClicker buttons = new ButtonClicker(ctx, ButtonLocator.exactText());
buttons.clickButtonByText("Save")
       .clickButtonInContainer(By.cssSelector(".modal.show"), "Confirm")   // you supply the container
       .clickButtonInFrame(By.id("payment-frame"), "Pay");

SidebarLocators nav = SidebarLocators.of(
        name -> By.xpath("//li[span[normalize-space()=" + XPaths.literal(name) + "]]"),   // parent, page-wide
        name -> By.xpath(".//li[normalize-space()=" + XPaths.literal(name) + "]"));       // child, within parent
new Sidebar(ctx, nav).clickItem("Inventory", "Items");
```

- `ButtonLocator`: `exactText()` (buttons, `input[type=button|submit|reset]`, `role=button`), `exactTextIgnoreCase()`, or `ButtonLocator.of(label -> By...)` for your own markup.
- Elements must be displayed; no match throws `ElementNotFoundException`, several matches throw `AmbiguousElementException`, a failed native click throws `ClickFailedException`.
- `XPaths.literal(...)` safely quotes text for XPath.

## Form components (Phase 6)

Each is bound to a locator you supply and re-resolves the element on every call.

```java
new TextInput(ctx, By.id("email")).type("a@b.com").append(".uk");     // value() reads it back
new Dropdown(ctx, By.id("country")).selectByText("Bangladesh");       // native <select> only
new Checkbox(ctx, By.id("terms")).check();                            // idempotent; also radios
```

- `TextInput`: `type` (replace), `append`, `clear`, `value`. Works on inputs and textareas; disabled or read-only fields throw `UiKitTimeoutException` rather than failing silently.
- `Dropdown`: `selectByText`, `selectByValue`, `selectedText`, `options`. Unknown options throw `ElementNotFoundException`; a non-`<select>` element throws `UiKitException`. Custom dropdown widgets are not supported.
- `Checkbox`: `check`, `uncheck`, `set`, `isChecked`. Only clicks when the state differs; unchecking a radio button throws `UiKitException`.
- Hidden elements are treated as not found, like all other components.

## More components (Phase 7)

```java
new Alerts(ctx).accept();                                   // also text(), dismiss(), typeAndAccept("x"), isPresent()
new Windows(ctx).switchToNewWindow(() -> buttons.clickButtonByText("Open"));   // also switchToTitle, closeCurrentAndSwitchTo
new FileUpload(ctx, By.cssSelector("input[type=file]")).upload(Path.of("report.pdf"));   // hidden inputs are fine

Table users = new Table(ctx, By.id("users"));
int row = users.findRow("Name", "Bob");
users.cell(row, "Action").findElement(By.tagName("a")).click();   // also headers(), rowCount(), rows(), cellText(row, header)

new DatePicker(ctx, By.id("dob"), DateTimeFormatter.ofPattern("dd/MM/yyyy")).setDate(LocalDate.of(1999, 12, 31));

new CustomDropdown(ctx, By.id("fruit-trigger"),
        text -> By.xpath("//li[normalize-space()=" + XPaths.literal(text) + "]")).select("Banana");
```

- `Alerts`, `Windows`: a missing dialog or window throws `UiKitTimeoutException`; `switchToTitle` returns to the original window if no title matches.
- `FileUpload`: a missing local file throws `IllegalArgumentException` before touching the browser.
- `Table`: standard `<table>` markup only (`<th>` headers, `<tr>` containing `<td>` as data rows), zero-based indexes. Grid widgets built from `div`s are not supported.
- `DatePicker`: text fields with a format you supply. Native `<input type="date">` and calendar pop-ups are **not** supported (their keyboard input is locale dependent); drive a pop-up with `CustomDropdown`, `ButtonClicker` or your own locators.
- `CustomDropdown`: you describe the markup (trigger locator plus option-by-text); use `Dropdown` for native `<select>`.

## JUnit 5 integration (Phase 3, `uikit-testing`, optional)

```xml
<dependency>
    <groupId>io.github.mudassorahmedchowdhury</groupId>
    <artifactId>uikit-testing</artifactId>
    <version>0.2.0</version>
    <scope>test</scope>
</dependency>
```

```java
@UiKitTest                                   // headless Chrome per test, quit afterwards
class CheckoutTest {
    @Test
    void saves(WebDriver driver, UiContext ctx) {   // injectable in tests and @BeforeEach
        driver.get("file:///.../page.html");
        new ButtonClicker(ctx, ButtonLocator.exactText()).clickButtonByText("Save");
    }
}
```

- `@UiKitTest` options: `headless`, `windowWidth/Height`, `timeoutMillis`, `pollingMillis`, `jsClickFallback` (opt-in), `screenshotDir` (empty disables), `factory` (custom `DriverFactory` for other browsers or a grid).
- A failed test saves `<Class>.<method>.png` in `target/uikit-screenshots` and is still reported as failed.
- Run with a visible browser: `-Duikit.headed=true`.
- Scopes: `uikit-core`, `junit-jupiter-api` and `selenium-chrome-driver` are compile scope; `junit-platform-launcher` is test only.
- Selenium Manager (bundled with Selenium) downloads the driver. Use your own `DriverFactory` if your network blocks that.

## Demo (Phase 4, `uikit-demo`)

`uikit-demo` is an example consumer, not a published artifact; all its dependencies are test scope. It shows the intended structure:

- `src/test/resources/app/index.html`: a local demo app (login, sidebar, modal, iframe).
- `pages/LoginPage`, `pages/InventoryApp`: page objects that hold **all** application-specific locators and pass them to the generic components.
- `InventoryAppIT`: tests using `@UiKitTest` and the injected `UiContext`.

Run just the demo: `mvn -pl uikit-demo -am verify`.

There are no tests against external websites in the build. If you add some, keep them out of the default lifecycle (for example behind a Maven profile).

## Breaking changes vs. the original repository

- `ButtonClicker`/`Sidebar` now live in `com.uikit.core.component`. `clickButtonInModal(text)` became `clickButtonInContainer(By, text)` and `clickButtonInIframe(String, text)` became `clickButtonInFrame(By, text)`; `Sidebar` requires `SidebarLocators` (the site-specific defaults were removed). Missing sidebar items now throw instead of printing.

- Maven coordinates are now `io.github.mudassorahmedchowdhury:uikit-parent` (was `com.uikit`); Java baseline is 17 (was 23); TestNG, JUnit 4, Allure, WebDriverManager and commons-io are removed.
- The original sources were removed from the working tree; they remain available in the git history. Issues fixed in the rewrite: `System.out` logging, silent failure in `Sidebar`, site-specific locators, lower-casing text against XPath `contains`, hard-coded 10 s/2 s waits.

## Contributing

Issues and pull requests are welcome. Before opening a pull request, run `mvn clean verify` and make sure it passes. Keep application-specific locators out of `uikit-core`; they belong in page objects such as those in `uikit-demo`.

## License

Licensed under the [Apache License, Version 2.0](LICENSE). See [NOTICE](NOTICE) for attribution.

## Credits

Original work by [Mudassor Ahmed Chowdhury](https://www.linkedin.com/in/mudassor/).
