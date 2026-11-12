# Contributing to Java Selenium UI Kit

Thanks for helping! SQA engineers, test automation engineers and developers are all welcome.

## Ways to contribute
- Report bugs or flaky behaviour (use the issue templates).
- Propose or add new UI components/interactions.
- Improve docs, Javadoc and examples (`uikit-demo`).
- Add tests. Pick an issue labelled `good first issue`.

## Setup
Requirements: Java 17+, Maven 3.9+, Google Chrome.

```
git clone https://github.com/mudassorahmedchowdhury/java-selenium-function-main.git
cd java-selenium-function-main
mvn clean verify
```

Modules: `uikit-core` (library), `uikit-testing` (JUnit 5 integration), `uikit-demo` (usage examples).

## Workflow
1. Open or comment on an issue first for anything non-trivial.
2. Fork and create a branch: `feature/<short-name>`, `fix/<short-name>`, `docs/<short-name>`.
3. Make small, focused commits (imperative mood, e.g. `Add DatePicker.selectRange`).
4. Run `mvn clean verify` locally; it must pass.
5. Open a pull request using the template.

## Coding guidelines
- Java 17; no new runtime dependencies without discussion.
- Use the centralized waits; never use `Thread.sleep`.
- Locators must be configurable, not hard-coded.
- Throw the library's typed exceptions with clear messages.
- Public APIs need Javadoc (the build fails on Javadoc warnings).
- Unit tests are `*Test` (surefire); browser fixture tests are `*IT` (failsafe). New behaviour needs a test.
- Keep tests deterministic: no external sites, use local fixtures.

## Licensing
By contributing you agree your work is licensed under the [Apache 2.0 License](LICENSE).

Please follow our [Code of Conduct](CODE_OF_CONDUCT.md).
