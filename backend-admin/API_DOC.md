# Backend Admin API 文档

## 项目概述
后台管理系统 - 提供管理员管理、角色管理、操作日志等功能

## 技术栈
- Spring Boot 3.2.0
- MyBatis Plus 3.5.5
- MySQL 8.0+
- Lombok

## API 接口列表

### 1. 管理员管理

#### 1.1 创建管理员
- **接口**: `POST /admin/create-admin`
- **描述**: 创建新的管理员账号
- **请求体**:
```json
{
  "username": "test_admin",
  "password": "123456",
  "realName": "测试管理员",
  "roleId": 1,
  "status": 1
}
```
- **响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "username": "test_admin",
    "password": "123456",
    "realName": "测试管理员",
    "roleId": 1
  },
  "timestamp": 1234567890
}
```

#### 1.2 更新管理员信息
- **接口**: `PUT /admin/update-admin`
- **描述**: 更新管理员信息
- **请求体**:
```json
{
  "id": "1",
  "realName": "新名字",
  "password": "newpassword123",
  "status": 1,
  "roleId": 2
}
```

#### 1.3 根据 ID 查询管理员
- **接口**: `GET /admin/admin/{id}`
- **描述**: 根据 ID 查询管理员详情
- **路径参数**: `id` - 管理员 ID
- **响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "系统管理员",
    "status": 1,
    "roleId": 1,
    "lastLoginIp": "192.168.1.1",
    "lastLoginTime": "2024-01-01T10:00:00",
    "createTime": "2024-01-01T09:00:00"
  }
}
```

#### 1.4 分页查询管理员
- **接口**: `GET /admin/list`
- **描述**: 分页查询管理员列表，支持按用户名模糊搜索
- **请求参数**:
  - `pageNum`: 页码（默认 1）
  - `pageSize`: 每页大小（默认 10）
  - `username`: 用户名（可选，支持模糊查询）
- **响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "username": "admin",
        "realName": "系统管理员",
        "status": 1,
        "roleId": 1
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

#### 1.5 管理员登录
- **接口**: `POST /admin/login-admin`
- **描述**: 管理员登录
- **请求体**:
```json
{
  "username": "admin",
  "password": "admin123",
  "code": "" 
}
```
- **响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "系统管理员",
    "status": 1,
    "roleId": 1,
    "lastLoginIp": "192.168.1.1",
    "lastLoginTime": "2024-01-01T10:00:00"
  }
}
```

#### 1.6 删除管理员
- **接口**: `DELETE /admin/admin/{id}`
- **描述**: 删除指定 ID 的管理员
- **路径参数**: `id` - 管理员 ID

#### 1.7 更新管理员状态
- **接口**: `PUT /admin/admin/{id}/status`
- **描述**: 启用或禁用管理员账号
- **路径参数**: `id` - 管理员 ID
- **请求参数**: `status` - 状态（0 禁用，1 启用）

### 2. 角色管理

#### 2.1 创建角色
- **接口**: `POST /admin/role/create`
- **描述**: 创建新角色
- **请求体**:
```json
{
  "name": "超级管理员",
  "description": "拥有所有权限"
}
```

#### 2.2 更新角色
- **接口**: `PUT /admin/role/update`
- **描述**: 更新角色信息
- **请求体**:
```json
{
  "id": 1,
  "name": "高级管理员",
  "description": "高级管理权限"
}
```

#### 2.3 根据 ID 查询角色
- **接口**: `GET /admin/role/{id}`
- **描述**: 根据 ID 查询角色详情
- **路径参数**: `id` - 角色 ID

#### 2.4 查询所有角色
- **接口**: `GET /admin/role/list`
- **描述**: 查询所有角色列表
- **响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "超级管理员",
      "description": "拥有所有权限"
    },
    {
      "id": 2,
      "name": "普通管理员",
      "description": "普通管理权限"
    }
  ]
}
```

#### 2.5 删除角色
- **接口**: `DELETE /admin/role/{id}`
- **描述**: 删除指定 ID 的角色
- **路径参数**: `id` - 角色 ID

## 数据字典

### 管理员状态 (status)
- `0`: 禁用
- `1`: 启用

### 结果码说明
- `200`: 操作成功
- `400`: 请求参数错误
- `401`: 未登录或登录已过期
- `403`: 没有权限访问
- `404`: 资源不存在
- `500`: 系统内部错误
- `1001`: 业务处理失败
- `1002`: 用户不存在
- `1003`: 密码错误
- `1004`: 账号已被禁用
- `1005`: 角色不存在
- `2001`: 数据不存在
- `2002`: 数据已存在

## 数据库初始化

执行 `src/main/resources/db/init.sql` 脚本初始化数据库

### 默认账号
- 用户名：`admin`
- 密码：`admin123`

## 注意事项

1. 密码目前为明文存储，生产环境建议使用 BCrypt 等加密方式
2. 登录功能后续需要添加 JWT 认证
3. 操作日志记录功能需要完善，当前仅记录基本信息
4. 需要添加权限验证机制
5. 建议添加验证码功能防止暴力破解

## 待开发功能

- [ ] JWT 令牌认证
- [ ] 密码加密存储
- [ ] 权限管理（基于角色的访问控制）
- [ ] 操作日志注解式记录
- [ ] 登录验证码
- [ ] 密码修改功能
- [ ] 账号找回功能
- [ ] 操作日志查询接口
