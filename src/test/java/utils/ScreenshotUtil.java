package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public final class ScreenshotUtil {

    private static final Path SCREENSHOT_DIR = Paths.get("screenshots");

    private ScreenshotUtil() {
    }

    /**
     * Captures a screenshot and saves it under /screenshots/<testName>_<timestamp>.png.
     * Returns the saved path, or null if the capture failed (never throws,
     * so a screenshot failure never masks the original test failure).
     */
    public static Path capture(WebDriver driver, String testName) {
        try {
            Files.createDirectories(SCREENSHOT_DIR);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String safeName = testName.replaceAll("[^a-zA-Z0-9_-]", "_");
            Path target = SCREENSHOT_DIR.resolve(safeName + "_" + timestamp + ".png");

            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), target);
            return target;
        } catch (IOException | ClassCastException e) {
            System.err.println("Screenshot capture failed for '" + testName + "': " + e.getMessage());
            return null;
        }
    }
}
