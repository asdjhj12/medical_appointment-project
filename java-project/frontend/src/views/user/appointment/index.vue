<template>
  <div class="user-appointment-list">
    <h2>我的预约</h2>



     <!-- 搜索和筛选区域 -->
    <div class="filter-container">
       <el-select v-model="listQuery.status" placeholder="预约状态" clearable style="width: 150px" class="filter-item">
        <el-option label="待处理" value="PENDING"></el-option>
        <el-option label="已确认" value="CONFIRMED"></el-option>
        <el-option label="已完成" value="COMPLETED"></el-option>
        <el-option label="已取消" value="CANCELLED"></el-option>
         <el-option label="已过期" value="EXPIRED"></el-option>
      </el-select>
      <!-- 可以添加日期范围选择 -->
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">搜索</el-button>
      <el-button class="filter-item" style="margin-left: 10px;" icon="el-icon-refresh" @click="resetFilter">重置</el-button>
    </div>

    <el-table :data="appointmentList" v-loading="loading">
      <el-table-column prop="appointmentNumber" label="预约编号" width="150"></el-table-column>
      <el-table-column prop="userName" label="患者姓名"></el-table-column>
      <el-table-column prop="departmentName" label="科室"></el-table-column>
       <el-table-column label="排班时间" width="160">
         <template #default="{ row }">
           {{ row.scheduleDate }} {{ row.periodText }}
         </template>
       </el-table-column>
       <el-table-column prop="status" label="状态" width="100">
         <template #default="{ row }">
           <el-tag :type="getStatusTagType(row.status)">{{ getStatusText(row.status) }}</el-tag>
         </template>
       </el-table-column>
      <el-table-column prop="createTime" label="预约时间" width="160"></el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="mini" @click="handleViewAppointment(row)">查看详情</el-button>
          <el-button size="mini" type="danger" v-if="row.status === 'PENDING' || row.status === 'CONFIRMED'" @click="handleCancelAppointment(row)">取消预约</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination v-show="total>0" :total="total" v-model:page="listQuery.pageNum" v-model:limit="listQuery.pageSize" @pagination="getList" />

     <!-- 预约详情弹窗 -->
     <appointment-detail
       v-model:visible="detailDialogVisible"
       :appointment="currentAppointment"
     ></appointment-detail>
  </div>
</template>

<script setup>
// 导入所需的API和组件
import { ref, reactive, onMounted } from 'vue';
import { getUserAppointmentList, cancelAppointment, createAppointment } from '@/api/appointment';
import { getPatientByUserId, registerPatientByUserId } from '@/api/patient';
import { ElMessage, ElMessageBox } from 'element-plus';
import Pagination from '@/components/Pagination.vue';
import AppointmentDetail from './components/AppointmentDetail.vue';
import { useUserStore } from '@/store/user';

// 状态定义
const appointmentList = ref([]);
const total = ref(0);
const loading = ref(false);
const listQuery = reactive({
  pageNum: 1,
  pageSize: 10,
  status: null,
});
const detailDialogVisible = ref(false);
const currentAppointment = ref({});
const patientId = ref(null);
const createForm = ref({
  doctorId: '',
  scheduleId: '',
  description: ''
});

// 获取 patientId
const fetchPatientId = async (userId) => {
  try {
    const res = await getPatientByUserId(userId);
    if (res && res.id) {
      patientId.value = res.id;
    } else {
      ElMessage.error('未找到患者信息，请完善个人信息');
    }
  } catch (e) {
    ElMessage.error('获取患者信息失败');
  }
};

// 新增：创建预约前自动注册患者
async function ensurePatientRegistered(userId) {
  try {
    await registerPatientByUserId(userId);
  } catch (e) {
    // 可以忽略已存在的情况
  }
}

