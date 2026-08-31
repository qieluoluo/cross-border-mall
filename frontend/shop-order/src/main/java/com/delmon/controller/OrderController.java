package com.delmon.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delmon.entity.Order;
import com.delmon.result.Result;
import com.delmon.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<Long> create(@RequestBody Map<String, Object> params) {
        return orderService.create(params);
    }
    
    /**
     * 订单详情
     */
    @GetMapping("/{id}")
    public Result<Object> detail(@PathVariable Long id) {
        return orderService.detail(id);
    }
    
    /**
     * 订单列表（支持分页和状态筛选）
     */
    @GetMapping("/list")
    public Result<Page<Object>> list(
            @RequestParam(name="pageNum", defaultValue="1") Integer pageNum,
            @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
            @RequestParam(name="status", required=false) Integer status) {
        return orderService.list(pageNum, pageSize, status);
    }
    
    /**
     * 取消订单
     */
    @PutMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable Long id) {
        return orderService.cancel(id);
    }
    
    /**
     * 更新订单
     */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Order order) {
        return orderService.update(order);
    }
    
    /**
     * 订单发货
     * @param params 包含 orderId, expressNo, expressCompany
     */
    @PostMapping("/delivery")
    public Result<Void> delivery(@RequestBody Map<String, Object> params) {
        Long orderId = Long.valueOf(params.get("orderId").toString());
        String expressNo = params.get("expressNo").toString();
        String expressCompany = params.get("expressCompany").toString();
        return orderService.delivery(orderId, expressNo, expressCompany);
    }
}
