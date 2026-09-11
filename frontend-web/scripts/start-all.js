import { spawn } from 'child_process'
import path from 'path'

const cwd = process.cwd()

console.log('启动前端开发服务器...')

const vite = spawn('npm', ['run', 'dev'], {
  cwd: cwd,
  stdio: 'inherit',
  shell: true
})

vite.on('close', (code) => {
  console.log(`前端服务器退出，退出码: ${code}`)
})

vite.on('error', (err) => {
  console.error('启动前端服务器失败:', err)
})