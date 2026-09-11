const IMAGE_FIELDS = [
  'main_image',
  'mainImage',
  'image',
  'productImage',
  'imgUrl',
  'picture',
  'photo',
  'thumbnail'
]

const DEFAULT_IMAGE = '/images/iphone15pro.png'

const INVALID_URL_PATTERNS = [
  'trae-api-cn.mchost.guru',
  'neeko-copilot.bytedance.net',
  'generating',
  'picsum.photos'
]

const isInvalidUrl = (url) => {
  return INVALID_URL_PATTERNS.some((pattern) => url.includes(pattern))
}

const normalizePath = (path) => {
  if (!path) return ''
  const trimmed = path.trim()
  if (trimmed.startsWith('http://') || trimmed.startsWith('https://')) {
    if (isInvalidUrl(trimmed)) return ''
    try {
      const { pathname } = new URL(trimmed)
      if (pathname.startsWith('/images/')) return pathname
    } catch {
      return ''
    }
    return trimmed
  }
  if (trimmed.startsWith('/images/') || trimmed.startsWith('/')) {
    return trimmed
  }
  if (trimmed.startsWith('images/')) {
    return `/${trimmed}`
  }
  return `/images/${trimmed}`
}

/** 从商品/购物车/订单项中解析图片路径（相对路径，如 /images/xxx.jpg） */
export const resolveProductImagePath = (item) => {
  if (!item) return ''

  for (const field of IMAGE_FIELDS) {
    const value = item[field]
    if (value && typeof value === 'string' && value.trim()) {
      const path = normalizePath(value.trim())
      if (path) return path
    }
  }
  return ''
}

/** 将图片路径转为可展示的 URL（开发环境走 Vite /images 代理） */
export const getImageUrl = (imagePath) => {
  const path = imagePath ? normalizePath(imagePath) : ''
  return path || DEFAULT_IMAGE
}

/** 获取商品/订单项展示用图片 URL */
export const getProductImage = (item) => {
  return getImageUrl(resolveProductImagePath(item))
}

/** 规范化后端字段，统一 image / mainImage，不注入假数据 */
export const normalizeProductFields = (product) => {
  if (!product) return product

  if (product.main_image && !product.mainImage) {
    product.mainImage = product.main_image
  }
  if (product.sub_title && !product.subTitle) {
    product.subTitle = product.sub_title
  }
  if (product.category_id != null && product.categoryId == null) {
    product.categoryId = product.category_id
  }
  if (product.create_time && !product.createTime) {
    product.createTime = product.create_time
  }
  if (product.update_time && !product.updateTime) {
    product.updateTime = product.update_time
  }

  const imagePath = resolveProductImagePath(product)
  if (imagePath) {
    product.image = imagePath
    if (!product.mainImage) {
      product.mainImage = imagePath
    }
  }

  return product
}
