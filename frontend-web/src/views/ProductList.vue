<template>
  <div class="min-h-[560px] bg-white">
    <div class="flex flex-col lg:flex-row">
      <aside class="shrink-0 border-b border-gray-200 bg-gray-50 p-5 lg:w-60 lg:border-b-0 lg:border-r">
        <div class="mb-4 flex items-center justify-between lg:block">
          <div>
            <p class="text-xs font-medium text-orange-600">商品导航</p>
            <h3 class="mt-1 text-lg font-bold text-gray-900">商品分类</h3>
          </div>
        </div>
        <ul class="flex gap-2 overflow-x-auto pb-1 lg:block lg:space-y-1 lg:overflow-visible">
          <li
              v-for="category in categoryList"
              :key="category.id"
              :class="[
              'shrink-0 cursor-pointer px-3 py-2 text-sm transition lg:block',
              selectedCategory === category.id ? 'bg-orange-500 font-medium text-white' : 'text-gray-600 hover:bg-white hover:text-orange-600'
            ]"
              @click="selectCategory(category.id)"
          >
            {{ category.name }}
          </li>
        </ul>
      </aside>

      <main class="min-w-0 flex-1 p-5 sm:p-7">
        <div class="mb-7 border-b border-gray-200 pb-5">
          <div class="flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
            <div>
              <p class="text-xs font-medium text-orange-600">商品库</p>
              <h1 class="mt-1 text-2xl font-bold text-gray-900">{{ searchKeyword ? `“${searchKeyword}” 的搜索结果` : '全部商品' }}</h1>
            </div>
            <p class="text-sm text-gray-500">共 <span class="font-semibold text-gray-900">{{ total }}</span> 件商品</p>
          </div>
          <div class="mt-5 flex flex-wrap items-center gap-2">
            <span class="mr-1 text-sm text-gray-500">排序</span>
            <button
                v-for="sort in sortOptions"
                :key="sort.value"
                :class="[
                'border px-3 py-1.5 text-sm transition',
                currentSort === sort.value ? 'border-orange-500 bg-orange-500 font-medium text-white' : 'border-gray-200 bg-white text-gray-600 hover:border-orange-300 hover:text-orange-600'
              ]"
                @click="handleSortChange(sort.value)"
            >
              {{ sort.label }}
            </button>
          </div>
        </div>

        <div v-if="loading" class="grid grid-cols-1 gap-5 sm:grid-cols-2 xl:grid-cols-3">
          <div v-for="n in 6" :key="n" class="overflow-hidden border border-gray-200 bg-white">
            <div class="h-52 bg-gray-100"></div>
            <div class="space-y-3 p-4">
              <div class="h-4 w-3/4 bg-gray-100"></div>
              <div class="h-3 w-1/2 bg-gray-100"></div>
              <div class="h-6 w-24 bg-gray-100"></div>
            </div>
          </div>
        </div>

        <div v-else class="grid grid-cols-1 gap-5 sm:grid-cols-2 xl:grid-cols-3">
          <div
              v-for="product in products"
              :key="product.id"
              class="group cursor-pointer overflow-hidden border border-gray-200 bg-white transition duration-200 hover:-translate-y-1 hover:border-orange-200 hover:shadow-lg"
              @click="goToDetail(product.id)"
          >
            <div class="h-52 overflow-hidden bg-gray-100">
              <img
                  :src="getProductImage(product)"
                  @error="applyImageFallback"
                  :alt="product.name"
                  class="h-full w-full object-cover transition duration-300 group-hover:scale-105"
              />
            </div>
            <div class="p-4">
              <h3 class="truncate text-base font-semibold text-gray-900">{{ product.name }}</h3>
              <p class="mt-1 h-5 truncate text-sm text-gray-500">{{ product.subTitle || '优选好物，品质保障' }}</p>
              <div class="mt-4 flex items-end justify-between gap-3">
                <div>
                  <p class="text-xl font-bold text-orange-600">¥{{ product.price }}</p>
                  <p class="mt-1 text-xs text-gray-400">库存 {{ product.stock || 0 }}</p>
                </div>
                <button @click.stop="addToCart(product)" class="shrink-0 bg-orange-500 px-3 py-2 text-sm font-medium text-white transition hover:bg-orange-600 focus:outline-none focus:ring-2 focus:ring-orange-300">
                  加入购物车
                </button>
              </div>
            </div>
          </div>
        </div>

        <div class="mt-10 flex justify-center border-t border-gray-100 pt-6">
          <el-pagination
              :current-page="pageNum"
              :page-size="pageSize"
              :total="total"
              @current-change="handlePageChange"
          />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { productAPI, cartAPI } from '../api'
