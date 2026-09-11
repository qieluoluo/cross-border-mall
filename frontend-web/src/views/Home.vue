<template>
  <div class="bg-white">
    <el-carousel :interval="4000" type="card" height="450px">
      <el-carousel-item v-for="item in carouselItems" :key="item.id">
        <img :src="item.image" :alt="item.title" class="w-full h-full object-cover" />
      </el-carousel-item>
    </el-carousel>

    <div class="py-8">
      <h2 class="text-2xl font-bold text-center mb-8">热门商品</h2>
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
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
              <span class="text-gray-400 text-sm">销量: {{ product.sales }}</span>
              <button @click.stop="addToCart(product)" class="bg-orange-500 text-white px-4 py-1 rounded hover:bg-orange-600 transition">
                加入购物车
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="py-8 bg-gray-50">
      <h2 class="text-2xl font-bold text-center mb-8">商品分类</h2>
      <div class="grid grid-cols-2 md:grid-cols-5 gap-4">
        <div
            v-for="category in categories"
            :key="category.id"
            class="bg-white rounded-lg p-4 text-center hover:shadow-md transition cursor-pointer"
            @click="goToProducts(category.id)"
        >
          <div class="w-16 h-16 mx-auto bg-orange-100 rounded-full flex items-center justify-center mb-2">
            <span class="text-2xl">{{ category.icon }}</span>
          </div>
          <span class="text-gray-700 font-medium">{{ category.name }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { productAPI, cartAPI } from '../api'
import { useRouter } from 'vue-router'
import { getProductImage } from '../utils/image'

const router = useRouter()
const products = ref([])

const carouselItems = [
  { id: 1, image: '/images/iphone15pro.png', title: '数码产品' },
  { id: 2, image: '/images/uniqlo_dress.png', title: '时尚服饰' },
  { id: 3, image: '/images/haier_bcd500.png', title: '家用电器' }
]

const categories = [
  { id: 1, name: '手机数码', icon: '📱' },
  { id: 2, name: '家用电器', icon: '📺' },
  { id: 3, name: '服装鞋帽', icon: '👔' },
  { id: 4, name: '美妆个护', icon: '💄' },
  { id: 5, name: '图书文娱', icon: '📚' }
]

const loadProducts = () => {
  productAPI.getProductList(1, 8).then(res => {
    if (res.code === 200) {
      products.value = res.data.records
    }
  })
}

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

const goToProducts = (categoryId) => {
  router.push(`/products?category=${categoryId}`)
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

onMounted(() => {
  loadProducts()
})
</script>
