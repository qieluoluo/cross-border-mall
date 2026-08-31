package com.delmon.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delmon.dto.AddCartDTO;
import com.delmon.dto.CartListDTO;
import com.delmon.result.Result;

public interface CartService {
    Result<Page<CartListDTO>> list(Integer pageNum, Integer pageSize, String username, Long userId);
    Result<Void> delete(Long id);
    Result<Void> addCart(AddCartDTO addCartDTO);

    Result<Void> updateCart(AddCartDTO addCartDTO);
}
