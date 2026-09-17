/*
Navicat MySQL Data Transfer

Source Server         : bundaberg
Source Server Version : 50743
Source Host           : localhost:3306
Source Database       : db_cart

Target Server Type    : MYSQL
Target Server Version : 50743
File Encoding         : 65001

Date: 2026-04-09 12:27:16
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `cart`
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `sku_id` bigint(20) NOT NULL COMMENT 'SKU ID',
  `quantity` int(11) NOT NULL DEFAULT '1' COMMENT '购买数量',
  `selected` tinyint(1) DEFAULT '1' COMMENT '是否勾选 0否 1是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`,`product_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ----------------------------
-- Records of cart
-- ----------------------------
INSERT INTO `cart` VALUES ('1', '1', '1', '1', '1', '1', '2026-04-09 11:45:40', '2026-04-09 11:45:40');
INSERT INTO `cart` VALUES ('2', '1', '2', '4', '1', '1', '2026-04-09 11:45:40', '2026-04-09 11:45:40');
INSERT INTO `cart` VALUES ('3', '1', '6', '10', '2', '0', '2026-04-09 11:45:40', '2026-04-09 11:45:40');
INSERT INTO `cart` VALUES ('4', '2', '1', '2', '1', '1', '2026-04-09 11:45:40', '2026-04-09 11:45:40');
INSERT INTO `cart` VALUES ('5', '2', '7', '13', '2', '1', '2026-04-09 11:45:40', '2026-04-09 11:45:40');
INSERT INTO `cart` VALUES ('6', '3', '4', '8', '1', '1', '2026-04-09 11:45:40', '2026-04-09 11:45:40');
INSERT INTO `cart` VALUES ('7', '3', '5', '9', '1', '0', '2026-04-09 11:45:40', '2026-04-09 11:45:40');
INSERT INTO `cart` VALUES ('8', '4', '3', '6', '1', '1', '2026-04-09 11:45:40', '2026-04-09 11:45:40');
INSERT INTO `cart` VALUES ('9', '5', '8', '15', '1', '1', '2026-04-09 11:45:40', '2026-04-09 11:45:40');
INSERT INTO `cart` VALUES ('10', '5', '1', '3', '1', '0', '2026-04-09 11:45:40', '2026-04-09 11:45:40');
