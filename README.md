# Java Selenium UI Kit

A reusable Java library of Selenium UI interactions with configurable locators, centralized waits and typed exceptions. It is a plain Maven library: no frontend, REST API, backend or database.

> **Status:** Phase 1 (foundation) is complete. Components such as buttons and sidebars are being ported from `legacy/` in later phases.

## Requirements

- Java 17+, Maven 3.9+
- Google Chrome (integration tests run headless Chrome; Selenium Manager fetches the driver)

## Build

```
mvn clean verify
```

Unit tests (`*Test`) run in `surefire`; fixture-based browser tests (`*IT`) run in `failsafe`. Javadoc is checked in `verify` and fails on warnings.

## Modules

| Module | Status |
|---|---|
| `uikit-core` | Phase 1: `WaitConfig`, `Waits`, typed exceptions |
| `uikit-testing` | planned (JUnit 5 integration) |
| `uikit-demo` | planned (examples) |

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

## Breaking changes vs. the original repository

- Maven coordinates are now `com.uikit:uikit-parent`; Java baseline is 17 (was 23); TestNG, JUnit 4, Allure, WebDriverManager and commons-io are removed.
- The original sources (`ButtonClicker`, `Sidebar`, site-specific locators, `LoginPage`, tests) are preserved unchanged under `legacy/` and are **not** part of the build. Known issues to fix when porting: `System.out` logging, silent failure in `Sidebar`, site-specific locators, lower-casing text against XPath `contains`, hard-coded 10 s/2 s waits.

## Credits

Original work by [Mudassor Ahmed Chowdhury](https://www.linkedin.com/in/mudassor/).
