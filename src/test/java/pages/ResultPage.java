package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.ElementUtils;

/**
 * Page Object for whatever page we land on after clicking a result
 * (a person's profile or a company page). Both show the name as an h2.
 */
public class ResultPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By heading = By.cssSelector("main h1, main h2");

    public ResultPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getHeadingText() {
        return ElementUtils.waitForFirstVisible(driver, wait, heading).getText();
    }

    public boolean isProfileOrCompanyPage() {
        String url = getCurrentUrl();
        return url.contains("/in/") || url.contains("/company/");
    }
}
