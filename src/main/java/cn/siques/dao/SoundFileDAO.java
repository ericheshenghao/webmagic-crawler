package cn.siques.dao;

import cn.siques.entity.SoundFile;

import java.util.List;

public interface SoundFileDAO {
    int save(SoundFile soundFile);

    List<SoundFile> findSoundInfo(String url);

    void updateInfo(SoundFile soundFile);
}
