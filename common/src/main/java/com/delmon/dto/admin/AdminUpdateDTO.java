package com.delmon.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminUpdateDTO {
    
    @NotBlank(message = "ID 不能为空")
    private String id;
    
    @Size(max = 50, message = "真实姓名长度不能超过 50")
    private String realName;
    
    @Size(min = 6, max = 20, message = "密码长度必须在 6-20 之间")
    private String password;
    
    private Integer status; // 0 禁用 1 启用
    
    private Long roleId;
}
