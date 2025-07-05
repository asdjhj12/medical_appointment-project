// 用户信息
export interface UserInfo {
  id: number
  username: string
  name: string
  realName?: string
  role: string
  avatar?: string
  phone?: string
  email?: string
  gender?: number
  status: number
  createTime: string
  updateTime: string
}

// 登录参数
export interface LoginParams {
  username: string
  password: string
}

// 登录响应
export interface LoginResult {
  token: string
  user: UserInfo
}

// 修改密码参数
export interface UpdatePasswordParams {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

// 更新用户信息参数
export interface UpdateUserInfoParams {
  realName: string
  gender: number
  phone: string
  email: string
}

// 用户角色枚举
export enum UserRole {
  ADMIN = 'ADMIN',
  DOCTOR = 'DOCTOR',
  USER = 'USER'
}

// 用户角色选项
export const userRoleOptions = [
  { label: '管理员', value: 'ADMIN' },
  { label: '医生', value: 'DOCTOR' },
  { label: '普通用户', value: 'USER' }
]

// 用户状态选项
export const userStatusOptions = [
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 }
] 