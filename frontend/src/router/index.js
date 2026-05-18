import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/admin',
    component: () => import('../views/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    children: [
      { path: 'dashboard', component: () => import('../views/admin/Dashboard.vue') },
      { path: 'student', component: () => import('../views/admin/StudentManage.vue') },
      { path: 'building', component: () => import('../views/admin/BuildingManage.vue') },
      { path: 'room', component: () => import('../views/admin/RoomManage.vue') },
      { path: 'accommodation', component: () => import('../views/admin/AccommodationManage.vue') },
      { path: 'repair', component: () => import('../views/admin/RepairManage.vue') },
      { path: 'visitor', component: () => import('../views/admin/VisitorManage.vue') },
      { path: 'checkin', component: () => import('../views/admin/CheckinManage.vue') }
    ]
  },
  {
    path: '/student',
    component: () => import('../views/StudentLayout.vue'),
    redirect: '/student/dashboard',
    children: [
      { path: 'dashboard', component: () => import('../views/student/Dashboard.vue') },
      { path: 'info', component: () => import('../views/student/MyInfo.vue') },
      { path: 'repair', component: () => import('../views/student/MyRepair.vue') },
      { path: 'visitor', component: () => import('../views/student/VisitorRegister.vue') },
      { path: 'checkin', component: () => import('../views/student/MyCheckin.vue') }
    ]
  },
  {
    path: '/',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
