package com.delmon.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delmon.entity.Order;
import com.delmon.result.Result;
import java.util.Map;

public interface OrderService {
    Result<Long> create(Map<String, Object> params);
    Result<Object> detail(Long id);
    Result<Page<Object>> list(Integer pageNum, Integer pageSize, Integer status);
    Result<Void> cancel(Long id);
    Result<Void> update(Order order);
    /**
     * 订单发货
     * @param orderId 订单ID
     * @param expressNo 快递单号
     * @param expressCompany 快递公司编码
     * @return 发货结果
     */
    Result<Void> delivery(Long orderId, String expressNo, String expressCompany);
}
