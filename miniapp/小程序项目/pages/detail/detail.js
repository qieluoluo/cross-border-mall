// pages/detail/detail.js
Page({
  data: {
    id: null,
    product: {}
  },

  onLoad(options) {
    console.log('接收到的参数:', options)
    this.setData({ id: options.id })
    this.loadDetail()
  },

  loadDetail() {
    wx.showLoading({ title: '加载中...' })
    wx.request({
      url: `http://localhost:8888/api/product/${this.data.id}`,
      method: 'GET',
      success: (res) => {
        console.log('商品详情:', res.data)
        if (res.data.code === 200) {
          let product = res.data.data
          const imagePath = product.mainImage || product.main_image
          product.mainImage = imagePath ? 'http://localhost:8888' + imagePath : 'http://localhost:8888/images/iphone.png'
          console.log('商品:', product.name, '使用图片:', product.mainImage)
          this.setData({ 
            product: product
          })
        } else {
          wx.showToast({ title: '加载失败', icon: 'none' })
        }
      },
      fail: (err) => {
        console.error('请求失败:', err)
        wx.showToast({ title: '网络错误', icon: 'none' })
      },
      complete: () => {
        wx.hideLoading()
      }
    })
  },

  addToCart() {
    const userId = wx.getStorageSync('userId')
    if (!userId) {
      wx.showModal({
        title: '提示',
        content: '请先登录',
        success: (res) => {
          if (res.confirm) {
            wx.navigateTo({ url: '/pages/login/login' })
          }
        }
      })
      return
    }

    console.log('=== 添加购物车 ===')
    console.log('用户ID:', userId)
    console.log('商品ID:', this.data.product.id)
    console.log('商品名称:', this.data.product.name)

    wx.request({
      url: 'http://localhost:8888/api/cart/add',
      method: 'POST',
      header: { 'Content-Type': 'application/json' },
      data: {
        userId: userId,
        productId: this.data.product.id,
        skuId: 1,
        quantity: 1
      },
      success: (res) => {
        console.log('添加结果:', res.data)
        if (res.data.code === 200) {
          wx.showToast({ title: '加入成功', icon: 'success' })
        } else {
          wx.showToast({ title: res.data.message || '加入失败', icon: 'none' })
        }
      },
      fail: (err) => {
        console.error('请求失败:', err)
        wx.showToast({ title: '网络错误', icon: 'none' })
      }
    })
  }
})
