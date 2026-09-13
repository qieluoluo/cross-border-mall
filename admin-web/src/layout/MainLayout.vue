<template>
  <div class="main-layout">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <el-icon :size="28" color="#409EFF"><Shop /></el-icon>
        <span v-show="!isCollapse" class="logo-text">电商管理系统</span>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>数据看板</template>
        </el-menu-item>

        <el-menu-item v-if="!authStore.isMerchant" index="/admin">
          <el-icon><UserFilled /></el-icon>
          <template #title>管理员管理</template>
        </el-menu-item>

        <el-menu-item v-if="!authStore.isMerchant" index="/role">
          <el-icon><Avatar /></el-icon>
          <template #title>角色管理</template>
        </el-menu-item>

        <el-sub-menu index="user-group">
          <template #title>
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/user">用户列表</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="product-group">
          <template #title>
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </template>
          <el-menu-item index="/product">商品列表</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="order-group">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>订单管理</span>
          </template>
          <el-menu-item index="/order">订单列表</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/cart">
          <el-icon><ShoppingCart /></el-icon>
          <template #title>购物车管理</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container class="main-container">
      <el-header class="header">
        <div class="header-left">
          <el-icon
            class="collapse-btn"
            @click="isCollapse = !isCollapse"
            :size="20"
          >
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :icon="UserFilled" />
              <span class="username">{{ authStore.userInfo?.username || '管理员' }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>

    <el-dialog v-model="profileDialogVisible" title="个人中心" width="500px">
      <el-form :model="profileForm" label-width="100px" disabled>
        <el-form-item label="用户名">
          <el-input v-model="profileForm.username" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="profileForm.realName" />
        </el-form-item>
        <el-form-item label="角色">
          <el-input v-model="profileForm.roleName" />
        </el-form-item>
        <el-form-item label="最后登录IP">
          <el-input v-model="profileForm.lastLoginIp" />
        </el-form-item>
        <el-form-item label="最后登录时间">
          <el-input v-model="profileForm.lastLoginTime" />
        </el-form-item>
        <el-form-item label="创建时间">
          <el-input v-model="profileForm.createTime" />
        </el-form-item>
      </el-form>

      <el-divider>密码重置</el-divider>

      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="profileDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleResetPassword" :loading="passwordLoading">重置密码</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Shop, UserFilled, Avatar, User, Goods, Document, ShoppingCart, DataAnalysis, Fold, Expand } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getAdminByIdApi, getRoleListApi, updateAdminApi } from '@/api'

const route = useRoute()
const authStore = useAuthStore()
const isCollapse = ref(false)

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '首页')

const profileDialogVisible = ref(false)
const profileForm = reactive({
  username: '',
  realName: '',
  roleName: '',
  lastLoginIp: '',
  lastLoginTime: '',
  createTime: ''
})

const passwordForm = reactive({
  newPassword: '',
  confirmPassword: ''
})

const passwordFormRef = ref(null)
const passwordLoading = ref(false)

const passwordRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const handleCommand = (command) => {
  if (command === 'profile') {
    loadProfile()
  } else if (command === 'logout') {
    authStore.logout()
  }
}

const loadProfile = async () => {
  const adminId = authStore.userInfo?.id
  if (!adminId) {
    ElMessage.error('用户信息不存在')
    return
  }

  try {
    const [adminRes, roleRes] = await Promise.all([
      getAdminByIdApi(adminId),
      getRoleListApi()
    ])

    const adminData = adminRes.data
    const roles = roleRes.data || []

    const role = roles.find(r => r.id === adminData.roleId)

    profileForm.username = adminData.username || ''
    profileForm.realName = adminData.realName || ''
    profileForm.roleName = role?.name || ''
    profileForm.lastLoginIp = adminData.lastLoginIp || ''
    profileForm.lastLoginTime = adminData.lastLoginTime ? formatDateTime(adminData.lastLoginTime) : ''
    profileForm.createTime = adminData.createTime ? formatDateTime(adminData.createTime) : ''

    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''

    profileDialogVisible.value = true
  } catch (error) {
    console.error('加载个人信息失败:', error)
    ElMessage.error('加载个人信息失败')
  }
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const handleResetPassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      const adminId = authStore.userInfo?.id
      if (!adminId) {
        ElMessage.error('用户信息不存在')
        return
      }

      passwordLoading.value = true
      try {
        await updateAdminApi({
          id: adminId,
          password: passwordForm.newPassword
        })
        ElMessage.success('密码重置成功')
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
        profileDialogVisible.value = false
      } catch (error) {
        console.error('密码重置失败:', error)
        ElMessage.error('密码重置失败')
      } finally {
        passwordLoading.value = false
      }
    }
  })
}
</script>

<style scoped lang="scss">
.main-layout {
  display: flex;
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;

  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    background-color: #263445;

    .logo-text {
      color: #fff;
      font-size: 18px;
      font-weight: 600;
      white-space: nowrap;
    }
  }

  .el-menu {
    border-right: none;
  }
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.header {
  background: white;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;

  .header-left {
    display: flex;
    align-items: center;
    gap: 20px;

    .collapse-btn {
      cursor: pointer;
      transition: transform 0.3s;

      &:hover {
        transform: scale(1.2);
      }
    }
  }

  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      gap: 10px;
      cursor: pointer;

      .username {
        color: #333;
        font-size: 14px;
      }
    }
  }
}

.main-content {
  background: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}
</style>