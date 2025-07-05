import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import type { ApiResponse } from '@/types/api'

// 扩展axios，支持泛型
interface AxiosRequestConfigWithType<T = any> extends AxiosRequestConfig {
  data?: T;
}

interface CustomInstance {
  <T = any>(config: AxiosRequestConfigWithType<any>): Promise<T>;
  request<T = any>(config: AxiosRequestConfigWithType<any>): Promise<T>;
  get<T = any>(url: string, config?: AxiosRequestConfig): Promise<T>;
  delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<T>;
  head<T = any>(url: string, config?: AxiosRequestConfig): Promise<T>;
  post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T>;
  put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T>;
  patch<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T>;
}

// 创建刷新token的函数
const refreshToken = async () => {
  try {
    // 从localStorage获取refreshToken
    const refreshToken = localStorage.getItem('refreshToken')
    if (!refreshToken) {
      return null
    }
    
    // 使用refreshToken请求新token
    const response = await axios.post('http://localhost:9001/api/auth/refresh', { 
      refreshToken 
    })
    
    if (response.data.code === 200 && response.data.data?.token) {
      const newToken = response.data.data.token
      localStorage.setItem('token', newToken)
      return newToken
    }
    return null
  } catch (error) {
    console.error('刷新Token失败:', error)
    return null
  }
}

const service = axios.create({
  baseURL: 'http://localhost:9001',
  timeout: 10000
}) as unknown as CustomInstance

// 记录是否正在刷新token
let isRefreshing = false
// 存储等待刷新token的请求
let requestsWaiting: Array<(token: string) => void> = []

service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== undefined) {
      if (res.code !== 200) {
        ElMessage.error(res.message || '请求失败')
        return Promise.reject(new Error(res.message || '请求失败'))
      }
      return res.data
    } else {
      return res
    }
  },
  async (error) => {
    if (error.response) {
      const { status } = error.response
      
      if (status === 401) {
        // 避免多次刷新Token
        if (!isRefreshing) {
          isRefreshing = true
          
          // 尝试刷新Token
          const newToken = await refreshToken()
          
          if (newToken) {
            // Token刷新成功，执行队列中的请求
            requestsWaiting.forEach(callback => callback(newToken))
            requestsWaiting = []
            
            // 重试当前失败的请求
            const config = error.config
            config.headers.Authorization = `Bearer ${newToken}`
            isRefreshing = false
            return service(config)
          } else {
            // Token刷新失败，需要重新登录
            ElMessage.error('登录已过期，请重新登录')
            localStorage.removeItem('token')
            localStorage.removeItem('refreshToken')
            localStorage.removeItem('USER_INFO_KEY')
            router.push('/login')
            
            // 清空等待队列
            requestsWaiting = []
          }
          
          isRefreshing = false
        }
        
        // 将请求加入队列
        return new Promise(resolve => {
          requestsWaiting.push(token => {
            error.config.headers.Authorization = `Bearer ${token}`
            resolve(service(error.config))
          })
        })
      } else if (status === 403) {
        ElMessage.error('无权限访问')
      } else if (status === 404) {
        ElMessage.error('请求的资源不存在')
      } else {
        ElMessage.error(error.response.data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请稍后重试')
    }
    return Promise.reject(error)
  }
)

// 直接导出 Axios 实例
export default service 