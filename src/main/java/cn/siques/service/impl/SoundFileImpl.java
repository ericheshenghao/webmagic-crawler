package cn.siques.service.impl;

import cn.siques.dao.SoundFileDAO;
import cn.siques.entity.SoundFile;
import cn.siques.service.SoundFileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional()
public class SoundFileImpl  implements SoundFileService {
    @Resource
    SoundFileDAO soundFileDAO;


    @Override
    public SoundFile saveSoundInfo(SoundFile soundFile) {


        //去重
        List<SoundFile> list = findSoundInfo(soundFile.getUrl());
        if(list.size()==0){
             soundFileDAO.save(soundFile);

            return  soundFile;
        }


        return soundFile;
    }

    @Override
    public List<SoundFile> findSoundInfo(String url) {
       return soundFileDAO.findSoundInfo(url);
    }

    @Override
    public void updateInfo(SoundFile soundFile) {
            soundFileDAO.updateInfo(soundFile);
    }


}
