<template>
  <div class="bg-white p-5 sm:p-8">
    <div v-if="order">
      <div class="mb-7 flex flex-col gap-2 border-b border-gray-200 pb-5 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <p class="text-sm font-medium text-orange-600">订单管理</p>
          <h1 class="mt-1 text-2xl font-bold text-gray-900">订单详情</h1>
        </div>
        <span :class="[getStatusClass(order.status), 'font-medium']">{{ getStatusText(order.status) }}</span>
      </div>

      <section class="mb-6 border border-gray-200">
        <div class="flex flex-col gap-1 border-b border-gray-100 bg-gray-50 px-5 py-4 sm:flex-row sm:items-center sm:justify-between">
          <span class="text-sm text-gray-500">订单号</span>
          <span class="break-all text-sm font-medium text-gray-900">{{ order.orderNo }}</span>
        </div>
        <div class="grid gap-4 px-5 py-5 text-sm sm:grid-cols-2">
          <p><span class="text-gray-500">收货人：</span><span class="text-gray-900">{{ order.receiverName }} {{ order.receiverPhone }}</span></p>
          <p><span class="text-gray-500">下单时间：</span><span class="text-gray-900">{{ order.createTime }}</span></p>
          <p class="sm:col-span-2"><span class="text-gray-500">收货地址：</span><span class="text-gray-900">{{ order.receiverAddress }}</span></p>
        </div>
      </section>

      <section class="mb-6 border border-gray-200">
        <div class="border-b border-gray-100 px-5 py-4">
          <h2 class="font-bold text-gray-900">商品清单</h2>
        </div>
        <div class="divide-y divide-gray-100 px-5">
          <div v-for="item in order.items" :key="item.id" class="flex items-center gap-3 py-4 sm:gap-4">
            <img
                :src="getProductImage(item)"
                @error="applyImageFallback"
                :alt="item.productName"
                class="h-20 w-20 shrink-0 object-cover"
            />
            <div class="min-w-0 flex-1">
              <p class="truncate font-medium text-gray-900">{{ item.productName }}</p>
              <p class="mt-1 truncate text-sm text-gray-500">{{ item.specs }}</p>
            </div>
            <div class="text-right">
              <p class="font-bold text-orange-600">¥{{ item.price }}</p>
              <p class="mt-1 text-sm text-gray-400">x{{ item.quantity }}</p>
            </div>
          </div>
        </div>
      </section>

      <section class="border border-gray-200">
        <div class="border-b border-gray-100 px-5 py-4">
          <h2 class="font-bold text-gray-900">费用明细</h2>
        </div>
        <div class="space-y-3 px-5 py-5 text-sm">
          <div class="flex justify-between gap-4">
            <span class="text-gray-500">商品金额</span>
            <span>¥{{ order.totalAmount }}</span>
          </div>
          <div class="flex justify-between gap-4">
            <span class="text-gray-500">运费</span>
            <span>¥{{ order.freightAmount }}</span>
          </div>
          <div class="flex justify-between gap-4">
            <span class="text-gray-500">优惠</span>
            <span class="text-green-500">-¥{{ order.discountAmount }}</span>
          </div>
          <div class="flex justify-between gap-4 border-t border-gray-200 pt-4">
            <span class="text-gray-800 font-bold">实付金额</span>
            <span class="text-orange-500 text-xl font-bold">¥{{ order.payAmount }}</span>
          </div>
        </div>
      </section>

      <div class="mt-6 flex flex-wrap justify-end gap-2">
        <button @click="goBack" class="border border-gray-300 px-6 py-2 text-sm text-gray-700 transition hover:bg-gray-50">
          返回
        </button>
        <button
            v-if="order.status === 0"
            @click="cancelOrder(order.id)"
            class="border border-gray-300 px-6 py-2 text-sm text-gray-700 transition hover:bg-gray-50"
        >
          取消订单
        </button>
        <button
            v-if="order.status === 0"
            @click="payOrder(order.id)"
            class="bg-orange-500 px-6 py-2 text-sm font-medium text-white transition hover:bg-orange-600"
        >
          去支付
        </button>
        <button
            v-if="order.status === 2"
            @click="confirmOrder(order.id)"
            class="bg-orange-500 px-6 py-2 text-sm font-medium text-white transition hover:bg-orange-600"
        >
          确认收货
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { orderAPI } from '../api'
import { useRoute, useRouter } from 'vue-router'
import { getProductImage } from '../utils/image'
import fallbackProductImage from '../../../miniapp/小程序项目/images/xiaomi14.png'

const route = useRoute()
const router = useRouter()
const order = ref(null)

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

const loadOrder = () => {
  const id = route.params.id
  orderAPI.getOrderById(id).then(res => {
    if (res.code === 200) {
      order.value = res.data
    }
  })
}

const cancelOrder = (orderId) => {
  if (confirm('确定取消订单？')) {
    orderAPI.cancelOrder(orderId).then(res => {
      if (res.code === 200) {
        router.push('/order')
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
        router.push('/order')
      }
    })
  }
}

const goBack = () => {
  router.push('/order')
}

const applyImageFallback = (event) => {
  event.target.onerror = null
  event.target.src = fallbackProductImage
}

onMounted(() => {
  loadOrder()
})
</script>
