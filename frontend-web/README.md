# 电商前端项目

基于 Vue 3 + Vite + Element Plus 构建的电商前端应用。

## 技术栈

- **框架**: Vue 3 (Composition API)
- **构建工具**: Vite 5
- **UI 组件**: Element Plus
- **路由**: Vue Router 4
- **样式**: Tailwind CSS 3
- **HTTP 客户端**: Axios

## 快速开始

### 环境要求

- Node.js >= 18.0.0
- JDK 17 (用于运行 Nacos)
- Maven 3.8+ (用于运行后端服务)
- MySQL 8.0+

### 步骤

1. **克隆项目**

```bash
git clone <repository-url>
cd the-mountain-cannot-run/frontend-web
```

2. **环境配置**

运行自动配置脚本，会自动检查环境、下载 Nacos 并安装依赖：

```bash
npm run setup
```

3. **数据库配置**

确保 MySQL 数据库已配置：
- 数据库名: `example_db`
- 用户名: `root`
- 密码: `123456`

4. **启动服务**

运行一站式启动脚本：

```bash
npm run start:all
```

或者手动启动：

```bash
# 启动 Nacos
cd ../nacos/bin
startup.cmd -m standalone

# 启动后端服务（在 IDE 中运行各 Spring Boot 应用）
# shop-gateway, shop-user, shop-product, shop-cart, shop-order, shop-payment

# 启动前端
npm run dev
```

5. **访问应用**

打开浏览器访问: http://localhost:5173

## 项目结构

```
frontend-web/
├── src/
│   ├── api/           # API 请求封装
│   ├── components/    # 公共组件
│   ├── router/        # 路由配置
│   ├── utils/         # 工具函数
│   ├── views/         # 页面组件
│   ├── App.vue        # 根组件
│   ├── main.js        # 入口文件
│   └── style.css      # 全局样式
├── scripts/
│   ├── setup.js       # 环境配置脚本
│   └── start-all.js   # 一站式启动脚本
├── .env               # 环境变量
├── vite.config.js     # Vite 配置
├── tailwind.config.js # Tailwind 配置
└── package.json       # 项目配置
```

## 功能模块

- **首页**: 商品展示、分类导航
- **商品列表**: 商品搜索、筛选、排序
- **商品详情**: 商品信息、加入购物车
- **购物车**: 商品管理、数量调整、结算
- **订单**: 订单列表、订单详情
- **用户中心**: 用户信息、个人设置
- **登录/注册**: 用户认证

## 脚本命令

| 命令 | 说明 |
|------|------|
| `npm run dev` | 启动开发服务器 |
| `npm run build` | 构建生产版本 |
| `npm run preview` | 预览生产版本 |
| `npm run setup` | 环境配置（下载依赖、Nacos） |
| `npm run start:all` | 启动所有服务 |

## 配置说明

### 环境变量

在 `.env` 文件中配置：

```env
# API 基础路径
VITE_API_BASE_URL=/api

# 网关服务地址
VITE_GATEWAY_URL=http://localhost:8888
```

### 代理配置

`vite.config.js` 中配置了后端服务代理：

- `/api/user` → http://localhost:8005
- `/api/product` → http://localhost:8002
- `/api/cart` → http://localhost:8006
- `/api/order` → http://localhost:8003
- `/api/payment` → http://localhost:8004

## 开发说明

### 代码规范

- 使用 Vue 3 Composition API
- 使用 ES Module 语法
- 使用 Tailwind CSS 进行样式开发
- 组件命名使用 PascalCase
- 文件命名使用 kebab-case

### 提交规范

- feat: 新增功能
- fix: 修复 bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 代码重构
- test: 测试用例

## License

MIT
