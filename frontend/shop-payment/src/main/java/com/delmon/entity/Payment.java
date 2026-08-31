package com.delmon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment")
public class Payment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private String paymentMethod;

    private BigDecimal amount;

    private String currency;

    private String status;

    private String transactionId;

    private LocalDateTime createTime;

    private LocalDateTime payTime;

    // 添加这个字段 - 存储回调数据
    private String callbackData;

    // 如果有其他字段也一并加上
    private String notifyUrl;

    private String returnUrl;
}
