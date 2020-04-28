package cn.siques.dao;

import cn.siques.entity.Tag;

import java.util.List;

public interface TagDAO {
    void save(Tag tag);

    List<Tag> findTag(String name);
}
