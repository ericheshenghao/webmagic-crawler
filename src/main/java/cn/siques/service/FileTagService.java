package cn.siques.service;

import cn.siques.entity.Tag;

import java.util.List;

public interface FileTagService {
        void save(Integer fileId,Integer tagId);
        List<Tag> findTagByFileId(Integer id);
}
