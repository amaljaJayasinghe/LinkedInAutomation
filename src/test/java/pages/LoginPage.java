package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import utils.ElementUtils;

/**
 * Page Object for the LinkedIn login page.
 *
 * Note on locators: LinkedIn's id attributes change every time the page
 * reloads, and the page also hides a duplicate copy of each field/button
 * somewhere on screen. So instead of By.id(...), we locate by input type /
 * button text, and always wait for the VISIBLE match (see ElementUtils).
 *
 * Security checks (CAPTCHA / "verify it's you" codes): this class never
 * tries to solve or click through one. If LinkedIn shows one (URL contains
 * "/checkpoint/"), the test pauses and waits for a person to clear it by
 * hand in the visible browser window, then carries on by itself.
 */
public class LoginPage {

    private static final String URL = "https://www.linkedin.com/login";

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By usernameField = By.cssSelector("input[type='email']");
    private final By passwordField = By.cssSelector("input[type='password']");
    private final By signInButton = By.xpath("//button[normalize-space(.)='Sign in']");
    private final By errorBanner = By.xpath(
            "//p[contains(normalize-space(.), 'Wrong email or password') "
            + "or contains(normalize-space(.), 'Please enter a valid')]");
    // LinkedIn sometimes shows a "We're signing you in..." auto-resume screen
    // for a remembered account instead of the login form - this cancels it.
    private final By cancelAutoSignIn = By.xpath("//*[self::a or self::button][normalize-space(.)='Cancel sign in']");

    private static final Duration MANUAL_CHECKPOINT_TIMEOUT = Duration.ofMinutes(5);

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public LoginPage open() {
        driver.get(URL);
        pauseForManualSecurityCheck();
        waitUntilFormReady();

        // We reuse the same Chrome profile between tests, so sometimes we're
        // already logged in from an earlier test and LinkedIn skips straight
        // to the feed instead of showing the form. If that happens, log out
        // and load the login page again.
        if (driver.getCurrentUrl().contains("/feed")) {
            driver.get("https://www.linkedin.com/m/logout/");
            driver.get(URL);
            pauseForManualSecurityCheck();
            waitUntilFormReady();
        }
        return this;
    }

    // If LinkedIn shows a CAPTCHA or "verify it's you" checkpoint, we do NOT
    // try to solve it in code. We just wait here - with the browser visible -
    // until a person clears it by hand, then the test continues on its own.
    private void pauseForManualSecurityCheck() {
        if (!driver.getCurrentUrl().contains("/checkpoint/")) {
            return;
        }
        System.out.println();
        System.out.println("============================================================");
        System.out.println("LinkedIn is showing a security check (CAPTCHA / email code).");
        System.out.println("The test will NOT try to bypass this.");
        System.out.println("Please complete it by hand in the open browser window.");
        System.out.println("Waiting up to 5 minutes for it to clear...");
        System.out.println("============================================================");
        System.out.println();

        new WebDriverWait(driver, MANUAL_CHECKPOINT_TIMEOUT)
                .until(d -> !d.getCurrentUrl().contains("/checkpoint/"));

        System.out.println("Security check cleared - continuing automatically.");
    }

    // Waits for the login form - or, if LinkedIn offers to auto sign back in
    // as a remembered account instead of showing the form, cancels that first.
    private void waitUntilFormReady() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/feed"),
                ExpectedConditions.presenceOfElementLocated(usernameField),
                ExpectedConditions.presenceOfElementLocated(cancelAutoSignIn)));

        if (!driver.findElements(cancelAutoSignIn).isEmpty()) {
            ElementUtils.waitForFirstVisible(driver, wait, cancelAutoSignIn).click();
            ElementUtils.waitForFirstVisible(driver, wait, usernameField);
        }
    }

    public LoginPage enterUsername(String username) {
        WebElement field = ElementUtils.waitForFirstVisible(driver, wait, usernameField);
        field.clear();
        field.sendKeys(username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        WebElement field = ElementUtils.waitForFirstVisible(driver, wait, passwordField);
        field.clear();
        field.sendKeys(password);
        return this;
    }

    public void clickSignIn() {
        ElementUtils.waitForFirstVisible(driver, wait, signInButton).click();
    }

    public LoginPage loginAs(String username, String password) {
        open();
        enterUsername(username);
        enterPassword(password);
        clickSignIn();

        // Give the page a moment to settle on wherever Sign in sends us
        // (feed, a wrong-password error, or a security checkpoint) before
        // checking which one happened.
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/feed"),
                    ExpectedConditions.urlContains("/checkpoint/"),
                    ExpectedConditions.presenceOfElementLocated(errorBanner)));
        } catch (Exception ignored) {
            // handled below - isErrorDisplayed()/pauseForManualSecurityCheck() just won't find anything yet
        }
        pauseForManualSecurityCheck();
        return this;
    }

    public boolean isErrorDisplayed() {
        try {
            WebElement error = ElementUtils.waitForFirstVisible(driver, wait, errorBanner);
            return error.isDisplayed() && !error.getText().isBlank();
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorText() {
        return ElementUtils.waitForFirstVisible(driver, wait, errorBanner).getText();
    }
}
