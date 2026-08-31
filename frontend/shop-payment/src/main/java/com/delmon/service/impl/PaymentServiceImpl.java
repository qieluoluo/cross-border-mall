package com.delmon.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.delmon.config.AlipayConfig;
import com.delmon.entity.Payment;
import com.delmon.mapper.PaymentMapper;
import com.delmon.result.Result;
import com.delmon.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final AlipayConfig alipayConfig;

    // ==================== PayPal（模拟实现） ====================

    @Override
    @Transactional
    public Result<Object> createPayPalPayment(Map<String, Object> params) {
        try {
            Long orderId = Long.valueOf(params.get("orderId").toString());
            BigDecimal amount = new BigDecimal(params.get("amount").toString());
            String currency = params.getOrDefault("currency", "USD").toString();

            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setPaymentMethod("PAYPAL");
            payment.setAmount(amount);
            payment.setCurrency(currency);
            payment.setStatus("WAITING_PAYMENT");
            payment.setCreateTime(LocalDateTime.now());
            paymentMapper.insert(payment);

            String mockToken = "MOCK_TOKEN_" + System.currentTimeMillis() + "_" + payment.getId();

            log.info("创建PayPal支付成功, paymentId: {}", payment.getId());

            return Result.success(Map.of(
                    "approvalUrl", "https://www.sandbox.paypal.com/checkoutnow?token=" + mockToken,
                    "paymentId", payment.getId(),
                    "token", mockToken
            ));
        } catch (Exception e) {
            log.error("创建PayPal支付失败", e);
            return Result.businessError("创建PayPal支付失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<Void> handlePayPalSuccess(String token) {
        try {
            log.info("处理PayPal成功回调, token: {}", token);
            return Result.success();
        } catch (Exception e) {
            log.error("处理PayPal成功回调失败", e);
            return Result.businessError("处理失败：" + e.getMessage());
        }
    }

    // ==================== Stripe（模拟实现） ====================

    @Override
    @Transactional
    public Result<Object> createStripeIntent(Map<String, Object> params) {
        try {
            Long orderId = Long.valueOf(params.get("orderId").toString());
            BigDecimal amount = new BigDecimal(params.get("amount").toString());

            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setPaymentMethod("STRIPE");
            payment.setAmount(amount);
            payment.setCurrency("USD");
            payment.setStatus("WAITING_PAYMENT");
            payment.setCreateTime(LocalDateTime.now());
            paymentMapper.insert(payment);

            String mockClientSecret = "pi_mock_" + System.currentTimeMillis() + "_" + payment.getId() + "_secret_xxx";

            log.info("创建Stripe支付成功, paymentId: {}", payment.getId());

            return Result.success(Map.of(
                    "clientSecret", mockClientSecret,
                    "paymentId", payment.getId()
            ));
        } catch (Exception e) {
            log.error("创建Stripe支付失败", e);
            return Result.businessError("创建Stripe支付失败：" + e.getMessage());
        }
    }

    // ==================== 支付宝 ====================

    @Override
    @Transactional
    public Result<Object> createAlipayPayment(Map<String, Object> params) {
        try {
            Long orderId = Long.valueOf(params.get("orderId").toString());
            BigDecimal amount = new BigDecimal(params.get("amount").toString());
            String subject = params.getOrDefault("subject", "订单支付-" + orderId).toString();
            String body = params.getOrDefault("body", "订单号：" + orderId).toString();

            // 1. 保存支付记录
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setPaymentMethod("ALIPAY");
            payment.setAmount(amount);
            payment.setCurrency("CNY");
            payment.setStatus("WAITING_PAYMENT");
            payment.setCreateTime(LocalDateTime.now());
            paymentMapper.insert(payment);

            // 2. 生成商户订单号
            String outTradeNo = generateOutTradeNo(orderId, payment.getId());

            // 3. 创建支付宝客户端
            AlipayClient alipayClient = new DefaultAlipayClient(
                    alipayConfig.getGatewayUrl(),
                    alipayConfig.getAppId(),
                    alipayConfig.getPrivateKey(),
                    "json",
                    "UTF-8",
                    alipayConfig.getPublicKey(),
                    "RSA2"
            );

            // 4. 创建支付请求
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setNotifyUrl(alipayConfig.getNotifyUrl());
            request.setReturnUrl(alipayConfig.getReturnUrl());

            // 5. 设置业务参数
            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", outTradeNo);
            bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
            bizContent.put("total_amount", amount.toString());
            bizContent.put("subject", subject);
            bizContent.put("body", body);
            bizContent.put("timeout_express", "30m");
            request.setBizContent(JSONUtil.toJsonStr(bizContent));

            // 6. 调用支付宝接口
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);

            if (response.isSuccess()) {
                payment.setTransactionId(outTradeNo);
                payment.setStatus("CREATED");
                paymentMapper.updateById(payment);

                log.info("创建支付宝支付成功, outTradeNo: {}, paymentId: {}", outTradeNo, payment.getId());

                return Result.success(Map.of(
                        "paymentForm", response.getBody(),
                        "outTradeNo", outTradeNo,
                        "paymentId", payment.getId(),
                        "orderId", orderId
                ));
            } else {
                log.error("创建支付宝支付失败: {}", response.getMsg());
                payment.setStatus("FAILED");
                paymentMapper.updateById(payment);
                return Result.businessError("创建支付失败：" + response.getMsg());
            }
        } catch (Exception e) {
            log.error("创建支付宝支付异常", e);
            return Result.businessError("支付服务异常：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<Object> handleAlipayNotify(Map<String, String> params) {
        try {
            log.info("处理支付宝异步回调，参数：{}", params);

            String tradeStatus = params.get("trade_status");
            String outTradeNo = params.get("out_trade_no");
            String tradeNo = params.get("trade_no");
            String appId = params.get("app_id");

            // 验证app_id
            if (StrUtil.isBlank(appId) || !alipayConfig.getAppId().equals(appId)) {
                log.warn("支付宝app_id不匹配");
                return Result.businessError("failure");
            }

            // 查询支付记录
            Payment payment = findPaymentByOutTradeNo(outTradeNo);
            if (payment == null) {
                log.warn("未找到支付记录，outTradeNo: {}", outTradeNo);
                return Result.businessError("failure");
            }

            // 处理交易状态
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                if ("COMPLETED".equals(payment.getStatus())) {
                    log.info("订单已处理完成，outTradeNo: {}", outTradeNo);
                    return Result.success("success");
                }

                payment.setStatus("COMPLETED");
                payment.setTransactionId(tradeNo);
                payment.setPayTime(LocalDateTime.now());
                payment.setCallbackData(JSONUtil.toJsonStr(params));
                paymentMapper.updateById(payment);

                log.info("支付宝支付成功, outTradeNo: {}, tradeNo: {}", outTradeNo, tradeNo);
                return Result.success("success");
            } else if ("WAIT_BUYER_PAY".equals(tradeStatus)) {
                log.info("支付宝交易等待支付, outTradeNo: {}", outTradeNo);
                return Result.success("success");
            } else if ("TRADE_CLOSED".equals(tradeStatus)) {
                log.info("支付宝交易关闭, outTradeNo: {}", outTradeNo);
                payment.setStatus("CLOSED");
                payment.setCallbackData(JSONUtil.toJsonStr(params));
                paymentMapper.updateById(payment);
                return Result.success("success");
            }

            log.warn("支付宝异步通知状态异常: {}", tradeStatus);
            return Result.businessError("failure");
        } catch (Exception e) {
            log.error("处理支付宝异步回调异常", e);
            return Result.businessError("处理失败：" + e.getMessage());
        }
    }

    @Override
    public Result<Object> handleAlipayReturn(Map<String, String> params) {
        try {
            log.info("处理支付宝同步回调，参数：{}", params);

            String outTradeNo = params.get("out_trade_no");
            String tradeNo = params.get("trade_no");
            String totalAmount = params.get("total_amount");

            if (StrUtil.isBlank(outTradeNo)) {
                return Result.businessError("订单号不能为空");
            }

            Payment payment = findPaymentByOutTradeNo(outTradeNo);
            if (payment == null) {
                return Result.businessError("订单不存在");
            }

            return Result.success(Map.of(
                    "outTradeNo", outTradeNo,
                    "tradeNo", tradeNo,
                    "totalAmount", totalAmount,
                    "status", payment.getStatus(),
                    "message", "支付成功"
            ));
        } catch (Exception e) {
            log.error("处理支付宝同步回调异常", e);
            return Result.businessError("处理失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> queryAlipayPayment(String outTradeNo) {
        try {
            log.info("查询支付宝支付状态，outTradeNo: {}", outTradeNo);

            AlipayClient alipayClient = new DefaultAlipayClient(
                    alipayConfig.getGatewayUrl(),
                    alipayConfig.getAppId(),
                    alipayConfig.getPrivateKey(),
                    "json",
                    "UTF-8",
                    alipayConfig.getPublicKey(),
                    "RSA2"
            );

            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", outTradeNo);
            request.setBizContent(JSONUtil.toJsonStr(bizContent));

            AlipayTradeQueryResponse response = alipayClient.execute(request);

            if (response.isSuccess()) {
                log.info("查询成功，交易状态：{}", response.getTradeStatus());
                return Result.success(response.getTradeStatus());
            } else {
                log.warn("查询失败：{}", response.getMsg());
                return Result.businessError(response.getMsg());
            }
        } catch (AlipayApiException e) {
            log.error("查询支付宝支付状态异常", e);
            return Result.businessError("查询失败：" + e.getMessage());
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 生成商户订单号
     * 格式：yyyyMMddHHmmss + orderId + paymentId
     */
    private String generateOutTradeNo(Long orderId, Long paymentId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        return String.format("%s%d%d", timestamp, orderId, paymentId);
    }

    /**
     * 根据商户订单号查询支付记录
     */
    private Payment findPaymentByOutTradeNo(String outTradeNo) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Payment> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(Payment::getTransactionId, outTradeNo);
        return paymentMapper.selectOne(wrapper);
    }
}
