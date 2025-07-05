import request from '@/utils/request'

// 患者信息类型（可根据实际后端返回结构调整）
export interface Patient {
  id: number;
  userId: number;
  patientName: string;
  [key: string]: any;
}

// 根据userId获取患者信息
export function getPatientByUserId(userId: number) {
  return request<Patient>({
    url: `/api/patient/user/${userId}`,
    method: 'get'
  })
}

// 根据userId自动注册患者信息
export function registerPatientByUserId(userId: number) {
  return request({
    url: `/api/patient/registerByUserId/${userId}`,
    method: 'post'
  })
}

// 获取患者总数（新接口）
export function getPatientCount() {
  return request({
    url: '/api/patient/count',
    method: 'get',
  });
}
