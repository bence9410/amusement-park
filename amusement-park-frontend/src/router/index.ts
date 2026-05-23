import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Login',
      component: () => import('../pages/Login.vue'),
    },
    {
      path: '/sign-up',
      name: 'SignUp',
      component: () => import('../pages/SignUp.vue'),
    },
    {
      path: '/amusement-parks',
      name: 'amusementParks',
      component: () => import('../pages/AmusementParks.vue'),
    },
    {
      path: '/machines',
      name: 'machines',
      component: () => import('../pages/Machines.vue'),
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../pages/Admin.vue'),
    },
  ],
})

export default router
