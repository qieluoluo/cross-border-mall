// pages/info/info.js
const app = getApp()

Page({
  data: {
    activeTab: 'user', // user 或 address
    // 用户信息相关
    userInfo: {
      nickname: '',
      phone: '',
      email: ''
    },
    userId: null,
    // 地址相关
    addressList: [],
    isEditAddress: false,
    currentAddress: null,
    editMode: 'add' // add 或 edit
  },

  onLoad() {
    const userId = wx.getStorageSync('userId')
    if (!userId) {
      wx.navigateTo({ url: '/pages/login/login' })
      return
    }
    this.setData({ userId })
    this.loadUserInfo()
    this.loadAddressList()
  },

  onShow() {
    if (this.data.userId) {
      this.loadAddressList()
    }
  },

  // 切换标签
  switchTab(e) {
    this.setData({ activeTab: e.currentTarget.dataset.tab })
  },

  // 加载用户信息
  loadUserInfo() {
    wx.request({
      url: `http://localhost:8888/api/user/${this.data.userId}`,
      method: 'GET',
      success: (res) => {
        if (res.data.code === 200) {
          const user = res.data.data
          this.setData({
            userInfo: {
              nickname: user.nickname || user.username || '',
              phone: user.phone || '',
              email: user.email || ''
            }
          })
        }
      }
    })
  },

  onNicknameInput(e) {
    this.setData({ 'userInfo.nickname': e.detail.value })
  },

  onPhoneInput(e) {
    this.setData({ 'userInfo.phone': e.detail.value })
  },

  onEmailInput(e) {
    this.setData({ 'userInfo.email': e.detail.value })
  },

  saveInfo() {
    const { nickname, phone, email } = this.data.userInfo
    wx.showLoading({ title: '保存中...' })
    wx.request({
      url: `http://localhost:8888/api/user/${this.data.userId}`,
      method: 'PUT',
      header: { 'Content-Type': 'application/json' },
      data: { nickname, phone, email },
      success: (res) => {
        wx.hideLoading()
        if (res.data.code === 200) {
          wx.showToast({ title: '保存成功', icon: 'success' })
        } else {
          wx.showToast({ title: res.data.message || '保存失败', icon: 'none' })
        }
      },
      fail: () => {
        wx.hideLoading()
        wx.showToast({ title: '网络错误', icon: 'none' })
      }
    })
  },

  // 地址相关
  loadAddressList() {
    wx.request({
      url: `http://localhost:8888/api/user-address/list/${this.data.userId}`,
      method: 'GET',
      success: (res) => {
        if (res.data.code === 200) {
          this.setData({ addressList: res.data.data || [] })
        }
      }
    })
  },

  addAddress() {
    this.setData({
      isEditAddress: true,
      editMode: 'add',
      currentAddress: {
        userId: this.data.userId,
        receiverName: '',
        receiverPhone: '',
        province: '',
        city: '',
        district: '',
        detailAddress: '',
        isDefault: 0
      }
    })
  },

  editAddress(e) {
    const address = e.currentTarget.dataset.address
    console.log('编辑地址:', address)
    this.setData({
      isEditAddress: true,
      editMode: 'edit',
      currentAddress: {
        id: address.id,
        userId: address.userId,
        receiverName: address.receiverName,
        receiverPhone: address.receiverPhone,
        province: address.province,
        city: address.city,
        district: address.district,
        detailAddress: address.detailAddress,
        isDefault: address.isDefault || 0
      }
    })
    console.log('设置后的地址:', this.data.currentAddress)
  },

  cancelEditAddress() {
    this.setData({ isEditAddress: false, currentAddress: null })
  },

  onAddressInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({
      [`currentAddress.${field}`]: e.detail.value })
  },

  toggleDefault() {
    const currentValue = this.data.currentAddress.isDefault
    const newValue = currentValue === 1 ? 0 : 1
    this.setData({ 'currentAddress.isDefault': newValue })
  },

  saveAddress() {
    const { currentAddress, editMode } = this.data
    console.log('保存地址:', currentAddress)
    if (!currentAddress.receiverName || !currentAddress.receiverPhone || 
        !currentAddress.province || !currentAddress.city || 
        !currentAddress.district || !currentAddress.detailAddress) {
      wx.showToast({ title: '请填写完整信息', icon: 'none' })
      return
    }

    wx.showLoading({ title: '保存中...' })
    const url = editMode === 'add' 
      ? 'http://localhost:8888/api/user-address/add'
      : 'http://localhost:8888/api/user-address/update'
    
    wx.request({
      url,
      method: editMode === 'add' ? 'POST' : 'PUT',
      header: { 'Content-Type': 'application/json' },
      data: currentAddress,
      success: (res) => {
        wx.hideLoading()
        if (res.data.code === 200) {
          wx.showToast({ title: '保存成功', icon: 'success' })
          this.setData({ isEditAddress: false, currentAddress: null })
          this.loadAddressList()
        } else {
          wx.showToast({ title: res.data.message || '保存失败', icon: 'none' })
        }
      },
      fail: () => {
        wx.hideLoading()
        wx.showToast({ title: '网络错误', icon: 'none' })
      }
    })
  },

  deleteAddress(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '提示',
      content: '确定删除该地址吗？',
      success: (res) => {
        if (res.confirm) {
          wx.request({
            url: `http://localhost:8888/api/user-address/delete/${id}`,
            method: 'DELETE',
            success: (res) => {
              if (res.data.code === 200) {
                wx.showToast({ title: '删除成功', icon: 'success' })
                this.loadAddressList()
              }
            }
          })
        }
      }
    })
  },

  setDefaultAddress(e) {
    const id = e.currentTarget.dataset.id
    wx.request({
      url: `http://localhost:8888/api/user-address/default/${this.data.userId}/${id}`,
      method: 'PUT',
      success: (res) => {
        if (res.data.code === 200) {
          wx.showToast({ title: '设置成功', icon: 'success' })
          this.loadAddressList()
        }
      }
    })
  }
})
