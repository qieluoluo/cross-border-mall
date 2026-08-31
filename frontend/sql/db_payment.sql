/*
Navicat MySQL Data Transfer

Source Server         : bundaberg
Source Server Version : 50743
Source Host           : localhost:3306
Source Database       : db_payment

Target Server Type    : MYSQL
Target Server Version : 50743
File Encoding         : 65001

Date: 2026-05-07 12:00:00
*/
CREATE DATABASE IF NOT EXISTS db_payment DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE db_payment;
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `payment`
-- ----------------------------
DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
  `order_id` bigint(20) NOT NULL COMMENT '关联订单ID',
  `payment_method` varchar(20) NOT NULL COMMENT '支付方式 PAYPAL/STRIPE/ALIPAY',
  `transaction_id` varchar(100) DEFAULT NULL COMMENT '交易单号（第三方支付返回）',
  `amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `currency` varchar(10) DEFAULT 'CNY' COMMENT '货币类型',
  `status` varchar(20) DEFAULT 'CREATED' COMMENT '支付状态 CREATED/COMPLETED/FAILED/REFUNDED',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_transaction_id` (`transaction_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- ----------------------------
-- Records of payment
-- ----------------------------
INSERT INTO `payment` VALUES ('1', '1', 'ALIPAY', '202502010001001', '7999.00', 'CNY', 'COMPLETED', '2025-02-01 10:30:00', '2025-02-01 10:35:00');
INSERT INTO `payment` VALUES ('2', '2', 'ALIPAY', '202502020002001', '6399.00', 'CNY', 'COMPLETED', '2025-02-02 14:20:00', '2025-02-02 14:25:00');
INSERT INTO `payment` VALUES ('3', '3', 'PAYPAL', 'PAYPAL-202502030003', '8999.00', 'USD', 'CREATED', '2025-02-03 16:30:00', '2025-02-03 16:30:00');
INSERT INTO `payment` VALUES ('4', '4', 'ALIPAY', '202502040004001', '608.00', 'CNY', 'CREATED', '2025-02-04 09:45:00', '2025-02-04 09:45:00');
INSERT INTO `payment` VALUES ('5', '5', 'STRIPE', 'STRIPE-202502050005', '3299.00', 'USD', 'COMPLETED', '2025-02-05 09:00:00', '2025-02-05 09:05:00');
INSERT INTO `payment` VALUES ('6', '6', 'ALIPAY', '202502060006001', '4319.00', 'CNY', 'COMPLETED', '2025-02-06 11:30:00', '2025-02-06 11:35:00');
INSERT INTO `payment` VALUES ('7', '7', 'ALIPAY', '202502070007001', '8799.00', 'CNY', 'COMPLETED', '2025-02-07 15:20:00', '2025-02-07 15:25:00');
INSERT INTO `payment` VALUES ('8', '8', 'ALIPAY', '202502080008001', '1198.00', 'CNY', 'COMPLETED', '2025-02-08 13:45:00', '2025-02-08 13:50:00');

-- ----------------------------
-- Table structure for `payment_refund`
-- ----------------------------
DROP TABLE IF EXISTS `payment_refund`;
CREATE TABLE `payment_refund` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '退款记录ID',
  `payment_id` bigint(20) NOT NULL COMMENT '关联支付记录ID',
  `order_id` bigint(20) NOT NULL COMMENT '关联订单ID',
  `refund_no` varchar(100) NOT NULL COMMENT '退款单号',
  `refund_amount` decimal(10,2) NOT NULL COMMENT '退款金额',
  `status` varchar(20) DEFAULT 'APPLIED' COMMENT '退款状态 APPLIED/PROCESSING/SUCCESS/FAILED',
  `reason` varchar(500) DEFAULT NULL COMMENT '退款原因',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_payment_id` (`payment_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_refund_no` (`refund_no`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款记录表';

-- ----------------------------
-- Records of payment_refund
-- ----------------------------
INSERT INTO `payment_refund` VALUES ('1', '7', '7', 'REF202502070001', '8799.00', 'PROCESSING', '质量有问题要求退款', '2025-02-10 10:00:00', '2025-02-10 10:00:00');

-- ----------------------------
-- Table structure for `payment_notify_log`
-- ----------------------------
DROP TABLE IF EXISTS `payment_notify_log`;
CREATE TABLE `payment_notify_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '通知日志ID',
  `payment_id` bigint(20) DEFAULT NULL COMMENT '关联支付记录ID',
  `notify_type` varchar(50) NOT NULL COMMENT '通知类型 PAY_SUCCESS/PAY_FAILED/REFUND_SUCCESS/REFUND_FAILED',
  `notify_data` text COMMENT '通知内容JSON',
  `status` tinyint(1) DEFAULT '0' COMMENT '0待处理 1已处理 2处理失败',
  `retry_count` int(11) DEFAULT '0' COMMENT '重试次数',
  `error_msg` varchar(500) DEFAULT NULL COMMENT '错误信息',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_payment_id` (`payment_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付通知日志表';

-- ----------------------------
-- Records of payment_notify_log
-- ----------------------------
