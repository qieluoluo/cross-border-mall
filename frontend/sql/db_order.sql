/*
Navicat MySQL Data Transfer

Source Server         : bundaberg
Source Server Version : 50743
Source Host           : localhost:3306
Source Database       : db_order

Target Server Type    : MYSQL
Target Server Version : 50743
File Encoding         : 65001

Date: 2026-04-09 12:27:22
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `data_sync_message`
-- ----------------------------
DROP TABLE IF EXISTS `data_sync_message`;
CREATE TABLE `data_sync_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `target_service` varchar(50) NOT NULL COMMENT '目标服务',
  `business_type` varchar(50) NOT NULL COMMENT '业务类型',
  `business_id` varchar(100) NOT NULL COMMENT '业务ID',
  `data_content` text NOT NULL COMMENT '同步数据JSON',
  `status` tinyint(1) DEFAULT '0' COMMENT '0待处理 1处理中 2成功 3失败',
  `retry_count` int(11) DEFAULT '0' COMMENT '重试次数',
  `error_msg` varchar(500) DEFAULT NULL COMMENT '错误信息',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据同步消息表';

-- ----------------------------
-- Records of data_sync_message
-- ----------------------------

-- ----------------------------
-- Table structure for `order`
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号（唯一）',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_phone` varchar(11) DEFAULT NULL COMMENT '用户手机号（冗余）',
  `user_nickname` varchar(50) DEFAULT NULL COMMENT '用户昵称（冗余）',
  `address_id` bigint(20) NOT NULL COMMENT '收货地址ID',
  `receiver_name` varchar(50) DEFAULT NULL COMMENT '收货人姓名（冗余）',
  `receiver_phone` varchar(11) DEFAULT NULL COMMENT '收货人电话（冗余）',
  `receiver_address` varchar(500) DEFAULT NULL COMMENT '完整收货地址（冗余）',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `freight_amount` decimal(10,2) DEFAULT '0.00' COMMENT '运费',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `discount_amount` decimal(10,2) DEFAULT '0.00' COMMENT '优惠减免金额',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '订单状态 0待付款 1待发货 2待收货 3已完成 4已取消 5退款中 6已退款',
  `pay_type` tinyint(1) DEFAULT NULL COMMENT '支付方式 1微信 2支付宝',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '收货时间',
  `close_time` datetime DEFAULT NULL COMMENT '关闭时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '买家留言',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES ('1', 'ORD202502010001', '1', '13800138001', '小明同学', '1', '张伟', '13800138001', '北京市朝阳区建国路88号SOHO现代城B座1801', '7999.00', '0.00', '7999.00', '0.00', '3', '1', '2025-02-01 10:30:00', '2025-02-02 09:15:00', '2025-02-05 14:20:00', null, '请尽快发货', '2026-04-09 11:45:51', '2026-04-09 11:45:51');
INSERT INTO `order` VALUES ('2', 'ORD202502020002', '1', '13800138001', '小明同学', '1', '张伟', '13800138001', '北京市朝阳区建国路88号SOHO现代城B座1801', '6499.00', '0.00', '6399.00', '100.00', '2', '2', '2025-02-02 14:20:00', '2025-02-03 10:30:00', null, null, '', '2026-04-09 11:45:51', '2026-04-09 11:45:51');
INSERT INTO `order` VALUES ('3', 'ORD202502030003', '2', '13800138002', '丽娜', '3', '李娜', '13800138002', '广东省深圳市南山区科技园科发路1号', '8999.00', '0.00', '8999.00', '0.00', '1', null, null, null, null, null, '', '2026-04-09 11:45:51', '2026-04-09 11:45:51');
INSERT INTO `order` VALUES ('4', 'ORD202502040004', '2', '13800138002', '丽娜', '3', '李娜', '13800138002', '广东省深圳市南山区科技园科发路1号', '598.00', '10.00', '608.00', '0.00', '0', null, null, null, null, null, '', '2026-04-09 11:45:51', '2026-04-09 11:45:51');
INSERT INTO `order` VALUES ('5', 'ORD202502050005', '3', '13800138003', '阿强', '5', '王强', '13800138003', '浙江省杭州市西湖区西湖科技园西园八路1号', '3299.00', '0.00', '3299.00', '0.00', '3', '1', '2025-02-05 09:00:00', '2025-02-06 14:20:00', '2025-02-09 16:30:00', null, '', '2026-04-09 11:45:51', '2026-04-09 11:45:51');
INSERT INTO `order` VALUES ('6', 'ORD202502060006', '3', '13800138003', '阿强', '5', '王强', '13800138003', '浙江省杭州市西湖区西湖科技园西园八路1号', '4299.00', '20.00', '4319.00', '0.00', '2', '2', '2025-02-06 11:30:00', '2025-02-07 09:45:00', null, null, '', '2026-04-09 11:45:51', '2026-04-09 11:45:51');
INSERT INTO `order` VALUES ('7', 'ORD202502070007', '5', '13800138005', '龙哥', '7', '陈龙', '13800138005', '四川省成都市武侯区天府大道中段800号', '8999.00', '0.00', '8799.00', '200.00', '5', '1', '2025-02-07 15:20:00', null, null, null, '质量有问题要求退款', '2026-04-09 11:45:51', '2026-04-09 11:45:51');
INSERT INTO `order` VALUES ('8', 'ORD202502080008', '5', '13800138005', '龙哥', '7', '陈龙', '13800138005', '四川省成都市武侯区天府大道中段800号', '1198.00', '0.00', '1198.00', '0.00', '4', '2', '2025-02-08 13:45:00', '2025-02-09 08:30:00', '2025-02-12 11:20:00', null, '', '2026-04-09 11:45:51', '2026-04-09 11:45:51');

-- ----------------------------
-- Table structure for `order_delivery`
-- ----------------------------
DROP TABLE IF EXISTS `order_delivery`;
CREATE TABLE `order_delivery` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '物流ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `company_code` varchar(50) DEFAULT NULL COMMENT '物流公司编码',
  `company_name` varchar(50) DEFAULT NULL COMMENT '物流公司名称',
  `tracking_no` varchar(100) DEFAULT NULL COMMENT '物流单号',
  `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`),
  KEY `idx_tracking_no` (`tracking_no`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='物流信息表';

-- ----------------------------
-- Records of order_delivery
-- ----------------------------
INSERT INTO `order_delivery` VALUES ('1', '1', 'ORD202502010001', 'SF', '顺丰速运', 'SF123456789CN', '2025-02-02 09:15:00', '2026-04-09 11:45:51', '2026-04-09 11:45:51');
INSERT INTO `order_delivery` VALUES ('2', '2', 'ORD202502020002', 'YT', '圆通速递', 'YT987654321CN', '2025-02-03 10:30:00', '2026-04-09 11:45:51', '2026-04-09 11:45:51');
INSERT INTO `order_delivery` VALUES ('3', '5', 'ORD202502050005', 'JD', '京东物流', 'JD112233445CN', '2025-02-06 14:20:00', '2026-04-09 11:45:51', '2026-04-09 11:45:51');
INSERT INTO `order_delivery` VALUES ('4', '6', 'ORD202502060006', 'SF', '顺丰速运', 'SF998877665CN', '2025-02-07 09:45:00', '2026-04-09 11:45:51', '2026-04-09 11:45:51');
INSERT INTO `order_delivery` VALUES ('5', '8', 'ORD202502080008', 'ZT', '中通快递', 'ZT556677889CN', '2025-02-09 08:30:00', '2026-04-09 11:45:51', '2026-04-09 11:45:51');

-- ----------------------------
-- Table structure for `order_item`
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `sku_id` bigint(20) NOT NULL COMMENT 'SKU ID',
  `sku_code` varchar(100) DEFAULT NULL COMMENT 'SKU编码（冗余）',
  `product_name` varchar(200) NOT NULL COMMENT '商品名称（快照）',
  `product_image` varchar(500) DEFAULT NULL COMMENT '商品图片（快照）',
  `specs` varchar(255) DEFAULT NULL COMMENT '规格组合（快照）',
  `price` decimal(10,2) NOT NULL COMMENT '购买时单价',
  `quantity` int(11) NOT NULL COMMENT '购买数量',
  `total_amount` decimal(10,2) NOT NULL COMMENT '小计金额',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_sku_id` (`sku_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES ('1', '1', 'ORD202502010001', '1', '1', 'IP15P-128-OT', 'Apple iPhone 15 Pro', '/images/iphone15pro.jpg', '{\"颜色\":\"原色钛金属\",\"容量\":\"128GB\"}', '7999.00', '1', '7999.00', '2026-04-09 11:45:51');
INSERT INTO `order_item` VALUES ('2', '2', 'ORD202502020002', '2', '4', 'X14U-256-W', '小米14 Ultra', '/images/xiaomi14u.jpg', '{\"颜色\":\"白色\",\"存储\":\"256GB\"}', '6499.00', '1', '6499.00', '2026-04-09 11:45:51');
INSERT INTO `order_item` VALUES ('3', '3', 'ORD202502030003', '8', '15', 'TP-X1-I7-16-512', '联想 ThinkPad X1', '/images/thinkpad_x1.jpg', '{\"处理器\":\"i7\",\"内存\":\"16GB\",\"硬盘\":\"512GB\"}', '8999.00', '1', '8999.00', '2026-04-09 11:45:51');
INSERT INTO `order_item` VALUES ('4', '4', 'ORD202502040004', '7', '13', 'UNIQLO-S-BE', '优衣库女装连衣裙', '/images/uniqlo_dress.jpg', '{\"尺寸\":\"S\",\"颜色\":\"米色\"}', '299.00', '2', '598.00', '2026-04-09 11:45:51');
INSERT INTO `order_item` VALUES ('5', '5', 'ORD202502050005', '4', '8', 'GL-KFR35-15', '格力空调 KFR-35GW', '/images/geli_kfr35.jpg', '{\"功率\":\"1.5匹\",\"颜色\":\"白色\"}', '3299.00', '1', '3299.00', '2026-04-09 11:45:51');
INSERT INTO `order_item` VALUES ('6', '6', 'ORD202502060006', '5', '9', 'HR-BCD500-SI', '海尔冰箱 BCD-500', '/images/haier_bcd500.jpg', '{\"颜色\":\"银色\",\"容积\":\"500升\"}', '4299.00', '1', '4299.00', '2026-04-09 11:45:51');
INSERT INTO `order_item` VALUES ('7', '7', 'ORD202502070007', '8', '15', 'TP-X1-I7-16-512', '联想 ThinkPad X1', '/images/thinkpad_x1.jpg', '{\"处理器\":\"i7\",\"内存\":\"16GB\",\"硬盘\":\"512GB\"}', '8999.00', '1', '8999.00', '2026-04-09 11:45:51');
INSERT INTO `order_item` VALUES ('8', '8', 'ORD202502080008', '6', '10', 'NIKE-MEN-S-BK', 'Nike 运动套装男', '/images/nike_men.jpg', '{\"尺寸\":\"S\",\"颜色\":\"黑色\"}', '599.00', '2', '1198.00', '2026-04-09 11:45:51');

-- ----------------------------
-- Table structure for `order_log`
-- ----------------------------
DROP TABLE IF EXISTS `order_log`;
CREATE TABLE `order_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `action` varchar(50) NOT NULL COMMENT '操作类型',
  `before_status` tinyint(2) DEFAULT NULL COMMENT '操作前状态',
  `after_status` tinyint(2) DEFAULT NULL COMMENT '操作后状态',
  `operator` varchar(50) DEFAULT NULL COMMENT '操作人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COMMENT='订单操作日志表';

-- ----------------------------
-- Records of order_log
-- ----------------------------
INSERT INTO `order_log` VALUES ('1', 'ORD202502010001', '创建订单', null, '0', '系统', '订单创建成功', '2026-04-09 11:45:51');
INSERT INTO `order_log` VALUES ('2', 'ORD202502010001', '支付订单', '0', '1', '用户', '微信支付成功', '2026-04-09 11:45:51');
INSERT INTO `order_log` VALUES ('3', 'ORD202502010001', '发货', '1', '2', 'admin', '顺丰发货', '2026-04-09 11:45:51');
INSERT INTO `order_log` VALUES ('4', 'ORD202502010001', '确认收货', '2', '3', '用户', '商品已签收', '2026-04-09 11:45:51');
INSERT INTO `order_log` VALUES ('5', 'ORD202502020002', '创建订单', null, '0', '系统', '订单创建成功', '2026-04-09 11:45:51');
INSERT INTO `order_log` VALUES ('6', 'ORD202502020002', '支付订单', '0', '1', '用户', '支付宝支付成功', '2026-04-09 11:45:51');
INSERT INTO `order_log` VALUES ('7', 'ORD202502020002', '发货', '1', '2', 'admin', '圆通发货', '2026-04-09 11:45:51');

-- ----------------------------
-- Table structure for `transaction_compensation`
-- ----------------------------
DROP TABLE IF EXISTS `transaction_compensation`;
CREATE TABLE `transaction_compensation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `transaction_id` varchar(64) NOT NULL COMMENT '事务ID',
  `business_type` varchar(50) NOT NULL COMMENT '业务类型',
  `step` int(11) NOT NULL COMMENT '步骤序号',
  `step_name` varchar(100) NOT NULL COMMENT '步骤名称',
  `status` tinyint(1) DEFAULT '0' COMMENT '0待执行 1执行中 2成功 3失败',
  `compensate_data` text COMMENT '补偿数据JSON',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_transaction_id` (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分布式事务补偿表';

-- ----------------------------
-- Records of transaction_compensation
-- ----------------------------
