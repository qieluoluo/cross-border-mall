<template>
  <div class="dashboard">
    <el-row :gutter="24" class="stats-section">
      <el-col :span="6">
        <div class="stat-card stat-card-gradient-1">
          <div class="stat-icon-wrapper">
            <el-icon :size="40"><User /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
            <div class="stat-label">用户总数</div>
          </div>
        </div>
      </el-col>

      <el-col :span="6">
        <div class="stat-card stat-card-gradient-2">
          <div class="stat-icon-wrapper">
            <el-icon :size="40"><Goods /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.totalProducts || 0 }}</div>
            <div class="stat-label">商品总数</div>
          </div>
        </div>
      </el-col>

      <el-col :span="6">
        <div class="stat-card stat-card-gradient-3">
          <div class="stat-icon-wrapper">
            <el-icon :size="40"><Document /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.totalOrders || 0 }}</div>
            <div class="stat-label">订单总数</div>
          </div>
        </div>
      </el-col>

      <el-col :span="6">
        <div class="stat-card stat-card-gradient-4">
          <div class="stat-icon-wrapper">
            <el-icon :size="40"><ShoppingCart /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.todayOrders || 0 }}</div>
            <div class="stat-label">今日订单</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="24" class="main-section">
      <el-col :span="8">
        <el-card class="category-card">
          <template #header>
            <div class="card-header">
              <el-icon :size="20" color="#E6A23C"><PieChart /></el-icon>
              <span>商品分类统计</span>
            </div>
          </template>
          <div ref="categoryChartRef" class="chart"></div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card class="orders-card">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon :size="20" color="#409EFF"><List /></el-icon>
                <span>最新订单</span>
              </div>
              <el-button type="primary" link @click="$router.push('/order')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentOrders" stripe>
            <el-table-column prop="orderNo" label="订单号" width="180" />
            <el-table-column prop="userNickname" label="用户" width="120" />
            <el-table-column prop="totalAmount" label="金额" width="100">
              <template #default="{ row }">¥{{ row.totalAmount }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="下单时间" width="160" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { User, Goods, Document, ShoppingCart, PieChart, List } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getUserListApi } from '@/api'
import { getProductListApi } from '@/api'
import { getOrderListApi } from '@/api'

const stats = ref({
  totalUsers: 0,
  totalProducts: 0,
  totalOrders: 0,
  todayOrders: 0
})

const recentOrders = ref([])
const categoryDistribution = ref([])

const categoryChartRef = ref(null)
let categoryChart = null

const getStatusType = (status) => {
  const map = {
    0: 'info',
    1: 'warning',
    2: 'primary',
    3: 'success',
    4: 'danger',
    5: 'warning',
    6: 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    0: '待付款',
    1: '待发货',
    2: '配送中',
    3: '已完成',
    4: '已取消',
    5: '退款中',
    6: '已退款'
  }
  return map[status] || status
}

const loadStats = async () => {
  try {
    const usersRes = await getUserListApi({ page: 1, size: 1 })
    stats.value.totalUsers = usersRes.data?.total || 0
  } catch (error) {
    console.error('加载用户数据失败:', error)
    stats.value.totalUsers = 0
  }

  try {
    const productsRes = await getProductListApi({ page: 1, size: 2000 })
    const productRecords = productsRes.data?.records || []
    stats.value.totalProducts = productsRes.data?.total || 0
    const categoryCountMap = {}
    productRecords.forEach(item => {
      const categoryName = item.category || '-'
      categoryCountMap[categoryName] = (categoryCountMap[categoryName] || 0) + 1
    })
    categoryDistribution.value = Object.entries(categoryCountMap).map(([name, value]) => ({ name, value }))
  } catch (error) {
    console.error('加载商品数据失败:', error)
    stats.value.totalProducts = 0
    categoryDistribution.value = []
  }

  try {
    const ordersRes = await getOrderListApi({ page: 1, size: 2000 })
    stats.value.totalOrders = ordersRes.data?.total || 0

    const today = new Date().toDateString()
    const orders = ordersRes.data?.records || []
    stats.value.todayOrders = orders.filter(order => {
      if (!order.createTime) return false
      const orderDate = new Date(order.createTime).toDateString()
      return orderDate === today
    }).length

    recentOrders.value = orders.slice(0, 5)
  } catch (error) {
    console.error('加载订单数据失败:', error)
    stats.value.totalOrders = 0
    stats.value.todayOrders = 0
    recentOrders.value = []
  }

  updateCategoryChart()
}

let refreshTimer = null

const initCategoryChart = () => {
  categoryChart = echarts.init(categoryChartRef.value)
  updateCategoryChart()
  window.addEventListener('resize', handleResize)
}

const updateCategoryChart = () => {
  if (!categoryChart) return
  categoryChart.setOption({
    tooltip: { trigger: 'item' },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'middle'
    },
    series: [{
      type: 'pie',
      radius: ['45%', '75%'],
      center: ['60%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 8,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 16,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: categoryDistribution.value,
      animationType: 'scale',
      animationEasing: 'elasticOut'
    }]
  })
}

const handleResize = () => {
  categoryChart?.resize()
}

onMounted(() => {
  loadStats()
  initCategoryChart()
  refreshTimer = setInterval(loadStats, 30000)
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
  window.removeEventListener('resize', handleResize)
  categoryChart?.dispose()
})
</script>

<style scoped lang="scss">
.dashboard {
  padding: 10px;

  .stats-section {
    margin-bottom: 24px;
  }

  .stat-card {
    display: flex;
    align-items: center;
    padding: 24px;
    border-radius: 16px;
    color: white;
    transition: transform 0.3s, box-shadow 0.3s;
    cursor: pointer;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
    }

    .stat-icon-wrapper {
      width: 70px;
      height: 70px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.25);
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 20px;
    }

    .stat-content {
      flex: 1;

      .stat-value {
        font-size: 36px;
        font-weight: 700;
        line-height: 1.2;
      }

      .stat-label {
        font-size: 14px;
        opacity: 0.9;
        margin-top: 4px;
      }
    }
  }

  .stat-card-gradient-1 {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }

  .stat-card-gradient-2 {
    background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  }

  .stat-card-gradient-3 {
    background: linear-gradient(135deg, #fc4a1a 0%, #f7b733 100%);
  }

  .stat-card-gradient-4 {
    background: linear-gradient(135deg, #0f3460 0%, #533483 100%);
  }

  .main-section {
    .category-card {
      height: 420px;
      border-radius: 16px;
      overflow: hidden;

      :deep(.el-card__header) {
        background: linear-gradient(135deg, #fdfbfb 0%, #ebedee 100%);
        border-bottom: none;
      }
    }

    .orders-card {
      border-radius: 16px;
      overflow: hidden;

      :deep(.el-card__header) {
        background: linear-gradient(135deg, #fdfbfb 0%, #ebedee 100%);
        border-bottom: none;
      }
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 16px;
    font-weight: 600;
    color: #333;

    .header-left {
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }

  .chart {
    height: 320px;
  }
}
</style>
