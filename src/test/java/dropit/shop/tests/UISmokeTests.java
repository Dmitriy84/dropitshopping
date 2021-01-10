package dropit.shop.tests;

import dropit.shop.pages.HomePage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

public final class UISmokeTests extends BaseTest {
    @Test
    public void testProxifying() {
        //TODO move URL to properties
        proxy.newHar("google.com.ua");
        driver.get("http://www.google.com.ua");
        driver.get("http://localhost:4100/");

        HomePage homePage = new HomePage(driver);

        homePage.waitPageLoaded();
        homePage.globalFeed.click();


        Assertions.assertEquals(driver.findElement(By.xpath("//body")).getText(), "{\"SUCCESS\"}", "unexpected page body on 'Your Feed' page");
    }
}
