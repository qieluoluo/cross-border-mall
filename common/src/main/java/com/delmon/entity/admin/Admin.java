package com.delmon.entity.admin;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("admin")
public class Admin {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String realName;

    private Integer status; // 0禁用 1启用

    private Long roleId;

    private String lastLoginIp;

    private LocalDateTime lastLoginTime;
    //@TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
