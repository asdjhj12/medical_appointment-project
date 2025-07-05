<template>
  <el-dialog :title="isEdit ? '编辑排班' : '新增排班'" :visible.sync="visible" width="40%">
    <el-form :model="form" :rules="rules" ref="form" label-width="100px">
      <el-form-item label="选择医生" prop="doctorId">
        <el-select v-model="form.doctorId" placeholder="请选择医生" :disabled="isEdit">
          <el-option
            v-for="doctor in doctorList"
            :key="doctor.id"
            :label="doctor.userName"
            :value="doctor.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="排班日期" prop="scheduleDate">
         <el-date-picker
           v-model="form.scheduleDate"
           type="date"
           placeholder="选择日期"
           value-format="yyyy-MM-dd"
            :disabled="isEdit"
         >
         </el-date-picker>
      </el-form-item>
       <el-form-item label="开始时间" prop="startTime">
         <el-time-select
           v-model="form.startTime"
           :picker-options="{
             start: '08:00',
             step: '00:30',
             end: '18:00'
           }"
           placeholder="选择开始时间">
         </el-time-select>
       </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
         <el-time-select
           v-model="form.endTime"
           :picker-options="{
             start: '08:00',
             step: '00:30',
             end: '18:00',
             minTime: form.startTime
           }"
           placeholder="选择结束时间">
         </el-time-select>
       </el-form-item>
      <el-form-item label="最大预约数" prop="maxAppointments">
        <el-input-number v-model="form.maxAppointments" :min="1"></el-input-number>
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
import { listAllDoctors } from '@/api/doctor'; // 获取医生列表
import { createSchedule, updateSchedule } from '@/api/doctorSchedule'; // 假设您有对应的API文件和方法
import { ElMessage } from 'element-plus';

export default {
  name: 'ScheduleForm',
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
        doctorId: null,
        scheduleDate: '',
        startTime: '',
        endTime: '',
        maxAppointments: 1,
        status: 1,
      },
      rules: {
        doctorId: [
          { required: true, message: '请选择医生', trigger: 'change' },
        ],
        scheduleDate: [
          { required: true, message: '请选择排班日期', trigger: 'change' },
        ],
        startTime: [
          { required: true, message: '请选择开始时间', trigger: 'change' },
        ],
         endTime: [
           { required: true, message: '请选择结束时间', trigger: 'change' },
         ],
        maxAppointments: [
          { required: true, message: '请输入最大预约数', trigger: 'change' },
          { type: 'number', message: '最大预约数必须为数字' }
        ],
      },
      doctorList: [], // 医生列表用于选择
    };
  },
  watch: {
    visible(val) {
      if (val) {
        this.fetchDoctors();
        if (this.isEdit) {
          this.form = { ...this.formData };
        } else {
          this.form = {
             doctorId: null,
             scheduleDate: '',
             startTime: '',
             endTime: '',
             maxAppointments: 1,
             status: 1,
          };
        }
      }
    },
  },
  methods: {
     fetchDoctors() {
        listAllDoctors().then(response => {
          if (response.code === 200) {
             this.doctorList = response.data;
          } else {
             ElMessage.error('获取医生列表失败: ' + response.msg);
          }
        }).catch(error => {
          ElMessage.error('获取医生列表失败');
          console.error(error);
        });
     },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.isEdit) {
            updateSchedule(this.form).then(() => {
              ElMessage.success('更新成功');
              this.$emit('success');
              this.visible = false;
            }).catch(error => {
              ElMessage.error('更新失败');
              console.error(error);
            });
          } else {
            createSchedule(this.form).then(() => {
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