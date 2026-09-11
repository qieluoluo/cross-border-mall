import axios from 'axios'
import { ElMessage } from 'element-plus'
import { normalizeProductFields, getProductImage } from '../utils/image'

const baseURL = '/api'

const instance = axios.create({
  baseURL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

instance.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

instance.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200 || res.code === 0 || res.success === true) {
      return res
    } else {
      const errorMessage = res.message || res.msg || '请求失败'
      return Promise.reject(new Error(errorMessage))
    }
  },
  error => {
    if (error.response) {
      const url = error.response.config?.url || ''
      
      switch (error.response.status) {
        case 401:
          ElMessage.error('未授权，请重新登录')
          localStorage.removeItem('token')
          localStorage.removeItem('userId')
          break
        case 403:
          ElMessage.error('拒绝访问')
          break
        case 404:
          break
        case 500:
          break
        default:
          break
      }
    } else if (error.request) {
      console.error('网络错误')
    } else {
      console.error('请求配置错误')
    }
    return Promise.reject(error)
  }
)


const mockProducts = [
  { id: 1, name: 'iPhone 15 Pro Max', subTitle: '256GB 原色钛金属', price: 9999, sales: 12580, stock: 1000, categoryId: 1, image: '/images/iphone15pro.png' },
  { id: 2, name: 'MacBook Pro 14寸', subTitle: 'M3 Pro芯片 18GB内存', price: 16999, sales: 5680, stock: 500, categoryId: 1, image: '/images/lianxiang.png' },
  { id: 3, name: 'AirPods Pro 2', subTitle: '主动降噪真无线耳机', price: 1899, sales: 23450, stock: 2000, categoryId: 1, image: '/images/iphone15pro.png' },
  { id: 4, name: 'iPad Air 11寸', subTitle: 'M3芯片 256GB WiFi版', price: 5999, sales: 8920, stock: 800, categoryId: 1, image: '/images/iphone15pro.png' },
  { id: 5, name: 'Apple Watch Ultra 2', subTitle: '49mm 钛金属表壳', price: 6499, sales: 3420, stock: 300, categoryId: 1, image: '/images/huawei60pro.png' },
  { id: 6, name: '华为Mate 60 Pro', subTitle: '5G 昆仑玻璃 512GB', price: 6999, sales: 18760, stock: 1200, categoryId: 1, image: '/images/huawei60pro.png' },
  { id: 7, name: '小米14 Pro', subTitle: '骁龙8 Gen3 5000mAh', price: 4999, sales: 15320, stock: 1500, categoryId: 1, image: '/images/xiaomi14u.png' },
  { id: 8, name: '索尼WH-1000XM5', subTitle: '无线降噪耳机 黑色', price: 2999, sales: 7890, stock: 600, categoryId: 1, image: '/images/iphone15pro.png' },
  { id: 9, name: '优衣库女装连衣裙', subTitle: '简约纯棉舒适连衣裙', price: 299, sales: 1280, stock: 2000, categoryId: 3, image: '/images/uniqlo_dress.png' },
  { id: 10, name: '海尔冰箱', subTitle: '450升变频无霜', price: 3999, sales: 4520, stock: 400, categoryId: 2, image: '/images/haier_bcd500.png' },
  { id: 11, name: '格力空调', subTitle: '1.5匹变频冷暖', price: 2599, sales: 8930, stock: 600, categoryId: 2, image: '/images/geli_kfr35.png' },
  { id: 12, name: 'Nike运动鞋', subTitle: 'Air Max 气垫跑鞋', price: 899, sales: 6750, stock: 1200, categoryId: 3, image: '/images/nike.png' },
  { id: 13, name: '雅诗兰黛面霜', subTitle: '小棕瓶精华面霜', price: 890, sales: 11200, stock: 800, categoryId: 4, image: '/images/uniqlo_dress.png' },
  { id: 14, name: '小米电视', subTitle: '65寸4K超高清', price: 3299, sales: 7820, stock: 450, categoryId: 2, image: '/images/xiaomi14u.png' },
  { id: 15, name: '资生堂防晒霜', subTitle: '蓝胖子防晒乳', price: 380, sales: 15600, stock: 1000, categoryId: 4, image: '/images/uniqlo_dress.png' },
  { id: 16, name: 'Python编程', subTitle: '从入门到精通', price: 89, sales: 23400, stock: 5000, categoryId: 5, image: '/images/lianxiang.png' },
  { id: 17, name: '漫步者耳机', subTitle: 'LolliPods Pro', price: 399, sales: 18900, stock: 2500, categoryId: 1, image: '/images/iphone15pro.png' },
  { id: 18, name: '联想电脑', subTitle: 'ThinkPad轻薄本', price: 6999, sales: 3250, stock: 200, categoryId: 1, image: '/images/lianxiang.png' }
]

