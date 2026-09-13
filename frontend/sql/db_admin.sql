/*
Navicat MySQL Data Transfer

Source Server         : bundaberg
Source Server Version : 50743
Source Host           : localhost:3306
Source Database       : db_admin

Target Server Type    : MYSQL
Target Server Version : 50743
File Encoding         : 65001

Date: 2026-04-09 12:27:04
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `admin`
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色ID',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态 0禁用 1启用',
  `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES ('1', '超级管理员', '123456', '张三', '1', '1', null, null, null);
INSERT INTO `admin` VALUES ('2', '商家', '123456', '李四', '2', '1', '10.177.13.239', '2026-04-03 19:15:31', '2026-03-31 15:23:13');
INSERT INTO `admin` VALUES ('3', 'admin', 'admin123', '系统管理员', '1', '1', null, null, null);

-- ----------------------------
-- Table structure for `admin_log`
-- ----------------------------
DROP TABLE IF EXISTS `admin_log`;
CREATE TABLE `admin_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `admin_id` bigint(20) NOT NULL COMMENT '管理员ID',
  `admin_name` varchar(50) NOT NULL COMMENT '管理员用户名',
  `module` varchar(50) DEFAULT NULL COMMENT '操作模块',
  `action` varchar(100) DEFAULT NULL COMMENT '操作类型',
  `request_url` varchar(500) DEFAULT NULL COMMENT '请求URL',
  `request_params` text COMMENT '请求参数',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_admin_id` (`admin_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员操作日志表';

-- ----------------------------
-- Records of admin_log
-- ----------------------------

-- ----------------------------
-- Table structure for `role`
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `permissions` text COMMENT '权限列表 JSON数组',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES ('1', '超级管理员', '拥有所有权限', '["*"]', '2026-03-25 09:14:38');
INSERT INTO `role` VALUES ('2', '商家', '商品管理、订单处理、售后管理', '["product:list","product:add","product:edit","product:delete","product:status","order:list","order:detail","order:delivery","after_sale:list","after_sale:handle","statistics:view"]', '2026-03-25 09:15:41');
