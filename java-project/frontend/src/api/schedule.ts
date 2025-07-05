import request from '@/utils/request';

// 排班类型（可根据实际后端返回结构调整）
export interface Schedule {
  id?: number; // 使 id 成为可选字段，因为新增时没有 id
  doctorId: number;
  scheduleDate: string; // 日期字符串
  period: number; // 时段 (数字)
  maxAppointments: number;
  currentAppointments?: number; // 使 currentAppointments 成为可选字段
  status: number; // 0:停诊 1:正常
  description?: string; // 使 description 成为可选字段
  doctorName?: string; // 医生姓名，用于显示
  departmentName?: string; // 科室名称，用于显示
  periodText?: string; // 时段文本，用于显示
}

// 查询参数类型
export interface ScheduleQuery {
  [key: string]: any;
}

// 获取排班列表
export function getScheduleList(query: ScheduleQuery) {
  return request({
    url: '/api/doctor-schedules',
    method: 'get',
    params: query,
  });
}

// 新增排班
export function addSchedule(data: Schedule) {
  return request({
    url: '/api/doctor-schedules',
    method: 'post',
    data,
  });
}

// 更新排班
export function updateSchedule(data: Schedule) {
  return request({
    url: `/api/doctor-schedules/${data.id}`,
    method: 'put',
    data,
  });
}

// 删除排班
export function deleteSchedule(id: number) {
  return request({
    url: `/api/doctor-schedules/${id}`,
    method: 'delete',
  });
}

// 获取医生列表 (用于排班表单中的医生选择)
import { listAllDoctors } from '@/api/doctor';
export { listAllDoctors }; 