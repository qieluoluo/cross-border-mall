package com.delmon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delmon.dto.admin.AdminCreateDTO;
import com.delmon.dto.admin.AdminLoginDTO;
import com.delmon.dto.admin.AdminSelectDTO;
import com.delmon.dto.admin.AdminUpdateDTO;
import com.delmon.entity.admin.Admin;
import com.delmon.enums.ResultCode;
import com.delmon.mapper.AdminMapper;
import com.delmon.result.Result;
import com.delmon.service.AdminService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.time.LocalDateTime;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminMapper adminMapper;

    /**
     * 创建管理员
     * @param adminCreateDTO
     * @return
     */
    @Override
    public Result<AdminCreateDTO> createAdmin(AdminCreateDTO adminCreateDTO) {
        // 判断用户名是否重复
        Admin existAdmin = adminMapper.selectOne(
            new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, adminCreateDTO.getUsername())
        );
        
        if (existAdmin != null) {
            return Result.error(ResultCode.DATA_DUPLICATE, "用户名已存在");
        }
        
        Admin admin = new Admin();
        BeanUtils.copyProperties(adminCreateDTO, admin);
        admin.setCreateTime(LocalDateTime.now());
        adminMapper.insert(admin);
        
        return Result.success(adminCreateDTO);
    }
    
    /**
     * 更新管理员信息
     */
    @Override
    public Result<AdminSelectDTO> updateAdmin(AdminUpdateDTO adminUpdateDTO) {
        Admin admin = adminMapper.selectById(adminUpdateDTO.getId());
        if (admin == null) {
            return Result.error(ResultCode.USER_NOT_EXIST, "用户不存在");
        }
        
        BeanUtils.copyProperties(adminUpdateDTO, admin, "id");
        adminMapper.updateById(admin);
        
        AdminSelectDTO adminSelectDTO = new AdminSelectDTO();
        BeanUtils.copyProperties(admin, adminSelectDTO);
        return Result.success(adminSelectDTO);
    }

    @Override
    public Result<AdminSelectDTO> selectAdminById(Long id) {
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            return Result.error(ResultCode.USER_NOT_EXIST, "用户不存在");
        }
        
        AdminSelectDTO adminSelectDTO = new AdminSelectDTO();
        BeanUtils.copyProperties(admin, adminSelectDTO);
        return Result.success(adminSelectDTO);
    }

    /**
     * 分页查询管理员
     */
    @Override
    public Result<Page<AdminSelectDTO>> selectAdminByPage(Integer pageNum, Integer pageSize, String username, Integer status) {
        Page<Admin> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(username)) {
            wrapper.like(Admin::getUsername, username);
        }
        if (status != null) {
            wrapper.eq(Admin::getStatus, status);
        }
        
        Page<Admin> adminPage = adminMapper.selectPage(page, wrapper);
        
        Page<AdminSelectDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(adminPage, resultPage, "records");
        resultPage.setRecords(adminPage.getRecords().stream()
            .map(admin -> {
                AdminSelectDTO dto = new AdminSelectDTO();
                BeanUtils.copyProperties(admin, dto);
                return dto;
            })
            .toList());
        
        return Result.success(resultPage);
    }
    
    /**
     * 登录
     * 根据账号和密码 查询人员
     *
     * @param adminLoginDTO
     * @return
     */
    @Override
    public Result<AdminSelectDTO> loginAdmin(AdminLoginDTO adminLoginDTO) {
        System.out.println("========== 登录开始 ==========");
        System.out.println("用户名: " + adminLoginDTO.getUsername());
        System.out.println("密码: " + adminLoginDTO.getPassword());
        
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>()
                        .eq(Admin::getUsername, adminLoginDTO.getUsername())
                        .eq(Admin::getPassword, adminLoginDTO.getPassword())
        );
        
        if (admin == null) {
            System.out.println("登录失败：用户名或密码错误");
            return Result.error(ResultCode.USER_NOT_EXIST, "用户名或密码错误");
        }
        
        System.out.println("找到用户: " + admin.getUsername());
        System.out.println("用户状态: " + admin.getStatus());
        
        // 检查账号状态
        if (admin.getStatus() != null && admin.getStatus() == 0) {
            System.out.println("登录失败：账号已被禁用");
            return Result.error(ResultCode.USER_DISABLED, "账号已被禁用");
        }
        
        AdminSelectDTO resultDTO = new AdminSelectDTO();
        BeanUtils.copyProperties(admin, resultDTO);
        resultDTO.setPassWord(null); // 不返回密码
        resultDTO.setLastLoginIp(this.getIP());
        resultDTO.setLastLoginTime(LocalDateTime.now());
        
        // 更新登录信息
        admin.setLastLoginIp(resultDTO.getLastLoginIp());
        admin.setLastLoginTime(resultDTO.getLastLoginTime());
        adminMapper.updateById(admin);
        
        return Result.success(resultDTO);
    }
    
    /**
     * 删除管理员
     */
    @Override
    public Result<Void> deleteAdmin(Long id) {
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            return Result.error(ResultCode.USER_NOT_EXIST, "用户不存在");
        }
        
        adminMapper.deleteById(id);
        return Result.success();
    }
    
    /**
     * 更新管理员状态
     */
    @Override
    public Result<Void> updateStatus(Long id, Integer status) {
        System.out.println("========== 更新管理员状态开始 ==========");
        System.out.println("管理员ID: " + id);
        System.out.println("新状态: " + status);
        
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            System.out.println("更新失败：用户不存在");
            return Result.error(ResultCode.USER_NOT_EXIST, "用户不存在");
        }
        
        System.out.println("找到用户: " + admin.getUsername());
        System.out.println("更新前状态: " + admin.getStatus());
        
        admin.setStatus(status);
        int result = adminMapper.updateById(admin);
        
        System.out.println("更新结果: " + (result > 0 ? "成功" : "失败"));
        System.out.println("========== 更新管理员状态结束 ==========");
        
        return Result.success();
    }

    /**
     * 记录登录ip
     */
    public String getIP() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostAddress();
        } catch (Exception ignored) {
            return "unknown";
        }
    }
}
