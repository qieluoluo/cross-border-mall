# cross-border-mall（Bundaberg 跨境电商）

这是一个跨境电商课程项目，包含 **用户商城前端、后台管理前端、微信小程序、后台管理服务、商城微服务**。

按下面步骤做，可以从零把项目跑起来。

---

## 一、项目怎么组成

| 目录 | 是什么 | 访问地址 |
|------|--------|----------|
| `frontend-web` | 用户商城网页 | http://localhost:5173 |
| `admin-web` | 后台管理网页 | http://127.0.0.1:18080 |
| `miniapp/小程序项目` | 微信小程序 | 微信开发者工具打开 |
| `backend-admin` | 后台管理接口 | http://localhost:9999 |
| `frontend/shop-gateway` | 网关（小程序走这里） | http://localhost:8888 |
| `frontend/shop-*` | 用户、商品、订单等微服务 | 见下方端口表 |
| `frontend/sql` | 数据库脚本 | — |
| `common` | Java 公共模块 | — |

商城微服务端口：

| 服务 | 端口 | 数据库 |
|------|------|--------|
| shop-product | 8001 | db_product |
| shop-order | 8003 | db_order |
| shop-payment | 8004 | db_payment |
| shop-user | 8005 | db_user |
| shop-cart | 8006 | db_cart |
| shop-after-sale | 8007 | db_after_sale |
| shop-express | 8008 | db_express |
| shop-gateway | 8888 | 无 |
| backend-admin | 9999 | db_admin |

---

## 二、你电脑上先装这些

1. **JDK 17**（在 IDEA 里看 Project SDK 是 17）
2. **Maven 3.8+**（IDEA 自带也可以）
3. **MySQL 8.0**，并保证能用账号 `root` / 密码 `123456` 登录  
   如果你的 MySQL 密码不是 `123456`，先改成这个，或把所有 `application.yml` 里的 `password` 改成你自己的密码。
4. **Node.js 18+**（安装后新开一个终端，执行 `node -v` 能看到版本）
5. **Nacos 2.3 或 2.4**（网关和小程序必须用）  
   下载：https://github.com/alibaba/nacos/releases  
   选 `nacos-server-2.3.2.zip` 或相近版本，解压到任意目录，例如 `D:\nacos`
6. 用 **IntelliJ IDEA** 打开本仓库根目录（有 `pom.xml` 的那一层）

---

## 三、导入数据库（只做一次）

### 方法 A：用 Navicat / MySQL Workbench（推荐）

依次执行 `frontend/sql` 下这些文件，**顺序不要乱**：

1. `bundaberg.sql`（创建数据库）
2. `db_admin.sql`
3. `db_user.sql`
4. `db_product.sql`
5. `db_cart.sql`
6. `db_order.sql`
7. `db_payment.sql`
8. `db_after_sale.sql`
9. `db_express.sql`

每个文件用「运行 SQL 文件」导入即可。

### 方法 B：命令行

