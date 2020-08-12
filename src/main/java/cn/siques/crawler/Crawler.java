package cn.siques.crawler;

import cn.siques.dao.PageDao;
import cn.siques.dao.SoundFileDAO;
import cn.siques.entity.File_Detail;
import cn.siques.entity.SoundFile;
import cn.siques.entity.SysPage;
import cn.siques.pipeline.SoundInfoPipeline;

import cn.siques.pipeline.UpdatePipeline;
import lombok.SneakyThrows;

import org.jsoup.Jsoup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;

import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.selector.Selectable;


import javax.annotation.Resource;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Crawler implements PageProcessor {

    @Resource
    Auth auth;

//    private String downloadPath ="D:/upload/";
    private String downloadPath ="soundFile/";
//    @Value("${upload.dir}")
//    public String downloadPath;


    static final String URL = "https://www.ear0.com/sound/list/page-";


    private Site site= Site.me()
            .setCharset("utf8")  // 设置编码
            .setTimeOut(10000)   //设置超时时间
            .setRetrySleepTime(3000) //设置重试间隔时间
            .setRetryTimes(3)
            .setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36");


    @SneakyThrows
    @Override
    public void process(Page page) {
        List<Selectable> nodes = page.getHtml().css("div.midder div.grid8").xpath("//div[@class='soundbox']").nodes();
        // 如果这个条件满足，表示不是列表页面，可以爬取数据并下载
        if(nodes.size()==0){
            // 拿到页面的信息
            SoundFile soundFile = new SoundFile();


            String filename = Jsoup.parse(page.getHtml().css("div.title").toString()).text();
            if(filename.contains("/")){
                filename = filename.replace("/","");
            }
            String ext = Jsoup.parse(page.getHtml().css("table.table-striped").xpath("//tbody/tr[1]/td[2]").toString()).text();
            String[] e = ext.split("/");
            if(e.length>1){
                ext="."+e[0];
            }else{
                ext="."+ext;
            }

            String url =page.getUrl().toString();
            String size = Jsoup.parse(page.getHtml().css("table.table-striped").xpath("//tbody/tr[2]/td[2]").toString()).text();
            String classification= Jsoup.parse(page.getHtml().css("span.type a").toString()).text();
            String description = Jsoup.parse(page.getHtml().css("div.sound_content").toString()).text();
            String originLocation = page.getHtml().css("a.location").$("a","onclick").toString();



            String cover  = page.getHtml().css("div.cover").$("img","src").toString();
            // 上传时间
            String uploadTime = Jsoup.parse(page.getHtml().css("span.upload_time").css("span.time").toString()).text();


            String location =null;
            if(originLocation!=null){
                String locationName = Jsoup.parse(page.getHtml().css("a.location").toString()).text();
//                System.out.println(locationName);
                List<String> strs = new ArrayList<String>();
                Pattern p = Pattern.compile("(?<=\\().*(?=\\))");
                Matcher m = p.matcher(originLocation);
                while(m.find()) {
                    strs.add(m.group());
                }

                 location = strs.get(0);
                List<Selectable> tagNodes = page.getHtml().css("div.tags a").nodes();
                ArrayList<String> taglists = new ArrayList<>();

                //标签
                for (Selectable s: tagNodes
                ) {
                    taglists.add(Jsoup.parse(s.toString()).text());
                }
                page.putField("taglists",taglists);


                // 拿到id
                String storagePath = downloadPath+ new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                String[] id = page.getUrl().toString().split("-");


                String cookie = Auth.cookie;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date parse = simpleDateFormat.parse(uploadTime);
                soundFile.setUploadTime(parse).setName(filename).setExt(ext).setCover(cover).setSize(size).setClassification(classification).setDescription(description).setPath(storagePath).setUrl(url).setLocation(location+","+locationName);

                File_Detail file_detail = new File_Detail();
                file_detail.setId(id[1])
                        .setCookie(cookie)
                        .setFileName(filename)

                        .setStoragePath(storagePath).setUrl(url);



                page.putField("download",file_detail);
                page.putField("soundInfo",soundFile);
            }


        }else{

            // 表示是列表页
            for (Selectable s:nodes
                 ) {
                // 跳转到详情页面
                String links = s.xpath("//div[@class='soundbox']/div[@class='box_right']/div[@class='float_btn']/a[3]").links().toString();
//                System.out.println(links);
                page.addTargetRequest(links);   
            }

            String[] split = page.getUrl().toString().split("-");

            int i = Integer.parseInt(split[1]);

            page.putField("pageNum",i);

            // 获取下一页的url
            String bkurl = "https://www.ear0.com/sound/list/page-"+String.valueOf(i+1)  ;

            // 如果页码大于多少页，停止爬取直到下次任务启动
            if(i<1000){
                //放到任务队列

                System.out.println("当前已到达第"+i+"页");
                page.addTargetRequest(bkurl);
            }

        }

    }

    @Override
    public Site getSite() {
//        for (Cookie cookie :cookies
//             ) {
//            site.addCookie(cookie.getName().toString(),cookie.getValue().toString());
//        }
        return site;
    }


    @Resource
    SoundInfoPipeline soundInfoPipeline;


    @Autowired
    PageDao pageDao;

    @Scheduled(initialDelay = 5000,fixedDelay = 1000*60*60*12)
//    @Scheduled(initialDelay = 1000,fixedDelay = 1000)
    public void process(){
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("获取认证信息: "+now);



        auth.login();

        SysPage page = pageDao.findPage();
        Long page1 = page.getPage();

        String now1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("创建爬虫: "+now1);
        Spider.create( new Crawler()).setScheduler
                (new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000))).addPipeline(soundInfoPipeline).addUrl(URL+page1).thread(2).run();
    }


}
