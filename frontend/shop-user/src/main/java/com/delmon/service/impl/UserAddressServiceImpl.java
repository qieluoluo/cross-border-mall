package com.delmon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.delmon.entity.UserAddress;
import com.delmon.enums.ResultCode;
import com.delmon.mapper.UserAddressMapper;
import com.delmon.result.Result;
import com.delmon.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressMapper userAddressMapper;

    @Override
    public Result<List<UserAddress>> listByUserId(Long userId) {
        List<UserAddress> list = userAddressMapper.selectList(
            new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId)
                .orderByDesc(UserAddress::getIsDefault)
                .orderByDesc(UserAddress::getCreateTime)
        );
        return Result.success(list);
    }

    @Override
    public Result<UserAddress> getById(Long id) {
        UserAddress address = userAddressMapper.selectById(id);
        if (address == null) {
            return Result.error(ResultCode.VALIDATION_ERROR,"地址不正确");
        }
        return Result.success(address);
    }

    @Override
    @Transactional
    public Result<Void> add(UserAddress address) {
        // 如果是默认地址，先取消其他地址的默认状态
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            userAddressMapper.update(null,
                new LambdaUpdateWrapper<UserAddress>()
                    .eq(UserAddress::getUserId, address.getUserId())
                    .set(UserAddress::getIsDefault, 0)
            );
        }
        address.setCreateTime(java.time.LocalDateTime.now());
        address.setUpdateTime(java.time.LocalDateTime.now());
        userAddressMapper.insert(address);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> update(UserAddress address) {
        // 如果是默认地址，先取消其他地址的默认状态
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            userAddressMapper.update(null,
                new LambdaUpdateWrapper<UserAddress>()
                    .eq(UserAddress::getUserId, address.getUserId())
                    .ne(UserAddress::getId, address.getId())
                    .set(UserAddress::getIsDefault, 0)
            );
        }
        address.setUpdateTime(java.time.LocalDateTime.now());
        userAddressMapper.updateById(address);
        return Result.success();
    }

    @Override
    public Result<Void> delete(Long id) {
        userAddressMapper.deleteById(id);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> setDefault(Long userId, Long id) {
        // 先取消其他地址的默认状态
        userAddressMapper.update(null,
            new LambdaUpdateWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId)
                .set(UserAddress::getIsDefault, 0)
        );
        // 设置当前地址为默认
        userAddressMapper.update(null,
            new LambdaUpdateWrapper<UserAddress>()
                .eq(UserAddress::getId, id)
                .set(UserAddress::getIsDefault, 1)
        );
        return Result.success();
    }
}
