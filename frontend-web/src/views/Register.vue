<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100">
    <div class="bg-white rounded-lg shadow-lg p-8 w-96">
      <h2 class="text-2xl font-bold text-center mb-6">注册</h2>
      
      <form @submit.prevent="handleRegister">
        <div class="mb-4">
          <label class="block text-gray-700 text-sm mb-2">用户名</label>
          <input 
            v-model="user.username" 
            type="text" 
            placeholder="请输入用户名"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
          />
        </div>
        
        <div class="mb-4">
          <label class="block text-gray-700 text-sm mb-2">密码</label>
          <input 
            v-model="user.password" 
            type="password" 
            placeholder="请输入密码"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
          />
        </div>
        
        <div class="mb-4">
          <label class="block text-gray-700 text-sm mb-2">确认密码</label>
          <input 
            v-model="confirmPassword" 
            type="password" 
            placeholder="请再次输入密码"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
          />
        </div>
        
        <div class="mb-4">
          <label class="block text-gray-700 text-sm mb-2">手机号</label>
          <input 
            v-model="user.phone" 
            type="tel" 
            placeholder="请输入手机号"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
          />
        </div>
        
        <div class="mb-4">
          <label class="block text-gray-700 text-sm mb-2">邮箱</label>
          <input 
            v-model="user.email" 
            type="email" 
            placeholder="请输入邮箱（选填）"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
          />
        </div>
        
        <button 
          type="submit" 
          class="w-full bg-orange-500 text-white py-3 rounded-lg hover:bg-orange-600 transition font-medium"
        >
          注册
        </button>
        
        <div class="mt-4 text-center">
          <span class="text-gray-500">已有账号？</span>
          <router-link to="/login" class="text-orange-500 ml-2">立即登录</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { userAPI } from '../api'
import { useRouter } from 'vue-router'

const router = useRouter()
const user = reactive({
  username: '',
  password: '',
  phone: '',
  email: ''
})
const confirmPassword = ref('')

const handleRegister = () => {
  if (!user.username || !user.password || !user.phone) {
    alert('请填写必填字段')
    return
  }
  
  if (user.password !== confirmPassword.value) {
    alert('两次输入的密码不一致')
    return
  }
  
  userAPI.register(user).then(res => {
    if (res.code === 200) {
      alert('注册成功，请登录')
      router.push('/login')
    } else {
      alert(res.message || '注册失败')
    }
  }).catch(() => {
    alert('注册失败，请检查网络')
  })
}
</script>