export const productAPI = {
  getProductList: (pageNum = 1, pageSize = 10, categoryId, sort) => {
    // 创建模拟数据处理函数
    const processMockData = () => {
      let filtered = [...mockProducts]
      
      // 分类筛选
      if (categoryId !== undefined && categoryId !== null && categoryId !== 0) {
        const categoryIdNum = parseInt(categoryId)
        filtered = filtered.filter(p => p.categoryId === categoryIdNum)
      }
      
      // 排序
      if (sort && sort !== 'default') {
        filtered.sort((a, b) => {
          if (sort === 'price_asc') return a.price - b.price
          if (sort === 'price_desc') return b.price - a.price
          if (sort === 'sales') return b.sales - a.sales
          return 0
        })
      }
      
      const start = (pageNum - 1) * pageSize
      const end = start + pageSize
      
      return {
        code: 200,
        data: {
          records: filtered.slice(start, end),
          total: filtered.length,
          size: pageSize,
          current: pageNum,
          pages: Math.ceil(filtered.length / pageSize)
        }
      }
    }
    
    // 尝试连接后端服务获取商品数据，如果失败则使用模拟数据
    return instance.get('/product/list', {
      params: { pageNum, pageSize, categoryId, sort }
    }).then(res => {
      if (!res || !res.data) {
        console.warn('后端返回数据为空，使用模拟数据')
        return processMockData()
      }
      
      if (res.code === 200 && res.data) {
        let data = res.data
        if (data.records && data.records.length > 0) {
          data.records.forEach(product => normalizeProductFields(product))
          
          // 前端二次分类筛选（确保分类功能正常）
          if (categoryId && categoryId !== 0) {
            const categoryIdNum = parseInt(categoryId)
            data.records = data.records.filter(p => {
              const catId = parseInt(p.categoryId)
              return catId === categoryIdNum
            })
          }
          
          // 前端排序
          if (sort && sort !== 'default') {
            data.records.sort((a, b) => {
              if (sort === 'price_asc') return a.price - b.price
              if (sort === 'price_desc') return b.price - a.price
              if (sort === 'sales') return b.sales - a.sales
              return 0
            })
          }
          
          data.total = data.records.length
        }
        return res
      }
      
      console.warn('后端返回code不是200，使用模拟数据')
      return processMockData()
    }).catch(err => {
      console.warn('后端商品服务不可用，使用模拟数据:', err.message)
      return processMockData()
    })
  },
  getProductById: (id) => {
    const numId = parseInt(id)
    
    return instance.get(`/product/${numId}`).then(res => {
      if ((res.code === 200 || res.success === true) && res.data) {
        const product = res.data
        normalizeProductFields(product)
        return { code: 200, data: product }
      }
      throw new Error('Invalid response')
    }).catch(err => {
      console.warn('后端商品服务不可用，使用模拟数据:', err.message)
      const product = mockProducts.find(p => p.id === numId)
      if (product) {
        return {
          code: 200,
          data: {
            ...product,
            description: '这是一款高品质的产品，品质保证。',
            specs: []
          }
        }
      }
      return {
        code: 404,
        data: null,
        message: '商品不存在'
      }
    })
  },
  searchProducts: (keyword, pageNum = 1, pageSize = 10) => {
    return instance.get('/product/search', {
      params: { keyword, pageNum, pageSize }
    }).then(res => {
      if (res.code === 200 && res.data) {
        const data = res.data
        if (data.records && data.records.length > 0) {
          data.records.forEach(product => normalizeProductFields(product))
        }
        return res
      }
      throw new Error('Invalid response')
    }).catch(err => {
      console.warn('后端商品服务不可用，使用模拟数据')
      const filtered = mockProducts.filter(p => 
        p.name.toLowerCase().includes(keyword.toLowerCase()) ||
        p.subTitle.toLowerCase().includes(keyword.toLowerCase())
      )
      const start = (pageNum - 1) * pageSize
      const end = start + pageSize
      return {
        code: 200,
        data: {
          records: filtered.slice(start, end),
          total: filtered.length
        }
      }
    })
  }
}

