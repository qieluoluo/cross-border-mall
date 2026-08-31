package com.delmon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.delmon.entity.ExpressInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExpressMapper extends BaseMapper<ExpressInfo> {
}