// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css'
import 'fabric'
const os = require('os');
var axios = require('axios')



let protocol = window.location.protocol; //协议
let host = window.location.host.split(":")[0]; //主机

console.log(protocol + "//" + host  +":8081")

axios.defaults.baseURL = protocol + "//" + host  +":8081/api";

axios.defaults.withCredentials = true
axios.defaults.changeOrigin = true
// 将API方法绑定到全局
Vue.prototype.$axios = axios

Vue.use(ElementUI)

new Vue({
  el: '#app',
  render: h => h(App),
  router,
})
