<template>
  <div class="user-layout">
    <div class="user-navbar">
      <div class="user-navbar-right">
        <button class="home-btn" @click="goHome">
          首页
        </button>
        <el-dropdown trigger="click">
          <span class="el-dropdown-link">
            <el-avatar :size="32" :src="logo" style="vertical-align: middle; margin: 0 8px;" />
            {{ userInfo.realName || userInfo.username || '用户' }}
            <el-icon class="el-icon--right"><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleLogout">
                <el-icon style="vertical-align: middle; margin-right: 4px;"><ArrowRight /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
    <router-view />
  </div>
</template>

<script setup>
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { ArrowDown, ArrowRight } from '@element-plus/icons-vue'
import { computed } from 'vue'
import logo from '@/assets/logo.png'

const userStore = useUserStore()
const router = useRouter()
const userInfo = computed(() => userStore.userInfo || {})

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.replace('/login')
  })
}

const goHome = () => {
  router.push('/user/dashboard')
}
</script>

<style scoped>
.user-layout {
  min-height: 100vh;
  background: #f5f7fa;
}
.user-navbar {
  width: 100%;
  height: 60px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 30px;
}
.user-navbar-right {
  display: flex;
  align-items: center;
}
.home-btn {
  background: none;
  border: none;
  outline: none;
  font-size: 22px;
  font-weight: bold;
  color: #409EFF;
  cursor: pointer;
  padding: 0 18px 0 0;
  transition: color 0.2s;
  letter-spacing: 2px;
}
.home-btn:hover {
  color: #1867c0;
}
.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
  display: flex;
  align-items: center;
  font-size: 16px;
}
.el-dropdown-menu {
  min-width: 120px;
}
</style> 