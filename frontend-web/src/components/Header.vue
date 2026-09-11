<template>
  <header class="bg-white shadow-md sticky top-0 z-50">
    <div class="container mx-auto px-4">
      <div class="flex items-center justify-between h-16">
        <div class="flex items-center">
          <router-link to="/" class="text-2xl font-bold text-orange-500">Bundaberg</router-link>
        </div>

        <nav class="hidden md:flex items-center space-x-8">
          <router-link to="/" class="text-gray-700 hover:text-orange-500 transition">首页</router-link>
          <router-link to="/products" class="text-gray-700 hover:text-orange-500 transition">商品列表</router-link>
          <router-link to="/order" class="text-gray-700 hover:text-orange-500 transition">我的订单</router-link>
          <router-link to="/express" class="text-gray-700 hover:text-orange-500 transition">物流查询</router-link>
        </nav>

        <div class="flex items-center space-x-4">
          <div class="hidden md:block relative">
            <input
              v-model="searchKeyword"
              @keyup.enter="handleSearch"
              type="text"
              placeholder="搜索商品..."
              class="w-64 px-4 py-2 border border-gray-300 rounded-full focus:outline-none focus:ring-2 focus:ring-orange-500 pr-10"
            />
            <button
              @click="handleSearch"
              class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-orange-500"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
              </svg>
            </button>
          </div>

          <div class="relative cart-icon">
            <router-link to="/cart" class="text-gray-700 hover:text-orange-500">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"></path>
              </svg>
            </router-link>
            <span v-if="cartCount > 0" class="absolute -top-2 -right-2 bg-red-500 text-white text-xs w-5 h-5 rounded-full flex items-center justify-center">
              {{ cartCount }}
            </span>
          </div>

          <div v-if="user" class="flex items-center space-x-2">
            <span class="text-gray-700">{{ user.nickname || user.username }}</span>
            <router-link to="/user" class="text-gray-700 hover:text-orange-500">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
              </svg>
            </router-link>
            <button @click="logout" class="text-gray-500 hover:text-orange-500">退出</button>
          </div>
          <div v-else class="flex items-center space-x-2">
            <router-link to="/login" class="text-gray-700 hover:text-orange-500">登录</router-link>
            <router-link to="/register" class="text-gray-700 hover:text-orange-500">注册</router-link>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { userAPI, cartAPI } from '../api'
import { useRouter } from 'vue-router'

const router = useRouter()
const user = ref(null)
const cartCount = ref(0)
const searchKeyword = ref('')

function clearAuthData() {
  localStorage.removeItem('token')
  localStorage.removeItem('userId')
  localStorage.removeItem('username')
  localStorage.removeItem('nickname')
  user.value = null
}

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push(`/products?keyword=${encodeURIComponent(searchKeyword.value.trim())}`)
    searchKeyword.value = ''
  }
}

const checkLogin = async () => {
  const token = localStorage.getItem('token')
  const userId = localStorage.getItem('userId')

  if (!token || !userId) {
    clearAuthData()
    return
  }

  try {
    const res = await userAPI.getUserInfo(userId)
    if (res.code === 200 && res.data) {
      const userData = res.data
      const dbUsername = userData.username || userData.userName || userData.name || '用户'
      const dbNickname = userData.nickname || userData.nickName || dbUsername
      localStorage.setItem('username', dbUsername)
      localStorage.setItem('nickname', dbNickname)
      user.value = {
        ...userData,
        username: dbUsername,
        nickname: dbNickname
      }
      console.log('登录状态验证成功 - 用户ID:', userId, '用户名:', dbUsername, '昵称:', dbNickname)
    } else {
      clearAuthData()
    }
  } catch (err) {
    console.warn('后端用户服务不可用，清除登录状态')
    clearAuthData()
  }
}

const getCartCount = () => {
  const token = localStorage.getItem('token')
  if (token) {
    cartAPI.getCartList().then(res => {
      if (res.code === 200) {
        const data = res.data || []
        cartCount.value = data.reduce((sum, item) => sum + (item.quantity || 0), 0)
      } else {
        cartCount.value = 0
      }
    }).catch(() => {
      cartCount.value = 0
    })
  } else {
    cartCount.value = 0
  }
}

const logout = () => {
  clearAuthData()
  cartCount.value = 0
  router.push('/login')
}

const refreshData = () => {
  checkLogin()
  getCartCount()
}

onMounted(() => {
  refreshData()
  router.afterEach(refreshData)
})

onUnmounted(() => {
  router.afterEach(() => {})
})
</script>
