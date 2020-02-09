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
  <div :class="{hide:getInfos}">
    <el-table
      :data="displayInfo"
      style="width: 100%"
      :summary-method="getSummaries"
      show-summary
      max-height="400px">
      <el-table-column
        prop="apMac"
        label="AP MAC"
        width="150">
      </el-table-column>
      <el-table-column label="基础信息">
        <el-table-column
          prop="dataNum"
          label="UDP接收数据包数"
          width="180">
        </el-table-column>
        <el-table-column
          prop="rssNum"
          label="rss数目"
          width="100">
        </el-table-column>
        <el-table-column
          prop="uniqueDevMacNum"
          label="探测到的设备数"
          width="150">
        </el-table-column>
      </el-table-column>

      <el-table-column label="不同信道上的rss数目">
        <template v-for="item in frequency">
          <el-table-column
            :label="item.label"
            :prop="item.prop"
            width="80">
          </el-table-column>
        </template>
      </el-table-column>
    </el-table>
  </div >
  <div>
    <el-button @click="confirmWatch">添加监控</el-button>
    <div>
      <template v-for="mac in watchMac">
      <el-row :gutter="10">
          <el-col :span="12">
            <el-card shadow="always">
<!--设备mac，记录数（table），被探测到ap的平均值（table）-->
              <div>
                <h3>{{mac}}</h3>
              </div>
              <div>
                  <template v-if="watchMacTableData[mac] != undefined">
                    <h3>均值及扫描次数信息</h3>
                    <el-table
                      :data="watchMacTableData[mac]"
                      show-summary
                      :summary-method="getSummariesByMac"
                      style="width: 100%">
                      <el-table-column
                        prop="apMac"
                        label="AP-Mac"
                        width="120">
                      </el-table-column>
                      <el-table-column
                        prop="rss"
                        label="Rss值"
                        width="180">
                      </el-table-column>
                      <el-table-column
                        prop="num"
                        label="被扫描到的次数"
                        width="180">
                      </el-table-column>
                    </el-table>
                  </template>
                <div>
                  <el-button @click="removeWatchMac(mac)">删除</el-button>
                </div>
              </div>
            </el-card>
          </el-col>
      </el-row>
      </template>
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
                watchMac:[
                ],
                watchMacTableData:{},
                socket:null,
                getInfos:false,
                allUniqueDevMacNum:0
            }
        },
        mounted(){
            this.init()
        },
        methods:{
            init:function(){
                if(typeof(WebSocket) === "undefined"){
                    alert("您的浏览器不支持socket")
                }else{
                    var path = "ws://"+window.location.host.split(":")[0]+":8080/ws/statistics"

                    // 实例化socket
                    this.socket = new WebSocket(path)
                    // 监听socket连接
                    this.socket.onopen = this.open
                    // 监听socket错误信息
                    this.socket.onerror = this.error
                    // 监听socket消息
                    this.socket.onmessage = this.getMessage
                }

            },
            goBack(){
                console.log("back...")
            },
            removeWatchMac(mac){
                var index = this.watchMac.indexOf(mac)
                this.watchMac.splice(index,1)
                this.send("removeWatchMac#"+mac)
            },
            confirmWatch(){
                var that = this
                this.$prompt('请输入设备Mac', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                }).then(({ value }) => {
                    value = that.transMac(value)
                    if (value == ''){
                        that.$message.warning("输入的mac有误")
                        return
                    }
                    if(that.watchMac.indexOf(value)!=-1){
                        that.$message.warning("输入的mac已经存在")
                        return;
                    }
                    that.watchMac.push(value)

                    that.send("addSingleDevWatch#"+value)
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

                this.send("startStatistics#"+s)
            },
            stopStatistics(){
                var that = this
                this.start = false
                this.send("stopStatistics")
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

            getSummaries(param) {
                const { columns, data } = param;
                const sums = [];
                columns.forEach((column, index) => {
                    if (index === 0) {
                        sums[index] = '总数';
                        return;
                    }
                    if (index === 3){
                        sums[index] = this.allUniqueDevMacNum;
                        return;
                    }
                    const values = data.map(item => Number(item[column.property]));
                    if (!values.every(value => isNaN(value))) {
                        sums[index] = values.reduce((prev, curr) => {
                            const value = Number(curr);
                            if (!isNaN(value)) {
                                return prev + curr;
                            } else {
                                return prev;
                            }
                        }, 0);
                    } else {
                        sums[index] = 'N/A';
                    }
                });

                return sums;
            },


            getSummariesByMac(param){
                const { columns, data } = param;
                const sums = [];
                columns.forEach((column, index) => {
                    if (index === 0) {
                        sums[index] = '总数';
                        return;
                    }
                    if (index === 1){
                        sums[index] = '';
                        return;
                    }
                    const values = data.map(item => Number(item[column.property]));
                    if (!values.every(value => isNaN(value))) {
                        sums[index] = values.reduce((prev, curr) => {
                            const value = Number(curr);
                            if (!isNaN(value)) {
                                return prev + curr;
                            } else {
                                return prev;
                            }
                        }, 0);
                    } else {
                        sums[index] = 'N/A';
                    }
                });

                return sums;
            },
            open: function () {
                console.log("socket连接成功")
            },
            error: function () {
                console.log("连接错误")
            },
            getMessage: function (msg) {
                msg = JSON.parse(msg.data)
                let data = msg['all']
                if(data != undefined&&data != null){
                    this.allUniqueDevMacNum = msg['devMacNum']
                    var setf = new Set()

                    for(let i=0,len = this.frequency.length;i<len;i++){
                        setf.add(parseInt(this.frequency[i].label))
                    }

                    for(var key in data){
                        var frequencyNum = data[key]['frequencyNum']
                        for (var key1 in frequencyNum){
                            key1 = parseInt(key1)
                            setf.add(key1)
                        }
                    }
                    setf = Array.from(setf)
                    setf.sort((a,b)=>{ return a-b})
                    var hideInfo = []

                    for(var key in data){
                        var info = data[key]
                        var dataNum = info['dataNum']
                        var devMap = info['devMap']
                        var frequencyNum = info['frequencyNum']
                        var rssNum = info['rssNum']
                        var uniqueDevMacNum = info['uniqueDevMacNum']
                        var it = {
                            apMac:key,
                            dataNum:dataNum,
                            rssNum:rssNum,
                            uniqueDevMacNum:uniqueDevMacNum,
                            rssMap:frequencyNum
                        }
                        for(let i = 0,len = setf.length;i<len;i++){
                            let ft = setf[i]
                            if (frequencyNum[ft]==undefined||frequencyNum[ft]==null){
                                it[ft] = 0
                            }else {
                                it[ft] = frequencyNum[ft]
                            }
                        }

                        hideInfo.push(it)
                    }
                    this.getInfos = true

                    this.frequency.splice(0,this.frequency.length)
                    for (let i=0,len=setf.length;i<len;i++){
                        this.frequency.push({
                            label:setf[i]+"",
                            prop:setf[i]+""
                        })
                    }

                    this.displayInfo = hideInfo;
                    this.getInfos = false
                }

                for (let i=0,len = this.watchMac.length;i<len;i++){
                    var mac = this.watchMac[i]
                    var info = msg[mac]
                    if(info == undefined||info == null){
                        continue
                    }
                    let rssMap = info['rssAvgMap']
                    let ms = info['messageNumMap']
                    this.watchMacTableData[mac] = []
                    for(var apMac in rssMap){
                        let rss = rssMap[apMac]
                        var num = ms[apMac]
                        this.watchMacTableData[mac].push({
                            apMac:apMac,
                            rss:rss.rssi[0].toFixed(2),
                            num:num
                        })
                    }
                }

            },

            send: function (msg) {
                this.socket.send(msg)
            },
            close: function () {
                console.log("socket已经关闭")
            }

        },
        beforeDestroy() {
            this.stopStatistics()
            this.socket.onclose = this.close
        }
    }
</script>

<style scoped>
  .hide{
    opacity:0
  }

</style>
