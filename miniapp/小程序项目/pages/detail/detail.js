const { request, requireLogin } = require('../../utils/request')
const { resolveImage, DEFAULT_IMAGE } = require('../../utils/config')

Page({
  data: {
    id: null,
    product: {}
  },

  onLoad(options) {
    this.setData({ id: options.id })
    this.loadDetail()
  },

  loadDetail() {
    wx.showLoading({ title: '加载中...' })
    request({
      url: `/api/product/${this.data.id}`
    }).then((res) => {
      const product = res.data || {}
      product.mainImage = resolveImage(product.mainImage || product.main_image)
      product.subTitle = product.subTitle || product.sub_title
      this.setData({ product })
    }).catch((err) => {
      wx.showToast({ title: err.message || '加载失败', icon: 'none' })
    }).finally(() => {
      wx.hideLoading()
    })
  },

  onImageError() {
    this.setData({
      'product.mainImage': DEFAULT_IMAGE
    })
  },

  addToCart() {
    const userId = requireLogin()
    if (!userId) return
    request({
      url: '/api/cart/add',
      method: 'POST',
      data: {
        userId,
        productId: this.data.product.id,
        skuId: 1,
        quantity: 1
      }
    }).then(() => {
      wx.showToast({ title: '已加入购物车', icon: 'success' })
    }).catch((err) => {
      wx.showToast({ title: err.message || '加入失败', icon: 'none' })
    })
  }
})
