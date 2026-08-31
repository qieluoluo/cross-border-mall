/*
Navicat MySQL Data Transfer

Source Server         : bundaberg
Source Server Version : 50743
Source Host           : localhost:3306
Source Database       : db_user

Target Server Type    : MYSQL
Target Server Version : 50743
File Encoding         : 65001

Date: 2026-04-09 12:27:33
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码（加密）',
  `phone` varchar(11) NOT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `gender` tinyint(1) DEFAULT '0' COMMENT '性别 0未知 1男 2女',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态 0禁用 1启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'zhangwei', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iOn6r.EuaVvVOzM8QvYKq5oqW2J2', '13800138001', 'zhangwei@example.com', null, '小明同学', '1', '1', '2026-04-09 11:44:58', '2026-04-09 11:44:58');
INSERT INTO `user` VALUES ('2', 'lina', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iOn6r.EuaVvVOzM8QvYKq5oqW2J2', '13800138002', 'lina@example.com', null, '丽娜', '2', '1', '2026-04-09 11:44:58', '2026-04-09 11:44:58');
INSERT INTO `user` VALUES ('3', 'wangqiang', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iOn6r.EuaVvVOzM8QvYKq5oqW2J2', '13800138003', 'wangqiang@example.com', null, '阿强', '1', '1', '2026-04-09 11:44:58', '2026-04-09 11:44:58');
INSERT INTO `user` VALUES ('4', 'zhaomei', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iOn6r.EuaVvVOzM8QvYKq5oqW2J2', '13800138004', 'zhaomei@example.com', null, '小美', '2', '0', '2026-04-09 11:44:58', '2026-04-09 11:44:58');
INSERT INTO `user` VALUES ('5', 'chenlong', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iOn6r.EuaVvVOzM8QvYKq5oqW2J2', '13800138005', 'chenlong@example.com', null, '龙哥', '1', '1', '2026-04-09 11:44:58', '2026-04-09 11:44:58');

-- ----------------------------
-- Table structure for `user_address`
-- ----------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(11) NOT NULL COMMENT '收货人电话',
  `province` varchar(50) NOT NULL COMMENT '省',
  `city` varchar(50) NOT NULL COMMENT '市',
  `district` varchar(50) NOT NULL COMMENT '区/县',
  `detail_address` varchar(255) NOT NULL COMMENT '详细地址',
  `is_default` tinyint(1) DEFAULT '0' COMMENT '是否默认 0否 1是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- ----------------------------
-- Records of user_address
-- ----------------------------
INSERT INTO `user_address` VALUES ('1', '1', '张伟', '13800138001', '北京市', '北京市', '朝阳区', '建国路88号SOHO现代城B座1801', '1', '2026-04-09 11:44:58', '2026-04-09 11:44:58');
INSERT INTO `user_address` VALUES ('2', '1', '张伟', '13800138001', '上海市', '上海市', '浦东新区', '世纪大道100号环球金融中心50层', '0', '2026-04-09 11:44:58', '2026-04-09 11:44:58');
INSERT INTO `user_address` VALUES ('3', '2', '李娜', '13800138002', '广东省', '深圳市', '南山区', '科技园科发路1号', '1', '2026-04-09 11:44:58', '2026-04-09 11:44:58');
INSERT INTO `user_address` VALUES ('4', '2', '李娜', '13800138002', '广东省', '广州市', '天河区', '珠江新城华夏路16号', '0', '2026-04-09 11:44:58', '2026-04-09 11:44:58');
INSERT INTO `user_address` VALUES ('5', '3', '王强', '13800138003', '浙江省', '杭州市', '西湖区', '西湖科技园西园八路1号', '1', '2026-04-09 11:44:58', '2026-04-09 11:44:58');
INSERT INTO `user_address` VALUES ('6', '4', '赵美', '13800138004', '江苏省', '南京市', '鼓楼区', '中山路55号新华大厦', '1', '2026-04-09 11:44:58', '2026-04-09 11:44:58');
INSERT INTO `user_address` VALUES ('7', '5', '陈龙', '13800138005', '四川省', '成都市', '武侯区', '天府大道中段800号', '1', '2026-04-09 11:44:58', '2026-04-09 11:44:58');
INSERT INTO `user_address` VALUES ('8', '5', '陈龙', '13800138005', '重庆市', '重庆市', '渝中区', '解放碑民族路188号', '0', '2026-04-09 11:44:58', '2026-04-09 11:44:58');
