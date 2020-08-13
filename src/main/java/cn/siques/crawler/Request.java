package cn.siques.crawler;

import cn.siques.entity.FileDetail;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Component
public class Request {
    private String preDownLoadLink ="https://www.ear0.com/index.php?app=sound&ac=download&cx=link";

    @Autowired
    Auth auth;

    public String preDownload(String url,String cookie,String id) throws IOException {
        String downloadUrl ="";
        try {
            CloseableHttpClient client = null;
            CloseableHttpResponse response = null;
            try {
                // 创建一个提交数据的容器
                List<BasicNameValuePair> parames = new ArrayList<>();
                parames.add(new BasicNameValuePair("soundid", id));


                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(parames, "UTF-8"));
                httpPost.setHeader("Cookie",cookie);
                client = HttpClients.createDefault();
                response = client.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);

                downloadUrl= URLDecoder.decode(result, "UTF-8");
//                 downloadUrl = URLDecoder.decode(str1);

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


        return downloadUrl;
    }


   public String  download(FileDetail fileDetail) throws IOException, InterruptedException {
       String downloadUrl = preDownload(preDownLoadLink, fileDetail.getCookie(), fileDetail.getId());

       List<String> strs = new ArrayList<String>();
       Pattern p = Pattern.compile("(?<=filetype=).*(?=&time)");

       Matcher m = p.matcher(downloadUrl);
       while(m.find()) {
           strs.add(m.group());
       }
       String ext ="";
        try {
             ext = strs.get(0);
        }catch (Exception e){
            auth.login();
        }


        Thread.sleep(10000);

       HttpGet httpGet = new HttpGet(downloadUrl);
       httpGet.setHeader("Cookie",fileDetail.getCookie());

       CloseableHttpClient client = HttpClients.createDefault();
       CloseableHttpResponse res = client.execute(httpGet);
       HttpEntity entity = res.getEntity();
       InputStream is = entity.getContent();

       // Endpoint以杭州为例，其它Region请按实际情况填写。
       String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
// 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
       String accessKeyId = "LTAI4GDTDQm93qwQFzu9nz8a";
       String accessKeySecret = "IoTPd2UEd7Sx9i1USc1cfrT44C1VZQ";

       OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
       PutObjectRequest putObjectRequest = new PutObjectRequest(
               "mango-sound",fileDetail.getStoragePath()+"/"+fileDetail.getFileName()+"."+ext , is);
       ossClient.putObject(putObjectRequest);


       ossClient.shutdown();
       res.close();
       client.close();
       Thread.sleep(3000);
       return ext;

    }

}
