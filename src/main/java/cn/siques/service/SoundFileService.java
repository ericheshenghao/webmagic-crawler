package cn.siques.service;

import cn.siques.entity.SoundFile;

import java.util.List;

public interface SoundFileService {
    SoundFile saveSoundInfo(SoundFile soundFile);

    List<SoundFile> findSoundInfo(String url);
     void updateInfoByURL(SoundFile soundFile);
}
