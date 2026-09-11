// pages/index/index.js
const app = getApp()

Page({
  data: {
    username: '',
    password: '',
    result: '',
    loginType: 'user',
    productList: [],
    displayProductList: [],
    searchKeyword: '',
    pageNum: 1,
    pageSize: 10,
    loading: false,
    noMore: false,
    searchTimer: null,
    isSearching: false
  },

  // 处理商品数据，添加图片
  processProductList(list) {
    return list.map(item => {
      const imagePath = item.mainImage || item.main_image
      const imageUrl = imagePath ? 'http://localhost:8888' + imagePath : 'http://localhost:8888/images/iphone.png'
      console.log('商品:', item.name, '使用图片:', imageUrl)
      return {
        ...item,
        mainImage: imageUrl
      }
    })
  },

  onLoad() {
    if (this.data.loginType === 'user') {
      this.loadProducts()
    }
  },

  onShow() {
    if (this.data.loginType === 'user' && !this.data.isSearching && this.data.productList.length === 0) {
      this.loadProducts()
    }
  },

  switchType(e) {
    const type = e.currentTarget.dataset.type
    this.setData({ 
      loginType: type,
      productList: [],
      displayProductList: [],
      searchKeyword: '',
      pageNum: 1,
      noMore: false,
      isSearching: false
    })
    if (type === 'user') {
      this.loadProducts()
    }
  },

  onUsernameInput(e) {
    this.setData({ username: e.detail.value })
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value })
  },

  doLogin() {
    const { username, password, loginType } = this.data
    if (!username || !password) {
      wx.showToast({ title: '请输入用户名和密码', icon: 'none' })
      return
    }
    if (loginType === 'admin') {
      this.adminLogin()
    } else {
      this.userLogin()
    }
  },

  adminLogin() {
    wx.request({
      url: 'http://localhost:9999/admin/login-admin',
      method: 'POST',
      header: { 'Content-Type': 'application/json' },
      data: { username: this.data.username, password: this.data.password },
      success: (res) => {
        if (res.data.code === 200) {
          wx.setStorageSync('adminInfo', res.data.data)
          wx.showToast({ title: '登录成功', icon: 'success' })
          setTimeout(() => {
            wx.navigateTo({ url: '/pages/admin/admin' })
          }, 1000)
        } else {
          this.setData({ result: res.data.message })
          wx.showToast({ title: res.data.message, icon: 'none' })
        }
      },
      fail: () => {
        this.setData({ result: '网络错误' })
        wx.showToast({ title: '网络错误', icon: 'none' })
      }
    })
  },

  userLogin() {
    this.setData({ result: '已切换到普通用户模式' })
    wx.showToast({ title: '游客模式', icon: 'success' })
  },

  // 加载商品列表
  loadProducts() {
    if (this.data.loading || this.data.noMore) return
    this.setData({ loading: true })
    
    wx.request({
      url: 'http://localhost:8888/api/product/list',
      method: 'GET',
      data: {
        pageNum: this.data.pageNum,
        pageSize: this.data.pageSize
      },
      success: (res) => {
        if (res.data.code === 200 && res.data.data) {
          const newList = this.processProductList(res.data.data.records || [])
          const updatedList = [...this.data.productList, ...newList]
          this.setData({
            productList: updatedList,
            displayProductList: updatedList,
            pageNum: this.data.pageNum + 1,
            noMore: newList.length < this.data.pageSize
          })
        } else {
          wx.showToast({ title: '加载失败', icon: 'none' })
        }
      },
      fail: () => {
        wx.showToast({ title: '网络错误', icon: 'none' })
      },
      complete: () => {
        this.setData({ loading: false })
      }
    })
  },

  // 输入时防抖
  onSearchInput(e) {
    const keyword = e.detail.value
    this.setData({ searchKeyword: keyword })
    if (this.data.searchTimer) {
      clearTimeout(this.data.searchTimer)
    }
    const timer = setTimeout(() => {
      this.doSearch(keyword)
    }, 500)
    this.setData({ searchTimer: timer })
  },

  // 点击键盘搜索按钮
  onSearchConfirm() {
    if (this.data.searchTimer) {
      clearTimeout(this.data.searchTimer)
    }
    this.doSearch(this.data.searchKeyword)
  },

  // 后端搜索
  doSearch(keyword) {
    if (!keyword || keyword.trim() === '') {
      this.setData({
        isSearching: false,
        displayProductList: this.data.productList,
        searchKeyword: ''
      })
      return
    }

    this.setData({
      isSearching: true,
      loading: true
    })

    wx.request({
      url: 'http://localhost:8888/api/product/search',
      method: 'GET',
      data: {
        keyword: keyword.trim(),
        pageNum: 1,
        pageSize: 100
      },
      success: (res) => {
        if (res.data.code === 200) {
          const searchResult = this.processProductList(res.data.data.records || [])
          this.setData({
            displayProductList: searchResult,
            noMore: true
          })
          if (searchResult.length === 0) {
            wx.showToast({ title: '未找到相关商品', icon: 'none' })
          }
        } else {
          wx.showToast({ title: '搜索失败', icon: 'none' })
        }
      },
      fail: () => {
        wx.showToast({ title: '网络错误', icon: 'none' })
      },
      complete: () => {
        this.setData({ loading: false })
      }
    })
  },

  onReachBottom() {
    if (!this.data.isSearching && this.data.loginType === 'user') {
      this.loadProducts()
    }
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/detail/detail?id=${id}`
    })
  }
})
