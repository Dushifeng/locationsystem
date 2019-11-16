<template>
<div>
  <el-page-header @back="goBack" content="统计详情" >
  </el-page-header>
  <el-form :inline="true">
    <el-form-item>
      <el-input v-model="sec" placeholder="" :disabled="start"></el-input>
    </el-form-item>
    <el-form-item>
      <el-button @click="startStatistics">开始</el-button>
      <el-button @click="stopStatistics">结束</el-button>
    </el-form-item>
  </el-form>
  <div>
    <el-table
      :data="displayInfo"
      style="width: 100%"
      max-height="400px">
      <el-table-column
        prop="apMac"
        label="AP Mac"
        width="150">
      </el-table-column>
      <el-table-column label="基础信息">
        <el-table-column
          prop="dataNum"
          label="数据包数"
          width="80">
        </el-table-column>
        <el-table-column
          prop="rssNum"
          label="有效信息数"
          width="80">
        </el-table-column>
      </el-table-column>

      <el-table-column label="频段数目">
        <template v-for="item in frequency">
          <el-table-column
            :label="item.label"
            :prop="item.prop"
            width="80">
          </el-table-column>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <div>
    <el-button @click="confirmWatch">添加监控</el-button>
    <div>
      <el-row :gutter="10">
        <template v-for="mac in watchMac">
          <el-col :span="8">
            <el-card shadow="always">
              {{mac}}
            </el-card>
          </el-col>
        </template>
      </el-row>

    </div>
  </div>
</div>
</template>



<script>
    const removeDuplicateItems = arr => [...new Set(arr)];
    export default {
        name: "StatisticsInfo",
        data(){
            return{
                displayInfo:[],
                frequency:[],
                sec:1,
                start:false,
                tid:null,
                watchMac:[],

            }
        },
        methods:{
            goBack(){
                console.log("back...")
            },

            confirmWatch(){
                this.$prompt('请输入设备Mac', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                }).then(({ value }) => {
                    this.watchMac.push(value)
                    //添加card
                }).catch((e) => {
                    this.$message({
                        type: 'info',
                        message: '取消输入'+e
                    });
                });
            },
            startStatistics() {
                var that = this
                this.start = true
                var s = parseInt(this.sec)
                this.$axios.get('startStatistics',{
                    params: {
                        t:s
                    }
                }).catch(e=>{
                    alert(e)
                })
                console.log("that.getInfo()")
                that.getInfo(s)
            },
            getInfo(n){
                var that = this
                if (that.tid!=null){
                    clearInterval(that.tid)
                }
                that.tid = setInterval(()=>{
                    //执行查询
                    console.log(that.watchMac)
                    that.$axios.post('getStatisticsInfo',that.watchMac).then(successReslut =>{
                        let data = successReslut.data.all
                        that.displayInfo.splice(0,that.displayInfo.length)
                        var setf = new Set()
                        console.log(data)
                        // for(var key in data){
                        //     for (var key1 in frequencyNum){
                        //         key1 = parseInt(key1)
                        //         setf.add(key1)
                        //     }
                        // }
                        // for(var key in data){
                        //     var info = data[key]
                        //     var dataNum = info['dataNum']
                        //     var devMap = info['devMap']
                        //     var frequencyNum = info['frequencyNum']
                        //     var rssNum = info['rssNum']
                        //     var uniqueDevMacNum = info['uniqueDevMacNum']
                        //     var it = {
                        //         apMac:key,
                        //         dataNum:dataNum,
                        //         rssNum:rssNum,
                        //         uniqueDevMacNum:uniqueDevMacNum,
                        //         rssMap:frequencyNum
                        //     }
                        //     for(var i in setf){
                        //         if (frequencyNum[i]==undefined||frequencyNum[i]==null){
                        //             it[i] = 0
                        //         }else {
                        //             it[i] = frequencyNum[i]
                        //         }
                        //     }
                        //     console.log(it)
                        //     that.displayInfo.push(it)
                        // }
                        // that.frequency.slice(0,that.frequency.length)
                        // for (var item in setf){
                        //     that.frequency.push({
                        //         label:item+"",
                        //         prop:item+""
                        //     })
                        // }

                    }).catch(error=>{
                        that.$message.error(error)
                    })
                },n*1000)
            },


            stopStatistics(){
                var that = this
                if(this.tid) { //如果定时器还在运行 或者直接关闭，不用判断
                    clearInterval(this.tid); //关闭
                }
                that.$axios.get('stopStatistics').catch(e=>{
                    alert(e)
                })
                this.start = false

            }


        },
        beforeDestroy() {
            this.stopStatistics()
        }
    }
</script>

<style scoped>

</style>
