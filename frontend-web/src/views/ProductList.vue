<template>
  <div class="bg-white">
    <div class="flex">
      <aside class="w-64 p-4 bg-gray-50">
        <h3 class="font-bold text-lg mb-4">商品分类</h3>
        <ul class="space-y-2">
          <li
              v-for="category in categoryList"
              :key="category.id"
              :class="[
              'p-2 rounded cursor-pointer transition',
              selectedCategory === category.id ? 'bg-orange-500 text-white' : 'hover:bg-gray-200'
            ]"
              @click="selectCategory(category.id)"
          >
            {{ category.name }}
          </li>
        </ul>
      </aside>

      <main class="flex-1 p-4">
        <div class="flex justify-between items-center mb-4">
          <div class="flex items-center space-x-4">
            <span class="text-gray-600">排序：</span>
            <button
                v-for="sort in sortOptions"
                :key="sort.value"
                :class="[
                'px-3 py-1 rounded transition',
                currentSort === sort.value ? 'bg-orange-500 text-white' : 'bg-gray-100 hover:bg-gray-200'
              ]"
                @click="handleSortChange(sort.value)"
            >
              {{ sort.label }}
            </button>
          </div>
          <div class="flex items-center space-x-2">
            <span class="text-gray-600">共 {{ total }} 件商品</span>
          </div>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          <div
              v-for="product in products"
              :key="product.id"
              class="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition cursor-pointer"
              @click="goToDetail(product.id)"
          >
            <div class="h-48 overflow-hidden">
              <img
                  :src="getProductImage(product)"
                  :alt="product.name"
                  class="w-full h-full object-cover hover:scale-105 transition"
              />
            </div>
            <div class="p-4">
              <h3 class="text-lg font-semibold text-gray-800 truncate">{{ product.name }}</h3>
              <p class="text-gray-500 text-sm mt-1">{{ product.subTitle }}</p>
              <p class="text-orange-500 text-xl font-bold mt-2">¥{{ product.price }}</p>
              <div class="flex justify-between items-center mt-3">
                <span class="text-gray-400 text-sm">库存: {{ product.stock }}</span>
                <button @click.stop="addToCart(product)" class="bg-orange-500 text-white px-4 py-1 rounded hover:bg-orange-600 transition">
                  加入购物车
                </button>
              </div>
            </div>
          </div>
        </div>

        <div class="mt-8 flex justify-center">
          <el-pagination
              :current-page="pageNum"
              :page-size="pageSize"
              :total="total"
              @current-change="handlePageChange"
          />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { productAPI, cartAPI } from '../api'
import { useRouter, useRoute } from 'vue-router'
import { getProductImage } from '../utils/image'

const router = useRouter()
const route = useRoute()
const products = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(9)
const selectedCategory = ref(0)
const currentSort = ref('default')
const searchKeyword = ref('')

const categoryList = [
  { id: 0, name: '全部商品' },
  { id: 1, name: '手机数码' },
  { id: 2, name: '家用电器' },
  { id: 3, name: '服装鞋帽' },
  { id: 4, name: '美妆个护' },
  { id: 5, name: '图书文娱' }
]

const sortOptions = [
  { label: '默认', value: 'default' },
  { label: '价格升序', value: 'price_asc' },
  { label: '价格降序', value: 'price_desc' },
  { label: '销量优先', value: 'sales' }
]

const loadProducts = () => {
  if (searchKeyword.value) {
    productAPI.searchProducts(searchKeyword.value, pageNum.value, pageSize.value).then(res => {
      if (res.code === 200) {
        products.value = res.data.records || res.data
        total.value = res.data.total || products.value.length
      }
    })
  } else {
    productAPI.getProductList(pageNum.value, pageSize.value, selectedCategory.value, currentSort.value).then(res => {
      if (res.code === 200) {
        products.value = res.data.records
        total.value = res.data.total
      }
    })
  }
}

onMounted(() => {
  const keyword = route.query.keyword
  if (keyword) {
    searchKeyword.value = decodeURIComponent(keyword)
  }
  loadProducts()
})

watch(() => route.query.keyword, (newKeyword) => {
  if (newKeyword) {
    searchKeyword.value = decodeURIComponent(newKeyword)
    pageNum.value = 1
    selectedCategory.value = 0
    loadProducts()
  }
})

const selectCategory = (id) => {
  selectedCategory.value = id
  pageNum.value = 1
  loadProducts()
}

const handleSortChange = (sortValue) => {
  currentSort.value = sortValue
  pageNum.value = 1
  loadProducts()
}

const handlePageChange = (page) => {
  pageNum.value = page
  loadProducts()
}

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

const addToCart = (product) => {
  if (!localStorage.getItem('token')) {
    router.push('/login')
    return
  }
  cartAPI.addToCart(product.id, 1).then(res => {
    if (res.code === 200) {
      alert('加入购物车成功')
    }
  }).catch(err => {
    console.error('加入购物车失败:', err)
    alert('加入购物车失败，请稍后重试')
  })
}
</script>
