# Java Selenium UI Kit

A reusable Java library of Selenium UI interactions with configurable locators, centralized waits and typed exceptions. It is a plain Maven library: no frontend, REST API, backend or database.

> **Status:** Phases 1 (foundation), 2 (`ButtonClicker`, `Sidebar`) 3 (JUnit 5 integration) and 4 (demo) are complete.

## Requirements

- Java 17+, Maven 3.9+
- Google Chrome (integration tests run headless Chrome; Selenium Manager fetches the driver)

## Build

```
mvn clean verify
```

Unit tests (`*Test`) run in `surefire`; fixture-based browser tests (`*IT`) run in `failsafe`. Javadoc is checked in `verify` and fails on warnings.

## Install into your own project

The artifacts are not on Maven Central yet. Install them into your local repository once:

```
git clone https://github.com/mudassorahmedchowdhury/java-selenium-function-main.git
cd java-selenium-function-main
mvn clean install        # or: mvn install -DskipTests
```

Then, in any Selenium project (Java 17+):

```xml
<dependency>
    <groupId>com.uikit</groupId>
    <artifactId>uikit-core</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

This was verified with a separate Maven project that depends only on `uikit-core` (plus Selenium's Chrome driver and JUnit for its test) and clicks a button via `new ButtonClicker(driver).clickButtonByText("Go")`.
`uikit-core` brings Selenium API and support transitively; add `selenium-chrome-driver` (or another driver) yourself. SLF4J output needs a binding such as `slf4j-simple`.

## Modules

| Module | Status |
|---|---|
| `uikit-core` | Phases 1-2: waits, typed exceptions, `ButtonClicker`, `Sidebar` |
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

## JUnit 5 integration (Phase 3, `uikit-testing`, optional)

```xml
<dependency>
    <groupId>com.uikit</groupId>
    <artifactId>uikit-testing</artifactId>
    <version>0.1.0-SNAPSHOT</version>
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

- Maven coordinates are now `com.uikit:uikit-parent`; Java baseline is 17 (was 23); TestNG, JUnit 4, Allure, WebDriverManager and commons-io are removed.
- The original sources (`ButtonClicker`, `Sidebar`, site-specific locators, `LoginPage`, tests) are preserved unchanged under `legacy/` and are **not** part of the build. Known issues to fix when porting: `System.out` logging, silent failure in `Sidebar`, site-specific locators, lower-casing text against XPath `contains`, hard-coded 10 s/2 s waits.

## Credits

Original work by [Mudassor Ahmed Chowdhury](https://www.linkedin.com/in/mudassor/).
