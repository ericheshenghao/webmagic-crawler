package cn.siques.dao;

import cn.siques.entity.Tag;

import java.util.List;

public interface File_TagDAO {
    void save(Integer fileId, Integer tagId);

    List<Tag> findTagByFileId(Integer id);
}
