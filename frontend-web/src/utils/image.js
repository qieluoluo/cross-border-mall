const IMAGE_FIELDS = [
  'mainImage',
  'main_image',
  'MAIN_IMAGE',
  'productImage',
  'product_image',
  'image',
  'imgUrl',
  'picture',
  'photo',
  'thumbnail'
]

const DEFAULT_IMAGE = '/images/placeholder.png'

const INVALID_URL_PATTERNS = [
  'trae-api-cn.mchost.guru',
  'neeko-copilot.bytedance.net',
  'generating',
  'picsum.photos'
]

const isInvalidUrl = (url) => INVALID_URL_PATTERNS.some((pattern) => url.includes(pattern))

const normalizePath = (path) => {
  if (!path) return ''
  const trimmed = String(path).trim()
  if (!trimmed) return ''
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

export const getImageUrl = (imagePath) => {
  const path = imagePath ? normalizePath(imagePath) : ''
  return path || DEFAULT_IMAGE
}

export const getProductImage = (item) => getImageUrl(resolveProductImagePath(item))

export const applyImageFallback = (event) => {
  const target = event?.target
  if (!target) return
  target.onerror = null
  target.src = DEFAULT_IMAGE
}

export const normalizeProductFields = (product) => {
  if (!product) return product

  if (product.main_image && !product.mainImage) product.mainImage = product.main_image
  if (product.MAIN_IMAGE && !product.mainImage) product.mainImage = product.MAIN_IMAGE
  if (product.sub_title && !product.subTitle) product.subTitle = product.sub_title
  if (product.category_id != null && product.categoryId == null) product.categoryId = product.category_id
  if (product.product_image && !product.productImage) product.productImage = product.product_image
  if (product.create_time && !product.createTime) product.createTime = product.create_time
  if (product.update_time && !product.updateTime) product.updateTime = product.update_time

  const imagePath = resolveProductImagePath(product)
  if (imagePath) {
    product.image = imagePath
    if (!product.mainImage) product.mainImage = imagePath
    if (!product.productImage) product.productImage = imagePath
  }

  return product
}
