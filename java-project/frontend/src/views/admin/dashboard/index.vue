<template>
  <div class="admin-dashboard">
    <h2>管理员控制台</h2>
    
    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-header">
            <div class="stat-title">总预约数</div>
            <el-icon class="stat-icon"><Calendar /></el-icon>
          </div>
          <div class="stat-value">{{ stats.appointments || 0 }}</div>
          <div class="stat-footer">
            <span>较昨日</span>
            <span :class="stats.appointmentsTrend > 0 ? 'up' : 'down'">
              {{ stats.appointmentsTrend > 0 ? '+' : '' }}{{ stats.appointmentsTrend || 0 }}%
            </span>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-header">
            <div class="stat-title">医生数量</div>
            <el-icon class="stat-icon"><User /></el-icon>
          </div>
          <div class="stat-value">{{ stats.doctors || 0 }}</div>
          <div class="stat-footer">
            <span>总计</span>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-header">
            <div class="stat-title">患者数量</div>
            <el-icon class="stat-icon"><UserFilled /></el-icon>
          </div>
          <div class="stat-value">{{ stats.patients || 0 }}</div>
          <div class="stat-footer">
            <span>较昨日</span>
            <span :class="stats.patientsTrend > 0 ? 'up' : 'down'">
              {{ stats.patientsTrend > 0 ? '+' : '' }}{{ stats.patientsTrend || 0 }}%
            </span>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-header">
            <div class="stat-title">今日预约</div>
            <el-icon class="stat-icon"><Document /></el-icon>
          </div>
          <div class="stat-value">{{ stats.todayAppointments || 0 }}</div>
          <div class="stat-footer">
            <span>待处理</span>
            <span class="pending">{{ stats.pendingAppointments || 0 }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 图表区域 -->
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <div class="chart-header">
            <h3>预约趋势</h3>
          </div>
          <div id="appointmentTrendChart" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <div class="chart-header">
            <h3>科室预约分布</h3>
          </div>
          <div id="departmentDistributionChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row style="margin-top: 20px;">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近预约</span>
              <el-button type="primary" size="small" @click="goToAppointments">查看全部</el-button>
            </div>
          </template>
          
          <el-table :data="recentAppointments" :key="recentAppointments.length" style="width: 100%" v-loading="loading">
            <el-table-column prop="appointmentNumber" label="预约编号" width="180" />
            <el-table-column label="患者姓名" width="120">
              <template #default="{ row }">
                {{ row.userName || row.patientName || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="doctorName" label="医生姓名" width="120" />
            <el-table-column prop="departmentName" label="科室" width="120" />
            <el-table-column label="预约时间" width="180">
              <template #default="{ row }">
                <!-- 优先显示 scheduleDate+periodText，否则用 createTime -->
                <span v-if="row.scheduleDate && row.periodText">{{ row.scheduleDate }} {{ row.periodText }}</span>
                <span v-else>{{ row.createTime || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="text" size="small" @click="viewAppointment(row)">查看</el-button>
                <el-button 
                  v-if="row.status === 'PENDING'" 
                  type="text" 
                  size="small" 
                  @click="confirmAppointment(row)"
                >确认</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 预约详情弹窗 -->
    <el-dialog
      v-model="appointmentDetailVisible"
      title="预约详情"
      width="500px"
    >
      <div v-if="selectedAppointment" class="appointment-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="预约编号">{{ selectedAppointment.appointmentNumber }}</el-descriptions-item>
          <el-descriptions-item label="患者姓名">{{ selectedAppointment.userName || selectedAppointment.patientName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="医生姓名">{{ selectedAppointment.doctorName }}</el-descriptions-item>
          <el-descriptions-item label="科室">{{ selectedAppointment.departmentName }}</el-descriptions-item>
          <el-descriptions-item label="预约时间">
            <span v-if="selectedAppointment.scheduleDate && selectedAppointment.periodText">
              {{ selectedAppointment.scheduleDate }} {{ selectedAppointment.periodText }}
            </span>
            <span v-else>{{ selectedAppointment.createTime || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedAppointment.status)">
              {{ getStatusText(selectedAppointment.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="预约描述">{{ selectedAppointment.description || '无' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ selectedAppointment.createTime }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ selectedAppointment.updateTime }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="appointmentDetailVisible = false">关闭</el-button>
          <el-button v-if="selectedAppointment && selectedAppointment.status === 'PENDING'" type="primary" @click="confirmAppointment(selectedAppointment)">确认预约</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Calendar, User, UserFilled, Document } from '@element-plus/icons-vue'
import { getRecentAppointments, getTodayAppointments, getAppointmentCount, getAppointmentTrend, getDepartmentDistribution } from '@/api/appointment'
import { getDoctorCount, getActiveDoctorCount } from '@/api/doctor'
import { getPatientCount } from '@/api/patient'
import * as echarts from 'echarts'

const router = useRouter()
const loading = ref(false)
let appointmentTrendChart = null
let departmentDistributionChart = null

// 统计数据
const stats = ref({
  appointments: 0,
  appointmentsTrend: 0,
  doctors: 0,
  patients: 0,
  patientsTrend: 0,
  todayAppointments: 0,
  pendingAppointments: 0
})

// 最近预约数据
const recentAppointments = ref([])

// 预约详情弹窗
const appointmentDetailVisible = ref(false)
const selectedAppointment = ref(null)

// 获取状态对应的类型
const getStatusType = (status) => {
  const typeMap = {
    0: 'info',         // 待就诊
    1: 'success',      // 已完成
    2: 'danger',       // 已取消
    3: 'primary',      // 已确认（如有）
    4: 'warning',      // 已过期（如有）
    'PENDING': 'info',
    'CONFIRMED': 'primary',
    'COMPLETED': 'success',
    'CANCELLED': 'danger',
    'EXPIRED': 'warning'
  }
  return typeMap[status] || 'info'
}

// 获取状态对应的文本
const getStatusText = (status) => {
  const textMap = {
    0: '待就诊',
    1: '已完成',
    2: '已取消',
    3: '已确认',
    4: '已过期',
    'PENDING': '待处理',
    'CONFIRMED': '已确认',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消',
    'EXPIRED': '已过期'
  }
  return textMap[status] || status
}

// 查看预约详情
const viewAppointment = (row) => {
  selectedAppointment.value = row
  appointmentDetailVisible.value = true
}

// 确认预约
const confirmAppointment = (row) => {
  console.log('确认预约', row)
  // 实际项目中需要调用API确认预约
}

// 跳转到预约管理页面
const goToAppointments = () => {
  router.push('/admin/appointment')
}

// 新增：获取今日预约数和待处理预约数
async function fetchTodayStats() {
  // 获取今日预约列表
  const todayList = await getTodayAppointments();
  if (Array.isArray(todayList)) {
    stats.value.todayAppointments = todayList.length;
    stats.value.pendingAppointments = todayList.filter(item => item.status === 0 || item.status === 'PENDING').length;
  } else if (todayList && todayList.records) {
    stats.value.todayAppointments = todayList.records.length;
    stats.value.pendingAppointments = todayList.records.filter(item => item.status === 0 || item.status === 'PENDING').length;
  } else {
    stats.value.todayAppointments = 0;
    stats.value.pendingAppointments = 0;
  }
}

// 初始化预约趋势图表
const initAppointmentTrendChart = (data) => {
  const chartDom = document.getElementById('appointmentTrendChart')
  if (!chartDom) return
  
  appointmentTrendChart = echarts.init(chartDom)
  
  // 如果没有数据，显示模拟数据
  if (!data || !data.dates || data.dates.length === 0) {
    // 模拟数据
    const dates = []
    const counts = []
    const now = new Date()
    for (let i = 6; i >= 0; i--) {
      const date = new Date(now)
      date.setDate(date.getDate() - i)
      dates.push(`${date.getMonth() + 1}/${date.getDate()}`)
      counts.push(Math.floor(Math.random() * 10) + 1) // 随机数据
    }
    
    data = { dates, counts }
  }
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.dates,
      axisLabel: {
        interval: 0,
        rotate: 30
      }
    },
    yAxis: {
      type: 'value',
      minInterval: 1
    },
    series: [
      {
        name: '预约数量',
        type: 'line',
        smooth: true,
        data: data.counts,
        itemStyle: {
          color: '#409EFF'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              {
                offset: 0,
                color: 'rgba(64, 158, 255, 0.7)'
              },
              {
                offset: 1,
                color: 'rgba(64, 158, 255, 0.1)'
              }
            ]
          }
        }
      }
    ]
  }
  
  appointmentTrendChart.setOption(option)
  
  // 响应窗口大小变化
  window.addEventListener('resize', () => {
    appointmentTrendChart && appointmentTrendChart.resize()
  })
}

// 初始化科室预约分布图表
const initDepartmentDistributionChart = (data) => {
  console.log('初始化科室预约分布图表，数据:', data);
  const chartDom = document.getElementById('departmentDistributionChart')
  if (!chartDom) {
    console.error('找不到科室预约分布图表DOM元素');
    return;
  }
  
  departmentDistributionChart = echarts.init(chartDom)
  
  // 检查数据是否有效
  const hasValidData = data && 
                      data.departments && 
                      Array.isArray(data.departments) && 
                      data.departments.length > 0 &&
                      data.counts && 
                      Array.isArray(data.counts);
  
  if (!hasValidData) {
    console.warn('没有有效的科室预约分布数据，显示默认提示');
    // 显示无数据提示
    const option = {
      title: {
        text: '暂无预约数据',
        left: 'center',
        top: 'center',
        textStyle: {
          color: '#999',
          fontSize: 16,
          fontWeight: 'normal'
        }
      },
      tooltip: {
        formatter: '暂无预约数据'
      },
      series: [
        {
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['50%', '50%'],
          data: [{ value: 1, name: '暂无数据', itemStyle: { color: '#eee' } }],
          label: {
            show: false
          },
          emphasis: {
            scale: false
          }
        }
      ]
    };
    
    departmentDistributionChart.setOption(option);
    return;
  }
  
  // 检查是否所有科室预约数都为0
  const allZero = data.counts.every(count => count === 0);
  
  if (allZero) {
    console.warn('所有科室预约数都为0，显示特殊提示');
    // 显示所有科室但预约数为0的图表
    const option = {
      title: {
        text: '暂无预约数据',
        left: 'center',
        top: 'center',
        textStyle: {
          color: '#999',
          fontSize: 16,
          fontWeight: 'normal'
        }
      },
      tooltip: {
        trigger: 'item',
        formatter: '{b}: 0'
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        data: data.departments
      },
      series: [
        {
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['50%', '50%'],
          data: data.departments.map(dept => ({
            name: dept,
            value: 1, // 给一个相等的值以便显示
            itemStyle: {
              color: '#eee',
              opacity: 0.5
            }
          })),
          label: {
            show: true,
            formatter: '{b}: 0'
          },
          emphasis: {
            scale: false
          }
        }
      ]
    };
    
    departmentDistributionChart.setOption(option);
    return;
  }
  
  // 创建图表数据
  const chartData = data.departments.map((dept, index) => ({
    name: dept,
    value: data.counts[index] || 0
  }));
  
  // 为图表选择合适的颜色
  const colors = [
    '#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de',
    '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc', '#8d98b3'
  ];
  
  // 确保每个科室有不同的颜色
  const colorMap = {};
  data.departments.forEach((dept, index) => {
    colorMap[dept] = colors[index % colors.length];
  });
  
  // 设置图表选项
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: data.departments
    },
    series: [
      {
        name: '预约分布',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2,
          color: (params) => {
            return colorMap[params.name] || colors[params.dataIndex % colors.length];
          }
        },
        label: {
          show: true,
          formatter: '{b}: {c}'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '18',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: true
        },
        data: chartData
      }
    ]
  };
  
  console.log('设置科室预约分布图表选项:', option);
  departmentDistributionChart.setOption(option);
  
  // 响应窗口大小变化
  window.addEventListener('resize', () => {
    departmentDistributionChart && departmentDistributionChart.resize()
  });
}

// 获取预约趋势数据
const fetchAppointmentTrend = async () => {
  try {
    // 尝试从后端获取真实数据
    const res = await getAppointmentTrend();
    console.log('预约趋势数据:', res);
    
    // 如果后端返回了有效数据，使用后端数据
    if (res && res.data && Array.isArray(res.data.dates)) {
      initAppointmentTrendChart(res.data);
    } else if (res && Array.isArray(res.data)) {
      // 如果直接返回数组格式数据
      const dates = [];
      const counts = [];
      res.data.forEach(item => {
        dates.push(item.date);
        counts.push(item.count);
      });
      initAppointmentTrendChart({ dates, counts });
    } else {
      // 如果后端未实现或返回格式不对，则使用模拟数据
      const dates = [];
      const counts = [];
      const now = new Date();
      for (let i = 6; i >= 0; i--) {
        const date = new Date(now);
        date.setDate(date.getDate() - i);
        dates.push(`${date.getMonth() + 1}/${date.getDate()}`);
        counts.push(Math.floor(Math.random() * 10) + 5); // 随机数据，但确保有一定数量
      }
      
      initAppointmentTrendChart({ dates, counts });
    }
  } catch (error) {
    console.error('获取预约趋势数据失败:', error);
    // 如果API调用失败，使用模拟数据
    const dates = [];
    const counts = [];
    const now = new Date();
    for (let i = 6; i >= 0; i--) {
      const date = new Date(now);
      date.setDate(date.getDate() - i);
      dates.push(`${date.getMonth() + 1}/${date.getDate()}`);
      counts.push(Math.floor(Math.random() * 10) + 5); // 随机数据，但确保有一定数量
    }
    
    initAppointmentTrendChart({ dates, counts });
  }
}

// 获取科室预约分布数据
const fetchDepartmentDistribution = async () => {
  try {
    // 从后端获取真实数据
    console.log('开始获取科室预约分布数据...');
    const res = await getDepartmentDistribution();
    console.log('科室预约分布数据原始响应:', res);
    
    // 处理后端返回的数据
    let processedData = null;
    
    if (res && res.data) {
      // 标准格式：res.data包含departments和counts数组
      if (res.data.departments && Array.isArray(res.data.departments) && 
          res.data.counts && Array.isArray(res.data.counts)) {
        processedData = res.data;
        console.log('使用标准格式的科室预约分布数据:', processedData);
      } 
      // 数组格式：res.data是数组，需要提取科室名称和预约数
      else if (Array.isArray(res.data)) {
        const departments = [];
        const counts = [];
        res.data.forEach(item => {
          departments.push(item.departmentName || item.name);
          counts.push(item.count || item.value || 0);
        });
        processedData = { departments, counts };
        console.log('处理数组格式的科室预约分布数据:', processedData);
      }
    } 
    // 直接返回结果格式
    else if (res && res.departments && Array.isArray(res.departments) && 
             res.counts && Array.isArray(res.counts)) {
      processedData = res;
      console.log('使用直接返回的科室预约分布数据:', processedData);
    }
    
    // 如果成功处理数据，初始化图表
    if (processedData) {
      initDepartmentDistributionChart(processedData);
    } else {
      console.warn('无法处理科室预约分布数据，使用空数据');
      initDepartmentDistributionChart({ departments: [], counts: [] });
    }
  } catch (error) {
    console.error('获取科室预约分布数据失败:', error);
    // 如果API调用失败，使用空数据
    initDepartmentDistributionChart({ departments: [], counts: [] });
  }
}

// 页面加载时获取数据
onMounted(() => {
  loading.value = true;
  Promise.all([
    getAppointmentCount(),
    getDoctorCount(),
    getPatientCount(),
    getRecentAppointments({ pageNum: 1, pageSize: 5 })
  ]).then(([appointmentCount, doctorCount, patientCount, recentRes]) => {
    console.log('appointmentCount:', appointmentCount)
    console.log('doctorCount:', doctorCount)
    console.log('patientCount:', patientCount)
    console.log('recentRes:', recentRes)
    stats.value.appointments = Number(appointmentCount) || 0;
    stats.value.doctors = Number(doctorCount) || 0;
    stats.value.patients = Number(patientCount) || 0;
    // recentAppointments 赋值
    if (recentRes && Array.isArray(recentRes.data)) {
      recentAppointments.value = recentRes.data;
    } else if (recentRes && Array.isArray(recentRes.records)) {
      recentAppointments.value = recentRes.records;
    } else if (Array.isArray(recentRes)) {
      recentAppointments.value = recentRes;
    } else {
      recentAppointments.value = [];
    }
    return fetchTodayStats();
  }).catch(() => {
    stats.value.appointments = 0;
    stats.value.doctors = 0;
    stats.value.patients = 0;
    stats.value.todayAppointments = 0;
    stats.value.pendingAppointments = 0;
    recentAppointments.value = [];
  }).finally(() => {
    loading.value = false;
    // 初始化图表
    fetchAppointmentTrend();
    fetchDepartmentDistribution();
  });
})

// 组件卸载时销毁图表实例
onUnmounted(() => {
  if (appointmentTrendChart) {
    appointmentTrendChart.dispose()
    appointmentTrendChart = null
  }
  
  if (departmentDistributionChart) {
    departmentDistributionChart.dispose()
    departmentDistributionChart = null
  }
  
  // 移除事件监听
  window.removeEventListener('resize', () => {})
})
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
}

.stat-card {
  height: 150px;
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.stat-title {
  font-size: 16px;
  color: #606266;
}

.stat-icon {
  font-size: 24px;
  color: #409EFF;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin: 10px 0;
}

.stat-footer {
  font-size: 14px;
  color: #909399;
  display: flex;
  justify-content: space-between;
}

.up {
  color: #67C23A;
}

.down {
  color: #F56C6C;
}

.pending {
  color: #E6A23C;
}

.chart-card {
  min-height: 300px;
}

.chart-header {
  margin-bottom: 15px;
}

.chart-header h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.chart-container {
  height: 300px;
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 