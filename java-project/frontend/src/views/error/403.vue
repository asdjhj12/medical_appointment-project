<template>
  <div class="error-container">
    <el-result
      icon="warning"
      title="403"
      sub-title="抱歉，您没有权限访问此页面"
    >
      <template #extra>
        <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup lang="ts">
// 403页面
</script>

<style scoped>
.error-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style> 