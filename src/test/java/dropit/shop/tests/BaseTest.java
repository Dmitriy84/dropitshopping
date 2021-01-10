package dropit.shop.tests;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.proxy.Whitelist;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class BaseTest {
    protected WebDriver driver;
    protected BrowserMobProxy proxy;

    @BeforeEach
    public void setUp() {
        proxy = new BrowserMobProxyServer();
        proxy.setTrustAllServers(true);
        proxy.start(0);
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

        // put our custom header to each request
        proxy.addRequestFilter((request, contents, messageInfo) -> {
            // request.headers().add("my-test-header", "my-test-value");
            System.out.println(request.headers().entries().toString());
            return null;
        });



        // Setting up Proxy for chrome
        ChromeOptions opts = new ChromeOptions();
        String proxyOption = "--proxy-server=" + seleniumProxy.getHttpProxy();
        opts.addArguments(proxyOption);
        //TODO Implement Win VS Linux support
        System.setProperty("webdriver.chrome.driver", "src/test/resources/webdriver/chromedriver.exe");
        driver = new ChromeDriver(opts);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Driver was instantiated. Quitting..");
        } else {
            System.out.println("Driver was null so nothing to do");
        }
    }
}
