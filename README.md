# MultiBank QA Automation Framework

Production-grade **Java + Selenium 4 + TestNG** framework for end-to-end testing of
[trade.multibank.io](https://trade.multibank.io).

---

## Table of Contents
1. [Tech Stack](#tech-stack)
2. [Project Structure](#project-structure)
3. [Architecture Overview](#architecture-overview)
4. [Prerequisites](#prerequisites)
5. [Quick Start](#quick-start)
6. [Running Tests](#running-tests)
7. [Test Data Management](#test-data-management)
8. [Cross-Browser Execution](#cross-browser-execution)
9. [Parallel Execution](#parallel-execution)
10. [Selenium Grid / Cloud](#selenium-grid--cloud)
11. [Reporting](#reporting)
12. [CI/CD Pipeline](#cicd-pipeline)
13. [Design Decisions](#design-decisions)

---

## Tech Stack

| Component            | Choice                          | Why                                              |
|----------------------|---------------------------------|--------------------------------------------------|
| Language             | Java 17                         | Strong typing, excellent IDE support, LTS        |
| Browser automation   | Selenium WebDriver 4            | Industry standard, full cross-browser support    |
| Test framework       | TestNG 7                        | Parallel execution, data providers, listeners    |
| Driver management    | WebDriverManager 5              | Zero-config binary resolution                    |
| Reporting            | ExtentReports 5 (HTML + Charts) | Rich inline screenshots, trend analysis          |
| Assertions           | AssertJ                         | Fluent, readable, soft-assertion support         |
| Test data            | Jackson (JSON / YAML)           | Clean external data; no hard-coded values        |
| Logging              | SLF4J + Logback                 | Structured logs, rolling file output             |
| Build                | Maven 3.9+                      | Profiles, property injection, surefire plugin    |
| CI/CD                | GitHub Actions                  | Native YAML, matrix builds, artifact upload      |

---

## Project Structure

```
multibank-qa/
├── pom.xml                                  # Maven build + dependency management
├── .github/workflows/ci.yml                 # GitHub Actions CI pipeline
│
├── src/main/java/com/multibank/
│   ├── config/
│   │   └── EnvironmentConfig.java           # Env-aware property loader
│   ├── factory/
│   │   └── DriverFactory.java               # Thread-safe WebDriver factory
│   ├── pages/                               # Page Object Model
│   │   ├── BasePage.java                    # Smart waits, JS helpers, scroll
│   │   ├── HomePage.java                    # Home + Spot trading + download section
│   │   ├── NavigationPage.java              # Top nav component
│   │   └── AboutPage.java                   # About Us / Why MultiBank page
│   ├── listeners/
│   │   ├── ExtentReportManager.java         # Singleton report engine
│   │   ├── TestListener.java                # Pass/fail hooks + screenshot on failure
│   │   ├── RetryAnalyzer.java               # Per-test retry logic
│   │   └── RetryTransformer.java            # Global retry wiring
│   └── utils/
│       ├── WaitUtils.java                   # Explicit wait helpers (no Thread.sleep)
│       ├── TestDataLoader.java              # JSON/YAML loader + extractors
│       └── ScreenshotUtils.java             # PNG capture + disk save
│
└── src/test/
    ├── java/com/multibank/tests/
    │   ├── BaseTest.java                    # Driver lifecycle (@Before/@AfterMethod)
    │   ├── NavigationTest.java              # TC-NAV-001 … TC-NAV-008
    │   ├── TradingTest.java                 # TC-TRADE-001 … TC-TRADE-008
    │   └── ContentTest.java                 # TC-CONTENT-001 … TC-CONTENT-011
    └── resources/
        ├── config/
        │   └── prod.properties              # Base URL, browser defaults
        ├── testdata/
        │   ├── navigation.json              # Expected nav items, link destinations
        │   ├── trading.json                 # Expected pairs, categories, counts
        │   └── content.json                 # App store domains, about page keywords
        ├── suites/
        │   ├── smoke.xml                    # @smoke tests only (<3 min)
        │   ├── full-regression.xml          # All tests, parallel by class
        │   ├── cross-browser.xml            # Chrome + Firefox + Edge
        │   └── grid.xml                     # Remote/Grid execution
        └── logback-test.xml                 # Logging config
```

---

## Architecture Overview

```
┌──────────────────────────────────────────────────────┐
│                   Test Classes                        │
│  NavigationTest  TradingTest  ContentTest             │
│         └── extends BaseTest                          │
└────────────────────┬─────────────────────────────────┘
                     │ uses
┌────────────────────▼─────────────────────────────────┐
│              Page Object Model                        │
│   BasePage ◄── NavigationPage / HomePage / AboutPage  │
└────────────────────┬─────────────────────────────────┘
                     │ uses
┌────────────────────▼──────────────┐  ┌───────────────┐
│         DriverFactory             │  │  TestDataLoader│
│   (ThreadLocal<WebDriver>)        │  │  (JSON/YAML)   │
└───────────────────────────────────┘  └───────────────┘
                     │
┌────────────────────▼──────────────────────────────────┐
│                  Listeners                             │
│  TestListener → ExtentReportManager + ScreenshotUtils │
│  RetryTransformer → RetryAnalyzer                      │
└───────────────────────────────────────────────────────┘
```

### Key Design Principles

| Principle              | Implementation                                                           |
|------------------------|--------------------------------------------------------------------------|
| **No hard-coded values** | All assertions read from `testdata/*.json`                             |
| **No `Thread.sleep`**   | `WaitUtils` + `WebDriverWait` with `ExpectedConditions`                |
| **Thread safety**       | `ThreadLocal<WebDriver>` in `DriverFactory`; each test gets its own driver |
| **Test independence**   | `@BeforeMethod` creates fresh driver + navigates to base URL           |
| **Soft assertions**     | `SoftAssertions` collects all failures before throwing                 |
| **Failure diagnostics** | Inline base64 screenshots + stack traces in ExtentReports              |
| **Retry on flakiness**  | `RetryTransformer` applies `RetryAnalyzer` globally (default: 1 retry) |

---

## Prerequisites

- **Java 17+** (`java -version`)
- **Maven 3.9+** (`mvn -version`)
- **Chrome / Firefox / Edge** installed locally (WebDriverManager downloads the matching driver binary automatically)

---

## Quick Start

```bash
# Clone
git clone https://github.com/your-org/multibank-qa.git
cd multibank-qa

# Compile and run smoke suite (headless Chrome)
mvn test -Psmoke -Dheadless=true

# Open the HTML report
open reports/html/MultiBank_TestReport_*.html
```

---

## Running Tests

### By suite profile

```bash
# Smoke (fast sanity, ~3 min)
mvn test -Psmoke

# Full regression
mvn test -Pregression

# Cross-browser (Chrome + Firefox + Edge)
mvn test -Pcross-browser
```

### By individual test groups

```bash
# Only navigation tests
mvn test -Dgroups=navigation

# Only smoke tests
mvn test -Dgroups=smoke

# Only content/about tests
mvn test -Dgroups=content,about
```

### Override browser / headless at runtime

```bash
mvn test -Dbrowser=firefox -Dheadless=true -Psmoke

mvn test -Dbrowser=edge -Dthreads=2 -Pregression
```

---

## Test Data Management

All expected values live in `src/test/resources/testdata/`.
Test classes never contain hard-coded assertion strings.

```json
// testdata/navigation.json
{
  "expectedNavItems": ["Trade", "Spot", "Convert", "Instant Buy"],
  "minimumNavLinks": 3,
  "navItemDestinations": [
    { "link": "Spot",    "url": "spot"    },
    { "link": "Convert", "url": "convert" }
  ]
}
```

To update expectations when the UI changes, **edit the JSON file only** — no Java changes needed.

---

## Cross-Browser Execution

```bash
# Explicit browser selection
mvn test -Dbrowser=chrome   -Psmoke
mvn test -Dbrowser=firefox  -Psmoke
mvn test -Dbrowser=edge     -Psmoke

# All three browsers in one run (parallel)
mvn test -Pcross-browser
```

`DriverFactory` resolves the binary via WebDriverManager — no PATH setup required.

---

## Parallel Execution

Parallelism is controlled by:

| Config point        | Default     | Override                      |
|---------------------|-------------|-------------------------------|
| `threads` in XML    | 2–4         | `-Dthreads=6`                 |
| `parallel` in XML   | `classes`   | Edit suite XML                |
| Maven `-T` flag     | —           | `mvn -T 2 test` (Maven level) |

Each thread owns its own `WebDriver` instance (ThreadLocal) — full isolation guaranteed.

---

## Selenium Grid / Cloud

Set the `grid.url` property to route tests to a remote hub:

```bash
# Local Selenium Grid 4
mvn test -Pgrid -Dgrid.url=http://localhost:4444/wd/hub

# Sauce Labs
mvn test -Pgrid \
  -Dgrid.url=https://USER:KEY@ondemand.saucelabs.com/wd/hub \
  -Dbrowser=chrome

# BrowserStack
mvn test -Pgrid \
  -Dgrid.url=https://USER:KEY@hub.browserstack.com/wd/hub \
  -Dbrowser=firefox
```

---

## Reporting

After a test run, reports are written to:

```
reports/
├── html/          # ExtentReports HTML (open in browser)
├── screenshots/   # PNG captures for failed tests
├── logs/          # Logback rolling log files
├── junit.xml      # JUnit XML (consumed by CI tools)
└── results.json   # Raw JSON results
```

The ExtentReports HTML includes:
- Per-test pass/fail timeline
- Inline screenshot on every failure
- System info panel (browser, env, OS, Java)
- Test duration breakdown

---

## CI/CD Pipeline

GitHub Actions (`.github/workflows/ci.yml`):

| Trigger                   | Jobs                                     |
|---------------------------|------------------------------------------|
| Push to `main`/`develop`  | Smoke – Chrome + Firefox (parallel)      |
| Pull Request              | Smoke – Chrome + Firefox                 |
| Nightly schedule (02:00)  | Full regression – Chrome                 |
| `workflow_dispatch`       | Configurable suite + browser via UI      |

Artefacts uploaded per run:
- ExtentReports HTML report (30-day retention)
- Failure screenshots (14-day retention)
- Log files (7-day retention)
- JUnit XML → GitHub PR check annotations

---

## Design Decisions

### Why `@BeforeMethod` driver creation (not `@BeforeSuite`)?
Each test method gets a fresh browser session. This ensures **zero shared state** — a key
requirement for deterministic, independently-runnable tests in parallel.

### Why soft assertions?
`SoftAssertions` collects every failure before throwing. A test that checks 5 nav items
reports all 5 missing items in one run rather than stopping at the first.

### Why ExtentReports over Allure?
ExtentReports produces a single self-contained HTML file with no server required.
It embeds screenshots as base64, making it trivially shareable as a CI artefact.

### Why JSON for test data over `@DataProvider` hard-coding?
JSON files are version-controlled separately from Java code. A QA analyst (non-developer)
can update expected values when the UI changes without touching or recompiling test classes.
