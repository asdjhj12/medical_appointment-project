import { createRouter, createWebHistory, RouteRecordRaw, NavigationGuardNext, RouteLocationNormalized } from 'vue-router';
import { useUserStore } from '../store/user';
import { h } from 'vue';

// 公共路由 (根据 index.js 和 index.ts 合并调整)
const publicRoutes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'), // 假设注册组件路径是这个
    meta: { title: '注册', requiresAuth: false }
  },
  {
    path: '/',
    redirect: () => {
      const userStore = useUserStore();
      if (!userStore.isLoggedIn()) {
        return '/login';
      }
      // 根据用户角色重定向到不同的首页
      const userRole = userStore.userInfo?.role;
      if (userRole === 'ADMIN') {
        return '/admin/dashboard';
      } else if (userRole === 'DOCTOR') {
        return '/doctor/appointment';
      } else {
        return '/user/dashboard';
      }
    }
  },
  // 添加顶部导航链接对应的路由重定向
  {
    path: '/dashboard',
    redirect: '/user/dashboard',
    meta: { requiresAuth: true }
  },
  {
    path: '/my-appointments',
    redirect: '/user/appointment',
    meta: { requiresAuth: true }
  },
  {
    path: '/my-records',
    redirect: '/user/medical-record',
    meta: { requiresAuth: true }
  },
  {
    path: '/my-profile',
    redirect: '/user/profile',
    meta: { requiresAuth: true }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'), // 假设 404 页面路径是这个
    meta: { title: '404' }
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/403.vue'),
    meta: { title: '403', requiresAuth: false }
  },
   {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
];

// 管理员路由 (根据 index.js 调整)
const adminRoutes: Array<RouteRecordRaw> = [
  {
    path: '/admin',
    component: () => import('@/layout/admin/index.vue'), // 假设管理员布局组件在这里
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true, roles: ['ADMIN'] }, // 添加认证和角色 meta
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/dashboard/index.vue'),
        meta: { title: '控制台' }
      },
      {
        path: 'department',
        name: 'AdminDepartment',
        component: () => import('@/views/admin/department/index.vue'), // 假设科室管理组件在这里
        meta: { title: '科室管理' }
      },
      {
        path: 'doctor',
        name: 'AdminDoctor',
        component: () => import('@/views/admin/doctor/index.vue'), // 假设医生管理组件在这里
        meta: { title: '医生管理' }
      },
      {
        path: 'schedule',
        name: 'AdminSchedule',
        component: () => import('@/views/admin/schedule/index.vue'), // 假设排班管理组件在这里
        meta: { title: '排班管理' }
      },
      {
        path: 'appointment',
        name: 'AdminAppointment',
        component: () => import('@/views/admin/appointment/index.vue'), // 使用新的管理员专用预约组件
        meta: { title: '预约管理' }
      },
      {
        path: 'medical-record',
        name: 'AdminMedicalRecord',
        component: () => import('@/views/admin/medicalRecord/index.vue'), // 修改为正确的电子病历管理组件路径
        meta: { title: '电子病历管理' }
      },
      {
        path: 'medicine-inventory',
        name: 'AdminMedicineInventory',
        component: () => import('@/views/admin/medicine-inventory/index.vue'),
        meta: { title: '药品库存管理' }
      },
      {
        path: 'medicine-stock-record',
        name: 'AdminMedicineStockRecord',
        component: () => import('@/views/admin/medicine-stock-record/index.vue'),
        meta: { title: '药品库存记录' }
      },
      {
        path: 'user',
        name: 'AdminUser',
        component: () => import('@/views/admin/user/index.vue'),
        meta: { title: '用户管理' }
      }
    ]
  }
];