export const userAPI = {
  login: (username, password) => {
    return instance.post('/user/login', { username, password }).then(res => {
      if (res.code === 200 || res.success === true) {
        let token = ''
        let userId = 1
        
        if (typeof res.data === 'string') {
          token = res.data
          const underscoreIndex = res.data.indexOf('_')
          if (underscoreIndex > 0) {
            userId = parseInt(res.data.substring(0, underscoreIndex)) || 1
          }
        } else {
          token = res.data?.token || res.data?.accessToken || res.data?.tokenString || ''
          userId = res.data?.userId || res.data?.id || res.data?.user_id || 1
        }
        
        return {
          code: 200,
          data: { token, userId }
        }
      }
      return res
    })
  },
  register: (user) => {
    return instance.post('/user/register', user)
  },
  getUserInfo: (userId) => {
    return instance.get(`/user/${userId || localStorage.getItem('userId')}`)
  },
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

const getCurrentUserId = () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return null
  const uid = parseInt(userId, 10)
  return Number.isNaN(uid) ? null : uid
}

export const cartAPI = {
  getCartList: () => {
    const uid = getCurrentUserId()
    if (!uid) {
      return Promise.resolve({ code: 200, data: [] })
    }

    return instance.get('/cart/list', {
      params: { userId: uid, pageNum: 1, pageSize: 100 }
    }).then(res => {
      if (res.code === 200 && res.data) {
        const records = (res.data.records || [])
          .filter(item => Number(item.userId) === uid)
          .map(item => normalizeProductFields(item))
        // 清除历史本地模拟购物车，避免与后端数据混淆
        localStorage.removeItem(`cart_${uid}`)
        return { code: 200, data: records }
      }
      return { code: 200, data: [] }
    })
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
  updateCart: (cartId, quantity) => {
    return instance.post('/cart/update', {
      id: Number(cartId),
      quantity
    })
  },
  deleteCart: (cartId) => {
    return instance.delete(`/cart/delete/${cartId}`)
  },
  clearCart: async () => {
    const listRes = await cartAPI.getCartList()
    const items = listRes.data || []
    if (items.length === 0) {
      return { code: 200 }
    }
    await Promise.all(items.map(item => instance.delete(`/cart/delete/${item.id}`)))
    return { code: 200 }
  }
}

export const paymentAPI = {
  createAlipayPayment: (orderId, amount) => {
    console.log('创建支付宝支付:', { orderId, amount })
    return instance.post('/payment/alipay/create', { orderId, amount }).then(res => {
      console.log('支付宝支付返回:', res)
      return res
    }).catch(err => {
      console.warn('后端支付服务不可用，模拟支付成功:', err.message)
      return { code: 200, data: { payUrl: 'mock_alipay_url' } }
    })
  },
  createStripePayment: (orderId, amount) => {
    console.log('创建Stripe支付:', { orderId, amount })
    return instance.post('/payment/stripe/create-intent', { orderId, amount }).then(res => {
      console.log('Stripe支付返回:', res)
      return res
    }).catch(err => {
      console.warn('后端支付服务不可用，模拟支付成功:', err.message)
      return { code: 200, data: { clientSecret: 'mock_stripe_secret' } }
    })
  },
  createPayPalPayment: (orderId, amount) => {
    console.log('创建PayPal支付:', { orderId, amount })
    return instance.post('/payment/paypal/create', { orderId, amount }).then(res => {
      console.log('PayPal支付返回:', res)
      return res
    }).catch(err => {
      console.warn('后端支付服务不可用，模拟支付成功:', err.message)
      return { code: 200, data: { approvalUrl: 'mock_paypal_url' } }
    })
  },
  queryPaymentStatus: (orderNo) => {
    return instance.get(`/payment/alipay/query/${orderNo}`).then(res => {
      return res
    }).catch(err => {
      console.warn('后端支付查询服务不可用，模拟查询结果:', err.message)
      return { code: 200, data: { tradeStatus: 'TRADE_SUCCESS' } }
    })
  }
}

const defaultMockOrders = [
  {
    id: 1,
    orderNo: 'ORD202502010001',
    status: 3,
    totalAmount: 7999,
    payAmount: 7999,
    freightAmount: 0,
    discountAmount: 0,
    receiverName: '张三',
    receiverPhone: '13800138001',
    receiverAddress: '北京市朝阳区xxx街道xxx号',
    createTime: '2025-02-01 10:30:00',
    items: [
      { id: 1, productId: 1, productName: '索尼WH-1000XM5', price: 2999, quantity: 2, specs: '颜色：黑色', image: 'https://picsum.photos/400/400?random=8' },
      { id: 2, productId: 3, productName: 'AirPods Pro 2', price: 1899, quantity: 1, specs: '标准版', image: 'https://picsum.photos/400/400?random=3' }
    ]
  },
  {
    id: 2,
    orderNo: 'ORD202502020002',
    status: 2,
    totalAmount: 6399,
    payAmount: 6399,
    freightAmount: 0,
    discountAmount: 0,
    receiverName: '李四',
    receiverPhone: '13800138002',
    receiverAddress: '上海市浦东新区xxx路xxx号',
    createTime: '2025-02-02 14:20:00',
    items: [
      { id: 3, productId: 1, productName: 'iPhone 15 Pro Max', price: 9999, quantity: 1, specs: '颜色：原色钛金属', image: 'https://picsum.photos/400/400?random=1' }
    ]
  },
  {
    id: 3,
    orderNo: 'ORD202502030003',
    status: 1,
    totalAmount: 8999,
    payAmount: 8999,
    freightAmount: 0,
    discountAmount: 0,
    receiverName: '王五',
    receiverPhone: '13800138003',
    receiverAddress: '广州市天河区xxx街xxx号',
    createTime: '2025-02-03 09:15:00',
    items: [
      { id: 4, productId: 2, productName: 'MacBook Pro 14寸', price: 16999, quantity: 1, specs: 'M3 Pro芯片', image: 'https://picsum.photos/400/400?random=2' }
    ]
  },
  {
    id: 4,
    orderNo: 'ORD202502040004',
    status: 0,
    totalAmount: 608,
    payAmount: 608,
    freightAmount: 0,
    discountAmount: 0,
    receiverName: '赵六',
    receiverPhone: '13800138004',
    receiverAddress: '深圳市南山区xxx路xxx号',
    createTime: '2025-02-04 16:45:00',
    items: [
      { id: 5, productId: 9, productName: '优衣库女装连衣裙', price: 299, quantity: 2, specs: '颜色：粉色', image: 'https://picsum.photos/400/400?random=9' }
    ]
  },
  {
    id: 5,
    orderNo: 'ORD202502050005',
    status: 3,
    totalAmount: 3299,
    payAmount: 3299,
    freightAmount: 0,
    discountAmount: 0,
    receiverName: '钱七',
    receiverPhone: '13800138005',
    receiverAddress: '成都市锦江区xxx路xxx号',
    createTime: '2025-02-05 11:30:00',
    items: [
      { id: 6, productId: 14, productName: '小米电视', price: 3299, quantity: 1, specs: '65寸4K', image: 'https://picsum.photos/400/400?random=14' }
    ]
  },
  {
    id: 6,
    orderNo: 'ORD202502060006',
    status: 2,
    totalAmount: 4319,
    payAmount: 4319,
    freightAmount: 0,
    discountAmount: 0,
    receiverName: '孙八',
    receiverPhone: '13800138006',
    receiverAddress: '杭州市西湖区xxx路xxx号',
    createTime: '2025-02-06 15:20:00',
    items: [
      { id: 7, productId: 10, productName: '海尔冰箱', price: 3999, quantity: 1, specs: '450升', image: 'https://picsum.photos/400/400?random=10' },
      { id: 8, productId: 13, productName: '雅诗兰黛面霜', price: 890, quantity: 1, specs: '50ml', image: 'https://picsum.photos/400/400?random=13' }
    ]
  },
  {
    id: 7,
    orderNo: 'ORD202502070007',
    status: 5,
    totalAmount: 1299,
    payAmount: 1299,
    freightAmount: 0,
    discountAmount: 0,
    receiverName: '周九',
    receiverPhone: '13800138007',
    receiverAddress: '南京市鼓楼区xxx路xxx号',
    createTime: '2025-02-07 09:45:00',
    items: [
      { id: 9, productId: 12, productName: 'Nike运动鞋', price: 899, quantity: 1, specs: '尺码：42', image: 'https://picsum.photos/400/400?random=12' }
    ]
  },
  {
    id: 8,
    orderNo: 'ORD202502080008',
    status: 0,
    totalAmount: 4999,
    payAmount: 4999,
    freightAmount: 0,
    discountAmount: 0,
    receiverName: '吴十',
    receiverPhone: '13800138008',
    receiverAddress: '武汉市武昌区xxx路xxx号',
    createTime: '2025-02-08 14:00:00',
    items: [
      { id: 10, productId: 7, productName: '小米14 Pro', price: 4999, quantity: 1, specs: '颜色：黑色', image: 'https://picsum.photos/400/400?random=7' }
    ]
  }
]

// 从 localStorage 读取订单，若不存在则使用默认订单
const getStoredOrders = () => {
  try {
    const stored = localStorage.getItem('mockOrders')
    if (stored) {
      return JSON.parse(stored)
    }
  } catch (e) {
    console.warn('读取 localStorage 失败:', e)
  }
  return [...defaultMockOrders]
}

// 保存订单到 localStorage
const saveOrdersToStorage = (orders) => {
  try {
    localStorage.setItem('mockOrders', JSON.stringify(orders))
  } catch (e) {
    console.warn('保存到 localStorage 失败:', e)
  }
}

let mockOrders = getStoredOrders()

const processOrderItems = (items) => {
  if (!items || !Array.isArray(items)) return []
  return items.map(item => {
    normalizeProductFields(item)
    return {
      ...item,
      image: getProductImage(item)
    }
  })
}

export const orderAPI = {
  createOrder: (order) => {
    const processedItems = processOrderItems(order.items)
    const newOrderId = Date.now()
    const uid = getCurrentUserId()
    const mockOrder = {
      id: newOrderId,
      userId: uid,
      orderNo: 'ORD' + newOrderId.toString().padStart(10, '0'),
      status: 0,
      totalAmount: order.totalAmount,
      payAmount: order.payAmount,
      freightAmount: order.freightAmount || 0,
      discountAmount: order.discountAmount || 0,
      receiverName: order.receiverName || '张三',
      receiverPhone: order.receiverPhone || '13800138000',
      receiverAddress: order.receiverAddress || '北京市朝阳区测试地址',
      items: processedItems,
      createTime: new Date().toISOString(),
      payTime: null,
      shipTime: null,
      finishTime: null
    }
    const storedOrders = getStoredOrders()
    storedOrders.unshift(mockOrder)
    saveOrdersToStorage(storedOrders)
    mockOrders = storedOrders
    const orderWithUserId = { ...order, userId: uid }
    return instance.post('/order/create', orderWithUserId).then(res => {
      if (res.code === 200 && res.data && res.data.id) {
        const backendOrderId = res.data.id
        const currentOrders = getStoredOrders()
        const index = currentOrders.findIndex(o => o.id === newOrderId)
        if (index !== -1) {
          currentOrders[index].id = backendOrderId
          currentOrders[index].orderNo = 'ORD' + backendOrderId.toString().padStart(10, '0')
          saveOrdersToStorage(currentOrders)
          mockOrders = currentOrders
        }
        return res
      }
      return { code: 200, data: { id: newOrderId } }
    }).catch(err => {
      return { code: 200, data: { id: newOrderId } }
    })
  },
  getOrderList: (status) => {
    mockOrders = getStoredOrders()
    let localOrders = mockOrders
    if (status !== '' && status !== undefined) {
      localOrders = mockOrders.filter(order => order.status === parseInt(status))
    }
    return instance.get('/order/list', { params: { status: status || undefined } }).then(res => {
      if (res.code === 200 && res.data) {
        const records = res.data.records || res.data
        let backendOrders = records
        if (status !== '' && status !== undefined) {
          backendOrders = records.filter(order => order.status === parseInt(status))
        }
        const localOrderIds = new Set(localOrders.map(o => String(o.id)))
        const mergedOrders = [...localOrders]
        backendOrders.forEach(backendOrder => {
          if (!localOrderIds.has(String(backendOrder.id))) {
            mergedOrders.push(backendOrder)
          }
        })
        return {
          code: 200,
          data: mergedOrders
        }
      }
      return {
        code: 200,
        data: localOrders
      }
    }).catch(err => {
      return {
        code: 200,
        data: localOrders
      }
    })
  },
  getOrderById: (id) => {
    const strId = String(id)
    mockOrders = getStoredOrders()
    const localOrder = mockOrders.find(o => String(o.id) === strId)
    if (localOrder) {
      return instance.get(`/order/${id}`).then(res => {
        if (res.code === 200 && res.data) {
          return {
            code: 200,
            data: res.data
          }
        }
        return {
          code: 200,
          data: localOrder
        }
      }).catch(err => {
        return {
          code: 200,
          data: localOrder
        }
      })
    }
    return instance.get(`/order/${id}`).then(res => {
      if (res.code === 200 && res.data) {
        return {
          code: 200,
          data: res.data
        }
      }
      return {
        code: 200,
        data: null
      }
    }).catch(err => {
      return {
        code: 200,
        data: null
      }
    })
  },
  updateOrder: (order) => {
    const strOrderId = String(order.id)
    mockOrders = getStoredOrders()
    const index = mockOrders.findIndex(o => String(o.id) === strOrderId)
    if (index !== -1) {
      mockOrders[index] = { ...mockOrders[index], ...order }
      saveOrdersToStorage(mockOrders)
    }
    return instance.put('/order/update', order).then(res => {
      return res
    }).catch(err => {
      return { code: 200 }
    })
  },
  cancelOrder: (orderId) => {
    const strOrderId = String(orderId)
    mockOrders = getStoredOrders()
    const index = mockOrders.findIndex(o => String(o.id) === strOrderId)
    if (index !== -1) {
      mockOrders[index].status = 4 // 已取消
      saveOrdersToStorage(mockOrders)
    }
    return instance.put(`/order/cancel/${orderId}`).then(res => {
      return res
    }).catch(err => {
      return { code: 200 }
    })
  },
  confirmOrder: (orderId) => {
    const strOrderId = String(orderId)
    mockOrders = getStoredOrders()
    const index = mockOrders.findIndex(o => String(o.id) === strOrderId)
    if (index !== -1) {
      mockOrders[index].status = 3 // 已完成
      saveOrdersToStorage(mockOrders)
    }
    return instance.put('/order/update', { id: orderId, status: 3 }).then(res => {
      return res
    }).catch(err => {
      return { code: 200 }
    })
  }
}

export default instance
