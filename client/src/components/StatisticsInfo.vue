<template>
<div>
  <el-page-header @back="goBack" content="统计详情" >
  </el-page-header>
  <el-form :inline="true">
    <el-form-item>
      <el-input v-model="sec" placeholder="" :disabled="start"></el-input>
    </el-form-item>
    <el-form-item>
      <el-button>开始</el-button>
      <el-button>结束</el-button>
    </el-form-item>
  </el-form>
  <div>
    <el-table
      :data="displayInfo"
      style="width: 100%">
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
      <el-table-column label="2.4G">
        <template v-for="info in frequency2">
          <el-table-column
            :prop="displayInfo.rssMap[info]",
            :label="info",
            width="80" >
          </el-table-column>
        </template>
      </el-table-column>
      <el-table-column label="5.8G">
        <template v-for="info in frequency5">
          <el-table-column
            :prop="displayInfo.rssMap[info]",
            :label="info",
            width="80" >
          </el-table-column>
        </template>
      </el-table-column>
    </el-table>
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
                frequency2:[],
                frequency5:[],
                sec:1,
                start:false,
                tid:null
            }
        },
        methods:{
            goBack(){
                console.log("back...")
            },
            start() {
                var that = this
                var s = parseInt(this.sec)
                this.$axios.get('startStatistics',{t:s},function () {
                    that.getInfo()
                }).catch(e=>{
                    alert(e)
                })
            },
            /**
             * "dataNum": 1,
             "devMap": {
            "a057e35e8643": 3,
            "2c5d34d1340f": 4,
            "9c2ea1dcd02d": 2,
            "843a4be5b600": 1,
            "386ea2661226": 1,
            "3408bc06a5f2": 2,
            "40bc60b8cb47": 2
          },
                   "frequencyNum": {
            42: 13,
            155: 2
          },
             "rssNum": 15,
             "uniqueDevMacNum": 7

             */
            getInfo(){
                var that = this
                if (that.tid!=null){
                    clearInterval(that.tid)
                }
                that.tid = setInterval(()=>{
                    //执行查询
                    that.$axios.get('getStatisticsInfo').then(successReslut =>{
                        let data = successReslut.data
                        for(var apMac in data){
                            var info = data[apMac]
                            var dataNum = info['dataNum']
                            var devMap = info['devMap']
                            var frequencyNum = info['frequencyNum']
                            var rssNum = info['rssNum']
                            var uniqueDevMacNum = info['uniqueDevMacNum']

                            for (var f in frequencyNum){
                                f = parseInt(f)
                                if (f<42){
                                    if(that.frequency2.indexOf(f)==-1){
                                        that.frequency2.push(f)
                                    }
                                }else{
                                    if(that.frequency5.indexOf(f)==-1){
                                        that.frequency5.push(f)
                                    }
                                }
                            }
                            that.displayInfo.push({
                                apMac:apMac,
                                dataNum:dataNum,
                                rssNum:rssNum,
                                uniqueDevMacNum:uniqueDevMacNum,
                                rssMap:frequencyNum
                            })
                        }
                    }).catch(error=>{
                        that.$message.error(error)
                    })
                },1000)
            },


            stop(){

            }


        }
    }
</script>

<style scoped>

</style>
