<template>
  <div class="medical-record">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="患者姓名">
          <el-input v-model="searchForm.patientName" placeholder="请输入患者姓名" clearable />
        </el-form-item>
        <el-form-item label="就诊科室">
          <el-select v-model="searchForm.departmentId" placeholder="请选择就诊科室" clearable>
            <el-option
              v-for="item in departmentOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="就诊医生">
          <el-select v-model="searchForm.doctorId" placeholder="请选择就诊医生" clearable>
            <el-option
              v-for="item in doctorOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="就诊时间">
          <el-date-picker
            v-model="searchForm.visitTime"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>病历列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增病历
          </el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="tableData"
        border
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="patientName" label="患者姓名" />
        <el-table-column prop="departmentName" label="就诊科室" />
        <el-table-column prop="doctorName" label="就诊医生" />
        <el-table-column prop="visitTime" label="就诊时间" width="180" />
        <el-table-column prop="diagnosis" label="诊断结果" show-overflow-tooltip />
        <el-table-column prop="treatment" label="治疗方案" show-overflow-tooltip />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>编辑
            </el-button>
            <el-button type="primary" link @click="handleView(row)">
              <el-icon><View /></el-icon>查看
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
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
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="患者姓名" prop="patientId">
              <el-select
                v-model="form.patientId"
                placeholder="请选择患者"
                filterable
                remote
                :remote-method="handlePatientSearch"
                :loading="patientLoading"
              >
                <el-option
                  v-for="item in patientOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="就诊科室" prop="departmentId">
              <el-select
                v-model="form.departmentId"
                placeholder="请选择就诊科室"
                @change="handleDepartmentChange"
              >
                <el-option
                  v-for="item in departmentOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="就诊医生" prop="doctorId">
              <el-select
                v-model="form.doctorId"
                placeholder="请选择就诊医生"
                :disabled="!form.departmentId"
              >
                <el-option
                  v-for="item in doctorOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="就诊时间" prop="visitTime">
              <el-date-picker
                v-model="form.visitTime"
                type="datetime"
                placeholder="请选择就诊时间"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="主诉" prop="chiefComplaint">
          <el-input
            v-model="form.chiefComplaint"
            type="textarea"
            :rows="3"
            placeholder="请输入主诉"
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
            placeholder="请输入体格检查"
          />
        </el-form-item>
        <el-form-item label="辅助检查" prop="auxiliaryExam">
          <el-input
            v-model="form.auxiliaryExam"
            type="textarea"
            :rows="3"
            placeholder="请输入辅助检查"
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
      <el-descriptions :column="2" border>
        <el-descriptions-item label="患者姓名">{{ viewData.patientName }}</el-descriptions-item>
        <el-descriptions-item label="就诊科室">{{ viewData.departmentName }}</el-descriptions-item>
        <el-descriptions-item label="就诊医生">{{ viewData.doctorName }}</el-descriptions-item>
        <el-descriptions-item label="就诊时间">{{ viewData.visitTime }}</el-descriptions-item>
        <el-descriptions-item label="主诉" :span="2">{{ viewData.chiefComplaint }}</el-descriptions-item>
        <el-descriptions-item label="现病史" :span="2">{{ viewData.presentIllness }}</el-descriptions-item>
        <el-descriptions-item label="既往史" :span="2">{{ viewData.pastHistory }}</el-descriptions-item>
        <el-descriptions-item label="体格检查" :span="2">{{ viewData.physicalExam }}</el-descriptions-item>
        <el-descriptions-item label="辅助检查" :span="2">{{ viewData.auxiliaryExam }}</el-descriptions-item>
        <el-descriptions-item label="诊断结果" :span="2">{{ viewData.diagnosis }}</el-descriptions-item>
        <el-descriptions-item label="治疗方案" :span="2">{{ viewData.treatment }}</el-descriptions-item>
        <el-descriptions-item label="医嘱" :span="2">{{ viewData.medicalAdvice }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import type { MedicalRecord, MedicalRecordStatus } from '@/types/medical-record'
import {
  getMedicalRecordList,
  getMedicalRecordDetail,
  createMedicalRecord,
  updateMedicalRecord,
  deleteMedicalRecord,
  searchPatients,
  getDepartmentList,
  getDoctorList
} from '@/api/medical-record'

// 搜索表单
const searchForm = reactive({
  patientName: '',
  departmentId: undefined,
  doctorId: undefined,
  visitTime: []
})

// 科室选项
const departmentOptions = ref([
  { id: 1, name: '内科' },
  { id: 2, name: '外科' },
  { id: 3, name: '儿科' }
])

// 医生选项
const doctorOptions = ref([
  { id: 1, name: '张医生', departmentId: 1 },
  { id: 2, name: '李医生', departmentId: 1 },
  { id: 3, name: '王医生', departmentId: 2 }
])

// 患者选项
const patientOptions = ref([])
const patientLoading = ref(false)

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    id: 1,
    patientName: '张三',
    departmentName: '内科',
    doctorName: '张医生',
    visitTime: '2024-03-20 10:00:00',
    diagnosis: '感冒',
    treatment: '口服药物治疗',
    status: 'completed'
  }
])

// 分页
const page = ref(1)
const pageSize = ref(10)
const total = ref(100)

