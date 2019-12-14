<template>
  <el-container>
      <el-header>
        <el-menu
          :default-active="activeIndex"
          class="el-menu-demo"
          mode="horizontal"
        background-color="#545c64"
        text-color="#fff"
        active-text-color="#ffd04b"
        router>
        <el-menu-item index="/">首页</el-menu-item>
        <el-submenu index="2">
          <template slot="title">采集系统</template>
          <el-menu-item index="collectionSysV1" disabled>version1</el-menu-item>
          <el-menu-item index="collectionSysV2">version2</el-menu-item>
        </el-submenu>
        <el-submenu index="3">
          <template slot="title">定位系统</template>
          <el-menu-item index="locationConfigPage">定位配置</el-menu-item>
          <el-menu-item index="locationShowPage">定位展示</el-menu-item>
        </el-submenu>
        <el-submenu index="4">
          <template slot="title">公共工具</template>
          <el-menu-item index="statisticsInfo">统计信息</el-menu-item>
          <el-menu-item index="searchMessage">数据库记录查询</el-menu-item>
          <el-menu-item index="simulationShowPage" disabled>生成晶格文件</el-menu-item>
        </el-submenu>
        </el-menu>
        </el-header>
        <el-main>
        <router-view></router-view>
      </el-main>
      <el-footer>
        地铁人流监测系统
      </el-footer>
  </el-container>
</template>
<script>
    export default {
        name: "Home",
        data() {
          return {
            activeIndex: '1'
          };
        },
        methods: {

        }
    }
</script>

<style>
  .el-header, .el-footer {
    background-color: #B3C0D1;
    color: #333;
    text-align: center;
    line-height: 60px;
    padding: 0;
    position: absolute;
    left: 0;
    right: 0;
  }



  .el-header {
    top: 0;
  }
  .el-footer {
    bottom: 0;
  }

  .el-main {
    color: #333;
    text-align: center;
    position: absolute;
    left: 0;
    right: 0;
    top: 60px;
    bottom: 60px;
  }
  .el-container:nth-child(5) .el-aside,
  .el-container:nth-child(6) .el-aside {
    line-height: 260px;
  }

  .el-container:nth-child(7) .el-aside {
    line-height: 320px;
  }
</style>
