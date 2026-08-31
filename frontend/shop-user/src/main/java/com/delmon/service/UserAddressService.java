package com.delmon.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delmon.entity.UserAddress;
import com.delmon.result.Result;

import java.util.List;

public interface UserAddressService {
    Result<List<UserAddress>> listByUserId(Long userId);
    Result<UserAddress> getById(Long id);
    Result<Void> add(UserAddress address);
    Result<Void> update(UserAddress address);
    Result<Void> delete(Long id);
    Result<Void> setDefault(Long userId, Long id);
}
