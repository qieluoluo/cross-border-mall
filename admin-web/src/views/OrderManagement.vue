<template>
  <div class="order-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单管理</span>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px;">
            <el-option label="待付款" :value="0" />
            <el-option label="待发货" :value="1" />
            <el-option label="配送中" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="userNickname" label="用户" />
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="{ row }">
            ¥{{ row.totalAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" />
        <el-table-column prop="receiverName" label="收货人" width="100" />
        <el-table-column prop="receiverPhone" label="联系电话" width="130" />
        <el-table-column label="物流" width="100">
          <template #default="{ row }">
            <el-button type="success" link @click="handleExpress(row)">物流</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button type="warning" link @click="handleDelivery(row)" v-if="row.status === 1">发货</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        class="pagination"
      />
    </el-card>

    <el-dialog v-model="detailVisible" title="订单详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户">{{ currentOrder.userNickname }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ currentOrder.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="运费">¥{{ currentOrder.freightAmount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="支付金额">¥{{ currentOrder.payAmount }}</el-descriptions-item>
        <el-descriptions-item label="优惠金额">¥{{ currentOrder.discountAmount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="支付方式">支付宝</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ currentOrder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ currentOrder.payTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发货时间">{{ currentOrder.deliveryTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="收货时间">{{ currentOrder.receiveTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="收货人">{{ currentOrder.receiverName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.receiverPhone }}</el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">{{ currentOrder.receiverAddress }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentOrder.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <div class="order-items" v-if="currentOrder.items && currentOrder.items.length">
        <h4>商品清单</h4>
        <el-table :data="currentOrder.items" border>
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column prop="price" label="单价" width="100">
            <template #default="{ row }">¥{{ row.price }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="subtotal" label="小计" width="100">
            <template #default="{ row }">¥{{ row.subtotal }}</template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <!-- 物流详情弹窗 -->
    <el-dialog v-model="expressVisible" title="物流详情" width="600px">
      <div v-if="currentExpress" class="express-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentExpress.orderId }}</el-descriptions-item>
          <el-descriptions-item label="快递单号">{{ currentExpress.expressNo }}</el-descriptions-item>
          <el-descriptions-item label="快递公司">{{ getExpressCompanyName(currentExpress.expressCompany) }}</el-descriptions-item>
          <el-descriptions-item label="物流状态">
            <el-tag :type="getExpressStatusType(currentExpress.status)">{{ getExpressStatusText(currentExpress.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="最新状态">{{ currentExpress.latestStatus }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ currentExpress.latestTime }}</el-descriptions-item>
        </el-descriptions>

        <!-- 物流轨迹 -->
        <div v-if="currentExpress.detailJson" class="express-trace">
          <h4>物流轨迹</h4>
          <div class="trace-list">
            <div
              v-for="(trace, index) in expressTraces"
              :key="index"
              class="trace-item"
              :class="{ 'active': index === 0 }"
            >
              <div class="trace-dot"></div>
              <div class="trace-content">
                <div class="trace-time">{{ trace.AcceptTime }}</div>
                <div class="trace-station">{{ trace.AcceptStation }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="no-express">
        <el-empty :description="`该订单${getStatusText(currentExpressOrderStatus)}，暂无物流信息`" />
      </div>
    </el-dialog>

    <!-- 发货弹窗 -->
    <el-dialog v-model="deliveryVisible" title="发货" width="500px">
      <el-form :model="deliveryForm" label-width="100px">
        <el-form-item label="订单号">
          <span>{{ currentDeliveryOrder.orderNo }}</span>
        </el-form-item>
        <el-form-item label="收货人">
          <span>{{ currentDeliveryOrder.receiverName }}</span>
        </el-form-item>
        <el-form-item label="联系电话">
          <span>{{ currentDeliveryOrder.receiverPhone }}</span>
        </el-form-item>
        <el-form-item label="收货地址">
          <span>{{ currentDeliveryOrder.receiverAddress }}</span>
        </el-form-item>
        <el-form-item label="物流公司" required>
          <el-select v-model="deliveryForm.expressCompany" placeholder="请选择物流公司" style="width: 100%;">
            <el-option
              v-for="company in expressCompanies"
              :key="company.code"
              :label="company.name"
              :value="company.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="快递单号" required>
          <el-input v-model="deliveryForm.expressNo" placeholder="请输入快递单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deliveryVisible = false">取消</el-button>
        <el-button type="primary" @click="handleDeliverySubmit" :loading="deliveryLoading">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrderListApi, getExpressInfoApi, deliveryOrderApi, getExpressCompaniesApi } from '@/api'

const loading = ref(false)
const detailVisible = ref(false)
const expressVisible = ref(false)
const deliveryVisible = ref(false)
const deliveryLoading = ref(false)
const currentOrder = ref({})
const currentExpress = ref(null)
const expressTraces = ref([])
const currentExpressOrderStatus = ref(null)
const currentDeliveryOrder = ref({})
const expressCompanies = ref([])

const deliveryForm = reactive({
  expressCompany: '',
  expressNo: ''
})

const searchForm = reactive({
  orderNo: '',
  status: ''
})

const tableData = ref([])
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

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

const getExpressStatusType = (status) => {
  const map = {
    'pending': 'info',
    'transit': 'warning',
    'delivered': 'success',
    'exception': 'danger'
  }
  return map[status] || 'info'
}

const getExpressStatusText = (status) => {
  const map = {
    'pending': '待揽收',
    'transit': '运输中',
    'delivered': '已签收',
    'exception': '异常'
  }
  return map[status] || status
}

const getExpressCompanyName = (code) => {
  if (!code) return ''
  const company = expressCompanies.value.find(c => c.code === code)
  return company ? company.name : code
}

const loadExpressCompanies = async () => {
  try {
    const res = await getExpressCompaniesApi()
    if (res.data) {
      expressCompanies.value = res.data
    }
  } catch (error) {
    console.error('获取快递公司列表失败:', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const hasSearch = !!searchForm.orderNo || searchForm.status !== ''
    const requestSize = hasSearch ? 1000 : pagination.size
    const res = await getOrderListApi({
      page: 1,
      size: requestSize
    })
    const records = res.data.records || []
    const filtered = records.filter(item => {
      const orderNoMatch = !searchForm.orderNo || String(item.orderNo || '').includes(searchForm.orderNo)
      const statusMatch = searchForm.status === '' || item.status === searchForm.status
      return orderNoMatch && statusMatch
    })
    pagination.total = filtered.length
    const start = (pagination.page - 1) * pagination.size
    const end = start + pagination.size
    tableData.value = filtered.slice(start, end)
  } catch (error) {
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchForm.orderNo = ''
  searchForm.status = ''
  pagination.page = 1
  loadData()
}

const handleView = (row) => {
  currentOrder.value = row
  detailVisible.value = true
}

const handleExpress = async (row) => {
  currentExpress.value = null
  expressTraces.value = []
  currentExpressOrderStatus.value = row.status

  try {
    const res = await getExpressInfoApi(row.id)
    currentExpress.value = res.data

    // 解析物流轨迹JSON
    if (currentExpress.value && currentExpress.value.detailJson) {
      try {
        const detail = JSON.parse(currentExpress.value.detailJson)
        expressTraces.value = detail.Traces || []
      } catch (e) {
        console.log('解析物流轨迹失败:', e)
      }
    }
  } catch (error) {
    console.log('获取物流信息失败:', error)
  }

  expressVisible.value = true
}

const handleDelivery = (row) => {
  currentDeliveryOrder.value = row
  deliveryForm.expressCompany = ''
  deliveryForm.expressNo = ''
  deliveryVisible.value = true
}

const handleDeliverySubmit = async () => {
  if (!deliveryForm.expressCompany) {
    ElMessage.warning('请选择物流公司')
    return
  }
  if (!deliveryForm.expressNo) {
    ElMessage.warning('请输入快递单号')
    return
  }
  
  deliveryLoading.value = true
  try {
    await deliveryOrderApi({
      orderId: currentDeliveryOrder.value.id,
      expressNo: deliveryForm.expressNo,
      expressCompany: deliveryForm.expressCompany
    })
    ElMessage.success('发货成功')
    deliveryVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('发货失败')
  } finally {
    deliveryLoading.value = false
  }
}

onMounted(() => {
  loadExpressCompanies()
  loadData()
})
</script>

<style scoped lang="scss">
.order-management {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .search-form {
    margin-bottom: 20px;
  }

  .pagination {
    margin-top: 20px;
    justify-content: flex-end;
  }

  .express-info {
    margin-top: 20px;

    h4 {
      margin-bottom: 10px;
      color: #333;
    }
  }

  .order-items {
    margin-top: 20px;

    h4 {
      margin-bottom: 10px;
      color: #333;
    }
  }

  .express-detail {
    .express-trace {
      margin-top: 20px;

      h4 {
        margin-bottom: 15px;
        color: #333;
      }

      .trace-list {
        padding-left: 20px;
      }

      .trace-item {
        display: flex;
        position: relative;
        padding-bottom: 20px;

        &:last-child {
          padding-bottom: 0;

          .trace-dot::after {
            display: none;
          }
        }

        &.active {
          .trace-dot {
            background-color: #409eff;
            border-color: #409eff;
          }

          .trace-content {
            color: #333;
          }
        }

        .trace-dot {
          width: 12px;
          height: 12px;
          border-radius: 50%;
          background-color: #e4e7ed;
          border: 2px solid #d9d9d9;
          position: relative;
          z-index: 1;
          margin-right: 15px;
          flex-shrink: 0;

          &::after {
            content: '';
            position: absolute;
            top: 12px;
            left: 50%;
            transform: translateX(-50%);
            width: 2px;
            height: calc(100% + 20px);
            background-color: #e4e7ed;
          }
        }

        .trace-content {
          flex: 1;
          color: #999;

          .trace-time {
            font-size: 14px;
            margin-bottom: 4px;
          }

          .trace-station {
            font-size: 14px;
            line-height: 1.5;
          }
        }
      }
    }
  }

  .no-express {
    padding: 40px 0;
  }
}
</style>
