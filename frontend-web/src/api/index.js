import axios from 'axios'
import { ElMessage } from 'element-plus'
import { normalizeProductFields } from '../utils/image'
import { filterProductsByCategory, paginate, sortProducts } from '../utils/category'

const instance = axios.create({
  baseURL: '/api',
  timeout: 12000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

instance.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

instance.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200 || res.code === 0 || res.success === true) {
      return res
    }
    return Promise.reject(new Error(res.message || res.msg || '请求失败'))
  },
  (error) => {
    if (error.response?.status === 401) {
      ElMessage.error('登录已失效，请重新登录')
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
    }
    const message = error.response?.data?.message || error.message || '网络错误'
    return Promise.reject(new Error(message))
  }
)

export const getCurrentUserId = () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return null
  const uid = parseInt(userId, 10)
  return Number.isNaN(uid) ? null : uid
}

const ORDER_ITEMS_KEY = 'order_items_cache'

const readOrderItemCache = () => {
  try {
    return JSON.parse(localStorage.getItem(ORDER_ITEMS_KEY) || '{}')
  } catch {
    return {}
  }
}

const cacheOrderItems = (orderId, items) => {
  const uid = getCurrentUserId()
  if (!uid || !orderId) return
  const all = readOrderItemCache()
  all[`${uid}_${orderId}`] = items
  localStorage.setItem(ORDER_ITEMS_KEY, JSON.stringify(all))
}

const getCachedOrderItems = (orderId) => {
  const uid = getCurrentUserId()
  if (!uid || !orderId) return []
  const items = readOrderItemCache()[`${uid}_${orderId}`] || []
  return items.map((item) => normalizeProductFields({ ...item }))
}

const attachOrderItems = (order) => {
  if (!order) return order
  const items = (order.items && order.items.length > 0)
    ? order.items.map((item) => normalizeProductFields({ ...item }))
    : getCachedOrderItems(order.id)
  return { ...order, items }
}

export const productAPI = {
  getCategories: () => instance.get('/product/category/list'),

  getProductList: async (pageNum = 1, pageSize = 10, categoryId, sort) => {
    const categoriesRes = await productAPI.getCategories().catch(() => ({ data: [] }))
    const categories = categoriesRes.data || []
    const needAll = Boolean(categoryId) || (sort && sort !== 'default')
    const res = await instance.get('/product/list', {
      params: {
        pageNum: needAll ? 1 : pageNum,
        pageSize: needAll ? 200 : pageSize
      }
    })

    let records = (res.data?.records || []).map((item) => normalizeProductFields({ ...item }))
    records = filterProductsByCategory(records, categories, categoryId)
    records = sortProducts(records, sort)

    if (needAll) {
      return {
        code: 200,
        data: paginate(records, pageNum, pageSize)
      }
    }

    return {
      code: 200,
      data: {
        ...res.data,
        records,
        total: res.data?.total ?? records.length
      }
    }
  },

  getProductById: async (id) => {
    const res = await instance.get(`/product/${id}`)
    return {
      code: 200,
      data: normalizeProductFields({ ...res.data })
    }
  },

  searchProducts: async (keyword, pageNum = 1, pageSize = 10) => {
    const res = await instance.get('/product/search', {
      params: { keyword, pageNum, pageSize, status: 1 }
    })
    const data = res.data || {}
    const records = (data.records || []).map((item) => normalizeProductFields({ ...item }))
    return {
      code: 200,
      data: {
        ...data,
        records,
        total: data.total ?? records.length
      }
    }
  }
}

export const userAPI = {
  login: async (username, password) => {
    const res = await instance.post('/user/login', { username, password })
    let token = ''
    let userId = null

    if (typeof res.data === 'string') {
      token = res.data
      const underscoreIndex = res.data.indexOf('_')
      if (underscoreIndex > 0) {
        userId = parseInt(res.data.substring(0, underscoreIndex), 10)
      }
    } else {
      token = res.data?.token || res.data?.accessToken || ''
      userId = res.data?.userId || res.data?.id || null
    }

    return {
      code: 200,
      data: { token, userId }
    }
  },

  register: (user) => instance.post('/user/register', user),

  getUserInfo: (userId) => instance.get(`/user/${userId || localStorage.getItem('userId')}`),

  updateUserInfo: (user) => {
    const userId = user.id || localStorage.getItem('userId')
    if (!userId) {
      return Promise.reject(new Error('请先登录'))
    }
    return instance.put(`/user/${userId}`, {
      nickname: user.nickname,
      email: user.email,
      phone: user.phone,
      gender: user.gender != null ? Number(user.gender) : undefined
    })
  }
}

