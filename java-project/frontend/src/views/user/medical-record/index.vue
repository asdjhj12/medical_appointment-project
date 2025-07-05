<template>
  <div class="user-medical-record-list">
    <h2>我的电子病历</h2>

     <!-- 搜索和筛选区域 -->
    <div class="filter-container">
       <el-input v-model="listQuery.doctorName" placeholder="医生姓名" style="width: 150px;" class="filter-item" @keyup.enter.native="handleFilter" />
       <el-select v-model="listQuery.departmentId" placeholder="选择科室" clearable style="width: 150px" class="filter-item">
         <el-option v-for="item in departmentList" :key="item.id" :label="item.name" :value="item.id"></el-option>
       </el-select>
      <!-- 可以添加日期范围选择 -->
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">搜索</el-button>
      <el-button class="filter-item" style="margin-left: 10px;" icon="el-icon-refresh" @click="resetFilter">重置</el-button>
    </div>

    <el-table :data="medicalRecordList" v-loading="loading">
      <el-table-column prop="appointmentNumber" label="预约编号" width="150"></el-table-column> <!-- 假设电子病历数据中关联了预约，有预约编号 -->
      <el-table-column prop="doctorName" label="医生姓名"></el-table-column>
      <el-table-column prop="departmentName" label="科室"></el-table-column>
      <el-table-column prop="diagnosis" label="诊断结果" show-overflow-tooltip></el-table-column>
      <el-table-column prop="visitTime" label="就诊日期" width="160"></el-table-column>
      <el-table-column prop="description" label="症状描述" show-overflow-tooltip></el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button size="mini" @click="handleViewMedicalRecord(row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination v-show="total>0" :total="total" :page.sync="listQuery.pageNum" :limit.sync="listQuery.pageSize" @pagination="getList" />

     <!-- 电子病历详情弹窗 -->
     <medical-record-detail
       v-if="detailDialogVisible"
       v-model:visible="detailDialogVisible"
       :medicalRecord="currentMedicalRecord"
       @refresh="getList"
     />
  </div>
</template>

<script>
// 假设您有用户端获取电子病历列表的API方法
import { getMedicalRecordsByPatientId, getMedicalRecordDetail } from '@/api/medical-record';
import { getPatientByUserId } from '@/api/patient';
import { useUserStore } from '@/store/user';
import { listAllDepartments } from '@/api/department'; // 获取科室列表用于筛选
import { ElMessage } from 'element-plus';
import Pagination from '@/components/Pagination.vue';
import MedicalRecordDetail from './components/MedicalRecordDetail.vue'; // 复用详情弹窗

export default {
  name: 'UserMedicalRecordList',
  components: {
    Pagination,
    MedicalRecordDetail,
  },
  data() {
    return {
      medicalRecordList: [],
      total: 0,
      loading: false,
      patientId: null,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        doctorName: null,
        departmentId: null,
        // 可以添加按日期范围筛选等其他条件
      },
       departmentList: [], // 科室列表用于筛选
       detailDialogVisible: false,
       currentMedicalRecord: {},
    };
  },
  created() {
    this.initPatientAndList();
    this.fetchDepartments(); // 获取科室列表用于筛选
  },
  methods: {
    async initPatientAndList() {
      // 获取当前登录用户id
      const userStore = useUserStore();
      const userInfo = userStore.userInfo;
      if (!userInfo || !userInfo.id) {
        this.$message.error('无法获取当前用户信息，请重新登录');
        return;
      }
      try {
        const patientRes = await getPatientByUserId(userInfo.id);
        if (patientRes && patientRes.id) {
          this.patientId = patientRes.id;
          this.getList();
        } else {
          this.$message.error('未找到患者信息，请完善个人信息');
        }
      } catch (e) {
        this.$message.error('获取患者信息失败');
      }
    },
    getList() {
      if (!this.patientId) {
        this.loading = false;
        return;
      }
      this.loading = true;
      getMedicalRecordsByPatientId(this.patientId).then(response => {
        // response 就是数组
        this.medicalRecordList = response || [];
        this.total = response ? response.length : 0;
      }).catch(error => {
        this.$message.error('获取我的电子病历列表失败');
        console.error(error);
      }).finally(() => {
        this.loading = false;
      });
    },
     fetchDepartments() {
        listAllDepartments().then(response => {
          if (response.code === 200) {
             // 过滤掉状态为禁用的科室
             this.departmentList = response.data.filter(dept => dept.status === 1);
          }
        }).catch(error => {
          console.error(error);
        });
     },
    handleFilter() {
      this.listQuery.pageNum = 1;
      this.getList();
    },
    resetFilter() {
      this.listQuery = {
         pageNum: 1,
         pageSize: 10,
         doctorName: null,
         departmentId: null,
      };
      this.getList();
    },
    async handleViewMedicalRecord(row) {
      console.log('点击详情', row)
      // 调用后端接口获取完整详情（含medicines）
      try {
        const res = await getMedicalRecordDetail(row.id)
        // 兼容 res.data 或 res
        this.currentMedicalRecord = res.data || res
        this.detailDialogVisible = true
        this.$nextTick(() => {
          console.log('弹窗状态', this.detailDialogVisible)
        })
      } catch (e) {
        this.$message.error('获取病历详情失败')
      }
    }
  },
};
</script>

<style scoped>
.user-medical-record-list {
  padding: 20px;
}

.filter-container {
  margin-bottom: 15px;
}

.filter-item {
  margin-right: 10px;
}

.el-table {
  margin-top: 20px;
}
</style> 