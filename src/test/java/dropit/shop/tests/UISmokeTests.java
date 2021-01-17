package dropit.shop.tests;

import dropit.shop.pages.HomePage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.opentest4j.AssertionFailedError;

public final class UISmokeTests extends BaseTest {
    @Test
    public void testProxifying() {
        //TODO move URL to properties
        driver.get("http://localhost:4100/");

        HomePage homePage = new HomePage(driver);

        homePage.waitPageLoaded();
        homePage.globalFeed.click();
        homePage.waitPagination();

        Assertions.assertAll(() -> {
                    Assertions.assertEquals(10, homePage.articles.size(), "Unexpected articles on the page");
                    Assertions.assertEquals(50, homePage.pages.size(), "Unexpected pages");
                }
        );
        WebElement testTag = homePage.popularTags.stream()
                .filter(t -> "test".equals(t.getText()))
                .findFirst()
                .orElseThrow(() -> new AssertionFailedError("No popular tag 'test' found"));

        testTag.click();

        useProxy = true;
        driver.navigate().refresh();
        homePage.waitPagination();

        Assertions.assertAll(() -> {
                    Assertions.assertEquals(12, homePage.articles.size(), "Unexpected articles on the page");
                    Assertions.assertEquals(2, homePage.pages.size(), "Unexpected pages");
                }
        );
    }
}
