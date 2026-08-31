package com.delmon;

import com.delmon.dto.admin.AdminCreateDTO;
import com.delmon.dto.admin.AdminLoginDTO;
import com.delmon.result.Result;
import com.delmon.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AdminTest {
    @Autowired
    AdminService adminService;

    @Test
    public void createAdminTest(){
        AdminCreateDTO adminCreateDTO = new AdminCreateDTO();
        adminCreateDTO.setUsername("test_admin");
        adminCreateDTO.setPassword("123456");
        adminCreateDTO.setRealName("测试管理员");
        adminCreateDTO.setRoleId(1L);
        adminCreateDTO.setStatus(1);
        
        Result<AdminCreateDTO> result = adminService.createAdmin(adminCreateDTO);
        System.out.println("创建结果：" + result.getCode() + " - " + result.getMessage());
    }
    
    @Test
    public void loginAdminTest() {
        AdminLoginDTO adminLoginDTO = new AdminLoginDTO();
        adminLoginDTO.setUsername("admin");
        adminLoginDTO.setPassword("admin123");
        
        try {
            Result<com.delmon.dto.admin.AdminSelectDTO> result = adminService.loginAdmin(adminLoginDTO);
            System.out.println("登录结果：" + result.getCode() + " - " + result.getMessage());
            if (result.getData() != null) {
                System.out.println("用户名：" + result.getData().getUsername());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
