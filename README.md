# Automation Toolkit: Dynamic Button Clicking and Sidebar Navigation with Java - Selenium 

This project provides a flexible and reusable solution for automating sidebar navigation and button clicks in web applications using Selenium WebDriver. The `Sidebar` class dynamically locates and interacts with parent and child sidebar items, and the `ButtonClicker` class provides convenient methods for interacting with buttons, both within modals and iframes.

## Features

- **Dynamic Sidebar Navigation:** Easily interact with parent and child items in the sidebar.
- **Minimal Locators:** Uses flexible XPath locators to handle sidebar items.
- **Error Handling:** Proper checks and error handling to ensure robustness.
- **Reusable Classes:** Can be used across different modules without needing new locators.
- **Button Click Handling:** Convenient methods to click buttons by their text, including buttons in modals and iframes.

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
    - [Sidebar Navigation](#sidebar-navigation)
    - [Button Click Handling](#button-click-handling)
- [Code Walkthrough](#code-walkthrough)
    - [Sidebar Class](#sidebar-class)
    - [ButtonClicker Class](#buttonclicker-class)
- [Functionality Overview](#functionality-overview)
- [Credits](#credits)

## Installation

1. Clone this repository to your local machine.
2. Ensure you have the following dependencies in your `pom.xml` or build file:
    - `selenium-java`
    - `JUnit` (or any other testing framework)
    - A WebDriver executable (e.g., `chromedriver` for Chrome)
3. Import the classes into your project.

```xml
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.0.0</version>
</dependency>
```

## Usage
### Sidebar Navigation
#### Step 1: Initialize the Sidebar Class
First, instantiate the `Sidebar` class by passing the `WebDriver` instance to the constructor. This allows the `Sidebar` class to interact with the browser.
```java
WebDriver driver = new ChromeDriver();
Sidebar sidebar = new Sidebar(driver);
```
#### Step 2: Interact with Sidebar Items
##### Click on a Parent Item Only
If you need to click only a parent module, such as "Dashboard", simply call the clickItem() method with the parent name.
```java
sidebar.clickItem("Dashboard");
```
##### Click on a Parent and a Child Item
To click on a child item within a parent module (e.g., "Settings" under "Admin"), provide both the parent and child names.
```java
sidebar.clickItem("Admin", "Settings");
```

### Button Click Handling
The `ButtonClicker` class provides functionality to click buttons, both on the main page and within `modals` or `iframes`.
#### Step 1: Initialize the ButtonClicker Class
First, create an instance of the `ButtonClicker` class by passing the `WebDriver` instance.
```java
ButtonClicker buttonClicker = new ButtonClicker(driver);
```

#### Step 2: Click Buttons by Text
##### Click a Button by Text
Use the `clickButtonByText()` method to click a button by its visible text:

```java
buttonClicker.clickButtonByText("Submit");
```
#### Click a Button Inside a Modal
If you need to click a button inside an active modal, you can use the `clickButtonInModal()` method:

```java
buttonClicker.clickButtonInModal("Close");
```

#### Click a Button Inside an Iframe
To click a button inside an `iframe`, use the `clickButtonInIframe()` method:

```java
buttonClicker.clickButtonInIframe("iframe-id", "Save");
```
### Code Walkthrough
#### Sidebar Class
The Sidebar class provides the functionality to interact with both parent and child items in the sidebar.

```java
public class Sidebar {
private WebDriver driver;
private WebDriverWait wait;

    public Sidebar(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
}
```

#### Click Item Method
The `clickItem()` method accepts the parent name and optionally the child name, allowing flexible interactions with the sidebar.

```java
public void clickItem(String parentName, String childName) {
List<WebElement> parents = driver.findElements(By.xpath(FunctionsLocators.PARENT_ITEM_XPATH));
    for (WebElement parent : parents) {
        if (parent.getText().trim().equals(parentName)) {
            parent.click();
            if (childName != null && !childName.isEmpty()) {
                List<WebElement> children = parent.findElements(By.xpath(FunctionsLocators.CHILD_ITEM_XPATH));
                for (WebElement child : children) {
                    if (child.getText().trim().equals(childName)) {
                        child.click();
                        break;
                    }
                }
            }
            break;
        }
    }
}
```
#### ButtonClicker Class
The `ButtonClicker` class provides methods to click buttons on the main page, within modals, and iframes. Here's the structure:

```java
public class ButtonClicker {
private WebDriver driver;
private WebDriverWait wait;

    public ButtonClicker(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
}
```
#### Click Button Methods
The `clickButtonByText()` method is used to click a button by its visible text, while `clickButtonInModal()` and `clickButtonInIframe()` help interact with buttons inside modals and iframes.

```java
public ButtonClicker clickButtonByText(String buttonText) {
WebElement button = findButton(buttonText);
if (button != null) {
button.click();
}
return this;
}

public ButtonClicker clickButtonInModal(String buttonText) {
WebElement modal = findActiveModal();
if (modal != null) {
WebElement button = modal.findElement(By.xpath(FunctionsLocators.generateButtonXpath(buttonText)));
button.click();
}
return this;
}

public ButtonClicker clickButtonInIframe(String iframeId, String buttonText) {
driver.switchTo().frame(iframeId);
WebElement button = findButton(buttonText);
if (button != null) {
button.click();
}
driver.switchTo().defaultContent();
return this;
}
```
### Functionality Overview
#### Sidebar Class
- **clickItem(String parentName):** Clicks the specified parent item.
- **clickItem(String parentName, String childName):** Clicks both the specified parent and child items.
ButtonClicker Class
- **clickButtonByText(String buttonText):** Clicks a button by its visible text.
- **clickButtonInModal(String buttonText):** Clicks a button inside a modal.
- **clickButtonInIframe(String iframeId, String buttonText):** 

## Credits
This solution was developed by [Mudassor Ahmed Chowdhury](https://www.linkedin.com/in/mudassor/) – feel free to connect on LinkedIn!







