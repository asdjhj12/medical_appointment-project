<template>
  <div class="user-management">
    <div class="search-container">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="queryParams.role" placeholder="请选择角色" clearable>
            <el-option
              v-for="item in userRoleOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option
              v-for="item in userStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="action-container">
      <el-button type="primary" @click="handleAdd">新增用户</el-button>
    </div>

    <el-table
      v-loading="loading"
      :data="userList"
      border
      style="width: 100%"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="姓名" width="120" />
      <el-table-column label="性别" width="80">
        <template #default="scope">
          {{ scope.row.gender === 1 ? '男' : '女' }}
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="120" />
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column label="角色" width="120">
        <template #default="scope">
          <el-tag :type="getRoleTagType(scope.row.role)">
            {{ getRoleLabel(scope.row.role) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" fixed="right" width="280">
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="warning" size="small" @click="handleResetPassword(scope.row)">重置密码</el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
        :current-page="queryParams.pageNum"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="queryParams.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 用户表单对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      append-to-body
      destroy-on-close
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userFormRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" :disabled="dialogType === 'edit'" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input v-model="userForm.password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="userForm.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="userForm.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色">
            <el-option
              v-for="item in userRoleOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="userForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, createUser, updateUser, deleteUser, resetPassword, updateUserStatus } from '@/api/user'
import type { UserInfo } from '@/types/user'
import { userRoleOptions, userStatusOptions, UserRole } from '@/types/user'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  name: '',
  phone: '',
  role: '',
  status: undefined
})

// 表格数据
const userList = ref<UserInfo[]>([])
const total = ref(0)
const loading = ref(false)

// 对话框控制
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const dialogTitle = computed(() => dialogType.value === 'add' ? '新增用户' : '编辑用户')

// 表单对象
const userFormRef = ref()
const userForm = reactive({
  id: undefined,
  username: '',
  password: '',
  realName: '',
  gender: 1,
  phone: '',
  email: '',
  role: '',
  status: 1
})

// 表单校验规则
const userFormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: false, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 获取用户列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getUserList(queryParams)
    userList.value = res.records
    total.value = res.total
  } catch (error) {
    console.error('获取用户列表失败', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 查询
const handleSearch = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置
const handleReset = () => {
  queryParams.username = ''
  queryParams.name = ''
  queryParams.phone = ''
  queryParams.role = ''
  queryParams.status = undefined
  queryParams.pageNum = 1
  getList()
}

// 分页
const handleSizeChange = (size: number) => {
  queryParams.pageSize = size
  getList()
}

const handleCurrentChange = (current: number) => {
  queryParams.pageNum = current
  getList()
}

// 新增用户
const handleAdd = () => {
  dialogType.value = 'add'
  resetForm()
  dialogVisible.value = true
}

// 编辑用户
const handleEdit = (row: UserInfo) => {
  dialogType.value = 'edit'
  resetForm()
  Object.assign(userForm, {
    id: row.id,
    username: row.username,
    realName: row.realName || row.name,
    gender: row.gender,
    phone: row.phone,
    email: row.email,
    role: row.role,
    status: row.status
  })
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  userForm.id = undefined
  userForm.username = ''
  userForm.password = ''
  userForm.realName = ''
  userForm.gender = 1
  userForm.phone = ''
  userForm.email = ''
  userForm.role = ''
  userForm.status = 1
}

// 提交表单
const handleSubmit = async () => {
  userFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    
    try {
      if (dialogType.value === 'add') {
        await createUser({
          username: userForm.username,
          password: userForm.password,
          realName: userForm.realName,
          gender: userForm.gender,
          phone: userForm.phone,
          email: userForm.email,
          role: userForm.role,
          status: userForm.status
        })
        ElMessage.success('新增用户成功')
      } else {
        if (userForm.id === undefined) {
          ElMessage.error('用户ID不能为空')
          return
        }
        await updateUser(userForm.id, {
          username: userForm.username,
          realName: userForm.realName,
          gender: userForm.gender,
          phone: userForm.phone,
          email: userForm.email,
          role: userForm.role,
          status: userForm.status
        })
        ElMessage.success('更新用户成功')
      }
      dialogVisible.value = false
      getList()
    } catch (error) {
      console.error('操作失败', error)
      ElMessage.error('操作失败')
    }
  })
}

// 删除用户
const handleDelete = (row: UserInfo) => {
  ElMessageBox.confirm(`确认删除用户"${row.username}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 重置密码
const handleResetPassword = (row: UserInfo) => {
  ElMessageBox.confirm(`确认重置用户"${row.username}"的密码吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await resetPassword(row.id)
      ElMessage.success('密码重置成功')
    } catch (error) {
      console.error('密码重置失败', error)
      ElMessage.error('密码重置失败')
    }
  }).catch(() => {})
}

// 更新用户状态
const handleStatusChange = async (row: UserInfo) => {
  try {
    await updateUserStatus(row.id, row.status)
    ElMessage.success(`${row.status === 1 ? '启用' : '禁用'}成功`)
  } catch (error) {
    console.error('状态更新失败', error)
    ElMessage.error('状态更新失败')
    // 恢复状态
    row.status = row.status === 1 ? 0 : 1
  }
}

// 获取角色标签类型
const getRoleTagType = (role: string) => {
  switch (role) {
    case UserRole.ADMIN:
      return 'danger'
    case UserRole.DOCTOR:
      return 'success'
    case UserRole.USER:
      return 'warning'
    default:
      return ''
  }
}

// 获取角色标签文本
const getRoleLabel = (role: string) => {
  const option = userRoleOptions.find(item => item.value === role)
  return option ? option.label : role
}

// 页面加载时获取数据
onMounted(() => {
  getList()
})
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.search-container {
  margin-bottom: 20px;
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.action-container {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style> 