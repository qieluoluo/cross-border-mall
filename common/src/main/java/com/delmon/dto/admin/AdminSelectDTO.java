package com.delmon.dto.admin;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminSelectDTO {
    private Long id;

    private String username;

    private String realName;

    private String passWord;

    private Integer status; // 0禁用 1启用

    private Long roleId;

    private String lastLoginIp;

    private LocalDateTime lastLoginTime;

    private LocalDateTime createTime;

}
