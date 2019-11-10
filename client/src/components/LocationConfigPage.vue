<template>
  <div>
    <el-row>
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>指纹库配置</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="fingerprintSubmit">提交</el-button>
          </div>
          <div>
            <el-form ref="form" label-width="150px">
              <el-form-item label="指纹库文件">
                <el-upload
                  ref="upload"
                  action="http://localhost:8081/upLoad"
                  :on-remove="handleRemove"
                  :limit="2"
                  multiple
                  :auto-upload="false"
                  :on-change="onchange">
                  <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
                </el-upload>
                <el-input v-model="sysfingerprintName" disabled></el-input>
              </el-form-item>

              <el-form-item label="指纹库名称">
                <el-input v-model="fingerprintName"></el-input>
              </el-form-item>
            </el-form>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>晶格配置</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="GridmapSubmit">提交</el-button>
          </div>
          <div>
            <el-form ref="form" label-width="150px">
              <el-form-item label="晶格文件">
                <el-upload
                  ref="gridUpload"
                  action=""
                  :limit="1"
                  :auto-upload="false"
                >
                  <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
                </el-upload>
                <el-input v-model="sysgridmapName" disabled></el-input>
              </el-form-item>

              <el-form-item label="晶格名称">
                <el-input v-model="gridmapName"></el-input>
              </el-form-item>
            </el-form>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>UDP服务器</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="UDPSubmit">提交</el-button>
          </div>
          <div class="text item">
            <el-form ref="form" label-width="150px">
              <el-form-item label="端口号">
                <el-input v-model="udpport"></el-input>
              </el-form-item>
              <el-form-item label="UDP状态">
                <el-switch
                  v-model="udpswitch"
                  active-color="#13ce66"
                  inactive-color="#ff4949">
                </el-switch>
              </el-form-item>
            </el-form>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="10">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>消息过滤配置</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="MessageConfigSubmit">提交</el-button>
          </div>
          <div>
            <el-row>
              <el-col :span="8">
                <el-table
                  height="250"
                  border
                  style="width: 100%;"
                  :data="macAllow">
                  <el-table-column
                    prop="mac"
                    label="白名单Mac列表">
                  </el-table-column>
                  <el-table-column
                    align="right">
                    <template slot="header" slot-scope="scope">
                      <el-button type="info" round>添加</el-button>
                    </template>
                    <template slot-scope="scope">
                      <el-button
                        size="mini"
                        type="danger"
                        @click="handleDelete(scope.$index, scope.row)">Delete</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </el-col>
              <el-col :span="8">
                <el-table
                  height="250"
                  border
                  style="width: 100%;"
                  :data="macRefuse">
                  <el-table-column
                    prop="mac"
                    label="黑名单Mac列表">
                  </el-table-column>
                  <el-table-column
                    align="right">
                    <template slot="header" slot-scope="scope">
                      <el-button type="info" round>添加</el-button>
                    </template>
                    <template slot-scope="scope">
                      <el-button
                        size="mini"
                        type="danger"
                        @click="handleDelete(scope.$index, scope.row)">Delete</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </el-col>
              <el-col :span="8">
                <el-table
                  height="250"
                  border
                  style="width: 100%;"
                  :data="frequencyRefuse">
                  <el-table-column
                    prop="mac"
                    label="频段拒绝列表">
                  </el-table-column>
                  <el-table-column
                    align="right">
                    <template slot="header" slot-scope="scope">
                      <el-button type="info" round>添加</el-button>
                    </template>
                    <template slot-scope="scope">
                      <el-button
                        size="mini"
                        type="danger"
                        @click="handleDelete(scope.$index, scope.row)">Delete</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </el-col>
            </el-row>
            <el-upload
              ref="messsageFileUpload"
              :show-file-list="false"
              action="http://localhost:8081/api/locationSys/changeMessageConfigByFile"
              :limit="1"
              :on-success="messageFileSuccess">
              <el-button type="primary">从文件加载</el-button>
            </el-upload>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>AP列表配置</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="APConfigSubmit">提交</el-button>
          </div>
          <div>
            <el-table
              height="250"
              border
              style="width: 100%;"
              :data="apList">
              <el-table-column
                prop="index"
                label="AP索引值">
              </el-table-column>
              <el-table-column
                prop="mac"
                label="MAC地址">
              </el-table-column>
              <el-table-column
                prop="x"
                label="x坐标值">
              </el-table-column>
              <el-table-column
                prop="y"
                label="y坐标值">
              </el-table-column>

              <el-table-column
                align="right">
                <template slot="header" slot-scope="scope">
                  <el-button type="info" round @click="APdialog = true">添加</el-button>
                </template>
                <template slot-scope="scope">
                  <el-button
                    size="mini"
                    type="danger"
                    @click="handleDelete(scope.$index, scope.row)">Delete</el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-upload
            ref="apFileUpload"
            :show-file-list="false"
            action="http://localhost:8081/api/locationSys/changeAPConfigByFile"
            :limit="1"
            :on-success="apFileSuccess">
              <el-button type="primary">从文件加载</el-button>
            </el-upload>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>定位参数配置</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="LocationConfigSubmit">提交</el-button>
          </div>
          <div>
            <el-form ref="form"  label-width="150px" :model="locaionConfig">
              <el-form-item label="K">
                <el-input v-model="locaionConfig.k"></el-input>
              </el-form-item>
              <el-form-item label="最小非Null的Rss个数">
                <el-input v-model="locaionConfig.minNotNullRssNum" ></el-input>
              </el-form-item>
              <el-form-item label="滑动窗口长度">
                <el-input v-model="locaionConfig.slidingWindow"></el-input>
              </el-form-item>
              <el-form-item label="滑动窗口步长">
                <el-input v-model="locaionConfig.slidingStep"></el-input>
              </el-form-item>
              <el-form-item label="定位结果保留时长">
                <el-input v-model="locaionConfig.resultExpireQueueTime"></el-input>
              </el-form-item>
              <el-form-item label="定位算法">
                <el-select v-model="locaionConfig.locationAlgorithmName" placeholder="请选择" >
                  <el-option
                    v-for="item in locaionConfig.nameList"
                    :key="item"
                    :label="item"
                    :value="item">
                  </el-option>
                </el-select>
            </el-form-item>
            </el-form>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-dialog title="AP信息" :visible.sync="APdialog" :close-on-click-modal="false">
      <el-form :model="apItem">
        <el-form-item label="探针MAC" label-width="120px">
          <el-input v-model="apItem.mac" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="x坐标"  label-width="120px">
          <el-input v-model="apItem.x" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="y坐标"  label-width="120px">
          <el-input v-model="apItem.y" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="APdialog = false">取 消</el-button>
        <el-button type="primary" @click="addAP">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
    export default {
        name: "LocationConfigPage",
        data(){
            return{
                udpport:"",
                udpswitch:false,
                sysgridmapName:"",
                gridmapName:"",
                frequencyRefuse:[],
                locaionConfig:{
                    minNotNullRssNum:0,
                    k:0,
                    locationAlgorithmName:"",
                    resultExpireQueueTime:0,
                    slidingWindow:0,
                    slidingStep:0,
                    locationAlgorithmName:'123',
                    nameList:[
                        '123',
                        '234'
                    ]
                },
                macAllow:[],
                macRefuse:[],
                apItem:{mac:"",x:0,y:0},
                APdialog:false,
                apList:[],
                fingerprintName:"",
                sysfingerprintName:"",
                fileList:[],
                fingerprintFiles:new FormData(),
                value: '',
                baseUrl:'locationSys/'
            };
        },
        mounted(){
          this.getLocationConfig()
          this.getMessageConfig()
          this.getAPConfig()
          this.getFingerprintConfig()
          this.getGridMapConfig()
          this.getUDPState()
        },
        methods:{

            UDPSubmit(){
                var that = this
                let config = {port:this.udpport,open:this.udpswitch}
                var fd = new FormData()
                fd.append("port",this.udpport)
                fd.append("open",this.udpswitch)
                this.$axios.post(that.baseUrl+"submitUDPState",fd).then(function (result) {

                }).catch(error =>{
                    that.$message.error(error)
                })
            },

            getUDPState(){
                var that = this
                this.$axios.get(that.baseUrl+"getUDPState").then(function (ans) {
                    that.udpport = ans.data.port
                    that.udpswitch = ans.data.switch
                })
            },

            GridmapSubmit(){
                var that = this;
                var fd = new FormData()
                fd.append('file',this.$refs.gridUpload.$children[0].fileList[0].raw,this.$refs.gridUpload.$children[0].fileList[0].name)
                fd.append('name',that.gridmapName)
                console.log(this.$refs.gridUpload.$children[0].fileList[0])
                let config = {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                };
                this.$axios.post(that.baseUrl+"upLoadGridMap",fd,config).then(function (result) {
                    console.log(result)
                    that.sysgridmapName = result.data
                })
            },

            LocationConfigSubmit(){
               var that = this;
               this.$axios.post(that.baseUrl+'submitLocationConfig',that.locaionConfig).then(function (result) {
                   console.log(result)
                   that.getLocationConfig()
               })
            },
            getLocationConfig(){
                var that = this
                this.$axios.get(that.baseUrl+'getLocationConfig').then(function (result) {
                    console.log(result.data)
                    let config = result.data.config
                    that.locaionConfig.nameList = result.data.nameList
                    that.locaionConfig.locationAlgorithmName = result.data.nameList[0]
                    that.locaionConfig.minNotNullRssNum = config.minNotNullRssNum
                    that.locaionConfig.resultExpireQueueTime = config.resultExpireQueueTime
                    that.locaionConfig.slidingStep = config.slidingStep
                    that.locaionConfig.slidingWindow = config.slidingWindow
                    that.locaionConfig.k = config.k
                })
            },
            messageFileSuccess(response, file, fileList){
                this.macAllow=[];
                this.macRefuse=[];
                var a = response.macAllow;
                var b = response.macRefuse;
                for (var i=0;i<a.length;i++){
                    this.macAllow.push({mac:a[i]})
                }
                for (var i=0;i<b.length;i++){
                    this.macRefuse.push({mac:b[i]})
                }
            },
            MessageConfigSubmit(){
                var that = this
                let params = {macAllow:this.macAllow,macRefuse:this.macRefuse}
                this.$axios.post(that.baseUrl+'changeMessageConfig',params).then(function (result) {
                    console.log(result.data)
                    that.getMessageConfig()
                })
            },
            getMessageConfig(){
                var that = this;
                this.$axios.get(that.baseUrl+"getMessageConfig").then(function (result) {
                    that.macAllow=[];
                    that.macRefuse=[];
                    var a = result.data.macAllow;
                    var b = result.data.macRefuse;
                    for (var i=0;i<a.length;i++){
                        that.macAllow.push({mac:JSON.parse(a[i]).mac})
                    }
                    console.log(b)
                    for (var i=0;i<b.length;i++){
                        that.macRefuse.push({mac:JSON.parse(b[i]).mac})
                    }
                })
            },
            apFileSuccess(response, file, fileList){
                console.log(response)
                this.apList = []
                for (var i=0;i<response.aPSize;i++){
                    this.apList.push(response.apList[i])
                }
            },
            APConfigSubmit(){
              var that = this
              this.$axios.post(that.baseUrl+'changeAPConfig',this.apList).then(function (result) {
                  console.log(result.data)
                  that.getAPConfig()
              })
            },
            getAPConfig(){
              var that = this;
              this.$axios.get(that.baseUrl+"getAPConfig").then(function (result) {
                  that.apList = []
                  var data = result.data
                  for (var i=0;i<data.aPSize;i++){
                      that.apList.push(data.apList[i])
                  }
              })
            },
            addAP(){
                this.apList.push({index:this.apList.length+1,mac:this.apItem.mac,x:parseFloat(this.apItem.x),y:parseFloat(this.apItem.y)})
                this.apItem = {mac:"",x:0,y:0}
                this.APdialog = false
            },

            fingerprintSubmit(){
              var that = this;
              that.fingerprintFiles.append("name",that.fingerprintName)
              let config = {
                  headers: {
                      'Content-Type': 'multipart/form-data'
                  }
              };
              this.$axios.post(that.baseUrl+"upLoadFingerprintFile",this.fingerprintFiles,config).then(function (result) {
                  console.log(result)
                  that.sysfingerprintName = result.data
              })
            },
            getFingerprintConfig(){
                var that = this;
                this.$axios.get(that.baseUrl+'getFingerprintConfig').then(function (result) {
                    that.sysfingerprintName = result.data
                })
            },
            getGridMapConfig(){
                var that = this;
                this.$axios.get(that.baseUrl+'getGridMapConfig').then(function (result) {
                    that.sysgridmapName = result.data
                })
            },
            onchange(file,fileList) {
               console.log(file)
               console.log(fileList)
              if (file.name.search("avg")!=-1){
                  this.fingerprintFiles.append("avg",file.raw,file.name)
              }else if(file.name.search("std")!=-1){
                  this.fingerprintFiles.append("std",file.raw,file.name)
              }else{
                  console.log("错误文件")
                  this.$alert("请添加文件名中包含avg或者std的文件")
                  var index = fileList.indexOf(file)
                  fileList.splice(index,1)
              }
            },
            handleRemove(file, fileList) {
                if (file.name.search("avg")!=-1){
                    this.fingerprintFiles.delete('avg')
                }else if(file.name.search("std")!=-1){
                    this.fingerprintFiles.delete('std')
                }
            },

        }
    }
</script>

<style scoped>
  .el-form{
    height: 25vh;
  }
</style>

<style>
  .el-card__header {
    padding: 5px 10px;
    background: #D9D9D9;
  }
  .el-card__body {
    height: 80%;
    overflow-y: auto;
  }
  .el-tag {
    margin-right: 10px;
  }
  .el-card {
    background: #F2F2F2;
    margin: 15px 10px 2px 2px;
    height: 37vh;
  }
  .subBtn{
    /*position: absolute;*/
    /*left: 0;*/
    /*right: 0;*/
    /*bottom: 0;*/
  }
</style>
