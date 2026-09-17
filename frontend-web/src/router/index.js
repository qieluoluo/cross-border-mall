import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import ProductList from '../views/ProductList.vue'
import ProductDetail from '../views/ProductDetail.vue'
import Cart from '../views/Cart.vue'
import Order from '../views/Order.vue'
import OrderDetail from '../views/OrderDetail.vue'
import Payment from '../views/Payment.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import User from '../views/User.vue'
import Express from '../views/Express.vue'

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/products', name: 'ProductList', component: ProductList },
  { path: '/product/:id', name: 'ProductDetail', component: ProductDetail },
  { path: '/cart', name: 'Cart', component: Cart, meta: { requiresAuth: true } },
  { path: '/order', name: 'Order', component: Order, meta: { requiresAuth: true } },
  { path: '/order/:id', name: 'OrderDetail', component: OrderDetail, meta: { requiresAuth: true } },
  { path: '/payment', name: 'Payment', component: Payment, meta: { requiresAuth: true } },
  { path: '/payment/success', name: 'PaymentSuccess', component: Payment, meta: { requiresAuth: true } },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  { path: '/user', name: 'User', component: User, meta: { requiresAuth: true } },
  { path: '/express', name: 'Express', component: Express, meta: { requiresAuth: true } },
  { path: '/search', name: 'Search', component: ProductList }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { left: 0, top: 0 }
  }
})

if (typeof window !== 'undefined' && 'scrollRestoration' in window.history) {
  window.history.scrollRestoration = 'manual'
}

const jumpToTop = () => {
  window.scrollTo(0, 0)
  document.documentElement.scrollTop = 0
  document.body.scrollTop = 0
  const scroller = document.querySelector('.page-scroll')
  if (scroller) {
    scroller.scrollTop = 0
  }
}

router.beforeEach((to, from, next) => {
  if (to.path !== from.path) {
    jumpToTop()
  }

  const token = localStorage.getItem('token')
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  if (requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

router.afterEach(() => {
  jumpToTop()
  requestAnimationFrame(jumpToTop)
})

export default router
