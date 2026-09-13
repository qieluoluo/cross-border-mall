import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { loginApi } from '@/api'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  const router = useRouter()

  const isLoggedIn = computed(() => !!token.value)

  const isMerchant = computed(() => {
    return userInfo.value?.roleId === 2
  })

  const isSuperAdmin = computed(() => {
    return userInfo.value?.roleId === 1
  })

  const login = async (loginForm) => {
    try {
      const res = await loginApi(loginForm)
      token.value = 'admin-token-' + Date.now()
      userInfo.value = res.data || { username: loginForm.username }

      localStorage.setItem('token', token.value)
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))

      ElMessage.success('登录成功')
      router.push('/')
      return true
    } catch (error) {
      console.error('登录失败详细错误:', error)
      token.value = ''
      userInfo.value = {}
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      ElMessage.error(error?.message || '登录失败，请检查账号密码')
      return false
    }
  }

  const logout = () => {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/login')
    ElMessage.success('已退出登录')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isMerchant,
    isSuperAdmin,
    login,
    logout
  }
})