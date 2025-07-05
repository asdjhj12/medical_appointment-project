<template>
  <div class="admin-schedule">
    <h2>排班管理</h2>
    <el-card>
      <el-button type="primary" @click="openAddDialog" style="margin-bottom: 16px;">新增排班</el-button>
      <el-button @click="fetchSchedules" style="margin-bottom: 16px; margin-left: 8px;">刷新</el-button>
      <el-table :data="scheduleList" v-loading="loading" style="width: 100%; margin-top: 8px;">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="doctorName" label="医生姓名" />
        <el-table-column prop="departmentName" label="所属科室" />
        <el-table-column prop="scheduleDate" label="日期" :formatter="formatDate" />
        <el-table-column prop="periodText" label="时间段" />
        <el-table-column prop="currentAppointments" label="当前预约数/最大预约数">
          <template #default="{ row }">
            {{ row.currentAppointments }}/{{ row.maxAppointments }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '正常' : '停诊' }}
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
      
      <!-- 分页组件 -->
      <div class="pagination-container" style="margin-top: 20px; text-align: right;">
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
    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑排班' : '新增排班'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="选择医生" prop="doctorId">
          <el-select v-model="form.doctorId" placeholder="请选择医生" filterable @change="handleDoctorChange">
            <el-option 
              v-for="doctor in doctorList" 
              :key="doctor.id" 
              :label="doctor.doctorName || doctor.name || doctor.realName || doctor.userName" 
              :value="doctor.id">
              <span>{{ doctor.doctorName || doctor.name || doctor.realName || doctor.userName }}</span>
              <span v-if="doctor.departmentName" style="float: right; color: #8492a6; font-size: 13px">
                {{ doctor.departmentName }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="所属科室">
          <el-input v-model="selectedDepartmentName" disabled placeholder="选择医生后自动显示"></el-input>
        </el-form-item>
         <el-form-item label="排班日期" prop="scheduleDate">
          <el-date-picker v-model="form.scheduleDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD"/>
        </el-form-item>
        <el-form-item label="时段" prop="period">
          <el-select v-model="form.period" placeholder="请选择时段">
            <el-option label="上午" :value="1"></el-option>
            <el-option label="下午" :value="2"></el-option>
            <el-option label="晚上" :value="3"></el-option>
          </el-select>
        </el-form-item>
         <el-form-item label="最大预约数" prop="maxAppointments">
          <el-input-number v-model="form.maxAppointments" :min="1" label="最大预约数"></el-input-number>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="正常" :value="1" />
            <el-option label="停诊" :value="0" />
          </el-select>
        </el-form-item>
         <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
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
import { ref, onMounted, reactive, computed } from 'vue'
import { getScheduleList, addSchedule, updateSchedule, deleteSchedule } from '@/api/schedule'
import { listAllDoctors } from '@/api/doctor'
import type { Schedule as ScheduleType } from '@/api/schedule'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import type { AxiosResponse } from 'axios'
import type { Doctor } from '@/api/doctor'

interface Schedule {
  id: number;
  doctorId: number;
  doctorName?: string;
  departmentName?: string;
  scheduleDate: string;
  period: number;
  periodText?: string;
  maxAppointments: number;
  currentAppointments?: number;
  status: number;
  description?: string;
}

interface ScheduleResponse {
  records?: Schedule[];
  total?: number;
  size?: number;
  current?: number;
  pages?: number;
}

const scheduleList = ref<Schedule[]>([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: null,
  doctorId: null,
  departmentId: null as number | null,
  scheduleDate: '',
  period: null,
  maxAppointments: null,
  status: 1,
  description: ''
})
const formRef = ref()

// 医生列表数据
const doctorList = ref<Doctor[]>([])
const doctorLoading = ref(false)
const selectedDepartmentName = ref('')

// 获取医生列表
const fetchDoctors = async () => {
  doctorLoading.value = true
  try {
    const res = await listAllDoctors()
    console.log('获取到的医生列表原始数据:', res)
    
    if (res && res.data) {
      doctorList.value = res.data
      console.log('医生列表数据(data):', doctorList.value)
    } else if (Array.isArray(res)) {
      doctorList.value = res
      console.log('医生列表数据(array):', doctorList.value)
    } else {
      doctorList.value = []
      ElMessage.warning('未获取到医生数据')
    }
  } catch (e) {
    console.error('获取医生列表失败:', e)
    ElMessage.error('获取医生列表失败')
    doctorList.value = []
  } finally {
    doctorLoading.value = false
  }
}

// 处理医生选择变化
const handleDoctorChange = (doctorId: number) => {
  const selectedDoctor = doctorList.value.find(doctor => doctor.id === doctorId)
  if (selectedDoctor) {
    selectedDepartmentName.value = selectedDoctor.departmentName || '未知科室'
    // 保存医生所属科室ID，用于提交
    form.departmentId = selectedDoctor.departmentId || null
    console.log('选择医生:', selectedDoctor.doctorName || selectedDoctor.name, '科室ID:', selectedDoctor.departmentId)
  } else {
    selectedDepartmentName.value = ''
    form.departmentId = null
  }
}

const rules = {
  doctorId: [{ required: true, message: '请选择医生', trigger: 'change' }],
  scheduleDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
  period: [{ required: true, message: '请选择时段', trigger: 'change' }],
  maxAppointments: [{ required: true, message: '请输入最大预约数', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
}

const fetchSchedules = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    const res = await getScheduleList(params)
    console.log('获取排班数据:', res)
    
    // 处理不同的响应格式
    if (res && res.data && res.data.records) {
      // 处理嵌套在data中的records
      scheduleList.value = res.data.records
      if (typeof res.data.total === 'number') {
        total.value = res.data.total
      }
    } else if (res && typeof res === 'object' && 'records' in res && Array.isArray(res.records)) {
      // 直接包含records的情况
      const data = res as unknown as ScheduleResponse
      scheduleList.value = data.records || []
      if (typeof data.total === 'number') {
        total.value = data.total
      } else {
        total.value = scheduleList.value.length
      }
    } else if (res && Array.isArray(res)) {
      // 直接是数组的情况
      scheduleList.value = res as unknown as Schedule[]
      total.value = scheduleList.value.length
    } else {
      scheduleList.value = []
      total.value = 0
      ElMessage.warning('未获取到排班数据')
    }
  } catch (e) {
    console.error('获取排班列表失败:', e)
    ElMessage.error('获取排班列表失败')
    scheduleList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  isEdit.value = false
  Object.assign(form, { 
    id: null, 
    doctorId: null, 
    departmentId: null,
    scheduleDate: '', 
    period: null, 
    maxAppointments: null, 
    status: 1, 
    description: '' 
  })
  selectedDepartmentName.value = ''
  dialogVisible.value = true
}

const openEditDialog = (row: any) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    doctorId: row.doctorId,
    departmentId: row.departmentId || null,
    scheduleDate: row.scheduleDate,
    period: row.period !== undefined ? row.period : null,
    maxAppointments: row.maxAppointments,
    status: row.status !== undefined ? row.status : 1,
    description: row.description || ''
  })
  
  // 设置所属科室
  selectedDepartmentName.value = row.departmentName || ''
  
  console.log('编辑排班ID:', form.id)
  dialogVisible.value = true
}

