<template>
  <div class="min-h-screen bg-gray-100">
    <Header v-if="!isAuthPage" />
    <main class="container mx-auto py-4" :class="{ 'p-0': isAuthPage }">
      <router-view />
    </main>
    <Footer v-if="!isAuthPage" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'

const route = useRoute()

const isAuthPage = computed(() => {
  return route.path === '/login' || route.path === '/register'
})
</script>