import request from '@/utils/request';

// 药品库存记录类型
export interface MedicineStockRecord {
  id?: number; // 记录ID
  medicineId: number; // 药品ID
  medicineName?: string; // 药品名称
  type: number; // 类型（1-入库，2-出库）
  quantity: number; // 数量
  beforeStock?: number; // 操作前库存
  afterStock?: number; // 操作后库存
  operatorId?: number; // 操作人ID
  operatorName?: string; // 操作人姓名
  remark?: string; // 备注
  operateTime?: string; // 操作时间
  createTime?: string; // 创建时间
  updateTime?: string; // 更新时间
}

// 查询参数类型
export interface MedicineStockRecordQuery {
  medicineId?: number; // 药品ID
  medicineName?: string; // 药品名称
  type?: number; // 类型
  operatorId?: number; // 操作人ID
  startTime?: string; // 开始时间
  endTime?: string; // 结束时间
  pageNum?: number; // 页码
  pageSize?: number; // 每页条数
}

// 获取库存记录列表
export function getStockRecordList(query: MedicineStockRecordQuery) {
  return request({
    url: '/api/medicine-stock-records',
    method: 'get',
    params: query,
  });
}

// 获取库存记录详情
export function getStockRecordById(id: number) {
  return request({
    url: `/api//medicine-stock-records/${id}`,
    method: 'get',
  });
} 