import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '数据看板', icon: 'DataAnalysis' }
      },
      {
        path: 'admin',
        name: 'Admin',
        component: () => import('@/views/AdminManagement.vue'),
        meta: { title: '管理员管理', icon: 'UserFilled' }
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/RoleManagement.vue'),
        meta: { title: '角色管理', icon: 'Avatar' }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/UserManagement.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'product',
        name: 'Product',
        component: () => import('@/views/ProductManagement.vue'),
        meta: { title: '商品管理', icon: 'Goods' }
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('@/views/OrderManagement.vue'),
        meta: { title: '订单管理', icon: 'Document' }
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('@/views/CartManagement.vue'),
        meta: { title: '购物车管理', icon: 'ShoppingCart' }
      },
      {
        path: 'after-sale',
        name: 'AfterSale',
        component: () => import('@/views/AfterSaleManagement.vue'),
        meta: { title: '售后服务', icon: 'Service' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth !== false && !authStore.isLoggedIn) {
    next('/login')
  } else if (to.path === '/login' && authStore.isLoggedIn) {
    next('/')
  } else {
    next()
  }
})

export default router
