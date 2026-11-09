package com.uikit.demo.pages;

import com.uikit.core.component.ButtonClicker;
import com.uikit.core.component.UiContext;
import com.uikit.core.locator.ButtonLocator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Page object for the demo login form. Text inputs are plain Selenium (waiting via the shared
 * context); the button uses the library's {@link ButtonClicker}.
 */
public final class LoginPage {
    private final UiContext ctx;
    private final ButtonClicker buttons;

    public LoginPage(UiContext ctx) {
        this.ctx = ctx;
        this.buttons = new ButtonClicker(ctx, ButtonLocator.exactText());
    }

    public LoginPage login(String user, String password) {
        type(By.id("username"), user);
        type(By.id("password"), password);
        buttons.clickButtonByText("Login");
        return this;
    }

    public String errorMessage() {
        return ctx.waits().visible(By.id("login-error")).getText();
    }

    private void type(By locator, String text) {
        WebElement input = ctx.waits().clickable(locator);
        input.clear();
        input.sendKeys(text);
    }
}
