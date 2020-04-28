package cn.siques.crawler;

import cn.siques.dao.SoundFileDAO;
import cn.siques.entity.SoundFile;
import cn.siques.pipeline.SoundInfoPipeline;
import cn.siques.service.SoundFileService;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientGenerator;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.selector.Selectable;


import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class Crawler implements PageProcessor {

//    private String downloadPath ="D:/upload/";
    private String downloadPath ="/soundFile/";


    private String downloadUrlpre =   "https://www.ear0.com/index.php?app=sound&ac=download&cx=link&soundid=" ;
    private String downloadUrlpro =    "&token=4a19ebd1c9e382451c2ee63d8ad3f6299d454c0d&channel=1";

    static final String URL = "https://www.ear0.com/sound/list/page-1";




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
            List<Selectable> tagNodes = page.getHtml().css("div.tags a").nodes();
            ArrayList<String> taglists = new ArrayList<>();


            //标签
            for (Selectable s: tagNodes
                 ) {
                taglists.add(Jsoup.parse(s.toString()).text());
            }

            page.putField("taglists",taglists);






            // 拿到id
            String storagePath = downloadPath+ new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"/";
            String[] id = page.getUrl().toString().split("-");
            String downloadURL= downloadUrlpre+id[1]+downloadUrlpro;

            String cookie = Auth.cookie;
            soundFile.setName(filename).setExt(ext).setSize(size).setClassification(classification).setDescription(description).setPath(storagePath).setUrl(url);

            List<String> list = new ArrayList<>();
            list.add(downloadURL);
            list.add(cookie);
            list.add(filename+ext);
            list.add(storagePath);
            list.add(url);

            page.putField("download",list);
            page.putField("soundInfo",soundFile);

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

            // 获取下一页的url
            String bkurl = "https://www.ear0.com/sound/list/page-"+String.valueOf(i+1)  ;
            //放到任务队列
            page.addTargetRequest(bkurl);
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

    @Scheduled(initialDelay = 8000,fixedDelay = 100000)
    public void process(){
        Spider.create( new Crawler()).setScheduler
                (new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000))).addPipeline(soundInfoPipeline).addUrl(URL).thread(3).run();


    }


}
