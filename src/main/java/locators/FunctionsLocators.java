package locators;

public class FunctionsLocators {

    // XPath locator for parent items
    public static final String PARENT_ITEM_XPATH = "//ul[contains(@class,'menu-parent')]//span[normalize-space(text()) = '%s']/..";

    // XPath locator for child items under a specific parent
    public static final String CHILD_ITEM_XPATH = ".//ul/li";

    // XPath to find the button by text (case-insensitive)
    public static String generateButtonXpath(String buttonText) {
        String lowerButtonText = buttonText.toLowerCase(); // Case-insensitive
        return "//button[(" +
                "contains(@type, 'button') or " +
                "contains(@type, 'submit') or " +
                "contains(@type, 'reset') or " +
                "contains(@aria-label, '" + lowerButtonText + "') or " +  // Handle aria-label
                "contains(@title, '" + lowerButtonText + "') or " +  // Handle title attribute
                "contains(text(), '" + lowerButtonText + "') or " +  // Text inside button
                "translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '" + lowerButtonText + "')" +  // Handle case-insensitive text matching
                "]";
    }

    // XPath to find active modal
    public static String getActiveModalXpath() {
        return "//div[contains(@class, 'modal') and contains(@class, 'show')]";
    }
}
