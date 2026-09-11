<template>
  <div class="bg-white p-6">
    <h2 class="text-xl font-bold mb-6">个人中心</h2>
    
    <div v-if="userInfo" class="grid grid-cols-3 gap-6">
      <div class="col-span-1">
        <div class="bg-gray-50 rounded-lg p-4">
          <div class="flex flex-col items-center">
            <div class="w-20 h-20 bg-orange-100 rounded-full flex items-center justify-center mb-4">
              <svg class="w-10 h-10 text-orange-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
              </svg>
            </div>
            <h3 class="text-lg font-bold">{{ userInfo.nickname || userInfo.username }}</h3>
            <p class="text-gray-500 text-sm mt-1">{{ userInfo.phone }}</p>
          </div>
        </div>
        
        <div class="mt-4 bg-gray-50 rounded-lg p-4">
          <h4 class="font-bold mb-2">账户信息</h4>
          <div class="space-y-2 text-sm">
            <p><span class="text-gray-500">用户名：</span>{{ userInfo.username }}</p>
            <p><span class="text-gray-500">邮箱：</span>{{ userInfo.email || '未绑定' }}</p>
            <p><span class="text-gray-500">性别：</span>{{ userInfo.gender === 1 ? '男' : userInfo.gender === 2 ? '女' : '未知' }}</p>
            <p><span class="text-gray-500">注册时间：</span>{{ userInfo.createTime }}</p>
          </div>
        </div>
      </div>
      
      <div class="col-span-2">
        <div class="bg-gray-50 rounded-lg p-4">
          <h4 class="font-bold mb-4">修改个人信息</h4>
          <form @submit.prevent="handleUpdate">
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-gray-700 text-sm mb-2">昵称</label>
                <input 
                  v-model="updateForm.nickname" 
                  type="text" 
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
                />
              </div>
              <div>
                <label class="block text-gray-700 text-sm mb-2">邮箱</label>
                <input 
                  v-model="updateForm.email" 
                  type="email" 
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
                />
              </div>
              <div>
                <label class="block text-gray-700 text-sm mb-2">性别</label>
                <select 
                  v-model.number="updateForm.gender" 
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
                >
                  <option :value="0">未知</option>
                  <option :value="1">男</option>
                  <option :value="2">女</option>
                </select>
              </div>
            </div>
            <button 
              type="submit" 
              class="mt-4 bg-orange-500 text-white px-6 py-2 rounded-lg hover:bg-orange-600 transition"
            >
              保存修改
            </button>
          </form>
        </div>
        
        <div class="mt-4 bg-gray-50 rounded-lg p-4">
          <h4 class="font-bold mb-4">收货地址</h4>
          <div class="space-y-4">
            <div 
              v-for="address in addresses" 
              :key="address.id"
              :class="[
                'p-4 border rounded-lg',
                address.isDefault === 1 ? 'border-orange-500 bg-orange-50' : 'border-gray-200'
              ]"
            >
              <div class="flex justify-between items-start">
                <div>
                  <p class="font-medium">{{ address.receiverName }} {{ address.receiverPhone }}</p>
                  <p class="text-gray-500 text-sm mt-1">{{ address.province }}{{ address.city }}{{ address.district }}{{ address.detailAddress }}</p>
                </div>
                <div class="flex space-x-2">
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
              class="w-full border-2 border-dashed border-gray-300 p-4 rounded-lg text-gray-500 hover:border-orange-500 hover:text-orange-500 transition"
            >
              + 添加收货地址
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <el-dialog title="添加收货地址" :visible.sync="showAddAddress">
      <form @submit.prevent="addAddress">
        <div class="grid grid-cols-2 gap-4">
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
        <div class="mt-4 flex justify-end space-x-2">
          <button type="button" @click="showAddAddress = false" class="px-4 py-2 border border-gray-300 rounded">取消</button>
          <button type="submit" class="px-4 py-2 bg-orange-500 text-white rounded">保存</button>
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
