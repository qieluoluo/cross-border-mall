package com.delmon.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delmon.dto.UserLoginDTO;
import com.delmon.dto.UserRegisterDTO;
import com.delmon.dto.UserUpdateDTO;
import com.delmon.result.Result;

public interface UserService {
    Result<Void> register(UserRegisterDTO dto);
    Result<String> login(UserLoginDTO dto);
    Result<Object> getUserInfo(Long id);
    Result<Page<Object>> list(Integer pageNum, Integer pageSize);
    Result<Void> updateStatus(Long id, Integer status);
    Result<Void> updateUser(Long id, UserUpdateDTO dto);
}
