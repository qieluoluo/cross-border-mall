package com.delmon.entity.admin;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("role")
public class Role {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String permissions;
}
