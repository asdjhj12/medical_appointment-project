<template>
  <el-dialog title="预约详情" :visible.sync="visible" width="40%">
    <el-form :model="appointment" label-width="120px" label-position="left">
      <el-form-item label="预约编号:">
        {{ appointment.appointmentNumber }}
      </el-form-item>
      <el-form-item label="患者姓名:">
        {{ appointment.patientName }}
      </el-form-item>
      <el-form-item label="医生姓名:">
        {{ appointment.doctorName }}
      </el-form-item>
      <el-form-item label="科室:">
        {{ appointment.departmentName }}
      </el-form-item>
      <el-form-item label="排班时间:">
        {{ appointment.scheduleTime }}
      </el-form-item>
       <el-form-item label="预约状态:">
         <el-tag :type="getStatusTagType(appointment.status)">{{ getStatusText(appointment.status) }}</el-tag>
       </el-form-item>
        <el-form-item label="创建时间:">
        {{ appointment.createTime }}
      </el-form-item>
       <el-form-item label="描述:">
        {{ appointment.description }}
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">关 闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  name: 'AppointmentDetail',
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    appointment: {
      type: Object,
      default: () => ({}),
    },
  },
   methods: {
     getStatusText(status) {
       const statusMap = {
         PENDING: '待处理',
         CONFIRMED: '已确认',
         COMPLETED: '已完成',
         CANCELLED: '已取消',
         EXPIRED: '已过期',
       };
       return statusMap[status] || status;
     },
      getStatusTagType(status) {
       const typeMap = {
         PENDING: 'info',
         CONFIRMED: '',
         COMPLETED: 'success',
         CANCELLED: 'danger',
         EXPIRED: 'warning',
       };
       return typeMap[status] || '';
     }
   }
};
</script> 