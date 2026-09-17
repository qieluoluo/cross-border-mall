const { request, requireLogin } = require('../../utils/request')
const { resolveImage, DEFAULT_IMAGE } = require('../../utils/config')

Page({
  data: {
    id: null,
    product: {},
    quantity: 1,
    maxQuantity: 99
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
      const stock = Number(product.stock || 99)
      const maxQuantity = stock > 0 ? stock : 1
      this.setData({
        product,
        maxQuantity,
        quantity: Math.min(this.data.quantity || 1, maxQuantity)
      })
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

  incQuantity() {
    const { quantity, maxQuantity } = this.data
    if (quantity >= maxQuantity) {
      wx.showToast({ title: '已达库存上限', icon: 'none' })
      return
    }
    this.setData({ quantity: quantity + 1 })
  },

  decQuantity() {
    if (this.data.quantity <= 1) return
    this.setData({ quantity: this.data.quantity - 1 })
  },

  addToCart() {
    const userId = requireLogin()
    if (!userId) return
    const product = this.data.product
    if (!product.id) {
      wx.showToast({ title: '商品信息未加载完成', icon: 'none' })
      return
    }
    request({
      url: '/api/cart/add',
      method: 'POST',
      data: {
        userId,
        productId: product.id,
        skuId: product.id,
        quantity: this.data.quantity
      }
    }).then(() => {
      wx.showToast({ title: `已加入购物车 x${this.data.quantity}`, icon: 'success' })
    }).catch((err) => {
      wx.showToast({ title: err.message || '加入失败', icon: 'none' })
    })
  },

  buyNow() {
    const userId = requireLogin()
    if (!userId) return
    const product = this.data.product
    if (!product.id) {
      wx.showToast({ title: '商品信息未加载完成', icon: 'none' })
      return
    }

    wx.showLoading({ title: '提交中...' })
    request({
      url: `/api/user-address/list/${userId}`
    }).then((res) => {
      const addresses = res.data || []
      const address = addresses.find((item) => Number(item.isDefault) === 1) || addresses[0]
      if (!address) {
        wx.hideLoading()
        wx.showModal({
          title: '请先添加收货地址',
          content: '立即购买需要收货地址，请先到个人中心添加。',
          confirmText: '去添加',
          success: (modal) => {
            if (modal.confirm) {
              wx.navigateTo({ url: '/pages/info/info' })
            }
          }
        })
        return Promise.reject(new Error('NO_ADDRESS'))
      }
      const quantity = this.data.quantity
      const price = Number(product.price || 0)
      return request({
        url: '/api/order/create',
        method: 'POST',
        data: {
          userId,
          items: [{
            productId: product.id,
            productName: product.name,
            productImage: product.mainImage || product.main_image,
            price,
            quantity,
            skuId: product.id,
            specs: '默认规格'
          }],
          totalAmount: price * quantity,
          payAmount: price * quantity,
          addressId: address.id,
          receiverName: address.receiverName,
          receiverPhone: address.receiverPhone,
          receiverAddress: `${address.province || ''}${address.city || ''}${address.district || ''}${address.detailAddress || ''}`
        }
      })
    }).then((res) => {
      wx.hideLoading()
      const orderId = res.data && res.data.id ? res.data.id : res.data
      const amount = Number(this.data.product.price || 0) * this.data.quantity
      wx.navigateTo({
        url: `/pages/pay/pay?orderId=${orderId}&amount=${amount}`
      })
    }).catch((err) => {
      wx.hideLoading()
      if (err && err.message === 'NO_ADDRESS') return
      wx.showToast({ title: (err && err.message) || '下单失败', icon: 'none' })
    })
  }
})
