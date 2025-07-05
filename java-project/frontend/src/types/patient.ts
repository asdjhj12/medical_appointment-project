export interface Patient {
  id: number;
  userId: number;
  patientName: string;
  gender?: number;
  age?: number;
  phone?: string;
  idCard?: string;
  address?: string;
  // ...其他字段
} 