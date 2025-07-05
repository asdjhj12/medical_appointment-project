<template>
  <el-dialog :title="isEdit ? '编辑科室' : '新增科室'" :visible.sync="visible" width="30%">
    <el-form :model="form" :rules="rules" ref="form" label-width="80px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="form.name"></el-input>
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" type="textarea"></el-input>
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
import { createDepartment, updateDepartment } from '@/api/department';
import { ElMessage } from 'element-plus'; // Assuming Element Plus

export default {
  name: 'DepartmentForm',
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
        description: '',
        status: 1,
      },
      rules: {
        name: [
          { required: true, message: '请输入科室名称', trigger: 'blur' },
          { max: 50, message: '长度不能超过50个字符', trigger: 'blur' },
        ],
        description: [
          { max: 500, message: '长度不能超过500个字符', trigger: 'blur' },
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
            description: '',
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
            updateDepartment(this.form).then(() => {
              ElMessage.success('更新成功');
              this.$emit('success');
              this.visible = false;
            }).catch(error => {
              ElMessage.error('更新失败');
              console.error(error);
            });
          } else {
            createDepartment(this.form).then(() => {
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