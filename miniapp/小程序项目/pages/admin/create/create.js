// pages/admin/create/create.js
Page({
  data: {
    username: '',
    password: '',
    realName: '',
    roleList: [],
    selectedRoleId: null,
    selectedRoleName: ''
  },

  onLoad() {
    this.loadRoles()
  },

  loadRoles() {
    wx.request({
      url: 'http://localhost:9999/admin/role/list',
      method: 'GET',
      success: (res) => {
        console.log('角色列表:', res.data)
        if (res.data.code === 200 && res.data.data) {
          this.setData({ roleList: res.data.data })
        }
      },
      fail: () => {
        console.log('角色列表加载失败，继续使用无角色模式')
      }
    })
  },

  onUsernameInput(e) {
    this.setData({ username: e.detail.value })
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value })
  },

  onRealNameInput(e) {
    this.setData({ realName: e.detail.value })
  },

  // 点击角色字段时显示 picker
  showRolePicker() {
    if (!this.data.roleList.length) {
      wx.showToast({ title: '暂无角色可选', icon: 'none' })
      return
    }
    const items = this.data.roleList.map(item => item.name)
    wx.showActionSheet({
      itemList: items,
      success: (res) => {
        const role = this.data.roleList[res.tapIndex]
        this.setData({
          selectedRoleId: role.id,
          selectedRoleName: role.name
        })
      }
    })
  },

  createAdmin() {
    const { username, password, realName, selectedRoleId } = this.data

    if (!username) {
      wx.showToast({ title: '请输入用户名', icon: 'none' })
      return
    }
    if (!password) {
      wx.showToast({ title: '请输入密码', icon: 'none' })
      return
    }

    const data = {
      username: username,
      password: password,
      realName: realName,
      roleId: selectedRoleId || 2  // 默认商家角色（role_id=2）
    }

    wx.showLoading({ title: '添加中...' })

    wx.request({
      url: 'http://localhost:9999/admin/create-admin',
      method: 'POST',
      header: { 'Content-Type': 'application/json' },
      data: data,
      success: (res) => {
        wx.hideLoading()
        console.log('添加结果:', res.data)
        if (res.data.code === 200) {
          wx.showToast({ title: '添加成功', icon: 'success' })
          setTimeout(() => {
            wx.navigateBack()
          }, 1500)
        } else {
          wx.showToast({ title: res.data.message || '添加失败', icon: 'none' })
        }
      },
      fail: () => {
        wx.hideLoading()
        wx.showToast({ title: '网络错误', icon: 'none' })
      }
    })
  }
})