import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

const serveSpaOnRefresh = (req) => {
  if (req.headers.accept && req.headers.accept.includes('text/html')) {
    return '/index.html'
  }
}

const backendProxy = (target) => ({
  target,
  changeOrigin: true,
  bypass: serveSpaOnRefresh
})

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    host: '127.0.0.1',
    port: 18080,
    proxy: {
      '/admin': backendProxy('http://localhost:9999'),
      '/role': backendProxy('http://localhost:9999'),
      '/user': backendProxy('http://localhost:8005'),
      '/product': backendProxy('http://localhost:8001'),
      '/images': backendProxy('http://localhost:8001'),
      '/order': backendProxy('http://localhost:8003'),
      '/cart': backendProxy('http://localhost:8006'),
      '/after-sale': backendProxy('http://localhost:8007'),
      '/express': backendProxy('http://localhost:8008')
    }
  }
})
