<template>
  <div class="user-schedule-list">
    <h2>{{ doctorName }} - 排班信息</h2>

    <el-table :data="scheduleList" v-loading="loading">
      <el-table-column prop="scheduleDate" label="日期" width="120"></el-table-column>
      <el-table-column prop="startTime" label="开始时间" width="100"></el-table-column>
      <el-table-column prop="endTime" label="结束时间" width="100"></el-table-column>
      <el-table-column prop="availableAppointments" label="可预约数" width="100"></el-table-column>
      <el-table-column prop="totalAppointments" label="总号源数" width="100"></el-table-column>
       <el-table-column prop="status" label="状态" width="100">
         <template slot-scope="scope">
           <el-tag :type="getScheduleStatusTagType(scope.row.status)">{{ getScheduleStatusText(scope.row.status) }}</el-tag>
         </template>
       </el-table-column>
      <el-table-column label="操作" width="100">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="primary"
            :disabled="scope.row.availableAppointments <= 0 || scope.row.status !== 'ACTIVE'"
            @click="handleAppointment(scope.row)"
          >
            立即预约
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="scheduleList.length === 0" description="暂无排班信息"></el-empty>

    <!-- 预约确认弹窗 -->
    <el-dialog
      title="确认预约"
      :visible.sync="appointmentDialogVisible"
      width="30%"
      center>
      <span>确定预约 {{ doctorName }} 医生 {{ currentSchedule.scheduleDate }} {{ currentSchedule.startTime }}-{{ currentSchedule.endTime }} 的号吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="appointmentDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmAppointment">确 定</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import { listSchedulesByDoctor } from '@/api/schedule'; // 假设您有根据医生ID获取排班列表的API方法
import { createAppointment } from '@/api/appointment'; // 假设您有创建预约的API方法
import { ElMessage, ElMessageBox } from 'element-plus';

export default {
  name: 'UserScheduleList',
  data() {
    return {
      doctorId: null,
      doctorName: '',
      scheduleList: [],
      loading: false,
      appointmentDialogVisible: false, // 控制预约确认弹窗显示
      currentSchedule: {}, // 当前选中的排班信息
    };
  },
  created() {
    this.doctorId = this.$route.params.doctorId;
    this.doctorName = this.$route.params.doctorName || '医生';
    if (this.doctorId) {
      this.fetchSchedules();
    } else {
      ElMessage.warning('缺少医生ID参数');
    }
  },
  methods: {
    fetchSchedules() {
      this.loading = true;
      listSchedulesByDoctor(this.doctorId).then(response => {
        if (response.code === 200 && response.data) {
           // 过滤掉状态不是 ACTIVE 的排班（假设状态字段为 status）
           this.scheduleList = response.data.filter(schedule => schedule.status === 'ACTIVE');
        } else {
           ElMessage.error('获取排班列表失败: ' + response.msg);
        }
      }).catch(error => {
        ElMessage.error('获取排班列表失败');
        console.error(error);
      }).finally(() => {
        this.loading = false;
      });
    },
    handleAppointment(schedule) {
      this.currentSchedule = schedule;
      this.appointmentDialogVisible = true;
    },
    confirmAppointment() {
       // 调用创建预约的API
       const appointmentData = {
         doctorId: this.doctorId,
         scheduleId: this.currentSchedule.id,
         // 其他必要的预约信息，例如用户ID可以从store中获取
         // userId: this.$store.getters.userId
       };
       createAppointment(appointmentData).then(response => {
          if (response.code === 200) {
            ElMessage.success('预约成功！');
            this.appointmentDialogVisible = false;
            this.fetchSchedules(); // 刷新排班列表，更新可预约数
            // TODO: 可以跳转到用户预约列表页面
          } else {
             ElMessage.error('预约失败: ' + response.msg);
          }
       }).catch(error => {
          ElMessage.error('预约失败');
          console.error(error);
       });
    },
     getScheduleStatusText(status) {
       const statusMap = {
         ACTIVE: '正常',
         INACTIVE: '停用',
         // 其他可能的排班状态
       };
       return statusMap[status] || status;
     },
     getScheduleStatusTagType(status) {
       const typeMap = {
         ACTIVE: 'success',
         INACTIVE: 'danger',
         // 其他可能的排班状态对应的tag类型
       };
       return typeMap[status] || '';
     }
  },
};
</script>

<style scoped>
.user-schedule-list {
  padding: 20px;
}

.el-table {
  margin-top: 20px;
}
</style> 