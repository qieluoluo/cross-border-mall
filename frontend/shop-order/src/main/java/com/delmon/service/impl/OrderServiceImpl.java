package com.delmon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delmon.entity.Order;
import com.delmon.enums.ResultCode;
import com.delmon.mapper.OrderMapper;
import com.delmon.result.Result;
import com.delmon.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${express.service.url:http://localhost:8083}")
    private String expressServiceUrl;

    @Override
    public Result<Long> create(Map<String, Object> params) {
        // 简化版：直接创建订单
        Long userId = Long.valueOf(params.get("userId").toString());
        List<Map<String, Object>> items = (List<Map<String, Object>>) params.get("items");

        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Map<String, Object> item : items) {
            BigDecimal price = new BigDecimal(item.get("price").toString());
            Integer quantity = Integer.valueOf(item.get("quantity").toString());
            totalAmount = totalAmount.add(price.multiply(new BigDecimal(quantity)));
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo("ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + System.currentTimeMillis() % 10000);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setFreightAmount(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setStatus(0); // 待付款
        order.setPayType(2); // 默认支付方式：支付宝

        // 收货地址信息（如果有）
        if (params.containsKey("addressId")) {
            order.setAddressId(Long.valueOf(params.get("addressId").toString()));
        }
        if (params.containsKey("receiverName")) {
            order.setReceiverName(params.get("receiverName").toString());
        }
        if (params.containsKey("receiverPhone")) {
            order.setReceiverPhone(params.get("receiverPhone").toString());
        }
        if (params.containsKey("receiverAddress")) {
            order.setReceiverAddress(params.get("receiverAddress").toString());
        }
        if (params.containsKey("remark")) {
            order.setRemark(params.get("remark").toString());
        }

        orderMapper.insert(order);

        // TODO: 保存订单项、扣减库存、清空购物车

        return Result.success(order.getId());
    }

    @Override
    public Result<Object> detail(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            return Result.error(ResultCode.DATA_NOT_EXIST, "订单不存在");
        }
        return Result.success(order);
    }

    @Override
    public Result<Page<Object>> list(Integer pageNum, Integer pageSize, Integer status) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();

        // 如果指定了状态，按状态筛选
        if (status != null) {
            queryWrapper.eq(Order::getStatus, status);
        }

        // 按创建时间倒序
        queryWrapper.orderByDesc(Order::getCreateTime);

        Page<Order> orderPage = orderMapper.selectPage(page, queryWrapper);
        
        // 转换为前端需要的格式
        Page<Object> resultPage = new Page<>();
        resultPage.setCurrent(orderPage.getCurrent());
        resultPage.setSize(orderPage.getSize());
        resultPage.setTotal(orderPage.getTotal());
        resultPage.setRecords(orderPage.getRecords().stream()
            .map(order -> {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("id", order.getId());
                map.put("userId", order.getUserId());
                map.put("orderNo", order.getOrderNo());
                String userNickname = order.getUserNickname();
                if (userNickname == null || userNickname.isBlank()) {
                    userNickname = orderMapper.selectUserNameByUserId(order.getUserId());
                }
                map.put("userNickname", (userNickname == null || userNickname.isBlank()) ? "-" : userNickname);
                map.put("totalAmount", order.getTotalAmount());
                map.put("payAmount", order.getPayAmount());
                map.put("freightAmount", order.getFreightAmount());
                map.put("discountAmount", order.getDiscountAmount());
                map.put("status", order.getStatus()); // 返回数字状态码，由前端转换为文字
                map.put("payType", order.getPayType());
                map.put("receiverName", order.getReceiverName());
                map.put("receiverPhone", order.getReceiverPhone());
                map.put("receiverAddress", order.getReceiverAddress());
                map.put("remark", order.getRemark());
                map.put("createTime", order.getCreateTime());
                map.put("payTime", order.getPayTime());
                map.put("deliveryTime", order.getDeliveryTime());
                map.put("receiveTime", order.getReceiveTime());
                return (Object) map;
            })
            .collect(java.util.stream.Collectors.toList()));
        
        return Result.success(resultPage);
    }

    @Override
    public Result<Void> cancel(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            return Result.error(ResultCode.DATA_NOT_EXIST, "订单不存在");
        }

        // 只有待付款和待发货的订单可以取消
        if (order.getStatus() != 0 && order.getStatus() != 1) {
            return Result.error(ResultCode.BUSINESS_ERROR, "该订单状态不允许取消");
        }

        order.setStatus(4); // 已取消
        order.setCloseTime(LocalDateTime.now());
        orderMapper.updateById(order);

        return Result.success();
    }

    @Override
    public Result<Void> update(Order order) {
        Order existOrder = orderMapper.selectById(order.getId());
        if (existOrder == null) {
            return Result.error(ResultCode.DATA_NOT_EXIST, "订单不存在");
        }
        orderMapper.updateById(order);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> delivery(Long orderId, String expressNo, String expressCompany) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.error(ResultCode.DATA_NOT_EXIST, "订单不存在");
        }
        
        // 只有待发货状态的订单才能发货
        if (order.getStatus() != 1) {
            return Result.error(ResultCode.BUSINESS_ERROR, "该订单状态不允许发货");
        }
        
        // 更新订单状态为配送中(2)，并设置发货时间
        order.setStatus(2);
        order.setDeliveryTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        // 通过HTTP调用保存快递信息到express服务
        try {
            Map<String, Object> expressParams = new HashMap<>();
            expressParams.put("orderId", orderId);
            expressParams.put("expressNo", expressNo);
            expressParams.put("expressCompany", expressCompany);
            restTemplate.postForObject(expressServiceUrl + "/express/update", expressParams, Object.class);
        } catch (Exception e) {
            log.error("调用快递服务失败: {}", e.getMessage());
            // 订单状态已更新，快递信息保存失败不影响订单状态
        }
        
        return Result.success();
    }
}