export const addressAPI = {
  list: (userId = getCurrentUserId()) => instance.get(`/user-address/list/${userId}`),
  add: (address) => instance.post('/user-address/add', address),
  update: (address) => instance.put('/user-address/update', address),
  remove: (id) => instance.delete(`/user-address/delete/${id}`),
  setDefault: (id, userId = getCurrentUserId()) => instance.put(`/user-address/default/${userId}/${id}`)
}

const formatAddress = (address) => {
  if (!address) return ''
  return `${address.province || ''}${address.city || ''}${address.district || ''}${address.detailAddress || ''}`
}

export const cartAPI = {
  getCartList: async () => {
    const uid = getCurrentUserId()
    if (!uid) {
      return { code: 200, data: [] }
    }
    const res = await instance.get('/cart/list', {
      params: { userId: uid, pageNum: 1, pageSize: 100 }
    })
    const records = (res.data?.records || [])
      .filter((item) => Number(item.userId) === uid)
      .map((item) => normalizeProductFields({ ...item }))
    return { code: 200, data: records }
  },

  addToCart: (productId, quantity = 1) => {
    const uid = getCurrentUserId()
    if (!uid) {
      return Promise.reject(new Error('请先登录'))
    }
    return instance.post('/cart/add', {
      userId: uid,
      productId,
      skuId: 1,
      quantity
    })
  },

  updateCart: (cartId, quantity) => instance.post('/cart/update', {
    id: Number(cartId),
    quantity
  }),

  deleteCart: (cartId) => instance.delete(`/cart/delete/${cartId}`),

  clearCart: async () => {
    const listRes = await cartAPI.getCartList()
    const items = listRes.data || []
    await Promise.all(items.map((item) => instance.delete(`/cart/delete/${item.id}`)))
    return { code: 200 }
  }
}

export const orderAPI = {
  createOrder: async (order) => {
    const uid = getCurrentUserId()
    if (!uid) {
      return Promise.reject(new Error('请先登录'))
    }

    const items = (order.items || []).map((item) => normalizeProductFields({ ...item }))
    const payload = {
      ...order,
      userId: uid,
      items
    }

    const res = await instance.post('/order/create', payload)
    const orderId = res.data?.id || res.data
    cacheOrderItems(orderId, items)
    return { code: 200, data: { id: orderId } }
  },

  getOrderList: async (status) => {
    const uid = getCurrentUserId()
    const res = await instance.get('/order/list', {
      params: {
        pageNum: 1,
        pageSize: 100,
        status: status === '' || status === undefined ? undefined : status
      }
    })
    let records = res.data?.records || res.data || []
    if (uid) {
      records = records.filter((order) => !order.userId || Number(order.userId) === uid)
    }
    if (status !== '' && status !== undefined) {
      records = records.filter((order) => Number(order.status) === Number(status))
    }
    return {
      code: 200,
      data: records.map(attachOrderItems)
    }
  },

  getOrderById: async (id) => {
    const res = await instance.get(`/order/${id}`)
    return {
      code: 200,
      data: attachOrderItems(res.data)
    }
  },

  updateOrder: (order) => instance.put('/order/update', order),

  cancelOrder: (orderId) => instance.put(`/order/cancel/${orderId}`),

  confirmOrder: (orderId) => instance.put('/order/update', { id: orderId, status: 3 })
}

export const paymentAPI = {
  createAlipayPayment: (orderId, amount) => instance.post('/payment/alipay/create', { orderId, amount }),
  createStripePayment: (orderId, amount) => instance.post('/payment/stripe/create-intent', { orderId, amount }),
  createPayPalPayment: (orderId, amount) => instance.post('/payment/paypal/create', { orderId, amount })
}

export const buildReceiverFromAddress = (address) => ({
  addressId: address.id,
  receiverName: address.receiverName,
  receiverPhone: address.receiverPhone,
  receiverAddress: formatAddress(address)
})

export default instance
