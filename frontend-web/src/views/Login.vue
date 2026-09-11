<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100">
    <div class="bg-white rounded-lg shadow-lg p-8 w-96">
      <h2 class="text-2xl font-bold text-center mb-6">登录</h2>
      
      <form @submit.prevent="handleLogin">
        <div class="mb-4">
          <label class="block text-gray-700 text-sm mb-2">用户名</label>
          <input 
            v-model="username" 
            type="text" 
            placeholder="请输入用户名"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
          />
        </div>
        
        <div class="mb-4">
          <label class="block text-gray-700 text-sm mb-2">密码</label>
          <input 
            v-model="password" 
            type="password" 
            placeholder="请输入密码"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
          />
        </div>
        
        <button 
          type="submit" 
          class="w-full bg-orange-500 text-white py-3 rounded-lg hover:bg-orange-600 transition font-medium"
        >
          登录
        </button>
        
        <div class="mt-4 text-center">
          <span class="text-gray-500">还没有账号？</span>
          <router-link to="/register" class="text-orange-500 ml-2">立即注册</router-link>
        </div>
      </form>
    </div>
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
