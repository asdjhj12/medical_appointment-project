<template>
  <div class="doctor-appointment-list">
    <h2>预约管理</h2>
    <el-table :data="appointmentList" v-loading="loading">
      <el-table-column prop="appointmentNumber" label="预约编号" width="180" />
      <el-table-column prop="userName" label="患者姓名" width="120" />
      <el-table-column prop="departmentName" label="科室" width="120" />
      <el-table-column prop="scheduleDate" label="预约日期" width="120" />
      <el-table-column prop="periodText" label="预约时段" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="viewAppointment(row)">查看</el-button>
          <el-button type="success" size="small" @click="handleComplete(row)" v-if="row.status === 0">完成预约</el-button>
        </template>
      </el-table-column>
    </el-table>
    <AppointmentDetail
      v-model:visible="detailVisible"
      :appointment="currentAppointment"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getByDoctorId, updateAppointmentStatus } from '@/api/appointment'
import { getDoctorByUserId } from '@/api/doctor'
import { useUserStore } from '@/store/user'
import AppointmentDetail from './components/AppointmentDetail.vue'

const loading = ref(false)
const appointmentList = ref([])
const detailVisible = ref(false)
const currentAppointment = ref(null)

const getStatusType = (status) => {
  const typeMap = {
    0: 'info',
    1: 'primary',
    2: 'success',
    3: 'danger',
    4: 'warning'
  }
  return typeMap[status] || 'info'
}
const getStatusText = (status) => {
  const textMap = {
    0: '待就诊',
    1: '已确认',
    2: '已完成',
    3: '已取消',
    4: '已过期'
  }
  return textMap[status] || status
}

const viewAppointment = (row) => {
  currentAppointment.value = row
  detailVisible.value = true
}

const handleComplete = (row) => {
  ElMessageBox.confirm(
    '确定要将该预约标记为已完成吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await updateAppointmentStatus(row.id, 2)
      ElMessage.success('操作成功')
      // 刷新列表
      const userStore = useUserStore()
      const userId = userStore.userInfo?.id
      let doctorId = null
      let doctorRes = null
      if (userId) {
        doctorRes = await getDoctorByUserId(userId)
        if (doctorRes && doctorRes.id) {
          doctorId = doctorRes.id
        }
      }
      if (doctorId) {
        const res = await getByDoctorId(doctorId)
        appointmentList.value = res
      }
    } catch (error) {
      ElMessage.error('操作失败')
    }
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}

onMounted(async () => {
  loading.value = true
  try {
    const userStore = useUserStore()
    const userId = userStore.userInfo?.id
    let doctorId = null
    let doctorRes = null
    if (userId) {
      doctorRes = await getDoctorByUserId(userId)
      console.log('doctorRes', doctorRes)
      if (doctorRes && doctorRes.id) {
        doctorId = doctorRes.id
      }
    }
    console.log('最终doctorId', doctorId)
    if (doctorId) {
      const res = await getByDoctorId(doctorId)
      console.log('getByDoctorId返回', res)
      appointmentList.value = res
      console.log('直接赋值的appointmentList', appointmentList.value)
    }
  } finally {
    loading.value = false
  }
})

</script>

<style scoped>
.doctor-appointment-list {
  padding: 20px;
}
</style>