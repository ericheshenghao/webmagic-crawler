package cn.siques.earcrawler;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@SpringBootTest
class CrawlerApplicationTests {

    @Test
    void contextLoads() throws IOException {
        preDownload("https://www.ear0.com/index.php?app=sound&ac=download&cx=link", "PHPSESSID=lr20qpkj7m96erbcno6fpop9j1; volume=75; Hm_lvt_ca7878677a94bf7a293315510fd64fd1=1596883837,1596884389,1596886052,1596893383; cx_userid=81332; cx_code=439e19439f1184249a72aee645264cfb; Hm_lpvt_ca7878677a94bf7a293315510fd64fd1=1596910794");
    }

    public void preDownload(String url, String cookie) throws IOException {
        try {
            CloseableHttpClient client = null;
            CloseableHttpResponse response = null;
            try {
                // 创建一个提交数据的容器
                List<BasicNameValuePair> parames = new ArrayList<>();
                parames.add(new BasicNameValuePair("soundid", "19898"));


                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(parames, "UTF-8"));
                httpPost.setHeader("Cookie",cookie);
                client = HttpClients.createDefault();
                response = client.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);

                String str1= URLDecoder.decode(result, "UTF-8");

                List<String> strs = new ArrayList<String>();
                Pattern p = Pattern.compile("(?<=filetype=).*(?=&time)");

                Matcher m = p.matcher(str1);
                while(m.find()) {
                    strs.add(m.group());
                }

                String ext = strs.get(0);

                System.out.println(ext);
            } finally {
                if (response != null) {
                    response.close();
                }
                if (client != null) {
                    client.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
