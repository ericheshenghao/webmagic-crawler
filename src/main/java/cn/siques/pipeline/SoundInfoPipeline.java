package cn.siques.pipeline;

import cn.siques.crawler.Auth;
import cn.siques.crawler.Request;
import cn.siques.dao.PageDao;
import cn.siques.entity.File_Detail;
import cn.siques.entity.SoundFile;
import cn.siques.entity.Tag;
import cn.siques.service.File_TagService;
import cn.siques.service.SoundFileService;
import cn.siques.service.TagService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.annotation.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SoundInfoPipeline implements Pipeline {

    @Resource
    SoundFileService soundFileService;

    @Resource
    TagService tagService;

    @Resource
    File_TagService file_tagService;


    @Autowired
    PageDao pageDao;


    @SneakyThrows
    @Override
    public void process(ResultItems resultItems, Task task) {
        // 抽取出的文件数据
        SoundFile soundFile = resultItems.get("soundInfo");

        try{
            int pageNum = resultItems.get("pageNum");
            pageDao.updatePage(pageNum);
        }catch (Exception e){}





        // 认证及下载信息
        File_Detail fileDetail = resultItems.get("download");

        ArrayList<String> taglists = resultItems.get("taglists");

        if(fileDetail!=null){
            // 根据url去查询数据库中是否已经有数据
            List<SoundFile> soundInfo = soundFileService.findSoundInfo(fileDetail.getUrl());
            // 数据库中没有数据才进行下载
            if(soundInfo.size()==0 ){
                // 在下载完成前状态都为false
                try {
                    soundFile.setStatu(false);
                    soundFileService.saveSoundInfo(soundFile);
                }catch(Exception e){
                    e.printStackTrace();
                    System.exit(0);
                };

                // 这是第一次插入的时候
                // 不管下载是否成功，可以先将标签赋值好
                // 这里标签存在则查出id返回，否则新增标签并返回id
                List<Integer> idLists = new ArrayList<>();
                if(taglists!=null){
                    for (String s: taglists
                    ) {
                        Tag tag = new Tag();
                        List<Tag> ExistTag = tagService.findTag(s);
                        if(ExistTag.size()==0){
                            // 新标签
                            tagService.save(tag.setName(s));
                            //  System.out.println("新标签"+tag.getId());
                        }else{
                            // 老标签
                            tag.setId(ExistTag.get(0).getId());
//                            System.out.println("老标签"+ExistTag.get(0).getId());
                        }
                        // 如果是新标签可以拿到id
                        idLists.add(tag.getId());
                    }
                }

                //  如果有文件id 且有标签id列表的情况
                if( soundFile.getId()!=null && idLists.size()!=0){
                    for (int i = 0; i <idLists.size() ; i++) {

                        file_tagService.save(soundFile.getId(), idLists.get(i));
                    }
                }

                // 发起下载请求

                    Request request = new Request();
                String ext = request.download(fileDetail);

                // 下载完成后设置成true
                soundFile.setStatu(true);
                soundFile.setExt("."+ext);
                // 更新状态
                soundFileService.updateInfoByURL(soundFile);


                // 这种情况表明已有数据，但可能此前下载是失败的
            }else if (soundInfo.size()!=0&&soundInfo.get(0).getStatu()==false){

                String oldpath = soundInfo.get(0).getPath();
                String realFilename = soundInfo.get(0).getName()+soundInfo.get(0).getExt();
                String realpath = oldpath+"/"+realFilename;

//                File file = new File(realpath, realFilename);
//                if(file.exists()) file.delete();
                // Endpoint以杭州为例，其它Region请按实际情况填写。
                String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
// 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
                String accessKeyId = "LTAI4GDTDQm93qwQFzu9nz8a";
                String accessKeySecret = "IoTPd2UEd7Sx9i1USc1cfrT44C1VZQ";

                OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
                // 删除原来路径下的文件
                ossClient.deleteObject("mango-sound", realpath);

                ossClient.shutdown();

                //先更新路径状态
                System.out.println("更新时间"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                SoundFile pathChanged = soundInfo.get(0).setPath(fileDetail.getStoragePath());

                soundFileService.updateInfoByURL(pathChanged);

                // 文件下载到新路径
                Request request = new Request();
                String ext = request.download(fileDetail);


                // 下载完成后设置成true
                pathChanged.setStatu(true);
                // 更新下载状态
                pathChanged.setExt(ext);
                soundFileService.updateInfoByURL(pathChanged);
            }
        }

    }
}
