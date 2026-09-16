const { request } = require('../../utils/request')
const { resolveImage, DEFAULT_IMAGE } = require('../../utils/config')

const STATUS_MAP = {
  0: '待付款',
  1: '待发货',
  2: '待收货',
  3: '已完成',
  4: '已取消',
  5: '退款中',
  6: '已退款'
}

Page({
  data: {
    orderList: [],
    userId: null
  },

  onShow() {
    const userId = wx.getStorageSync('userId')
    if (!userId) {
      wx.navigateTo({ url: '/pages/login/login' })
      return
    }
    this.setData({ userId })
    this.loadOrders()
  },

  loadOrders() {
    wx.showLoading({ title: '加载中...' })
    request({
      url: '/api/order/list',
      data: { pageNum: 1, pageSize: 50 }
    }).then((res) => {
      let orderList = res.data.records || []
      orderList = orderList
        .filter((item) => !item.userId || Number(item.userId) === Number(this.data.userId))
        .map((item) => ({
          ...item,
          statusText: STATUS_MAP[item.status] || '处理中',
          items: (item.items || []).map((goods) => ({
            ...goods,
            productImage: resolveImage(goods.productImage || goods.product_image || goods.image)
          }))
        }))
      this.setData({ orderList })
    }).catch((err) => {
      wx.showToast({ title: err.message || '加载失败', icon: 'none' })
    }).finally(() => {
      wx.hideLoading()
    })
  },

  onImageError(e) {
    const orderIndex = e.currentTarget.dataset.orderIndex
    const goodsIndex = e.currentTarget.dataset.goodsIndex
    if (orderIndex === undefined || goodsIndex === undefined) return
    this.setData({
      [`orderList[${orderIndex}].items[${goodsIndex}].productImage`]: DEFAULT_IMAGE
    })
  },

  goPay(e) {
    const { orderId, totalPrice } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/pay/pay?orderId=${orderId}&amount=${totalPrice}`
    })
  },

  goHome() {
    wx.switchTab({ url: '/pages/index/index' })
  }
})
