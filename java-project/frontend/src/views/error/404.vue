<template>
  <div class="error-container">
    <el-result
      icon="warning"
      title="404"
      sub-title="抱歉，您访问的页面不存在"
    >
      <template #extra>
        <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup lang="ts">
// 404页面
</script>

<style scoped>
.error-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style> 