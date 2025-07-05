export interface UserVO {
  id: number
  username: string
  name: string
  gender: number
  phone: string
  email: string
  avatar: string
  role: string
  status: number
  createTime: string
  updateTime: string
}

export interface LoginVO {
  user: UserVO
  token: string
}

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
} 