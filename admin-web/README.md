# Bundaberg 后台管理系统前端

## 项目介绍
基于 Vue 3 + Element Plus 的电商后台管理系统前端页面。

## 技术栈
- **前端框架**: Vue 3
- **UI 组件库**: Element Plus
- **构建工具**: Vite 5
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP 请求**: Axios
- **图表**: ECharts

## 功能模块
- ✅ 登录认证
- ✅ 数据看板（统计卡片、订单趋势图、商品分类饼图）
- ✅ 管理员管理（增删改查、分页、搜索）
- ✅ 角色管理（增删改查）
- ✅ 用户管理（列表查询、详情查看、状态管理）
- ✅ 商品管理（列表查询、详情查看、上下架）
- ✅ 订单管理（列表查询、订单详情、状态筛选）
- ✅ 购物车管理（查看、删除）
- ✅ 售后服务（列表查询、详情查看、售后处理）

## 快速开始

### 1. 环境要求
- Node.js 16+
- npm 或 yarn

### 2. 安装依赖
```bash
cd frontend/admin-web
npm install
```

### 3. 配置后端地址
编辑 `.env` 文件，修改后端 API 地址：
```
VITE_API_BASE_URL=http://localhost:9999
```

### 4. 启动开发服务器
```bash
npm run dev
```

访问地址：`http://localhost:3000`

### 5. 构建生产版本
```bash
npm run build
```

## 默认账号
- 用户名：`admin`
- 密码：`admin123`

## 项目结构
```
admin-web/
├── src/
│   ├── api/              # API 接口
│   ├── layout/           # 布局组件
│   ├── router/           # 路由配置
│   ├── stores/           # 状态管理
│   ├── utils/            # 工具函数
│   ├── views/            # 页面组件
│   ├── App.vue           # 根组件
│   └── main.js           # 入口文件
├── index.html
├── package.json
├── vite.config.js
└── .env
```

## 注意事项
1. 需要先启动后端服务（端口 9999）
2. 前端开发服务器端口为 3000
3. 已配置代理，开发时会自动转发 API 请求到后端
