package cn.siques.service;

import cn.siques.entity.Tag;

import java.util.List;

public interface TagService {


    void save(Tag tag);

    List<Tag> findTag(String name);
}
