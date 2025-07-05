<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="card-header">
          <h2>用户注册</h2>
        </div>
      </template>
      
      <el-form
        :model="formData"
        label-width="100px"
        class="register-form"
      >
        <!-- 第一步：基本信息 -->
        <div v-if="currentStep === 1">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="formData.username" placeholder="请输入用户名" />
          </el-form-item>
          
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="formData.password"
              type="password"
              placeholder="请输入密码"
              show-password
            />
          </el-form-item>
          
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="formData.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              show-password
            />
          </el-form-item>
          
          <el-form-item label="角色" prop="role">
            <el-radio-group v-model="formData.role">
              <el-radio :label="'USER'">用户</el-radio>
              <el-radio :label="'DOCTOR'">医生</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="formData.phone" placeholder="请输入手机号">
            </el-input>
          </el-form-item>
          
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="formData.email" placeholder="请输入邮箱" />
          </el-form-item>
        </div>

        <!-- 第二步：个人信息 -->
        <div v-if="currentStep === 2">
          <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
          </el-form-item>
          
          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="formData.gender">
              <el-radio :label="1">男</el-radio>
              <el-radio :label="0">女</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="出生日期" prop="birthDate">
            <el-date-picker
              v-model="formData.birthDate"
              type="date"
              placeholder="请选择出生日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          
          <el-form-item label="身份证号" prop="idCard">
            <el-input v-model="formData.idCard" placeholder="请输入身份证号" />
          </el-form-item>
          
          <el-form-item label="地址" prop="address">
            <el-input
              v-model="formData.address"
              type="textarea"
              placeholder="请输入详细地址"
            />
          </el-form-item>
        </div>

        <!-- 按钮组 -->
        <el-form-item>
          <el-button
            v-if="currentStep === 2"
            @click="currentStep--"
            class="step-button"
          >
            上一步
          </el-button>
          
          <el-button @click="goToLogin" class="step-button">
            返回登录
          </el-button>
          
          <el-button
            v-if="currentStep === 1"
            type="primary"
            @click="nextStep"
            class="step-button"
          >
            下一步
          </el-button>
          
          <el-button
            v-if="currentStep === 2"
            type="primary"
            @click="handleRegister"
            class="step-button"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/user'

const router = useRouter()
const currentStep = ref(1)

const formData = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  email: '',
  realName: '',
  gender: 1,
  birthDate: '',
  idCard: '',
  address: '',
  role: 'USER'
})

const nextStep = () => {
  // 手动进行第一步表单验证，逐个字段检查
  if (!formData.username) {
    ElMessage.error('请输入用户名')
    return
  }
  if (formData.username.length < 4 || formData.username.length > 20) {
    ElMessage.error('用户名长度在 4 到 20 个字符')
    return
  }
  if (!formData.password) {
    ElMessage.error('请输入密码')
    return
  }
  if (formData.password.length < 6 || formData.password.length > 20) {
    ElMessage.error('密码长度在 6 到 20 个字符')
    return
  }
  if (!formData.confirmPassword) {
    ElMessage.error('请再次输入密码')
    return
  }
  if (formData.password !== formData.confirmPassword) {
    ElMessage.error('两次输入密码不一致!')
    return
  }
  if (!formData.role) {
    ElMessage.error('请选择角色')
    return
  }
  if (!formData.phone) {
    ElMessage.error('请输入手机号')
    return
  }
  const phonePattern = /^1[3-9]\d{9}$/
  if (!phonePattern.test(formData.phone)) {
    ElMessage.error('请输入正确的手机号')
    return
  }

  // 邮箱必填检查
  if (!formData.email) {
    ElMessage.error('请输入邮箱')
    return
  }
  // 邮箱格式校验 - 使用更标准的正则表达式
  const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  if (!emailPattern.test(formData.email)) {
    ElMessage.error('请输入有效的邮箱地址')
    return
  }

  // 如果所有验证通过，则进入下一步
  currentStep.value++
}

const handleRegister = async () => {
  // 第二步验证（真实姓名、性别、出生日期、身份证号、地址）
  if (!formData.realName) {
    ElMessage.error('请输入真实姓名')
    return
  }
  if (formData.gender === null || formData.gender === undefined) {
    ElMessage.error('请选择性别')
    return
  }
  if (!formData.birthDate) {
    ElMessage.error('请选择出生日期')
    return
  }
  if (!formData.idCard) {
    ElMessage.error('请输入身份证号')
    return
  }
  const idCardPattern = /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[0-9Xx]$/
  if (!idCardPattern.test(formData.idCard)) {
    ElMessage.error('请输入正确的身份证号')
    return
  }
  if (!formData.address) {
    ElMessage.error('请输入详细地址')
    return
  }

  // 如果所有第二步验证通过，则执行注册
  try {
    // 注册时后端可能还需要再次校验部分字段（如手机号、身份证号是否重复）
    await register(formData)
    ElMessage.success('注册成功')
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.message || '注册失败')
  }
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.register-card {
  width: 500px;
}

.card-header {
  text-align: center;
}

.register-form {
  margin-top: 20px;
}

.step-button {
  margin-right: 10px;
}
</style> 