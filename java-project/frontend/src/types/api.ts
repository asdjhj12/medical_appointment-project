export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
  timestamp?: number;
}

export interface PageResult<T = any> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
} 