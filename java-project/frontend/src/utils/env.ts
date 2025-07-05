/**
 * 获取环境变量
 * @param key 环境变量键名
 * @returns 环境变量值
 */
export const getEnv = (key: string): string => {
  return import.meta.env[key] || ''
}

/**
 * 获取应用标题
 */
export const getAppTitle = (): string => {
  return getEnv('VITE_APP_TITLE')
}

/**
 * 获取API基础路径
 */
export const getApiBaseUrl = (): string => {
  return getEnv('VITE_API_BASE_URL')
}

/**
 * 获取上传文件大小限制
 */
export const getUploadSizeLimit = (): number => {
  return Number(getEnv('VITE_UPLOAD_SIZE_LIMIT')) || 5
}

/**
 * 获取上传文件类型限制
 */
export const getUploadTypeLimit = (): string[] => {
  const types = getEnv('VITE_UPLOAD_TYPE_LIMIT')
  return types ? types.split(',') : []
}

/**
 * 获取Token存储键名
 */
export const TOKEN_KEY = 'token'

/**
 * 获取用户信息存储键名
 */
export const USER_INFO_KEY = 'userInfo'

/**
 * 是否启用Mock数据
 */
export const USE_MOCK = getEnv('VITE_USE_MOCK') === 'true'

/**
 * 是否启用代理
 */
export const USE_PROXY = getEnv('VITE_USE_PROXY') === 'true'

/**
 * 获取代理目标地址
 */
export const PROXY_TARGET = getEnv('VITE_PROXY_TARGET')

/**
 * 获取生产环境API地址
 */
export const API_URL = getEnv('VITE_API_URL') 