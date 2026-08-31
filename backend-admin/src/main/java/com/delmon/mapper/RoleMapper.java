package com.delmon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.delmon.entity.admin.Role;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    
}
