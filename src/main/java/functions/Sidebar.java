package functions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;
import locators.FunctionsLocators;

public class Sidebar {
    private WebDriver driver;
    private WebDriverWait wait;

    public Sidebar(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickItem(String parentName) {
        clickItem(parentName, null);
    }

    public void clickItem(String parentName, String childName) {
        if (parentName == null || parentName.isEmpty()) {
            throw new IllegalArgumentException("Parent name cannot be null or empty");
        }

        // Wait for the parent item to be visible and click on it
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(String.format(FunctionsLocators.PARENT_ITEM_XPATH, parentName))));

        List<WebElement> parents = driver.findElements(By.xpath(String.format(FunctionsLocators.PARENT_ITEM_XPATH, parentName)));
        boolean parentClicked = false;

        System.out.println("Found " + parents.size() + " parent items.");

        for (WebElement parent : parents) {
            String text = parent.getText().trim();
            System.out.println("Found parent with text: '" + text + "'");

            if (text.equals(parentName)) {
                parent.click();
                parentClicked = true;
                System.out.println("Clicked on parent: " + parentName);

                // If childName is provided (not null), wait for and click the child
                if (childName != null && !childName.isEmpty()) {
                    new WebDriverWait(driver, Duration.ofSeconds(10))
                            .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(FunctionsLocators.CHILD_ITEM_XPATH)));
                    List<WebElement> children = parent.findElements(By.xpath(FunctionsLocators.CHILD_ITEM_XPATH)); // Now check the children under the parent
                    boolean childClicked = false;

                    for (WebElement child : children) {
                        String childText = child.getText().trim();
                        if (childText.equals(childName)) {
                            child.click();
                            childClicked = true;
                            System.out.println("Clicked on child: " + childName);
                            break;
                        }
                    }

                    if (!childClicked) {
                        System.err.println("Child item '" + childName + "' not found under parent '" + parentName + "'.");
                    }
                }
                break;
            }
        }

        if (!parentClicked) {
            System.err.println("Parent item '" + parentName + "' not found.");
        }
    }
}

