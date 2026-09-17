package com.delmon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delmon.dto.AddCartDTO;
import com.delmon.dto.CartListDTO;
import com.delmon.entity.Cart;
import com.delmon.enums.ResultCode;
import com.delmon.mapper.CartMapper;
import com.delmon.result.Result;
import com.delmon.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Override
    public Result<Page<CartListDTO>> list(Integer pageNum, Integer pageSize, String username, Long userId) {
        Page<CartListDTO> page = new Page<>(pageNum, pageSize);
        IPage<CartListDTO> cartPage = cartMapper.selectCartPage(page, username, userId);
        if (userId != null && cartPage.getRecords() != null) {
            cartPage.getRecords().removeIf(item -> !userId.equals(item.getUserId()));
        }
        if (cartPage instanceof Page<CartListDTO> pageResult) {
            return Result.success(pageResult);
        }
        Page<CartListDTO> result = new Page<>(cartPage.getCurrent(), cartPage.getSize(), cartPage.getTotal());
        result.setRecords(cartPage.getRecords());
        return Result.success(result);
    }

    @Override
    public Result<Void> delete(Long id) {
        Cart exist = cartMapper.selectById(id);
        if (exist == null) {
            return Result.error(ResultCode.DATA_NOT_EXIST, "购物车项不存在");
        }
        cartMapper.deleteById(id);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> addCart(AddCartDTO addCartDTO) {
        if (addCartDTO.getUserId() == null || addCartDTO.getProductId() == null) {
            return Result.error(ResultCode.VALIDATION_ERROR, "用户ID和商品ID不能为空");
        }

        if (addCartDTO.getQuantity() == null || addCartDTO.getQuantity() <= 0) {
            addCartDTO.setQuantity(1);
        }

        Long productId = addCartDTO.getProductId();
        Long skuId = addCartDTO.getSkuId();
        if (skuId == null || skuId <= 0 || (skuId == 1L && productId != 1L)) {
            skuId = productId;
        }

        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, addCartDTO.getUserId())
                .eq(Cart::getProductId, productId);

        Cart existCart = cartMapper.selectOne(wrapper);
        if (existCart != null) {
            existCart.setQuantity(existCart.getQuantity() + addCartDTO.getQuantity());
            existCart.setSkuId(skuId);
            existCart.setUpdateTime(LocalDateTime.now());
            cartMapper.updateById(existCart);
            return Result.success();
        }

        Cart cart = new Cart();
        cart.setUserId(addCartDTO.getUserId());
        cart.setProductId(productId);
        cart.setSkuId(skuId);
        cart.setQuantity(addCartDTO.getQuantity());
        cart.setSelected(1);
        cart.setCreateTime(LocalDateTime.now());
        cart.setUpdateTime(LocalDateTime.now());
        try {
            cartMapper.insert(cart);
            return Result.success();
        } catch (DuplicateKeyException e) {
            Cart duplicated = cartMapper.selectOne(wrapper);
            if (duplicated == null) {
                return Result.error(ResultCode.BUSINESS_ERROR, "加入购物车失败，请重试");
            }
            duplicated.setQuantity(duplicated.getQuantity() + addCartDTO.getQuantity());
            duplicated.setUpdateTime(LocalDateTime.now());
            cartMapper.updateById(duplicated);
            return Result.success();
        }
    }

    @Override
    @Transactional
    public Result<Void> updateCart(AddCartDTO addCartDTO) {
        if (addCartDTO.getId() == null) {
            return Result.error(ResultCode.VALIDATION_ERROR, "购物车ID不能为空");
        }
        if (addCartDTO.getQuantity() == null || addCartDTO.getQuantity() <= 0) {
            return Result.error(ResultCode.VALIDATION_ERROR, "数量必须大于0");
        }

        Cart existCart = cartMapper.selectById(addCartDTO.getId());
        if (existCart == null) {
            return Result.error(ResultCode.DATA_NOT_EXIST, "购物车项不存在");
        }

        existCart.setQuantity(addCartDTO.getQuantity());
        existCart.setUpdateTime(LocalDateTime.now());
        cartMapper.updateById(existCart);

        return Result.success();
    }
}