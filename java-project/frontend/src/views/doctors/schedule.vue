<template>
  <div class="schedule">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>排班管理</span>
          <el-button type="primary" @click="handleAdd">新增排班</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="医生姓名">
          <el-input v-model="searchForm.doctorName" placeholder="请输入医生姓名" />
        </el-form-item>
        <el-form-item label="科室">
          <el-select v-model="searchForm.department" placeholder="请选择科室">
            <el-option label="全部" value="" />
            <el-option label="内科" value="内科" />
            <el-option label="外科" value="外科" />
            <el-option label="妇产科" value="妇产科" />
            <el-option label="儿科" value="儿科" />
            <el-option label="眼科" value="眼科" />
            <el-option label="口腔科" value="口腔科" />
          </el-select>
        </el-form-item>
        <el-form-item label="排班日期">
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
        <el-table-column prop="scheduleDate" label="排班日期" width="120" />
        <el-table-column prop="timeSlot" label="时间段" width="120" />
        <el-table-column prop="maxPatients" label="最大接诊人数" width="120" />
        <el-table-column prop="bookedPatients" label="已预约人数" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'AVAILABLE' ? 'success' : 'info'">
              {{ scope.row.status === 'AVAILABLE' ? '可预约' : '已满' }}
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
          <el-select v-model="form.doctorId" placeholder="请选择医生" @change="handleDoctorChange">
            <el-option
              v-for="doctor in doctorList"
              :key="doctor.id"
              :label="doctor.name"
              :value="doctor.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="排班日期" prop="scheduleDate">
          <el-date-picker
            v-model="form.scheduleDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledDate"
          />
        </el-form-item>
        <el-form-item label="时间段" prop="timeSlot">
          <el-select v-model="form.timeSlot" placeholder="请选择时间段">
            <el-option label="上午 (08:00-12:00)" value="上午" />
            <el-option label="下午 (14:00-18:00)" value="下午" />
          </el-select>
        </el-form-item>
        <el-form-item label="最大接诊人数" prop="maxPatients">
          <el-input-number v-model="form.maxPatients" :min="1" :max="50" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 批量排班对话框 -->
    <el-dialog
      v-model="batchDialogVisible"
      title="批量排班"
      width="600px"
    >
      <el-form
        ref="batchFormRef"
        :model="batchForm"
        :rules="batchRules"
        label-width="100px"
      >
        <el-form-item label="医生" prop="doctorId">
          <el-select v-model="batchForm.doctorId" placeholder="请选择医生" @change="handleDoctorChange">
            <el-option
              v-for="doctor in doctorList"
              :key="doctor.id"
              :label="doctor.name"
              :value="doctor.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围" prop="dateRange">
          <el-date-picker
            v-model="batchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledDate"
          />
        </el-form-item>
        <el-form-item label="排班周期" prop="weekdays">
          <el-checkbox-group v-model="batchForm.weekdays">
            <el-checkbox label="1">周一</el-checkbox>
            <el-checkbox label="2">周二</el-checkbox>
            <el-checkbox label="3">周三</el-checkbox>
            <el-checkbox label="4">周四</el-checkbox>
            <el-checkbox label="5">周五</el-checkbox>
            <el-checkbox label="6">周六</el-checkbox>
            <el-checkbox label="0">周日</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="时间段" prop="timeSlots">
          <el-checkbox-group v-model="batchForm.timeSlots">
            <el-checkbox label="上午">上午 (08:00-12:00)</el-checkbox>
            <el-checkbox label="下午">下午 (14:00-18:00)</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="最大接诊人数" prop="maxPatients">
          <el-input-number v-model="batchForm.maxPatients" :min="1" :max="50" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="batchDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleBatchSubmit">确定</el-button>
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
  doctorName: '',
  department: '',
  date: ''
})

// 表格数据
const loading = ref(false)
const scheduleList = ref([
  {
    id: 1,
    doctorName: '李医生',
    department: '内科',
    scheduleDate: '2024-01-01',
    timeSlot: '上午',
    maxPatients: 20,
    bookedPatients: 15,
    status: 'AVAILABLE'
  },
  {
    id: 2,
    doctorName: '王医生',
    department: '外科',
    scheduleDate: '2024-01-01',
    timeSlot: '下午',
    maxPatients: 15,
    bookedPatients: 15,
    status: 'FULL'
  }
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
  doctorId: undefined,
  scheduleDate: '',
  timeSlot: '',
  maxPatients: 20,
  remark: ''
})

// 批量排班对话框
const batchDialogVisible = ref(false)
const batchFormRef = ref<FormInstance>()
const batchForm = reactive({
  doctorId: undefined,
  dateRange: [] as string[],
  weekdays: [] as string[],
  timeSlots: [] as string[],
  maxPatients: 20
})

// 医生列表
const doctorList = ref([
  { id: 1, name: '李医生', department: '内科' },
  { id: 2, name: '王医生', department: '外科' }
])

// 表单验证规则
const rules = reactive<FormRules>({
  doctorId: [
    { required: true, message: '请选择医生', trigger: 'change' }
  ],
  scheduleDate: [
    { required: true, message: '请选择排班日期', trigger: 'change' }
  ],
  timeSlot: [
    { required: true, message: '请选择时间段', trigger: 'change' }
  ],
  maxPatients: [
    { required: true, message: '请输入最大接诊人数', trigger: 'change' }
  ]
})

// 批量表单验证规则
const batchRules = reactive<FormRules>({
  doctorId: [
    { required: true, message: '请选择医生', trigger: 'change' }
  ],
  dateRange: [
    { required: true, message: '请选择日期范围', trigger: 'change' }
  ],
  weekdays: [
    { required: true, message: '请选择排班周期', trigger: 'change' }
  ],
  timeSlots: [
    { required: true, message: '请选择时间段', trigger: 'change' }
  ],
  maxPatients: [
    { required: true, message: '请输入最大接诊人数', trigger: 'change' }
  ]
})

// 禁用过去的日期
const disabledDate = (time: Date) => {
  return time.getTime() < Date.now() - 8.64e7 // 禁用今天之前的日期
}

// 医生选择变更
const handleDoctorChange = (doctorId: number) => {
  // TODO: 根据医生ID获取医生信息
  console.log('选择医生ID：', doctorId)
}

// 搜索
const handleSearch = () => {
  // TODO: 实现搜索逻辑
  console.log('搜索条件：', searchForm)
}

// 重置搜索
const resetSearch = () => {
  searchForm.doctorName = ''
  searchForm.department = ''
  searchForm.date = ''
  handleSearch()
}

// 新增排班
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  form.doctorId = undefined
  form.scheduleDate = ''
  form.timeSlot = ''
  form.maxPatients = 20
  form.remark = ''
}

// 编辑排班
const handleEdit = (_row: any) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  // TODO: 加载排班详情并填充表单
}

// 删除排班
const handleDelete = (_row: any) => {
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
const handleSubmit = () => {
  if (!formRef.value) return
  formRef.value.validate((valid, fields) => {
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

// 批量排班提交
const handleBatchSubmit = () => {
  if (!batchFormRef.value) return
  batchFormRef.value.validate((valid, fields) => {
    if (valid) {
      // TODO: 实现批量排班逻辑
      console.log('批量排班表单：', batchForm)
      ElMessage.success('批量排班成功')
      batchDialogVisible.value = false
    } else {
      console.log('表单验证失败：', fields)
    }
  })
}

// 分页
const handleSizeChange = (val: number) => {
  pageSize.value = val
  // TODO: 重新加载数据
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  // TODO: 重新加载数据
}
</script>

<style scoped>
.schedule {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 