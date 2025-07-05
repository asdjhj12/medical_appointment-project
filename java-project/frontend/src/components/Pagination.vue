<template>
  <div class="pagination-container" v-if="total > 0">
    <el-pagination
      background
      layout="total, prev, pager, next, sizes, jumper"
      :total="total"
      :current-page="page"
      :page-size="limit"
      :page-sizes="[10, 20, 50, 100]"
      @update:current-page="handlePageChange"
      @update:page-size="handleSizeChange"
    />
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, watch } from 'vue';

const props = defineProps({
  total: {
    type: Number,
    required: true
  },
  page: {
    type: Number,
    required: true
  },
  limit: {
    type: Number,
    required: true
  }
});
const emit = defineEmits(['update:page', 'update:limit', 'pagination']);

function handlePageChange(val: number) {
  emit('update:page', val);
  emit('pagination');
}
function handleSizeChange(val: number) {
  emit('update:limit', val);
  emit('pagination');
}
</script>

<style scoped>
.pagination-container {
  margin: 20px 0;
  text-align: right;
}
</style> 