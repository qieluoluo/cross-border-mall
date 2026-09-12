<template>
  <div class="bg-white p-5 sm:p-8">
    <div class="mb-7 border-b border-gray-200 pb-5">
      <p class="text-sm font-medium text-orange-600">账户设置</p>
      <h1 class="mt-1 text-2xl font-bold text-gray-900">个人中心</h1>
    </div>
    
    <div v-if="userInfo" class="grid grid-cols-1 gap-6 lg:grid-cols-3">
      <div class="lg:col-span-1">
        <div class="border border-gray-200 bg-gray-50 p-5">
          <div class="flex flex-col items-center">
            <div class="mb-4 flex h-20 w-20 items-center justify-center rounded-full bg-orange-100">
              <svg class="w-10 h-10 text-orange-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
              </svg>
            </div>
            <h3 class="text-lg font-bold text-gray-900">{{ userInfo.nickname || userInfo.username }}</h3>
            <p class="mt-1 text-sm text-gray-500">{{ userInfo.phone }}</p>
          </div>
        </div>
        
        <div class="mt-4 border border-gray-200 bg-white p-5">
          <h4 class="mb-4 font-bold text-gray-900">账户信息</h4>
          <div class="space-y-3 text-sm">
            <p><span class="text-gray-500">用户名：</span>{{ userInfo.username }}</p>
            <p><span class="text-gray-500">邮箱：</span>{{ userInfo.email || '未绑定' }}</p>
            <p><span class="text-gray-500">性别：</span>{{ userInfo.gender === 1 ? '男' : userInfo.gender === 2 ? '女' : '未知' }}</p>
            <p><span class="text-gray-500">注册时间：</span>{{ userInfo.createTime }}</p>
          </div>
        </div>
      </div>
      
      <div class="lg:col-span-2">
        <div class="border border-gray-200 bg-white p-5 sm:p-6">
          <div class="mb-5 border-b border-gray-100 pb-4">
            <p class="text-sm font-medium text-orange-600">基础资料</p>
            <h4 class="mt-1 font-bold text-gray-900">修改个人信息</h4>
          </div>
          <form @submit.prevent="handleUpdate">
            <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <div>
                <label class="block text-gray-700 text-sm mb-2">昵称</label>
                <input 
                  v-model="updateForm.nickname" 
                  type="text" 
                  class="w-full border border-gray-300 px-4 py-2.5 text-sm outline-none transition focus:border-orange-500 focus:ring-2 focus:ring-orange-100"
                />
              </div>
              <div>
                <label class="block text-gray-700 text-sm mb-2">邮箱</label>
                <input 
                  v-model="updateForm.email" 
                  type="email" 
                  class="w-full border border-gray-300 px-4 py-2.5 text-sm outline-none transition focus:border-orange-500 focus:ring-2 focus:ring-orange-100"
                />
              </div>
              <div>
                <label class="block text-gray-700 text-sm mb-2">性别</label>
                <select 
                  v-model.number="updateForm.gender" 
                  class="w-full border border-gray-300 px-4 py-2.5 text-sm outline-none transition focus:border-orange-500 focus:ring-2 focus:ring-orange-100"
                >
                  <option :value="0">未知</option>
                  <option :value="1">男</option>
                  <option :value="2">女</option>
                </select>
              </div>
            </div>
            <button 
              type="submit" 
              class="mt-5 bg-orange-500 px-6 py-2.5 text-sm font-medium text-white transition hover:bg-orange-600"
            >
              保存修改
            </button>
          </form>
        </div>
        
        <div class="mt-6 border border-gray-200 bg-white p-5 sm:p-6">
          <div class="mb-5 border-b border-gray-100 pb-4">
            <p class="text-sm font-medium text-orange-600">配送信息</p>
            <h4 class="mt-1 font-bold text-gray-900">收货地址</h4>
          </div>
          <div class="space-y-4">
            <div 
              v-for="address in addresses" 
              :key="address.id"
              :class="[
                'border p-4',
                address.isDefault === 1 ? 'border-orange-500 bg-orange-50' : 'border-gray-200'
              ]"
            >
              <div class="flex flex-col justify-between gap-3 sm:flex-row sm:items-start">
                <div>
                  <p class="font-medium">{{ address.receiverName }} {{ address.receiverPhone }}</p>
                  <p class="text-gray-500 text-sm mt-1">{{ address.province }}{{ address.city }}{{ address.district }}{{ address.detailAddress }}</p>
                </div>
                <div class="flex shrink-0 gap-3">
                  <button 
                    v-if="address.isDefault !== 1" 
                    @click="setDefault(address.id)" 
                    class="text-sm text-orange-500 hover:text-orange-600"
                  >
                    设置默认
                  </button>
                  <button 
                    @click="editAddress(address)" 
                    class="text-sm text-blue-500 hover:text-blue-600"
                  >
                    编辑
                  </button>
                  <button 
                    @click="deleteAddress(address.id)" 
                    class="text-sm text-red-500 hover:text-red-600"
                  >
                    删除
                  </button>
                </div>
              </div>
            </div>
            
            <button 
              @click="showAddAddress = true" 
              class="w-full border-2 border-dashed border-gray-300 p-4 text-sm text-gray-500 transition hover:border-orange-500 hover:text-orange-500"
            >
              + 添加收货地址
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <el-dialog v-model="showAddAddress" title="添加收货地址" width="min(92vw, 640px)">
      <form @submit.prevent="addAddress">
        <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <div>
            <label class="block text-gray-700 text-sm mb-2">收货人</label>
            <input 
              v-model="addressForm.receiverName" 
              type="text" 
              class="w-full px-4 py-2 border border-gray-300 rounded"
            />
          </div>
          <div>
            <label class="block text-gray-700 text-sm mb-2">手机号</label>
            <input 
              v-model="addressForm.receiverPhone" 
              type="tel" 
              class="w-full px-4 py-2 border border-gray-300 rounded"
            />
          </div>
          <div>
            <label class="block text-gray-700 text-sm mb-2">省</label>
            <input 
              v-model="addressForm.province" 
              type="text" 
              class="w-full px-4 py-2 border border-gray-300 rounded"
            />
          </div>
          <div>
            <label class="block text-gray-700 text-sm mb-2">市</label>
            <input 
              v-model="addressForm.city" 
              type="text" 
              class="w-full px-4 py-2 border border-gray-300 rounded"
            />
          </div>
          <div>
            <label class="block text-gray-700 text-sm mb-2">区/县</label>
            <input 
              v-model="addressForm.district" 
              type="text" 
              class="w-full px-4 py-2 border border-gray-300 rounded"
            />
          </div>
          <div>
            <label class="block text-gray-700 text-sm mb-2">详细地址</label>
            <input 
              v-model="addressForm.detailAddress" 
              type="text" 
              class="w-full px-4 py-2 border border-gray-300 rounded"
            />
          </div>
        </div>
        <div class="mt-4">
          <label class="flex items-center">
            <input type="checkbox" v-model="addressForm.isDefault" />
            <span class="ml-2 text-gray-600">设为默认地址</span>
          </label>
        </div>
        <div class="mt-5 flex justify-end gap-2">
          <button type="button" @click="showAddAddress = false" class="border border-gray-300 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50">取消</button>
          <button type="submit" class="bg-orange-500 px-4 py-2 text-sm font-medium text-white hover:bg-orange-600">保存</button>
        </div>
      </form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { userAPI } from '../api'
