package base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import utils.ConfigReader;

/**
 * Opens a new browser before each test and closes it after. Every test class
 * extends this so we don't repeat the setup/teardown code everywhere.
 */
@ExtendWith(ScreenshotOnFailureExtension.class)
public abstract class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    protected static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(15);

    // Reusing the same Chrome profile folder between runs keeps us logged in
    // as a "known" device, so LinkedIn doesn't ask for an email code every time.
    private static final String PROFILE_DIR = System.getProperty("user.dir") + "\\.tools\\chrome-profile";

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();

        if ("true".equalsIgnoreCase(ConfigReader.getOrDefault("HEADLESS", "false"))) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--disable-notifications");
        options.addArguments("--user-data-dir=" + PROFILE_DIR);

        // Selenium Manager (built into Selenium 4) downloads the matching
        // chromedriver by itself, so we don't need WebDriverManager.
        driver = new ChromeDriver(options);
        driver.manage().window().setSize(new Dimension(1440, 900));
        wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Used by ScreenshotOnFailureExtension to grab a screenshot on failure.
    public WebDriver getDriver() {
        return driver;
    }
}
