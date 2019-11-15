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
                tid:null
            }
        },
        methods:{
            goBack(){
                console.log("back...")
            },
            startStatistics() {
                var that = this
                this.start = true
                var s = parseInt(this.sec)
                this.$axios.get('startStatistics',{t:s}).catch(e=>{
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
                    that.$axios.get('getStatisticsInfo').then(successReslut =>{
                        let data = successReslut.data
                        that.displayInfo.splice(0,that.displayInfo.length)
                        var setf = new Set()
                        for(var key in data){
                            console.log(key)
                            var info = data[key]
                            var dataNum = info['dataNum']
                            var devMap = info['devMap']
                            var frequencyNum = info['frequencyNum']
                            var rssNum = info['rssNum']
                            var uniqueDevMacNum = info['uniqueDevMacNum']
                            for (var key1 in frequencyNum){
                                key1 = parseInt(key1)
                                setf.add(key1)
                            }

                            that.displayInfo.push({
                                apMac:key,
                                dataNum:dataNum,
                                rssNum:rssNum,
                                uniqueDevMacNum:uniqueDevMacNum,
                                rssMap:frequencyNum
                            })
                        }
                        that.frequency.slice(0,that.frequency.length)
                        for (var item in setf){
                            that.frequency.push({
                                label:item+"",
                                prop:item+"f"
                            })
                        }

                        // for (var info in that.displayInfo){
                        //     for (var item in setf){
                        //         let m = info['rssMap']
                        //         if (m[item]==undefined||m[item]==null){
                        //             info[item+"f"]=0
                        //         }
                        //         else {
                        //             info[item+"f"]=m[item]
                        //         }
                        //     }
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
            if(this.tid) { //如果定时器还在运行 或者直接关闭，不用判断
                clearInterval(this.tid); //关闭
            }
        }
    }
</script>

<style scoped>

</style>