// 获取预约列表
const getList = () => {
  loading.value = true;
  const userStore = useUserStore();
  const userInfo = userStore.userInfo;

  if (!userInfo || !userInfo.id) {
    ElMessage.error('无法获取当前用户信息，请重新登录');
    loading.value = false;
    return;
  }

  // 优先获取 patientId
  if (!patientId.value) {
    fetchPatientId(userInfo.id).then(() => {
      if (patientId.value) {
        getUserAppointmentList({ userId: userInfo.id, ...listQuery }).then(handleAppointmentResponse).catch(handleAppointmentError).finally(() => {
          loading.value = false;
        });
      } else {
        loading.value = false;
      }
    });
    return;
  }

  getUserAppointmentList({ userId: userInfo.id, ...listQuery }).then(handleAppointmentResponse).catch(handleAppointmentError).finally(() => {
    loading.value = false;
  });
};

// 响应处理函数
const handleAppointmentResponse = (response) => {
  if (response) {
    if (Array.isArray(response)) {
      appointmentList.value = response;
      total.value = response.length;
    } else if (response.records) {
      appointmentList.value = response.records;
      total.value = response.total || response.records.length;
    } else {
      appointmentList.value = [response];
      total.value = 1;
    }
  } else {
    appointmentList.value = [];
    total.value = 0;
    ElMessage.warning('没有找到预约记录');
  }
};
const handleAppointmentError = (error) => {
  ElMessage.error('获取我的预约列表失败');
  console.error(error);
};

// 筛选
const handleFilter = () => {
  listQuery.pageNum = 1;
  getList();
};

// 重置筛选
const resetFilter = () => {
  listQuery.pageNum = 1;
  listQuery.pageSize = 10;
  listQuery.status = null;
  getList();
};

// 查看预约详情
const handleViewAppointment = (row) => {
  currentAppointment.value = { ...row };
  detailDialogVisible.value = true;
};

// 取消预约
const handleCancelAppointment = (row) => {
  ElMessageBox.confirm(`确定取消该预约记录吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    cancelAppointment(row.id).then(() => {
      ElMessage.success('取消成功');
      getList(); // 刷新列表
    }).catch(error => {
      ElMessage.error('取消失败');
      console.error(error);
    });
  }).catch(() => {
    // 用户取消操作
  });
};

// 状态文本
const getStatusText = (status) => {
  const statusMap = {
    PENDING: '待处理',
    CONFIRMED: '已确认',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
    EXPIRED: '已过期',
    0: '待处理',
    1: '已确认',
    2: '已完成',
    3: '已取消',
    4: '已过期'
  };
  return statusMap[status] || status;
};

// 状态标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    PENDING: 'info',
    CONFIRMED: '',
    COMPLETED: 'success',
    CANCELLED: 'danger',
    EXPIRED: 'warning',
    0: 'info',
    1: '',
    2: 'success',
    3: 'danger',
    4: 'warning'
  };
  return typeMap[status] || '';
};

// 新增：创建预约方法
async function handleCreateAppointment(formData) {
  const userStore = useUserStore();
  const userId = userStore.userInfo?.id;
  if (!userId) {
    ElMessage.error('请先登录');
    return;
  }
  // 1. 先注册患者，返回 patient
  const patient = await getPatientByUserId(userId).catch(async () => {
    await registerPatientByUserId(userId);
    return await getPatientByUserId(userId);
  });
  if (!patient || !patient.id) {
    ElMessage.error('患者信息注册失败');
    return;
  }
  // 2. 用 patient.id 作为 patientId 创建预约
  await createAppointment({ ...formData, patientId: patient.id });
  ElMessage.success('预约成功');
  getList();
}

// 组件挂载时先查 patientId 再查预约
onMounted(() => {
  const userStore = useUserStore();
  const userInfo = userStore.userInfo;
  if (userInfo && userInfo.id) {
    fetchPatientId(userInfo.id).then(() => {
      getList();
    });
  }
});
</script>

<style scoped>
.user-appointment-list {
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