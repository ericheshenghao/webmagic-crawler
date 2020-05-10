package cn.siques.crawler;

import cn.siques.entity.SoundFile;
import cn.siques.pipeline.UpdatePipeline;
import org.jsoup.Jsoup;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UpdateCrawler implements PageProcessor {
    static final String URL = "https://www.ear0.com/sound/list/page-1";

    @Override
    public void process(Page page) {
        List<Selectable> nodes = page.getHtml().css("div.midder div.grid8").xpath("//div[@class='soundbox']").nodes();
        // 如果这个条件满足，表示不是列表页面，可以更新数据
        if(nodes.size()==0){
            // 拿到页面的信息
            SoundFile soundFile = new SoundFile();
            String url =page.getUrl().toString();
            String originLocation = page.getHtml().css("a.location").$("a","onclick").toString();
            String cover  = page.getHtml().css("div.cover").$("img","src").toString();

            String location =null;
            if(originLocation!=null){
                List<String> strs = new ArrayList<String>();
                Pattern p = Pattern.compile("(?<=\\().*(?=\\))");
                Matcher m = p.matcher(originLocation);
                while(m.find()) {
                    strs.add(m.group());
                }
                location = strs.get(0);
            }

            soundFile.setLocation(location).setUrl(url).setCover(cover);
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
            if(i<100){
                //放到任务队列
                System.out.println("当前更新已到达第"+i+"页");
                page.addTargetRequest(bkurl);
            }

        }
    }

    private Site site= Site.me()
            .setCharset("utf8")  // 设置编码
            .setTimeOut(10000)   //设置超时时间
            .setRetrySleepTime(3000) //设置重试间隔时间
            .setRetryTimes(3)
            .setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36");

    @Override
    public Site getSite() {
        return site;
    }


    @Resource
    UpdatePipeline updatePipeline;

    @Scheduled(initialDelay = 1000,fixedDelay = 1000*60*60*24*10)
    public void process(){
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        System.out.println("创建更新爬虫: "+now);

        Spider.create( new UpdateCrawler()).setScheduler
                (new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover
                        (100000))).addPipeline(updatePipeline).addUrl(URL).thread(3).run();

    }
}
