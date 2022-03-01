import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import vuetify from './plugins/vuetify';
import "material-design-icons-iconfont/dist/material-design-icons.css";
import SequentialEntrance from "vue-sequential-entrance";
import "vue-sequential-entrance/vue-sequential-entrance.css";
Vue.use(SequentialEntrance);


Vue.config.productionTip = false

const bus = new Vue();
Vue.prototype.$bus = bus;

new Vue({
  router,
  store,
  vuetify,
  render: h => h(App)
}).$mount('#app')
