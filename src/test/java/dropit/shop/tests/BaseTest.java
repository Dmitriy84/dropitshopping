package dropit.shop.tests;


import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;

public class BaseTest {
    final static String URL_TEMPLATE_TO_REPLACE = "https://conduit.productionready.io/api/articles?limit=%s&offset=0";

    protected WebDriver driver;
    protected BrowserMobProxy proxy;

    protected Boolean useProxy;

    @BeforeEach
    public void setUp() {
        proxy = new BrowserMobProxyServer();
        proxy.setTrustAllServers(true);
        proxy.start(0);
        System.out.println("PROXY PORT: " + proxy.getPort());
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

        proxy.addRequestFilter((request, contents, messageInfo) -> {
            if (!useProxy)
                return null;

            String uri = request.getUri();
            System.out.println("PROXY: " + uri);

            if (!uri.equals(String.format(URL_TEMPLATE_TO_REPLACE, 10))) {
                request.setUri(String.format(URL_TEMPLATE_TO_REPLACE, 12));
            }

            return null;
        });

        // Setting up Proxy for chrome
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments(
                "--proxy-server=" + seleniumProxy.getHttpProxy(),
                "--no-sandbox"
        );
        opts.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        //TODO Implement Win VS Linux support
        System.setProperty("webdriver.chrome.driver", "src/test/resources/webdriver/chromedriver.exe");
        driver = new ChromeDriver(opts);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null)
            driver.quit();
    }
}
