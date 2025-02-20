package pages;

import locators.Global;
import locators.Login;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

public class LoginPage {
    private WebDriver driver;
    private Login loginLocators;
    private Global globalLocators;
    private WebDriverWait wait;


    public LoginPage(WebDriver driver) {
        this.driver = driver;
        loginLocators = new Login();
        globalLocators = new Global();
        PageFactory.initElements(driver, loginLocators);
        PageFactory.initElements(driver, globalLocators);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }


    private void waitForVisibility(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }


    private void waitForClickability(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }


    public void enterPassword(String password) {
        waitForVisibility(loginLocators.getPassword());
        loginLocators.getPassword().clear();
        loginLocators.getPassword().sendKeys(password);
    }


    public void clickLoginButton() {
        waitForClickability(loginLocators.getLoginbutton());
        loginLocators.getLoginbutton().click();
    }


    public void clickDefaultLocation() {
        waitForClickability(globalLocators.getLocation());
        globalLocators.getLocation().click();
    }
}
