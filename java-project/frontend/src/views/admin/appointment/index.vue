<template>
  <div class="admin-appointment">
    <h2>预约管理</h2>
    <el-card>
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
            <el-option label="全部" :value="undefined" />
            <el-option label="待就诊" :value="0" />
            <el-option label="已确认" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
            <el-option label="已过期" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="appointmentList" style="width: 100%" v-loading="loading">
        <el-table-column prop="appointmentNumber" label="预约编号" width="150" />
        <el-table-column label="患者姓名" width="120">
          <template #default="scope">
            {{ scope.row.userName || scope.row.patientName || '未知患者' }}
          </template>
        </el-table-column>
        <el-table-column label="医生姓名" width="120">
          <template #default="scope">
            {{ scope.row.doctorName || '未知医生' }}
          </template>
        </el-table-column>
        <el-table-column label="科室" width="120">
          <template #default="scope">
            {{ scope.row.departmentName || '未知科室' }}
          </template>
        </el-table-column>
        <el-table-column label="预约日期" width="120">
          <template #default="scope">
            {{ scope.row.scheduleDate || '未安排' }}
          </template>
        </el-table-column>
        <el-table-column label="时间段" width="120">
          <template #default="scope">
            {{ scope.row.periodText || getPeriodText(scope.row.period) || '未安排' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="scope">
            {{ scope.row.createTime ? formatDate(scope.row.createTime) : '' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="success" link @click="handleComplete(scope.row)" v-if="scope.row.status === 0">
              完成就诊
            </el-button>
            <el-button type="danger" link @click="handleCancel(scope.row)" v-if="scope.row.status === 0">
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

    <!-- 编辑预约对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑预约信息"
      width="500px"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <el-form-item label="预约编号">
          <el-input v-model="editForm.appointmentNumber" disabled />
        </el-form-item>
        <el-form-item label="患者姓名">
          <el-input v-model="editForm.userName" disabled />
        </el-form-item>
        <el-form-item label="医生姓名">
          <el-input v-model="editForm.doctorName" disabled />
        </el-form-item>
        <el-form-item label="科室">
          <el-input v-model="editForm.departmentName" disabled />
        </el-form-item>
        <el-form-item label="预约日期" prop="scheduleDate">
          <el-date-picker
            v-model="editForm.scheduleDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="时间段" prop="periodText">
          <el-select v-model="editForm.periodText" placeholder="请选择时间段">
            <el-option label="上午" value="上午" />
            <el-option label="下午" value="下午" />
            <el-option label="晚上" value="晚上" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="editForm.status" placeholder="请选择状态">
            <el-option label="待就诊" :value="0" />
            <el-option label="已确认" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="editForm.description" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getAppointmentList, updateAppointmentStatus, cancelAppointment, completeAppointment } from '@/api/appointment'

// 状态定义
const loading = ref(false)
const appointmentList = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const editDialogVisible = ref(false)
const editFormRef = ref<FormInstance>()

// 搜索表单
const searchForm = reactive({
  patientName: '',
  doctorName: '',
  date: '',
  status: undefined
})

// 编辑表单
const editForm = reactive({
  id: '',
  appointmentNumber: '',
  userName: '',
  doctorName: '',
  departmentName: '',
  scheduleDate: '',
  periodText: '',
  status: 0,
  description: ''
})

// 表单验证规则
const editRules = reactive<FormRules>({
  scheduleDate: [
    { required: true, message: '请选择预约日期', trigger: 'change' }
  ],
  periodText: [
    { required: true, message: '请选择时间段', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
})

// 获取预约列表
const fetchAppointmentList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      patientName: searchForm.patientName || undefined,
      doctorName: searchForm.doctorName || undefined,
      date: searchForm.date || undefined,
      status: searchForm.status
    }
    const res = await getAppointmentList(params)
    // 检查返回数据结构并安全地提取列表和总数
    if (res && typeof res === 'object') {
      if ('list' in res) {
        appointmentList.value = res.list || []
      } else if (Array.isArray(res)) {
        appointmentList.value = res
      } else {
        appointmentList.value = []
      }
      
      if ('total' in res) {
        total.value = res.total || 0
      } else {
        total.value = appointmentList.value.length
      }
    } else {
      appointmentList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取预约列表失败')
  } finally {
    loading.value = false
  }
}

// 查询
const handleSearch = () => {
  currentPage.value = 1
  fetchAppointmentList()
}

// 重置查询条件
const resetSearch = () => {
  Object.assign(searchForm, {
    patientName: '',
    doctorName: '',
    date: '',
    status: undefined
  })
  currentPage.value = 1
  fetchAppointmentList()
}

// 编辑预约
const handleEdit = (row: any) => {
  editForm.id = row.id
  editForm.appointmentNumber = row.appointmentNumber
  editForm.userName = row.userName || row.patientName
  editForm.doctorName = row.doctorName
  editForm.departmentName = row.departmentName
  editForm.scheduleDate = row.scheduleDate
  editForm.periodText = row.periodText
  editForm.status = row.status
  editForm.description = row.description
  editDialogVisible.value = true
}

// 提交编辑
const submitEdit = async () => {
  if (!editFormRef.value) return
  await editFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    try {
      await updateAppointmentStatus({
        id: editForm.id,
        scheduleDate: editForm.scheduleDate,
        periodText: editForm.periodText,
        status: editForm.status,
        description: editForm.description
      })
      ElMessage.success('编辑成功')
      editDialogVisible.value = false
      fetchAppointmentList()
    } catch (error) {
      console.error(error)
      ElMessage.error('编辑失败')
    }
  })
}

// 完成就诊
const handleComplete = (row: any) => {
  ElMessageBox.confirm('确认完成此预约的就诊?', '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await completeAppointment(row.id, { status: 2 })
      ElMessage.success('操作成功')
      fetchAppointmentList()
    } catch (error) {
      console.error(error)
      ElMessage.error('操作失败')
    }
  }).catch(() => {})
}

// 取消预约
const handleCancel = (row: any) => {
  ElMessageBox.confirm('确认取消此预约?', '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await cancelAppointment(row.id, { status: 3 })
      ElMessage.success('取消成功')
      fetchAppointmentList()
    } catch (error) {
      console.error(error)
      ElMessage.error('取消失败')
    }
  }).catch(() => {})
}

// 分页处理
const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchAppointmentList()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchAppointmentList()
}

// 格式化日期
const formatDate = (dateString: string) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 获取状态类型
const getStatusType = (status: number) => {
  const statusMap: Record<number, string> = {
    0: 'info',
    1: 'primary',
    2: 'success',
    3: 'danger',
    4: 'warning'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    0: '待就诊',
    1: '已确认',
    2: '已完成',
    3: '已取消',
    4: '已过期'
  }
  return statusMap[status] || '未知状态'
}

// 获取时段文本
const getPeriodText = (period: number) => {
  const periodMap: Record<number, string> = {
    0: '上午',
    1: '下午',
    2: '晚上'
  }
  return periodMap[period] || '未知时段'
}

onMounted(() => {
  fetchAppointmentList()
})
</script>

<style scoped>
.admin-appointment {
  padding: 20px;
}
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
  text-align: right;
}
</style> 