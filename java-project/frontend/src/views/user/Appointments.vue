<template>
  <div class="appointments">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>预约挂号管理</span>
          <el-button type="primary" @click="handleAdd">新增预约</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="患者姓名">
          <el-input v-model="searchForm.patientName" placeholder="请输入患者姓名" />
        </el-form-item>
        <el-form-item label="医生姓名">
          <el-input v-model="searchForm.doctorName" placeholder="请输入医生姓名" />
        </el-form-item>
        <el-form-item label="预约日期">
          <el-date-picker
            v-model="searchForm.date"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态">
            <el-option label="待就诊" value="待就诊" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已取消" value="已取消" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="appointmentList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="patientName" label="患者姓名" width="120" />
        <el-table-column prop="doctorName" label="医生姓名" width="120" />
        <el-table-column prop="department" label="科室" width="120" />
        <el-table-column prop="date" label="预约日期" width="120" />
        <el-table-column prop="timeSlot" label="时间段" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="success" link @click="handleComplete(scope.row)" v-if="scope.row.status === '待就诊'">
              完成就诊
            </el-button>
            <el-button type="danger" link @click="handleCancel(scope.row)" v-if="scope.row.status === '待就诊'">
              取消预约
            </el-button>
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
      :title="dialogType === 'add' ? '新增预约' : '编辑预约'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="患者" prop="patientId">
          <el-select v-model="form.patientId" placeholder="请选择患者">
            <el-option
              v-for="patient in patientList"
              :key="patient.id"
              :label="patient.name"
              :value="patient.id"
            />
          </el-select>
        </el-form-item>
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
        <el-form-item label="预约日期" prop="date">
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
  patientName: '',
  doctorName: '',
  date: '',
  status: ''
})

// 表格数据
const loading = ref(false)
const appointmentList = ref([
  {
    id: 1,
    patientName: '张三',
    doctorName: '李医生',
    department: '内科',
    date: '2024-01-01',
    timeSlot: '上午',
    status: '待就诊',
    createTime: '2024-01-01 12:00:00'
  },
  {
    id: 2,
    patientName: '李四',
    doctorName: '王医生',
    department: '外科',
    date: '2024-01-01',
    timeSlot: '下午',
    status: '已完成',
    createTime: '2024-01-01 12:00:00'
  }
])

// 患者列表
const patientList = ref([
  { id: 1, name: '张三' },
  { id: 2, name: '李四' },
  { id: 3, name: '王五' }
])

// 医生列表
const doctorList = ref([
  { id: 1, name: '李医生', department: '内科' },
  { id: 2, name: '王医生', department: '外科' },
  { id: 3, name: '张医生', department: '儿科' }
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
  patientId: '',
  doctorId: '',
  date: '',
  timeSlot: ''
})

// 表单验证规则
const rules = reactive<FormRules>({
  patientId: [
    { required: true, message: '请选择患者', trigger: 'change' }
  ],
  doctorId: [
    { required: true, message: '请选择医生', trigger: 'change' }
  ],
  date: [
    { required: true, message: '请选择预约日期', trigger: 'change' }
  ],
  timeSlot: [
    { required: true, message: '请选择时间段', trigger: 'change' }
  ]
})

// 获取状态标签类型
const getStatusType = (status: string) => {
  switch (status) {
    case '待就诊':
      return 'warning'
    case '已完成':
      return 'success'
    case '已取消':
      return 'info'
    default:
      return ''
  }
}

// 搜索
const handleSearch = () => {
  // TODO: 实现搜索逻辑
  console.log('搜索条件：', searchForm)
}

// 重置搜索
const resetSearch = () => {
  searchForm.patientName = ''
  searchForm.doctorName = ''
  searchForm.date = ''
  searchForm.status = ''
  handleSearch()
}

// 新增预约
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  form.patientId = ''
  form.doctorId = ''
  form.date = ''
  form.timeSlot = ''
}

// 编辑预约
const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(form, row)
}

// 完成就诊
const handleComplete = (row: any) => {
  ElMessageBox.confirm(
    '确定要将该预约标记为已完成吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // TODO: 实现完成就诊逻辑
    ElMessage.success('操作成功')
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}

// 取消预约
const handleCancel = (row: any) => {
  ElMessageBox.confirm(
    '确定要取消该预约吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // TODO: 实现取消预约逻辑
    ElMessage.success('取消成功')
  }).catch(() => {
    ElMessage.info('已取消操作')
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
.appointments {
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