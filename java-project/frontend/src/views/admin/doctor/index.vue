<template>
  <div class="admin-doctor">
    <h2>医生管理</h2>
    <el-card>
      <el-button type="primary" @click="openAddDialog" style="margin-bottom: 16px;">新增医生</el-button>
      <el-button @click="fetchDoctors" style="margin-bottom: 16px; margin-left: 8px;">刷新</el-button>
      <el-table :data="doctorList" v-loading="loading" style="width: 100%; margin-top: 8px;">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="doctorName" label="姓名">
          <template #default="{ row }">
            {{ row.doctorName || row.userName || row.realName || '未知姓名' }}
          </template>
        </el-table-column>
        <el-table-column prop="departmentName" label="所属科室">
          <template #default="{ row }">
            {{ row.departmentName || '未知科室' }}
          </template>
        </el-table-column>
        <el-table-column prop="title" label="职称" />
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑医生' : '新增医生'" width="400px" @opened="onDialogOpened">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="姓名" prop="doctorName">
          <el-input v-model="form.doctorName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" ref="usernameInputRef" />
          <div style="color: #999; font-size: 12px;">当前用户名值: {{ form.username || '未设置' }}</div>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" :placeholder="isEdit ? '不修改密码请留空' : '请输入密码'" show-password />
          <div v-if="isEdit" style="color: #E6A23C; font-size: 12px; margin-top: 5px;">
            出于安全考虑，编辑时密码不会显示，如需修改请重新输入密码
          </div>
        </el-form-item>
        <el-form-item label="所属科室" prop="departmentId">
          <el-select v-model="form.departmentId" placeholder="请选择科室">
            <el-option v-for="dept in departmentList" :key="dept.id" :label="dept.name" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="职称" prop="title">
          <el-input v-model="form.title" placeholder="请输入职称" />
        </el-form-item>
        <el-form-item label="专长" prop="specialty">
          <el-input v-model="form.specialty" placeholder="请输入专长" />
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
import { ref, onMounted, reactive, computed, nextTick } from 'vue'
import { listAllDoctors, getDoctorById } from '@/api/doctor'
import { listAllDepartments } from '@/api/department'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import type { ApiResponse } from '@/types/api'

interface Department {
  id: number;
  name: string;
}

interface Doctor {
  id: number;
  doctorName?: string;
  userName?: string;
  username?: string;
  password?: string;
  realName?: string;
  departmentId?: number;
  departmentName?: string;
  title?: string;
  specialty?: string;
  status: number;
  userId?: number;
}

const doctorList = ref<Doctor[]>([])
const departmentList = ref<Department[]>([])
const loading = ref(false)

const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: null as number | null, 
  doctorName: '', 
  username: '',
  password: '',
  departmentId: null as number | null, 
  title: '',
  specialty: '',
  status: 1,
  userId: null as number | null
})
const formRef = ref()
const usernameInputRef = ref()

const passwordRule = computed(() => {
  return [{ required: !isEdit.value, message: '请输入密码', trigger: 'blur' }]
})

