<template>
  <div class="admin-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>管理员管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增管理员
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px;">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="roleName" label="角色" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button 
              :type="row.status === 1 ? 'warning' : 'success'" 
              link 
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!form.id">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="form.roleId" placeholder="请选择角色" style="width: 100%">
            <el-option label="超级管理员" :value="1" />
            <el-option label="普通管理员" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminListApi, createAdminApi, updateAdminApi, deleteAdminApi, updateAdminStatusApi } from '@/api'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增管理员')
const formRef = ref(null)

const searchForm = reactive({
  username: '',
  status: null
})

const tableData = ref([])
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})
const allSearchData = ref([])

const form = reactive({
  id: null,
  username: '',
  password: '',
  roleId: null,
  status: 1
})

const roleNameMap = {
  1: '超级管理员',
  2: '普通管理员'
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const hasSearch = !!searchForm.username || searchForm.status !== null
    const requestSize = hasSearch ? 1000 : pagination.size
    const res = await getAdminListApi({
      page: 1,
      size: requestSize,
      username: searchForm.username
    })
    const records = (res.data.records || []).map(item => ({
      ...item,
      roleName: item.roleName || roleNameMap[item.roleId] || '-'
    }))
    const filtered = records.filter(item => {
      const usernameMatch = !searchForm.username || (item.username || '').includes(searchForm.username)
      const statusMatch = searchForm.status === null || Number(item.status) === Number(searchForm.status)
      return usernameMatch && statusMatch
    })
    allSearchData.value = filtered
    pagination.total = filtered.length
    const start = (pagination.page - 1) * pagination.size
    const end = start + pagination.size
    tableData.value = filtered.slice(start, end)
  } catch (error) {
    ElMessage.error('获取管理员列表失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchForm.username = ''
  searchForm.status = null
  pagination.page = 1
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = '新增管理员'
  Object.assign(form, { id: null, username: '', password: '', roleId: null, status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑管理员'
  Object.assign(form, {
    id: row.id,
    username: row.username,
    password: '',
    roleId: row.roleId,
    status: Number(row.status)
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (form.id) {
      const updatePayload = {
        id: form.id,
        roleId: form.roleId,
        status: Number(form.status)
      }
      if (form.password && form.password.trim()) {
        updatePayload.password = form.password.trim()
      }
      await updateAdminApi(updatePayload)
      ElMessage.success('更新成功')
    } else {
      await createAdminApi(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await loadData()
  } catch (error) {
    ElMessage.error(form.id ? '更新失败' : '创建失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该管理员吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteAdminApi(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '禁用'
  
  try {
    await updateAdminStatusApi(row.id, newStatus)
    ElMessage.success(`${action}成功`)
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
.admin-management {
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
