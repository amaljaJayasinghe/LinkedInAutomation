package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.ElementUtils;

/**
 * Page Object for the feed page you land on after logging in.
 * We treat "URL has /feed + search box is visible" as proof that login worked.
 */
public class FeedPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By searchBox = By.cssSelector("input[placeholder='Search']");

    public FeedPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public boolean isLoaded() {
        try {
            wait.until(ExpectedConditions.urlContains("/feed"));
            ElementUtils.waitForFirstVisible(driver, wait, searchBox);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public SearchResultsPage searchFor(String term) {
        WebElement box = ElementUtils.waitForFirstVisible(driver, wait, searchBox);
        box.click();
        box.clear();
        box.sendKeys(term);
        box.sendKeys(Keys.ENTER);
        return new SearchResultsPage(driver, wait);
    }
}
