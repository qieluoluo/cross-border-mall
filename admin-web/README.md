# Bundaberg 后台管理系统前端

基于 Vue 3 + Element Plus 的电商后台管理系统。

## 技术栈

- Vue 3、Element Plus、Vite 5、Pinia、Vue Router 4、Axios、ECharts

## 功能模块

- 登录认证
- 数据看板
- 管理员 / 角色 / 用户管理
- 商品、订单、购物车、售后管理

## 快速开始

### 环境要求

- Node.js 18+
- 已启动 `backend-admin`（端口 9999）以及需要用到的商城微服务

### 安装并启动

```bash
cd admin-web
npm install
npm run dev
```

访问地址：`http://127.0.0.1:18080`

### 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 超级管理员 |
| 超级管理员 | 123456 | 超级管理员 |
| 商家 | 123456 | 商家 |

## 注意事项

1. 先启动后端 `backend-admin`（9999），商品/订单等页面还需要对应微服务。
2. 前端开发端口是 **18080**，已通过 Vite 代理转发到各后端。
3. 完整启动顺序见仓库根目录 `README.md`。
