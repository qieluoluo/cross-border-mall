// pages/logistics/logistics.js
Page({
  data: {
    orderId: null,
    logisticsInfo: null,
    loading: false
  },

  onLoad(options) {
    this.setData({ orderId: options.orderId })
    this.loadLogistics()
  },

  loadLogistics() {
    this.setData({ loading: true })
    
    // 先尝试调用后端接口
    wx.request({
      url: `http://localhost:8888/api/express/order/${this.data.orderId}`,
      method: 'GET',
      success: (res) => {
        console.log('物流接口返回:', res.data)
        if (res.data.code === 200 && res.data.data && res.data.data.number) {
          this.setData({ logisticsInfo: res.data.data })
        } else {
          // 后端没有物流信息时，显示模拟数据
          this.setMockData()
        }
      },
      fail: (err) => {
        console.log('接口调用失败，使用模拟数据', err)
        this.setMockData()
      },
      complete: () => {
        this.setData({ loading: false })
      }
    })
  },

  setMockData() {
    // 根据订单号生成不同的快递单号
    const mockNumber = 'SF' + (1000000000 + Math.floor(Math.random() * 900000000))
    
    this.setData({
      logisticsInfo: {
        status: '运输中',
        company: '顺丰速运',
        number: mockNumber,
        traces: [
          { time: this.getCurrentTime(), desc: '您的订单已发货，正在运输中' },
          { time: this.getYesterdayTime(), desc: '商家已打包，等待快递揽收' },
          { time: this.getTwoDaysAgoTime(), desc: '订单已确认，准备出库' }
        ]
      }
    })
  },

  getCurrentTime() {
    const now = new Date()
    return `${now.getFullYear()}-${now.getMonth()+1}-${now.getDate()} ${now.getHours()}:${now.getMinutes()}:${now.getSeconds()}`
  },

  getYesterdayTime() {
    const yesterday = new Date()
    yesterday.setDate(yesterday.getDate() - 1)
    return `${yesterday.getFullYear()}-${yesterday.getMonth()+1}-${yesterday.getDate()} 14:30:00`
  },

  getTwoDaysAgoTime() {
    const twoDaysAgo = new Date()
    twoDaysAgo.setDate(twoDaysAgo.getDate() - 2)
    return `${twoDaysAgo.getFullYear()}-${twoDaysAgo.getMonth()+1}-${twoDaysAgo.getDate()} 10:00:00`
  }
})