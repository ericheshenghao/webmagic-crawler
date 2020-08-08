package cn.siques.dao;

import cn.siques.entity.SysPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface PageDao {

    @Select({"SELECT * FROM `sys_page`"})
    SysPage findPage();

    @Update({"UPDATE `sys_page` SET `page`=#{num} WHERE `id`=1"})
    void updatePage(@Param("num") Integer num);
}


