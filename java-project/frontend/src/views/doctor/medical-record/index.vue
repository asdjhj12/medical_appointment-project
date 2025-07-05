<template>
  <div class="doctor-medical-record-list">
    <h2>病历管理</h2>
    <el-table :data="medicalRecordList" v-loading="loading">
      <el-table-column prop="id" label="病历编号" width="100" />
      <el-table-column prop="patientName" label="患者姓名" width="120" />
      <el-table-column prop="departmentName" label="科室" width="120" />
      <el-table-column prop="description" label="症状描述" width="180" />
      <el-table-column prop="diagnosis" label="诊断结果" width="180" />
      <el-table-column prop="treatmentPlan" label="治疗方案" width="180" />
      <el-table-column prop="visitTime" label="就诊时间" width="160" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="viewRecord(row)">查看</el-button>
          <el-button type="success" size="small" @click="editRecord(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" :title="dialogType === 'view' ? '查看病历' : '编辑病历'" width="600px">
      <el-form :model="currentRecord" label-width="100px">
        <el-form-item label="患者姓名"><el-input v-model="currentRecord.patientName" :disabled="true" /></el-form-item>
        <el-form-item label="科室"><el-input v-model="currentRecord.departmentName" :disabled="true" /></el-form-item>
        <el-form-item label="症状描述"><el-input v-model="currentRecord.description" :disabled="dialogType==='view'" /></el-form-item>
        <el-form-item label="诊断结果"><el-input v-model="currentRecord.diagnosis" :disabled="dialogType==='view'" /></el-form-item>
        <el-form-item label="治疗方案"><el-input v-model="currentRecord.treatmentPlan" :disabled="dialogType==='view'" /></el-form-item>
        <el-form-item label="用药" v-if="dialogType==='view' && currentRecord.medicines && currentRecord.medicines.length">
          <el-table :data="currentRecord.medicines" size="small" border style="width:100%;margin-bottom:8px;">
            <el-table-column prop="medicineName" label="药品名称" />
            <el-table-column prop="quantity" label="数量" width="100">
              <template #default="{ row }">
                <el-input-number :model-value="row.quantity" :min="1" :max="9999" size="small" style="width:80px" disabled />
              </template>
            </el-table-column>
            <el-table-column prop="dosageUsage" label="用法">
              <template #default="{ row }">
                <div style="white-space:pre-line;word-break:break-all;max-width:300px;">{{ row.dosageUsage }}</div>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        <el-form-item label="用药" v-if="dialogType==='edit'">
          <el-select v-model="selectedMedicines" multiple filterable placeholder="请选择药品" style="width: 100%;margin-bottom:12px;">
            <el-option v-for="item in medicineList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
          <el-table :data="currentRecord.medicines" size="small" border style="width:100%;margin-bottom:16px;">
            <el-table-column prop="medicineName" label="药品名称" min-width="120" align="center" />
            <el-table-column prop="quantity" label="数量" width="120" align="center">
              <template #default="{ row }">
                <el-input-number v-model="row.quantity" :min="1" :max="9999" size="small" style="width:90px;" />
              </template>
            </el-table-column>
            <el-table-column prop="dosageUsage" label="用法" min-width="200" align="center">
              <template #default="{ row }">
                <el-input v-model="row.dosageUsage" size="small" style="white-space:pre-line;word-break:break-all;max-width:350px;line-height:1.6;" />
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="currentRecord.remarks" :disabled="dialogType==='view'" /></el-form-item>
        <el-form-item label="就诊时间"><el-input v-model="currentRecord.visitTime" :disabled="true" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">关闭</el-button>
        <el-button v-if="dialogType==='edit'" type="primary" @click="saveRecord">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getMedicalRecordsByDoctorId, updateMedicalRecord, getMedicalRecordDetail } from '@/api/medical-record'
import { getMedicineInventoryList } from '@/api/medicineInventory'
import { getDoctorByUserId } from '@/api/doctor'
import dayjs from 'dayjs'

const loading = ref(false)
const medicalRecordList = ref([])
const dialogVisible = ref(false)
const dialogType = ref('view')
const currentRecord = ref({})

// 新增：药品相关
const medicineList = ref([])
const selectedMedicines = ref([]) // 选中的药品id数组

