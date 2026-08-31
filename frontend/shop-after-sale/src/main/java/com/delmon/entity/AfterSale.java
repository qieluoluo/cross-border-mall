package com.delmon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("after_sale")
public class AfterSale {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String afterSaleNo;
    private Long orderId;
    private String orderNo;
    private Long orderItemId;
    private Long userId;
    private String userPhone;
    private Integer type;
    private String reason;
    private String description;
    private BigDecimal refundAmount;
    private Integer status;
    private String rejectReason;
    private LocalDateTime applyTime;
    private LocalDateTime handleTime;
    private LocalDateTime completeTime;
}
