package com.delmon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@TableName("`order`")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    
    private Long userId;
    
    private String userPhone;
    
    private String userNickname;
    
    private Long addressId;
    
    private String receiverName;
    
    private String receiverPhone;
    
    private String receiverAddress;
    
    private BigDecimal totalAmount;
    
    private BigDecimal freightAmount;
    
    private BigDecimal payAmount;
    
    private BigDecimal discountAmount;
    
    private Integer status; // 0待付款 1待发货 2待收货 3已完成 4已取消 5退款中 6已退款
    
    private Integer payType; // 1微信 2支付宝
    
    private LocalDateTime payTime;
    
    private LocalDateTime deliveryTime;
    
    private LocalDateTime receiveTime;
    
    private LocalDateTime closeTime;
    
    private String remark;
    
    @TableField(exist = false)
    private String expressNo;
    
    @TableField(exist = false)
    private String expressCompany;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
