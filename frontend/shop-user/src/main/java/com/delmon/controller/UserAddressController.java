package com.delmon.controller;

import com.delmon.entity.UserAddress;
import com.delmon.result.Result;
import com.delmon.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-address")
@RequiredArgsConstructor
public class UserAddressController {
    
    private final UserAddressService userAddressService;
    
    @GetMapping("/list/{userId}")
    public Result<List<UserAddress>> listByUserId(@PathVariable("userId") Long userId) {
        return userAddressService.listByUserId(userId);
    }
    
    @GetMapping("/{id}")
    public Result<UserAddress> getById(@PathVariable("id") Long id) {
        return userAddressService.getById(id);
    }
    
    @PostMapping("/add")
    public Result<Void> add(@RequestBody UserAddress address) {
        return userAddressService.add(address);
    }
    
    @PutMapping("/update")
    public Result<Void> update(@RequestBody UserAddress address) {
        return userAddressService.update(address);
    }
    
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        return userAddressService.delete(id);
    }
    
    @PutMapping("/default/{userId}/{id}")
    public Result<Void> setDefault(@PathVariable("userId") Long userId, @PathVariable("id") Long id) {
        return userAddressService.setDefault(userId, id);
    }
}