import { useRouter, useRoute } from 'vue-router'
import { applyImageFallback, getProductImage } from '../utils/image'

const router = useRouter()
const route = useRoute()
const products = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(9)
const selectedCategory = ref(0)
const currentSort = ref('default')
const searchKeyword = ref('')
const loading = ref(true)

const categoryList = ref([
  { id: 0, name: '全部商品' }
])

const sortOptions = [
  { label: '默认', value: 'default' },
  { label: '价格升序', value: 'price_asc' },
  { label: '价格降序', value: 'price_desc' },
  { label: '销量优先', value: 'sales' }
]

const syncQuery = () => {
  const keyword = route.query.keyword
  searchKeyword.value = keyword ? decodeURIComponent(String(keyword)) : ''
  const category = Number(route.query.category)
  selectedCategory.value = Number.isNaN(category) ? 0 : category
}

const loadCategories = async () => {
  try {
    const res = await productAPI.getCategories()
    const all = res.data || []
    const topLevel = all
      .filter((item) => Number(item.parentId ?? item.parent_id ?? 0) === 0)
      .sort((a, b) => Number(a.sortOrder || 0) - Number(b.sortOrder || 0))
    categoryList.value = [{ id: 0, name: '全部商品' }, ...topLevel]
  } catch {
    categoryList.value = [
      { id: 0, name: '全部商品' },
      { id: 1, name: '手机数码' },
      { id: 2, name: '家用电器' },
      { id: 3, name: '服装鞋帽' },
      { id: 4, name: '美妆个护' },
      { id: 5, name: '图书文娱' }
    ]
  }
}

const loadProducts = () => {
  loading.value = true
  const request = searchKeyword.value
    ? productAPI.searchProducts(searchKeyword.value, pageNum.value, pageSize.value)
    : productAPI.getProductList(pageNum.value, pageSize.value, selectedCategory.value, currentSort.value)

  request.then((res) => {
    products.value = res.data?.records || []
    total.value = res.data?.total || products.value.length
  }).catch((err) => {
    ElMessage.error(err.message || '商品加载失败')
    products.value = []
    total.value = 0
  }).finally(() => {
    loading.value = false
  })
}

onMounted(async () => {
  syncQuery()
  await loadCategories()
  loadProducts()
})

watch(() => [route.query.keyword, route.query.category], () => {
  pageNum.value = 1
  syncQuery()
  loadProducts()
})

const selectCategory = (id) => {
  selectedCategory.value = id
  pageNum.value = 1
  const query = { ...route.query }
  if (id) {
    query.category = String(id)
  } else {
    delete query.category
  }
  router.replace({ path: '/products', query })
}

const handleSortChange = (sortValue) => {
  currentSort.value = sortValue
  pageNum.value = 1
  loadProducts()
}

const handlePageChange = (page) => {
  pageNum.value = page
  loadProducts()
}

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

const addToCart = (product) => {
  if (!localStorage.getItem('token')) {
    router.push('/login')
    return
  }
  cartAPI.addToCart(product.id, 1).then(() => {
    ElMessage.success('已加入购物车')
  }).catch(err => {
    ElMessage.error(err.message || '加入购物车失败')
  })
}
</script>
