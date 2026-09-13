<template>
  <div class="after-sale-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>售后服务</span>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 160px">
            <el-option label="待处理" value="待处理" />
            <el-option label="处理中" value="处理中" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已拒绝" value="已拒绝" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="userName" label="用户" />
        <el-table-column prop="reason" label="售后原因" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button 
              v-if="row.status === '待处理'" 
              type="success" 
              link 
              @click="handleProcess(row)"
            >
              处理
            </el-button>
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

    <el-dialog v-model="detailVisible" title="售后详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="售后ID">{{ currentAfterSale.id }}</el-descriptions-item>
        <el-descriptions-item label="订单号">{{ currentAfterSale.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户">{{ currentAfterSale.userName }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ currentAfterSale.type }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentAfterSale.status)">
            {{ currentAfterSale.status }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ currentAfterSale.createTime }}</el-descriptions-item>
        <el-descriptions-item label="售后原因" :span="2">{{ currentAfterSale.reason }}</el-descriptions-item>
        <el-descriptions-item label="处理说明" :span="2">{{ currentAfterSale.processNote || '暂无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="processVisible" title="处理售后" width="500px">
      <el-form :model="processForm" label-width="80px">
        <el-form-item label="处理方式">
          <el-select v-model="processForm.action" placeholder="请选择处理方式" style="width: 100%">
            <el-option label="同意售后" value="approve" />
            <el-option label="拒绝售后" value="reject" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理说明">
          <el-input 
            v-model="processForm.note" 
            type="textarea" 
            placeholder="请输入处理说明" 
            :rows="4"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="processVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitProcess">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAfterSaleListApi, handleAfterSaleApi } from '@/api'

const loading = ref(false)
const detailVisible = ref(false)
const processVisible = ref(false)
const currentAfterSale = ref({})
const processingId = ref(null)

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

const processForm = reactive({
  action: '',
  note: ''
})

const getStatusType = (status) => {
  const map = {
    '待处理': 'warning',
    '处理中': 'primary',
    '已完成': 'success',
    '已拒绝': 'danger'
  }
  return map[status] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const hasSearch = !!searchForm.orderNo || !!searchForm.status
    const requestSize = hasSearch ? 1000 : pagination.size
    const res = await getAfterSaleListApi({
      page: 1,
      size: requestSize
    })
    const records = res.data.records || []
    const filtered = records.filter(item => {
      const orderNoMatch = !searchForm.orderNo || String(item.orderNo || '').includes(searchForm.orderNo)
      const statusMatch = !searchForm.status || item.status === searchForm.status
      return orderNoMatch && statusMatch
    })
    pagination.total = filtered.length
    const start = (pagination.page - 1) * pagination.size
    const end = start + pagination.size
    tableData.value = filtered.slice(start, end)
  } catch (error) {
    ElMessage.error('获取售后服务列表失败')
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
  currentAfterSale.value = row
  detailVisible.value = true
}

const handleProcess = (row) => {
  processingId.value = row.id
  processForm.action = ''
  processForm.note = ''
  processVisible.value = true
}

const handleSubmitProcess = async () => {
  if (!processForm.action) {
    ElMessage.warning('请选择处理方式')
    return
  }
  
  try {
    // action: 1=同意, 2=拒绝
    const action = processForm.action === 'approve' ? 1 : 2
    await handleAfterSaleApi(processingId.value, action, processForm.note)
    
    ElMessage.success('处理成功')
    processVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('处理失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.after-sale-management {
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
}
</style>
