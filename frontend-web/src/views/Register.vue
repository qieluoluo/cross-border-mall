<template>
  <div class="flex min-h-screen items-center justify-center bg-gray-100 px-4 py-10">
    <section class="w-full max-w-md border border-gray-200 bg-white p-6 shadow-xl sm:p-9">
      <div class="border-b border-gray-200 pb-6">
        <router-link to="/" class="text-lg font-bold text-orange-600">Bundaberg</router-link>
        <p class="mt-5 text-sm font-medium text-orange-600">创建账户</p>
        <h1 class="mt-1 text-2xl font-bold text-gray-900">开始购物</h1>
        <p class="mt-2 text-sm leading-6 text-gray-500">填写基本信息，即可保存心仪商品并追踪订单。</p>
      </div>

      <form class="mt-7 space-y-5" @submit.prevent="handleRegister">
        <div>
          <label class="mb-2 block text-sm font-medium text-gray-700">用户名</label>
          <input v-model="user.username" type="text" autocomplete="username" placeholder="请输入用户名" class="w-full border border-gray-300 px-4 py-3 text-sm text-gray-900 outline-none transition placeholder:text-gray-400 focus:border-orange-500 focus:ring-2 focus:ring-orange-100" />
        </div>
        <div>
          <label class="mb-2 block text-sm font-medium text-gray-700">密码</label>
          <input v-model="user.password" type="password" autocomplete="new-password" placeholder="请输入密码" class="w-full border border-gray-300 px-4 py-3 text-sm text-gray-900 outline-none transition placeholder:text-gray-400 focus:border-orange-500 focus:ring-2 focus:ring-orange-100" />
        </div>
        <div>
          <label class="mb-2 block text-sm font-medium text-gray-700">确认密码</label>
          <input v-model="confirmPassword" type="password" autocomplete="new-password" placeholder="请再次输入密码" class="w-full border border-gray-300 px-4 py-3 text-sm text-gray-900 outline-none transition placeholder:text-gray-400 focus:border-orange-500 focus:ring-2 focus:ring-orange-100" />
        </div>
        <div>
          <label class="mb-2 block text-sm font-medium text-gray-700">手机号</label>
          <input v-model="user.phone" type="tel" autocomplete="tel" placeholder="请输入手机号" class="w-full border border-gray-300 px-4 py-3 text-sm text-gray-900 outline-none transition placeholder:text-gray-400 focus:border-orange-500 focus:ring-2 focus:ring-orange-100" />
        </div>
        <div>
          <label class="mb-2 block text-sm font-medium text-gray-700">邮箱 <span class="font-normal text-gray-400">选填</span></label>
          <input v-model="user.email" type="email" autocomplete="email" placeholder="请输入邮箱" class="w-full border border-gray-300 px-4 py-3 text-sm text-gray-900 outline-none transition placeholder:text-gray-400 focus:border-orange-500 focus:ring-2 focus:ring-orange-100" />
        </div>
        <button type="submit" class="w-full bg-orange-500 py-3 text-sm font-semibold text-white transition hover:bg-orange-600 focus:outline-none focus:ring-2 focus:ring-orange-300">
          注册账户
        </button>
        <p class="text-center text-sm text-gray-500">
          已有账号？
          <router-link to="/login" class="ml-1 font-medium text-orange-600 hover:text-orange-700">立即登录</router-link>
        </p>
      </form>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
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
    ElMessage.warning('请填写必填字段')
    return
  }
  
  if (user.password !== confirmPassword.value) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  
  userAPI.register(user).then(() => {
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  }).catch((err) => {
    ElMessage.error(err.message || '注册失败')
  })
}
</script>
