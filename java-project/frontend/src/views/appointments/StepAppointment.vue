<template>
  <div>
    <el-steps :active="activeStep" finish-status="success" align-center>
      <el-step title="选择科室" />
      <el-step title="选择医生" />
      <el-step title="选择时间" />
      <el-step title="填写描述" />
      <el-step title="确认预约" />
    </el-steps>
    <div style="margin-top: 30px;">
      <component :is="currentStepComponent"
                 v-model="form"
                 @next="nextStep"
                 @prev="prevStep"
                 @submit="submitAppointment" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import Step1Department from './Step1Department.vue'
import Step2Doctor from './Step2Doctor.vue'
import Step3Schedule from './Step3Schedule.vue'
import Step4Description from './Step4Description.vue'
import Step5Confirm from './Step5Confirm.vue'
import { createAppointment } from '@/api/appointment'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getPatientByUserId, registerPatientByUserId } from '@/api/patient'

const activeStep = ref(0)
const form = ref({
  departmentId: '',
  doctorId: '',
  scheduleId: '',
  description: ''
})

const steps = [
  Step1Department,
  Step2Doctor,
  Step3Schedule,
  Step4Description,
  Step5Confirm
]

const currentStepComponent = computed(() => steps[activeStep.value])

function nextStep() { activeStep.value++ }
function prevStep() { activeStep.value-- }
const userStore = useUserStore()
async function submitAppointment() {
  try {
    // 先注册/获取 patientId
    let patient = await getPatientByUserId(userStore.userInfo.id).catch(async () => {
      await registerPatientByUserId(userStore.userInfo.id);
      return await getPatientByUserId(userStore.userInfo.id);
    });
    if (!patient || !patient.id) {
      ElMessage.error('患者信息注册失败');
      return;
    }
    await createAppointment({
      patientId: patient.id,
      doctorId: form.value.doctorId,
      scheduleId: form.value.scheduleId,
      description: form.value.description
    })
    ElMessage.success('预约成功！')
    activeStep.value = 0
    // 可选：emit关闭弹窗
    // emit('close')
  } catch (e) {
    ElMessage.error('预约失败')
  }
}
</script>