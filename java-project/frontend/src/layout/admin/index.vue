<template>
  <div class="admin-layout">
    <div class="admin-sidebar">
      <div class="logo-container">
        <img :src="avatarSrc" alt="Logo" class="logo">
        <h2>医疗预约系统</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        :collapse="isCollapse"
        router
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><Monitor /></el-icon>
          <span>控制台</span>
        </el-menu-item>
        <el-menu-item index="/admin/user">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/department">
          <el-icon><OfficeBuilding /></el-icon>
          <span>科室管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/doctor">
          <el-icon><UserFilled /></el-icon>
          <span>医生管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/schedule">
          <el-icon><Calendar /></el-icon>
          <span>排班管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/appointment">
          <el-icon><Document /></el-icon>
          <span>预约管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/medical-record">
          <el-icon><Notebook /></el-icon>
          <span>电子病历管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/medicine-inventory">
          <el-icon><Box /></el-icon>
          <span>药品库存管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/medicine-stock-record">
          <el-icon><List /></el-icon>
          <span>药品库存记录</span>
        </el-menu-item>
      </el-menu>
    </div>
    
    <div class="admin-container">
      <div class="admin-navbar">
        <div class="left">
          <el-icon class="fold-btn" @click="toggleSidebar">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
        </div>
        <div class="right">
          <el-dropdown trigger="click">
            <span class="el-dropdown-link">
              <el-avatar :size="32" :src="avatarSrc" style="vertical-align: middle; margin: 0 8px;" />
              {{ userInfo.realName || userInfo.username || '管理员' }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
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
      
      <div class="admin-main">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessageBox } from 'element-plus'
import { 
  ArrowDown, 
  ArrowRight, 
  Monitor, 
  OfficeBuilding, 
  UserFilled, 
  Calendar, 
  Document, 
  Notebook,
  Fold,
  Expand,
  Box,
  List,
  User
} from '@element-plus/icons-vue'
import logo from '@/assets/logo.png'
import adminAvatar from '@/assets/admin.jpg'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo || {})

// 侧边栏折叠状态
const isCollapse = ref(false)

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path
})

// 头像显示逻辑
const avatarSrc = computed(() => userInfo.value.role === 'ADMIN' ? adminAvatar : logo)

// 切换侧边栏折叠状态
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

// 退出登录
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
</script>

<style scoped>
.admin-layout {
  display: flex;
  height: 100vh;
}

.admin-sidebar {
  background-color: #304156;
  transition: width 0.3s;
  width: 210px;
  height: 100%;
  overflow-y: auto;
}

.logo-container {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 15px;
  background-color: #263445;
}

.logo {
  width: 32px;
  height: 32px;
  margin-right: 10px;
}

.logo-container h2 {
  color: #fff;
  font-size: 18px;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 210px;
}

.admin-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.admin-navbar {
  height: 60px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 15px;
}

.left {
  display: flex;
  align-items: center;
}

.fold-btn {
  font-size: 20px;
  cursor: pointer;
  margin-right: 15px;
}

.right {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  font-size: 16px;
}

.admin-main {
  padding: 20px;
  height: calc(100vh - 60px);
  overflow-y: auto;
  background-color: #f5f7fa;
}
</style> 