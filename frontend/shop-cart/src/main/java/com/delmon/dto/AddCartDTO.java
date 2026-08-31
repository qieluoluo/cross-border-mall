package com.delmon.dto;

import lombok.Data;

@Data
public class AddCartDTO {
    private Long id;           // 购物车记录ID（更新时使用）
    private Long userId;       // 用户ID
    private Long productId;    // 商品ID
    private Long skuId;        // SKU ID
    private Integer quantity;  // 数量
}