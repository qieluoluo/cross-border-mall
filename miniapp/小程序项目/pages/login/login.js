const { request } = require('../../utils/request')

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
    if (!username || !password || !phone) {
      wx.showToast({ title: '请填写用户名、密码和手机号', icon: 'none' })
      return
    }
    request({
      url: '/api/user/register',
      method: 'POST',
      data: { username, password, phone }
    }).then(() => {
      wx.showToast({ title: '注册成功，请登录', icon: 'success' })
      this.setData({ isRegisterMode: false, username: '', password: '', phone: '' })
    }).catch((err) => {
      wx.showToast({ title: err.message || '注册失败', icon: 'none' })
    })
  },

  doLogin() {
    const { username, password } = this.data
    if (!username || !password) {
      wx.showToast({ title: '请输入用户名和密码', icon: 'none' })
      return
    }
    request({
      url: '/api/user/login',
      method: 'POST',
      data: { username, password }
    }).then((res) => {
      const token = res.data
      let userId = null
      if (typeof token === 'string' && token.includes('_')) {
        userId = parseInt(token.split('_')[0], 10)
      } else if (token && typeof token === 'object') {
        userId = token.userId || token.id
      }
      if (!userId) {
        wx.showToast({ title: '登录失败', icon: 'none' })
        return
      }
      wx.setStorageSync('isLogin', true)
      wx.setStorageSync('username', username)
      wx.setStorageSync('userId', userId)
      wx.setStorageSync('token', typeof token === 'string' ? token : token.token || '')
      wx.showToast({ title: '登录成功', icon: 'success' })
      setTimeout(() => {
        wx.switchTab({ url: '/pages/index/index' })
      }, 400)
    }).catch((err) => {
      wx.showToast({ title: err.message || '登录失败', icon: 'none' })
    })
  }
})
