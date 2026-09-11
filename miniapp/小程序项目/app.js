// app.js
App({
  onLaunch() {
    // 展示本地存储能力
    const logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)

    // 检查登录状态
    const isLogin = wx.getStorageSync('isLogin')
    if (!isLogin) {
      // 未登录，跳转到登录页
      wx.reLaunch({
        url: '/pages/login/login'
      })
    }

    // 微信登录（获取code，用于后续后端登录）
    wx.login({
      success: res => {
        // 发送 res.code 到后台换取 openId, sessionKey, unionId
        console.log('微信登录code:', res.code)
      }
    })
  },
  globalData: {
    userInfo: null
  }
})