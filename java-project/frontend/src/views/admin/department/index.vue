<template>
  <div class="admin-department">
    <h2>科室管理</h2>
    <el-card>
      <el-button type="primary" @click="openAddDialog" style="margin-bottom: 16px;">新增科室</el-button>
      <el-button @click="fetchDepartments" style="margin-bottom: 16px; margin-left: 8px;">刷新</el-button>
      <el-table :data="departmentList" v-loading="loading" style="width: 100%; margin-top: 8px;">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="科室名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑科室' : '新增科室'" width="400px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="科室名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入科室名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { listAllDepartments } from '@/api/department'
import { ElMessage, ElMessageBox } from 'element-plus'
// 假设你有如下API
import request from '@/utils/request'

const departmentList = ref([])
const loading = ref(false)

const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({ id: null, name: '', description: '', status: 1 })
const formRef = ref()

const rules = {
  name: [{ required: true, message: '请输入科室名称', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const fetchDepartments = async () => {
  loading.value = true
  try {
    const res = await listAllDepartments()
    if (res.code === 200 && res.data) {
      departmentList.value = res.data
    } else if (Array.isArray(res)) {
      departmentList.value = res
    } else {
      departmentList.value = []
      ElMessage.warning('未获取到科室数据')
    }
  } catch (e) {
    ElMessage.error('获取科室列表失败')
    departmentList.value = []
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  isEdit.value = false
  Object.assign(form, { id: null, name: '', description: '', status: 1 })
  dialogVisible.value = true
}

const openEditDialog = (row: any) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = () => {
  formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        // 编辑
        await request.put(`/api/departments/${form.id}`, form)
        ElMessage.success('编辑成功')
      } else {
        // 新增
        await request.post('/api/departments', form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      fetchDepartments()
    } catch (e) {
      ElMessage.error(isEdit.value ? '编辑失败' : '新增失败')
    }
  })
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除科室"${row.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/api/departments/${row.id}`)
      ElMessage.success('删除成功')
      fetchDepartments()
    } catch (e) {
      ElMessage.error('删除失败')
    }
  })
}

onMounted(fetchDepartments)
</script>
<style scoped>
.admin-department {
  padding: 20px;
}
</style> 