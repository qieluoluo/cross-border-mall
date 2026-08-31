# 物流功能测试指南

## 一、环境准备

### 1. 数据库初始化

```sql
-- 1. 创建物流数据库
CREATE DATABASE IF NOT EXISTS db_express DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 创建支付数据库
CREATE DATABASE IF NOT EXISTS db_payment DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 3. 执行SQL文件
USE db_express;
source /path/to/db_express.sql;

USE db_payment;
source /path/to/db_payment.sql;
```

### 2. 服务启动顺序

```bash
# 1. 启动 Nacos (服务注册中心)
startup.cmd -m standalone

# 2. 启动 Gateway (网关)
cd shop-gateway
mvn spring-boot:run

# 3. 启动各业务服务
cd shop-user && mvn spring-boot:run
cd shop-product && mvn spring-boot:run
cd shop-order && mvn spring-boot:run
cd shop-payment && mvn spring-boot:run
cd shop-express && mvn spring-boot:run
```

## 二、API 接口测试

### 1. 支付功能测试

#### 1.1 创建支付宝支付
```bash
curl -X POST http://localhost:8888/api/payment/alipay/create \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 100,
    "amount": 1999.00,
    "subject": "iPhone 15 Pro",
    "body": "订单支付"
  }'
```

**预期响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "paymentUrl": "https://openapi.alipaydev.com/gateway.do?...",
    "outTradeNo": "ORDER123456789",
    "paymentId": 1
  }
}
```

#### 1.2 支付宝异步回调
```bash
curl -X POST http://localhost:8888/api/payment/alipay/notify \
  -d "out_trade_no=ORDER123456789" \
  -d "trade_no=2024XXXXXX" \
  -d "trade_status=TRADE_SUCCESS" \
  -d "sign=xxx"
```

**预期响应：** `success` 或 `failure`

#### 1.3 支付宝同步回调
```bash
curl -X GET "http://localhost:8888/api/payment/alipay/return?out_trade_no=ORDER123456789&trade_no=2024XXXXXX"
```

### 2. 物流功能测试

#### 2.1 查询快递物流信息
```bash
# 查询顺丰快递
curl -X GET "http://localhost:8888/api/express/query?expressNo=SF123456789CN&expressCompany=SF"

# 查询圆通快递
curl -X GET "http://localhost:8888/api/express/query?expressNo=YT987654321CN&expressCompany=YTO"
```

**预期响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "success": true,
    "expressNo": "SF123456789CN",
    "expressCompany": "顺丰速运",
    "status": "3",
    "traces": [
      {
        "AcceptTime": "2024-01-15 14:30:00",
        "AcceptStation": "快件已签收，签收人：本人"
      },
      {
        "AcceptTime": "2024-01-15 10:20:00",
        "AcceptStation": "快件正在派送中，派送员：张师傅 138****8888"
      }
    ]
  }
}
```

#### 2.2 根据订单ID查询物流
```bash
curl -X GET http://localhost:8888/api/express/order/1
```

**预期响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "orderId": 1,
    "expressNo": "SF123456789CN",
    "expressCompany": "顺丰速运",
    "status": "delivered",
    "latestTime": "2025-02-05 14:30:00",
    "latestStatus": "快件已签收，签收人：本人"
  }
}
```

#### 2.3 更新订单物流信息
```bash
curl -X POST http://localhost:8888/api/express/update \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 100,
    "expressNo": "SF999888777CN",
    "expressCompany": "SF"
  }'
```

**预期响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "快递信息更新成功"
}
```

#### 2.4 获取支持的快递公司列表
```bash
curl -X GET http://localhost:8888/api/express/companies
```

**预期响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {"code": "SF", "name": "顺丰速运"},
    {"code": "YTO", "name": "圆通速递"},
    {"code": "ZTO", "name": "中通快递"},
    {"code": "YD", "name": "韵达快递"},
    {"code": "EMS", "name": "EMS"},
    {"code": "JD", "name": "京东物流"}
  ]
}
```

## 三、支持的快递公司编码

| 编码 | 名称 | 客服电话 |
|------|------|----------|
| SF | 顺丰速运 | 95338 |
| YTO | 圆通速递 | 95554 |
| ZTO | 中通快递 | 95311 |
| YD | 韵达快递 | 95546 |
| EMS | EMS | 11183 |
| JD | 京东物流 | 950616 |
| TTKDEX | 天天快递 | 400-188-8888 |
| HTKY | 百世快递 | 95320 |
| POST | 中国邮政 | 11185 |
| DB | 德邦快递 | 95353 |

## 四、物流状态说明

| 状态 | 说明 |
|------|------|
| pending | 待发货 |
| transit | 运输中 |
| delivered | 已签收 |
| exception | 异常 |

## 五、常见问题排查

### 1. 服务启动失败

**问题：** `Property 'sqlSessionFactory' or 'sqlSessionTemplate' are required`

**解决：** 
- 检查 MyBatis Plus 配置类是否存在
- 确认 `@MapperScan` 注解配置正确
- 检查数据库连接配置

### 2. 物流查询返回模拟数据

**原因：** 快递鸟 API 未配置或调用失败

**解决：**
- 在 `application.yml` 中配置正确的快递鸟 API Key
- 或者使用模拟数据进行测试

### 3. 支付宝支付跳转失败

**原因：** 支付宝沙箱环境配置问题

**解决：**
- 确认已配置正确的 `ALIPAY_APP_ID`
- 确认已配置正确的私钥和公钥
- 使用支付宝沙箱环境进行测试

## 六、集成测试流程

```
1. 用户下单 -> 创建订单
2. 用户支付 -> 调用 /api/payment/alipay/create
3. 支付成功 -> 支付宝回调更新订单状态
4. 商家发货 -> 调用 /api/express/update 更新物流信息
5. 用户查询 -> 调用 /api/express/order/{orderId} 查看物流
6. 物流跟踪 -> 调用 /api/express/query 实时查询物流状态
```

## 七、Postman 测试集合

推荐使用 Postman 进行接口测试，可以导入以下环境变量：

```json
{
  "base_url": "http://localhost:8888/api",
  "gateway_url": "http://localhost:8888"
}
```
