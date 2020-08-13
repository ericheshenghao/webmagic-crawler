package cn.siques.service.impl;

import cn.siques.dao.FileTagDAO;
import cn.siques.entity.Tag;
import cn.siques.service.FileTagService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class FileTagImpl implements FileTagService {
    @Resource
    FileTagDAO file_tagDAO;

    @Override
    public void save(Integer fileId, Integer tagId) {
          file_tagDAO.save(fileId,tagId);
    }

    @Override
    public List<Tag> findTagByFileId(Integer id) {
        return file_tagDAO.findTagByFileId(id);
    }
}
