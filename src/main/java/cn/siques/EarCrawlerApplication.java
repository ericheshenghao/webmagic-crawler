package cn.siques;

import cn.siques.crawler.Auth;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("cn.siques.dao")
@SpringBootApplication
@EnableScheduling
public class EarCrawlerApplication {


    public static void main(String[] args) {


        SpringApplication.run(EarCrawlerApplication.class,args);

        Auth auth = new Auth();
        auth.login();
//          crawler.login();


    }



}