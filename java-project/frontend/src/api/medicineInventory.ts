import request from '@/utils/request';

// 药品库存类型
export interface MedicineInventory {
  id?: number; // 药品ID
  name: string; // 药品名称
  specification?: string; // 规格
  manufacturer?: string; // 生产厂家
  category?: string; // 药品类别
  unit?: string; // 单位
  price: number; // 单价
  stockQuantity: number; // 库存数量
  minStock: number; // 最低库存
  status: number; // 状态：1-正常，0-停用
}

// 查询参数类型
export interface MedicineInventoryQuery {
  name?: string; // 药品名称
  category?: string; // 药品类别
  manufacturer?: string; // 生产厂家
  status?: number; // 状态
  lowStock?: boolean; // 是否只查询低库存
  pageNum?: number; // 页码
  pageSize?: number; // 每页条数
}

// 分页结果类型
export interface PageResult<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}

// 获取药品库存列表
export function getMedicineInventoryList(query: MedicineInventoryQuery) {
  return request<PageResult<MedicineInventory> | MedicineInventory[]>({
    url: '/api/medicine-inventory',
    method: 'get',
    params: query,
  });
}

// 获取药品库存详情
export function getMedicineInventoryById(id: number) {
  return request<MedicineInventory>({
    url: `/api/medicine-inventory/${id}`,
    method: 'get',
  });
}

// 新增药品库存
export function addMedicineInventory(data: MedicineInventory) {
  return request({
    url: '/api/medicine-inventory',
    method: 'post',
    data,
  });
}

// 更新药品库存
export function updateMedicineInventory(data: MedicineInventory) {
  return request({
    url: '/api/medicine-inventory',
    method: 'put',
    data,
  });
}

// 删除药品库存
export function deleteMedicineInventory(id: number) {
  return request({
    url: `/api/medicine-inventory/${id}`,
    method: 'delete',
  });
}

// 更新库存数量
export function updateStockQuantity(id: number, quantityChange: number) {
  return request({
    url: `/api/medicine-inventory/${id}/stock`,
    method: 'put',
    params: { quantityChange },
  });
}

// 检查库存是否低于最低库存
export function checkLowStock(id: number) {
  return request<boolean>({
    url: `/api/medicine-inventory/${id}/low-stock`,
    method: 'get',
  });
} 