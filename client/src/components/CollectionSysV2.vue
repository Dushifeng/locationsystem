<template>
  <div>
      <el-row >
        <el-col :span="8">
          <div class="taskForm">
            <h2>创建任务</h2>
            <el-form ref="form" :model="form" label-width="80px" class="myForm">
              <el-form-item label="晶格文件">
                <el-input v-model="form.gridmapName" disabled>
                  <template slot="append">
                    <el-upload
                      ref="gridmap"
                      action=""
                      :auto-upload="false"
                      :on-change="onGridMapchange"
                      :show-file-list="false">
                      <el-button slot="trigger" size="small" type="primary" :disabled="form.doing">选取文件</el-button>
                    </el-upload>
                  </template>
                </el-input>

              </el-form-item>
              <el-form-item label="探针列表">
                <el-input v-model="form.apName" disabled>
                  <template slot="append">
                    <el-upload
                      ref="aplist"
                      action=""
                      :auto-upload="false"
                      :on-change="onAPchange"
                      :show-file-list="false">
                      <el-button slot="trigger" size="small" type="primary" :disabled="form.doing">选取文件</el-button>
                    </el-upload>
                  </template>
                </el-input>

              </el-form-item>
              <el-form-item label="设备列表">
                <el-row>
                  <el-col :span="10">
                    <el-input placeholder="请输入内容" v-model="form.dev.mac">
                      <template slot="prepend">mac地址</template>
                    </el-input>
                  </el-col>
                  <el-col :span="10">
                    <el-input placeholder="请输入内容" v-model="form.dev.name">
                      <template slot="prepend">设备名</template>
                    </el-input>
                  </el-col>
                  <el-col :span="4">
                    <el-button @click="addDev" :disabled="form.doing">添加</el-button>
                  </el-col>
                </el-row>
                <el-row>
                  <el-table
                    :data="form.tableData"
                    style="width: 100%"
                    max-height="250">
                    <el-table-column
                      prop="mac"
                      label="MAC地址"
                      width="200">
                    </el-table-column>
                    <el-table-column
                      prop="name"
                      label="别称"
                      width="200">
                    </el-table-column>
                    <el-table-column
                      fixed="right"
                      label="操作"
                      width="120">
                      <template slot-scope="scope">
                        <el-button
                          @click.native.prevent="deleteRow(scope.$index, form.tableData)"
                          type="text"
                          size="small">
                          移除
                        </el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                </el-row>
              </el-form-item>
              <el-form-item label="内晶格数" >
                <el-input v-model="form.innerNum" placeholder="请输入内容" :disabled="form.doing"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="onSubmit" :disabled="form.doing">立即创建</el-button>
                <el-button type="success" @click="onFinish" :disabled="!form.doing">停止任务</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-col>
        <el-col :span="16">
          <div class="taskForm">
            <h2>采集详情</h2>
              <el-table
                :data="resultData"
                border
                :summary-method="getSummaries"
                show-summary
                style="width: 100%"
                max-height="500px">
                <el-table-column
                  type="index"
                  :index="indexMethod">
                </el-table-column>
                <el-table-column
                  prop="name"
                  label="设备名"
                  sortable
                  width="180">
                </el-table-column>
                <el-table-column
                  prop="mac"
                  sortable
                  label="Mac">
                </el-table-column>
                <el-table-column
                  prop="24G"
                  sortable
                  label="2.4G">
                </el-table-column>
                <el-table-column
                  prop="58G"
                  sortable
                  label="5.8G">
                </el-table-column>
                <el-table-column
                  prop="gridMapIndex"
                  sortable
                  label="采集晶格">
                </el-table-column>
                <el-table-column
                  prop="durSec"
                  sortable
                  label="持续秒数">
                </el-table-column>
                <el-table-column
                  prop="status"
                  sortable
                  label="状态">
                </el-table-column>
                <el-table-column
                  prop="startTime"
                  sortable
                  label="开始时间">
                </el-table-column>
                <el-table-column
                  prop="endTime"
                  sortable
                  label="结束时间">
                </el-table-column>
                <el-table-column
                  fixed="right"
                  label="操作"
                  width="100">
                  <template slot-scope="scope">
                    <el-button @click="lookInfo(scope.row)" type="text" size="small">详情</el-button>
                  </template>
                </el-table-column>
              </el-table>
          </div>
        </el-col>
      </el-row>
  </div>
</template>

