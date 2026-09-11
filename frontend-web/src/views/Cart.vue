<template>
  <div class="bg-white p-6">
    <h2 class="text-xl font-bold mb-6">购物车</h2>

    <div v-if="cartList.length === 0" class="text-center py-12">
      <svg class="w-16 h-16 mx-auto text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"></path>
      </svg>
      <p class="text-gray-400 mt-4">购物车是空的</p>
      <router-link to="/products" class="text-orange-500 mt-4 inline-block">去购物</router-link>
    </div>

    <div v-else v-loading="loading">
      <table class="w-full">
        <thead>
        <tr class="border-b">
          <th class="text-left py-3">商品</th>
          <th class="text-left py-3">单价</th>
          <th class="text-left py-3">数量</th>
          <th class="text-left py-3">小计</th>
          <th class="text-left py-3">操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="item in cartList" :key="item.id" class="border-b">
          <td class="py-4">
            <div class="flex items-center">
              <img
                  :src="getProductImage(item)"
                  :alt="item.productName"
                  class="w-20 h-20 object-cover rounded"
              />
              <span class="ml-4">{{ item.productName }}</span>
            </div>
          </td>
          <td class="py-4">¥{{ item.price }}</td>
          <td class="py-4">
            <div class="flex items-center border border-gray-300 w-24">
              <button
                  @click="updateQuantity(item.id, item.quantity - 1)"
                  :disabled="item.quantity <= 1"
                  :class="[
                    'w-8 h-8 flex items-center justify-center transition',
                    item.quantity <= 1 ? 'bg-gray-200 text-gray-400 cursor-not-allowed' : 'hover:bg-gray-100'
                  ]"
              >-</button>
              <span class="w-8 text-center">{{ item.quantity }}</span>
              <button
                  @click="updateQuantity(item.id, item.quantity + 1)"
                  class="w-8 h-8 flex items-center justify-center hover:bg-gray-100"
              >+</button>
            </div>
          </td>
          <td class="py-4 text-orange-500 font-bold">¥{{ item.totalPrice || (item.price * item.quantity).toFixed(2) }}</td>
          <td class="py-4">
            <button @click="deleteCartItem(item.id)" class="text-red-500 hover:text-red-700">删除</button>
          </td>
        </tr>
        </tbody>
      </table>

      <div class="flex justify-end items-center mt-6">
        <div class="text-right">
          <span class="text-gray-600">合计：</span>
          <span class="text-orange-500 text-2xl font-bold">¥{{ totalAmount.toFixed(2) }}</span>
        </div>
        <button
            @click="checkout"
            :disabled="isSubmitting"
            class="ml-6 bg-orange-500 text-white px-8 py-3 rounded-lg hover:bg-orange-600 disabled:bg-gray-400 disabled:cursor-not-allowed transition font-medium"
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
import { cartAPI, orderAPI } from '../api'
import { useRouter } from 'vue-router'
import { getProductImage } from '../utils/image'

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
const checkout = () => {
 if (cartList.value.length === 0) {
 alert('购物车是空的');
 return;
 }
 isSubmitting.value = true;
 const orderItems = cartList.value.map(item => ({
 productId: item.productId,
 productName: item.productName,
 price: item.price,
 quantity: item.quantity,
 skuId: item.skuId || 1,
 specs: item.specs || '默认规格'
 }));
 const order = {
 items: orderItems,
 totalAmount: totalAmount.value,
 payAmount: totalAmount.value,
 freightAmount: 0,
 discountAmount: 0,
 receiverName: '张三',
 receiverPhone: '13800138000',
 receiverAddress: '北京市朝阳区测试地址123号'
 };
 orderAPI.createOrder(order).then(res => {
    if (res.code === 200) {
      cartAPI.clearCart().then(() => {
        router.push(`/payment?orderId=${res.data.id}`);
      });
    }
    else {
      alert(res.message || '创建订单失败');
    }
  }).catch(err => {
    console.error('创建订单失败:', err);
  }).finally(() => {
    isSubmitting.value = false;
  });
};
onMounted(() => {
 loadCart();
});
</script>
