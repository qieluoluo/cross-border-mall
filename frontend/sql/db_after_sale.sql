/*
Navicat MySQL Data Transfer

Source Server         : bundaberg
Source Server Version : 50743
Source Host           : localhost:3306
Source Database       : db_after_sale

Target Server Type    : MYSQL
Target Server Version : 50743
File Encoding         : 65001

Date: 2026-04-09 12:27:10
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `after_sale`
-- ----------------------------
DROP TABLE IF EXISTS `after_sale`;
CREATE TABLE `after_sale` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '售后ID',
  `after_sale_no` varchar(32) NOT NULL COMMENT '售后单号',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号（冗余）',
  `order_item_id` bigint(20) DEFAULT NULL COMMENT '订单明细ID（退货时关联）',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_phone` varchar(11) DEFAULT NULL COMMENT '用户手机号（冗余）',
  `type` tinyint(1) NOT NULL COMMENT '售后类型 1仅退款 2退货退款',
  `reason` varchar(200) NOT NULL COMMENT '申请原因',
  `description` varchar(500) DEFAULT NULL COMMENT '问题描述',
  `refund_amount` decimal(10,2) NOT NULL COMMENT '退款金额',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0待审核 1审核通过 2审核拒绝 3退款完成 4已关闭',
  `reject_reason` varchar(500) DEFAULT NULL COMMENT '拒绝原因',
  `apply_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_after_sale_no` (`after_sale_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='售后表';

-- ----------------------------
-- Records of after_sale
-- ----------------------------
INSERT INTO `after_sale` VALUES ('1', 'AS202502150001', '7', 'ORD202502070007', '7', '5', '13800138005', '1', '商品质量问题', '电脑开机蓝屏，无法正常使用', '8999.00', '2', null, '2025-02-15 10:00:00', '2025-02-16 09:30:00', '2025-02-17 14:20:00');
INSERT INTO `after_sale` VALUES ('2', 'AS202502160002', '4', 'ORD202502040004', '4', '2', '13800138002', '2', '尺码不合适', '连衣裙尺码偏小，申请退货退款', '598.00', '1', null, '2025-02-16 14:30:00', '2025-02-17 10:15:00', null);
INSERT INTO `after_sale` VALUES ('3', 'AS202502170003', '2', 'ORD202502020002', '2', '1', '13800138001', '1', '不想要了', '临时改变主意，申请退款', '6399.00', '0', null, '2025-02-17 09:20:00', null, null);
INSERT INTO `after_sale` VALUES ('4', 'AS202502180004', '6', 'ORD202502060006', '6', '3', '13800138003', '2', '商品破损', '冰箱运输过程中外壳有凹陷', '4319.00', '2', '审核拒绝：请提供更多证据照片', '2025-02-18 11:45:00', '2025-02-19 15:30:00', null);
INSERT INTO `after_sale` VALUES ('5', 'AS202502190005', '1', 'ORD202502010001', '1', '1', '13800138001', '1', '质量问题', '手机屏幕有坏点', '7999.00', '3', null, '2025-02-19 08:00:00', '2025-02-20 11:00:00', '2025-02-21 16:45:00');

-- ----------------------------
-- Table structure for `after_sale_evidence`
-- ----------------------------
DROP TABLE IF EXISTS `after_sale_evidence`;
CREATE TABLE `after_sale_evidence` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `after_sale_id` bigint(20) NOT NULL COMMENT '售后ID',
  `evidence_type` tinyint(1) DEFAULT '1' COMMENT '凭证类型 1图片 2视频',
  `evidence_url` varchar(500) NOT NULL COMMENT '凭证URL',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_after_sale_id` (`after_sale_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COMMENT='售后凭证表';

-- ----------------------------
-- Records of after_sale_evidence
-- ----------------------------
INSERT INTO `after_sale_evidence` VALUES ('1', '1', '1', '/uploads/evidence/1/blue_screen.jpg', '2026-04-09 11:46:07');
INSERT INTO `after_sale_evidence` VALUES ('2', '1', '1', '/uploads/evidence/1/error_log.png', '2026-04-09 11:46:07');
INSERT INTO `after_sale_evidence` VALUES ('3', '2', '1', '/uploads/evidence/2/size_measure.jpg', '2026-04-09 11:46:07');
INSERT INTO `after_sale_evidence` VALUES ('4', '4', '1', '/uploads/evidence/4/damage_front.jpg', '2026-04-09 11:46:07');
INSERT INTO `after_sale_evidence` VALUES ('5', '4', '1', '/uploads/evidence/4/damage_side.jpg', '2026-04-09 11:46:07');
INSERT INTO `after_sale_evidence` VALUES ('6', '5', '1', '/uploads/evidence/5/screen_dead_pixel.jpg', '2026-04-09 11:46:07');
INSERT INTO `after_sale_evidence` VALUES ('7', '5', '2', '/uploads/evidence/5/video_check.mp4', '2026-04-09 11:46:07');
