package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Some LinkedIn pages have a hidden duplicate of a field/button sitting next
 * to the real one, with the same locator. So instead of just grabbing the
 * first match, this waits until one of the matches is actually visible.
 */
public final class ElementUtils {

    private ElementUtils() {
    }

    public static WebElement waitForFirstVisible(WebDriver driver, WebDriverWait wait, By locator) {
        return wait.until(d -> {
            List<WebElement> candidates = d.findElements(locator);
            for (WebElement candidate : candidates) {
                if (candidate.isDisplayed()) {
                    return candidate;
                }
            }
            return null;
        });
    }
}
