<template>
  <div class="medical-records">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>电子病历管理</span>
          <el-button type="primary" @click="handleAdd">新增病历</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="患者姓名">
          <el-input v-model="searchForm.patientName" placeholder="请输入患者姓名" />
        </el-form-item>
        <el-form-item label="医生姓名">
          <el-input v-model="searchForm.doctorName" placeholder="请输入医生姓名" />
        </el-form-item>
        <el-form-item label="就诊日期">
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

      <el-table :data="recordList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="patientName" label="患者姓名" width="120" />
        <el-table-column prop="doctorName" label="医生姓名" width="120" />
        <el-table-column prop="department" label="科室" width="120" />
        <el-table-column prop="visitDate" label="就诊日期" width="120" />
        <el-table-column prop="diagnosis" label="诊断结果" show-overflow-tooltip />
        <el-table-column prop="treatment" label="治疗方案" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="primary" link @click="handleView(scope.row)">查看</el-button>
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
      :title="dialogType === 'add' ? '新增病历' : '编辑病历'"
      width="800px"
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
        <el-form-item label="就诊日期" prop="visitDate">
          <el-date-picker
            v-model="form.visitDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="主诉" prop="chiefComplaint">
          <el-input
            v-model="form.chiefComplaint"
            type="textarea"
            :rows="3"
            placeholder="请输入患者主诉"
          />
        </el-form-item>
        <el-form-item label="现病史" prop="presentIllness">
          <el-input
            v-model="form.presentIllness"
            type="textarea"
            :rows="3"
            placeholder="请输入现病史"
          />
        </el-form-item>
        <el-form-item label="既往史" prop="pastHistory">
          <el-input
            v-model="form.pastHistory"
            type="textarea"
            :rows="3"
            placeholder="请输入既往史"
          />
        </el-form-item>
        <el-form-item label="体格检查" prop="physicalExam">
          <el-input
            v-model="form.physicalExam"
            type="textarea"
            :rows="3"
            placeholder="请输入体格检查结果"
          />
        </el-form-item>
        <el-form-item label="诊断结果" prop="diagnosis">
          <el-input
            v-model="form.diagnosis"
            type="textarea"
            :rows="3"
            placeholder="请输入诊断结果"
          />
        </el-form-item>
        <el-form-item label="治疗方案" prop="treatment">
          <el-input
            v-model="form.treatment"
            type="textarea"
            :rows="3"
            placeholder="请输入治疗方案"
          />
        </el-form-item>
        <el-form-item label="医嘱" prop="medicalAdvice">
          <el-input
            v-model="form.medicalAdvice"
            type="textarea"
            :rows="3"
            placeholder="请输入医嘱"
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

    <!-- 查看对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="查看病历"
      width="800px"
    >
      <div class="record-detail">
        <div class="detail-item">
          <span class="label">患者姓名：</span>
          <span class="value">{{ currentRecord.patientName }}</span>
        </div>
        <div class="detail-item">
          <span class="label">医生姓名：</span>
          <span class="value">{{ currentRecord.doctorName }}</span>
        </div>
        <div class="detail-item">
          <span class="label">科室：</span>
          <span class="value">{{ currentRecord.department }}</span>
        </div>
        <div class="detail-item">
          <span class="label">就诊日期：</span>
          <span class="value">{{ currentRecord.visitDate }}</span>
        </div>
        <div class="detail-item">
          <span class="label">主诉：</span>
          <span class="value">{{ currentRecord.chiefComplaint }}</span>
        </div>
        <div class="detail-item">
          <span class="label">现病史：</span>
          <span class="value">{{ currentRecord.presentIllness }}</span>
        </div>
        <div class="detail-item">
          <span class="label">既往史：</span>
          <span class="value">{{ currentRecord.pastHistory }}</span>
        </div>
        <div class="detail-item">
          <span class="label">体格检查：</span>
          <span class="value">{{ currentRecord.physicalExam }}</span>
        </div>
        <div class="detail-item">
          <span class="label">诊断结果：</span>
          <span class="value">{{ currentRecord.diagnosis }}</span>
        </div>
        <div class="detail-item">
          <span class="label">治疗方案：</span>
          <span class="value">{{ currentRecord.treatment }}</span>
        </div>
        <div class="detail-item">
          <span class="label">医嘱：</span>
          <span class="value">{{ currentRecord.medicalAdvice }}</span>
        </div>
      </div>
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
  date: ''
})

// 表格数据
const loading = ref(false)
const recordList = ref([
  {
    id: 1,
    patientName: '张三',
    doctorName: '李医生',
    department: '内科',
    visitDate: '2024-01-01',
    diagnosis: '上呼吸道感染',
    treatment: '口服药物治疗',
    createTime: '2024-01-01 12:00:00'
  },
  {
    id: 2,
    patientName: '李四',
    doctorName: '王医生',
    department: '外科',
    visitDate: '2024-01-01',
    diagnosis: '急性阑尾炎',
    treatment: '手术治疗',
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
  visitDate: '',
  chiefComplaint: '',
  presentIllness: '',
  pastHistory: '',
  physicalExam: '',
  diagnosis: '',
  treatment: '',
  medicalAdvice: ''
})

// 查看对话框
const viewDialogVisible = ref(false)
const currentRecord = ref({
  patientName: '',
  doctorName: '',
  department: '',
  visitDate: '',
  chiefComplaint: '',
  presentIllness: '',
  pastHistory: '',
  physicalExam: '',
  diagnosis: '',
  treatment: '',
  medicalAdvice: ''
})

// 表单验证规则
const rules = reactive<FormRules>({
  patientId: [
    { required: true, message: '请选择患者', trigger: 'change' }
  ],
  doctorId: [
    { required: true, message: '请选择医生', trigger: 'change' }
  ],
  visitDate: [
    { required: true, message: '请选择就诊日期', trigger: 'change' }
  ],
  chiefComplaint: [
    { required: true, message: '请输入主诉', trigger: 'blur' }
  ],
  diagnosis: [
    { required: true, message: '请输入诊断结果', trigger: 'blur' }
  ],
  treatment: [
    { required: true, message: '请输入治疗方案', trigger: 'blur' }
  ]
})

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
  handleSearch()
}

// 新增病历
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  form.patientId = ''
  form.doctorId = ''
  form.visitDate = ''
  form.chiefComplaint = ''
  form.presentIllness = ''
  form.pastHistory = ''
  form.physicalExam = ''
  form.diagnosis = ''
  form.treatment = ''
  form.medicalAdvice = ''
}

// 编辑病历
const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(form, row)
}

// 查看病历
const handleView = (row: any) => {
  currentRecord.value = { ...row }
  viewDialogVisible.value = true
}

// 删除病历
const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    '确定要删除该病历信息吗？',
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
.medical-records {
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

  .record-detail {
    .detail-item {
      margin-bottom: 15px;
      
      .label {
        font-weight: bold;
        margin-right: 10px;
      }
      
      .value {
        color: #666;
      }
    }
  }
}
</style> 