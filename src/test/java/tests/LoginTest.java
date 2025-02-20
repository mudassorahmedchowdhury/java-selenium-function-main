package tests;


import functions.ButtonClicker;
import functions.Sidebar;
import org.junit.Before;
import org.junit.Test;
import pages.LoginPage;

public class LoginTest extends test.Base {
    private LoginPage loginPage;
    private Sidebar sidebar;
    private ButtonClicker buttonClicker;

    @Before
    public void setUp() {
        super.setUp();
        loginPage = new LoginPage(driver);
        sidebar = new Sidebar(driver);
        buttonClicker = new ButtonClicker(driver);
    }

    @Test
    public void testLogin() throws InterruptedException {
        // loginPage.enterUsername("admin");
        // loginPage.enterPassword("pointofsale");
        loginPage.clickLoginButton();
        loginPage.clickDefaultLocation();
        Thread.sleep(3000);

        // Uses ButtonClicker to click a button based on its text
        buttonClicker.clickButtonByText("Connect To Woocommerce");
        Thread.sleep(5000);
        driver.navigate().back();

        // Example of navigating a sidebar to a specific item
        String parentItem = "Inventory";
        String childItem = "Items";
        sidebar.clickItem(parentItem, childItem);
        Thread.sleep(5000);

    }
}
