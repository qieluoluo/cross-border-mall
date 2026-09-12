<template>
  <div class="space-y-12 pb-12">
    <section class="overflow-hidden bg-white shadow-sm ring-1 ring-gray-100">
      <el-carousel :interval="4000" height="420px" class="home-carousel">
        <el-carousel-item v-for="(item, index) in carouselItems" :key="item.id">
          <div class="relative h-full">
            <img :src="item.image" :alt="item.title" class="h-full w-full object-cover" />
            <div class="absolute inset-x-0 bottom-0 bg-black/55 px-6 py-6 text-white sm:px-10 sm:py-8">
              <p class="text-sm font-medium text-orange-200">精选好物</p>
              <h1 class="mt-1 text-2xl font-bold sm:text-3xl">{{ item.title }}</h1>
              <p class="mt-2 max-w-xl text-sm text-gray-200">发现日常所需的高品质商品，精选推荐持续更新。</p>
              <button @click="goToProducts(index + 1)" class="mt-4 border border-white px-4 py-2 text-sm font-medium transition hover:bg-white hover:text-gray-900">
                立即选购
              </button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <section class="grid grid-cols-1 gap-px overflow-hidden border border-gray-200 bg-gray-200 sm:grid-cols-3">
      <div class="bg-white px-6 py-5 text-center">
        <p class="text-lg font-semibold text-gray-900">品质甄选</p>
        <p class="mt-1 text-sm text-gray-500">精选实用好物</p>
      </div>
      <div class="bg-white px-6 py-5 text-center">
        <p class="text-lg font-semibold text-gray-900">安心配送</p>
        <p class="mt-1 text-sm text-gray-500">订单进度清晰可查</p>
      </div>
      <div class="bg-white px-6 py-5 text-center">
        <p class="text-lg font-semibold text-gray-900">贴心服务</p>
        <p class="mt-1 text-sm text-gray-500">售后问题及时响应</p>
      </div>
    </section>

    <section>
      <div class="mb-6 flex items-end justify-between border-b border-gray-200 pb-4">
        <div>
          <p class="text-sm font-medium text-orange-600">本周推荐</p>
          <h2 class="mt-1 text-2xl font-bold text-gray-900">热门商品</h2>
        </div>
        <button @click="goToProducts(0)" class="text-sm font-medium text-gray-600 transition hover:text-orange-600">查看全部</button>
      </div>
      <div class="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        <div
            v-for="product in products"
            :key="product.id"
            class="group cursor-pointer overflow-hidden border border-gray-200 bg-white transition duration-200 hover:-translate-y-1 hover:border-orange-200 hover:shadow-lg"
            @click="goToDetail(product.id)"
        >
          <div class="h-52 overflow-hidden bg-gray-100">
            <img
                :src="getProductImage(product)"
                @error="applyImageFallback"
                :alt="product.name"
                class="h-full w-full object-cover transition duration-300 group-hover:scale-105"
            />
          </div>
          <div class="p-4">
            <h3 class="truncate text-base font-semibold text-gray-900">{{ product.name }}</h3>
            <p class="mt-1 h-5 truncate text-sm text-gray-500">{{ product.subTitle || '优选商品，限时热卖' }}</p>
            <div class="mt-4 flex items-end justify-between gap-3">
              <div>
                <p class="text-xl font-bold text-orange-600">¥{{ product.price }}</p>
                <p class="mt-1 text-xs text-gray-400">已售 {{ product.sales || 0 }} 件</p>
              </div>
              <button @click.stop="addToCart(product)" class="shrink-0 bg-orange-500 px-3 py-2 text-sm font-medium text-white transition hover:bg-orange-600 focus:outline-none focus:ring-2 focus:ring-orange-300">
                加入购物车
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="border-y border-gray-200 bg-white px-4 py-8 sm:px-8">
      <div class="mb-6 text-center">
        <p class="text-sm font-medium text-orange-600">按需选购</p>
        <h2 class="mt-1 text-2xl font-bold text-gray-900">商品分类</h2>
      </div>
      <div class="grid grid-cols-2 gap-3 md:grid-cols-5">
        <div
            v-for="category in categories"
            :key="category.id"
            class="cursor-pointer border border-gray-200 px-3 py-5 text-center transition hover:border-orange-300 hover:bg-orange-50"
            @click="goToProducts(category.id)"
        >
          <div class="mx-auto mb-3 flex h-14 w-14 items-center justify-center rounded-full bg-orange-100">
            <span class="text-2xl" aria-hidden="true">{{ category.icon }}</span>
          </div>
          <span class="text-sm font-medium text-gray-800">{{ category.name }}</span>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { productAPI, cartAPI } from '../api'
import { useRouter } from 'vue-router'
import { getProductImage } from '../utils/image'
import carouselPhoneImage from '../../../miniapp/小程序项目/images/iphone.png'
import carouselDressImage from '../../../miniapp/小程序项目/images/qunzi.png'
import carouselApplianceImage from '../../../miniapp/小程序项目/images/haier.png'
import fallbackProductImage from '../../../miniapp/小程序项目/images/xiaomi14.png'

const router = useRouter()
const products = ref([])

const carouselItems = [
  { id: 1, image: carouselPhoneImage, title: '数码产品' },
  { id: 2, image: carouselDressImage, title: '时尚服饰' },
  { id: 3, image: carouselApplianceImage, title: '家用电器' }
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

const applyImageFallback = (event) => {
  event.target.onerror = null
  event.target.src = fallbackProductImage
}

onMounted(() => {
  loadProducts()
})
</script>
