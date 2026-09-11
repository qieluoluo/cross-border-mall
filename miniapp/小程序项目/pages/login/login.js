// pages/login/login.js
Page({
  data: {
    username: '',
    password: '',
    phone: '',
    isRegisterMode: false
  },

  onUsernameInput(e) {
    this.setData({ username: e.detail.value })
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value })
  },

  onPhoneInput(e) {
    this.setData({ phone: e.detail.value })
  },

  goRegister() {
    this.setData({ isRegisterMode: true, username: '', password: '', phone: '' })
  },

  goLogin() {
    this.setData({ isRegisterMode: false, username: '', password: '', phone: '' })
  },

  doRegister() {
    const { username, password, phone } = this.data
    if (!username || !password) {
      wx.showToast({ title: '请填写用户名和密码', icon: 'none' })
      return
    }
    if (!phone) {
      wx.showToast({ title: '请填写手机号', icon: 'none' })
      return
    }
    wx.request({
      url: 'http://localhost:8888/api/user/register',
      method: 'POST',
      header: { 'Content-Type': 'application/json' },
      data: {
        username: username,
        password: password,
        phone: phone
      },
      success: (res) => {
        console.log('注册结果:', res.data)
        if (res.data.code === 200) {
          wx.showToast({ title: '注册成功，请登录', icon: 'success' })
          this.setData({ isRegisterMode: false, username: '', password: '', phone: '' })
        } else {
          wx.showToast({ title: res.data.message || '注册失败', icon: 'none' })
        }
      },
      fail: () => {
        wx.showToast({ title: '网络错误', icon: 'none' })
      }
    })
  },

  doLogin() {
    const { username, password } = this.data
    if (!username || !password) {
      wx.showToast({ title: '请输入用户名和密码', icon: 'none' })
      return
    }
    wx.request({
      url: 'http://localhost:8888/api/user/login',
      method: 'POST',
      header: { 'Content-Type': 'application/json' },
      data: { username, password },
      success: (res) => {
        console.log('登录结果:', res.data)
        if (res.data.code === 200) {
          const token = res.data.data
          let userId = null
          if (token && typeof token === 'string') {
            const parts = token.split('_')
            if (parts.length >= 1) {
              userId = parseInt(parts[0])
            }
          }
          if (!userId) {
            wx.showToast({ title: '登录失败', icon: 'none' })
            return
          }
          wx.setStorageSync('isLogin', true)
          wx.setStorageSync('username', username)
          wx.setStorageSync('userId', userId)
          console.log('保存的userId:', userId)
          wx.showToast({ title: '登录成功', icon: 'success' })
          setTimeout(() => {
            wx.switchTab({ url: '/pages/index/index' })
          }, 1000)
        } else {
          wx.showToast({ title: res.data.message || '登录失败', icon: 'none' })
        }
      },
      fail: () => {
        wx.showToast({ title: '网络错误', icon: 'none' })
      }
    })
  }
})