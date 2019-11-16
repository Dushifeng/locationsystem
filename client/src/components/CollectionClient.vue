<template>
    <div>

      <h4>
        {{notice}}
      </h4>
      <el-row>
        <el-form ref="form" v-model="info"  label-width="80px">
          <el-form-item label="本机Mac">
            <el-select v-model="info.selectedDev" placeholder="请选择" :disabled="start">
              <el-option
                v-for="dev in info.devmap"
                :key="dev.mac"
                :label="dev.mac+'-------'+dev.name"
                :value="dev.mac">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="晶格序号">
            <el-input v-model="info.gridMapIndex" @change="gridmapIndexChanged" :disabled="start"></el-input>
          </el-form-item>
          <el-form-item label="x坐标">
            <el-input v-model="info.gridX" :disabled="start"></el-input>
          </el-form-item>
          <el-form-item label="y坐标">
            <el-input v-model="info.gridY" :disabled="start"></el-input>
          </el-form-item>
          <el-form-item label="开始">
            <el-switch
              v-model="start"
              active-color="#13ce66"
              inactive-color="#ff4949"
              @change="switchChanged">
            </el-switch>
          </el-form-item>
        </el-form>
      </el-row>
      <el-row>
          <h1 align="center">
            {{seconds}}秒
          </h1>
      </el-row>
    </div>
</template>
<script>
    export default {
        name: "CollectionClient",
        data(){
          return {
            notice:"使用说明：1.输入的mac地址要求由只小写字母和数字组成。2.晶格编号和坐标填写一个就可以，系统优先使用晶格编号",
            start:false,
            seconds:0,
            timer:null,

            info:{
              mac:"",
              devmap:[],
              id:"",
              gridMapIndex:0,
              gridX:"",
              gridY:"",
            },
            selectedDev:'',
            canvasParams:{
              width:1800,
              height:300,
              canvas:''
            },
            gridMap:{
              left:0.0,
              right:0.0,
              top:0.0,
              buttom:0.0,
              k:1
            },
          };
      },
        mounted(){
            this.getDevMapList()
            this.login()
        },

        methods:{
            gridmapIndexChanged(val){
                var that = this
                this.$axios.get('collectionSys/getGridMapXY',{params:{
                        index:val
                    }}).then(successResult =>{
                    let data = successResult.data
                    if(data.status == 200){
                        that.info.gridX = data.refObject.x
                        that.info.gridY = data.refObject.y
                    }else {
                        that.$message.error(data.message)
                    }

                }).catch(error =>{
                    that.$message.error(error)
                })
            },
            login(){
                var that = this
                this.$axios.get("collectionSys/login").then(successResult => {
                    let data = successResult.data
                    if(data.status==200){
                        //有正在运行的任务 or 有记录
                        let obj = data.refObject
                        console.log(obj)
                        if(obj != undefined){
                            that.info.selectedDev = obj.mac
                            that.info.gridMapIndex = obj.gridmapIndex
                            that.info.gridX = obj.gridX
                            that.info.gridY = obj.gridY

                            if(obj.doing == true){
                                let date = new Date()
                                let time = date.getTime()/1000 - obj.startTime
                                that.start = true
                                that.seconds = time.toFixed(0)
                                that.timer = setInterval(()=>{
                                    that.seconds++
                                },1000)
                            }
                        }
                    }
                }).catch(error=>{
                    that.$message.error("网络连接错误"+error)
                })
            },
            getDevMapList(){
                var that = this
                this.$axios.get('collectionSys/getDevList').then(successResult =>{
                    if(successResult.data.status==200){
                        let devList = successResult.data.refObject;
                        for (let i = 0;i<devList.length;i++){
                            let mac = devList[i].mac
                            let name = devList[i].name
                            that.info.devmap.push({
                                mac:mac,
                                name:name
                            })
                        }
                    }else {
                        that.$alert("当前无采集任务，请联系管理员")
                    }
                }).catch(error => {

                    that.$alert("与服务器链接错误。请联系管理员  "+error)
                })
            },
            switchChanged(val){
                var that = this
                if (val){
                    //验证入参 mac 晶格id 坐标
                    let fd = new FormData()
                    fd.append('mac',that.info.selectedDev)
                    fd.append('x',that.info.gridX)
                    fd.append('y',that.info.gridY)
                    this.$axios.post('collectionSys/registerTask',fd).then(successResponse =>{
                        var result = successResponse.data
                        if(result.status == 200){
                            that.$message.success("提交成功,采集已开始")
                            //开启秒表
                            if (that.timer!=null){
                                that.stopTimer()
                            }
                            that.timer = setInterval(()=>{
                                that.seconds++
                            },1000)
                        }else{
                            that.start = false
                            that.$message.error('错误信息：'+result.message)
                        }
                    }).catch(error => {
                        //网络错误
                        that.start = false
                        that.$message.error('错误信息：'+error)
                    })
                }else {
                    //发送停止请求
                    let params = new FormData()
                    params.append("mac",that.info.selectedDev)
                    this.$axios.post('collectionSys/completeSingle',params).then(successResponse =>{
                        var result = successResponse.data
                        if(result.status == 200){
                            //返回采集结果
                            let ref = result.refObject

                            let str = '共采集到2.4GHz信号数'+ref.infoNum2G+'条，5GHz信号数'+ref.infoNum5G+'条'
                            that.$message.success(str)
                            that.stopTimer()
                        }else{
                            that.start = true
                            that.$message.error(result.message)
                        }
                    }).catch(error => {
                        that.start = true
                        that.$message.error('网络错误，错误信息：'+error)
                    })
                    //停止并重置计时器
                }
            },
            stopTimer(){
                clearInterval(this.timer);
                this.timer = null;
                this.seconds = 0;
            }
        },

    }
</script>

<style scoped>

</style>
