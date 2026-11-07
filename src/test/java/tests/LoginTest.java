package tests;

import base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.FeedPage;
import pages.LoginPage;
import utils.ConfigReader;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Task 2: Automate Login.
 */
public class LoginTest extends BaseTest {

    @Test
    @DisplayName("Positive login: valid credentials reach the feed page")
    void validCredentials_logsInSuccessfully() {
        String username = ConfigReader.require("LINKEDIN_EMAIL");
        String password = ConfigReader.require("LINKEDIN_PASSWORD");

        LoginPage loginPage = new LoginPage(driver, wait);
        loginPage.loginAs(username, password);

        FeedPage feedPage = new FeedPage(driver, wait);
        assertTrue(feedPage.isLoaded(),
                "Expected to land on the feed page with the search box visible after a valid login, but got: "
                        + driver.getCurrentUrl());
    }

    @Test
    @DisplayName("Negative login: invalid password shows an error message")
    void invalidPassword_showsErrorMessage() {
        String username = ConfigReader.require("LINKEDIN_EMAIL");

        LoginPage loginPage = new LoginPage(driver, wait);
        loginPage.loginAs(username, "ThisIsDefinitelyWrong123!");

        assertTrue(loginPage.isErrorDisplayed(),
                "Expected an error message to be displayed after logging in with an invalid password.");
    }
}
