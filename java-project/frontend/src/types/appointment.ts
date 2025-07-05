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