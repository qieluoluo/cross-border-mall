<template>
  <div class="bg-white p-6 max-w-2xl mx-auto">
    <h2 class="text-xl font-bold mb-6">选择支付方式</h2>

    <div v-if="order" class="border rounded-lg p-4 mb-6">
      <div class="flex justify-between items-center mb-2">
        <span class="text-gray-600">订单号</span>
        <span>{{ order.orderNo }}</span>
      </div>
      <div class="flex justify-between items-center">
        <span class="text-gray-600">支付金额</span>
        <span class="text-orange-500 text-xl font-bold">¥{{ order.payAmount }}</span>
      </div>
    </div>

    <div class="space-y-4">
      <div
          v-for="method in paymentMethods"
          :key="method.id"
          :class="[
          'border rounded-lg p-4 cursor-pointer transition',
          selectedMethod === method.id ? 'border-orange-500 bg-orange-50' : 'border-gray-200 hover:border-gray-300'
        ]"
          @click="selectedMethod = method.id"
      >
        <div class="flex items-center justify-between">
          <div class="flex items-center">
            <div :class="['w-10 h-10 rounded-full flex items-center justify-center text-lg mr-4', method.bgClass]">
              {{ method.icon }}
            </div>
            <div>
              <p class="font-medium">{{ method.name }}</p>
              <p class="text-gray-500 text-sm">{{ method.description }}</p>
            </div>
          </div>
          <div :class="['w-5 h-5 rounded-full border-2 flex items-center justify-center', selectedMethod === method.id ? 'border-orange-500' : 'border-gray-300']">
            <div v-if="selectedMethod === method.id" class="w-3 h-3 rounded-full bg-orange-500"></div>
          </div>
        </div>
      </div>
    </div>

    <div class="mt-8">
      <button
          @click="submitPayment"
          :disabled="isSubmitting"
          class="w-full py-3 bg-orange-500 text-white rounded-lg hover:bg-orange-600 disabled:bg-gray-400 disabled:cursor-not-allowed transition"
      >
        {{ isSubmitting ? '处理中...' : `立即支付 ¥${order?.payAmount || 0}` }}
      </button>
    </div>

    <div class="mt-4 text-center">
      <button @click="goBack" class="text-gray-500 hover:text-gray-700">
        返回订单列表
      </button>
    </div>

    <div v-if="showSuccess" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-8 text-center max-w-sm mx-4">
        <div class="w-16 h-16 rounded-full bg-green-100 flex items-center justify-center mx-auto mb-4">
          <span class="text-green-500 text-3xl">✓</span>
        </div>
        <h3 class="text-xl font-bold mb-2">支付成功</h3>
        <p class="text-gray-500 mb-6">您的订单已成功支付</p>
        <button @click="goToOrder" class="px-8 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600">
          查看订单
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { paymentAPI, orderAPI } from '../api'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const selectedMethod = ref('alipay')
const isSubmitting = ref(false)
const showSuccess = ref(false)

const paymentMethods = [
  {
    id: 'alipay',
    name: '支付宝',
    description: '推荐使用支付宝APP扫码支付',
    icon: '支',
    bgClass: 'bg-blue-100 text-blue-600'
  },
  {
    id: 'wechat',
    name: '微信支付',
    description: '使用微信APP扫码支付',
    icon: '微',
    bgClass: 'bg-green-100 text-green-600'
  },
  {
    id: 'stripe',
    name: 'Stripe',
    description: '支持Visa/MasterCard信用卡',
    icon: 'S',
    bgClass: 'bg-purple-100 text-purple-600'
  },
  {
    id: 'paypal',
    name: 'PayPal',
    description: '国际支付平台',
    icon: 'P',
    bgClass: 'bg-blue-600 text-white'
  }
]

const loadOrder = () => {
  const orderId = route.query.orderId
  if (orderId) {
    orderAPI.getOrderById(orderId).then(res => {
      if (res.code === 200) {
        order.value = res.data
      }
    }).catch(err => {
      console.error('Payment.vue - loadOrder - error:', err)
    })
  }
}

const submitPayment = () => {
  if (!order.value) return

  isSubmitting.value = true

  switch (selectedMethod.value) {
    case 'alipay':
      handleAlipayPayment()
      break
    case 'wechat':
      handleWechatPayment()
      break
    case 'stripe':
      handleStripePayment()
      break
    case 'paypal':
      handlePayPalPayment()
      break
  }
}

const handleAlipayPayment = async () => {
  try {
    const res = await paymentAPI.createAlipayPayment(order.value.id, order.value.payAmount)
    if (res.code === 200) {
      isSubmitting.value = false
      await showMockPaymentSuccess()
    } else {
      isSubmitting.value = false
      alert('支付失败，请重试')
    }
  } catch (err) {
    isSubmitting.value = false
    alert('支付失败，请重试')
  }
}

const handleWechatPayment = async () => {
  isSubmitting.value = false
  alert('微信支付功能开发中，将模拟支付成功')
  await showMockPaymentSuccess()
}

const handleStripePayment = async () => {
  try {
    const res = await paymentAPI.createStripePayment(order.value.id, order.value.payAmount)
    if (res.code === 200) {
      isSubmitting.value = false
      await showMockPaymentSuccess()
    } else {
      isSubmitting.value = false
      alert('支付失败，请重试')
    }
  } catch (err) {
    isSubmitting.value = false
    alert('支付失败，请重试')
  }
}

const handlePayPalPayment = async () => {
  try {
    const res = await paymentAPI.createPayPalPayment(order.value.id, order.value.payAmount)
    if (res.code === 200) {
      isSubmitting.value = false
      await showMockPaymentSuccess()
    } else {
      isSubmitting.value = false
      alert('支付失败，请重试')
    }
  } catch (err) {
    isSubmitting.value = false
    alert('支付失败，请重试')
  }
}

const showMockPaymentSuccess = async () => {
  console.log('Payment.vue - showMockPaymentSuccess - order:', order.value)
  // 支付成功后更新订单状态为待发货
  if (order.value && order.value.id) {
    try {
      console.log('Payment.vue - showMockPaymentSuccess - updating order status, orderId:', order.value.id)
      const res = await orderAPI.updateOrder({ id: order.value.id, status: 1 })
      console.log('Payment.vue - showMockPaymentSuccess - updateOrder result:', res)
      if (res.code === 200) {
        console.log('订单状态已更新为待发货')
      }
    } catch (err) {
      console.warn('更新订单状态失败:', err)
    }
  }
  showSuccess.value = true
}

const goToOrder = () => {
  console.log('Payment.vue - goToOrder - navigating to /order')
  router.push('/order')
}

const goBack = () => {
  router.push('/order')
}

onMounted(() => {
  loadOrder()
})
</script>