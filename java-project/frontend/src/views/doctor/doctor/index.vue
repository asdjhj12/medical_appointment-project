<template>
  <div class="user-doctor-list">
    <h2>{{ departmentName }} - 医生列表</h2>

    <el-table :data="doctorList" v-loading="loading">
      <el-table-column label="头像" width="80">
        <template slot-scope="scope">
          <el-avatar :src="scope.row.avatar" :key="scope.row.avatar"> <!-- 假设医生数据中有avatar字段 -->
             <img src="https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png"/> <!-- 默认头像 -->
          </el-avatar>
        </template>
      </el-table-column>
      <el-table-column prop="userName" label="姓名"></el-table-column> <!-- 假设医生数据中关联了用户，有userName字段 -->
      <el-table-column prop="title" label="职称"></el-table-column>
      <el-table-column prop="specialty" label="专长"></el-table-column>
       <el-table-column prop="introduction" label="介绍" show-overflow-tooltip></el-table-column>
      <el-table-column label="操作" width="100">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" @click="viewSchedule(scope.row)">查看排班</el-button>
        </template>
      </el-table-column>
    </el-table>

     <el-empty v-if="doctorList.length === 0" description="暂无医生信息"></el-empty>
  </div>
</template>

<script>
import { listDoctorsByDepartment } from '@/api/doctor'; // 假设您有根据科室ID获取医生列表的API方法
import { ElMessage } from 'element-plus';

export default {
  name: 'UserDoctorList',
  data() {
    return {
      departmentId: null,
      departmentName: '',
      doctorList: [],
      loading: false,
    };
  },
  created() {
    this.departmentId = this.$route.params.departmentId;
    this.departmentName = this.$route.params.departmentName || '医生列表';
    if (this.departmentId) {
      this.fetchDoctors();
    } else {
      ElMessage.warning('缺少科室ID参数');
    }
  },
  methods: {
    fetchDoctors() {
      this.loading = true;
      listDoctorsByDepartment(this.departmentId).then(response => {
        if (response.code === 200 && response.data) {
           // 过滤掉状态为禁用的医生（假设状态字段为 status， 1为正常，0为禁用）
           // 假设医生数据中关联了用户表，并且后端返回的医生列表中包含了用户的userName和头像avatar
           this.doctorList = response.data.filter(doctor => doctor.status === 1);
        } else {
           ElMessage.error('获取医生列表失败: ' + response.msg);
        }
      }).catch(error => {
        ElMessage.error('获取医生列表失败');
        console.error(error);
      }).finally(() => {
        this.loading = false;
      });
    },
    viewSchedule(doctor) {
      // 跳转到医生排班页面，并传递医生ID
      this.$router.push({ name: 'UserScheduleList', params: { doctorId: doctor.id, doctorName: doctor.userName } });
    },
  },
};
</script>

<style scoped>
.user-doctor-list {
  padding: 20px;
}

.el-table {
  margin-top: 20px;
}

/* 医生头像样式 */
.el-avatar {
  vertical-align: middle;
}
</style> 