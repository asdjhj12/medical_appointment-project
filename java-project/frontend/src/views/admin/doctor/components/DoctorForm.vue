<template>
  <el-dialog :title="isEdit ? '编辑医生' : '新增医生'" :visible.sync="visible" width="40%">
    <el-form :model="form" :rules="rules" ref="form" label-width="80px">
      <el-form-item label="关联用户" prop="userId">
        <el-select v-model="form.userId" placeholder="请选择关联用户" :disabled="isEdit">
          <el-option
            v-for="user in userList"
            :key="user.id"
            :label="user.username + ' (' + user.realName + ')'"
            :value="user.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="所属科室" prop="departmentId">
        <el-select v-model="form.departmentId" placeholder="请选择所属科室">
          <el-option
            v-for="department in departmentList"
            :key="department.id"
            :label="department.name"
            :value="department.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="职称" prop="title">
        <el-input v-model="form.title"></el-input>
      </el-form-item>
      <el-form-item label="专长" prop="specialty">
        <el-input v-model="form.specialty" type="textarea"></el-input>
      </el-form-item>
       <el-form-item label="介绍" prop="introduction">
        <el-input v-model="form.introduction" type="textarea"></el-input>
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
import { createUser, updateUser, getAllUsers } from '@/api/user'; // 假设用户API已实现
import { listAllDepartments } from '@/api/department'; // 假设科室API已实现
import { createDoctor, updateDoctor } from '@/api/doctor'; // 假设医生API已实现
import { ElMessage } from 'element-plus';

export default {
  name: 'DoctorForm',
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
        userId: null,
        departmentId: null,
        title: '',
        specialty: '',
        introduction: '',
        status: 1,
      },
      rules: {
        userId: [
          { required: true, message: '请选择关联用户', trigger: 'change' },
        ],
        departmentId: [
          { required: true, message: '请选择所属科室', trigger: 'change' },
        ],
        title: [
          { required: true, message: '请输入职称', trigger: 'blur' },
          { max: 20, message: '长度不能超过20个字符', trigger: 'blur' },
        ],
        specialty: [
          { max: 200, message: '长度不能超过200个字符', trigger: 'blur' },
        ],
        introduction: [
          { max: 500, message: '长度不能超过500个字符', trigger: 'blur' },
        ],
      },
      userList: [], // 用户列表用于选择关联用户
      departmentList: [], // 科室列表用于选择所属科室
    };
  },
  watch: {
    visible(val) {
      if (val) {
        this.fetchUsers();
        this.fetchDepartments();
        if (this.isEdit) {
          this.form = { ...this.formData };
        } else {
          this.form = {
            userId: null,
            departmentId: null,
            title: '',
            specialty: '',
            introduction: '',
            status: 1,
          };
        }
      }
    },
  },
  methods: {
    fetchUsers() {
      // 假设getAllUsers API只返回普通用户或未被关联的医生用户
      getAllUsers().then(response => {
         if (response.code === 200) {
           this.userList = response.data;
         } else {
           ElMessage.error('获取用户列表失败: ' + response.msg);
         }
      }).catch(error => {
        ElMessage.error('获取用户列表失败');
        console.error(error);
      });
    },
    fetchDepartments() {
       listAllDepartments().then(response => {
         if (response.code === 200) {
            this.departmentList = response.data;
         } else {
            ElMessage.error('获取科室列表失败: ' + response.msg);
         }
       }).catch(error => {
         ElMessage.error('获取科室列表失败');
         console.error(error);
       });
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.isEdit) {
            updateDoctor(this.form).then(() => {
              ElMessage.success('更新成功');
              this.$emit('success');
              this.visible = false;
            }).catch(error => {
              ElMessage.error('更新失败');
              console.error(error);
            });
          } else {
            createDoctor(this.form).then(() => {
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