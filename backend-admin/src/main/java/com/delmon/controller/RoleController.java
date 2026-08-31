package com.delmon.controller;

import com.delmon.dto.admin.RoleDTO;
import com.delmon.result.Result;
import com.delmon.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/role")
public class RoleController {
    
    @Autowired
    private RoleService roleService;
    
    /**
     * 创建角色
     */
    @PostMapping("/create")
    public Result<RoleDTO> createRole(@RequestBody @Valid RoleDTO roleDTO) {
        return roleService.createRole(roleDTO);
    }
    
    /**
     * 更新角色
     */
    @PutMapping("/update")
    public Result<RoleDTO> updateRole(@RequestBody @Valid RoleDTO roleDTO) {
        return roleService.updateRole(roleDTO);
    }
    
    /**
     * 根据 ID 查询角色
     */
    @GetMapping("/{id}")
    public Result<RoleDTO> selectRoleById(@PathVariable("id") Long id) {
        return roleService.selectRoleById(id);
    }
    
    /**
     * 查询所有角色
     */
    @GetMapping("/list")
    public Result<List<RoleDTO>> selectAllRoles() {
        return roleService.selectAllRoles();
    }
    
    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@PathVariable("id") Long id) {
        return roleService.deleteRole(id);
    }
}
