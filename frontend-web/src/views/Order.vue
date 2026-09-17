<template>
  <div class="bg-white p-5 sm:p-8">
    <div class="mb-6 border-b border-gray-200 pb-5">
      <p class="text-sm font-medium text-orange-600">交易中心</p>
      <h1 class="mt-1 text-2xl font-bold text-gray-900">我的订单</h1>

    <div class="mt-5 flex gap-2 overflow-x-auto pb-1">
      <button
          v-for="tab in tabs"
          :key="tab.value"
          :class="[
          'shrink-0 border px-4 py-2 text-sm transition',
          activeTab === tab.value ? 'border-orange-500 bg-orange-500 font-medium text-white' : 'border-gray-200 bg-white text-gray-600 hover:border-orange-300 hover:text-orange-600'
        ]"
          @click="activeTab = tab.value"
      >
        {{ tab.label }}
      </button>
    </div>
    </div>

    <div v-if="loading" class="space-y-4 py-2">
      <div v-for="n in 3" :key="n" class="border border-gray-200 p-4">
        <div class="mb-4 h-4 w-48 bg-gray-100"></div>
        <div class="flex items-center gap-4">
          <div class="h-16 w-16 bg-gray-100"></div>
          <div class="flex-1 space-y-2">
            <div class="h-4 w-2/3 bg-gray-100"></div>
            <div class="h-3 w-1/3 bg-gray-100"></div>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="orderList.length === 0" class="py-20 text-center">
      <p class="text-gray-500">暂无订单</p>
      <router-link to="/products" class="mt-5 inline-block text-sm font-medium text-orange-600 hover:text-orange-700">去选购商品</router-link>
    </div>

    <div v-else class="space-y-5">
      <article v-for="order in orderList" :key="order.id" class="border border-gray-200">
        <div class="flex flex-col gap-2 border-b border-gray-100 bg-gray-50 px-4 py-3 text-sm sm:flex-row sm:items-center sm:justify-between">
          <span class="break-all text-gray-500">订单号：{{ order.orderNo }}</span>
          <span :class="[getStatusClass(order.status), 'font-medium']">{{ getStatusText(order.status) }}</span>
        </div>

        <div class="divide-y divide-gray-100 px-4">
          <div v-if="!order.items || order.items.length === 0" class="px-4 py-6 text-sm text-gray-400">暂无商品明细</div>
          <div v-for="item in (order.items || [])" :key="item.id || item.productId" class="flex items-center gap-3 py-4 sm:gap-4">
            <img
                :src="getProductImage(item)"
                @error="applyImageFallback"
                :alt="item.productName"
                class="h-16 w-16 shrink-0 object-cover"
            />
            <div class="min-w-0 flex-1">
              <p class="truncate font-medium text-gray-900">{{ item.productName }}</p>
              <p class="mt-1 truncate text-sm text-gray-500">{{ item.specs }}</p>
            </div>
            <div class="text-right">
              <p class="font-medium text-orange-600">¥{{ item.price }}</p>
              <p class="mt-1 text-sm text-gray-400">x{{ item.quantity }}</p>
            </div>
          </div>
        </div>

        <div class="flex flex-col gap-4 border-t border-gray-200 px-4 py-4 sm:flex-row sm:items-center sm:justify-between">
          <p class="text-sm text-gray-600">实付金额 <span class="ml-1 text-lg font-bold text-orange-600">¥{{ order.payAmount }}</span></p>
          <div class="flex flex-wrap gap-2">
            <button
                v-if="order.status === 0"
                @click="cancelOrder(order.id)"
                class="border border-gray-300 px-4 py-2 text-sm text-gray-700 transition hover:bg-gray-50"
            >
              取消订单
            </button>
            <button
                v-if="order.status === 0"
                @click="payOrder(order.id)"
                class="bg-orange-500 px-4 py-2 text-sm font-medium text-white transition hover:bg-orange-600"
            >
              去支付
            </button>
            <button
                v-if="order.status === 2"
                @click="confirmOrder(order.id)"
                class="bg-orange-500 px-4 py-2 text-sm font-medium text-white transition hover:bg-orange-600"
            >
              确认收货
            </button>
            <button
                @click="goToDetail(order.id)"
                class="border border-gray-300 px-4 py-2 text-sm text-gray-700 transition hover:bg-gray-50"
            >
              查看详情
            </button>
          </div>
        </div>
      </article>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { orderAPI } from '../api'
import { useRouter } from 'vue-router'
import { applyImageFallback, getProductImage } from '../utils/image'

const router = useRouter()
const orderList = ref([])
const activeTab = ref('')
const loading = ref(true)

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
  loading.value = true
  orderAPI.getOrderList(activeTab.value).then(res => {
    orderList.value = res.data || []
  }).catch(err => {
    ElMessage.error(err.message || '订单加载失败')
    orderList.value = []
  }).finally(() => {
    loading.value = false
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
