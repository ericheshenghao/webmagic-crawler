package cn.siques;

import cn.siques.crawler.Auth;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("cn.siques.dao")
@SpringBootApplication
@EnableScheduling
public class CrawlerApplication {


    public static void main(String[] args) {


        SpringApplication.run(CrawlerApplication.class,args);


//          crawler.login();


    }



}