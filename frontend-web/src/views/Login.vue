<template>
  <div class="flex min-h-screen items-center justify-center bg-gray-100 px-4 py-10">
    <section class="w-full max-w-md border border-gray-200 bg-white p-6 shadow-xl sm:p-9">
      <div class="border-b border-gray-200 pb-6">
        <router-link to="/" class="text-lg font-bold text-orange-600">Bundaberg</router-link>
        <p class="mt-5 text-sm font-medium text-orange-600">欢迎回来</p>
        <h1 class="mt-1 text-2xl font-bold text-gray-900">登录账户</h1>
        <p class="mt-2 text-sm leading-6 text-gray-500">登录后可查看订单、购物车与个人资料。</p>
      </div>

      <form class="mt-7" @submit.prevent="handleLogin">
        <div class="mb-5">
          <label class="mb-2 block text-sm font-medium text-gray-700">用户名</label>
          <input
            v-model="username"
            type="text"
            autocomplete="username"
            placeholder="请输入用户名"
            class="w-full border border-gray-300 px-4 py-3 text-sm text-gray-900 outline-none transition placeholder:text-gray-400 focus:border-orange-500 focus:ring-2 focus:ring-orange-100"
          />
        </div>

        <div class="mb-6">
          <label class="mb-2 block text-sm font-medium text-gray-700">密码</label>
          <input
            v-model="password"
            type="password"
            autocomplete="current-password"
            placeholder="请输入密码"
            class="w-full border border-gray-300 px-4 py-3 text-sm text-gray-900 outline-none transition placeholder:text-gray-400 focus:border-orange-500 focus:ring-2 focus:ring-orange-100"
          />
        </div>

        <button type="submit" class="w-full bg-orange-500 py-3 text-sm font-semibold text-white transition hover:bg-orange-600 focus:outline-none focus:ring-2 focus:ring-orange-300">
          登录
        </button>

        <p class="mt-5 text-center text-sm text-gray-500">
          还没有账号？
          <router-link to="/register" class="ml-1 font-medium text-orange-600 hover:text-orange-700">立即注册</router-link>
        </p>
      </form>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { userAPI } from '../api'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const username = ref('')
const password = ref('')

const handleLogin = async () => {
  if (!username.value || !password.value) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  
  try {
    const res = await userAPI.login(username.value, password.value)
    if (res.code === 200) {
      if (res.data && res.data.token) {
        localStorage.setItem('token', res.data.token)
        localStorage.setItem('userId', String(res.data.userId))
        
        try {
          const userRes = await userAPI.getUserInfo(res.data.userId)
          if (userRes.code === 200 && userRes.data) {
            const userData = userRes.data
            const dbUsername = userData.username || userData.userName || userData.name || username.value
            const dbNickname = userData.nickname || userData.nickName || userData.username || userData.userName || username.value
            localStorage.setItem('username', dbUsername)
            localStorage.setItem('nickname', dbNickname)
          } else {
            localStorage.setItem('username', username.value)
            localStorage.setItem('nickname', username.value)
          }
        } catch {
          localStorage.setItem('username', username.value)
          localStorage.setItem('nickname', username.value)
        }
        
        ElMessage.success('登录成功')
        router.push('/')
      } else {
        ElMessage.error('登录失败：未获取到token')
      }
    } else {
      const msg = res.message || res.msg || '登录失败'
      try {
        ElMessage.error(decodeURIComponent(escape(msg)))
      } catch {
        ElMessage.error(msg)
      }
    }
  } catch (error) {
    console.error('登录错误:', error)
    ElMessage.error('登录失败，请检查网络或联系管理员')
  }
}
</script>
