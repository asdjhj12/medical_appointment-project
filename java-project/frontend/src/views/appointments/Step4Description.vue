<template>
  <el-form>
    <el-form-item label="症状描述">
      <el-input
        v-model="modelValue.description"
        type="textarea"
        :rows="4"
        placeholder="请简要描述您的症状"
      />
    </el-form-item>
    <el-button @click="$emit('prev')">上一步</el-button>
    <el-button type="primary" @click="$emit('next')" :disabled="!modelValue.description">下一步</el-button>
  </el-form>
</template>

<script setup>
const props = defineProps(['modelValue'])
</script> 