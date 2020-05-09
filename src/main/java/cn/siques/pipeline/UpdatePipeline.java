package cn.siques.pipeline;

import cn.siques.entity.SoundFile;
import cn.siques.service.SoundFileService;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.annotation.Resource;
import java.util.List;
@Component
public class UpdatePipeline implements Pipeline {
    @Resource
    SoundFileService soundFileService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        SoundFile soundfile = resultItems.get("soundInfo");
        if(soundfile!=null){
            List<SoundFile> soundInfo = soundFileService.findSoundInfo(soundfile.getUrl());

            if(soundInfo.size()!=0 ){
                SoundFile soundFile = soundInfo.get(0).setCover(soundfile.getCover()).setLocation(soundfile.getLocation());
                soundFileService.updateInfoByURL(soundFile);
            }
        }

    }
}
