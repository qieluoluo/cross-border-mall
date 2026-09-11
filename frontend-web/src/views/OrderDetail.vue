<template>
  <div class="bg-white p-6">
    <div v-if="order">
      <h2 class="text-xl font-bold mb-6">订单详情</h2>

      <div class="border rounded-lg p-4 mb-6">
        <div class="flex justify-between items-center mb-4">
          <span class="text-gray-600">订单号：{{ order.orderNo }}</span>
          <span :class="getStatusClass(order.status)">{{ getStatusText(order.status) }}</span>
        </div>
        <div class="space-y-2">
          <p><span class="text-gray-500">收货人：</span>{{ order.receiverName }} {{ order.receiverPhone }}</p>
          <p><span class="text-gray-500">收货地址：</span>{{ order.receiverAddress }}</p>
          <p><span class="text-gray-500">下单时间：</span>{{ order.createTime }}</p>
        </div>
      </div>

      <div class="border rounded-lg p-4 mb-6">
        <h3 class="font-bold mb-4">商品清单</h3>
        <div class="space-y-4">
          <div v-for="item in order.items" :key="item.id" class="flex items-center">
            <img
                :src="getProductImage(item)"
                :alt="item.productName"
                class="w-20 h-20 object-cover rounded"
            />
            <div class="ml-4 flex-1">
              <p class="text-gray-800 font-medium">{{ item.productName }}</p>
              <p class="text-gray-500 text-sm">{{ item.specs }}</p>
            </div>
            <div class="text-right">
              <p class="text-orange-500 font-bold">¥{{ item.price }}</p>
              <p class="text-gray-400 text-sm">x{{ item.quantity }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="border rounded-lg p-4">
        <h3 class="font-bold mb-4">费用明细</h3>
        <div class="space-y-2">
          <div class="flex justify-between">
            <span class="text-gray-500">商品金额</span>
            <span>¥{{ order.totalAmount }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-500">运费</span>
            <span>¥{{ order.freightAmount }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-500">优惠</span>
            <span class="text-green-500">-¥{{ order.discountAmount }}</span>
          </div>
          <div class="flex justify-between pt-2 border-t">
            <span class="text-gray-800 font-bold">实付金额</span>
            <span class="text-orange-500 text-xl font-bold">¥{{ order.payAmount }}</span>
          </div>
        </div>
      </div>

      <div class="mt-6 flex justify-end space-x-4">
        <button @click="goBack" class="px-6 py-2 border border-gray-300 rounded hover:bg-gray-50">
          返回
        </button>
        <button
            v-if="order.status === 0"
            @click="cancelOrder(order.id)"
            class="px-6 py-2 border border-gray-300 rounded hover:bg-gray-50"
        >
          取消订单
        </button>
        <button
            v-if="order.status === 0"
            @click="payOrder(order.id)"
            class="px-6 py-2 bg-orange-500 text-white rounded hover:bg-orange-600"
        >
          去支付
        </button>
        <button
            v-if="order.status === 2"
            @click="confirmOrder(order.id)"
            class="px-6 py-2 bg-orange-500 text-white rounded hover:bg-orange-600"
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

onMounted(() => {
  loadOrder()
})
</script>
