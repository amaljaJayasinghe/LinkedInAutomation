package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import utils.ElementUtils;

/**
 * Page Object for the search results page.
 * Each result is a div[role='listitem'], and each one has a link to
 * either a /in/... profile page or a /company/... page.
 */
public class SearchResultsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By resultItems = By.cssSelector("main div[role='listitem']");
    private final By resultLinks = By.cssSelector("main div[role='listitem'] a[href*='/in/'], "
            + "main div[role='listitem'] a[href*='/company/']");

    public SearchResultsPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public boolean isLoaded() {
        try {
            wait.until(ExpectedConditions.urlContains("/search/results"));
            wait.until(ExpectedConditions.presenceOfElementLocated(resultItems));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getResultItems() {
        return driver.findElements(resultItems);
    }

    public WebElement getFirstResultLink() {
        return ElementUtils.waitForFirstVisible(driver, wait, resultLinks);
    }

    public String clickFirstResult() {
        WebElement firstLink = getFirstResultLink();
        String expectedHref = firstLink.getAttribute("href");
        firstLink.click();
        return expectedHref;
    }
}
