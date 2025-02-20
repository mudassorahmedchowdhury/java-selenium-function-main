package locators;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Login {
    @FindBy(xpath = "(//input[@id='username'])[1]")
    private WebElement username;
    public WebElement getUsername() {return username;}

    @FindBy(xpath = "(//input[@id='password'])[1]")
    private WebElement password;
    public WebElement getPassword() {return password;}

    @FindBy(xpath = "(//button[normalize-space()='Login'])[1]")
    private WebElement loginbutton;
    public WebElement getLoginbutton(){return loginbutton;}

}
