<template>
  <div class="appointments">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>预约管理</span>
          <el-button type="primary" @click="showStepAppointment = true">新增预约</el-button>
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

    <StepAppointment v-if="showStepAppointment" @close="showStepAppointment = false" />

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
import { getAppointmentList, createAppointment, updateAppointmentStatus, cancelAppointment, completeAppointment } from '@/api/appointment'
import { getDoctorList } from '@/api/doctor'
import { searchPatients } from '@/api/medical-record'
import StepAppointment from './StepAppointment.vue'

// 状态定义
const loading = ref(false)
const appointmentList = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const showStepAppointment = ref(false)
const patientList = ref<any[]>([])
const doctorList = ref<any[]>([])

// 搜索表单
const searchForm = reactive({
  patientName: '',
  doctorName: '',
  date: '',
  status: undefined
})

// 表单数据
const form = reactive({
  patientId: '',
  doctorId: '',
  appointmentDate: '',
  timeSlot: '',
  remark: ''
})

// 表单验证规则
const rules = reactive<FormRules>({
  patientId: [
    { required: true, message: '请选择患者', trigger: 'change' }
  ],
  doctorId: [
    { required: true, message: '请选择医生', trigger: 'change' }
  ],
  appointmentDate: [
    { required: true, message: '请选择预约日期', trigger: 'change' }
  ],
  timeSlot: [
    { required: true, message: '请选择时间段', trigger: 'change' }
  ]
})

// 编辑预约相关
const editDialogVisible = ref(false)
const editFormRef = ref<FormInstance>()
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

// 编辑表单验证规则
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

// 获取状态类型
const getStatusType = (status: number) => {
  switch (status) {
    case 0:
      return 'warning' // 待就诊
    case 1:
      return 'primary' // 已确认
    case 2:
      return 'success' // 已完成
    case 3:
      return 'info' // 已取消
    case 4:
      return 'danger' // 已过期
    default:
      return ''
  }
}

// 获取状态文本
const getStatusText = (status: number) => {
  switch (status) {
    case 0:
      return '待就诊'
    case 1:
      return '已确认'
    case 2:
      return '已完成'
    case 3:
      return '已取消'
    case 4:
      return '已过期'
    default:
      return '未知'
  }
}

// 获取预约列表
const getList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      ...searchForm
    }
    console.log('请求参数:', params)
    const res = await getAppointmentList(params)
    console.log('API返回数据:', res)
    
    // 处理数据，确保各字段有默认值
    let processedRecords: any[] = [];
    
    if (Array.isArray(res)) {
      // API已经返回了处理好的数组
      processedRecords = res.map((record: any) => ({
        ...record,
        userName: record.userName || record.patientName || '未知患者',
        doctorName: record.doctorName || '未知医生',
        departmentName: record.departmentName || '未知科室',
        scheduleDate: record.scheduleDate || '未排班',
        periodText: record.periodText || getPeriodText(record.period) || '未知时段'
      }));
    } else if (res && typeof res === 'object' && res.data) {
      // 处理Axios响应对象
      const responseData = res.data;
      
      if (Array.isArray(responseData)) {
        // 响应数据是数组
        processedRecords = responseData.map((record: any) => ({
          ...record,
          userName: record.userName || record.patientName || '未知患者',
          doctorName: record.doctorName || '未知医生',
          departmentName: record.departmentName || '未知科室',
          scheduleDate: record.scheduleDate || '未排班',
          periodText: record.periodText || getPeriodText(record.period) || '未知时段'
        }));
      } else if (typeof responseData === 'object') {
        // 响应数据是单个对象
        processedRecords = [
          {
            ...responseData,
            userName: responseData.userName || responseData.patientName || '未知患者',
            doctorName: responseData.doctorName || '未知医生',
            departmentName: responseData.departmentName || '未知科室',
            scheduleDate: responseData.scheduleDate || '未排班',
            periodText: responseData.periodText || getPeriodText(responseData.period) || '未知时段'
          }
        ];
      }
    }
    
    appointmentList.value = processedRecords;
    total.value = processedRecords.length;
    console.log('处理后的数据数量:', appointmentList.value.length)
    console.log('处理后的数据:', appointmentList.value)
  } catch (error) {
    console.error('获取预约列表失败:', error)
    ElMessage.error('获取预约列表失败')
    appointmentList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.patientName = ''
  searchForm.doctorName = ''
  searchForm.date = ''
  searchForm.status = undefined
  handleSearch()
}

