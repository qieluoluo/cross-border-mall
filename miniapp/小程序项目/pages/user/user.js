const { resolveImage, DEFAULT_IMAGE } = require('../../utils/config')

Page({
  data: {
    userInfo: {
      nickName: '加载中...',
      avatarUrl: '/images/yonghu.jpg'
    },
    userId: null
  },

  onShow() {
    const userId = wx.getStorageSync('userId')
    console.log('当前userId:', userId)
    if (!userId) {
      wx.navigateTo({ url: '/pages/login/login' })
      return
    }
    this.setData({ userId })
    this.loadUserInfo()
  },

  loadUserInfo() {
    wx.request({
      url: `http://localhost:8888/api/user/${this.data.userId}`,
      method: 'GET',
      success: (res) => {
        console.log('用户信息返回:', res.data)
        if (res.data.code === 200) {
          const user = res.data.data
          let avatarUrl = resolveImage(user.avatar || '/images/yonghu.jpg')
          const userInfo = {
            nickName: user.nickname || user.username || '用户',
            avatarUrl: avatarUrl
          }
          this.setData({ userInfo })
          wx.setStorageSync('userInfo', userInfo)
        } else {
          console.log('获取用户信息失败:', res.data.message)
          this.useLocalCache()
        }
      },
      fail: (err) => {
        console.error('请求失败:', err)
        this.useLocalCache()
      }
    })
  },

  onImageError() {
    this.setData({
      'userInfo.avatarUrl': DEFAULT_IMAGE
    })
  },

  useLocalCache() {
    const cached = wx.getStorageSync('userInfo')
    if (cached && cached.nickName) {
      this.setData({ userInfo: cached })
    }
  },

  chooseAvatar() {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFilePath = res.tempFilePaths[0]
        const userInfo = { ...this.data.userInfo, avatarUrl: tempFilePath }
        this.setData({ userInfo })
        wx.setStorageSync('userInfo', userInfo)
        
        wx.showLoading({ title: '上传中...' })
        
        wx.uploadFile({
          url: 'http://localhost:8888/api/user/avatar/upload',
          filePath: tempFilePath,
          name: 'file',
          formData: {
            userId: this.data.userId
          },
          success: (uploadRes) => {
            wx.hideLoading()
            try {
              const data = JSON.parse(uploadRes.data)
              if (data.code === 200) {
                const fullAvatarUrl = 'http://localhost:8888' + data.data
                const updatedUserInfo = { ...this.data.userInfo, avatarUrl: fullAvatarUrl }
                this.setData({ userInfo: updatedUserInfo })
                wx.setStorageSync('userInfo', updatedUserInfo)
                wx.showToast({ title: '头像上传成功', icon: 'success' })
              } else {
                wx.showToast({ title: data.message || '上传失败', icon: 'none' })
              }
            } catch (e) {
              wx.showToast({ title: '解析失败', icon: 'none' })
            }
          },
          fail: () => {
            wx.hideLoading()
            wx.showToast({ title: '上传失败', icon: 'none' })
          }
        })
      }
    })
  },

  editNickname() {
    wx.showModal({
      title: '修改昵称',
      editable: true,
      placeholderText: '请输入昵称',
      success: (res) => {
        if (res.confirm && res.content) {
          const newNickname = res.content
          const userInfo = { ...this.data.userInfo, nickName: newNickname }
          this.setData({ userInfo })
          wx.setStorageSync('userInfo', userInfo)
          wx.request({
            url: `http://localhost:8888/api/user/${this.data.userId}`,
            method: 'PUT',
            header: { 'Content-Type': 'application/json' },
            data: { nickname: newNickname },
            success: (updateRes) => {
              if (updateRes.data.code !== 200) {
                wx.showToast({ title: '同步后端失败', icon: 'none' })
              }
            }
          })
          wx.showToast({ title: '修改成功', icon: 'success' })
        }
      }
    })
  },

  goToOrders() {
    wx.switchTab({ url: '/pages/order/order' })
  },

  goToInfo() {
    wx.navigateTo({ url: '/pages/info/info' })
  },

  contactService() {
    wx.showToast({ title: '客服功能开发中', icon: 'none' })
  },

  logout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          wx.clearStorageSync()
          wx.reLaunch({ url: '/pages/login/login' })
        }
      }
    })
  }
})