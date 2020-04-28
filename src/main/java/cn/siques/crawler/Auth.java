package cn.siques.crawler;




import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Auth {


    public static String cookie="";


    private static String USERNAME = "秋刀不是鱼"; //账号
    private static String PASSWORD = "heshenghao"; //密码

    public void login(){
//
//        DesiredCapabilities dcaps = new DesiredCapabilities();
//        // ssl证书支持
////        dcaps.setCapability("acceptSslCerts", true);
//        // css搜索支持
//        dcaps.setCapability("cssSelectorsEnabled", true);
////        // js支持
//        dcaps.setJavascriptEnabled(true);
//        // 驱动支持
//        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
//                "D:\\web\\ear-crawler\\src\\main\\resources\\static\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");
//        // // 创建无界面浏览器对象
//        WebDriver driver = new PhantomJSDriver(dcaps);
//
//        try {
//            // 让浏览器访问空间主页
//            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
//            driver.get("https://www.ear0.com/user/login");
//            Thread.sleep(2000l);
//            String html = driver.getPageSource();
//            driver.findElement(By.name("user")).sendKeys(USERNAME);
//            driver.findElement(By.name("pwd")).sendKeys(PASSWORD);
//            driver.findElement(By.xpath("//input[@class='btn w100 mt20 legitRipple']")).click();
//
//
//
//
//            Thread.sleep(1000);
//
//        } catch (Exception e) {

//            e.printStackTrace();
//        } finally {
//            // 关闭并退出浏览器
//            Set<Cookie> cookies = driver.manage().getCookies();
//             Map<String, String> defaultCookies = new LinkedHashMap<String, String>();
//
//            for (Cookie cookie:cookies
//                 ) {
//             defaultCookies.put(cookie.getName().toString(),cookie.getValue().toString());
//            }

           cookie="PHPSESSID=hmejeq7jt5fla713jtop43idp1;";

           log.info("cookie获取成功");

//        }
    }

}
