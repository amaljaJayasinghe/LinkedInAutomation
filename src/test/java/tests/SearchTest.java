package tests;

import base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.FeedPage;
import pages.LoginPage;
import pages.SearchResultsPage;
import utils.ConfigReader;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Task 3: Automate Search.
 */
public class SearchTest extends BaseTest {

    private static final String SEARCH_TERM = "software testing";

    @Test
    @DisplayName("Search after login shows a results page with at least one visible result")
    void search_afterLogin_showsResults() {
        String username = ConfigReader.require("LINKEDIN_EMAIL");
        String password = ConfigReader.require("LINKEDIN_PASSWORD");

        LoginPage loginPage = new LoginPage(driver, wait);
        loginPage.loginAs(username, password);

        FeedPage feedPage = new FeedPage(driver, wait);
        assertTrue(feedPage.isLoaded(), "Login must succeed before the search step can run.");

        SearchResultsPage resultsPage = feedPage.searchFor(SEARCH_TERM);

        assertTrue(resultsPage.isLoaded(),
                "Expected the URL to contain '/search/results' and the results container to be visible.");
        assertTrue(driver.getCurrentUrl().contains("/search/results"),
                "Expected URL to contain '/search/results', but was: " + driver.getCurrentUrl());
        assertFalse(resultsPage.getResultItems().isEmpty(),
                "Expected at least one visible result item on the search results page.");
    }
}
