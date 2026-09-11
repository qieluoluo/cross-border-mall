// pages/order/order.js
Page({
  data: {
    orderList: [],
    userId: null,
    username: null
  },

  onShow() {
    const userId = wx.getStorageSync('userId')
    const username = wx.getStorageSync('username')
    if (!userId || !username) {
      wx.navigateTo({ url: '/pages/login/login' })
      return
    }
    this.setData({ userId, username })
    this.loadOrders()
  },

  loadOrders() {
    wx.showLoading({ title: '加载中...' })
    wx.request({
      url: `http://localhost:8888/api/order/list`,
      method: 'GET',
      success: (res) => {
        console.log('订单列表:', res.data)
        if (res.data.code === 200) {
          let orderList = res.data.data.records || []
          // 按 userId 过滤
          orderList = orderList.filter(item => item.userId == this.data.userId)
          // 按订单号去重
          const uniqueMap = new Map()
          orderList.forEach(item => {
            if (!uniqueMap.has(item.orderNo)) {
              uniqueMap.set(item.orderNo, item)
            }
          })
          orderList = Array.from(uniqueMap.values())
          // 按创建时间倒序排序
          orderList.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
          this.setData({ orderList })
          console.log('去重后订单数量:', orderList.length)
        } else {
          wx.showToast({ title: '加载失败', icon: 'none' })
        }
      },
      fail: () => {
        wx.showToast({ title: '网络错误', icon: 'none' })
      },
      complete: () => {
        wx.hideLoading()
      }
    })
  },

  goPay(e) {
    const { orderId, totalPrice } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/pay/pay?orderId=${orderId}&amount=${totalPrice}`
    })
  }
})