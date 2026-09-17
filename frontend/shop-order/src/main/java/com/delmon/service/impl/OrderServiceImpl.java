package com.delmon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delmon.entity.Order;
import com.delmon.entity.OrderItem;
import com.delmon.enums.ResultCode;
import com.delmon.mapper.OrderItemMapper;
import com.delmon.mapper.OrderMapper;
import com.delmon.result.Result;
import com.delmon.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${express.service.url:http://localhost:8083}")
    private String expressServiceUrl;

    @Override
    @Transactional
    public Result<Long> create(Map<String, Object> params) {
        if (params == null || params.get("userId") == null) {
            return Result.validationError("用户ID不能为空");
        }

        Long userId = toLong(params.get("userId"), null);
        if (userId == null) {
            return Result.validationError("用户ID不能为空");
        }

        List<Map<String, Object>> items = extractItems(params.get("items"));
        if (items.isEmpty()) {
            return Result.validationError("订单商品不能为空");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Map<String, Object> item : items) {
            BigDecimal price = toDecimal(item.get("price"), BigDecimal.ZERO);
            Integer quantity = toInt(item.get("quantity"), 1);
            totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(quantity)));
        }
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            BigDecimal payloadTotal = toDecimal(params.get("totalAmount"), BigDecimal.ZERO);
            if (payloadTotal.compareTo(BigDecimal.ZERO) > 0) {
                totalAmount = payloadTotal;
            }
        }

        Order order = new Order();
        order.setOrderNo("ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(toDecimal(params.get("payAmount"), totalAmount));
        order.setFreightAmount(toDecimal(params.get("freightAmount"), BigDecimal.ZERO));
        order.setDiscountAmount(toDecimal(params.get("discountAmount"), BigDecimal.ZERO));
        order.setStatus(0);
        order.setPayType(2);
        order.setAddressId(toLong(params.get("addressId"), 0L));
        order.setReceiverName(clip(params.get("receiverName"), 50, null));
        order.setReceiverPhone(clipPhone(params.get("receiverPhone")));
        order.setReceiverAddress(clip(params.get("receiverAddress"), 500, null));
        order.setRemark(clip(params.get("remark"), 500, null));
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        try {
            orderMapper.insert(order);
            saveOrderItems(order, items);
            return Result.success(order.getId());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("创建订单失败: {}", e.getMessage(), e);
            return Result.error(ResultCode.SYSTEM_ERROR, "创建订单失败，请稍后重试");
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractItems(Object raw) {
        if (raw instanceof List<?> list) {
            return list.stream()
                    .filter(Map.class::isInstance)
                    .map(item -> (Map<String, Object>) item)
                    .toList();
        }
        return List.of();
    }

    private void saveOrderItems(Order order, List<Map<String, Object>> items) {
        for (Map<String, Object> item : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setOrderNo(order.getOrderNo());
            Long productId = toLong(firstNonNull(item.get("productId"), item.get("id")), 0L);
            Long skuId = toLong(item.get("skuId"), productId);
            if (skuId == null || skuId <= 0) {
                skuId = productId == null || productId <= 0 ? 1L : productId;
            }
            orderItem.setProductId(productId == null ? 0L : productId);
            orderItem.setSkuId(skuId);
            orderItem.setProductName(clip(firstNonNull(item.get("productName"), item.get("name")), 200, "商品"));
            Object image = firstNonNull(item.get("productImage"), item.get("mainImage"), item.get("image"));
            orderItem.setProductImage(clip(image, 500, ""));
            orderItem.setSpecs(clip(item.get("specs"), 255, "默认规格"));
            BigDecimal price = toDecimal(item.get("price"), BigDecimal.ZERO);
            Integer quantity = toInt(item.get("quantity"), 1);
            orderItem.setPrice(price);
            orderItem.setQuantity(quantity);
            orderItem.setTotalAmount(price.multiply(BigDecimal.valueOf(quantity)));
            orderItem.setCreateTime(LocalDateTime.now());
            orderItemMapper.insert(orderItem);
        }
    }

    private Object firstNonNull(Object... values) {
        if (values == null) {
            return null;
        }
        for (Object value : values) {
            if (value != null && !value.toString().isBlank()) {
                return value;
            }
        }
        return null;
    }

    private Long toLong(Object value, Long fallback) {
        if (value == null || value.toString().isBlank()) {
            return fallback;
        }
        try {
            return new BigDecimal(value.toString().trim()).longValue();
        } catch (Exception e) {
            return fallback;
        }
    }

    private Integer toInt(Object value, Integer fallback) {
        if (value == null || value.toString().isBlank()) {
            return fallback;
        }
        try {
            return new BigDecimal(value.toString().trim()).intValue();
        } catch (Exception e) {
            return fallback;
        }
    }

    private BigDecimal toDecimal(Object value, BigDecimal fallback) {
        if (value == null) {
            return fallback;
        }
        String text = value.toString().trim().replace("¥", "").replace(",", "");
        if (text.isEmpty()) {
            return fallback;
        }
        try {
            return new BigDecimal(text);
        } catch (Exception e) {
            return fallback;
        }
    }

    private String clip(Object value, int max, String fallback) {
        if (value == null) {
            return fallback;
        }
        String text = value instanceof Map || value instanceof List ? String.valueOf(value) : value.toString().trim();
        if (text.isEmpty()) {
            return fallback;
        }
        return text.length() <= max ? text : text.substring(0, max);
    }

    private String clipPhone(Object value) {
        String phone = clip(value, 32, null);
        if (phone == null) {
            return null;
        }
        String digits = phone.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return clip(phone, 11, null);
        }
        return digits.length() <= 11 ? digits : digits.substring(digits.length() - 11);
    }

    private List<OrderItem> listOrderItems(Long orderId) {
        return orderItemMapper.selectList(
            new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId)
        );
    }

    @Override
    public Result<Object> detail(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            return Result.error(ResultCode.DATA_NOT_EXIST, "订单不存在");
        }
        Map<String, Object> detail = toOrderMap(order);
        detail.put("items", listOrderItems(id));
        return Result.success(detail);
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
                Map<String, Object> map = toOrderMap(order);
                map.put("items", listOrderItems(order.getId()));
                return (Object) map;
            })
            .collect(java.util.stream.Collectors.toList()));
        
        return Result.success(resultPage);
    }

    private Map<String, Object> toOrderMap(Order order) {
        Map<String, Object> map = new HashMap<>();
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
        map.put("status", order.getStatus());
        map.put("payType", order.getPayType());
        map.put("receiverName", order.getReceiverName());
        map.put("receiverPhone", order.getReceiverPhone());
        map.put("receiverAddress", order.getReceiverAddress());
        map.put("remark", order.getRemark());
        map.put("createTime", order.getCreateTime());
        map.put("payTime", order.getPayTime());
        map.put("deliveryTime", order.getDeliveryTime());
        map.put("receiveTime", order.getReceiveTime());
        return map;
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
