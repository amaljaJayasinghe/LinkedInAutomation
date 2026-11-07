package base;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;

import java.util.Optional;

import utils.ScreenshotUtil;

/**
 * Takes a screenshot automatically whenever a test fails.
 *
 * Note: this has to run BEFORE @AfterEach closes the browser, so we use
 * AfterTestExecutionCallback instead of TestWatcher (TestWatcher fires too
 * late - after the driver is already quit, so there's nothing left to capture).
 */
public class ScreenshotOnFailureExtension implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) {
        Optional<Throwable> failure = context.getExecutionException();
        if (failure.isEmpty()) {
            return;
        }
        Optional<Object> testInstance = context.getTestInstance();
        if (testInstance.isPresent() && testInstance.get() instanceof BaseTest baseTest) {
            WebDriver driver = baseTest.getDriver();
            if (driver != null) {
                ScreenshotUtil.capture(driver, context.getDisplayName());
            }
        }
    }
}
