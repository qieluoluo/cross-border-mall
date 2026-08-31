# SQL 文件修复总结

## ✅ 已修复的问题

### 1. db_admin.sql - 严重错误修复

**问题**：第82行 `role` 表的 `permissions` 字段数据重复5次，JSON格式错误

**修复前**：
```sql
INSERT INTO `role` VALUES ('2', '商家', '商品管理、订单处理、售后管理', '[
    "product:list",
    "product:add",
    ...
]
[
    "product:list",
    ...
]
[...重复5次...]', '2026-03-25 09:15:41');
```

**修复后**：
```sql
INSERT INTO `role` VALUES ('2', '商家', '商品管理、订单处理、售后管理', '["product:list","product:add","product:edit","product:delete","product:status","order:list","order:detail","order:delivery","after_sale:list","after_sale:handle","statistics:view"]', '2026-03-25 09:15:41');
```

**影响**：❌ 修复前无法导入，JSON 解析会失败

---

### 2. bundaberg.sql - 缺少数据库创建语句

**问题**：只有用户创建和授权，没有 `CREATE DATABASE` 语句

**修复**：添加了6个数据库的创建语句
```sql
CREATE DATABASE IF NOT EXISTS `db_user` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `db_product` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `db_cart` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `db_order` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `db_after_sale` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `db_admin` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

---

### 3. 数据库名称统一

**检查项**：
- ✅ `bundaberg.sql` - 使用 `db_*` 格式
- ✅ `shop-user/application.yml` - 使用 `db_user`
- ✅ `shop-product/application.yml` - 使用 `db_product`
- ✅ `shop-order/application.yml` - 使用 `db_order`
- ✅ `shop-payment/application.yml` - 使用 `db_payment`

**结论**：所有配置文件已统一使用 `db_*` 格式 ✅

---

## 📋 SQL 文件清单

| 文件 | 用途 | 状态 | 说明 |
|------|------|------|------|
| `bundaberg.sql` | 数据库和用户初始化 | ✅ 已修复 | 创建6个数据库+6个用户 |
| `db_admin.sql` | 管理员模块数据 | ✅ 已修复 | admin, role, admin_log 表 |
| `db_cart.sql` | 购物车模块数据 | ✅ 正常 | cart 表+测试数据 |
| `db_after_sale.sql` | 售后模块数据 | ✅ 正常 | after_sale, after_sale_evidence 表 |
| `README.md` | 数据库使用文档 | ✅ 新增 | 完整的初始化指南 |

---

## 🗄️ 数据库结构总览

### 1. db_user（用户数据库）
```
表：user
来源：frontend/shop-user/src/main/resources/db/init.sql
```

### 2. db_product（商品数据库）
```
表：product
来源：frontend/shop-product/src/main/resources/db/init.sql
```

### 3. db_order（订单数据库）
```
表：order, order_item
来源：frontend/shop-order/src/main/resources/db/init.sql
```

### 4. db_payment（支付数据库）
```
表：payment
来源：frontend/shop-payment/src/main/resources/db/init.sql
```

### 5. db_cart（购物车数据库）
```
表：cart
来源：sql/db_cart.sql
```

### 6. db_after_sale（售后数据库）
```
表：after_sale, after_sale_evidence
来源：sql/db_after_sale.sql
```

### 7. db_admin（管理员数据库）
```
表：admin, role, admin_log
来源：sql/db_admin.sql
```

---

## 🚀 快速初始化命令

### 一键初始化所有数据库

```bash
# 1. 创建数据库和用户
mysql -u root -p < sql/bundaberg.sql

# 2. 导入管理员数据
mysql -u root -p db_admin < sql/db_admin.sql

# 3. 导入购物车数据
mysql -u root -p db_cart < sql/db_cart.sql

# 4. 导入售后数据
mysql -u root -p db_after_sale < sql/db_after_sale.sql

# 5. 导入服务内置数据库
mysql -u root -p < frontend/shop-user/src/main/resources/db/init.sql
mysql -u root -p < frontend/shop-product/src/main/resources/db/init.sql
mysql -u root -p < frontend/shop-order/src/main/resources/db/init.sql
mysql -u root -p < frontend/shop-payment/src/main/resources/db/init.sql
```

### 验证导入结果

```sql
-- 查看所有数据库
SHOW DATABASES LIKE 'db_%';

-- 查看各数据库的表
USE db_admin; SHOW TABLES;
USE db_cart; SHOW TABLES;
USE db_after_sale; SHOW TABLES;

-- 查看测试数据
SELECT * FROM db_admin.admin;
SELECT * FROM db_admin.role;
SELECT COUNT(*) FROM db_cart.cart;
SELECT COUNT(*) FROM db_after_sale.after_sale;
```

---

## 📊 测试数据说明

### 管理员账户
| 用户名 | 密码 | 角色 | 姓名 |
|--------|------|------|------|
| 超级管理员 | 123456 | 超级管理员 | 张三 |
| 商家 | 123456 | 商家 | 李四 |

### 角色权限
- **超级管理员**：`["*"]`（所有权限）
- **商家**：`["product:list","product:add","product:edit","product:delete","product:status","order:list","order:detail","order:delivery","after_sale:list","after_sale:handle","statistics:view"]`

### 购物车数据
- 10条测试记录
- 覆盖5个用户（user_id: 1-5）

### 售后数据
- 5条售后申请
- 4种状态（待审核、审核通过、审核拒绝、退款完成）
- 8条凭证记录

---

## ⚠️ 注意事项

### 1. 字符集
所有数据库统一使用：
- 字符集：`utf8mb4`
- 排序规则：`utf8mb4_unicode_ci`

### 2. 数据库用户
为每个服务创建了专用用户，权限隔离：
```sql
user_service → db_user
product_service → db_product
cart_service → db_cart
order_service → db_order
after_sale_service → db_after_sale
admin_service → db_admin
```

### 3. 服务配置
当前所有服务使用 `root` 用户连接数据库（开发环境），生产环境应改用专用用户：

```yaml
spring:
  datasource:
    username: user_service  # 改为专用用户
    password: user_pass_2026
```

---

## 🎯 修复成果

✅ **1个严重错误已修复** - db_admin.sql JSON 数据重复
✅ **1个重要改进** - bundaberg.sql 添加数据库创建语句
✅ **数据库名称已统一** - 全部使用 `db_*` 格式
✅ **新增完整文档** - sql/README.md 初始化指南
✅ **所有配置文件已验证** - 无冲突

现在可以顺利执行所有 SQL 脚本了！🎉
