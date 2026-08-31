package com.delmon.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {
    private String appId;
    private String privateKey;
    private String publicKey;
    private String gatewayUrl;
    private String notifyUrl;
    private String returnUrl;

    // 支付宝网关
    public static final String GATEWAY_URL = "https://openapi.alipay.com/gateway.do";
    // 沙箱网关
    public static final String SANDBOX_GATEWAY_URL = "https://openapi.alipaydev.com/gateway.do";
    // 格式
    public static final String FORMAT = "JSON";
    // 编码
    public static final String CHARSET = "UTF-8";
    // 签名方式
    public static final String SIGN_TYPE = "RSA2";
}
