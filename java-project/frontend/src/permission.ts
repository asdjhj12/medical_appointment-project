import router from './router';
import { useUserStore } from './store/user';

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();

  // 检查用户是否已登录
  if (to.meta.requiresAuth && !userStore.isLoggedIn()) {
    next('/login');
    return;
  }

  // 如果用户已登录，获取用户信息
  if (userStore.isLoggedIn() && !userStore.userInfo) {
    try {
      await userStore.getUserInfo();
    } catch (error) {
      console.error('获取用户信息失败:', error);
      next('/login');
      return;
    }
  }

  // 检查路由是否需要特定角色权限
  if (to.meta.roles && to.meta.roles.length > 0) {
    const userRole = userStore.userInfo?.role;
    // 如果用户角色不在允许的角色列表中，则重定向到403页面
    if (!userRole || !to.meta.roles.includes(userRole)) {
      next('/403');
      return;
    }
  }

  next();
}); 