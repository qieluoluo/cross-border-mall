package com.delmon.service;

import com.delmon.result.Result;
import java.util.Map;

/**
 * 支付服务接口
 */
public interface PaymentService {

    /**
     * 创建PayPal支付
     * @param params 支付参数（orderId, amount, currency）
     * @return 支付链接
     */
    Result<Object> createPayPalPayment(Map<String, Object> params);

    /**
     * 处理PayPal成功回调
     * @param token PayPal返回的token
     * @return 处理结果
     */
    Result<Void> handlePayPalSuccess(String token);

    /**
     * 创建Stripe支付Intent
     * @param params 支付参数（orderId, amount）
     * @return clientSecret
     */
    Result<Object> createStripeIntent(Map<String, Object> params);

    /**
     * 创建支付宝支付
     * @param params 支付参数（orderId, amount, subject, body）
     * @return 支付表单/支付链接
     */
    Result<Object> createAlipayPayment(Map<String, Object> params);

    /**
     * 处理支付宝异步回调
     * @param params 回调参数
     * @return 处理结果（success/failure）
     */
    Result<Object> handleAlipayNotify(Map<String, String> params);

    /**
     * 处理支付宝同步回调
     * @param params 回调参数
     * @return 支付结果
     */
    Result<Object> handleAlipayReturn(Map<String, String> params);

    /**
     * 查询支付宝支付状态
     * @param outTradeNo 商户订单号
     * @return 支付状态
     */
    Result<String> queryAlipayPayment(String outTradeNo);
}