// 编辑预约
const handleEdit = (row: any) => {
  console.log('编辑预约数据:', row);
  editDialogVisible.value = true
  // 复制数据到编辑表单
  editForm.id = row.id
  editForm.appointmentNumber = row.appointmentNumber || ''
  editForm.userName = row.userName || row.patientName || '未知患者'
  editForm.doctorName = row.doctorName || '未知医生'
  editForm.departmentName = row.departmentName || '未知科室'
  editForm.scheduleDate = row.scheduleDate || ''
  editForm.periodText = row.periodText || getPeriodText(row.period) || ''
  editForm.status = typeof row.status === 'number' ? row.status : 0
  editForm.description = row.description || ''
}

// 提交编辑
const submitEdit = async () => {
  if (!editFormRef.value) return
  
  await editFormRef.value.validate(async (valid, fields) => {
    if (valid) {
      try {
        console.log('提交编辑表单:', editForm);
        
        // 这里应该调用更新预约的API
        // 例如: await updateAppointment(editForm)
        
        // 由于我们没有完整的updateAppointment API，这里先模拟更新状态
        const id = editForm.id;
        const status = editForm.status;
        
        if (id && typeof status === 'number') {
          // 确保id是数字
          const numericId = typeof id === 'string' ? parseInt(id) : id;
          
          if (!isNaN(numericId)) {
            await updateAppointmentStatus(numericId, status);
            ElMessage.success('更新成功');
            editDialogVisible.value = false;
            getList(); // 刷新列表
          } else {
            ElMessage.error('无效的预约ID');
          }
        } else {
          ElMessage.error('缺少必要的预约信息');
        }
      } catch (error) {
        console.error('更新预约失败:', error)
        ElMessage.error('更新失败')
      }
    } else {
      console.log('表单验证失败:', fields)
    }
  })
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
  ).then(async () => {
    try {
      // 将状态修改为2（已完成）
      const res = await updateAppointmentStatus(row.id, 2)
      ElMessage.success('操作成功')
      getList()
    } catch (error) {
      console.error('完成就诊失败:', error)
      ElMessage.error('操作失败')
    }
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
  ).then(async () => {
    try {
      // 将状态修改为3（已取消）
      const res = await updateAppointmentStatus(row.id, 3)
      ElMessage.success('取消成功')
      getList()
    } catch (error) {
      console.error('取消预约失败:', error)
      ElMessage.error('取消失败')
    }
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}

// 获取患者列表
const fetchPatients = async () => {
  try {
    const res = await searchPatients('') // 传入空字符串作为参数
    if (res && res.data && res.data.code === 200) {
      patientList.value = res.data.data || []
    }
  } catch (error) {
    console.error('获取患者列表失败:', error)
  }
}

// 获取医生列表
const fetchDoctors = async () => {
  try {
    const res = await getDoctorList({}) // 传入空对象作为参数
    if (res && res.data && res.data.code === 200) {
      doctorList.value = res.data.data || []
    }
  } catch (error) {
    console.error('获取医生列表失败:', error)
  }
}

// 分页大小改变
const handleSizeChange = (val: number) => {
  pageSize.value = val
  getList()
}

// 页码改变
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  getList()
}

// 格式化日期
const formatDate = (dateStr: string) => {
  try {
    const date = new Date(dateStr);
    return date.toLocaleString();
  } catch (e) {
    return dateStr;
  }
}

// 获取时间段文本
const getPeriodText = (period: number) => {
  switch (period) {
    case 1:
      return '上午';
    case 2:
      return '下午';
    case 3:
      return '晚上';
    default:
      return '';
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.appointments {
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