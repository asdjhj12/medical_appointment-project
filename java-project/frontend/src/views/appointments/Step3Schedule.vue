<template>
  <div style="padding: 40px;">
    <h2 style="color:blue">可预约排班时间</h2>
    
    <!-- 添加加载状态 -->
    <div v-if="loading" style="color: #999;">加载中...</div>
    
    <!-- 显示排班内容 -->
    <div v-else>
      <div v-if="scheduleList.length > 0">
        <div
          v-for="item in scheduleList"
          :key="item.id"
          :style="{
            marginBottom: '12px',
            fontSize: '16px',
            color: selectedId === item.id ? '#409EFF' : '#333',
            cursor: 'pointer',
            border: selectedId === item.id ? '1px solid #409EFF' : '1px solid #eee',
            padding: '8px',
            borderRadius: '4px',
            background: selectedId === item.id ? '#f0f7ff' : '#fff'
          }"
          @click="selectSchedule(item)"
        >
          {{ item.scheduleDate }} {{ item.periodText || periodMap[item.period] || '' }}
        </div>
      </div>
      <div v-else style="color: #999;">该医生暂无排班</div>
    </div>

    <div style="margin-top: 30px;">
      <el-button @click="$emit('prev')">上一步</el-button>
      <el-button 
        type="primary" 
        @click="$emit('next')" 
        :disabled="!selectedId"
      >
        下一步
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, watchEffect } from 'vue'
import { getScheduleList } from '@/api/schedule'

const props = defineProps(['modelValue'])
const scheduleList = ref([])
const loading = ref(false)
const periodMap = { 1: '上午', 2: '下午', 3: '晚上' }
const selectedId = ref(props.modelValue.scheduleId || '')

function selectSchedule(item) {
  selectedId.value = item.id
  props.modelValue.scheduleId = item.id // 关键：写入v-model
}

watchEffect(async () => {
  if (props.modelValue.doctorId) {
    try {
      loading.value = true
      console.log('请求参数 doctorId:', props.modelValue.doctorId)
      const res = await getScheduleList({ doctorId: props.modelValue.doctorId })
      console.log('接口完整响应:', res)
      let records = []
      if (res?.data?.data?.records) {
        records = res.data.data.records
      } else if (res?.data?.records) {
        records = res.data.records
      } else if (res?.records) {
        records = res.records
      }
      scheduleList.value = Array.isArray(records) ? records : []
      console.log('最终用于渲染的scheduleList:', scheduleList.value)
    } catch (error) {
      console.error('获取排班失败:', error)
      scheduleList.value = []
    } finally {
      loading.value = false
    }
  } else {
    scheduleList.value = []
  }
})
</script>