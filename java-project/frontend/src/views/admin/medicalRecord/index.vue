<template>
    <div class="admin-medical-record-list">
      <h2>电子病历管理</h2>
      <el-table :data="medicalRecordList" :key="medicalRecordList.length" v-loading="loading">
        <el-table-column prop="id" label="病历编号" width="100" />
        <el-table-column prop="appointmentNumber" label="预约编号" width="160" />
        <el-table-column prop="patientName" label="患者姓名" width="120" />
        <el-table-column prop="doctorName" label="医生姓名" width="120" />
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
        <el-form v-if="currentRecord" :model="currentRecord" label-width="100px">
          <el-form-item label="预约编号"><el-input v-model="currentRecord.appointmentNumber" :disabled="true" /></el-form-item>
          <el-form-item label="患者姓名"><el-input v-model="currentRecord.patientName" :disabled="true" /></el-form-item>
          <el-form-item label="医生姓名"><el-input v-model="currentRecord.doctorName" :disabled="true" /></el-form-item>
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

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllMedicalRecords, getMedicalRecordDetail, updateMedicalRecord } from '@/api/medical-record'
import { getMedicineInventoryList } from '@/api/medicineInventory'

interface Medicine {
  medicineId: number
  medicineName: string
  quantity: number
  dosageUsage: string
  unit?: string
}

interface MedicalRecord {
  id: number
  appointmentNumber: string
  patientName: string
  doctorName: string
  departmentName: string
  description: string
  diagnosis: string
  treatmentPlan: string
  visitTime: string
  remarks?: string
  medicines?: Medicine[]
}

const loading = ref(false)
const medicalRecordList = ref<MedicalRecord[]>([])
const dialogVisible = ref(false)
const dialogType = ref<'view'|'edit'>('view')
const currentRecord = ref<MedicalRecord | any>({})
const medicineList = ref<any[]>([])
const selectedMedicines = ref<number[]>([])
const rawRes = ref<any>(null)
const rawData = ref<any>(null)
const rawType = ref<string>('')
const rawString = ref<string>('')

const fetchMedicineList = async () => {
  const res = await getMedicineInventoryList({ status: 1 })
  medicineList.value = Array.isArray(res) ? res : (res?.data || [])
}

const fetchList = async () => {
  (window as any)._fetchListCalled = true

  loading.value = true
  try {
    const res = await getAllMedicalRecords()
    // 这里的 res 直接就是数组，不需要 res.data.data
    console.log('getAllMedicalRecords 返回：', res)
    medicalRecordList.value = Array.isArray(res) ? res : []
    (window as any)._testList = res
    (window as any)._medicalRecordList = medicalRecordList.value

  } catch (e) {
    console.error('fetchList error', e)
  } finally {
    loading.value = false
  }
}

const viewRecord = async (row: MedicalRecord) => {
  dialogType.value = 'view'
  const res = await getMedicalRecordDetail(row.id)
  let detail = res?.data || res
  currentRecord.value = { ...detail }
  dialogVisible.value = true
}

const editRecord = async (row: MedicalRecord) => {
  dialogType.value = 'edit'
  const res = await getMedicalRecordDetail(row.id)
  let detail = res?.data || res
  currentRecord.value = { ...detail }
  await fetchMedicineList()
  selectedMedicines.value = detail.medicines ? detail.medicines.map((i: any) => i.medicineId) : []
  currentRecord.value.medicines = selectedMedicines.value.map(id => {
    const exist = detail.medicines?.find((i: any) => i.medicineId === id)
    const med = medicineList.value.find((m: any) => m.id === id)
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
    const exist = currentRecord.value.medicines?.find((i: any) => i.medicineId === id)
    const med = medicineList.value.find((m: any) => m.id === id)
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
    const medicines = currentRecord.value.medicines
    const payload = {
      ...currentRecord.value,
      medicines
    }
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

onMounted(fetchList)
</script>

<style scoped>
.admin-medical-record-list {
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