package tests;

import base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.FeedPage;
import pages.LoginPage;
import pages.ResultPage;
import pages.SearchResultsPage;
import utils.ConfigReader;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Task 4: Click a Search Result.
 */
public class SearchResultClickTest extends BaseTest {

    private static final String SEARCH_TERM = "software testing";

    @Test
    @DisplayName("Clicking the first search result opens the matching profile/company page")
    void clickFirstResult_opensCorrectPage() {
        String username = ConfigReader.require("LINKEDIN_EMAIL");
        String password = ConfigReader.require("LINKEDIN_PASSWORD");

        LoginPage loginPage = new LoginPage(driver, wait);
        loginPage.loginAs(username, password);

        FeedPage feedPage = new FeedPage(driver, wait);
        assertTrue(feedPage.isLoaded(), "Login must succeed before the search step can run.");

        SearchResultsPage resultsPage = feedPage.searchFor(SEARCH_TERM);
        assertTrue(resultsPage.isLoaded(), "Search results page must load before clicking a result.");

        String expectedHref = resultsPage.clickFirstResult();

        // Wait for navigation away from the results page to the clicked result.
        wait.until(ExpectedConditions.urlContains(
                expectedHref.contains("/company/") ? "/company/" : "/in/"));

        ResultPage resultPage = new ResultPage(driver, wait);
        assertTrue(resultPage.isProfileOrCompanyPage(),
                "Expected the opened page URL to contain '/in/' or '/company/', but was: "
                        + resultPage.getCurrentUrl());
        assertFalse(resultPage.getHeadingText().isBlank(),
                "Expected the opened page to show a non-empty heading (person/company name).");
    }
}
