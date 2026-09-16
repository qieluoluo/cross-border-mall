<template>
  <div class="product-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">添加商品</el-button>
            <el-button type="success" @click="handleExcelUploadClick">批量导入</el-button>
            <input
              ref="excelFileInput"
              type="file"
              accept=".xlsx,.xls"
              class="excel-file-input"
              @change="handleExcelFileChange"
            />
          </div>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.name" placeholder="请输入商品名称" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.categoryId" placeholder="请选择分类" clearable style="width: 200px">
            <el-option
              v-for="item in categoryList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="商品名称" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column prop="subTitle" label="副标题" width="150" show-overflow-tooltip />
        <el-table-column prop="mainImage" label="主图" width="150">
          <template #default="{ row }">
            <img v-if="row.mainImage" :src="row.mainImage" class="main-image" @error="applyImageFallback" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === true || row.status === 1 ? 'success' : 'info'">
              {{ row.status === true || row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button type="warning" link @click="handleToggleStatus(row)">
              {{ row.status ? '下架' : '上架' }}
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

    <!-- 商品详情弹窗 -->
    <el-dialog v-model="detailVisible" title="商品详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="商品ID">{{ currentProduct.id }}</el-descriptions-item>
        <el-descriptions-item label="商品名称">{{ currentProduct.name }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ currentProduct.category }}</el-descriptions-item>
        <el-descriptions-item label="价格">¥{{ currentProduct.price }}</el-descriptions-item>
        <el-descriptions-item label="库存">{{ currentProduct.stock }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentProduct.status === true || currentProduct.status === 1 ? 'success' : 'info'">
            {{ currentProduct.status === true || currentProduct.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="副标题" :span="2">{{ currentProduct.subTitle }}</el-descriptions-item>
        <el-descriptions-item label="主图" :span="2">
          <img v-if="currentProduct.mainImage" :src="currentProduct.mainImage" class="detail-image" @error="applyImageFallback" />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentProduct.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ currentProduct.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 添加商品弹窗 -->
    <el-dialog v-model="addVisible" title="添加商品" width="600px" @close="resetAddForm">
      <el-form :model="addForm" label-width="100px">
        <el-form-item label="商品名称" required>
          <el-input v-model="addForm.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="addForm.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="item in categoryList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" required>
          <el-input-number v-model="addForm.price" :min="0" :precision="2" placeholder="请输入价格" />
        </el-form-item>
        <el-form-item label="库存" required>
          <el-input-number v-model="addForm.stock" :min="0" placeholder="请输入库存" />
        </el-form-item>
        <el-form-item label="副标题">
          <el-input v-model="addForm.subTitle" placeholder="请输入副标题" />
        </el-form-item>
        <el-form-item label="主图" required>
          <el-upload
            class="image-upload"
            :show-file-list="false"
            :http-request="handleImageUpload"
            :before-upload="beforeImageUpload"
            accept="image/*"
          >
            <div v-if="!addForm.mainImage" class="upload-placeholder">
              <el-icon :size="48" color="#ccc"><Plus /></el-icon>
              <span>点击上传主图</span>
            </div>
            <img v-else :src="addForm.mainImage" class="uploaded-image" />
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddSubmit" :loading="addLoading">确认添加</el-button>
      </template>
    </el-dialog>

    <!-- Excel导入结果弹窗 -->
    <el-dialog v-model="importResultVisible" title="导入结果" width="500px">
      <div class="import-result">
        <div v-if="importSuccess" class="success-icon">
          <el-icon :size="48" color="#67c23a"><Check /></el-icon>
        </div>
        <div v-else class="error-icon">
          <el-icon :size="48" color="#f56c6c"><Close /></el-icon>
        </div>
        <h3>{{ importSuccess ? '导入成功' : '导入失败' }}</h3>
        <p>{{ importMessage }}</p>
        <div v-if="importSuccess && importResult" class="result-detail">
          <p>成功导入：{{ importResult.successCount }} 条</p>
          <p v-if="importResult.failedCount > 0">失败：{{ importResult.failedCount }} 条</p>
          <div v-if="importResult.errors && importResult.errors.length">
            <p>失败详情：</p>
            <ul class="error-list">
              <li v-for="(error, index) in importResult.errors.slice(0, 5)" :key="index">
                第{{ error.row }}行: {{ error.message }}
              </li>
            </ul>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button type="primary" @click="importResultVisible = false">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Check, Close, Plus } from '@element-plus/icons-vue'
import { getProductListApi, updateProductStatusApi, addProductApi, batchImportProductApi, getCategoryListApi, uploadProductImageApi } from '@/api'

const loading = ref(false)
const detailVisible = ref(false)
const addVisible = ref(false)
const importResultVisible = ref(false)
const addLoading = ref(false)
const importLoading = ref(false)
const currentProduct = ref({})
const excelFileInput = ref(null)
const categoryList = ref([])

const importSuccess = ref(false)
const importMessage = ref('')
const importResult = ref(null)

const searchForm = reactive({
  name: '',
  categoryId: ''
})

const addForm = reactive({
  name: '',
  categoryId: '',
  price: 0,
  stock: 0,
  subTitle: '',
  mainImage: '',
  status: 1
})

const tableData = ref([])
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const resolveImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http://') || path.startsWith('https://')) return path
  return path.startsWith('/') ? path : `/${path}`
}

const applyImageFallback = (event) => {
  const target = event?.target
  if (!target) return
  target.onerror = null
  target.src = '/images/placeholder.png'
}

const normalizeProduct = (item) => {
  const mainImagePath = item.mainImage || item.main_image || ''
  const fullImageUrl = resolveImageUrl(mainImagePath)
  return {
    ...item,
    categoryId: item.categoryId || item.category_id || '',
    category: item.category || '',
    createTime: item.createTime || item.create_time || '',
    updateTime: item.updateTime || item.update_time || '',
    subTitle: item.subTitle || item.sub_title || '',
    mainImage: fullImageUrl
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const hasSearch = !!searchForm.name || !!searchForm.categoryId
    const requestSize = hasSearch ? 1000 : pagination.size
    const res = await getProductListApi({
      page: 1,
      size: requestSize
    })
    const records = (res.data.records || []).map(normalizeProduct)
    const filtered = records.filter(item => {
      const nameMatch = !searchForm.name || (item.name || '').includes(searchForm.name)
      const categoryIdMatch = !searchForm.categoryId || String(item.categoryId || '').includes(String(searchForm.categoryId))
      return nameMatch && categoryIdMatch
    })
    pagination.total = filtered.length
    const start = (pagination.page - 1) * pagination.size
    const end = start + pagination.size
    tableData.value = filtered.slice(start, end)
  } catch (error) {
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchForm.name = ''
  searchForm.categoryId = ''
  pagination.page = 1
  loadData()
}

const handleView = (row) => {
  currentProduct.value = row
  detailVisible.value = true
}

const handleToggleStatus = async (row) => {
  const currentStatus = row.status === true || row.status === 1
  const newStatus = currentStatus ? 0 : 1
  const action = newStatus === 1 ? '上架' : '下架'

  try {
    await updateProductStatusApi(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    row.status = newStatus
  } catch (error) {
    ElMessage.error(`${action}失败`)
  }
}

const handleAdd = () => {
  resetAddForm()
  addVisible.value = true
}

const resetAddForm = () => {
  addForm.name = ''
  addForm.categoryId = ''
  addForm.price = 0
  addForm.stock = 0
  addForm.subTitle = ''
  addForm.mainImage = ''
  addForm.status = 1
}

const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('请选择图片文件')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB')
    return false
  }
  return true
}

const handleImageUpload = async ({ file }) => {
  try {
    const res = await uploadProductImageApi(file)
    addForm.mainImage = resolveImageUrl(res.data)
    ElMessage.success('图片上传成功')
  } catch (error) {
    ElMessage.error(error.message || '图片上传失败')
  }
}

const handleAddSubmit = async () => {
  if (!addForm.name) {
    ElMessage.warning('请输入商品名称')
    return
  }
  if (!addForm.categoryId) {
    ElMessage.warning('请选择分类')
    return
  }
  if (!addForm.price || addForm.price <= 0) {
    ElMessage.warning('请输入有效的价格')
    return
  }
  if (!addForm.mainImage) {
    ElMessage.warning('请上传商品主图')
    return
  }

  addLoading.value = true
  try {
    const formData = {
      ...addForm,
      mainImage: addForm.mainImage
    }
    await addProductApi(formData)
    ElMessage.success('添加商品成功')
    addVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('添加商品失败')
  } finally {
    addLoading.value = false
  }
}

const handleExcelUploadClick = () => {
  excelFileInput.value?.click()
}

const handleExcelFileChange = async (event) => {
  const file = event.target.files?.[0]
  if (!file) return

  if (!file.name.match(/\.(xlsx|xls)$/)) {
    ElMessage.warning('请选择Excel文件(.xlsx或.xls格式)')
    event.target.value = ''
    return
  }

  importLoading.value = true

  const formData = new FormData()
  formData.append('file', file)

  try {
    const res = await batchImportProductApi(formData)
    importSuccess.value = true
    importMessage.value = res.message || '导入成功'
    importResult.value = res.data
    importResultVisible.value = true
    loadData()
  } catch (error) {
    importSuccess.value = false
    importMessage.value = error.message || '导入失败'
    importResult.value = null
    importResultVisible.value = true
  } finally {
    importLoading.value = false
    event.target.value = ''
  }
}

const loadCategoryList = async () => {
  try {
    const res = await getCategoryListApi()
    categoryList.value = res.data || []
  } catch (error) {
    ElMessage.error('获取分类列表失败')
  }
}

onMounted(() => {
  loadData()
  loadCategoryList()
})
</script>

<style scoped lang="scss">
.product-management {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-actions {
      display: flex;
      gap: 10px;

      .excel-file-input {
        display: none;
      }
    }
  }

  .search-form {
    margin-bottom: 20px;
  }

  .pagination {
    margin-top: 20px;
    justify-content: flex-end;
  }

  .main-image {
    width: 60px;
    height: 60px;
    object-fit: cover;
    border-radius: 4px;
  }

  .detail-image {
    max-width: 200px;
    max-height: 200px;
    object-fit: contain;
    border-radius: 4px;
  }

  .image-upload {
    .upload-placeholder {
      width: 150px;
      height: 150px;
      border: 2px dashed #d9d9d9;
      border-radius: 4px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        border-color: #409EFF;
        background: #f5f7fa;
      }

      span {
        margin-top: 10px;
        color: #999;
        font-size: 14px;
      }
    }

    .uploaded-image {
      width: 150px;
      height: 150px;
      object-fit: cover;
      border-radius: 4px;
      cursor: pointer;
      border: 2px solid #d9d9d9;

      &:hover {
        border-color: #409EFF;
      }
    }
  }

  .import-result {
    text-align: center;
    padding: 30px 0;

    .success-icon, .error-icon {
      margin-bottom: 20px;
    }

    h3 {
      margin-bottom: 10px;
    }

    .result-detail {
      margin-top: 20px;
      text-align: left;
      padding: 15px;
      background: #f5f7fa;
      border-radius: 4px;

      p {
        margin: 5px 0;
      }

      .error-list {
        margin-top: 10px;
        padding-left: 20px;
        font-size: 13px;
        color: #f56c6c;
      }
    }
  }
}
</style>
