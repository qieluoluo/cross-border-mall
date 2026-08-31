package com.delmon.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Slf4j
public class RSAUtil {

    private static final String SIGN_ALGORITHMS = "SHA256WithRSA";
    private static final String CHARSET = "UTF-8";

    /**
     * RSA2签名
     */
    public static String sign(String content, String privateKey) {
        try {
            // 解码私钥
            byte[] privateKeyBytes = Base64.decodeBase64(privateKey);
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            // 签名
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(priKey);
            signature.update(content.getBytes(CHARSET));

            // 修正：使用 sign() 方法，不是 signature()
            byte[] signed = signature.sign();
            return Base64.encodeBase64String(signed);
        } catch (Exception e) {
            log.error("RSA签名失败", e);
            throw new RuntimeException("RSA签名失败", e);
        }
    }

    /**
     * RSA2验签
     */
    public static boolean verify(String content, String sign, String publicKey) {
        try {
            // 解码公钥
            byte[] publicKeyBytes = Base64.decodeBase64(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);

            // 验签
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
            signature.initVerify(pubKey);
            signature.update(content.getBytes(CHARSET));

            // 修正：签名值需要解码
            byte[] signBytes = Base64.decodeBase64(sign);
            return signature.verify(signBytes);
        } catch (Exception e) {
            log.error("RSA验签失败", e);
            return false;
        }
    }
}
