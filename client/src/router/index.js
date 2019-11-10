import Vue from 'vue'
import Router from 'vue-router'
import Home from "../components/Home";
import Information from "../components/Information";
import collectionStart from "../components/CollectionSysV1";
import LocationShowPage from "../components/LocationShowPage";
import LocationConfigPage from "../components/LocationConfigPage";
import CollectionClient from "../components/CollectionClient";
import CollectionConfig from "../components/CollectionSysV2";
Vue.use(Router)

export default new Router({
  routes: [
    {
      mode:'history',
      path: '/',
      name: 'Home',
      component: Home,
      children:[
        {
          path:'/',
          component:Information
        },
        {
          path:'/collectionSysV1',
          component:collectionStart
        },
        {
          path:'/locationShowPage',
          component:LocationShowPage
        },
        {
          path:'/locationConfigPage',
          component:LocationConfigPage
        },
        {
          path:'/collectionSysV2',
          component:CollectionConfig
        }

      ]
    },{
      path:"/c",
      name:'CollectionClient',
      component:CollectionClient,
    }
  ]
})
