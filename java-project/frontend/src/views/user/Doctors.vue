<template>
  <div class="doctors">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>医生排班管理</span>
          <el-button type="primary" @click="handleAdd">新增排班</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="医生姓名">
          <el-input v-model="searchForm.name" placeholder="请输入医生姓名" />
        </el-form-item>
        <el-form-item label="科室">
          <el-select v-model="searchForm.department" placeholder="请选择科室">
            <el-option label="内科" value="内科" />
            <el-option label="外科" value="外科" />
            <el-option label="儿科" value="儿科" />
            <el-option label="妇科" value="妇科" />
            <el-option label="眼科" value="眼科" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker
            v-model="searchForm.date"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="scheduleList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="doctorName" label="医生姓名" width="120" />
        <el-table-column prop="department" label="科室" width="120" />
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="timeSlot" label="时间段" width="120" />
        <el-table-column prop="maxPatients" label="最大接诊数" width="120" />
        <el-table-column prop="currentPatients" label="已预约数" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '已满' ? 'danger' : 'success'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增排班' : '编辑排班'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="医生" prop="doctorId">
          <el-select v-model="form.doctorId" placeholder="请选择医生">
            <el-option
              v-for="doctor in doctorList"
              :key="doctor.id"
              :label="doctor.name"
              :value="doctor.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="日期" prop="date">
          <el-date-picker
            v-model="form.date"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="时间段" prop="timeSlot">
          <el-select v-model="form.timeSlot" placeholder="请选择时间段">
            <el-option label="上午 (08:00-12:00)" value="上午" />
            <el-option label="下午 (14:00-17:00)" value="下午" />
          </el-select>
        </el-form-item>
        <el-form-item label="最大接诊数" prop="maxPatients">
          <el-input-number v-model="form.maxPatients" :min="1" :max="50" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'

// 搜索表单
const searchForm = reactive({
  name: '',
  department: '',
  date: ''
})

// 表格数据
const loading = ref(false)
const scheduleList = ref([
  {
    id: 1,
    doctorName: '张医生',
    department: '内科',
    date: '2024-01-01',
    timeSlot: '上午',
    maxPatients: 20,
    currentPatients: 15,
    status: '可预约'
  },
  {
    id: 2,
    doctorName: '李医生',
    department: '外科',
    date: '2024-01-01',
    timeSlot: '下午',
    maxPatients: 15,
    currentPatients: 15,
    status: '已满'
  }
])

// 医生列表
const doctorList = ref([
  { id: 1, name: '张医生', department: '内科' },
  { id: 2, name: '李医生', department: '外科' },
  { id: 3, name: '王医生', department: '儿科' }
])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)

// 表单对话框
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const formRef = ref<FormInstance>()
const form = reactive({
  doctorId: '',
  date: '',
  timeSlot: '',
  maxPatients: 20
})

// 表单验证规则
const rules = reactive<FormRules>({
  doctorId: [
    { required: true, message: '请选择医生', trigger: 'change' }
  ],
  date: [
    { required: true, message: '请选择日期', trigger: 'change' }
  ],
  timeSlot: [
    { required: true, message: '请选择时间段', trigger: 'change' }
  ],
  maxPatients: [
    { required: true, message: '请输入最大接诊数', trigger: 'blur' }
  ]
})

// 搜索
const handleSearch = () => {
  // TODO: 实现搜索逻辑
  console.log('搜索条件：', searchForm)
}

// 重置搜索
const resetSearch = () => {
  searchForm.name = ''
  searchForm.department = ''
  searchForm.date = ''
  handleSearch()
}

// 新增排班
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  form.doctorId = ''
  form.date = ''
  form.timeSlot = ''
  form.maxPatients = 20
}

// 编辑排班
const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(form, row)
}

// 删除排班
const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    '确定要删除该排班信息吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // TODO: 实现删除逻辑
    ElMessage.success('删除成功')
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid, fields) => {
    if (valid) {
      // TODO: 实现提交逻辑
      console.log('提交表单：', form)
      ElMessage.success(dialogType.value === 'add' ? '新增成功' : '编辑成功')
      dialogVisible.value = false
    } else {
      console.log('表单验证失败：', fields)
    }
  })
}

// 分页大小改变
const handleSizeChange = (val: number) => {
  pageSize.value = val
  // TODO: 重新加载数据
}

// 当前页改变
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  // TODO: 重新加载数据
}
</script>

<style lang="scss" scoped>
.doctors {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .search-form {
    margin-bottom: 20px;
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style> 