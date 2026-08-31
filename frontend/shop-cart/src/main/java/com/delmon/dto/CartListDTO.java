package com.delmon.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CartListDTO {
    private Long id;
    private Long userId;
    private Long productId;
    private Long skuId;
    private Integer quantity;
    private String userName;
    private String productName;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private String productImage;
    private LocalDateTime createTime;
}