先确认终端里能执行 `mysql`。然后在项目根目录执行：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\init-db.ps1
```

导入成功后，可用账号：

| 端 | 用户名 | 密码 |
|----|--------|------|
| 后台管理 | `admin` | `admin123` |
| 后台管理 | `超级管理员` | `123456` |
| 后台管理 | `商家` | `123456` |
| 用户商城 | `zhangwei` | `123456` |
| 用户商城 | `lina` | `123456` |

用户端也可以自己注册新账号。

---

## 四、启动 Nacos

打开一个新的 PowerShell / CMD：

```bat
cd D:\nacos\bin
startup.cmd -m standalone
```

浏览器打开 http://localhost:8848/nacos

- 默认账号：`nacos`
- 默认密码：`nacos`

能打开登录页就说明 Nacos 已启动。后面启动微服务时，这里会陆续出现 `shop-user`、`shop-product` 等服务名。

---

## 五、用 IDEA 启动后端（按这个顺序）

1. 用 IDEA 打开本仓库根目录，等待 Maven 依赖下载完成。  
   如果右下角提示 Trust Project / Load Maven Project，点允许。
2. 先编译一次，菜单：`Maven` → 根项目 `bundaberg` → `Lifecycle` → `install`  
   或在项目根目录终端执行：

```powershell
mvn -DskipTests install
```

3. 按下面顺序启动主类（每个都点绿色三角 Run）：

| 顺序 | 模块 | 主类 |
|------|------|------|
| 1 | shop-user | `com.delmon.UserApplication` |
| 2 | shop-product | `com.delmon.ProductApplication` |
| 3 | shop-order | `com.delmon.OrderApplication` |
| 4 | shop-payment | `com.delmon.PaymentApplication` |
| 5 | shop-cart | `com.delmon.CartApplication` |
| 6 | shop-after-sale | `com.delmon.AfterSaleApplication` |
| 7 | shop-express | `com.delmon.ExpressApplication` |
| 8 | shop-gateway | `com.delmon.gateway.GatewayApplication` |
| 9 | backend-admin | `com.delmon.AdminApplication` |

启动成功的标志：控制台最后出现 `Started xxxApplication`，且没有红色报错。

只做后台管理演示时，至少启动：`backend-admin` + 你要点开的业务服务（商品页就要 `shop-product`）。  
用户商城网页至少启动：`shop-user`、`shop-product`、`shop-cart`、`shop-order`。  
小程序还要再启动 `shop-gateway`，并且 Nacos 必须在跑。

---

## 六、启动用户商城网页

再开一个终端：

```powershell
cd frontend-web
npm install
npm run dev
```

浏览器打开：http://localhost:5173

用 `zhangwei` / `123456` 登录，或先注册再登录。

---

## 七、启动后台管理网页

再开一个终端：

```powershell
cd admin-web
npm install
npm run dev
```

浏览器打开：http://127.0.0.1:18080

用 `admin` / `admin123` 登录。

---

## 八、启动微信小程序（可选）

1. 打开微信开发者工具
2. 导入目录：`miniapp/小程序项目`
3. 详情里关闭「校验合法域名」（项目已配置 `urlCheck: false`）
4. 小程序请求的是网关 `http://localhost:8888`，所以 **Nacos + 网关 + 各微服务** 都要先启动

---

## 九、怎么确认已经跑通

1. Nacos 控制台能看到各个 `shop-*` 服务
2. 浏览器打开 http://localhost:9999/admin/list 能返回 JSON（后台接口活了）
3. http://localhost:5173 能打开商城首页、能登录
4. http://127.0.0.1:18080 能登录后台、能看到管理员列表

---

## 十、常见问题

**1. 后端启动报找不到 `com.delmon:bundaberg`**  
在仓库根目录执行 `mvn -DskipTests install`，不要只打开某个子模块。

**2. 后端报连不上 MySQL**  
检查 MySQL 是否启动，以及 `root` 密码是不是 `123456`。数据库名必须是 `db_user`、`db_product` 这些，不是 `example_db`。

**3. 网关或小程序报服务找不到**  
先确认 Nacos 已启动，再确认对应微服务已注册到 Nacos。用户商城网页是直连各服务端口的，可以不依赖网关。

**4. 后台登录失败**  
确认导入的是 `db_admin.sql`，并用 `admin` / `admin123`。

**5. 用户登录失败**  
确认导入了最新的 `db_user.sql`，密码是明文 `123456`。不要用旧的加密密文数据。

**6. `npm install` 很慢或失败**  
可先执行：

```powershell
npm config set registry https://registry.npmmirror.com
```

**7. 推送 GitHub 失败（Connection reset）**  
国内访问 GitHub 不稳定时，在项目目录用：

```powershell
git -c http.version=HTTP/1.1 push origin main
```

---

## 十一、建议的演示顺序

1. 后台登录 → 看数据看板、用户、商品、订单
2. 用户商城注册/登录 → 浏览商品 → 加入购物车 → 下单
3. 回到后台处理订单 / 售后
4. 有时间再演示小程序同一套接口
