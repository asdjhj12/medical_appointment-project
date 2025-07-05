<template>
  <el-dialog :title="isEdit ? '编辑电子病历' : '电子病历详情'" :visible.sync="visible" width="50%">
    <el-form :model="form" :rules="rules" ref="form" label-width="120px" :disabled="!isEdit">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="预约ID" prop="appointmentId">
            <el-input v-model="form.appointmentId" disabled></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="患者姓名" prop="patientName">
            <el-input v-model="form.patientName" disabled></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="医生姓名" prop="doctorName">
            <el-input v-model="form.doctorName" disabled></el-input>
          </el-form-item>
        </el-col>
         <el-col :span="12">
          <el-form-item label="创建时间" prop="createTime">
            <el-input v-model="form.createTime" disabled></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="诊断结果" prop="diagnosis">
        <el-input v-model="form.diagnosis" type="textarea" :rows="4"></el-input>
      </el-form-item>
      <el-form-item label="治疗方案" prop="treatmentPlan">
        <el-input v-model="form.treatmentPlan" type="textarea" :rows="4"></el-input>
      </el-form-item>
       <el-form-item label="备注" prop="remarks">
        <el-input v-model="form.remarks" type="textarea" :rows="3"></el-input>
      </el-form-item>
      <el-form-item label="用药">
        <el-select v-model="selectedMedicines" multiple filterable placeholder="请选择药品" style="width: 100%;margin-bottom:12px;">
          <el-option v-for="item in medicineList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-table :data="form.medicines" size="small" border style="width:100%;margin-bottom:16px;" :key="form.medicines.length">
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
        <pre>form.medicines: {{ form.medicines }}</pre>
        <pre>typeof: {{ typeof form.medicines }}</pre>
        <pre>length: {{ form.medicines.length }}</pre>
        <pre>first: {{ form.medicines[0] }}</pre>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">{{ isEdit ? '取 消' : '关 闭' }}</el-button>
      <el-button v-if="isEdit" type="primary" @click="submitForm">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateMedicalRecord } from '@/api/medical-record'; // 假设您有对应的API文件和方法
import { ElMessage } from 'element-plus';
import { getMedicineInventoryList } from '@/api/medicineInventory'

export default {
  name: 'MedicalRecordForm',
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    formData: {
      type: Object,
      default: () => ({}),
    },
    isEdit: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      form: {
        id: null,
        appointmentId: null,
        patientId: null,
        doctorId: null,
        diagnosis: '',
        treatmentPlan: '',
        remarks: '',
        // 以下为用于展示的非数据库字段，不需要验证
        patientName: '',
        doctorName: '',
        createTime: '',
      },
      rules: {
        diagnosis: [
          { required: true, message: '请输入诊断结果', trigger: 'blur' },
          { max: 1000, message: '长度不能超过1000个字符', trigger: 'blur' },
        ],
         treatmentPlan: [
           { max: 1000, message: '长度不能超过1000个字符', trigger: 'blur' },
         ],
        remarks: [
           { max: 500, message: '长度不能超过500个字符', trigger: 'blur' },
         ],
      },
      medicineList: [],
      selectedMedicines: [],
    };
  },
  watch: {
    visible(val) {
      if (val) {
        this.form = { ...this.formData };
        this.fetchMedicineList();
        this.selectedMedicines = this.form.medicines ? this.form.medicines.map(i => i.medicineId) : [];
        this.form.medicines = this.selectedMedicines.map(id => {
          const exist = this.form.medicines?.find(i => i.medicineId === id)
          const med = this.medicineList.find(m => m.id === id)
          return exist || {
            medicineId: id,
            medicineName: med ? med.name : '',
            quantity: 1,
            unit: med ? med.unit : '',
            dosageUsage: ''
          }
        })
      }
    },
    selectedMedicines(newVal) {
      this.form.medicines = newVal.map(id => {
        const exist = this.form.medicines?.find(i => i.medicineId === id)
        const med = this.medicineList.find(m => m.id === id)
        return exist || {
          medicineId: id,
          medicineName: med ? med.name : '',
          quantity: 1,
          unit: med ? med.unit : '',
          dosageUsage: ''
        }
      })
    }
  },
  methods: {
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          updateMedicalRecord(this.form).then(() => {
            ElMessage.success('更新成功');
            this.$emit('success');
            this.visible = false;
          }).catch(error => {
            ElMessage.error('更新失败');
            console.error(error);
          });
        }
      });
    },
    async fetchMedicineList() {
      const res = await getMedicineInventoryList({ status: 1 })
      this.medicineList = res || []
    },
  },
};
</script> 