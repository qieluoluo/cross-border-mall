<template>
  <div class="user-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button type="warning" link @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
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

    <el-dialog v-model="detailVisible" title="用户详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentUser.email }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'">
            {{ currentUser.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ currentUser.createTime }}</el-descriptions-item>
        <el-descriptions-item label="最后登录时间">{{ currentUser.lastLoginTime }}</el-descriptions-item>
        <el-descriptions-item label="登录次数">{{ currentUser.loginCount }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserListApi, updateUserStatusApi } from '@/api'

const loading = ref(false)
const detailVisible = ref(false)
const currentUser = ref({})

const searchForm = reactive({
  username: '',
  phone: ''
})

const tableData = ref([])
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const loadData = async () => {
  loading.value = true
  try {
    const hasSearch = !!searchForm.username || !!searchForm.phone
    const requestSize = hasSearch ? 1000 : pagination.size
    const res = await getUserListApi({
      page: 1,
      size: requestSize
    })
    const records = res.data.records || []
    const filtered = records.filter(item => {
      const usernameMatch = !searchForm.username || (item.username || '').includes(searchForm.username)
      const phoneMatch = !searchForm.phone || String(item.phone || '').includes(searchForm.phone)
      return usernameMatch && phoneMatch
    })
    pagination.total = filtered.length
    const start = (pagination.page - 1) * pagination.size
    const end = start + pagination.size
    tableData.value = filtered.slice(start, end)
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchForm.username = ''
  searchForm.phone = ''
  pagination.page = 1
  loadData()
}

const handleView = (row) => {
  currentUser.value = row
  detailVisible.value = true
}

const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '禁用'
  
  try {
    await updateUserStatusApi(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    // 立即更新本地数据
    row.status = newStatus
  } catch (error) {
    ElMessage.error(`${action}失败`)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.user-management {
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
