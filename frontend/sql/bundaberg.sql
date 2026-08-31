-- =============================================
-- Bundaberg 跨境电商系统 - 数据库和用户初始化脚本
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `db_user` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `db_product` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `db_cart` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `db_order` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `db_after_sale` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `db_admin` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户
CREATE USER IF NOT EXISTS 'user_service'@'%' IDENTIFIED BY 'user_pass_2026';
CREATE USER IF NOT EXISTS 'product_service'@'%' IDENTIFIED BY 'product_pass_2026';
CREATE USER IF NOT EXISTS 'cart_service'@'%' IDENTIFIED BY 'cart_pass_2026';
CREATE USER IF NOT EXISTS 'order_service'@'%' IDENTIFIED BY 'order_pass_2026';
CREATE USER IF NOT EXISTS 'after_sale_service'@'%' IDENTIFIED BY 'after_sale_pass_2026';
CREATE USER IF NOT EXISTS 'admin_service'@'%' IDENTIFIED BY 'admin_pass_2026';

-- 授权
GRANT ALL PRIVILEGES ON db_user.* TO 'user_service'@'%';
GRANT ALL PRIVILEGES ON db_product.* TO 'product_service'@'%';
GRANT ALL PRIVILEGES ON db_cart.* TO 'cart_service'@'%';
GRANT ALL PRIVILEGES ON db_order.* TO 'order_service'@'%';
GRANT ALL PRIVILEGES ON db_after_sale.* TO 'after_sale_service'@'%';
GRANT ALL PRIVILEGES ON db_admin.* TO 'admin_service'@'%';

-- 刷新权限
FLUSH PRIVILEGES;

-- =============================================
-- 验证脚本（可选）
-- =============================================

-- 查看所有数据库
SHOW DATABASES;

-- 查看各数据库的表
USE db_user;
SHOW TABLES;

USE db_product;
SHOW TABLES;

USE db_cart;
SHOW TABLES;

USE db_order;
SHOW TABLES;

USE db_after_sale;
SHOW TABLES;

USE db_admin;
SHOW TABLES;
