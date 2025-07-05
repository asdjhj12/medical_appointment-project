<template>
  <el-dialog v-model="visible" title="预约详情" width="500px">
    <div v-if="appointment">
      <p><strong>预约编号：</strong>{{ appointment.appointmentNumber }}</p>
      <p><strong>患者姓名：</strong>{{ appointment.userName }}</p>
      <p><strong>医生姓名：</strong>{{ appointment.doctorName }}</p>
      <p><strong>科室：</strong>{{ appointment.departmentName }}</p>
      <p><strong>排班时间：</strong>{{ appointment.scheduleDate }} {{ appointment.periodText }}</p>
      <p><strong>状态：</strong>{{ getStatusText(appointment.status) }}</p>
      <p><strong>预约时间：</strong>{{ appointment.createTime }}</p>
      <p><strong>症状描述：</strong>{{ appointment.description }}</p>
      <!-- 可根据实际需求补充更多字段 -->
    </div>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
const props = defineProps({
  visible: Boolean,
  appointment: Object
});
const emit = defineEmits(['update:visible']);

// 双向绑定 visible
const visible = ref(props.visible);
watch(() => props.visible, val => visible.value = val);
watch(visible, val => emit('update:visible', val));

// 状态文本方法
function getStatusText(status: string) {
  const statusMap: Record<string, string> = {
    PENDING: '待处理',
    CONFIRMED: '已确认',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
    EXPIRED: '已过期',
  };
  return statusMap[status] || status;
}
// 状态标签类型方法（如有需要可用）
function getStatusTagType(status: string) {
  const typeMap: Record<string, string> = {
    PENDING: 'info',
    CONFIRMED: '',
    COMPLETED: 'success',
    CANCELLED: 'danger',
    EXPIRED: 'warning',
  };
  return typeMap[status] || '';
}
</script> 