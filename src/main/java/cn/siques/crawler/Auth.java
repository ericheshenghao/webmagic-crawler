package cn.siques.crawler;





import lombok.extern.slf4j.Slf4j;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.*;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.openqa.selenium.phantomjs.PhantomJSDriverService;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Component
public class Auth {

    private ChromeDriver driver;
    public static String cookie=null;
    public static  String token=null;
    @Value("${driver.dir}")
    public String driverdir;

    private static String USERNAME = "秋刀不是鱼"; //账号
    private static String PASSWORD = "heshenghao"; //密码

    public void login()  {

        DesiredCapabilities dcaps = new DesiredCapabilities();
        // ssl证书支持
//        dcaps.setCapability("acceptSslCerts", true);
        // css搜索支持
        dcaps.setCapability("cssSelectorsEnabled", true);
//        // js支持
        dcaps.setJavascriptEnabled(true);

        System.setProperty("webdriver.chrome.driver",
                driverdir);

        // 驱动支持
//        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
//                "src/main/resources/static/phantomjs-2.1.1-windows/bin/phantomjs.exe");
         // 创建无界面浏览器对象

//         start the proxy
        BrowserMobProxy proxy = new BrowserMobProxyServer();


        proxy.start(0);


        // get the Selenium proxy object
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

//        dcaps.setCapability(CapabilityType.PROXY, seleniumProxy);

        ChromeOptions options = new ChromeOptions();
        options.setAcceptInsecureCerts(true);
        options.setHeadless(true);

        options.setProxy(seleniumProxy);

//        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-infobars");
//// 忽略不可信证书错误。
//        options.addArguments("--ignore-certificate-errors");
//// options.addArguments("--window-size=1920,1080");
//// 启动就最大化
 options.addArguments("--start-maximized");
//// 禁止默认浏览器检查
        options.addArguments("no-default-browser-check");
        options.addArguments("--disable-cache");
        options.addArguments("--disk-cache-size=0");
        options.addArguments("--disable-icon-ntp");
        options.addArguments("--disable-ntp-favicons");
        options.addArguments("--no-sandbox");
// 设置用户代理
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.109 Safari/537.36";
        options.addArguments(userAgent);

        driver = new ChromeDriver(options);

//         driver = new ChromeDriver(dcaps);
//        JavascriptExecutor jse = (JavascriptExecutor) driver;
//        String width = (String) jse.executeScript("download_source($(this), 19898, 'main')");
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.MINUTES);
        driver.manage().timeouts().pageLoadTimeout(60,TimeUnit.SECONDS) ;

        proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT,
                CaptureType.RESPONSE_HEADERS);





        try {
            // 让浏览器访问空间主页
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

            driver.get("https://www.ear0.com/user/login");
            Thread.sleep(2000l);




            driver.findElement(By.name("user")).sendKeys(USERNAME);
            driver.findElement(By.name("pwd")).sendKeys(PASSWORD);
            driver.findElement(By.xpath("//input[@class='btn w100 mt20 legitRipple']")).click();




            Thread.sleep(1000);

        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            // 关闭并退出浏览器
            Cookie phpsessid = driver.manage().getCookieNamed("PHPSESSID");

            cookie=phpsessid.toString();

            log.info("cookie获取成功:"+cookie);

            proxy.newHar();
            driver.get("https://www.ear0.com/sound/show/soundid-19898");
            driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
            driver.findElement(By.xpath("//a[@class='download round-btn']")).click();

            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

            driver.findElement(By.xpath("//a[@class='download_source grid12 btn']")).click();

            driver.executeScript("download_source($(this), 19898, 'main')") ;
            
            Har har = proxy.endHar();
            HarLog harLog = har.getLog();
            List<HarEntry> entries = harLog.getEntries();
            for (HarEntry entry : entries) {
                // 响应的详细信息
                HarResponse harResponse = entry.getResponse();
                //响应状态码
                int status = harResponse.getStatus();

                HarContent harContent = harResponse.getContent();
                // 响应体的长度
                long contenSize = harContent.getSize();

                // 返回内容的MIME类型
                String mimeType = harContent.getMimeType();

                // 已连接服务器的IP地址（DNS解析的结果）[v1.2版本]
                String serverIp = entry.getServerIPAddress();

                // 请求的详细信息。
                HarRequest harRequest = entry.getRequest();
                // 请求地址
                String reqUrl = harRequest.getUrl();
                if(reqUrl.startsWith("https://www.ear0.com/index.php?app=sound&ac=download&cx=link&soundid=")){

                    List<String> strs = new ArrayList<String>();
                    Pattern p = Pattern.compile("(?<=token=).*(?=&)");
                    Matcher m = p.matcher(reqUrl);
                    while(m.find()) {
                        strs.add(m.group());
                    }

                    token=strs.get(0);
                }

                // 有关请求/响应往返的详细时间信息。
                HarTimings harTimings = entry.getTimings();
                // >>>>排队等待网络连接所花费的时间，如果不支持则返回-1
                long blocked = harTimings.getBlocked(TimeUnit.MICROSECONDS);

                // >>>> DNS解析时间，如果不使用当前请求则返回-1
                long dns = harTimings.getDns(TimeUnit.MICROSECONDS);

                // >>>> 创建TCP连接所需的时间，如果不支持则返回-1
                long connect = harTimings.getConnect(TimeUnit.MICROSECONDS);

                // >>>> 向服务器发送HTTP请求所需的时间
                long send = harTimings.getSend(TimeUnit.MICROSECONDS);

                // >>>> 正在等待服务器的响应[等待收到第一个数据包]
                long wait = harTimings.getWait(TimeUnit.MICROSECONDS);

                // >>>> 从服务器（或缓存）读取整个响应所需的时间[接收响应数据总耗时]
                long receive = harTimings.getReceive(TimeUnit.MICROSECONDS);


                // 从发起请求到完成响应的总耗时[阻塞等待耗时+DNS解析耗时+TCP连接耗时+发送HTTP请求耗时+等待HTTP响应耗时+接收HTTP响应包耗时].不包括-1值
                long totalTime = entry.getTime(TimeUnit.MILLISECONDS);

                // SSL/TLS协商所需的时间。如果定义了此字段，则时间也包括在连接字段中（以确保与har
                // 1.1向后兼容）。如果时间不适用于当前请求，请使用-1。
                long ssl = harTimings.getSsl(TimeUnit.MICROSECONDS);
            }

        }
    }





}
