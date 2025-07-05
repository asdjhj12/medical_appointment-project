<template>
  <el-container style="height: 100vh;">
    <el-aside width="200px" style="background: #263445; color: #fff;">
      <div style="height: 60px; display: flex; align-items: center; justify-content: center; font-size: 20px; font-weight: bold; border-bottom: 1px solid #222;">医生预约系统</div>
      <el-menu :default-active="$route.path" router background-color="#263445" text-color="#fff" active-text-color="#409EFF" style="border: none;">
        <el-menu-item index="/doctor/appointment">
          <i class="el-icon-date"></i>
          <span>预约管理</span>
        </el-menu-item>
        <el-menu-item index="/doctor/medical-record">
          <i class="el-icon-document"></i>
          <span>病历管理</span>
        </el-menu-item>
        <el-menu-item index="/doctor/profile">
          <i class="el-icon-user"></i>
          <span>个人信息</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background:#409EFF;color:#fff;display:flex;justify-content:space-between;align-items:center;">
        <div style="font-size:18px;">{{ $route.meta.title || '医生端' }}</div>
        <el-dropdown @command="handleDropdownCommand">
          <span style="display:flex;align-items:center;cursor:pointer;">
            <span style="margin-right:10px;">{{ userInfo.realName || userInfo.username || '医生' }}</span>
            <el-avatar :size="32" :src="doctorAvatar" style="background:#fff;color:#409EFF;">{{ (userInfo.realName || userInfo.username || '医')[0] }}</el-avatar>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main style="background:#f5f7fa;">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useUserStore } from '@/store/user'
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import doctorAvatar from '@/assets/doctor.jpg'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo || {})
const router = useRouter()
const handleDropdownCommand = async (command) => {
  if (command === 'logout') {
    await userStore.logout()
    router.replace('/login')
  }
}
</script> 