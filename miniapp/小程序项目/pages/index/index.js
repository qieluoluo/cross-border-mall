const { request } = require('../../utils/request')
const { resolveImage, DEFAULT_IMAGE } = require('../../utils/config')

const CATEGORY_MAP = {
  1: [1, 11, 12, 13, 111, 112],
  2: [2, 21, 22, 211, 212],
  3: [3, 31, 32],
  4: [4],
  5: [5]
}

Page({
  data: {
    productList: [],
    displayProductList: [],
    searchKeyword: '',
    pageNum: 1,
    pageSize: 50,
    loading: false,
    noMore: false,
    searchTimer: null,
    isSearching: false,
    banners: [
      { id: 1, image: '/images/banner-digital.jpg', tag: '数码精选', title: '把好用的数码带回家', categoryId: 1 },
      { id: 2, image: '/images/banner-fashion.jpg', tag: '当季穿搭', title: '轻松选一件顺眼的衣服', categoryId: 3 },
      { id: 3, image: '/images/banner-home.jpg', tag: '家居焕新', title: '厨房和客厅也能更舒服', categoryId: 2 }
    ],
    categories: [
      { id: 1, name: '数码', icon: '📱' },
      { id: 2, name: '家电', icon: '📺' },
      { id: 3, name: '服饰', icon: '👔' },
      { id: 4, name: '美妆', icon: '💄' },
      { id: 5, name: '图书', icon: '📚' }
    ]
  },

  processProductList(list) {
    return (list || []).map((item) => ({
      ...item,
      name: item.name,
      subTitle: item.subTitle || item.sub_title || '',
      price: item.price,
      sales: item.sales || 0,
      categoryId: item.categoryId || item.category_id,
      mainImage: resolveImage(item.mainImage || item.main_image)
    }))
  },

  onLoad() {
    this.loadProducts(true)
  },

  onPullDownRefresh() {
    this.loadProducts(true).finally(() => wx.stopPullDownRefresh())
  },

  onReachBottom() {
    if (!this.data.isSearching) {
      this.loadProducts(false)
    }
  },

  loadProducts(reset = false) {
    if (this.data.loading) return Promise.resolve()
    if (!reset && this.data.noMore) return Promise.resolve()

    const pageNum = reset ? 1 : this.data.pageNum
    this.setData({ loading: true })

    return request({
      url: '/api/product/list',
      data: { pageNum, pageSize: this.data.pageSize }
    }).then((res) => {
      const newList = this.processProductList(res.data.records || [])
      const productList = reset ? newList : this.data.productList.concat(newList)
      this.setData({
        productList,
        displayProductList: productList,
        pageNum: pageNum + 1,
        noMore: newList.length < this.data.pageSize,
        isSearching: false
      })
    }).catch((err) => {
      wx.showToast({ title: err.message || '加载失败', icon: 'none' })
    }).finally(() => {
      this.setData({ loading: false })
    })
  },

  onSearchInput(e) {
    const keyword = e.detail.value
    this.setData({ searchKeyword: keyword })
    if (this.data.searchTimer) clearTimeout(this.data.searchTimer)
    const timer = setTimeout(() => this.doSearch(keyword), 400)
    this.setData({ searchTimer: timer })
  },

  onSearchConfirm() {
    if (this.data.searchTimer) clearTimeout(this.data.searchTimer)
    this.doSearch(this.data.searchKeyword)
  },

  doSearch(keyword) {
    const value = (keyword || '').trim()
    if (!value) {
      this.setData({
        isSearching: false,
        displayProductList: this.data.productList,
        searchKeyword: ''
      })
      return
    }

    this.setData({ isSearching: true, loading: true })
    request({
      url: '/api/product/search',
      data: { keyword: value, pageNum: 1, pageSize: 100, status: 1 }
    }).then((res) => {
      this.setData({
        displayProductList: this.processProductList(res.data.records || []),
        noMore: true
      })
    }).catch((err) => {
      wx.showToast({ title: err.message || '搜索失败', icon: 'none' })
    }).finally(() => {
      this.setData({ loading: false })
    })
  },

  goToCategory(e) {
    const id = Number(e.currentTarget.dataset.id)
    const allowed = CATEGORY_MAP[id] || [id]
    const filtered = this.data.productList.filter((item) => allowed.includes(Number(item.categoryId)))
    this.setData({
      displayProductList: filtered,
      isSearching: true,
      searchKeyword: '',
      noMore: true
    })
    if (filtered.length === 0) {
      wx.showToast({ title: '该分类暂无商品', icon: 'none' })
    }
  },

  onImageError(e) {
    const index = e.currentTarget.dataset.index
    if (index === undefined || index === null) return
    this.setData({
      [`displayProductList[${index}].mainImage`]: DEFAULT_IMAGE
    })
  },

  goToDetail(e) {
    wx.navigateTo({
      url: `/pages/detail/detail?id=${e.currentTarget.dataset.id}`
    })
  }
})
