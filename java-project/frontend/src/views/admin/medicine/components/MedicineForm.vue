<template>
  <el-dialog :title="isEdit ? '编辑药品' : '新增药品'" :visible.sync="visible" width="40%">
    <el-form :model="form" :rules="rules" ref="form" label-width="100px">
      <el-form-item label="药品名称" prop="name">
        <el-input v-model="form.name"></el-input>
      </el-form-item>
      <el-form-item label="规格" prop="specification">
        <el-input v-model="form.specification"></el-input>
      </el-form-item>
      <el-form-item label="制造商" prop="manufacturer">
        <el-input v-model="form.manufacturer"></el-input>
      </el-form-item>
      <el-form-item label="类别" prop="category">
        <el-input v-model="form.category"></el-input>
      </el-form-item>
      <el-form-item label="单位" prop="unit">
        <el-input v-model="form.unit"></el-input>
      </el-form-item>
      <el-form-item label="价格" prop="price">
        <el-input-number v-model="form.price" :precision="2" :step="0.1"></el-input-number>
      </el-form-item>
      <el-form-item label="库存数量" prop="stockQuantity">
        <el-input-number v-model="form.stockQuantity" :min="0"></el-input-number>
      </el-form-item>
      <el-form-item label="最低库存" prop="minStock">
        <el-input-number v-model="form.minStock" :min="0"></el-input-number>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-switch v-model="form.status" :active-value="1" :inactive-value="0"></el-switch>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取 消</el-button>
      <el-button type="primary" @click="submitForm">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { createMedicine, updateMedicine } from '@/api/medicine'; // 假设您有对应的API文件和方法
import { ElMessage } from 'element-plus';

export default {
  name: 'MedicineForm',
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
        name: '',
        specification: '',
        manufacturer: '',
        category: '',
        unit: '',
        price: 0,
        stockQuantity: 0,
        minStock: 0,
        status: 1,
      },
      rules: {
        name: [
          { required: true, message: '请输入药品名称', trigger: 'blur' },
          { max: 100, message: '长度不能超过100个字符', trigger: 'blur' },
        ],
        specification: [
          { max: 100, message: '长度不能超过100个字符', trigger: 'blur' },
        ],
        manufacturer: [
          { max: 100, message: '长度不能超过100个字符', trigger: 'blur' },
        ],
        category: [
          { max: 50, message: '长度不能超过50个字符', trigger: 'blur' },
        ],
        unit: [
          { max: 20, message: '长度不能超过20个字符', trigger: 'blur' },
        ],
        price: [
          { required: true, message: '请输入药品价格', trigger: 'change' },
          { type: 'number', message: '价格必须为数字' },
        ],
        stockQuantity: [
           { required: true, message: '请输入库存数量', trigger: 'change' },
           { type: 'number', message: '库存数量必须为数字' },
        ],
         minStock: [
            { required: true, message: '请输入最低库存', trigger: 'change' },
            { type: 'number', message: '最低库存必须为数字' },
         ],
      },
    };
  },
  watch: {
    visible(val) {
      if (val) {
        if (this.isEdit) {
          this.form = { ...this.formData };
        } else {
          this.form = {
            name: '',
            specification: '',
            manufacturer: '',
            category: '',
            unit: '',
            price: 0,
            stockQuantity: 0,
            minStock: 0,
            status: 1,
          };
        }
      }
    },
  },
  methods: {
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.isEdit) {
            updateMedicine(this.form).then(() => {
              ElMessage.success('更新成功');
              this.$emit('success');
              this.visible = false;
            }).catch(error => {
              ElMessage.error('更新失败');
              console.error(error);
            });
          } else {
            createMedicine(this.form).then(() => {
              ElMessage.success('新增成功');
              this.$emit('success');
              this.visible = false;
            }).catch(error => {
              ElMessage.error('新增失败');
              console.error(error);
            });
          }
        }
      });
    },
  },
};
</script> 