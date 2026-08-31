package com.delmon.controller;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delmon.dto.admin.AdminCreateDTO;
import com.delmon.dto.admin.AdminLoginDTO;
import com.delmon.dto.admin.AdminSelectDTO;
import com.delmon.dto.admin.AdminUpdateDTO;
import com.delmon.entity.admin.Admin;
import com.delmon.enums.ResultCode;
import com.delmon.result.Result;
import com.delmon.service.AdminService;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    AdminService adminService;
    @Autowired
    com.delmon.mapper.AdminMapper adminMapper;
    @Autowired
    com.delmon.service.RoleService roleService;

    /**
     * 创建管理员
     */
    @PostMapping("/create-admin")
    public Result<AdminCreateDTO> createAdmin(@RequestBody @Valid AdminCreateDTO adminCreateDTO){
        return adminService.createAdmin(adminCreateDTO);
    }
    
    /**
     * 更新管理员信息
     */
    @PutMapping("/update-admin")
    public Result<AdminSelectDTO> updateAdmin(@RequestBody @Valid AdminUpdateDTO adminUpdateDTO) {
        return adminService.updateAdmin(adminUpdateDTO);
    }

    /**
     * 根据 id 查询
     */
    @GetMapping("/admin/{id}")
    public Result<AdminSelectDTO> selectAdminById(@PathVariable("id") Long id){
        return adminService.selectAdminById(id);
    }
    
    /**
     * 分页查询管理员
     */
    @GetMapping("/list")
    public Result<Page<AdminSelectDTO>> selectAdminByPage(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "status", required = false) Integer status) {
        return adminService.selectAdminByPage(pageNum, pageSize, username, status);
    }
    
    /**
     * 登录
     */
    @PostMapping("/login-admin")
    public Result<AdminSelectDTO> loginAdmin(@RequestBody @Valid AdminLoginDTO adminLoginDTO){
        try {
            System.out.println("收到登录请求: username=" + adminLoginDTO.getUsername() + ", password=" + adminLoginDTO.getPassword());
            Result<AdminSelectDTO> result = adminService.loginAdmin(adminLoginDTO);
            System.out.println("登录结果: code=" + result.getCode() + ", message=" + result.getMessage());
            return result;
        } catch (Exception e) {
            System.out.println("登录异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error(ResultCode.SYSTEM_ERROR, "登录失败：" + e.getMessage());
        }
    }
    
    /**
     * 测试数据库连接
     */
    @GetMapping("/test-db")
    public Result<Object> testDatabase() {
        try {
            // 查询所有管理员，验证数据库连接
            java.util.List<Admin> admins = adminMapper.selectList(null);
            System.out.println("数据库连接成功，管理员数量: " + admins.size());
            for (Admin admin : admins) {
                System.out.println("管理员: id=" + admin.getId() + ", username=" + admin.getUsername() + ", password=" + admin.getPassword());
            }
            return Result.success(admins);
        } catch (Exception e) {
            System.out.println("数据库连接失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error(ResultCode.SYSTEM_ERROR, "数据库连接失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取角色列表（用于前端下拉框）
     */
    @GetMapping("/roles")
    public Result<java.util.List<com.delmon.dto.admin.RoleDTO>> getRoles() {
        try {
            return roleService.selectAllRoles();
        } catch (Exception e) {
            return Result.error(ResultCode.SYSTEM_ERROR, "获取角色失败");
        }
    }
    
    /**
     * 删除管理员
     */
    @DeleteMapping("/admin/{id}")
    public Result<Void> deleteAdmin(@PathVariable("id") Long id) {
        return adminService.deleteAdmin(id);
    }
    
    /**
     * 更新管理员状态
     */
    @PutMapping("/admin/{id}/status")
    public Result<Void> updateAdminStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") Integer status) {
        return adminService.updateStatus(id, status);
    }
}
