import fs from 'fs'
import path from 'path'

const envExamplePath = path.join(process.cwd(), '.env.example')
const envPath = path.join(process.cwd(), '.env')

if (!fs.existsSync(envPath)) {
  if (fs.existsSync(envExamplePath)) {
    fs.copyFileSync(envExamplePath, envPath)
    console.log('已创建 .env 文件')
  } else {
    const envContent = `# API 基础路径
VITE_API_BASE_URL=/api

# 网关服务地址
VITE_GATEWAY_URL=http://localhost:8888

# 商品服务地址
VITE_PRODUCT_SERVICE_URL=http://localhost:8002

# 用户服务地址
VITE_USER_SERVICE_URL=http://localhost:8005

# 购物车服务地址
VITE_CART_SERVICE_URL=http://localhost:8006

# 订单服务地址
VITE_ORDER_SERVICE_URL=http://localhost:8003

# 支付服务地址
VITE_PAYMENT_SERVICE_URL=http://localhost:8004

# 物流服务地址
VITE_EXPRESS_SERVICE_URL=http://localhost:8008
`
    fs.writeFileSync(envPath, envContent)
    console.log('已创建 .env 文件')
  }
} else {
  console.log('.env 文件已存在')
}

console.log('安装依赖中...')