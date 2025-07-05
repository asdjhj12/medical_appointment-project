<template>
  <!-- 顶部导航链接 -->
  <div class="top-header">
    <div class="container">
      <div class="logo-text">医疗预约系统</div>
      <div class="top-nav-links">
        <router-link to="/dashboard" class="nav-link">首页</router-link>
        <router-link to="/my-appointments" class="nav-link">我的预约</router-link>
        <router-link to="/my-records" class="nav-link">我的病历</router-link>
      </div>
      <div class="user-info" v-if="userStore.isLoggedIn()">
        <span>{{ userStore.userInfo?.name }}</span>
      </div>
    </div>
  </div>

  <div class="app-wrapper">
    <!-- 侧边栏 -->
    <div class="sidebar-container">
      <div class="logo">
        <img src="@/assets/logo.png" alt="logo" />
        <span>医疗预约系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        :collapse="isCollapse"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Monitor /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>

        <el-sub-menu index="/appointment">
          <template #title>
            <el-icon><Calendar /></el-icon>
            <span>预约管理</span>
          </template>
          <el-menu-item index="/appointment/list">预约列表</el-menu-item>
          <el-menu-item index="/appointment/schedule">排班管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/patient">
          <template #title>
            <el-icon><User /></el-icon>
            <span>患者管理</span>
          </template>
          <el-menu-item index="/patient/list">患者列表</el-menu-item>
          <el-menu-item index="/patient/record">病历管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/doctor">
          <template #title>
            <el-icon><UserFilled /></el-icon>
            <span>医生管理</span>
          </template>
          <el-menu-item index="/doctor/list">医生列表</el-menu-item>
          <el-menu-item index="/doctor/schedule">排班管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/department">
          <template #title>
            <el-icon><OfficeBuilding /></el-icon>
            <span>科室管理</span>
          </template>
          <el-menu-item index="/department/list">科室列表</el-menu-item>
          <el-menu-item index="/department/doctor">医生分配</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/system" v-if="userStore.userInfo?.role === 'ADMIN'">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/user">用户管理</el-menu-item>
          <el-menu-item index="/system/role">角色管理</el-menu-item>
          <el-menu-item index="/system/menu">菜单管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </div>

    <!-- 主容器 -->
    <div class="main-container">
      <!-- 顶部导航栏 -->
      <div class="navbar">
        <div class="left">
          <el-icon class="fold-btn" @click="toggleSidebar">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <breadcrumb />
        </div>
         
        <div class="right">
          <el-dropdown trigger="click">
            <div class="avatar-wrapper">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar" />
              <span class="username">{{ userStore.userInfo?.name }}</span>
              <el-icon><CaretBottom /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goToProfile">
                  <el-icon><User /></el-icon>个人信息
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 主内容区域 -->
      <div class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <keep-alive>
              <component :is="Component" />
            </keep-alive>
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import Breadcrumb from './components/Breadcrumb.vue'
import { User, CaretBottom, SwitchButton, Fold, Expand, UserFilled, OfficeBuilding, Setting } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 侧边栏折叠状态
const isCollapse = ref(false)
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path
})

// 跳转到个人信息页
const goToProfile = () => {
  router.push('/profile')
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login')
  })
}
</script>

<style scoped>
.top-header {
  width: 100%;
  height: 40px;
  background-color: #409EFF;
  color: white;
  position: relative;
  z-index: 1000;
}

.top-header .container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 15px;
}

.logo-text {
  font-weight: bold;
  font-size: 16px;
}

.top-header .top-nav-links {
  display: flex;
}

.top-header .nav-link {
  color: white;
  padding: 0 15px;
  text-decoration: none;
  font-size: 14px;
  height: 40px;
  line-height: 40px;
  transition: all 0.3s;
}

.top-header .nav-link:hover, 
.top-header .nav-link.router-link-active {
  background-color: rgba(255, 255, 255, 0.2);
}

.top-header .user-info {
  font-size: 14px;
}

.app-wrapper {
  display: flex;
  width: 100%;
  height: calc(100vh - 40px); /* 减去顶部导航的高度 */
}

.sidebar-container {
  width: 210px;
  height: 100%;
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.sidebar-container.collapse {
  width: 64px;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}

.logo img {
  width: 32px;
  height: 32px;
  margin-right: 12px;
}

.sidebar-menu {
  border: none;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.navbar {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.left {
  display: flex;
  align-items: center;
  width: 25%;
}

.right {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  width: 25%;
}

.fold-btn {
  font-size: 20px;
  cursor: pointer;
  margin-right: 16px;
}

.avatar-wrapper {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin: 0 8px;
  font-size: 14px;
}

.app-main {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background-color: #f0f2f5;
}

/* 路由切换动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style> 