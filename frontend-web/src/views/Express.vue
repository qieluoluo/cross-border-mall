<template>
  <div class="bg-white p-6 max-w-2xl mx-auto">
    <h2 class="text-xl font-bold mb-6">物流查询</h2>
    
    <div class="border rounded-lg p-4 mb-6">
      <div class="flex space-x-4">
        <input 
          v-model="trackingNo" 
          type="text" 
          placeholder="请输入运单号"
          class="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
        />
        <button 
          @click="searchExpress" 
          class="px-6 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition"
        >
          查询
        </button>
      </div>
    </div>

    <div v-if="expressInfo" class="border rounded-lg p-4">
      <div class="flex justify-between items-center mb-4">
        <div>
          <p class="font-bold">运单号：{{ expressInfo.trackingNo }}</p>
          <p class="text-gray-500 text-sm">{{ expressInfo.company }}</p>
        </div>
        <span :class="getStatusClass(expressInfo.status)">{{ getStatusText(expressInfo.status) }}</span>
      </div>
      
      <div class="relative pl-6">
        <div class="absolute left-2 top-0 bottom-0 w-0.5 bg-gray-200"></div>
        
        <div class="space-y-4">
          <div 
            v-for="(item, index) in expressInfo.trackingList" 
            :key="index"
            class="relative"
          >
            <div 
              :class="[
                'absolute -left-4 w-4 h-4 rounded-full border-2 flex items-center justify-center',
                index === 0 ? 'border-orange-500 bg-orange-500' : 'border-gray-300 bg-white'
              ]"
            >
              <div v-if="index === 0" class="w-2 h-2 bg-white rounded-full"></div>
            </div>
            <div class="ml-4">
              <p>{{ item.description }}</p>
              <p class="text-gray-400 text-sm">{{ item.time }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="searched" class="text-center py-12">
      <p class="text-gray-400">未查询到物流信息</p>
    </div>

    <div v-else class="text-center py-12">
      <svg class="w-16 h-16 mx-auto text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 8h14M5 8a2 2 0 110-4h14a2 2 0 110 4M5 8v10a2 2 0 002 2h10a2 2 0 002-2V8m-9 4h4"></path>
      </svg>
      <p class="text-gray-400 mt-4">请输入运单号查询物流信息</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const trackingNo = ref('')
const expressInfo = ref(null)
const searched = ref(false)

const mockExpressData = {
  trackingNo: 'SF1234567890',
  company: '顺丰速运',
  status: 3,
  trackingList: [
    { description: '已签收，签收人：本人', time: '2024-01-15 14:30:00' },
    { description: '快件已到达【北京朝阳网点】', time: '2024-01-15 10:20:00' },
    { description: '快件正在派送中，派送员：张师傅 138xxxx8888', time: '2024-01-15 08:15:00' },
    { description: '快件已从【北京分拨中心】发出', time: '2024-01-14 22:30:00' },
    { description: '快件已到达【北京分拨中心】', time: '2024-01-14 18:45:00' },
    { description: '快件已从【上海分拨中心】发出', time: '2024-01-14 06:00:00' },
    { description: '快件已到达【上海分拨中心】', time: '2024-01-13 23:30:00' },
    { description: '快件已揽收', time: '2024-01-13 18:00:00' }
  ]
}

const getStatusText = (status) => {
  const map = {
    0: '待揽收',
    1: '运输中',
    2: '派送中',
    3: '已签收'
  }
  return map[status] || '未知'
}

const getStatusClass = (status) => {
  const map = {
    0: 'text-gray-500',
    1: 'text-blue-500',
    2: 'text-orange-500',
    3: 'text-green-500'
  }
  return map[status] || 'text-gray-500'
}

const searchExpress = () => {
  if (!trackingNo.value.trim()) {
    alert('请输入运单号')
    return
  }
  
  searched.value = true
  
  expressInfo.value = {
    ...mockExpressData,
    trackingNo: trackingNo.value
  }
}
</script>