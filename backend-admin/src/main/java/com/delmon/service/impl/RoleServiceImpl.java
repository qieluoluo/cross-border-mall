package com.delmon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.delmon.dto.admin.RoleDTO;
import com.delmon.entity.admin.Role;
import com.delmon.enums.ResultCode;
import com.delmon.mapper.RoleMapper;
import com.delmon.result.Result;
import com.delmon.service.RoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Override
    public Result<RoleDTO> createRole(RoleDTO roleDTO) {
        // 检查角色名是否重复
        Role existRole = roleMapper.selectOne(
            new LambdaQueryWrapper<Role>().eq(Role::getName, roleDTO.getName())
        );
        
        if (existRole != null) {
            return Result.error(ResultCode.DATA_DUPLICATE, "角色名称已存在");
        }
        
        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);
        role.setPermissions("[]"); // 默认空权限
        
        roleMapper.insert(role);
        
        roleDTO.setId(role.getId());
        return Result.success(roleDTO);
    }
    
    @Override
    public Result<RoleDTO> updateRole(RoleDTO roleDTO) {
        Role role = roleMapper.selectById(roleDTO.getId());
        if (role == null) {
            return Result.error(ResultCode.ROLE_NOT_EXIST, "角色不存在");
        }
        
        // 检查新角色名是否与其他角色重复
        Role existRole = roleMapper.selectOne(
            new LambdaQueryWrapper<Role>()
                .eq(Role::getName, roleDTO.getName())
                .ne(Role::getId, roleDTO.getId())
        );
        
        if (existRole != null) {
            return Result.error(ResultCode.DATA_DUPLICATE, "角色名称已存在");
        }
        
        BeanUtils.copyProperties(roleDTO, role, "id");
        
        roleMapper.updateById(role);
        return Result.success(roleDTO);
    }
    
    @Override
    public Result<RoleDTO> selectRoleById(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            return Result.error(ResultCode.ROLE_NOT_EXIST, "角色不存在");
        }
        
        RoleDTO roleDTO = new RoleDTO();
        BeanUtils.copyProperties(role, roleDTO);
        return Result.success(roleDTO);
    }
    
    @Override
    public Result<List<RoleDTO>> selectAllRoles() {
        List<Role> roles = roleMapper.selectList(null);
        List<RoleDTO> roleDTOList = roles.stream()
            .map(role -> {
                RoleDTO roleDTO = new RoleDTO();
                BeanUtils.copyProperties(role, roleDTO);
                return roleDTO;
            })
            .collect(Collectors.toList());
        
        return Result.success(roleDTOList);
    }
    
    @Override
    public Result<Void> deleteRole(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            return Result.error(ResultCode.ROLE_NOT_EXIST, "角色不存在");
        }
        
        roleMapper.deleteById(id);
        return Result.success();
    }
}
