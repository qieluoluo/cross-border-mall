import request from '@/utils/request'

// 管理员登录
export const loginApi = (data) => {
  return request({
    url: '/admin/login-admin',
    method: 'post',
    data
  })
}

// 获取管理员列表
export const getAdminListApi = (params) => {
  return request({
    url: '/admin/list',
    method: 'get',
    params: {
      pageNum: params.page || 1,
      pageSize: params.size || 10,
      username: params.username || '',
      status: params.status
    }
  })
}

// 创建管理员
export const createAdminApi = (data) => {
  return request({
    url: '/admin/create-admin',
    method: 'post',
    data
  })
}

// 更新管理员
export const updateAdminApi = (data) => {
  return request({
    url: '/admin/update-admin',
    method: 'put',
    data
  })
}

// 删除管理员
export const deleteAdminApi = (id) => {
  return request({
    url: `/admin/admin/${id}`,
    method: 'delete'
  })
}

// 获取管理员详情
export const getAdminByIdApi = (id) => {
  return request({
    url: `/admin/admin/${id}`,
    method: 'get'
  })
}

// 更新管理员状态（禁用/启用）
export const updateAdminStatusApi = (id, status) => {
  return request({
    url: `/admin/admin/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 获取角色列表
export const getRoleListApi = () => {
  return request({
    url: '/admin/role/list',
    method: 'get'
  })
}

// 创建角色
export const createRoleApi = (data) => {
  return request({
    url: '/admin/role/create',
    method: 'post',
    data
  })
}

// 更新角色
export const updateRoleApi = (data) => {
  return request({
    url: '/admin/role/update',
    method: 'put',
    data
  })
}

// 删除角色
export const deleteRoleApi = (id) => {
  return request({
    url: `/admin/role/${id}`,
    method: 'delete'
  })
}

// 获取用户列表
export const getUserListApi = (params) => {
  return request({
    url: '/user/list',
    method: 'get',
    params: {
      pageNum: params.page || 1,
      pageSize: params.size || 10,
      username: params.username || '',
      phone: params.phone || ''
    }
  })
}

// 更新用户状态（禁用/启用）
export const updateUserStatusApi = (id, status) => {
  return request({
    url: `/user/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 获取商品列表
export const getProductListApi = (params) => {
  return request({
    url: '/product/list',
    method: 'get',
    params: {
      pageNum: params.page || 1,
      pageSize: params.size || 10
    }
  })
}

// 更新商品状态（上架/下架）
export const updateProductStatusApi = (id, status) => {
  return request({
    url: '/product/update',
    method: 'put',
    data: { id, status }
  })
}

// 获取订单列表
export const getOrderListApi = (params) => {
  return request({
    url: '/order/list',
    method: 'get',
    params: {
      pageNum: params.page || 1,
      pageSize: params.size || 10,
      orderNo: params.orderNo || '',
      status: params.status
    }
  })
}

// 获取购物车列表
export const getCartListApi = (params) => {
  return request({
    url: '/cart/list',
    method: 'get',
    params: {
      pageNum: params.page || 1,
      pageSize: params.size || 10,
      username: params.username || ''
    }
  })
}

// 删除购物车项
export const deleteCartItemApi = (id) => {
  return request({
    url: `/cart/delete/${id}`,
    method: 'delete'
  })
}

// 获取物流信息
export const getExpressInfoApi = (orderId) => {
  return request({
    url: `/express/order/${orderId}`,
    method: 'get'
  })
}

// 获取售后列表
export const getAfterSaleListApi = (params) => {
  return request({
    url: '/after-sale/list',
    method: 'get',
    params: {
      pageNum: params.page || 1,
      pageSize: params.size || 10,
      orderNo: params.orderNo || '',
      status: params.status
    }
  })
}

// 处理售后（同意/拒绝）
export const handleAfterSaleApi = (id, action, reason) => {
  return request({
    url: `/after-sale/handle/${id}`,
    method: 'post',
    params: {
      action,
      reason: reason || ''
    }
  })
}

// 获取统计数据
export const getDashboardStatsApi = () => {
  return request({
    url: '/admin/dashboard/stats',
    method: 'get'
  })
}

// 添加商品
export const addProductApi = (data) => {
  return request({
    url: '/product/add',
    method: 'post',
    data
  })
}

// 删除商品
export const deleteProductApi = (id) => {
  return request({
    url: `/product/delete/${id}`,
    method: 'delete'
  })
}

// 批量导入商品
export const batchImportProductApi = (data) => {
  return request({
    url: '/product/batch-import',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 订单发货
export const deliveryOrderApi = (data) => {
  return request({
    url: '/order/delivery',
    method: 'post',
    data
  })
}

// 获取快递公司列表
export const getExpressCompaniesApi = () => {
  return request({
    url: '/express/companies',
    method: 'get'
  })
}

// 获取分类列表
export const getCategoryListApi = () => {
  return request({
    url: '/product/category/list',
    method: 'get'
  })
}

// 上传商品主图
export const uploadProductImageApi = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/product/image/upload',
    method: 'post',
    data: formData
  })
}