// 对话框
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const formRef = ref<FormInstance>()
const form = reactive({
  id: undefined,
  patientId: undefined,
  departmentId: undefined,
  doctorId: undefined,
  visitTime: '',
  chiefComplaint: '',
  presentIllness: '',
  pastHistory: '',
  physicalExam: '',
  auxiliaryExam: '',
  diagnosis: '',
  treatment: '',
  medicalAdvice: ''
})

// 查看对话框
const viewDialogVisible = ref(false)
const viewData = reactive({
  patientName: '',
  departmentName: '',
  doctorName: '',
  visitTime: '',
  chiefComplaint: '',
  presentIllness: '',
  pastHistory: '',
  physicalExam: '',
  auxiliaryExam: '',
  diagnosis: '',
  treatment: '',
  medicalAdvice: ''
})

// 表单验证规则
const rules: FormRules = {
  patientId: [
    { required: true, message: '请选择患者', trigger: 'change' }
  ],
  departmentId: [
    { required: true, message: '请选择就诊科室', trigger: 'change' }
  ],
  doctorId: [
    { required: true, message: '请选择就诊医生', trigger: 'change' }
  ],
  visitTime: [
    { required: true, message: '请选择就诊时间', trigger: 'change' }
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
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const { list, total: totalCount } = await getMedicalRecordList({
      patientName: searchForm.patientName,
      departmentId: searchForm.departmentId,
      doctorId: searchForm.doctorId,
      visitTime: searchForm.visitTime,
      page: page.value,
      pageSize: pageSize.value
    })
    tableData.value = list
    total.value = totalCount
  } catch (error) {
    console.error('加载病历列表失败:', error)
    ElMessage.error('加载病历列表失败')
  } finally {
    loading.value = false
  }
}

// 加载科室列表
const loadDepartments = async () => {
  try {
    const res = await getDepartmentList()
    if (res && res.code === 200) {
      departmentOptions.value = res.data.filter(dept => dept.status === 1)
    }
  } catch (error) {
    console.error('加载科室列表失败:', error)
  }
}

// 加载医生列表
const loadDoctors = async (departmentId: number) => {
  try {
    const list = await getDoctorList(departmentId)
    doctorOptions.value = list
  } catch (error) {
    console.error('加载医生列表失败:', error)
    ElMessage.error('加载医生列表失败')
  }
}

// 搜索患者
const handlePatientSearch = async (query: string) => {
  if (query) {
    patientLoading.value = true
    try {
      const list = await searchPatients(query)
      patientOptions.value = list
    } catch (error) {
      console.error('搜索患者失败:', error)
      ElMessage.error('搜索患者失败')
    } finally {
      patientLoading.value = false
    }
  } else {
    patientOptions.value = []
  }
}

// 科室变更
const handleDepartmentChange = async (value: number) => {
  form.doctorId = undefined
  if (value) {
    await loadDoctors(value)
  } else {
    doctorOptions.value = []
  }
}

// 搜索
const handleSearch = () => {
  page.value = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.patientName = ''
  searchForm.departmentId = undefined
  searchForm.doctorId = undefined
  searchForm.visitTime = []
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  Object.keys(form).forEach(key => {
    form[key] = key === 'status' ? MedicalRecordStatus.PENDING : ''
  })
}

// 编辑
const handleEdit = async (row: MedicalRecord) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  try {
    const detail = await getMedicalRecordDetail(row.id)
    Object.assign(form, detail)
  } catch (error) {
    console.error('获取病历详情失败:', error)
    ElMessage.error('获取病历详情失败')
  }
}

// 查看
const handleView = async (row: MedicalRecord) => {
  try {
    const detail = await getMedicalRecordDetail(row.id)
    Object.assign(viewData, detail)
    viewDialogVisible.value = true
  } catch (error) {
    console.error('获取病历详情失败:', error)
    ElMessage.error('获取病历详情失败')
  }
}

// 删除
const handleDelete = (row: MedicalRecord) => {
  ElMessageBox.confirm('确定要删除该病历吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteMedicalRecord(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error('删除病历失败:', error)
      ElMessage.error('删除病历失败')
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          await createMedicalRecord(form)
          ElMessage.success('新增成功')
        } else {
          await updateMedicalRecord(form)
          ElMessage.success('编辑成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error(dialogType.value === 'add' ? '新增病历失败:' : '编辑病历失败:', error)
        ElMessage.error(dialogType.value === 'add' ? '新增病历失败' : '编辑病历失败')
      }
    }
  })
}

// 分页
const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadData()
}

const handleCurrentChange = (val: number) => {
  page.value = val
  loadData()
}

// 初始化
onMounted(() => {
  loadData()
  loadDepartments()
})

// 状态格式化方法
const getStatusType = (status: number | string) => {
  switch (status) {
    case 0:
    case '0':
    case '未诊断':
      return 'info'
    case 1:
    case '1':
    case '已诊断':
      return 'primary'
    case 2:
    case '2':
    case '已取药':
      return 'success'
    default:
      return ''
  }
}

const getStatusText = (status: number | string) => {
  switch (status) {
    case 0:
    case '0':
      return '未诊断'
    case 1:
    case '1':
      return '已诊断'
    case 2:
    case '2':
      return '已取药'
    default:
      return '未知'
  }
}
</script>

<style scoped>
.medical-record {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 