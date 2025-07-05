<template>
  <el-form>
    <el-form-item label="科室">
      <el-select v-model="modelValue.departmentId" placeholder="请选择科室">
        <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.id" />
      </el-select>
    </el-form-item>
    <el-button type="primary" @click="$emit('next')" :disabled="!modelValue.departmentId">下一步</el-button>
  </el-form>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listAllDepartments } from '@/api/department'
const props = defineProps(['modelValue'])
const departments = ref([])
onMounted(async () => {
  departments.value = await listAllDepartments()
})
</script> 