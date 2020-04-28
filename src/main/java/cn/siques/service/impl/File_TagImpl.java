package cn.siques.service.impl;

import cn.siques.dao.File_TagDAO;
import cn.siques.entity.Tag;
import cn.siques.service.File_TagService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class File_TagImpl implements File_TagService {
    @Resource
    File_TagDAO file_tagDAO;

    @Override
    public void save(Integer fileId, Integer tagId) {
          file_tagDAO.save(fileId,tagId);
    }

    @Override
    public List<Tag> findTagByFileId(Integer id) {
        return file_tagDAO.findTagByFileId(id);
    }
}
