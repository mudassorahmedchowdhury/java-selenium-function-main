package locators;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Global {
    @FindBy(xpath="(//a[@class='set_employee_current_location_after_login'][normalize-space()='Default'])[1]")
    private WebElement location;
    public WebElement getLocation() {
        return location;
    }
}
