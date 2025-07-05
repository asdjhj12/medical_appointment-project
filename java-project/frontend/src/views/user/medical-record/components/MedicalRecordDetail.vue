<template>
  <el-dialog title="电子病历详情" :model-value="visible" @update:model-value="$emit('update:visible', $event)" width="700px">
    <el-form :model="medicalRecord" label-width="110px" label-position="left" style="margin-bottom:0;">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="预约编号：">
            <b>{{ medicalRecord.appointmentNumber }}</b>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="患者姓名：">
            <b>{{ medicalRecord.patientName }}</b>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="医生姓名：">
            {{ medicalRecord.doctorName }}
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="科室：">
            {{ medicalRecord.departmentName }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="就诊日期：">
            {{ showVisitTime }}
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="诊断结果：">
            {{ medicalRecord.diagnosis }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="治疗方案：">
            {{ medicalRecord.treatmentPlan }}
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="备注：">
            {{ medicalRecord.remarks }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="24">
          <el-form-item label="症状描述：">
            {{ medicalRecord.description }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="24">
          <el-form-item v-if="showMedicines.length" label="用药明细：">
            <el-table :data="showMedicines" size="small" border style="width:100%;margin-bottom:0;">
              <el-table-column prop="medicineName" label="药品名称" min-width="120" align="center" />
              <el-table-column prop="quantity" label="数量" width="80" align="center" />
              <el-table-column prop="dosageUsage" label="用法" min-width="200" align="center">
                <template #default="{ row }">
                  <div style="white-space:pre-line;word-break:break-all;max-width:350px;line-height:1.6;">{{ row.dosageUsage || row.usage }}</div>
                </template>
              </el-table-column>
            </el-table>
            <el-button
                v-if="showTakeMedicine"
                type="primary"
                style="margin-top: 10px"
                @click="() => { console.log('【用户端按钮被点击】', medicalRecord); takeMedicine() }"
            >去药房拿药</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <div style="text-align: right; padding-right: 24px;">
        <el-button @click="$emit('update:visible', false)">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script>
import { takeMedicine } from '@/api/medical-record'
export default {
  name: 'MedicalRecordDetail',
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    medicalRecord: {
      type: Object,
      default: () => ({}),
    },
  },
  computed: {
    showMedicines() {
      // 优先用 medicines 字段，兼容 medicineInfo
      if (this.medicalRecord.medicines && this.medicalRecord.medicines.length) {
        return this.medicalRecord.medicines
      } else if (this.medicalRecord.medicineInfo) {
        try {
          const arr = JSON.parse(this.medicalRecord.medicineInfo)
          // 兼容老数据字段
          return arr.map(i => ({
            medicineName: i.medicineName || i.name,
            quantity: i.quantity,
            dosageUsage: i.dosageUsage || i.usage
          }))
        } catch {
          return []
        }
      }
      return []
    },
    showVisitTime() {
      // 优先用 visitTime 字段，兼容 createTime
      return this.medicalRecord.visitTime || this.medicalRecord.createTime || ''
    },
    showTakeMedicine() {
      // 只有预约状态为2（已完成）才显示按钮
      return this.medicalRecord && (this.medicalRecord.appointmentStatus === 2 || this.medicalRecord.appointmentStatus === '2')
    }
  },
  methods: {
    async takeMedicine() {
      console.log('【用户端】takeMedicine方法被调用，recordId=', this.medicalRecord.id)
      try {
        const res = await takeMedicine({ recordId: this.medicalRecord.id })
        console.log('【用户端】takeMedicine接口返回：', res)
        // 只要没有异常就是成功
        this.$message.success('取药成功！')
        this.$emit('refresh')
        this.$emit('update:visible', false)
      } catch (e) {
        this.$message.error('取药失败')
        console.error(e)
      }
    }
  }
};
</script>

<style scoped>
.el-dialog__body {
  padding-top: 10px;
  padding-bottom: 0;
}
.el-form-item__label {
  font-weight: bold;
  color: #333;
}
.el-form-item {
  margin-bottom: 10px;
}
.el-table th, .el-table td {
  font-size: 14px;
}
.el-table {
  border-radius: 6px;
  overflow: hidden;
}
</style> 