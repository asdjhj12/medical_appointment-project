<template>
  <div class="profile-container">
    <div class="profile-center">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>个人信息</span>
            <div>
              <el-button type="primary" @click="handleEdit" style="margin-right: 10px;">编辑</el-button>
              <el-button type="primary" @click="showPwdDialog = true">修改密码</el-button>
            </div>
          </div>
        </template>
        <div class="user-info">
          <div class="avatar-container">
            <el-avatar :size="100" :src="avatarUrl">
              {{ userInfo.realName?.charAt(0) || userInfo.name?.charAt(0) || userInfo.username?.charAt(0) }}
            </el-avatar>
          </div>
          <div class="info-list">
            <div class="info-item">
              <span class="label">用户名：</span>
              <span class="value">{{ userInfo.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">姓名：</span>
              <span class="value">{{ userInfo.realName || userInfo.name }}</span>
            </div>
            <div class="info-item">
              <span class="label">性别：</span>
              <span class="value">{{ userInfo.gender === 1 ? '男' : userInfo.gender === 2 ? '女' : '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">手机号：</span>
              <span class="value">{{ userInfo.phone }}</span>
            </div>
            <div class="info-item">
              <span class="label">邮箱：</span>
              <span class="value">{{ userInfo.email }}</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="showPwdDialog" title="修改密码" width="500px">
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showPwdDialog = false">取消</el-button>
          <el-button type="primary" @click="handleUpdatePassword">修改密码</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑个人信息对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑个人信息"
      width="500px"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="editForm.realName" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="editForm.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/store/user'
import logo from '@/assets/logo.png'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo || {})

// 页面加载时获取最新用户信息
onMounted(async () => {
  try {
    await userStore.getUserInfo()
    console.log('用户信息加载成功:', userStore.userInfo)
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
})

// 头像显示逻辑
const avatarUrl = computed(() => userInfo.value.avatar ? userInfo.value.avatar : logo)

// 修改密码弹窗控制
const showPwdDialog = ref(false)

// 修改密码表单
const passwordFormRef = ref<FormInstance>()
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules: FormRules = {
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
const dialogVisible = ref(false)
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
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await userStore.updatePassword(passwordForm)
        ElMessage.success('密码修改成功')
        passwordFormRef.value?.resetFields()
        showPwdDialog.value = false
      } catch (error: any) {
        ElMessage.error(error.message || '密码修改失败')
      }
    }
  })
}

// 处理编辑个人信息
const handleEdit = () => {
  console.log('userInfo value:', userInfo.value);
  console.log('realName:', userInfo.value.realName);
  console.log('name:', userInfo.value.name);
  
  editForm.realName = userInfo.value.realName || userInfo.value.name || '';
  editForm.gender = userInfo.value.gender || 1;
  editForm.phone = userInfo.value.phone || '';
  editForm.email = userInfo.value.email || '';
  
  console.log('editForm after assignment:', editForm);
  
  dialogVisible.value = true;
}

const handleSave = async () => {
  if (!editFormRef.value) return
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await userStore.updateUserInfo(editForm)
        await userStore.getUserInfo() // 保存后刷新用户信息
        ElMessage.success('个人信息修改成功')
        dialogVisible.value = false
      } catch (error: any) {
        ElMessage.error(error.message || '个人信息修改失败')
      }
    }
  })
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
}
.profile-center {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
}
.el-card {
  width: 420px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.user-info {
  display: flex;
  flex-direction: column;
}
.avatar-container {
  text-align: center;
  margin-bottom: 20px;
}
.info-list {
  margin-top: 20px;
}
.info-item {
  margin-bottom: 15px;
  display: flex;
}
.label {
  width: 80px;
  color: #606266;
  font-weight: bold;
}
.value {
  color: #303133;
  flex: 1;
}
</style> 