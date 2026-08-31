package com.delmon.service;

import com.delmon.dto.admin.RoleDTO;
import com.delmon.entity.admin.Role;
import com.delmon.result.Result;

import java.util.List;

public interface RoleService {
    
    Result<RoleDTO> createRole(RoleDTO roleDTO);
    
    Result<RoleDTO> updateRole(RoleDTO roleDTO);
    
    Result<RoleDTO> selectRoleById(Long id);
    
    Result<List<RoleDTO>> selectAllRoles();
    
    Result<Void> deleteRole(Long id);
}
