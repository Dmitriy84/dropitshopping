package dropit.shop.tests;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.brotli.dec.BrotliInputStream;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

//TODO replace System.out.println with log4j

public class BaseTest {
    final static String URL_TEMPLATE_TO_REPLACE = "/api/articles?limit=%s&offset=0";

    protected WebDriver driver;
    protected BrowserMobProxy proxy;

    protected Boolean useProxy;

    @BeforeEach
    public void setUp() {
        proxy = new BrowserMobProxyServer();
        proxy.setTrustAllServers(true);
        proxy.start(8081);
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

        proxy.addRequestFilter((request, contents, messageInfo) -> {
            String uri = request.getUri();

            if (!useProxy || !uri.startsWith("/api/articles"))
                return null;

            System.out.println("PROXY ORIGINAL URI: " + uri);
            String replaced = uri.replaceAll("limit=\\d+", "limit=12");
            System.out.println("PROXY REPLACED URI: " + replaced);
            request.setUri(replaced);

            return null;
        });

        proxy.addResponseFilter((response, contents, messageInfo) -> {
            String uri = messageInfo.getUrl();

            if (!useProxy || !uri.contains("/api/articles"))
                return;

            System.out.println("PROXY RESPONSE URI: " + uri);

            JSONObject json = null;
            try (ByteArrayInputStream source = new ByteArrayInputStream(contents.getBinaryContents());
                 BrotliInputStream brotliInputStream = new BrotliInputStream(source);
                 InputStreamReader inputStreamReader = new InputStreamReader(brotliInputStream);
                 BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
            ) {
                String contentText = bufferedReader
                        .lines()
                        .collect(Collectors.joining(System.lineSeparator()));

                System.out.println("PROXY ORIGINAL RESPONSE: " + contentText);

                json = new JSONObject(contentText);
                json.remove("articlesCount");
                json.put("articlesCount", 12);

                System.out.println("PROXY REPLACED RESPONSE: " + json);

            } catch (IOException e) {
                System.out.println("Something went wrong with getting response body. Details:");
                e.printStackTrace();
            }

            byte[] b = json.toString().getBytes(StandardCharsets.UTF_8);

            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                bos.writeBytes(b);
                CompressorOutputStream cos = new CompressorStreamFactory()
                        .createCompressorOutputStream(CompressorStreamFactory.BROTLI, bos);
                contents.setBinaryContents(bos.toByteArray());
            } catch (CompressorException | IOException e) {
                System.out.println("Something went wrong with setting response body. Details:");
                e.printStackTrace();
            }
        });

        // Setting up Proxy for chrome
        // TODO multi-browsers support
        ChromeOptions opts = new ChromeOptions();
        opts.setAcceptInsecureCerts(true);
        opts.addArguments("--proxy-server=" + seleniumProxy.getHttpProxy());
        opts.addArguments("--disable-web-security");
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