const fetchMedicineList = async () => {
  const res = await getMedicineInventoryList({ status: 1 })
  medicineList.value = res || []
  console.log('药品列表 medicineList.value:', medicineList.value)
}
const viewRecord = async (row) => {
  dialogType.value = 'view'
  // 通过ID获取后端详情，保证 medicines、visitTime 等字段完整
  const res = await getMedicalRecordDetail(row.id)
  let detail = res?.data || res
  currentRecord.value = { ...detail }
  dialogVisible.value = true
}
const editRecord = async (row) => {
  dialogType.value = 'edit'
  const res = await getMedicalRecordDetail(row.id)
  let detail = res?.data || res
  currentRecord.value = { ...detail }
  await fetchMedicineList()
  selectedMedicines.value = detail.medicines ? detail.medicines.map(i => i.medicineId) : []
  currentRecord.value.medicines = selectedMedicines.value.map(id => {
    const exist = detail.medicines?.find(i => i.medicineId === id)
    const med = medicineList.value.find(m => m.id === id)
    return exist || {
      medicineId: id,
      medicineName: med ? med.name : '',
      quantity: 1,
      unit: med ? med.unit : '',
      dosageUsage: ''
    }
  })
  dialogVisible.value = true
}
watch(selectedMedicines, (newVal) => {
  currentRecord.value.medicines = newVal.map(id => {
    const exist = currentRecord.value.medicines?.find(i => i.medicineId === id)
    const med = medicineList.value.find(m => m.id === id)
    return exist || {
      medicineId: id,
      medicineName: med ? med.name : '',
      quantity: 1,
      unit: med ? med.unit : '',
      dosageUsage: ''
    }
  })
})
const saveRecord = async () => {
  loading.value = true
  try {
    currentRecord.value.visitTime = dayjs().format('YYYY-MM-DD HH:mm:ss')
    const medicines = currentRecord.value.medicines
    console.log('即将保存的medicines:', medicines)
    console.log('medicineList:', medicineList.value)
    const payload = {
      ...currentRecord.value,
      medicines
    }
    console.log('保存时 payload：', payload)
    await updateMedicalRecord(payload)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchList()
  } catch (e) {
    ElMessage.error('保存失败')
    console.error('保存失败', e)
  }
  loading.value = false
}
const fetchList = async () => {
  loading.value = true
  try {
    const userStore = useUserStore()
    const userId = userStore.userInfo?.id
    let doctorId = null
    if (userId) {
      const doctorRes = await getDoctorByUserId(userId)
      if (doctorRes && doctorRes.id) {
        doctorId = doctorRes.id
      }
    }
    console.log('最终doctorId:', doctorId)
    if (doctorId) {
      const res = await getMedicalRecordsByDoctorId(doctorId)
      console.log('接口返回的res', res)
      if (Array.isArray(res)) {
        medicalRecordList.value = res
        console.log('赋值后medicalRecordList', medicalRecordList.value)
      } else if (res && Array.isArray(res.data)) {
        medicalRecordList.value = res.data
        console.log('赋值后medicalRecordList', medicalRecordList.value)
      } else if (res && res.data && Array.isArray(res.data.data)) {
        medicalRecordList.value = res.data.data
        console.log('赋值后medicalRecordList', medicalRecordList.value)
      } else {
        medicalRecordList.value = []
        console.log('赋值后medicalRecordList为空')
      }
    }
  } catch (e) {
    ElMessage.error('获取病历失败')
    console.error(e)
  } finally {
    loading.value = false
  }
}
onMounted(fetchList)

const takeMedicine = async (recordId) => {
  console.log('takeMedicine方法被调用，recordId=', recordId)
  try {
    const res = await takeMedicineApi({ recordId })
    console.log('takeMedicine接口返回res：', res)
    const result = res.data ? res.data : res
    console.log('最终判断用result：', result)
    if (result.code === 200) {
      ElMessage.success('取药成功')
    } else {
      ElMessage.error(result.message || '取药失败')
    }
  } catch (e) {
    ElMessage.error('取药失败')
    console.error('取药失败', e)
  }
}

</script>

<style scoped>
.doctor-medical-record-list {
  padding: 20px;
}
.el-table th, .el-table td {
  font-size: 14px;
}
.el-table {
  border-radius: 6px;
  overflow: hidden;
}
.el-form-item {
  margin-bottom: 18px;
}
</style> 