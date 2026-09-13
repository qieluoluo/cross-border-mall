<template>
  <div class="mx-auto max-w-3xl bg-white p-5 sm:p-8">
    <div class="mb-7 border-b border-gray-200 pb-5">
      <p class="text-sm font-medium text-orange-600">安全结算</p>
      <h1 class="mt-1 text-2xl font-bold text-gray-900">选择支付方式</h1>
    </div>

    <div v-if="order" class="mb-6 border border-gray-200 bg-gray-50 p-5">
      <div class="flex flex-col gap-1 sm:flex-row sm:items-center sm:justify-between">
        <span class="text-gray-600">订单号</span>
        <span class="break-all text-sm font-medium text-gray-900">{{ order.orderNo }}</span>
      </div>
      <div class="mt-4 flex items-center justify-between border-t border-gray-200 pt-4">
        <span class="text-gray-600">支付金额</span>
        <span class="text-2xl font-bold text-orange-600">¥{{ order.payAmount }}</span>
      </div>
    </div>

    <div class="space-y-3">
      <div
          v-for="method in paymentMethods"
          :key="method.id"
          :class="[
          'cursor-pointer border p-4 transition',
          selectedMethod === method.id ? 'border-orange-500 bg-orange-50' : 'border-gray-200 hover:border-orange-200'
        ]"
          @click="selectedMethod = method.id"
      >
        <div class="flex items-center justify-between gap-4">
          <div class="flex items-center">
            <div :class="['mr-4 flex h-10 w-10 shrink-0 items-center justify-center rounded-full text-lg', method.bgClass]">
              {{ method.icon }}
            </div>
            <div>
              <p class="font-medium text-gray-900">{{ method.name }}</p>
              <p class="mt-1 text-sm text-gray-500">{{ method.description }}</p>
            </div>
          </div>
          <div :class="['flex h-5 w-5 shrink-0 items-center justify-center rounded-full border-2', selectedMethod === method.id ? 'border-orange-500' : 'border-gray-300']">
            <div v-if="selectedMethod === method.id" class="w-3 h-3 rounded-full bg-orange-500"></div>
          </div>
        </div>
      </div>
    </div>

    <div class="mt-8">
      <button
          @click="submitPayment"
          :disabled="isSubmitting"
          class="w-full bg-orange-500 py-3 text-sm font-semibold text-white transition hover:bg-orange-600 disabled:cursor-not-allowed disabled:bg-gray-400"
      >
        {{ isSubmitting ? '处理中...' : `立即支付 ¥${order?.payAmount || 0}` }}
      </button>
    </div>

    <div class="mt-5 text-center">
      <button @click="goBack" class="text-sm text-gray-500 transition hover:text-orange-600">
        返回订单列表
      </button>
    </div>

    <div v-if="showSuccess" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="mx-4 max-w-sm bg-white p-8 text-center shadow-xl">
        <div class="w-16 h-16 rounded-full bg-green-100 flex items-center justify-center mx-auto mb-4">
          <span class="text-green-500 text-3xl">✓</span>
        </div>
        <h3 class="text-xl font-bold mb-2">支付成功</h3>
        <p class="text-gray-500 mb-6">您的订单已成功支付</p>
        <button @click="goToOrder" class="bg-orange-500 px-8 py-2.5 text-sm font-medium text-white hover:bg-orange-600">
          查看订单
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
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
      order.value = res.data
    }).catch(err => {
      ElMessage.error(err.message || '订单加载失败')
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
      ElMessage.error('支付失败，请重试')
    }
  } catch (err) {
    isSubmitting.value = false
    await showMockPaymentSuccess()
  }
}

const handleWechatPayment = async () => {
  isSubmitting.value = false
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
      ElMessage.error('支付失败，请重试')
    }
  } catch (err) {
    isSubmitting.value = false
    await showMockPaymentSuccess()
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
      ElMessage.error('支付失败，请重试')
    }
  } catch (err) {
    isSubmitting.value = false
    await showMockPaymentSuccess()
  }
}

const showMockPaymentSuccess = async () => {
  if (order.value && order.value.id) {
    try {
      await orderAPI.updateOrder({ id: order.value.id, status: 1 })
    } catch (err) {
      ElMessage.warning(err.message || '订单状态更新失败，可稍后在订单页刷新')
    }
  }
  showSuccess.value = true
}

const goToOrder = () => {
  router.push('/order')
}

const goBack = () => {
  router.push('/order')
}

onMounted(() => {
  loadOrder()
})
</script>
