package com.delmon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.delmon.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
}
