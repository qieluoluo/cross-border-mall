package com.delmon.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delmon.dto.AddCartDTO;
import com.delmon.dto.CartListDTO;
import com.delmon.result.Result;
import com.delmon.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/list")
    public Result<Page<CartListDTO>> list(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "100") Integer pageSize,
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "userId", required = false) Long userId) {
        return cartService.list(pageNum, pageSize, username, userId);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        return cartService.delete(id);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteCompat(@PathVariable("id") Long id) {
        return cartService.delete(id);
    }

    @PostMapping("/add")
    public Result<Void> addCart(@RequestBody AddCartDTO addCartDTO) {
        return cartService.addCart(addCartDTO);
    }

    @PostMapping("/update")
    public Result<Void> updateCart(@RequestBody AddCartDTO addCartDTO) {
        return cartService.updateCart(addCartDTO);
    }
}