const rules = {
  doctorName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: computed(() => {
    return [{ required: !isEdit.value, message: '请输入密码', trigger: 'blur' }]
  }),
  departmentId: [{ required: true, message: '请选择所属科室', trigger: 'change' }],
  title: [{ required: true, message: '请输入职称', trigger: 'blur' }],
  specialty: [{ required: true, message: '请输入专长', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const fetchDoctors = async () => {
  loading.value = true
  try {
    const res = await listAllDoctors()
    console.log('获取到的医生数据:', res)
    
    if (res && Array.isArray(res)) {
      doctorList.value = res
    } else if (res && typeof res === 'object' && 'data' in res && Array.isArray(res.data)) {
      doctorList.value = res.data
    } else {
      doctorList.value = []
      ElMessage.warning('未获取到医生数据')
    }
  } catch (e) {
    console.error('获取医生列表失败:', e)
    ElMessage.error('获取医生列表失败')
    doctorList.value = []
  } finally {
    loading.value = false
  }
}

const fetchDepartments = async () => {
  try {
    const res = await listAllDepartments()
    if (res && Array.isArray(res)) {
      departmentList.value = res
    } else if (res && typeof res === 'object' && 'data' in res && Array.isArray(res.data)) {
      departmentList.value = res.data
    } else {
      departmentList.value = []
      console.error('科室数据格式不正确:', res)
    }
  } catch (e) {
    console.error('获取科室列表失败:', e)
    departmentList.value = []
  }
}

const openAddDialog = () => {
  isEdit.value = false
  // 重置表单
  Object.assign(form, {
    id: null, 
    doctorName: '', 
    username: '', 
    password: '',
    departmentId: null, 
    title: '', 
    specialty: '', 
    status: 1,
    userId: null
  })
  dialogVisible.value = true
}

const openEditDialog = async (row: any) => {
  isEdit.value = true
  console.log('编辑的医生数据:', row);
  
  // 重置表单并设置基本信息
  Object.assign(form, {
    id: row.id,
    doctorName: row.doctorName || row.realName || '',
    username: row.username || row.userName || '', // 尝试从行数据中获取用户名
    password: '',
    departmentId: row.departmentId,
    title: row.title || '',
    specialty: row.specialty || '',
    status: row.status === undefined ? 1 : row.status,
    userId: row.userId || null
  });
  
  // 显示对话框
  dialogVisible.value = true;
}

const handleSubmit = () => {
  formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    
    // 额外检查username字段
    if (!form.username && usernameInputRef.value) {
      // 尝试从输入框直接获取值
      const inputValue = usernameInputRef.value.value;
      if (inputValue) {
        console.log('从输入框直接获取到用户名:', inputValue);
        form.username = inputValue;
      }
    }
    
    // 再次检查用户名
    if (!form.username) {
      ElMessage.error('用户名不能为空');
      return;
    }
    
    // 新增时检查密码字段
    if (!isEdit.value && !form.password) {
      ElMessage.error('新增医生时密码不能为空');
      return;
    }
    
    try {
      console.log('提交的表单数据:', JSON.stringify(form));
      
      // 构造请求数据，确保与后端期望的格式匹配
      const doctorData: any = {
        doctorName: form.doctorName,
        departmentId: form.departmentId,
        title: form.title,
        specialty: form.specialty,
        status: form.status,
        username: form.username, // 直接添加用户名
      }
      
      // 只有在密码不为空时才添加密码字段
      if (form.password) {
        doctorData.password = form.password;
      }
      
      // 如果是编辑模式，添加ID和userId
      if (isEdit.value) {
        doctorData.id = form.id;
        if (form.userId) {
          doctorData.userId = form.userId;
        }
      }
      
      console.log('发送到后端的数据:', JSON.stringify(doctorData));
      
      if (isEdit.value) {
        const res = await request.put(`/api/doctors/${form.id}`, doctorData);
        console.log('编辑医生响应:', res);
        ElMessage.success('编辑成功');
      } else {
        console.log('发送新增医生请求，完整数据:', JSON.stringify(doctorData, null, 2));
        
        // 直接使用axios发送请求，以便查看更多细节
        try {
          const res = await request.post('/api/doctors', doctorData);
          console.log('新增医生响应:', res);
          ElMessage.success('新增成功');
        } catch (error: any) {
          console.error('新增医生请求失败:', error);
          console.error('请求配置:', error.config);
          console.error('请求数据:', error.config?.data);
          console.error('响应状态:', error.response?.status);
          console.error('响应数据:', error.response?.data);
          throw error;  // 重新抛出异常以便后续处理
        }
      }
      dialogVisible.value = false;
      fetchDoctors();
    } catch (e: any) {
      console.error('提交失败:', e);
      console.error('错误详情:', e.response?.data || e.message);
      ElMessage.error(isEdit.value ? '编辑失败' : '新增失败');
    }
  });
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确认删除医生 ${row.doctorName || row.userName || '未知姓名'} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/api/doctors/${row.id}`);
      ElMessage.success('删除成功');
      fetchDoctors();
    } catch (e) {
      console.error('删除失败:', e);
      ElMessage.error('删除失败');
    }
  }).catch(() => {});
}

// 添加对话框打开后的处理函数
const onDialogOpened = async () => {
  if (isEdit.value && form.id) {
    try {
      // 获取医生详细信息
      const response = await getDoctorById(form.id);
      console.log('获取到的医生详情:', response);
      
      // 从响应中提取数据
      const doctorRes = response.data || response;
      
      // 更新表单数据
      if (doctorRes) {
        // 更新表单基本信息
        form.doctorName = doctorRes.doctorName || doctorRes.realName || form.doctorName;
        form.departmentId = doctorRes.departmentId || form.departmentId;
        form.title = doctorRes.title || form.title;
        form.specialty = doctorRes.specialty || form.specialty;
        form.status = doctorRes.status !== undefined ? doctorRes.status : form.status;
        form.userId = doctorRes.userId || form.userId;
        
        // 直接从医生信息中获取用户名
        if (doctorRes.username) {
          form.username = doctorRes.username;
          console.log('从医生详情直接设置用户名:', form.username);
        }
      }
    } catch (e) {
      console.error('获取医生详情失败:', e);
    }
  }
}

onMounted(() => {
  fetchDoctors()
  fetchDepartments()
})
</script>