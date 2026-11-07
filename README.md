# LinkedIn Selenium Test Suite

A JUnit 5 + Selenium WebDriver (Java) test suite that automates LinkedIn login,
global search, and clicking a search result, using the Page Object Model and
explicit waits. Built for an academic assignment on UI test automation.

## Project layout

```
pom.xml
mvnw.cmd                  # thin wrapper, runs the bundled Maven under .tools/ (no system install needed)
src/test/java/
  base/BaseTest.java                   # starts/stops the browser per test; wires screenshot-on-failure
  base/ScreenshotOnFailureExtension.java
  pages/LoginPage.java                 # Page Object: /login
  pages/FeedPage.java                  # Page Object: /feed (nav bar, search box)
  pages/SearchResultsPage.java         # Page Object: /search/results
  pages/ResultPage.java                # Page Object: generic /in/ or /company/ landing page
  utils/ConfigReader.java              # reads credentials from env vars / config.properties
  utils/ScreenshotUtil.java            # saves PNGs to /screenshots
  tests/LoginTest.java                 # Task 2 (positive + negative)
  tests/SearchTest.java                # Task 3
  tests/SearchResultClickTest.java     # Task 4
src/test/resources/config.properties.example
```

## Prerequisites

- **Java 17+** (JDK). Check with `java -version`.
- **Google Chrome** installed (the tests use ChromeDriver via Selenium Manager,
  which downloads the matching driver binary automatically - no manual
  driver setup needed).
- Maven is **not required to be installed system-wide** - this project
  bundles a local Maven under `.tools/` and `mvnw.cmd` calls it directly.
  If you already have Maven on your PATH, plain `mvn` works too.

## Configuring credentials

Credentials are **never hardcoded**. `utils.ConfigReader` resolves each value
in this order:

1. **Environment variable** (preferred)
2. **`src/test/resources/config.properties`** (local file, gitignored)

### Option A - environment variables (recommended)

PowerShell:
```powershell
$env:LINKEDIN_EMAIL = "your-email@example.com"
$env:LINKEDIN_PASSWORD = "your-password"
```

### Option B - local properties file

```powershell
Copy-Item src\test\resources\config.properties.example src\test\resources\config.properties
```
Then edit `config.properties` and fill in `LINKEDIN_EMAIL` / `LINKEDIN_PASSWORD`.
This file is listed in `.gitignore` and must never be committed.

> Use a real LinkedIn account you control and are comfortable automating.
> See the report's "Challenges" and "Limits of UI automation" sections for
> why LinkedIn is a particularly uncooperative automation target (CAPTCHAs,
> login challenges, changing markup).

Optional: set `HEADLESS=true` (env var or properties file) to run Chrome
headless, e.g. in CI. Defaults to `false` (visible browser) so runs can be
screen-recorded.

### About the persistent Chrome profile

LinkedIn treats a brand-new, cookie-less browser as "suspicious" and forces an
email verification checkpoint on login (a one-time code sent to your inbox).
To avoid needing that on every single test run, `BaseTest` reuses one on-disk
Chrome profile under `.tools/chrome-profile` (gitignored) across runs instead
of a fresh temp profile every time. The browser process still fully starts
and quits around every test (satisfying the "fresh browser per test"
requirement) - only the on-disk cookie/session data persists between runs.

**The first time** you run the suite against a given LinkedIn account, you may
still see the checkpoint once; sign in manually at that prompt (check your
email for the code) and subsequent runs should skip it, since the device is
now trusted.

## Running the tests

Run the whole suite:
```powershell
.\mvnw.cmd test
```

Run a single test class:
```powershell
.\mvnw.cmd test "-Dtest=LoginTest"
```

Run a single test method:
```powershell
.\mvnw.cmd test "-Dtest=LoginTest#invalidPassword_showsErrorMessage"
```

Surefire writes per-test XML/TXT reports to `target/surefire-reports/` after
each run - these are the "actual result" source for the report table.

On any test failure, a screenshot is saved automatically to `screenshots/`
(see `base/ScreenshotOnFailureExtension.java`).

## Notes on stability

LinkedIn frequently changes DOM structure, class names, and occasionally
challenges logins with a CAPTCHA or verification step that no Selenium
script can click through. If a locator in `pages/*.java` stops matching:

1. Open the relevant LinkedIn page, DevTools (F12), and inspect the element.
2. Prefer `id`, `aria-label`, `role`, or `href` substring matches over
   generated/hashed CSS class names - that's the strategy already used
   throughout `pages/`.
3. Update the `By` locator and re-run.

If a login attempt is blocked by a CAPTCHA/security checkpoint, the positive
login test will fail waiting for the feed page - this is expected and
discussed in the report, not a bug in the test code.


