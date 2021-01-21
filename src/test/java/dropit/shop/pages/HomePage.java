package dropit.shop.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class HomePage extends BasePage {
    public HomePage(WebDriver driver) {
        super(driver);
    }

    //TODO add missed controls

    @FindBy(xpath = "//a[@class='nav-link' and text()='Global Feed']")
    public WebElement globalFeed;

    @FindBy(css = "div.article-preview")
    public List<WebElement> articles;

    @FindBy(css = "li.page-item")
    public List<WebElement> pages;

    @FindBy(css = "a.tag-default.tag-pill")
    public List<WebElement> popularTags;

    public void waitPagination() {
        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("ul.pagination")
                )
        );
    }
}