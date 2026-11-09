package functions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import locators.FunctionsLocators;

public class ButtonClicker {
    private static final int TIMEOUT_SECONDS = 10;
    private static final int POLLING_SECONDS = 2;
    private final WebDriver driver;
    private final WebDriverWait wait;

    public ButtonClicker(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver instance cannot be null");
        }
        this.driver = driver;
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(TIMEOUT_SECONDS),
                Duration.ofSeconds(POLLING_SECONDS));
    }

    public ButtonClicker clickButtonByText(String buttonText) {
        if (buttonText == null || buttonText.trim().isEmpty()) {
            throw new IllegalArgumentException("Button text cannot be null or empty");
        }
        WebElement button = findButton(buttonText);
        if (button != null) {
            click(button, buttonText);
            return this;
        }
        throw new RuntimeException("Button with text '" + buttonText + "' not found.");
    }

    private WebElement findButton(String buttonText) {
        String xpath = FunctionsLocators.generateButtonXpath(buttonText);
        List<WebElement> buttons = driver.findElements(By.xpath(xpath));

        if (buttons.isEmpty()) return null;
        if (buttons.size() > 1) {
            throw new RuntimeException("Multiple buttons found with text: " + buttonText);
        }
        return buttons.get(0);
    }

    public ButtonClicker clickButtonInModal(String buttonText) {
        if (buttonText == null || buttonText.trim().isEmpty()) {
            throw new IllegalArgumentException("Button text cannot be null or empty");
        }
        WebElement modal = findActiveModal();
        if (modal != null) {
            String xpath = FunctionsLocators.generateButtonXpath(buttonText);
            try {
                WebElement button = modal.findElement(By.xpath(xpath));
                click(button, "Modal - " + buttonText);
                return this;
            } catch (Exception e) {
                throw new RuntimeException("Button '" + buttonText + "' not found in modal.", e);
            }
        }
        throw new RuntimeException("No active modal found.");
    }

    public ButtonClicker clickButtonInIframe(String iframeId, String buttonText) {
        if (iframeId == null || iframeId.trim().isEmpty()) {
            throw new IllegalArgumentException("IFrame ID cannot be null or empty");
        }
        if (buttonText == null || buttonText.trim().isEmpty()) {
            throw new IllegalArgumentException("Button text cannot be null or empty");
        }

        try {
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iframeId));
            WebElement button = findButton(buttonText);
            if (button != null) {
                click(button, "iFrame - " + buttonText);
                return this;
            }
            throw new RuntimeException("Button '" + buttonText + "' not found in iframe.");
        } finally {
            driver.switchTo().defaultContent();
        }
    }

    private void click(WebElement element, String description) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception e) {
            throw new RuntimeException("Failed to click element: " + description, e);
        }
    }

    private WebElement findActiveModal() {
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath(FunctionsLocators.getActiveModalXpath())));
        } catch (Exception e) {
            return null;
        }
    }
}
