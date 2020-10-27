import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Login',
    component: () => import(/* webpackChunkName: "login" */ '../views/Login.vue')
  },
  {
    path: '/sign-up',
    name: 'SignUp',
    component: () => import(/* webpackChunkName: "signUp" */ '../views/SignUp.vue')
  },
  {
    path: '/amusement-park',
    name: 'amusementPark',
    component: () => import(/* webpackChunkName: "amusementPark" */ '../views/AmusementPark.vue')
  },

]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