<script>
    export default {
        name: "CollectionConfig",
        data() {
            return {
                baseUrl:"collectionSys/",
                resultData:[],
                form: {
                    gridmapName:'',
                    apName:'',
                    dev:{
                      mac:'',
                      name:''
                    },
                    tableData: [],
                    innerNum:0,
                    doing:false
                },
                timer:null
            }
        },
        mounted(){
            //获取当前任务
            this.recoverTask()
        },
        methods: {
            recoverTask(){
                var that = this
                this.$axios.get(this.baseUrl+"recoverTask").then(function (successResult) {
                    let data = successResult.data
                    if(data.status == 200){
                        //有未完成的任务
                        let devMap = data.refObject.devMap
                        let inZoomNum = data.refObject.config.inZoomNum
                        console.log(devMap)
                        for (var key in devMap){
                            that.form.tableData.push({
                                mac:key,
                                name:devMap[key].name
                            })
                        }
                        console.log(inZoomNum)
                        that.form.innerNum = inZoomNum

                        that.getTaskList()
                        that.form.doing = true
                    }

                }).catch(error => {
                    that.$message.error(error)
                })
            },
            onFinish(){
                //发送请求
                var that = this
                this.$axios({
                    url:this.baseUrl+'finishAllTask',
                    method:'get',
                    responseType:'blob'
                }).then(res => {           //定义文件名等相关信息
                    const blob = res.data
                    const reader = new FileReader()
                    reader.readAsDataURL(blob)
                    reader.onload = (e) => {
                        const a = document.createElement('a')
                        a.download = `采集结果.zip`
                        a.href = e.target.result
                        document.body.appendChild(a)
                        a.click()
                        document.body.removeChild(a)
                    }
                })
                this.stopGetTaskList()
                this.form.doing = false
            },
            indexMethod(index){
              return index+1;
            },
            lookInfo(row){
                this.$message.info(row)
            },
            transMac(mac){
                var ans = '';
                mac = mac.toLowerCase()
                for (var i=0;i<mac.length;i++){
                    if (mac[i]>='0'&&mac[i]<='9'){
                        ans+=mac[i];
                    }else if (mac[i]>='a'&&mac[i]<='z'){
                        ans+=mac[i];
                    }else if (mac[i]==':'||mac[i]=='-'){
                        continue;
                    }else {
                        return '';
                    }
                }
                if (ans.length != 12){
                    return '';
                }
                return ans
            },
            addDev(){
              let mac =   this.transMac(this.form.dev.mac);
              if(mac == ''){
                  this.$message.error("输入的mac地址有误")
                  return;
              }

              this.form.tableData.push({
                  mac:mac,
                  name:this.form.dev.name
              })

              this.form.dev.mac=''
              this.form.dev.name=''
            },
            deleteRow(index, rows) {
                rows.splice(index, 1);
            },
            onSubmit() {
                var that = this
                var data = new FormData()
                data.append('gridmapFile',this.$refs.gridmap.$children[0].fileList[0].raw,this.$refs.gridmap.$children[0].fileList[0].name)
                data.append('apListFile',this.$refs.aplist.$children[0].fileList[0].raw,this.$refs.aplist.$children[0].fileList[0].name)
                let devmap = []
                for(var i =0;i<this.form.tableData.length;i++){
                    devmap.push({mac:this.form.tableData[i].mac,name:this.form.tableData[i].name})
                }
                data.append('devMapList',JSON.stringify(devmap))
                data.append('innerNum',this.form.innerNum)
                let config = {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                };
                this.$axios.post("collectionSys/submitNewTask",data,config).then(successReslut => {
                    that.$alert(successReslut.data)
                    that.getTaskList()
                    that.form.doing = true
                }).catch(error => {
                    that.$alert(successReslut.data)
                })
            },
            onGridMapchange(file,fileList){
                while (fileList.length>1){
                    fileList.shift()
                }

                this.form.gridmapName = file.name
            },
            onAPchange(file,fileList){
                while (fileList.length>1){
                    fileList.shift()
                }
                this.form.apName = file.name
            },
            getTaskList(){
                var that = this

                if (that.timer!=null){
                    clearInterval(that.timer)
                }
                that.timer = setInterval(()=>{
                    //执行查询
                    that.$axios.get('collectionSys/getTaskList').then(successReslut =>{
                        let data = successReslut.data
                        var doingtask = data.doing
                        var donetask = data.done
                        var devMap = data.devMap
                        that.resultData=[]
                        for(var i=0;i<donetask.length;i++){
                            let t = donetask[i]
                            that.resultData.push({
                                'name':devMap[t.mac].name,
                                'mac':t.mac,
                                '24G':t.result.infoNum2G,
                                '58G':t.result.infoNum5G,
                                'startTime':t.startTime,
                                'endTime':t.endTime,
                                'durSec':t.endTime-t.startTime,
                                'status':'已完成',
                                'gridMapIndex':t.gridmapIndex
                            })
                        }
                    }).catch(error=>{
                        that.$message.error(error)
                    })
                },1000)
            },
            getSummaries(param){
                const{columns,data} = param
                const sums = []
                columns.forEach((column,index)=>{
                    if (index == 0){
                        sums[index] = '总数';
                        return;
                    }
                    if(column.property == '24G'||column.property == '58G'){
                        const values = data.map(item => Number(item[column.property]));
                        sums[index] = values.reduce((prev, curr) => {
                            const value = Number(curr);
                            if (!isNaN(value)) {
                                return prev + curr;
                            } else {
                                return prev;
                            }
                        }, 0);
                    }

                })
                return sums
            },
            stopGetTaskList(){
                if (this.timer!=null){
                    clearInterval(this.timer)
                }
                this.timer = null
            }

        }
    }
</script>

<style scoped>
  .taskForm{
    display: block;
    border: 1px solid #ebebeb;
    transition: .2s;
    margin-bottom:24px;
    padding: 24px;
    margin-right: 24px;
    min-height: 600px;
  }
  .myForm{
    display: block;
    margin-top: 0em;
  }
</style>
