package cn.siques.crawler;

import cn.siques.Exception.FileDownloadFailException;
import cn.siques.service.SoundFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


@Slf4j
public class Request {




   public void  download(String downloadURL, String cookie, String filename, String storagePath) throws IOException {


            URL downloadurl=new URL(downloadURL);
            //建立连接
            URLConnection urlConnection = downloadurl.openConnection();
            //连接对象类型转换
            HttpURLConnection connection=(HttpURLConnection)urlConnection;
            //设定请求方法
            connection.setRequestMethod("GET");

            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Accept-Encoding","gzip, deflate, br");
            connection.setRequestProperty("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64)" +
                    " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36");
            connection.setRequestProperty("Cookie",cookie);
            // 建立实际的连接
            connection.connect();
            



            FileOutputStream os =null;
            InputStream is =null;
//       try {
            // 获取文件输入流

            is = connection.getInputStream();

            if(!new File(storagePath).exists()){
                new File(storagePath).mkdirs();
            }

            os = new FileOutputStream(new File(storagePath, filename));
            // 文件拷贝
       long l = IOUtils.copyLarge(is, os);

       if(l==34){
           log.warn("认证信息已失效");
           System.exit(0);
          throw new FileDownloadFailException("认证信息已失效，下载失败");
       }


            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);

            connection.disconnect();

        }





}
