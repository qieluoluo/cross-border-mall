package com.delmon.controller;

import com.delmon.result.Result;
import com.delmon.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * 统一支付控制器
 * 对外提供所有支付方式的统一入口
 */
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 创建PayPal支付
     * POST /payment/paypal/create
     */
    @PostMapping("/paypal/create")
    public Result<Object> createPayPalPayment(@RequestBody Map<String, Object> params) {
        log.info("创建PayPal支付，参数：{}", params);
        return paymentService.createPayPalPayment(params);
    }

    /**
     * PayPal成功回调
     * GET /payment/paypal/success?token=xxx
     */
    @GetMapping("/paypal/success")
    public Result<Void> paypalSuccess(@RequestParam(name = "token") String token) {
        log.info("PayPal回调，token：{}", token);
        return paymentService.handlePayPalSuccess(token);
    }

    /**
     * 创建Stripe支付Intent
     * POST /payment/stripe/create-intent
     */
    @PostMapping("/stripe/create-intent")
    public Result<Object> createStripeIntent(@RequestBody Map<String, Object> params) {
        log.info("创建Stripe支付，参数：{}", params);
        return paymentService.createStripeIntent(params);
    }

    /**
     * 创建支付宝支付
     * POST /payment/alipay/create
     */
    @PostMapping("/alipay/create")
    public Result<Object> createAlipayPayment(@RequestBody Map<String, Object> params) {
        log.info("创建支付宝支付，参数：{}", params);
        return paymentService.createAlipayPayment(params);
    }

    /**
     * 支付宝异步回调（支付宝服务器调用）
     * POST /payment/alipay/notify
     */
    @PostMapping("/alipay/notify")
    public String handleAlipayNotify(@RequestParam Map<String, String> params) {
        log.info("收到支付宝异步通知，参数：{}", params);
        Result<Object> result = paymentService.handleAlipayNotify(params);
        if (result.isSuccess() && "success".equals(result.getData())) {
            return "success";
        }
        return "failure";
    }

    /**
     * 支付宝同步回调（支付完成跳转）
     * GET /payment/alipay/return
     */
    @GetMapping("/alipay/return")
    public Result<Object> handleAlipayReturn(@RequestParam Map<String, String> params) {
        log.info("收到支付宝同步返回，参数：{}", params);
        return paymentService.handleAlipayReturn(params);
    }

    /**
     * 查询支付宝支付状态
     * GET /payment/alipay/query/{outTradeNo}
     */
    @GetMapping("/alipay/query/{outTradeNo}")
    public Result<String> queryAlipayPayment(@PathVariable String outTradeNo) {
        log.info("查询支付宝支付状态，outTradeNo：{}", outTradeNo);
        return paymentService.queryAlipayPayment(outTradeNo);
    }
}
