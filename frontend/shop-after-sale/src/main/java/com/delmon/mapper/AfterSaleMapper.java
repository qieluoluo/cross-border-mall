package com.delmon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.delmon.entity.AfterSale;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AfterSaleMapper extends BaseMapper<AfterSale> {
    @Select("SELECT COALESCE(NULLIF(nickname, ''), username) FROM db_user.user WHERE id = #{userId}")
    String selectUserNameByUserId(@Param("userId") Long userId);
}
