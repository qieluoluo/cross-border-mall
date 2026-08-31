package com.delmon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("express_info")
public class ExpressInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long orderId;
    
    private String expressNo;
    
    private String expressCompany;
    
    private String status;
    
    private String latestTime;
    
    private String latestStatus;
    
    private String detailJson;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}