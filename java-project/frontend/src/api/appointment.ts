import request from '@/utils/request';

// 预约信息类型（可根据实际后端返回结构调整）
export interface Appointment {
  id: number;
  patientId: number;
  doctorId: number;
  scheduleId: number;
  appointmentNumber: string;
  status: number; // 0:待处理 1:已确认 2:已完成 3:已取消 4:已过期
  description?: string;
  createTime?: string;
  updateTime?: string;
  userName?: string;
  doctorName?: string;
  departmentName?: string;
  appointmentDate?: string;
  timeSlot?: string;
}

// 查询参数类型
export interface AppointmentQuery {
  patientId?: number;
  doctorId?: number;
  departmentId?: number;
  status?: number;
  startTime?: string;
  endTime?: string;
  appointmentNumber?: string;
  pageNum?: number;
  pageSize?: number;
}

// 获取预约列表
export function getAppointmentList(query: AppointmentQuery) {
  return request({
    url: '/api/appointments',
    method: 'get',
    params: query,
    transformResponse: [(data) => {
      try {
        // 尝试解析JSON
        const parsedData = JSON.parse(data);
        
        // 直接返回records数组，如果存在的话
        if (parsedData && parsedData.records && Array.isArray(parsedData.records)) {
          return parsedData.records;
        }
        
        // 如果是标准响应格式，返回data字段
        if (parsedData && parsedData.code === 200 && parsedData.data) {
          if (Array.isArray(parsedData.data)) {
            return parsedData.data;
          }
          if (parsedData.data.records && Array.isArray(parsedData.data.records)) {
            return parsedData.data.records;
          }
        }
        
        return parsedData;
      } catch (e) {
        return data;
      }
    }]
  });
}

// 获取当前用户的预约列表
export function getUserAppointmentList(params: { userId: number } & AppointmentQuery) {
  const { userId, ...query } = params;
  return request({
    url: `/api/appointments/user/${userId}`,
    method: 'get',
    params: query,
  });
}

// 获取预约详情
export function getAppointmentById(id: number) {
  return request({
    url: `/api/appointments/${id}`,
    method: 'get',
  });
}

// 创建预约
export function createAppointment(data: { patientId: number; doctorId: number; scheduleId: number; description?: string }) {
  return request({
    url: '/api/appointments',
    method: 'post',
    data,
  });
}

// 更新预约状态
export function updateAppointmentStatus(id: number, status: number) {
  return request({
    url: `/api/appointments/${id}/status`,
    method: 'put',
    params: { status },
  });
}

// 取消预约
export function cancelAppointment(id: number) {
  return request({
    url: `/api/appointments/${id}/cancel`,
    method: 'put',
  });
}

// 完成预约
export function completeAppointment(id: number) {
  return request({
    url: `/api/appointments/${id}/complete`,
    method: 'put',
  });
}

// 获取今日预约列表
export function getTodayAppointments() {
  return request({
    url: '/api/appointments/today/list',
    method: 'get',
  });
}

// 获取最近预约（分页，按时间倒序）
export function getRecentAppointments(params = { pageNum: 1, pageSize: 5 }) {
  return request({
    url: '/api/appointments',
    method: 'get',
    params: { ...params, sortField: 'createTime', sortOrder: 'desc' },
  });
}

// 获取总预约数（新接口）
export function getAppointmentCount() {
  return request({
    url: '/api/appointments/count',
    method: 'get',
  });
}

// 医生端mock统计API
export async function getDoctorAppointmentsStats(doctorId: number) {
  // 这里返回静态数据，实际可对接后端
  return {
    myAppointments: 12,
    todayAppointments: 3,
    pendingAppointments: 2
  }
}

export async function getDoctorRecentAppointments(doctorId: number) {
  // 这里返回静态数据，实际可对接后端
  return [
    {
      appointmentNumber: 'A20240531001',
      userName: '张三',
      departmentName: '内科',
      scheduleDate: '2024-05-31',
      periodText: '上午',
      status: 'PENDING',
      createTime: '2024-05-31 09:00:00'
    },
    {
      appointmentNumber: 'A20240531002',
      userName: '李四',
      departmentName: '外科',
      scheduleDate: '2024-05-31',
      periodText: '下午',
      status: 'CONFIRMED',
      createTime: '2024-05-31 14:00:00'
    }
  ]
}

// 获取医生的预约列表（不分页）
export function getByDoctorId(doctorId: number) {
  return request({
    url: `/api/appointments/doctor/${doctorId}`,
    method: 'get',
  });
}

// 获取预约趋势数据
export function getAppointmentTrend(days: number = 7) {
  return request({
    url: '/api/appointments/stats/trend',
    method: 'get',
    params: { days }
  });
}

// 获取科室预约分布
export function getDepartmentDistribution() {
  return request({
    url: '/api/appointments/stats/department-distribution',
    method: 'get',
    transformResponse: [(data) => {
      try {
        console.log('科室预约分布原始响应数据:', data);
        // 尝试解析JSON
        const parsedData = JSON.parse(data);
        console.log('科室预约分布解析后数据:', parsedData);
        
        // 如果是标准响应格式，返回data字段
        if (parsedData && parsedData.code === 200 && parsedData.data) {
          console.log('返回标准响应格式的data字段:', parsedData.data);
          return parsedData.data;
        }
        
        return parsedData;
      } catch (e) {
        console.error('解析科室预约分布数据失败:', e);
        return data;
      }
    }]
  });
}

// 其他API方法可按需补充 