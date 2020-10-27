import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },

  {
    path: '/login',
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
