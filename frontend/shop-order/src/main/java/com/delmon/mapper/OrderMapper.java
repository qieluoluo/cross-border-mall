package com.delmon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.delmon.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    @Select("SELECT COALESCE(NULLIF(nickname, ''), username) FROM db_user.user WHERE id = #{userId}")
    String selectUserNameByUserId(@Param("userId") Long userId);
}


