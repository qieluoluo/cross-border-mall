package com.delmon.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delmon.dto.admin.AdminCreateDTO;
import com.delmon.dto.admin.AdminLoginDTO;
import com.delmon.dto.admin.AdminSelectDTO;
import com.delmon.dto.admin.AdminUpdateDTO;
import com.delmon.entity.admin.Admin;
import com.delmon.result.Result;

public interface AdminService {
    Result<AdminCreateDTO> createAdmin(AdminCreateDTO adminCreateDTO);
    
    Result<AdminSelectDTO> updateAdmin(AdminUpdateDTO adminUpdateDTO);
    
    Result<AdminSelectDTO> selectAdminById(Long id);
    
    Result<Page<AdminSelectDTO>> selectAdminByPage(Integer pageNum, Integer pageSize, String username, Integer status);
    
    Result<AdminSelectDTO> loginAdmin(AdminLoginDTO adminLoginDTO);
    
    Result<Void> deleteAdmin(Long id);
    
    Result<Void> updateStatus(Long id, Integer status);
}
