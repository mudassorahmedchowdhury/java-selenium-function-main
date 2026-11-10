package com.uikit.demo.pages;

import com.uikit.core.component.ButtonClicker;
import com.uikit.core.component.TextInput;
import com.uikit.core.component.UiContext;
import com.uikit.core.locator.ButtonLocator;
import org.openqa.selenium.By;

/** Page object for the demo login form, built entirely from library components. */
public final class LoginPage {
    private final UiContext ctx;
    private final TextInput username;
    private final TextInput password;
    private final ButtonClicker buttons;

    public LoginPage(UiContext ctx) {
        this.ctx = ctx;
        this.username = new TextInput(ctx, By.id("username"));
        this.password = new TextInput(ctx, By.id("password"));
        this.buttons = new ButtonClicker(ctx, ButtonLocator.exactText());
    }

    public LoginPage login(String user, String pass) {
        username.type(user);
        password.type(pass);
        buttons.clickButtonByText("Login");
        return this;
    }

    public String errorMessage() {
        return ctx.waits().visible(By.id("login-error")).getText();
    }
}
