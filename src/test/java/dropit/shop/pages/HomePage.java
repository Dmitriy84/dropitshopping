package dropit.shop.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends BasePage {
    public HomePage(WebDriver driver) {
        super(driver);
    }

    //TODO add missed controls

    @FindBy(xpath = "//a[@class='nav-link' and text()='Global Feed']")
    public WebElement globalFeed;
}
