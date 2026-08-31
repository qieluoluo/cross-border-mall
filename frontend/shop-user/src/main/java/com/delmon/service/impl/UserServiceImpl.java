package com.delmon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delmon.dto.UserLoginDTO;
import com.delmon.dto.UserRegisterDTO;
import com.delmon.dto.UserUpdateDTO;
import com.delmon.entity.User;
import com.delmon.enums.ResultCode;
import com.delmon.mapper.UserMapper;
import com.delmon.result.Result;
import com.delmon.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public Result<Void> register(UserRegisterDTO dto) {
        // 检查用户名是否已存在
        User existUser = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())
        );

        if (existUser != null) {
            return Result.error(ResultCode.DATA_DUPLICATE, "用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword()); // TODO: 后续添加密码加密
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setStatus(1);

        userMapper.insert(user);
        return Result.success();
    }

    @Override
    public Result<String> login(UserLoginDTO dto) {
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername())
                .eq(User::getPassword, dto.getPassword())
        );

        if (user == null) {
            return Result.error(ResultCode.USER_NOT_EXIST, "用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            return Result.error(ResultCode.USER_DISABLED, "账号已被禁用");
        }

        // TODO: 生成JWT Token
        String token = user.getId() + "_" + System.currentTimeMillis();
        return Result.success(token);
    }

    @Override
    public Result<Object> getUserInfo(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(ResultCode.USER_NOT_EXIST, "用户不存在");
        }
        // 不返回密码
        user.setPassword(null);
        return Result.success(user);
    }

    @Override
    public Result<Page<Object>> list(Integer pageNum, Integer pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        Page<User> userPage = userMapper.selectPage(page, null);

        // 清除密码信息
        userPage.getRecords().forEach(user -> user.setPassword(null));

        return Result.success((Page<Object>) (Page<?>) userPage);
    }

    @Override
    public Result<Void> updateStatus(Long id, Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(ResultCode.USER_NOT_EXIST, "用户不存在");
        }

        user.setStatus(status);
        userMapper.updateById(user);
        return Result.success();
    }

    @Override
    public Result<Void> updateUser(Long id, UserUpdateDTO dto) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(ResultCode.USER_NOT_EXIST, "用户不存在");
        }

        // 更新用户信息，只更新非空的字段
        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }

        userMapper.updateById(user);
        return Result.success();
    }
}
