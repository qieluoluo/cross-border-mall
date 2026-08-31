package com.delmon.dto.admin;

import lombok.Data;

@Data
public class AdminLoginDTO {
    
    private String username;
    
    private String password;
    
    private String code; // 验证码（后续扩展）
}
