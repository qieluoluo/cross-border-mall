// pages/admin/list/list.js
Page({
  data: {
    adminList: [],
    displayList: [],
    searchKeyword: '',
    loading: false
  },

  onShow() {
    this.loadAdminList()
  },

  loadAdminList() {
    this.setData({ loading: true })
    wx.request({
      url: 'http://localhost:9999/admin/list',
      method: 'GET',
      success: (res) => {
        console.log('管理员列表原始数据:', res.data)
        
        // 关键：判断返回的数据结构，提取数组
        let adminArray = []
        if (res.data.code === 200) {
          // 如果 data 本身就是数组
          if (Array.isArray(res.data.data)) {
            adminArray = res.data.data
          }
          // 如果 data 是对象且包含 records 或 list 字段
          else if (res.data.data && Array.isArray(res.data.data.records)) {
            adminArray = res.data.data.records
          }
          else if (res.data.data && Array.isArray(res.data.data.list)) {
            adminArray = res.data.data.list
          }
          // 兜底：如果都不是，尝试把 data 转成数组
          else if (res.data.data && typeof res.data.data === 'object') {
            console.warn('data不是数组，是对象:', res.data.data)
            adminArray = []
          }
        }
        
        console.log('解析后的管理员数组:', adminArray)
        
        this.setData({ 
          adminList: adminArray,
          displayList: adminArray
        })
      },
      fail: (err) => {
        console.error('请求失败:', err)
        wx.showToast({ title: '网络错误', icon: 'none' })
      },
      complete: () => {
        this.setData({ loading: false })
      }
    })
  },

  onSearchInput(e) {
    this.setData({ searchKeyword: e.detail.value })
    this.searchAdmin()
  },

  searchAdmin() {
    // 确保 adminList 是数组
    const adminList = this.data.adminList
    if (!Array.isArray(adminList)) {
      console.error('adminList 不是数组:', adminList)
      this.setData({ displayList: [] })
      return
    }
    
    const keyword = this.data.searchKeyword.trim()
    if (!keyword) {
      this.setData({ displayList: adminList })
      return
    }
    
    const filtered = adminList.filter(item => 
      (item.username && item.username.toLowerCase().includes(keyword.toLowerCase())) || 
      (item.realName && item.realName.toLowerCase().includes(keyword.toLowerCase()))
    )
    this.setData({ displayList: filtered })
    console.log('搜索结果数量:', filtered.length)
  },

  toggleStatus(e) {
    const { id, status } = e.currentTarget.dataset
    const newStatus = status === 1 ? 0 : 1
    const actionText = newStatus === 1 ? '启用' : '禁用'
    
    wx.request({
      url: `http://localhost:9999/admin/admin/${id}/status`,
      method: 'PUT',
      data: { status: newStatus },
      success: (res) => {
        if (res.data.code === 200) {
          wx.showToast({ title: `${actionText}成功`, icon: 'success' })
          this.loadAdminList()
        } else {
          wx.showToast({ title: res.data.message || '操作失败', icon: 'none' })
        }
      },
      fail: () => {
        wx.showToast({ title: '网络错误', icon: 'none' })
      }
    })
  }
})