<template>
  <div>
    <el-descriptions title="预约信息确认" :column="1" border>
      <el-descriptions-item label="科室">{{ departmentName }}</el-descriptions-item>
      <el-descriptions-item label="医生">{{ doctorName }}</el-descriptions-item>
      <el-descriptions-item label="排班时间">{{ scheduleText }}</el-descriptions-item>
      <el-descriptions-item label="症状描述">{{ modelValue.description }}</el-descriptions-item>
    </el-descriptions>
    <div style="margin-top: 20px;">
      <el-button @click="$emit('prev')">上一步</el-button>
      <el-button type="primary" @click="$emit('submit')">确认预约</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { listDoctorsByDepartment } from '@/api/doctor'
import { getDepartmentList } from '@/api/department'
import { getScheduleList } from '@/api/schedule'
const props = defineProps(['modelValue'])

const departmentName = ref('')
const doctorName = ref('')
const scheduleText = ref('')

function getArrayFromRes(res) {
  if (Array.isArray(res)) return res
  if (Array.isArray(res?.data?.records)) return res.data.records
  if (Array.isArray(res?.data)) return res.data
  if (Array.isArray(res?.records)) return res.records
  return []
}

watch(
  () => props.modelValue,
  async (val) => {
    // 获取科室名
    if (val.departmentId) {
      const res = await getDepartmentList()
      const deptList = getArrayFromRes(res)
      const dept = deptList.find(d => d.id === val.departmentId)
      departmentName.value = dept ? dept.name : ''
    }
    // 获取医生名
    if (val.doctorId) {
      const res = await listDoctorsByDepartment(val.departmentId)
      const docList = getArrayFromRes(res)
      console.log('doctorId:', val.doctorId, 'docList:', docList)
      const doc = docList.find(d => String(d.id) === String(val.doctorId))
      doctorName.value = doc ? doc.userName : ''
    }
    // 获取排班信息
    if (val.doctorId && val.scheduleId) {
      const res = await getScheduleList({ doctorId: val.doctorId })
      const schList = getArrayFromRes(res)
      const sch = schList.find(s => s.id === val.scheduleId)
      if (sch) {
        const periodMap = { 1: '上午', 2: '下午', 3: '晚上' }
        scheduleText.value = sch.scheduleDate + ' ' + (sch.periodText || periodMap[sch.period] || '')
      } else {
        scheduleText.value = ''
      }
    } else {
      scheduleText.value = ''
    }
  },
  { immediate: true, deep: true }
)
</script> 