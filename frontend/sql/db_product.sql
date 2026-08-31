/*
Navicat MySQL Data Transfer

Source Server         : bundaberg
Source Server Version : 50743
Source Host           : localhost:3306
Source Database       : db_product

Target Server Type    : MYSQL
Target Server Version : 50743
File Encoding         : 65001

Date: 2026-04-09 12:27:27
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `category`
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父分类ID 0为顶级',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `level` tinyint(1) DEFAULT '1' COMMENT '层级 1一级 2二级 3三级',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序值',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态 0禁用 1启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=213 DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES ('1', '0', '手机数码', '1', '1', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');
INSERT INTO `category` VALUES ('2', '0', '家用电器', '1', '2', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');
INSERT INTO `category` VALUES ('3', '0', '服装鞋帽', '1', '3', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');
INSERT INTO `category` VALUES ('4', '0', '美妆个护', '1', '4', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');
INSERT INTO `category` VALUES ('5', '0', '图书文娱', '1', '5', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');
INSERT INTO `category` VALUES ('11', '1', '手机', '2', '1', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');
INSERT INTO `category` VALUES ('12', '1', '电脑办公', '2', '2', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');
INSERT INTO `category` VALUES ('13', '1', '摄影摄像', '2', '3', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');
INSERT INTO `category` VALUES ('21', '2', '大家电', '2', '1', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');
INSERT INTO `category` VALUES ('22', '2', '生活电器', '2', '2', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');
INSERT INTO `category` VALUES ('31', '3', '男装', '2', '1', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');
INSERT INTO `category` VALUES ('32', '3', '女装', '2', '2', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');
INSERT INTO `category` VALUES ('111', '11', '智能手机', '3', '1', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');
INSERT INTO `category` VALUES ('112', '11', '老人机', '3', '2', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');
INSERT INTO `category` VALUES ('211', '21', '空调', '3', '1', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');
INSERT INTO `category` VALUES ('212', '21', '冰箱', '3', '2', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');

-- ----------------------------
-- Table structure for `product`
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `name` varchar(200) NOT NULL COMMENT '商品名称',
  `sub_title` varchar(500) DEFAULT NULL COMMENT '副标题/卖点',
  `main_image` varchar(500) DEFAULT NULL COMMENT '主图URL',
  `detail_html` text COMMENT '商品详情（富文本）',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  `stock` int(11) NOT NULL DEFAULT '0' COMMENT '总库存',
  `sales` int(11) DEFAULT '0' COMMENT '销量',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态 0下架 1上架',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES ('1', '111', 'Apple iPhone 15 Pro', '钛金属设计，A17 Pro芯片', '/images/iphone15pro.png', null, '7999.00', '500', '1280', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');
INSERT INTO `product` VALUES ('2', '111', '小米14 Ultra', '徕卡四摄，专业影像手机', '/images/xiaomi14u.png', null, '6499.00', '800', '2340', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');
INSERT INTO `product` VALUES ('3', '112', '华为 Mate 60 Pro', '卫星通信，鸿蒙系统', '/images/huawei60pro.png', null, '6999.00', '300', '890', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');
INSERT INTO `product` VALUES ('4', '211', '格力空调 KFR-35GW', '1.5匹新能效变频空调', '/images/geli_kfr35.png', null, '3299.00', '150', '320', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');
INSERT INTO `product` VALUES ('5', '212', '海尔冰箱 BCD-500', '500升风冷无霜对开门冰箱', '/images/haier_bcd500.png', null, '4299.00', '100', '210', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');
INSERT INTO `product` VALUES ('6', '31', 'Nike 运动套装男', '夏季新款透气运动服', '/images/nike.png', null, '599.00', '1000', '560', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');
INSERT INTO `product` VALUES ('7', '32', '优衣库女装连衣裙', '简约纯棉舒适连衣裙', '/images/uniqlo_dress.png', null, '299.00', '2000', '1280', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');
INSERT INTO `product` VALUES ('8', '12', '联想 ThinkPad X1', '商务轻薄笔记本电脑', '/images/lianxiang.png', null, '8999.00', '80', '156', '1', '2026-04-09 11:45:21', '2026-04-09 11:45:21');

-- ----------------------------
-- Table structure for `product_sku`
-- ----------------------------
DROP TABLE IF EXISTS `product_sku`;
CREATE TABLE `product_sku` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'SKU ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `specs` varchar(255) NOT NULL COMMENT '规格组合 JSON格式 如{"颜色":"红","尺寸":"M"}',
  `price` decimal(10,2) NOT NULL COMMENT 'SKU价格',
  `stock` int(11) NOT NULL DEFAULT '0' COMMENT 'SKU库存',
  `sku_code` varchar(100) DEFAULT NULL COMMENT 'SKU编码',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sku_code` (`sku_code`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SKU表';

-- ----------------------------
-- Records of product_sku
-- ----------------------------
