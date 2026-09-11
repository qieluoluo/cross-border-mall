// pages/cart/cart.js
Page({
  data: {
    cartList: [],
    totalPrice: 0,
    userId: null,
    username: null,
    selectedAddress: null,
    addressList: [],           // ✅ 添加缺失的字段
    showCheckoutModal: false,  // 是否显示结算弹窗
    showAddressPicker: false   // 是否显示地址选择器
  },

  onShow() {
    console.log('=== 购物车页面加载 ===')
    const userId = wx.getStorageSync('userId')
    const username = wx.getStorageSync('username')
    
    if (!userId || !username) {
      wx.navigateTo({ url: '/pages/login/login' })
      return
    }
    
    this.setData({ 
      userId: userId,
      username: username 
    })
    
    this.loadCart()
    this.loadAddressList()
  },

  // 加载地址列表
  loadAddressList() {
    console.log('加载地址列表, userId:', this.data.userId)
    
    wx.request({
      url: `http://localhost:8888/api/user-address/list/${this.data.userId}`,
      method: 'GET',
      success: (res) => {
        console.log('地址列表响应:', res.data)
        
        if (res.data.code === 200 && res.data.data) {
          const addresses = res.data.data
          this.setData({ addressList: addresses })
          
          // 设置默认地址
          if (addresses.length > 0 && !this.data.selectedAddress) {
            const defaultAddress = addresses.find(addr => addr.isDefault === 1) || addresses[0]
            this.setData({ selectedAddress: defaultAddress })
            console.log('默认地址:', defaultAddress)
          }
        } else {
          console.log('获取地址列表失败或为空')
        }
      },
      fail: (err) => {
        console.error('请求地址列表失败:', err)
        wx.showToast({ title: '获取地址失败', icon: 'none' })
      }
    })
  },

  // 获取购物车列表
  loadCart() {
    console.log('加载购物车, username:', this.data.username)
    wx.showLoading({ title: '加载中...' })
    
    wx.request({
      url: `http://localhost:8888/api/cart/list?username=${this.data.username}`,
      method: 'GET',
      success: (res) => {
        console.log('购物车响应:', res.data)
        
        if (res.data.code === 200) {
          let cartList = res.data.data.records || []
          
          // 处理图片URL
          cartList = cartList.map(item => {
            const imagePath = item.productImage
            const imageUrl = imagePath ? 'http://localhost:8888' + imagePath : 'http://localhost:8888/images/iphone.png'
            return {
              ...item,
              productImage: imageUrl
            }
          })
          
          // 计算总价
          const totalPrice = cartList.reduce((sum, item) => sum + (item.price * item.quantity), 0)
          
          this.setData({ 
            cartList: cartList, 
            totalPrice: totalPrice 
          })
          
          console.log('购物车加载完成, 共', cartList.length, '件商品')
        } else {
          wx.showToast({ title: res.data.message || '加载失败', icon: 'none' })
        }
      },
      fail: (err) => {
        console.error('请求购物车失败:', err)
        wx.showToast({ title: '网络错误', icon: 'none' })
      },
      complete: () => {
        wx.hideLoading()
      }
    })
  },

  // 增加数量
  addQuantity(e) {
    const id = e.currentTarget.dataset.id
    const item = this.data.cartList.find(item => item.id === id)
    
    if (!item) return
    
    wx.request({
      url: 'http://localhost:8888/api/cart/update',
      method: 'POST',
      header: { 'Content-Type': 'application/json' },
      data: {
        id: id,
        userId: item.userId,
        productId: item.productId,
        skuId: item.skuId,
        quantity: item.quantity + 1
      },
      success: (res) => {
        if (res.data.code === 200) {
          this.loadCart()
        } else {
          wx.showToast({ title: '修改失败', icon: 'none' })
        }
      },
      fail: () => {
        wx.showToast({ title: '网络错误', icon: 'none' })
      }
    })
  },

  // 减少数量
  subQuantity(e) {
    const id = e.currentTarget.dataset.id
    const item = this.data.cartList.find(item => item.id === id)
    
    if (!item) return
    
    if (item.quantity > 1) {
      wx.request({
        url: 'http://localhost:8888/api/cart/update',
        method: 'POST',
        header: { 'Content-Type': 'application/json' },
        data: {
          id: id,
          userId: item.userId,
          productId: item.productId,
          skuId: item.skuId,
          quantity: item.quantity - 1
        },
        success: (res) => {
          if (res.data.code === 200) {
            this.loadCart()
          } else {
            wx.showToast({ title: '修改失败', icon: 'none' })
          }
        },
        fail: () => {
          wx.showToast({ title: '网络错误', icon: 'none' })
        }
      })
    } else {
      wx.showModal({
        title: '提示',
        content: '确定要删除该商品吗？',
        success: (res) => {
          if (res.confirm) {
            this.deleteCartItem(id)
          }
        }
      })
    }
  },

  // 删除购物车商品
  deleteCartItem(id) {
    wx.request({
      url: `http://localhost:8888/api/cart/delete/${id}`,
      method: 'DELETE',
      success: (res) => {
        if (res.data.code === 200) {
          this.loadCart()
          wx.showToast({ title: '删除成功', icon: 'success' })
        } else {
          wx.showToast({ title: '删除失败', icon: 'none' })
        }
      },
      fail: () => {
        wx.showToast({ title: '网络错误', icon: 'none' })
      }
    })
  },

  // 打开结算弹窗 ✅ 已修复方法名
  openCheckoutModal() {
    console.log('=== 点击了去结算按钮 ===')
    console.log('购物车商品数量:', this.data.cartList.length)
    console.log('选中的地址:', this.data.selectedAddress)
    
    // 检查购物车是否为空
    if (this.data.cartList.length === 0) {
      wx.showToast({ title: '购物车是空的', icon: 'none' })
      return
    }

    // 检查是否有收货地址
    if (!this.data.selectedAddress) {
      console.log('没有选中地址')
      wx.showModal({
        title: '提示',
        content: '请先添加收货地址',
        confirmText: '去添加',
        success: (res) => {
          if (res.confirm) {
            wx.navigateTo({ url: '/pages/info/info' })
          }
        }
      })
      return
    }

    // 检查地址列表是否为空
    if (this.data.addressList.length === 0) {
      wx.showModal({
        title: '提示',
        content: '暂无收货地址，请先添加',
        confirmText: '去添加',
        success: (res) => {
          if (res.confirm) {
            wx.navigateTo({ url: '/pages/info/info' })
          }
        }
      })
      return
    }

    console.log('打开结算弹窗')
    this.setData({ showCheckoutModal: true })
  },

  // 关闭结算弹窗
  closeCheckoutModal() {
    this.setData({ showCheckoutModal: false })
  },

  // 选择地址
  selectAddress() {
    this.setData({ 
      showAddressPicker: true, 
      showCheckoutModal: false 
    })
  },

  // 确认选择地址
  confirmSelectAddress(e) {
    const address = e.currentTarget.dataset.address
    this.setData({
      selectedAddress: address,
      showAddressPicker: false,
      showCheckoutModal: true
    })
    console.log('切换地址:', address)
  },

  // 关闭地址选择器
  closeAddressPicker() {
    this.setData({ 
      showAddressPicker: false, 
      showCheckoutModal: true 
    })
  },

  // 添加新地址
  addNewAddress() {
    wx.navigateTo({ 
      url: '/pages/info/info' 
    })
  },

  // 清空购物车
  clearCart() {
    const cartList = this.data.cartList
    if (cartList.length === 0) return

    wx.showModal({
      title: '提示',
      content: '确定要清空购物车吗？',
      success: (res) => {
        if (res.confirm) {
          // 逐个删除购物车商品
          let deleteCount = 0
          const totalCount = cartList.length

          const deleteItem = (index) => {
            if (index >= totalCount) {
              this.setData({ cartList: [], totalPrice: 0 })
              wx.showToast({ title: '清空成功', icon: 'success' })
              return
            }

            wx.request({
              url: `http://localhost:8888/api/cart/delete/${cartList[index].id}`,
              method: 'DELETE',
              success: () => {
                deleteCount++
                deleteItem(deleteCount)
              },
              fail: () => {
                deleteCount++
                deleteItem(deleteCount)
              }
            })
          }

          deleteItem(0)
        }
      }
    })
  },

  // 确认下单
  confirmOrder() {
    const { selectedAddress, cartList, totalPrice, userId } = this.data
    
    console.log('确认下单:', { selectedAddress, cartList, totalPrice, userId })
    
    wx.showLoading({ title: '提交中...' })

    const orderData = {
      userId: userId,
      items: cartList.map(item => ({
        id: item.id,
        productId: item.productId,
        productName: item.productName,
        price: item.price,
        quantity: item.quantity,
        skuId: item.skuId
      })),
      totalAmount: totalPrice,
      addressId: selectedAddress.id,
      receiverName: selectedAddress.receiverName,
      receiverPhone: selectedAddress.receiverPhone,
      receiverAddress: `${selectedAddress.province}${selectedAddress.city}${selectedAddress.district}${selectedAddress.detailAddress}`
    }

    wx.request({
      url: 'http://localhost:8888/api/order/create',
      method: 'POST',
      header: { 'Content-Type': 'application/json' },
      data: orderData,
      success: (res) => {
        wx.hideLoading()
        console.log('下单响应:', res.data)
        
        if (res.data.code === 200) {
          this.setData({ showCheckoutModal: false })
          
          wx.showToast({ 
            title: '下单成功', 
            icon: 'success',
            duration: 1500
          })
          
          setTimeout(() => {
            wx.navigateTo({
              url: `/pages/pay/pay?orderId=${res.data.data}&amount=${totalPrice}`
            })
          }, 1500)
        } else {
          wx.showToast({ 
            title: res.data.message || '下单失败', 
            icon: 'none' 
          })
        }
      },
      fail: (err) => {
        wx.hideLoading()
        console.error('下单失败:', err)
        wx.showToast({ title: '网络错误', icon: 'none' })
      }
    })
  },

  // 阻止弹窗内容点击冒泡
  stopPropagation() {
    // 空方法，用于阻止冒泡
  }
})