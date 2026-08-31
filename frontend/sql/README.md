# Bundaberg 数据库初始化指南

## 📋 数据库列表

| 数据库 | 说明 | SQL文件 | 服务 |
|--------|------|---------|------|
| `db_user` | 用户数据 | 服务内置 | shop-user (8001) |
| `db_product` | 商品数据 | 服务内置 | shop-product (8002) |
| `db_order` | 订单数据 | 服务内置 | shop-order (8003) |
| `db_payment` | 支付数据 | 服务内置 | shop-payment (8004) |
| `db_cart` | 购物车数据 | sql/db_cart.sql | shop-cart (待开发) |
| `db_after_sale` | 售后数据 | sql/db_after_sale.sql | shop-after-sale (待开发) |
| `db_admin` | 管理员数据 | sql/db_admin.sql | backend-admin (9999) |

---

## 🚀 快速初始化

### 方法一：一键初始化（推荐）

```bash
# 在 MySQL 中执行
mysql -u root -p < sql/bundaberg.sql
mysql -u root -p db_admin < sql/db_admin.sql
mysql -u root -p db_cart < sql/db_cart.sql
mysql -u root -p db_after_sale < sql/db_after_sale.sql
```

### 方法二：分步执行

#### 1. 创建数据库和用户

```bash
mysql -u root -p < sql/bundaberg.sql
```

这将创建：
- ✅ 6个数据库（db_user, db_product, db_cart, db_order, db_after_sale, db_admin）
- ✅ 6个专用用户
- ✅ 授权配置

#### 2. 初始化管理员数据

```bash
mysql -u root -p db_admin < sql/db_admin.sql
```

包含：
- ✅ admin 表（管理员）
- ✅ role 表（角色）
- ✅ admin_log 表（操作日志）
- ✅ 测试数据

#### 3. 初始化购物车数据

```bash
mysql -u root -p db_cart < sql/db_cart.sql
```

包含：
- ✅ cart 表（购物车）
- ✅ 测试数据

#### 4. 初始化售后数据

```bash
mysql -u root -p db_after_sale < sql/db_after_sale.sql
```

包含：
- ✅ after_sale 表（售后申请）
- ✅ after_sale_evidence 表（售后凭证）
- ✅ 测试数据

#### 5. 初始化服务内置数据库

各服务的数据库脚本在各自的 `src/main/resources/db/init.sql` 中：

```bash
# 用户数据库
mysql -u root -p < frontend/shop-user/src/main/resources/db/init.sql

# 商品数据库
mysql -u root -p < frontend/shop-product/src/main/resources/db/init.sql

# 订单数据库
mysql -u root -p < frontend/shop-order/src/main/resources/db/init.sql

# 支付数据库
mysql -u root -p < frontend/shop-payment/src/main/resources/db/init.sql
```

---

## 🔧 数据库用户信息

| 用户名 | 密码 | 权限数据库 |
|--------|------|-----------|
| user_service | user_pass_2026 | db_user |
| product_service | product_pass_2026 | db_product |
| cart_service | cart_pass_2026 | db_cart |
| order_service | order_pass_2026 | db_order |
| after_sale_service | after_sale_pass_2026 | db_after_sale |
| admin_service | admin_pass_2026 | db_admin |

---

## 📊 测试数据说明

### 管理员账户

| 用户名 | 密码 | 角色 | 真实姓名 |
|--------|------|------|----------|
| 超级管理员 | 123456 | 超级管理员 | 张三 |
| 商家 | 123456 | 商家 | 李四 |

### 角色权限

**超级管理员**：
- 拥有所有权限 `["*"]`

**商家**：
- 商品管理：`product:list`, `product:add`, `product:edit`, `product:delete`, `product:status`
- 订单管理：`order:list`, `order:detail`, `order:delivery`
- 售后管理：`after_sale:list`, `after_sale:handle`
- 统计查看：`statistics:view`

---

## ⚠️ 注意事项

### 1. 数据库名称统一

**重要**：项目中存在两种数据库命名方式：

- `sql/` 目录使用：`db_user`, `db_product` 等
- 服务配置使用：`shop_user`, `shop_product` 等

**建议统一修改为** `db_*` 格式，需要修改以下配置文件：

```yaml
# frontend/shop-user/src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/db_user?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai

# frontend/shop-product/src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/db_product?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai

# frontend/shop-order/src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/db_order?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai

# frontend/shop-payment/src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/db_payment?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
```

### 2. 字符集统一

所有数据库统一使用：
- 字符集：`utf8mb4`
- 排序规则：`utf8mb4_unicode_ci`

### 3. MySQL 版本

- 推荐：MySQL 8.0+
- 兼容：MySQL 5.7+

---

## 🔍 验证安装

### 1. 检查数据库

```sql
SHOW DATABASES LIKE 'db_%';
```

应该看到：
- db_user
- db_product
- db_cart
- db_order
- db_after_sale
- db_admin

### 2. 检查用户

```sql
SELECT user, host FROM mysql.user WHERE user LIKE '%_service';
```

### 3. 检查表

```sql
USE db_admin;
SHOW TABLES;
-- 应该看到：admin, admin_log, role

USE db_cart;
SHOW TABLES;
-- 应该看到：cart

USE db_after_sale;
SHOW TABLES;
-- 应该看到：after_sale, after_sale_evidence
```

### 4. 检查数据

```sql
-- 检查管理员
SELECT id, username, real_name, role_id FROM db_admin.admin;

-- 检查角色
SELECT id, name, description FROM db_admin.role;

-- 检查购物车
SELECT COUNT(*) FROM db_cart.cart;

-- 检查售后
SELECT COUNT(*) FROM db_after_sale.after_sale;
```

---

## 🛠️ 常见问题

### Q1: 数据库已存在

```sql
-- 删除重建
DROP DATABASE IF EXISTS db_user;
CREATE DATABASE db_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Q2: 用户权限不足

```sql
-- 重新授权
GRANT ALL PRIVILEGES ON db_user.* TO 'user_service'@'%';
FLUSH PRIVILEGES;
```

### Q3: 导入中文乱码

确保：
1. SQL文件编码为 UTF-8
2. MySQL 字符集为 utf8mb4
3. 连接时指定字符集：`mysql -u root -p --default-character-set=utf8mb4`

---

## 📞 需要帮助？

如果遇到问题，请检查：
1. MySQL 服务是否运行
2. root 用户密码是否正确
3. SQL 文件路径是否正确
4. 字符集配置是否正确

随时问我！😊
