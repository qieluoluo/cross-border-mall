package com.delmon.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delmon.dto.UserLoginDTO;
import com.delmon.dto.UserRegisterDTO;
import com.delmon.dto.UserUpdateDTO;
import com.delmon.result.Result;
import com.delmon.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid UserRegisterDTO dto) {
        return userService.register(dto);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody @Valid UserLoginDTO dto) {
        return userService.login(dto);
    }


    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    public Result<Object> getUserInfo(@PathVariable("id") Long id) {
        return userService.getUserInfo(id);
    }
    /**
     * 用户列表（分页）
     */
    @GetMapping("/list")
    public Result<Page<Object>> list(
            @RequestParam(name="pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name="pageSize", defaultValue = "10") Integer pageSize) {
        return userService.list(pageNum, pageSize);
    }

    /**
     * 更新用户状态（禁用/启用）
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") Integer status) {
        return userService.updateStatus(id, status);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public Result<Void> updateUser(
            @PathVariable("id") Long id,
            @RequestBody @Valid UserUpdateDTO dto) {
        return userService.updateUser(id, dto);
    }
}
