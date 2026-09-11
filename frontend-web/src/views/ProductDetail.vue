<template>
  <div class="bg-white p-6">
    <div v-if="productNotFound" class="text-center py-16">
      <svg class="w-24 h-24 mx-auto text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
      </svg>
      <h2 class="text-2xl font-bold text-gray-800 mt-4">商品不存在</h2>
      <p class="text-gray-500 mt-2">该商品可能已被删除或不存在</p>
      <button
          @click="goBack"
          class="mt-6 bg-orange-500 text-white px-6 py-2 rounded-lg hover:bg-orange-600 transition"
      >
        返回商品列表
      </button>
    </div>

    <div v-else-if="loading" class="text-center py-16">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-orange-500 mx-auto"></div>
      <p class="text-gray-500 mt-4">加载中...</p>
    </div>

    <div v-else-if="product" class="flex">
      <div class="w-1/2">
        <img
            :src="getProductImage(product)"
            :alt="product.name"
            class="w-full h-96 object-cover rounded-lg"
        />
      </div>

      <div class="w-1/2 pl-8">
        <h1 class="text-2xl font-bold text-gray-800">{{ product.name }}</h1>
        <p class="text-gray-500 mt-2">{{ product.subTitle || product.description }}</p>
        <p class="text-orange-500 text-3xl font-bold mt-4">¥{{ product.price }}</p>

        <div class="mt-6">
          <span class="text-gray-600">销量：</span>
          <span class="text-gray-800">{{ product.sales || 0 }}</span>
          <span class="text-gray-400 ml-4">库存：</span>
          <span class="text-gray-800">{{ product.stock || 0 }}</span>
        </div>

        <div class="mt-8">
          <span class="text-gray-600">数量：</span>
          <div class="inline-flex items-center border border-gray-300">
            <button
                @click="quantity = Math.max(1, quantity - 1)"
                class="w-10 h-10 flex items-center justify-center hover:bg-gray-100"
            >-</button>
            <span class="w-10 text-center">{{ quantity }}</span>
            <button
                @click="quantity = Math.min(product.stock || 999, quantity + 1)"
                class="w-10 h-10 flex items-center justify-center hover:bg-gray-100"
            >+</button>
          </div>
        </div>

        <div class="mt-8 flex space-x-4">
          <button
              @click="addToCart"
              class="bg-orange-500 text-white px-8 py-3 rounded-lg hover:bg-orange-600 transition font-medium"
          >
            加入购物车
          </button>
          <button
              @click="buyNow"
              class="bg-red-500 text-white px-8 py-3 rounded-lg hover:bg-red-600 transition font-medium"
          >
            立即购买
          </button>
        </div>
      </div>
    </div>

    <div v-if="product" class="mt-12">
      <h2 class="text-xl font-bold mb-4">商品详情</h2>
      <div class="border-t pt-4">
        <p class="text-gray-600 leading-relaxed">{{ product.detailHtml || product.description || '暂无详细描述' }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { productAPI, cartAPI } from '../api'
import { useRoute, useRouter } from 'vue-router'
import { getProductImage } from '../utils/image'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const quantity = ref(1)
const loading = ref(true)
const productNotFound = ref(false)

const goBack = () => {
  router.push('/products')
}

const loadProduct = () => {
  loading.value = true
  productNotFound.value = false
  
  const id = route.params.id
  productAPI.getProductById(id).then(res => {
    if (res.code === 200 && res.data) {
      product.value = res.data
      productNotFound.value = false
    } else if (res.code === 404) {
      product.value = null
      productNotFound.value = true
    } else {
      product.value = null
      productNotFound.value = false
    }
    loading.value = false
  }).catch(err => {
    console.error('加载商品失败:', err)
    product.value = null
    productNotFound.value = false
    loading.value = false
  })
}

const addToCart = () => {
  if (!localStorage.getItem('token')) {
    router.push('/login')
    return
  }
  cartAPI.addToCart(product.value.id, quantity.value).then(res => {
    if (res.code === 200) {
      alert('加入购物车成功')
    }
  }).catch(err => {
    console.error('加入购物车失败:', err)
    alert('加入购物车失败，请稍后重试')
  })
}

const buyNow = () => {
  if (!localStorage.getItem('token')) {
    router.push('/login')
    return
  }
  cartAPI.addToCart(product.value.id, quantity.value).then(res => {
    if (res.code === 200) {
      router.push('/cart')
    } else {
      alert('添加商品失败')
    }
  }).catch(() => {
    alert('添加商品失败')
  })
}

onMounted(() => {
  loadProduct()
})
</script>
