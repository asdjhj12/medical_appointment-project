import { defineStore } from 'pinia'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { UserInfo, LoginParams, UpdatePasswordParams, UpdateUserInfoParams } from '@/types/user'
import { TOKEN_KEY, USER_INFO_KEY } from '@/utils/env'
import { login, getUserInfo, updatePassword, updateUserInfo } from '@/api/user'


export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>(localStorage.getItem(TOKEN_KEY) || '')
  const userInfo = ref<UserInfo | null>(null)
  
  // 初始化时从localStorage加载用户信息
  try {
    const storedUserInfo = localStorage.getItem(USER_INFO_KEY)
    if (storedUserInfo) {
      userInfo.value = JSON.parse(storedUserInfo)
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
    localStorage.removeItem(USER_INFO_KEY)
  }

  // 登录
  const loginAction = async (params: LoginParams) => {
    try {
      const data = await login(params) // 直接获取业务数据
      token.value = data.token
      userInfo.value = data.user
      localStorage.setItem(TOKEN_KEY, data.token)
      localStorage.setItem(USER_INFO_KEY, JSON.stringify(data.user))
    } catch (error: any) {
      ElMessage.error(error.message || '登录失败') // 使用 error.message
      throw error
    }
  }

  // 获取用户信息
  const getUserInfoAction = async () => {
    try {
      const data = await getUserInfo() // 直接获取业务数据
      userInfo.value = data
      localStorage.setItem(USER_INFO_KEY, JSON.stringify(data))
    } catch (error: any) {
      console.error('获取用户信息失败:', error)
      ElMessage.error(error.message || '获取用户信息失败') // 使用 error.message
      throw error
    }
  }

  // 更新密码
  const updatePasswordAction = async (params: UpdatePasswordParams) => {
    try {
      await updatePassword(params) // updatePassword 返回 Promise<void>，无需处理返回值
      ElMessage.success('修改密码成功');
    } catch (error: any) {
      ElMessage.error(error.message || '修改密码失败') // 使用 error.message
      throw error
    }
  }

  // 更新用户信息
  const updateUserInfoAction = async (params: UpdateUserInfoParams) => {
    try {
      const data = await updateUserInfo(params) // 直接获取业务数据
      userInfo.value = data
      localStorage.setItem(USER_INFO_KEY, JSON.stringify(data))
      ElMessage.success('更新用户信息成功');
    } catch (error: any) {
      ElMessage.error(error.message || '更新用户信息失败') // 使用 error.message
      throw error
    }
  }

  // 退出登录
  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_INFO_KEY)
  }

  const isLoggedIn = () => {
    return !!token.value;
  };

  return {
    token,
    userInfo,
    login: loginAction,
    getUserInfo: getUserInfoAction,
    updatePassword: updatePasswordAction,
    updateUserInfo: updateUserInfoAction,
    logout,
    isLoggedIn
  }
}) 