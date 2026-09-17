<template>
  <div class="page-shell">
    <Header v-if="!isAuthPage" />
    <div ref="scrollRoot" class="page-scroll">
      <div class="page-body">
        <main
          class="container mx-auto w-full page-main"
          :class="isAuthPage ? 'p-0 page-main-auth' : 'py-4'"
        >
          <router-view v-slot="{ Component }">
            <component :is="Component" />
          </router-view>
        </main>
        <Footer v-if="!isAuthPage" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'

const route = useRoute()
const scrollRoot = ref(null)

const isAuthPage = computed(() => {
  return route.path === '/login' || route.path === '/register'
})

const resetScroll = () => {
  window.scrollTo(0, 0)
  document.documentElement.scrollTop = 0
  document.body.scrollTop = 0
  if (scrollRoot.value) {
    scrollRoot.value.scrollTop = 0
  }
}

watch(
  () => route.fullPath,
  () => {
    resetScroll()
  },
  { flush: 'pre' }
)
</script>

<style scoped>
.page-shell {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
  background-color: #f3f4f6;
}

.page-scroll {
  flex: 1;
  min-height: 0;
  overflow-x: hidden;
  overflow-y: auto;
  overflow-anchor: none;
}

.page-body {
  min-height: 100%;
  display: flex;
  flex-direction: column;
}

.page-main {
  flex: 1 0 auto;
  min-height: calc(100vh - 64px);
}

.page-main-auth {
  min-height: 100vh;
}
</style>
