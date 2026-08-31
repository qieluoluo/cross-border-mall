package com.delmon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_item")
public class OrderItem {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private String orderNo;

    private Long productId;

    private Long skuId;

    private String skuCode;

    private String productName;

    private String productImage;

    private String specs;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal totalAmount;

    private LocalDateTime createTime;
}
