<template>
  <div class="doctor-profile-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <div>
            <el-button type="primary" @click="handleEdit" style="margin-right: 10px;">编辑</el-button>
            <el-button type="primary" @click="pwdDialogVisible=true">修改密码</el-button>
          </div>
        </div>
      </template>
      <div class="user-info">
        <el-avatar :size="100" :src="avatarUrl">{{ user?.realName?.charAt(0) || user?.username?.charAt(0) }}</el-avatar>
        <div class="info-list">
          <div class="info-item"><span class="label">用户名：</span><span class="value">{{ user?.username }}</span></div>
          <div class="info-item"><span class="label">姓名：</span><span class="value">{{ user?.realName || user?.name }}</span></div>
          <div class="info-item"><span class="label">性别：</span><span class="value">{{ user?.gender === 1 ? '男' : user?.gender === 0 ? '女' : '未设置' }}</span></div>
          <div class="info-item"><span class="label">手机号：</span><span class="value">{{ user?.phone }}</span></div>
          <div class="info-item"><span class="label">邮箱：</span><span class="value">{{ user?.email }}</span></div>
        </div>
      </div>
    </el-card>
    <!-- 编辑个人信息对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑个人信息" width="500px">
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="100px">
        <el-form-item label="姓名" prop="realName"><el-input v-model="editForm.realName" /></el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="editForm.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号" prop="phone"><el-input v-model="editForm.phone" /></el-form-item>
        <el-form-item label="邮箱" prop="email"><el-input v-model="editForm.email" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible=false">取消</el-button>
        <el-button type="primary" @click="() => { handleSave(); }">确定</el-button>
      </template>
    </el-dialog>
    <!-- 修改密码对话框 -->
    <el-dialog v-model="pwdDialogVisible" title="修改密码" width="400px">
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword"><el-input v-model="pwdForm.oldPassword" type="password" /></el-form-item>
        <el-form-item label="新密码" prop="newPassword"><el-input v-model="pwdForm.newPassword" type="password" /></el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword"><el-input v-model="pwdForm.confirmPassword" type="password" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialogVisible=false">取消</el-button>
        <el-button type="primary" @click="handleUpdatePassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/store/user'
import doctorAvatar from '@/assets/doctor.jpg'
import type { UserInfo } from '@/types/user'

const userStore = useUserStore()
const userInfo = computed<Partial<UserInfo>>(() => userStore.userInfo || {})
const user = computed<Partial<UserInfo>>(() => userInfo.value || {})

// 页面加载时获取最新用户信息
onMounted(async () => {
  try {
    await userStore.getUserInfo()
    console.log('用户信息加载成功:', userStore.userInfo)
    Object.assign(editForm, {
      realName: userInfo.value?.realName || userInfo.value?.name || '',
      gender: typeof userInfo.value?.gender === 'number' ? userInfo.value?.gender : 1,
      phone: userInfo.value?.phone || '',
      email: userInfo.value?.email || ''
    })
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
})

// 头像显示逻辑
const avatarUrl = computed(() => doctorAvatar)

// 修改密码弹窗控制
const pwdDialogVisible = ref(false)

// 修改密码表单
const pwdFormRef = ref<FormInstance>()
const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const pwdRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 编辑个人信息表单
const editDialogVisible = ref(false)
const editFormRef = ref<FormInstance>()
const editForm = reactive({
  realName: '',
  gender: 1,
  phone: '',
  email: ''
})

const editRules: FormRules = {
  realName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 处理修改密码
const handleUpdatePassword = async () => {
  if (!pwdFormRef.value) {
    return
  }
  await pwdFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await userStore.updatePassword(pwdForm)
        ElMessage.success('密码修改成功')
        pwdFormRef.value?.resetFields()
        pwdDialogVisible.value = false
      } catch (error: any) {
        ElMessage.error(error.message || '密码修改失败')
      }
    }
  })
}

// 处理编辑个人信息
const handleEdit = () => {
  Object.assign(editForm, {
    realName: userInfo.value?.realName || userInfo.value?.name || '',
    gender: typeof userInfo.value?.gender === 'number' ? userInfo.value?.gender : 1,
    phone: userInfo.value?.phone || '',
    email: userInfo.value?.email || ''
  })
  editDialogVisible.value = true
}

const handleSave = async () => {
  if (!editFormRef.value) {
    return
  }
  editFormRef.value.validate((valid: boolean) => {
    if (valid) {
      try {
        const submitData = {
          ...editForm,
          username: userInfo.value?.username,
          role: userInfo.value?.role,
          status: userInfo.value?.status
        }
        userStore.updateUserInfo(submitData)
          .then(() => userStore.getUserInfo())
          .then(() => {
            ElMessage.success('个人信息修改成功')
            editDialogVisible.value = false
          })
          .catch((error: any) => {
            ElMessage.error(error.message || '个人信息修改失败')
          })
      } catch (error: any) {
        ElMessage.error(error.message || '个人信息修改失败')
      }
    }
  })
}
</script>

<style scoped>
.doctor-profile-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.user-info { display: flex; flex-direction: column; align-items: center; }
.info-list { margin-top: 20px; }
.info-item { margin-bottom: 15px; display: flex; }
.label { width: 80px; color: #888; }
.value { font-weight: bold; }
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.avatar-upload-placeholder {
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #8c939d;
  font-size: 14px;
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  background: #fafbfc;
  cursor: pointer;
  transition: border-color 0.3s;
}
.avatar-upload-placeholder:hover {
  border-color: #409EFF;
  color: #409EFF;
}
.avatar {
  width: 100px;
  height: 100px;
  display: block;
  border-radius: 50%;
}
</style> 