const handleSubmit = () => {
  formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    try {
      // 创建一个新对象，避免直接传递reactive对象
      const submitData: any = {
        doctorId: Number(form.doctorId),
        departmentId: Number(form.departmentId),
        scheduleDate: form.scheduleDate,
        period: Number(form.period),
        maxAppointments: Number(form.maxAppointments),
        status: Number(form.status),
        description: form.description || ''
      }
      
      console.log('提交数据:', submitData)
      
      if (isEdit.value && form.id) {
        // 编辑模式
        submitData.id = Number(form.id)
        
        try {
          // 使用与后端控制器匹配的API路径
          await request({
            url: `/api/doctor-schedules/${submitData.id}`,
            method: 'put',
            data: submitData
          })
          
          ElMessage.success('编辑成功')
          dialogVisible.value = false
          fetchSchedules()
        } catch (error: any) {
          console.error('编辑排班失败:', error)
          ElMessage.error('编辑失败: ' + (error.message || '未知错误'))
        }
      } else {
        // 新增模式
        try {
          // 使用与后端控制器匹配的API路径
          await request({
            url: '/api/doctor-schedules',
            method: 'post',
            data: submitData
          })
          
          ElMessage.success('新增成功')
          dialogVisible.value = false
          fetchSchedules()
        } catch (error: any) {
          console.error('新增排班失败:', error)
          ElMessage.error('新增失败: ' + (error.message || '未知错误'))
        }
      }
    } catch (e) {
      console.error('表单验证或其他错误:', e)
      ElMessage.error('操作失败，请检查表单数据')
    }
  })
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除排班"${row.doctorName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request({
        url: `/api/doctor-schedules/${row.id}`,
        method: 'delete'
      })
      ElMessage.success('删除成功')
      fetchSchedules()
    } catch (e) {
      ElMessage.error('删除失败')
    }
  })
}

const formatDate = (row: any) => {
  if (!row.scheduleDate) return '';
  if (typeof row.scheduleDate === 'string') {
    return row.scheduleDate.split('T')[0];
  }
  return row.scheduleDate;
}

// 处理分页大小变化
const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchSchedules()
}

// 处理页码变化
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchSchedules()
}

// 初始化
onMounted(() => {
  fetchSchedules()
  fetchDoctors()
})
</script>
<style scoped>
.admin-schedule {
  padding: 20px;
}
</style> 