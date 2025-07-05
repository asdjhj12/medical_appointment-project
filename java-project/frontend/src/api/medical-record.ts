import request from '@/utils/request'
import type {
  MedicalRecord as MedicalRecordType,
  MedicalRecordQuery as MedicalRecordQueryType,
  CreateMedicalRecordParams,
  UpdateMedicalRecordParams,
  PageResponse
} from '@/types/medical-record'

// 电子病历类型（可根据实际后端返回结构调整）
export interface MedicalRecordApi {
  id: number;
  userId: number;
  doctorId: number;
  departmentId?: number;
  appointmentNumber?: string;
  doctorName?: string;
  departmentName?: string;
  diagnosis?: string;
  createTime?: string;
  // ... 其他字段
}

// 查询参数类型
export interface MedicalRecordQueryApi {
  userId?: number;
  doctorId?: number;
  departmentId?: number;
  doctorName?: string;
  pageNum?: number;
  pageSize?: number;
}

// 获取电子病历列表
export function getMedicalRecordList(query: MedicalRecordQueryApi) {
  return request({
    url: '/api/medical-records',
    method: 'get',
    params: query,
  });
}

// 获取当前用户的电子病历列表
export function getUserMedicalRecordList(query: MedicalRecordQueryApi) {
  return request({
    url: '/api/medical-records/user',
    method: 'get',
    params: query,
  });
}

// 获取病历详情（含用药明细）
export function getMedicalRecordDetail(id: number) {
  return request({
    url: `/api/medical-records/detail/${id}`,
    method: 'get',
  });
}

// 创建病历
export function createMedicalRecord(data: any) {
  return request({
    url: '/api/medical-records',
    method: 'post',
    data,
  });
}

// 更新病历
export function updateMedicalRecord(data: any) {
  return request({
    url: '/api/medical-records',
    method: 'put',
    data,
  });
}

// 删除电子病历
export function deleteMedicalRecord(id: number) {
  return request({
    url: `/api/medical-records/${id}`,
    method: 'delete',
  });
}

// 搜索患者
export function searchPatients(keyword: string) {
  return request.get('/api/patients/search', {
    params: { keyword }
  })
}

// 获取科室列表
export function getDepartmentList() {
  return request.get('/api/departments').then(res => res.data)
}

// 获取医生列表
export function getDoctorList(departmentId: number) {
  return request.get('/api/doctors', {
    params: { departmentId }
  })
}

// 根据患者ID获取电子病历列表
export function getMedicalRecordsByPatientId(patientId: number) {
  return request({
    url: `/api/medical-records/patient/${patientId}`,
    method: 'get',
  });
}

// 根据医生ID获取电子病历列表
export function getMedicalRecordsByDoctorId(doctorId: number) {
  return request({
    url: `/api/medical-records/doctor/${doctorId}`,
    method: 'get',
  });
}

// 管理员获取所有电子病历（含用药明细）
export async function getAllMedicalRecords() {
  // 这里直接返回 await 结果，确保是 Promise
  return await request({
    url: '/api/medical-records/list',
    method: 'get'
  })
}

// 用户取药
export function takeMedicine(data: { recordId: number }) {
  return request({
    url: '/api/medical-records/take-medicine',
    method: 'post',
    data
  })
}