import { ElMessage } from 'element-plus'

const userInfo = ref(null)
const addresses = ref([])
const showAddAddress = ref(false)

const updateForm = reactive({
  nickname: '',
  email: '',
  gender: 0
})

const addressForm = reactive({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: false
})

const loadUserInfo = () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  userAPI.getUserInfo(userId).then(res => {
    if (res.code === 200 && res.data) {
      userInfo.value = res.data
      updateForm.nickname = res.data.nickname || ''
      updateForm.email = res.data.email || ''
      updateForm.gender = res.data.gender ?? 0
    }
  }).catch(err => {
    ElMessage.error(err.response?.data?.message || err.message || '加载用户信息失败')
  })
}

const handleUpdate = () => {
  const userId = localStorage.getItem('userId')
  userAPI.updateUserInfo({
    id: userId,
    nickname: updateForm.nickname,
    email: updateForm.email,
    gender: updateForm.gender
  }).then(res => {
    if (res.code === 200) {
      ElMessage.success('修改成功')
      if (updateForm.nickname) {
        localStorage.setItem('nickname', updateForm.nickname)
      }
      loadUserInfo()
    } else {
      ElMessage.error(res.message || '修改失败')
    }
  }).catch(err => {
    ElMessage.error(err.response?.data?.message || err.message || '修改失败')
  })
}

const addAddress = () => {
  alert('地址添加功能开发中...')
  showAddAddress.value = false
}

const setDefault = (addressId) => {
  alert('设置默认地址功能开发中...')
}

const editAddress = (address) => {
  alert('编辑地址功能开发中...')
}

const deleteAddress = (addressId) => {
  if (confirm('确定删除该地址？')) {
    alert('删除地址功能开发中...')
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>
