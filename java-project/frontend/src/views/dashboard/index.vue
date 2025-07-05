<template>
  <div class="dashboard-container">
    <el-card class="welcome-card">
      <template #header>
        <div class="card-header">
          <h2>欢迎使用医疗预约系统</h2>
        </div>
      </template>
      <div class="welcome-content">
        <p class="greeting">尊敬的 {{ userStore.userInfo?.realName || userStore.userInfo?.name || '用户' }}，欢迎您！</p>
        
        <div class="quick-actions">
          <el-row :gutter="24" class="feature-list" style="width: 100%; flex-wrap: wrap;">
            <el-col :xs="24" :sm="12" :md="8">
              <el-card shadow="hover" @click="showStepAppointment = true">
                <el-icon><Plus /></el-icon>
                <h4>立即预约</h4>
                <p>快速发起新的预约</p>
              </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :md="8">
              <el-card shadow="hover" @click="router.push('/user/appointment')">
                <el-icon><Calendar /></el-icon>
                <h4>我的预约</h4>
                <p>查看和管理您的预约</p>
              </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :md="8">
              <el-card shadow="hover" @click="router.push('/user/medical-record')">
                <el-icon><Document /></el-icon>
                <h4>我的病历</h4>
                <p>查看您的就诊记录</p>
              </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :md="8">
              <el-card shadow="hover" @click="router.push('/user/profile')">
                <el-icon><User /></el-icon>
                <h4>个人信息</h4>
                <p>管理您的个人资料</p>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </div>
    </el-card>

    <el-dialog v-model="showStepAppointment" title="分步预约" width="600px" :close-on-click-modal="false">
      <StepAppointment />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { Calendar, Document, User, Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { ref } from 'vue'
import { createAppointment } from '@/api/appointment'
import { listAllDoctors } from '@/api/doctor'
import { getScheduleList } from '@/api/schedule'
import { ElMessage } from 'element-plus'
import StepAppointment from '@/views/appointments/StepAppointment.vue'

const router = useRouter()
const userStore = useUserStore()

const showStepAppointment = ref(false)

function fetchDoctors() {
  listAllDoctors().then(res => {
    if (res.code === 200) {
      doctorList.value = res.data.records || res.data
    }
  })
}
function fetchSchedules(doctorId: number) {
  getScheduleList({ doctorId }).then(res => {
    if (res.code === 200) {
      scheduleList.value = (res.data.records || res.data).map(item => ({
        id: item.id,
        time: item.scheduleTime || item.scheduleDate || item.id
      }))
    }
  })
}
function handleCreateAppointment() {
  // 假设用户ID从 userStore 获取
  const userId = userStore.userInfo?.id
  if (!createForm.value.doctorId || !createForm.value.scheduleId) {
    ElMessage.error('请选择医生和排班时间')
    return
  }
  createAppointment({
    userId,
    doctorId: createForm.value.doctorId,
    scheduleId: createForm.value.scheduleId,
    description: createForm.value.description
  }).then(res => {
    if (res.code === 200) {
      ElMessage.success('预约成功')
      createDialogVisible.value = false
    } else {
      ElMessage.error(res.msg || '预约失败')
    }
  })
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: center;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.welcome-content {
  padding: 20px 0;
}

.greeting {
  font-size: 18px;
  margin-bottom: 40px;
  text-align: center;
  color: #606266;
}

.quick-actions {
  margin-top: 20px;
}

.feature-list {
  margin-top: 20px;
}

.feature-list .el-card {
  height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 20px;
  border-radius: 8px;
}

.feature-list .el-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.feature-list .el-icon {
  font-size: 50px;
  margin: 15px 0;
  color: #409EFF;
}

.feature-list h4 {
  margin: 15px 0 10px;
  color: #303133;
  font-size: 18px;
}

.feature-list p {
  color: #606266;
  font-size: 14px;
}

@media (max-width: 768px) {
  .feature-list .el-card {
    height: 180px;
  }
  
  .feature-list .el-icon {
    font-size: 40px;
  }
}
</style> 