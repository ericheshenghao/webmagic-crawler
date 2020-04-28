package cn.siques.service.impl;

import cn.siques.dao.TagDAO;
import cn.siques.entity.SoundFile;
import cn.siques.entity.Tag;
import cn.siques.service.TagService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TagImpl implements TagService {
    @Resource
    TagDAO tagDAO;
    @Override
    public void save(Tag tag) {

        //去重
        List<Tag> list = findTag(tag.getName());
        if(list.size()==0) {
            tagDAO.save(tag);
        }
    }

    @Override
    public List<Tag> findTag(String name) {
        return tagDAO.findTag(name);
    }
}
