package cn.siques.pipeline;

import cn.siques.crawler.Auth;
import cn.siques.crawler.Request;
import cn.siques.entity.SoundFile;
import cn.siques.entity.Tag;
import cn.siques.service.File_TagService;
import cn.siques.service.SoundFileService;
import cn.siques.service.TagService;
import lombok.SneakyThrows;
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



    @SneakyThrows
    @Override
    public void process(ResultItems resultItems, Task task) {
        // 抽取出的文件数据
        SoundFile soundFile = resultItems.get("soundInfo");

        // 认证及下载信息
        List<String> list = resultItems.get("download");

        ArrayList<String> taglists = resultItems.get("taglists");

        if(list!=null){
            // 根据url去查询数据库中是否已经有数据
            List<SoundFile> soundInfo = soundFileService.findSoundInfo(list.get(4));
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
                    request.download(list.get(0),list.get(1),list.get(2),list.get(3));

                // 下载完成后设置成true
                soundFile.setStatu(true);
                // 更新状态
                soundFileService.updateInfo(soundFile);


                // 这种情况表明已有数据，但可能此前下载是失败的
            }else if (soundInfo.size()!=0&&soundInfo.get(0).getStatu()==false){
                // 删除原来路径下的文件
                String oldpath = soundInfo.get(0).getPath();
                String realFilename = soundInfo.get(0).getName()+soundInfo.get(0).getExt();
                String realpath = oldpath+realFilename;
                File file = new File(realpath, realFilename);
                if(file.exists()) file.delete();

                //先更新路径状态
                System.out.println("更新路径"+list.get(3));
                System.out.println("更新时间"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                SoundFile pathChanged = soundInfo.get(0).setPath(list.get(3));
                soundFileService.updateInfo(pathChanged);

                // 文件下载到新路径
                Request request = new Request();
                request.download(list.get(0),list.get(1),list.get(2),list.get(3));


                // 下载完成后设置成true
                pathChanged.setStatu(true);
                // 更新下载状态
                soundFileService.updateInfo(pathChanged);
            }
        }

    }
}
