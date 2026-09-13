import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

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
      '/admin': {
        target: 'http://localhost:9999',
        changeOrigin: true
      },
      '/role': {
        target: 'http://localhost:9999',
        changeOrigin: true
      },
      '/user': {
        target: 'http://localhost:8005',
        changeOrigin: true
      },
      '/product': {
        target: 'http://localhost:8001',
        changeOrigin: true
      },
      '/images': {
        target: 'http://localhost:8001',
        changeOrigin: true
      },
      '/order': {
        target: 'http://localhost:8003',
        changeOrigin: true
      },
      '/cart': {
        target: 'http://localhost:8006',
        changeOrigin: true
      },
      '/after-sale': {
        target: 'http://localhost:8007',
        changeOrigin: true
      },
      '/express': {
        target: 'http://localhost:8008',
        changeOrigin: true
      }
    },
    historyApiFallback: true
  }
})
