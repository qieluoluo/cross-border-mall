// pages/admin/admin.js
Page({
  data: {
    adminInfo: {}
  },

  onLoad() {
    const adminInfo = wx.getStorageSync('adminInfo')
    if (!adminInfo) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      setTimeout(() => {
        wx.switchTab({ url: '/pages/index/index' })
      }, 1500)
      return
    }
    this.setData({ adminInfo })
  },

  goToAdminList() {
    wx.navigateTo({ url: '/pages/admin/list/list' })
  },

  createAdmin() {
    wx.navigateTo({ url: '/pages/admin/create/create' })
  },

  logout() {
    wx.showModal({
      title: '提示',
      content: '确定退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('adminInfo')
          wx.switchTab({ url: '/pages/index/index' })
        }
      }
    })
  }
})