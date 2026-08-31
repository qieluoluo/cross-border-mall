package com.delmon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体
 */
@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long categoryId;
    
    private String name;
    
    private String subTitle;
    
    private String mainImage;
    
    private String detailHtml;
    
    private BigDecimal price;
    
    private Integer stock;
    
    private Integer sales;
    
    private Integer status; // 0下架 1上架
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
