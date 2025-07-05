// 病历状态枚举
export enum MedicalRecordStatus {
  PENDING = 'pending',    // 待就诊
  COMPLETED = 'completed', // 已完成
  CANCELLED = 'cancelled'  // 已取消
}

// 病历信息接口
export interface MedicalRecord {
  id: number
  patientId: number
  patientName: string
  departmentId: number
  departmentName: string
  doctorId: number
  doctorName: string
  visitTime: string
  chiefComplaint: string
  presentIllness: string
  pastHistory: string
  physicalExam: string
  auxiliaryExam: string
  diagnosis: string
  treatment: string
  medicalAdvice: string
  status: MedicalRecordStatus
  createTime: string
  updateTime: string
}

// 病历查询参数接口
export interface MedicalRecordQuery {
  patientName?: string
  departmentId?: number
  doctorId?: number
  visitTime?: [string, string]
  page: number
  pageSize: number
}

// 病历创建参数接口
export interface CreateMedicalRecordParams {
  patientId: number
  departmentId: number
  doctorId: number
  visitTime: string
  chiefComplaint: string
  presentIllness: string
  pastHistory: string
  physicalExam: string
  auxiliaryExam: string
  diagnosis: string
  treatment: string
  medicalAdvice: string
}

// 病历更新参数接口
export interface UpdateMedicalRecordParams extends CreateMedicalRecordParams {
  id: number
  status?: MedicalRecordStatus
}

// 分页响应接口
export interface PageResponse<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
}

export interface MedicalRecordMedicine {
  id?: number
  medicalRecordId?: number
  medicineId: number
  medicineName: string
  quantity: number
  unit: string
  dosageUsage: string
} 