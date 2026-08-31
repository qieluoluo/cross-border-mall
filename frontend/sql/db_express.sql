/*
Navicat MySQL Data Transfer

Source Server         : bundaberg
Source Server Version : 50743
Source Host           : localhost:3306
Source Database       : db_express

Target Server Type    : MYSQL
Target Server Version : 50743
File Encoding         : 65001

Date: 2026-05-07 12:00:00
*/
CREATE DATABASE IF NOT EXISTS db_express DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `express_info`
-- ----------------------------
DROP TABLE IF EXISTS `express_info`;
CREATE TABLE `express_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '物流记录ID',
  `order_id` bigint(20) NOT NULL COMMENT '关联订单ID',
  `express_no` varchar(100) NOT NULL COMMENT '快递单号',
  `express_company` varchar(50) NOT NULL COMMENT '快递公司编码 SF/YTO/ZTO/YD/EMS/JD',
  `status` varchar(20) DEFAULT 'pending' COMMENT '物流状态 pending/transit/delivered/exception',
  `latest_time` varchar(50) DEFAULT NULL COMMENT '最新物流时间',
  `latest_status` varchar(200) DEFAULT NULL COMMENT '最新物流状态描述',
  `detail_json` text COMMENT '完整物流详情JSON',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`),
  KEY `idx_express_no` (`express_no`),
  KEY `idx_express_company` (`express_company`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流信息表';

-- ----------------------------
-- Records of express_info
-- ----------------------------
INSERT INTO `express_info` VALUES ('1', '1', 'SF123456789CN', 'SF', 'delivered', '2025-02-05 14:30:00', '快件已签收，签收人：本人', '{"Traces":[{"AcceptTime":"2025-02-05 14:30:00","AcceptStation":"快件已签收，签收人：本人"},{"AcceptTime":"2025-02-05 10:20:00","AcceptStation":"快件正在派送中，派送员：张师傅 138****8888"},{"AcceptTime":"2025-02-04 18:30:00","AcceptStation":"快件已到达【北京朝阳区网点】"},{"AcceptTime":"2025-02-04 12:00:00","AcceptStation":"快件已从【上海转运中心】发出"},{"AcceptTime":"2025-02-03 16:45:00","AcceptStation":"快件已到达【上海转运中心】"},{"AcceptTime":"2025-02-03 10:00:00","AcceptStation":"快件已揽收"}]}', '2025-02-03 10:00:00', '2025-02-05 14:30:00');
INSERT INTO `express_info` VALUES ('2', '2', 'YT987654321CN', 'YTO', 'transit', '2025-02-05 08:30:00', '快件已到达【杭州转运中心】', '{"Traces":[{"AcceptTime":"2025-02-05 08:30:00","AcceptStation":"快件已到达【杭州转运中心】"},{"AcceptTime":"2025-02-04 20:00:00","AcceptStation":"快件已从【上海转运中心】发出"},{"AcceptTime":"2025-02-04 14:00:00","AcceptStation":"快件已揽收"}]}', '2025-02-04 14:00:00', '2025-02-05 08:30:00');
INSERT INTO `express_info` VALUES ('3', '5', 'JD112233445CN', 'JD', 'delivered', '2025-02-09 16:30:00', '快件已签收，签收人：本人', '{"Traces":[{"AcceptTime":"2025-02-09 16:30:00","AcceptStation":"快件已签收，签收人：本人"},{"AcceptTime":"2025-02-09 09:00:00","AcceptStation":"快件正在派送中，派送员：李师傅 139****9999"},{"AcceptTime":"2025-02-08 22:00:00","AcceptStation":"快件已到达【杭州西湖区网点】"},{"AcceptTime":"2025-02-08 14:00:00","AcceptStation":"快件已从【上海转运中心】发出"},{"AcceptTime":"2025-02-08 08:00:00","AcceptStation":"快件已揽收"}]}', '2025-02-08 08:00:00', '2025-02-09 16:30:00');
INSERT INTO `express_info` VALUES ('4', '6', 'SF998877665CN', 'SF', 'transit', '2025-02-08 06:00:00', '快件已从【上海转运中心】发出', '{"Traces":[{"AcceptTime":"2025-02-08 06:00:00","AcceptStation":"快件已从【上海转运中心】发出"},{"AcceptTime":"2025-02-07 20:00:00","AcceptStation":"快件已到达【上海转运中心】"},{"AcceptTime":"2025-02-07 14:00:00","AcceptStation":"快件已揽收"}]}', '2025-02-07 14:00:00', '2025-02-08 06:00:00');
INSERT INTO `express_info` VALUES ('5', '8', 'ZT556677889CN', 'ZTO', 'delivered', '2025-02-12 11:20:00', '快件已签收，签收人：本人', '{"Traces":[{"AcceptTime":"2025-02-12 11:20:00","AcceptStation":"快件已签收，签收人：本人"},{"AcceptTime":"2025-02-12 08:00:00","AcceptStation":"快件正在派送中，派送员：王师傅 137****7777"},{"AcceptTime":"2025-02-11 20:00:00","AcceptStation":"快件已到达【成都武侯区网点】"},{"AcceptTime":"2025-02-11 10:00:00","AcceptStation":"快件已从【重庆转运中心】发出"},{"AcceptTime":"2025-02-10 18:00:00","AcceptStation":"快件已到达【重庆转运中心】"},{"AcceptTime":"2025-02-10 08:30:00","AcceptStation":"快件已揽收"}]}', '2025-02-10 08:30:00', '2025-02-12 11:20:00');

-- ----------------------------
-- Table structure for `express_company`
-- ----------------------------
DROP TABLE IF EXISTS `express_company`;
CREATE TABLE `express_company` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '快递公司ID',
  `code` varchar(20) NOT NULL COMMENT '快递公司编码',
  `name` varchar(50) NOT NULL COMMENT '快递公司名称',
  `name_en` varchar(50) DEFAULT NULL COMMENT '英文名称',
  `website` varchar(100) DEFAULT NULL COMMENT '官方网站',
  `tel` varchar(20) DEFAULT NULL COMMENT '客服电话',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态 0禁用 1启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='快递公司信息表';

-- ----------------------------
-- Records of express_company
-- ----------------------------
INSERT INTO `express_company` VALUES ('1', 'SF', '顺丰速运', 'SF Express', 'https://www.sf-express.com', '95338', '1', '1', '2026-05-07 12:00:00', '2026-05-07 12:00:00');
INSERT INTO `express_company` VALUES ('2', 'YTO', '圆通速递', 'YTO Express', 'https://www.yto.net.cn', '95554', '2', '1', '2026-05-07 12:00:00', '2026-05-07 12:00:00');
INSERT INTO `express_company` VALUES ('3', 'ZTO', '中通快递', 'ZTO Express', 'https://www.zto.com', '95311', '3', '1', '2026-05-07 12:00:00', '2026-05-07 12:00:00');
INSERT INTO `express_company` VALUES ('4', 'YD', '韵达快递', 'Yunda Express', 'https://www.yundaex.com', '95546', '4', '1', '2026-05-07 12:00:00', '2026-05-07 12:00:00');
INSERT INTO `express_company` VALUES ('5', 'EMS', 'EMS', 'EMS', 'https://www.ems.com.cn', '11183', '5', '1', '2026-05-07 12:00:00', '2026-05-07 12:00:00');
INSERT INTO `express_company` VALUES ('6', 'JD', '京东物流', 'JD Logistics', 'https://www.jdl.com', '950616', '6', '1', '2026-05-07 12:00:00', '2026-05-07 12:00:00');
INSERT INTO `express_company` VALUES ('7', 'TTKDEX', '天天快递', 'TTKDEX', 'http://www.ttkdex.com', '400-188-8888', '7', '1', '2026-05-07 12:00:00', '2026-05-07 12:00:00');
INSERT INTO `express_company` VALUES ('8', 'HTKY', '百世快递', 'Best Express', 'http://www.800bestex.com', '95320', '8', '1', '2026-05-07 12:00:00', '2026-05-07 12:00:00');
INSERT INTO `express_company` VALUES ('9', 'POST', '中国邮政', 'China Post', 'https://www.chinapost.com.cn', '11185', '9', '1', '2026-05-07 12:00:00', '2026-05-07 12:00:00');
INSERT INTO `express_company` VALUES ('10', 'DB', '德邦快递', 'Deppon Express', 'https://www.deppon.com', '95353', '10', '1', '2026-05-07 12:00:00', '2026-05-07 12:00:00');

-- ----------------------------
-- Table structure for `express_trace`
-- ----------------------------
DROP TABLE IF EXISTS `express_trace`;
CREATE TABLE `express_trace` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '物流轨迹ID',
  `express_info_id` bigint(20) NOT NULL COMMENT '关联物流信息ID',
  `accept_time` datetime NOT NULL COMMENT '物流时间',
  `accept_station` varchar(500) NOT NULL COMMENT '物流描述',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_express_info_id` (`express_info_id`),
  KEY `idx_accept_time` (`accept_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流轨迹明细表';

-- ----------------------------
-- Records of express_trace
-- ----------------------------

-- ----------------------------
-- Table structure for `express_subscribe`
-- ----------------------------
DROP TABLE IF EXISTS `express_subscribe`;
CREATE TABLE `express_subscribe` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订阅ID',
  `order_id` bigint(20) NOT NULL COMMENT '关联订单ID',
  `express_no` varchar(100) NOT NULL COMMENT '快递单号',
  `express_company` varchar(50) NOT NULL COMMENT '快递公司编码',
  `subscribe_status` tinyint(1) DEFAULT '0' COMMENT '订阅状态 0未订阅 1已订阅 2订阅失败',
  `callback_count` int(11) DEFAULT '0' COMMENT '回调次数',
  `last_callback_time` datetime DEFAULT NULL COMMENT '最后回调时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_express_no` (`express_no`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流订阅记录表';

-- ----------------------------
-- Records of express_subscribe
-- ----------------------------
