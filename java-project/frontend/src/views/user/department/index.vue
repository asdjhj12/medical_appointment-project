<template>
  <div class="user-department-list">
    <h2>选择科室</h2>

    <el-row :gutter="20">
      <el-col :span="6" v-for="department in departmentList" :key="department.id">
        <el-card shadow="hover" @click="selectDepartment(department)">
          <h3>{{ department.name }}</h3>
          <p>{{ department.description }}</p>
        </el-card>
      </el-col>
    </el-row>

     <el-empty v-if="departmentList.length === 0" description="暂无科室信息"></el-empty>
  </div>
</template>

<script>
import { listAllDepartments } from '@/api/department'; // 假设复用管理员端的API方法
import { ElMessage } from 'element-plus';

export default {
  name: 'UserDepartmentList',
  data() {
    return {
      departmentList: [],
      loading: false,
    };
  },
  created() {
    this.fetchDepartments();
  },
  methods: {
    fetchDepartments() {
      this.loading = true;
      listAllDepartments().then(response => {
        if (response.code === 200 && response.data) {
          // 过滤掉状态为禁用的科室（假设状态字段为 status， 1为正常，0为禁用）
          this.departmentList = response.data.filter(dept => dept.status === 1);
        } else {
          ElMessage.error('获取科室列表失败: ' + response.msg);
        }
      }).catch(error => {
        ElMessage.error('获取科室列表失败');
        console.error(error);
      }).finally(() => {
        this.loading = false;
      });
    },
    selectDepartment(department) {
      // 跳转到医生列表页面，并传递科室ID
      this.$router.push({ name: 'UserDoctorList', params: { departmentId: department.id, departmentName: department.name } });
    },
  },
};
</script>

<style scoped>
.user-department-list {
  padding: 20px;
}

.el-card {
  margin-bottom: 20px;
  cursor: pointer;
}
</style> 