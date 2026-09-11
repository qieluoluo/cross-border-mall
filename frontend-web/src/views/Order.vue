<template>
  <div class="bg-white p-6">
    <h2 class="text-xl font-bold mb-6">我的订单</h2>

    <div class="flex space-x-4 mb-6">
      <button
          v-for="tab in tabs"
          :key="tab.value"
          :class="[
          'px-4 py-2 rounded-lg transition',
          activeTab === tab.value ? 'bg-orange-500 text-white' : 'bg-gray-100 hover:bg-gray-200'
        ]"
          @click="activeTab = tab.value"
      >
        {{ tab.label }}
      </button>
    </div>

    <div v-if="orderList.length === 0" class="text-center py-12">
      <p class="text-gray-400">暂无订单</p>
    </div>

    <div v-else class="space-y-6">
      <div v-for="order in orderList" :key="order.id" class="border rounded-lg p-4">
        <div class="flex justify-between items-center mb-4">
          <span class="text-gray-600">订单号：{{ order.orderNo }}</span>
          <span :class="getStatusClass(order.status)">{{ getStatusText(order.status) }}</span>
        </div>

        <div class="space-y-2">
          <div v-for="item in order.items" :key="item.id" class="flex items-center">
            <img
                :src="getProductImage(item)"
                :alt="item.productName"
                class="w-16 h-16 object-cover rounded"
            />
            <div class="ml-4 flex-1">
              <p class="text-gray-800">{{ item.productName }}</p>
              <p class="text-gray-500 text-sm">{{ item.specs }}</p>
            </div>
            <div class="text-right">
              <p class="text-orange-500">¥{{ item.price }}</p>
              <p class="text-gray-400 text-sm">x{{ item.quantity }}</p>
            </div>
          </div>
        </div>

        <div class="flex justify-between items-center mt-4 pt-4 border-t">
          <span class="text-gray-600">实付金额：<span class="text-orange-500 font-bold">¥{{ order.payAmount }}</span></span>
          <div class="flex space-x-2">
            <button
                v-if="order.status === 0"
                @click="cancelOrder(order.id)"
                class="px-4 py-2 border border-gray-300 rounded hover:bg-gray-50"
            >
              取消订单
            </button>
            <button
                v-if="order.status === 0"
                @click="payOrder(order.id)"
                class="px-4 py-2 bg-orange-500 text-white rounded hover:bg-orange-600"
            >
              去支付
            </button>
            <button
                v-if="order.status === 2"
                @click="confirmOrder(order.id)"
                class="px-4 py-2 bg-orange-500 text-white rounded hover:bg-orange-600"
            >
              确认收货
            </button>
            <button
                @click="goToDetail(order.id)"
                class="px-4 py-2 border border-gray-300 rounded hover:bg-gray-50"
            >
              查看详情
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { orderAPI } from '../api'
import { useRouter } from 'vue-router'
import { getProductImage } from '../utils/image'

const router = useRouter()
const orderList = ref([])
const activeTab = ref('')

const tabs = [
  { label: '全部', value: '' },
  { label: '待付款', value: 0 },
  { label: '待发货', value: 1 },
  { label: '待收货', value: 2 },
  { label: '已完成', value: 3 }
]

const getStatusText = (status) => {
  const map = {
    0: '待付款',
    1: '待发货',
    2: '待收货',
    3: '已完成',
    4: '已取消',
    5: '退款中',
    6: '已退款'
  }
  return map[status] || '未知'
}

const getStatusClass = (status) => {
  const map = {
    0: 'text-orange-500',
    1: 'text-blue-500',
    2: 'text-green-500',
    3: 'text-gray-500',
    4: 'text-gray-400',
    5: 'text-yellow-500',
    6: 'text-gray-400'
  }
  return map[status] || 'text-gray-500'
}

const loadOrders = () => {
  if (!localStorage.getItem('token')) {
    router.push('/login')
    return
  }
  console.log('Order.vue - loadOrders - activeTab:', activeTab.value)
  orderAPI.getOrderList(activeTab.value).then(res => {
    console.log('Order.vue - loadOrders - getOrderList result:', res)
    if (res.code === 200) {
      orderList.value = res.data
      console.log('Order.vue - loadOrders - orderList:', orderList.value)
    }
  }).catch(err => {
    console.error('Order.vue - loadOrders - error:', err)
  })
}

const cancelOrder = (orderId) => {
  if (confirm('确定取消订单？')) {
    orderAPI.cancelOrder(orderId).then(res => {
      if (res.code === 200) {
        loadOrders()
      }
    })
  }
}

const payOrder = (orderId) => {
  router.push(`/payment?orderId=${orderId}`)
}

const confirmOrder = (orderId) => {
  if (confirm('确认已收到商品？')) {
    orderAPI.confirmOrder(orderId).then(res => {
      if (res.code === 200) {
        loadOrders()
      }
    })
  }
}

const goToDetail = (orderId) => {
  router.push(`/order/${orderId}`)
}

onMounted(() => {
  loadOrders()
})

watch(activeTab, () => {
  loadOrders()
})
</script>
