// pages/pay/pay.js
Page({
  data: {
    orderId: '',
    amount: 0,
    payForm: '',
    outTradeNo: '',
    isCreating: false,
    isPaying: false
  },

  onLoad(options) {
    console.log('支付页面参数:', options)
    this.setData({
      orderId: options.orderId,
      amount: options.amount
    })
    this.createPayment()
  },

  createPayment() {
    if (this.data.isCreating) {
      wx.showToast({ title: '正在创建支付...', icon: 'none' })
      return
    }
    
    this.setData({ isCreating: true })
    wx.showLoading({ title: '创建支付中...' })

    wx.request({
      url: 'http://localhost:8888/api/payment/alipay/create',
      method: 'POST',
      header: { 'Content-Type': 'application/json' },
      data: {
        orderId: this.data.orderId,
        amount: this.data.amount,
        subject: '订单支付-' + this.data.orderId,
        body: '订单号：' + this.data.orderId
      },
      success: (res) => {
        wx.hideLoading()
        if (res.data.code === 200) {
          const data = res.data.data
          this.setData({
            payForm: data.paymentForm,
            outTradeNo: data.outTradeNo
          })
          wx.showToast({ title: '支付已创建', icon: 'success' })
        } else {
          this.setData({ outTradeNo: 'DEMO' + Date.now() })
          wx.showToast({ title: '已进入演示支付', icon: 'none' })
        }
      },
      fail: (err) => {
        wx.hideLoading()
        console.error('创建支付失败:', err)
        this.setData({ outTradeNo: 'DEMO' + Date.now() })
        wx.showToast({ title: '支付服务未就绪，可直接确认演示支付', icon: 'none' })
      },
      complete: () => {
        this.setData({ isCreating: false })
      }
    })
  },

  doPay() {
    if (this.data.isCreating) {
      wx.showToast({ title: '正在创建支付...', icon: 'none' })
      return
    }
    if (this.data.isPaying) {
      wx.showToast({ title: '支付处理中...', icon: 'none' })
      return
    }

    if (!this.data.outTradeNo) {
      this.createPayment()
      return
    }

    wx.showModal({
      title: '支付宝支付',
      content: `支付金额：¥${this.data.amount}\n订单号：${this.data.outTradeNo}\n\n请确认支付`,
      confirmText: '确认支付',
      cancelText: '取消',
      success: (modalRes) => {
        if (modalRes.confirm) {
          this.confirmPay()
        }
      }
    })
  },

  confirmPay() {
    this.setData({ isPaying: true })
    wx.showLoading({ title: '确认支付中...' })
    
    wx.request({
      url: 'http://localhost:8888/api/order/update',
      method: 'PUT',
      header: { 'Content-Type': 'application/json' },
      data: {
        id: this.data.orderId,
        status: 1,
        payType: 2,
        payTime: new Date().toISOString()
      },
      success: (res) => {
        if (res.data.code === 200) {
          wx.hideLoading()
          wx.removeStorageSync('cartList')
          wx.showModal({
            title: '支付成功',
            content: '您的订单已支付成功，商家正在处理中！',
            showCancel: false,
            confirmText: '返回首页',
            success: () => {
              wx.switchTab({ url: '/pages/index/index' })
            }
          })
        } else {
          wx.hideLoading()
          wx.showToast({ title: res.data.message || '支付失败', icon: 'none' })
        }
      },
      fail: () => {
        wx.hideLoading()
        wx.showToast({ title: '网络错误', icon: 'none' })
      },
      complete: () => {
        this.setData({ isPaying: false })
      }
    })
  },

  retryCreatePayment() {
    this.createPayment()
  }
})
