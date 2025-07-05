import { MenuItem } from '@/types/menu'

// 管理员菜单
export const adminMenus: MenuItem[] = [
  {
    path: '/admin/dashboard',
    title: '控制台',
    icon: 'Odometer'
  },
  {
    path: '/admin/department',
    title: '科室管理',
    icon: 'OfficeBuilding'
  },
  {
    path: '/admin/doctor',
    title: '医生管理',
    icon: 'User'
  },
  {
    path: '/admin/schedule',
    title: '排班管理',
    icon: 'Calendar'
  },
  {
    path: '/admin/appointment',
    title: '预约管理',
    icon: 'List'
  },
  {
    path: '/admin/medical-record',
    title: '电子病历管理',
    icon: 'Document'
  }
]

// 用户菜单
export const userMenus: MenuItem[] = [
  {
    path: '/user/dashboard',
    title: '首页',
    icon: 'Odometer'
  },
  {
    path: '/user/appointment',
    title: '我的预约',
    icon: 'List'
  },
  {
    path: '/user/medical-record',
    title: '我的病历',
    icon: 'Document'
  },
  {
    path: '/user/profile',
    title: '个人信息',
    icon: 'User'
  }
] 