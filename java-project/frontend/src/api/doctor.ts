import request from '@/utils/request';

// 医生数据结构
export interface Doctor {
  id: number;
  userId?: number;
  doctorName?: string; // 医生姓名字段
  name?: string; // 兼容旧字段
  userName?: string; // 关联用户的真实姓名
  realName?: string; // 用户真实姓名
  departmentId?: number;
  departmentName?: string;
  title?: string;
  specialty?: string;
  introduction?: string;
  status?: number; // 1:正常, 0:禁用
  avatar?: string;
  // ... 其他字段
}

// 查询条件
export interface DoctorQuery {
  pageNum?: number;
  pageSize?: number;
  departmentId?: number;
  name?: string;
  status?: number;
}

// 获取所有医生列表（不分页）
export function listAllDoctors() {
  console.log('调用获取所有医生API');
  return request({
    url: '/api/doctors/all',
    method: 'get',
  }).then(response => {
    console.log('获取医生列表响应:', response);
    return response;
  }).catch(error => {
    console.error('获取医生列表出错:', error);
    throw error;
  });
}

// 获取医生分页列表（带查询条件）
export function getDoctorList(query: DoctorQuery) {
  return request({
    url: '/api/doctors',
    method: 'get',
    params: query,
  });
}

// 根据科室ID获取医生列表
export function listDoctorsByDepartment(departmentId: number) {
  return request({
    url: `/api/doctors/department/${departmentId}/list`,
    method: 'get',
  });
}

// 获取医生详情
export function getDoctorById(id: number) {
  return request({
    url: `/api/doctors/${id}`,
    method: 'get',
  });
}

// 创建医生
export function createDoctor(data: Partial<Doctor>) {
  return request({
    url: '/api/doctors',
    method: 'post',
    data,
  });
}

// 更新医生
export function updateDoctor(data: Partial<Doctor>) {
  return request({
    url: `/api/doctors/${data.id}`,
    method: 'put',
    data,
  });
}

// 删除医生
export function deleteDoctor(id: number) {
  return request({
    url: `/api/doctors/${id}`,
    method: 'delete',
  });
}

// 获取医生总数
export function getDoctorCount() {
  return request({
    url: '/api/doctors/count',
    method: 'get',
  });
}

// 获取医生活跃数量
export function getActiveDoctorCount() {
  return request({
    url: '/api/doctors/active/count',
    method: 'get',
  }).then(res => res && res.data ? res.data : 0);
}

// 通过userId获取医生信息
export function getDoctorByUserId(userId: number) {
  return request({
    url: `/api/doctors/user/${userId}`,
    method: 'get',
  })
} 