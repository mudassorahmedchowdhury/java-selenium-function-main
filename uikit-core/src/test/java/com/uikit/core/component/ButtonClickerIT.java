package com.uikit.core.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.uikit.core.config.WaitConfig;
import com.uikit.core.exception.AmbiguousElementException;
import com.uikit.core.exception.ClickFailedException;
import com.uikit.core.exception.ElementNotFoundException;
import com.uikit.core.locator.ButtonLocator;
import com.uikit.core.support.ChromeFixtureSupport;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

class ButtonClickerIT extends ChromeFixtureSupport {
    private UiContext ctx;

    @BeforeEach
    void load() {
        open("buttons.html");
        ctx = UiContext.of(driver).withWaitConfig(WaitConfig.of(Duration.ofMillis(800), Duration.ofMillis(50)));
    }

    private String result() {
        return driver.findElement(By.id("result")).getText();
    }

    @Test
    void clicksButtonByExactText() {
        new ButtonClicker(ctx, ButtonLocator.exactText()).clickButtonByText("Save");
        assertEquals("save", result());
    }

    @Test
    void clicksInputSubmitByValue() {
        new ButtonClicker(ctx, ButtonLocator.exactText()).clickButtonByText("Send");
        assertEquals("send", result());
    }

    @Test
    void handlesQuotesAndNormalizesWhitespace() {
        ButtonClicker c = new ButtonClicker(ctx, ButtonLocator.exactText());
        c.clickButtonByText("Say \"hi\" it's");
        assertEquals("quote", result());
        c.clickButtonByText("Mixed Case");
        assertEquals("cased", result());
    }

    @Test
    void caseInsensitiveStrategyIsOptIn() {
        assertThrows(ElementNotFoundException.class,
                () -> new ButtonClicker(ctx, ButtonLocator.exactText()).clickButtonByText("save"));
        new ButtonClicker(ctx, ButtonLocator.exactTextIgnoreCase()).clickButtonByText("save");
        assertEquals("save", result());
    }

    @Test
    void customLocatorStrategyWorks() {
        ButtonLocator byId = ButtonLocator.of(label -> By.id(label));
        new ButtonClicker(ctx, byId).clickButtonByText("a2");
        assertEquals("dup2", result());
    }

    @Test
    void missingAndAmbiguousButtonsThrowTypedExceptions() {
        ButtonClicker c = new ButtonClicker(ctx, ButtonLocator.exactText());
        assertThrows(ElementNotFoundException.class, () -> c.clickButtonByText("Nope"));
        assertThrows(AmbiguousElementException.class, () -> c.clickButtonByText("Duplicate"));
    }

    @Test
    void blankLabelIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonClicker(ctx, ButtonLocator.exactText()).clickButtonByText(" "));
    }

    @Test
    void clicksInsideCallerDefinedContainer() {
        ButtonClicker c = new ButtonClicker(ctx, ButtonLocator.exactText());
        c.clickButtonInContainer(By.cssSelector(".any-dialog"), "OK");
        assertEquals("dialog-ok", result());
    }

    @Test
    void hiddenContainerIsNotFound() {
        assertThrows(ElementNotFoundException.class,
                () -> new ButtonClicker(ctx, ButtonLocator.exactText())
                        .clickButtonInContainer(By.id("hidden-dialog"), "OK"));
    }

    @Test
    void clicksInsideIframeAndRestoresDefaultContent() {
        new ButtonClicker(ctx, ButtonLocator.exactText()).clickButtonInFrame(By.id("frame"), "Confirm");
        assertEquals("frame-ok", result());
    }

    @Test
    void coveredButtonFailsNativelyButSucceedsWithOptInJsFallback() {
        assertThrows(ClickFailedException.class,
                () -> new ButtonClicker(ctx, ButtonLocator.exactText()).clickButtonByText("Covered"));
        new ButtonClicker(ctx.withJsClickFallback(), ButtonLocator.exactText()).clickButtonByText("Covered");
        assertEquals("covered", result());
    }
}
