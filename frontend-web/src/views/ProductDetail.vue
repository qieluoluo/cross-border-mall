<template>
  <div class="bg-white p-5 sm:p-8">
    <div v-if="productNotFound" class="py-20 text-center">
      <svg class="mx-auto h-24 w-24 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
      </svg>
      <h2 class="mt-4 text-2xl font-bold text-gray-800">商品不存在</h2>
      <p class="mt-2 text-gray-500">该商品可能已被删除或不存在</p>
      <button @click="goBack" class="mt-6 bg-orange-500 px-6 py-2.5 text-sm font-medium text-white transition hover:bg-orange-600">
        返回商品列表
      </button>
    </div>

    <div v-else-if="loading" class="py-20 text-center">
      <div class="mx-auto h-12 w-12 animate-spin rounded-full border-b-2 border-orange-500"></div>
      <p class="mt-4 text-gray-500">加载中...</p>
    </div>

    <div v-else-if="product" class="grid gap-8 lg:grid-cols-2 lg:gap-12">
      <div class="overflow-hidden bg-gray-100">
        <img
            :src="getProductImage(product)"
            @error="applyImageFallback"
            :alt="product.name"
            class="h-80 w-full object-cover sm:h-[30rem]"
        />
      </div>

      <div class="flex flex-col py-1">
        <p class="text-sm font-medium text-orange-600">精选商品</p>
        <h1 class="mt-2 text-2xl font-bold text-gray-900 sm:text-3xl">{{ product.name }}</h1>
        <p class="mt-3 leading-6 text-gray-500">{{ product.subTitle || product.description || '优选品质，为日常生活带来更多便利。' }}</p>
        <div class="mt-6 border-y border-gray-200 bg-orange-50 px-5 py-4">
          <span class="text-sm text-gray-500">售价</span>
          <p class="mt-1 text-3xl font-bold text-orange-600">¥{{ product.price }}</p>
        </div>

        <div class="mt-6 grid grid-cols-2 divide-x divide-gray-200 border-y border-gray-200 py-4 text-center">
          <div>
            <p class="text-sm text-gray-500">累计销量</p>
            <p class="mt-1 font-semibold text-gray-900">{{ product.sales || 0 }}</p>
          </div>
          <div>
            <p class="text-sm text-gray-500">当前库存</p>
            <p class="mt-1 font-semibold text-gray-900">{{ product.stock || 0 }}</p>
          </div>
        </div>

        <div class="mt-7 flex items-center gap-4">
          <span class="text-sm text-gray-600">购买数量</span>
          <div class="inline-flex items-center border border-gray-300 bg-white">
            <button @click="quantity = Math.max(1, quantity - 1)" class="flex h-10 w-10 items-center justify-center text-lg text-gray-600 transition hover:bg-gray-100">-</button>
            <span class="w-10 text-center text-sm font-medium">{{ quantity }}</span>
            <button @click="quantity = Math.min(product.stock || 999, quantity + 1)" class="flex h-10 w-10 items-center justify-center text-lg text-gray-600 transition hover:bg-gray-100">+</button>
          </div>
        </div>

        <div class="mt-8 flex flex-col gap-3 sm:flex-row">
          <button @click="addToCart" class="border border-orange-500 bg-orange-50 px-8 py-3 font-medium text-orange-600 transition hover:bg-orange-100">
            加入购物车
          </button>
          <button @click="buyNow" class="bg-orange-500 px-8 py-3 font-medium text-white transition hover:bg-orange-600">
            立即购买
          </button>
        </div>
      </div>
    </div>

    <div v-if="product" class="mt-12 border-t border-gray-200 pt-8">
      <p class="text-sm font-medium text-orange-600">商品信息</p>
      <h2 class="mt-1 text-xl font-bold text-gray-900">商品详情</h2>
      <div class="mt-5 max-w-3xl border-l-2 border-orange-400 pl-4">
        <p class="leading-8 text-gray-600">{{ product.detailHtml || product.description || '暂无详细描述' }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { productAPI, cartAPI } from '../api'
import { useRoute, useRouter } from 'vue-router'
import { getProductImage } from '../utils/image'
import fallbackProductImage from '../../../miniapp/小程序项目/images/xiaomi14.png'

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

const applyImageFallback = (event) => {
  event.target.onerror = null
  event.target.src = fallbackProductImage
}

onMounted(() => {
  loadProduct()
})
</script>
