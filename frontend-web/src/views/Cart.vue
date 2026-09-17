<template>
  <div class="bg-white p-5 sm:p-8">
    <div class="mb-7 flex flex-col gap-3 border-b border-gray-200 pb-5 sm:flex-row sm:items-end sm:justify-between">
      <div>
        <p class="text-sm font-medium text-orange-600">购物清单</p>
        <h1 class="mt-1 text-2xl font-bold text-gray-900">购物车</h1>
      </div>
      <p class="text-sm text-gray-500">已选 <span class="font-semibold text-gray-900">{{ cartList.length }}</span> 件商品</p>
    </div>

    <div v-if="cartList.length === 0" class="py-20 text-center">
      <svg class="w-16 h-16 mx-auto text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"></path>
      </svg>
      <p class="mt-4 text-gray-500">购物车还是空的</p>
      <router-link to="/products" class="mt-5 inline-block bg-orange-500 px-5 py-2.5 text-sm font-medium text-white transition hover:bg-orange-600">去选购</router-link>
    </div>

    <div v-else v-loading="loading">
      <div class="overflow-x-auto">
        <table class="min-w-[720px] w-full">
        <thead class="border-y border-gray-200 bg-gray-50 text-sm text-gray-500">
        <tr>
          <th class="px-4 py-3 text-left font-medium">商品</th>
          <th class="px-4 py-3 text-left font-medium">单价</th>
          <th class="px-4 py-3 text-left font-medium">数量</th>
          <th class="px-4 py-3 text-left font-medium">小计</th>
          <th class="px-4 py-3 text-right font-medium">操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="item in cartList" :key="item.id" class="border-b border-gray-100">
          <td class="px-4 py-4">
            <div class="flex items-center gap-4">
              <img
                  :src="getProductImage(item)"
                  @error="applyImageFallback"
                  :alt="item.productName"
                  class="h-20 w-20 object-cover"
              />
              <span class="max-w-[260px] font-medium text-gray-900">{{ item.productName }}</span>
            </div>
          </td>
          <td class="px-4 py-4 text-gray-600">¥{{ item.price }}</td>
          <td class="px-4 py-4">
            <div class="flex w-28 items-center border border-gray-300 bg-white">
              <button
                  @click="updateQuantity(item.id, item.quantity - 1)"
                  :disabled="item.quantity <= 1"
                  :class="[
                    'flex h-9 w-9 items-center justify-center text-lg transition',
                    item.quantity <= 1 ? 'cursor-not-allowed bg-gray-100 text-gray-300' : 'text-gray-600 hover:bg-gray-100'
                  ]"
              >-</button>
              <span class="w-10 text-center text-sm font-medium">{{ item.quantity }}</span>
              <button
                  @click="updateQuantity(item.id, item.quantity + 1)"
                  class="flex h-9 w-9 items-center justify-center text-lg text-gray-600 transition hover:bg-gray-100"
              >+</button>
            </div>
          </td>
          <td class="px-4 py-4 font-semibold text-orange-600">¥{{ item.totalPrice || (item.price * item.quantity).toFixed(2) }}</td>
          <td class="px-4 py-4 text-right">
            <button @click="deleteCartItem(item.id)" class="text-sm text-gray-500 transition hover:text-red-600">删除</button>
          </td>
        </tr>
        </tbody>
        </table>
      </div>

      <div class="mt-6 flex flex-col gap-4 border-t border-gray-200 pt-5 sm:flex-row sm:items-center sm:justify-end">
        <div class="text-right">
          <p class="text-sm text-gray-500">合计</p>
          <p class="mt-1 text-2xl font-bold text-orange-600">¥{{ totalAmount.toFixed(2) }}</p>
        </div>
        <button
            @click="checkout"
            :disabled="isSubmitting"
            class="bg-orange-500 px-8 py-3 text-sm font-semibold text-white transition hover:bg-orange-600 disabled:cursor-not-allowed disabled:bg-gray-400"
        >
          {{ isSubmitting ? '提交中...' : '去结算' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { addressAPI, buildReceiverFromAddress, cartAPI, orderAPI } from '../api'
import { useRouter } from 'vue-router'
import { applyImageFallback, getProductImage } from '../utils/image'

const router = useRouter()
const cartList = ref([])
const loading = ref(false)
const isSubmitting = ref(false)

const totalAmount = computed(() => {
  return cartList.value.reduce((sum, item) => {
    const price = parseFloat(item.price) || 0
    const quantity = parseInt(item.quantity) || 0
    return sum + price * quantity
  }, 0)
})

const loadCart = () => {
  if (!localStorage.getItem('token')) {
    router.push('/login')
    return
  }
  loading.value = true
  cartAPI.getCartList()
    .then(res => {
      if (res.code === 200) {
        cartList.value = res.data || []
      }
    })
    .catch(err => {
      console.error('加载购物车失败:', err)
      ElMessage.error(err.message || '加载购物车失败，请确认购物车服务已启动')
      cartList.value = []
    })
    .finally(() => {
      loading.value = false
    })
}

const updateQuantity = (cartId, quantity) => {
  if (quantity < 1) return
  cartAPI.updateCart(cartId, quantity)
    .then(res => {
      if (res.code === 200) {
        loadCart()
      }
    })
    .catch(err => {
      console.error('更新数量失败:', err)
      ElMessage.error(err.message || '更新数量失败')
    })
}

const deleteCartItem = (cartId) => {
  if (!confirm('确定删除该商品？')) return
  cartAPI.deleteCart(cartId)
    .then(res => {
      if (res.code === 200) {
        ElMessage.success('已删除')
        loadCart()
      }
    })
    .catch(err => {
      console.error('删除失败:', err)
      ElMessage.error(err.message || '删除失败')
    })
}

const checkout = async () => {
  if (cartList.value.length === 0) {
    ElMessage.warning('购物车是空的')
    return
  }

  isSubmitting.value = true
  try {
    const addressRes = await addressAPI.list()
    const addresses = addressRes.data || []
    const address = addresses.find((item) => Number(item.isDefault) === 1) || addresses[0]
    if (!address) {
      ElMessage.warning('请先在个人中心添加收货地址')
      router.push('/user')
      return
    }

    const orderItems = cartList.value.map((item) => ({
      productId: item.productId,
      productName: item.productName,
      productImage: item.productImage || item.mainImage || item.image,
      price: item.price,
      quantity: item.quantity,
      skuId: item.skuId || item.productId,
      specs: item.specs || '默认规格'
    }))

    const res = await orderAPI.createOrder({
      items: orderItems,
      totalAmount: totalAmount.value,
      payAmount: totalAmount.value,
      freightAmount: 0,
      discountAmount: 0,
      ...buildReceiverFromAddress(address)
    })

    await cartAPI.clearCart()
    router.push(`/payment?orderId=${res.data.id}`)
  } catch (err) {
    ElMessage.error(err.message || '创建订单失败')
  } finally {
    isSubmitting.value = false
  }
}
onMounted(() => {
 loadCart();
});
</script>
