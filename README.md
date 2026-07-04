# demo-shop-spring-tests

[![QA Tests](https://github.com/prkpchk/demo-shop-spring-tests/actions/workflows/tests.yml/badge.svg)](https://github.com/prkpchk/demo-shop-spring-tests/actions/workflows/tests.yml)

Production-ready QA automation framework for the [demo-shop](https://github.com/prkpchk/demo-shop) Spring Boot e-commerce application.

Covers API testing, UI testing, database verification, mobile testing, and full CI/CD integration.

📊 **[Live Allure Report](https://prkpchk.github.io/demo-shop-spring-tests/)** — latest test results with run history and trends, published to GitHub Pages on every push.

## Stack

| Concern | Technology                                         |
|---|----------------------------------------------------|
| Language | Java 21                                            |
| Build | Gradle 8 (Groovy DSL)                              |
| DI / Config | Spring Boot 3.3 (test context only, no web server) |
| API testing | Retrofit2 + OkHttp3                                |
| UI testing | Selenide 7                                         |
| Mobile testing | Appium 2 + selenide-appium                         |
| DB verification | Spring JdbcTemplate → PostgreSQL                   |
| Test framework | JUnit 5                                            |
| Reporting | Allure 2.25                                        |
| Logging | SLF4J + Logback                                    |
| Test data | JavaFaker (random, no cleanup required)            |
| Containers | Testcontainers (PostgreSQL + Selenium Grid)        |

---

## Prerequisites

| Requirement | Version |
|---|---|
| Java | 21+ |
| Docker + Docker Compose | any recent |
| Allure CLI (for local reports) | 2.x (`brew install allure` / `scoop install allure`) |
| Appium Server (mobile only) | 2.x |

---

## Quick Start

### 1 — Start the demo-shop application

```bash
docker compose -f docker-compose.hub.yml up -d

# Wait until healthy
curl -s http://localhost:8080/actuator/health
```

### 2 — Run all tests

```bash
./gradlew test
```

### 3 — Open the Allure report

```bash
allure serve build/allure-results
```

---

## Running Specific Test Groups

```bash
# API tests only
./gradlew test --tests "com.demoshop.qa.api.*"

# UI tests only
./gradlew test --tests "com.demoshop.qa.ui.*"

# A single test class
./gradlew test --tests "com.demoshop.qa.api.auth.AuthApiTest"

# A single test method
./gradlew test --tests "com.demoshop.qa.api.auth.AuthApiTest.register_success"

# Mobile tests (requires Appium server + device/emulator)
./gradlew test -Dspring.profiles.active=mobile --tests "com.demoshop.qa.mobile.*"
```

---

## Spring Profiles

| Profile | When to use |
|---|---|
| *(default / local)* | demo-shop stack running locally via Docker Compose |
| `ci` | Inside CI pipelines — app & DB accessed by container hostname |
| `docker` | Custom Docker network run |
| `testcontainers` | Fully isolated run — starts fresh Postgres + Selenium Grid via Testcontainers |
| `mobile` | Appium mobile tests |

```bash
# Example: run with testcontainers profile (no external stack needed)
./gradlew test -Dspring.profiles.active=testcontainers
```

---

## Configuration

All settings live in `src/test/resources/`. Environment variables override any value.

| Variable | Default | Description |
|---|---|---|
| `APP_BASE_URL` | `http://localhost:8080` | Demo-shop REST API base URL |
| `APP_UI_URL` | `http://localhost:80` | Demo-shop frontend base URL |
| `DB_URL` | `jdbc:postgresql://localhost:5432/demoshop` | JDBC URL for DB verification |
| `DB_USER` | `demoshop` | DB username |
| `DB_PASSWORD` | `demoshop` | DB password |
| `BROWSER` | `chrome` | Selenide browser (`chrome`, `firefox`, `edge`, `safari`) |
| `HEADLESS` | `false` | Run browser headless |
| `APPIUM_URL` | `http://localhost:4723` | Appium server URL |
| `MOBILE_PLATFORM` | `android` | `android` or `ios` |
| `MOBILE_DEVICE_NAME` | `emulator-5554` | Device name / UDID |
| `MOBILE_APP_PATH` | `apps/demo-shop.apk` | Path to the app binary |

---

## Project Structure

```
src/main/java/com/demoshop/qa/          # reusable framework
├── TestApplication.java          # Spring Boot entry point (@SpringBootApplication)
├── core/
│   ├── config/
│   │   ├── AppConfig.java            # Binds app.* properties
│   │   ├── SelenideConfig.java       # Configures Selenide (browser, timeout, baseUrl)
│   │   ├── RetrofitConfig.java       # OkHttp client, Retrofit bean, auth factory
│   │   ├── TestcontainersConfig.java # Postgres @ServiceConnection + Selenium container
│   │   └── MobileDriverConfig.java   # UiAutomator2 / XCUITest driver factory
│   ├── junit/extensions/
│   │   └── RetryExtension.java       # JUnit 5 extension — retries failing tests up to 3×
│   └── utils/
│       └── TestUtils.java            # waitUntil(), Allure text/json attachments
├── clients/
│   ├── api/                      # Retrofit interfaces: Auth, Product, Cart, Order, User
│   └── database/
│       └── DatabaseHelper.java   # Low-level JdbcTemplate queries
├── data/dto/api/                 # request/response DTOs, one subpackage per module
│   ├── auth/  cart/  order/  product/  user/  common/
├── assertions/
│   └── DbAssertions.java         # High-level DB assertions (assertUserExists, assertOrderStatus, …)
├── dataproviders/
│   └── FakerUtils.java           # Random email, name, password, product data
├── ui/
│   ├── pages/                    # BasePage, LoginPage, RegisterPage, CatalogPage,
│   │                             #   ProductPage, CartPage, OrdersPage, ProfilePage, PaymentModalPage
│   └── components/               # NavbarComponent
└── mobile/
    └── pages/                    # AbstractMobilePage, LoginPageAndroid/IOS, CatalogPageAndroid/IOS

src/test/java/com/demoshop/qa/          # test classes only
├── base/                         # BaseTest, BaseApiTest, BaseUiTest, BaseMobileTest
├── api/                          # one subpackage per module
│   ├── auth/  cart/  orders/  products/
├── ui/
│   ├── auth/  cart/  catalog/  orders/
└── mobile/
    └── auth/                     # LoginMobileTest
```

---

## Parallelism

Parallelism works on two levels: how the CI pipeline schedules the test suites, and how JUnit runs tests inside a single Gradle invocation.

### Inside a Gradle run — parallel

JUnit 5 parallel execution is enabled by default (`junit-platform.properties`): both test classes and test methods run concurrently on a fixed thread pool.

```properties
junit.jupiter.execution.parallel.enabled=true
junit.jupiter.execution.parallel.mode.default=concurrent
junit.jupiter.execution.parallel.mode.classes.default=concurrent
junit.jupiter.execution.parallel.config.strategy=fixed
junit.jupiter.execution.parallel.config.fixed.parallelism=4
```

The pool width can be overridden per run:

```bash
./gradlew test -Djunit.jupiter.execution.parallel.config.fixed.parallelism=2
```

UI tests are safe to parallelize because Selenide keeps its WebDriver in a `ThreadLocal` — each thread drives its own browser. Keep the parallelism for UI runs lower than for API runs: every extra thread is an extra Chrome instance.

### Across suites in CI — depends on the pipeline

- **GitHub Actions** (`.github/workflows/tests.yml`): a single `test` job; the *API tests* and *UI tests* steps run **sequentially** (steps inside a job always do). API tests run with `parallelism=4`, then UI tests with `parallelism=2`. If the API step fails, the UI step is skipped.
- **Jenkins** (`Jenkinsfile`): API and UI are separate **parallel** stages.
- **GitLab CI** (`.gitlab-ci.yml`): jobs within the same stage run in parallel, subject to available runners.

### Why it doesn't flake

Every test registers its own random JavaFaker user and never touches seed data (except the ADMIN-only tests) — no shared state between threads, no cleanup needed. This same property makes `RetryExtension` retries safe: a re-run of a test method operates on data no other test can see.

---

## Allure Report

Artifacts produced after `./gradlew test`:

| Path | Contents |
|---|---|
| `build/allure-results/` | Raw Allure JSON (consumed by report tool) |
| `build/reports/selenide/` | Screenshots saved by Selenide |
| `build/reports/video/` | Videos from BrowserWebDriverContainer (testcontainers profile) |
| `build/reports/logs/` | Rolling test log |

```bash
# Generate static HTML report
allure generate build/allure-results --clean -o build/allure-report

# Or serve interactively
allure serve build/allure-results
```

---

## CI/CD Pipelines

Three pipeline files are provided. All trigger on `push` only.

| File | Platform |
|---|---|
| `Jenkinsfile` | Jenkins (Docker agent, Allure plugin) |
| `.gitlab-ci.yml` | GitLab CI (Docker-in-Docker) |
| `.github/workflows/tests.yml` | GitHub Actions (ubuntu-latest, gh-pages Allure publish) |

Each pipeline:
1. Starts the demo-shop stack via `docker compose -f docker-compose.hub.yml up -d`
2. Runs API and UI tests in parallel
3. Archives Allure results, screenshots, videos, and JUnit XML

---

## Seed Users

The demo-shop ships with two pre-seeded users available for tests:

| Email | Password | Role | Balance |
|---|---|---|---|
| `admin@demoshop.com` | `admin123` | ADMIN | $10 000 |
| `user@demoshop.com` | `user123` | USER | $1 000 |

All other tests register random users via the API (`FakerUtils.randomRegisterRequest()`).
