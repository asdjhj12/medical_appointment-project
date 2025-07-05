import request from '@/utils/request';

// 获取所有科室列表
export function listAllDepartments() {
  return request({
    url: '/api/departments',
    method: 'get',
  });
}

// 获取科室列表
export function getDepartmentList() {
  return request({
    url: '/api/departments',
    method: 'get',
  });
} 