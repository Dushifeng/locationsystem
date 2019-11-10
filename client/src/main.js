// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css'
import 'fabric'

var axios = require('axios')
axios.defaults.baseURL = 'http://192.168.1.100:8081/api'
axios.defaults.withCredentials = true
// 将API方法绑定到全局
Vue.prototype.$axios = axios

Vue.use(ElementUI)

new Vue({
  el: '#app',
  render: h => h(App),
  router,
})