// 用户路由 (根据 index.js 调整)
const userRoutes: Array<RouteRecordRaw> = [
  {
    path: '/user',
    component: () => import('@/layout/user/index.vue'), // 假设用户布局组件在这里
    redirect: '/user/dashboard',
    meta: { requiresAuth: true, roles: ['USER'] }, // 添加认证和角色 meta
    children: [
      {
        path: 'dashboard',
        name: 'UserDashboard',
        component: () => import('@/views/dashboard/index.vue'), // 更正用户仪表盘组件路径
        meta: { title: '首页' }
      },
      {
        path: 'appointment',
        name: 'UserAppointment',
        component: () => import('@/views/user/appointment/index.vue'), // 假设用户预约列表组件在这里
        meta: { title: '我的预约' }
      },
      {
        path: 'medical-record',
        name: 'UserMedicalRecord',
        component: () => import('@/views/user/medical-record/index.vue'), // 假设用户病历列表组件在这里
        meta: { title: '我的病历' }
      },
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('@/views/profile/index.vue'), // 用户个人信息页面
        meta: { title: '个人信息' }
      }
    ]
  }
];

// 医生路由
const doctorRoutes: Array<RouteRecordRaw> = [
  {
    path: '/doctor',
    component: () => import('@/layout/doctor/index.vue'),
    redirect: '/doctor/appointment',
    meta: { requiresAuth: true, roles: ['DOCTOR'] },
    children: [
      {
        path: 'appointment',
        name: 'DoctorAppointment',
        component: () => import('@/views/doctor/appointment/index.vue'),
        meta: { title: '预约管理' }
      },
      {
        path: 'medical-record',
        name: 'DoctorMedicalRecord',
        component: () => import('@/views/doctor/medical-record/index.vue'),
        meta: { title: '病历管理' }
      },
      {
        path: 'profile',
        name: 'DoctorProfile',
        component: () => import('@/views/doctor/profile/index.vue'),
        meta: { title: '个人信息' }
      },
      {
        path: 'doctor',
        name: 'DoctorInfo',
        component: () => import('@/views/doctor/doctor/index.vue'),
        meta: { title: '医生信息' }
      },
      {
        path: 'department',
        name: 'DoctorDepartment',
        component: () => import('@/views/doctor/department/index.vue'),
        meta: { title: '科室信息' }
      }
    ]
  }
];

const routes: Array<RouteRecordRaw> = [...publicRoutes, ...adminRoutes, ...userRoutes, ...doctorRoutes];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 路由守卫 (精简逻辑)
router.beforeEach(async (to: RouteLocationNormalized, _from: RouteLocationNormalized, next: NavigationGuardNext) => {
  console.log('导航守卫：正在跳转到', to.path);
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 医院预约系统` : '医院预约系统';

  const userStore = useUserStore();
  const isLoggedIn = userStore.isLoggedIn();
  const hasUserInfo = !!userStore.userInfo;
  const requiresAuth = to.meta.requiresAuth !== false; // 默认为 true

  if (requiresAuth && (!isLoggedIn || !hasUserInfo)) {
    console.log('导航守卫：需要认证但用户未登录或无用户信息');
    // 如果目标路由是登录页或注册页，直接跳转，避免无限重定向
    if (to.path === '/login' || to.path === '/register') {
      next();
    } else {
      console.log('导航守卫：重定向到登录页');
      next('/login');
    }
  } else {
    // 允许所有人访问的公共页面
    const publicPaths = ['/login', '/register', '/404', '/403'];
    if (publicPaths.includes(to.path)) {
      next();
      return;
    }
    // 角色访问限制
    const role = userStore.userInfo?.role;
    if (to.path.startsWith('/user') && role !== 'USER') {
      if (role === 'ADMIN') {
        next({ path: '/admin/dashboard', replace: true });
      } else if (role === 'DOCTOR') {
        next({ path: '/doctor/appointment', replace: true });
      } else {
        next('/login');
      }
      return;
    }
    if (to.path.startsWith('/doctor') && role !== 'DOCTOR') {
      if (role === 'ADMIN') {
        next({ path: '/admin/dashboard', replace: true });
      } else if (role === 'USER') {
        next({ path: '/user/dashboard', replace: true });
      } else {
        next('/login');
      }
      return;
    }
    if (to.path.startsWith('/admin') && role !== 'ADMIN') {
      if (role === 'DOCTOR') {
        next({ path: '/doctor/appointment', replace: true });
      } else if (role === 'USER') {
        next({ path: '/user/dashboard', replace: true });
      } else {
        next('/login');
      }
      return;
    }
    console.log('导航守卫：满足条件或无需认证，正常跳转');
    next(); // 其他情况，正常跳转
  }
});

export default router;
