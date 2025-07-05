import request from '../utils/request'
import type { UserInfo, LoginParams, UpdatePasswordParams, UpdateUserInfoParams } from '@/types/user'

// 注册参数
export interface RegisterParams {
  username: string;
  password: string;
  confirmPassword: string;
  phone: string;
  verificationCode?: string;
  email?: string;
  role: string;
  realName?: string;
  gender?: number;
  birthDate?: string;
  idCard?: string;
  address?: string;
}

// 用户查询参数
export interface UserQueryParams {
  pageNum?: number;
  pageSize?: number;
  username?: string;
  name?: string;
  phone?: string;
  role?: string;
  status?: number;
}

// 用户更新参数
export interface UserUpdateParams {
  username: string;
  password?: string;
  realName: string;
  gender: number;
  phone: string;
  email?: string;
  avatar?: string;
  role: string;
  status: number;
}

// 登录
export const login = (data: LoginParams): Promise<{ token: string; user: UserInfo }> => {
  return request.post('/api/users/login', data)
}

// 用户注册
export const register = (data: RegisterParams): Promise<void> => {
  return request.post('/api/users/register', data);
}

// 获取用户信息
export const getUserInfo = (): Promise<UserInfo> => {
  return request.get('/api/users/info')
}

// 修改密码
export const updatePassword = (data: UpdatePasswordParams): Promise<void> => {
  return request.put('/api/users/password', data)
}

// 更新用户信息
export const updateUserInfo = (data: UpdateUserInfoParams): Promise<UserInfo> => {
  return request.put('/api/users/info', data)
}

// 发送验证码参数
export interface SendVerificationCodeParams {
  phone: string;
  type: number; // 1-注册, 2-找回密码等
}

// 发送验证码
export const sendVerificationCode = (data: SendVerificationCodeParams): Promise<void> => {
  return request.post('/api/verification-code/send', data);
}

// 获取用户列表
export const getUserList = (params: UserQueryParams): Promise<{ records: UserInfo[]; total: number }> => {
  return request.get('/api/admin-users', { params })
}

// 获取用户详情
export const getUserById = (id: number): Promise<UserInfo> => {
  return request.get(`/api/admin-users/${id}`)
}

// 创建用户
export const createUser = (data: UserUpdateParams): Promise<void> => {
  return request.post('/api/admin-users', data)
}

// 更新用户
export const updateUser = (id: number, data: UserUpdateParams): Promise<void> => {
  return request.put(`/api/admin-users/${id}`, data)
}

// 删除用户
export const deleteUser = (id: number): Promise<void> => {
  return request.delete(`/api/admin-users/${id}`)
}

// 重置密码
export const resetPassword = (id: number): Promise<void> => {
  return request.put(`/api/admin-users/${id}/password/reset`)
}

// 更新用户状态
export const updateUserStatus = (id: number, status: number): Promise<void> => {
  return request.put(`/api/admin-users/${id}/status?status=${status}`)
}

// 通过ID获取用户详情（普通用户API）
export const getUserDetailById = (id: number): Promise<any> => {
  return request.get(`/api/users/${id}`)
}