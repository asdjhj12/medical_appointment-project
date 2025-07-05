<template>
  <el-dialog title="查看病历" :visible.sync="visible" width="650px">
    <el-form :model="medicalRecord" label-width="90px" label-position="left">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="患者姓名">
            <el-input v-model="medicalRecord.patientName" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="科室">
            <el-input v-model="medicalRecord.departmentName" disabled />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="24">
          <el-form-item label="症状描述">
            <el-input v-model="medicalRecord.description" disabled />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="诊断结果">
            <el-input v-model="medicalRecord.diagnosis" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="治疗方案">
            <el-input v-model="medicalRecord.treatmentPlan" disabled />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="24">
          <el-form-item label="用药">
            <el-table :data="medicalRecord.medicines" size="small" border style="width:100%;margin-bottom:0;">
              <el-table-column prop="medicineName" label="药品名称" min-width="120" align="center" />
              <el-table-column prop="quantity" label="数量" width="80" align="center" />
              <el-table-column prop="dosageUsage" label="用法" min-width="200" align="center">
                <template #default="{ row }">
                  <div style="white-space:pre-line;word-break:break-all;max-width:350px;line-height:1.6;">{{ row.dosageUsage || row.usage }}</div>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="备注">
            <el-input v-model="medicalRecord.remarks" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="就诊时间">
            <el-input v-model="medicalRecord.visitTime" disabled />
          </el-form-item>
        </el-col>
      </el-row>
      <template #footer>
        <div style="text-align: right; padding-right: 24px;">
          <el-button @click="visible = false">关闭</el-button>
        </div>
      </template>
    </el-form>
  </el-dialog>
</template>

<script>
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
};
</script> 