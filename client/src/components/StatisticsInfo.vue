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
          width="100">
        </el-table-column>
      </el-table-column>

      <el-table-column label="信道号统计">
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
          <el-col :span="12">
            <el-card shadow="always">
<!--设备mac，记录数（table），被探测到ap的平均值（table）-->
              <div>
                <h3>{{mac}}</h3>
              </div>
              <div>
                <el-row :gutter="60">
                  <el-col :span="6">
                    <template v-if="watchMacTableData[mac] != undefined">
                      <h3>均值信息</h3>
                      <el-table
                        :data="watchMacTableData[mac].rssAvgMap"
                        style="width: 100%">
                        <el-table-column
                          prop="apMac"
                          label="AP-Mac"
                          width="120">
                        </el-table-column>
                        <el-table-column
                          prop="rss"
                          label="Rss值"
                          width="80">
                        </el-table-column>
                      </el-table>
                    </template>
                  </el-col>
                  <el-col :span="12" :offset="6">
                      <template v-if="watchMacTableData[mac] != undefined">
                        <h3>被嗅探到的记录 <span>{{watchMacTableData[mac].messages.length}} 条</span></h3>
                        <el-table
                          :data="watchMacTableData[mac].messages"
                          style="width: 100%;"
                          max-height="300"
                        >
                          <el-table-column
                            prop="apMac"
                            label="AP-Mac"
                            width="140">
                          </el-table-column>
                          <el-table-column
                            prop="frequency"
                            label="信道号"
                            width="80">
                          </el-table-column>
                          <el-table-column
                            prop="rssi"
                            label="rss值"
                            width="80">
                          </el-table-column>
                          <el-table-column
                            prop="timestamp"
                            label="探测时间"
                            width="150">
                          </el-table-column>
                        </el-table>
                      </template>
                  </el-col>
                </el-row>
                <div>
                  <el-button @click="removeWatchMac(mac)">删除</el-button>
                </div>
              </div>
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
                watchMac:[
                ],
                watchMacTableData:{},
                socket:null,

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
                    var path = "ws://"+window.location.host.split(":")[0]+":8081/ws/statistics"

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

            getInfo(n){
                var that = this
                if (that.tid!=null){
                    clearInterval(that.tid)
                }
                that.tid = setInterval(()=>{
                    //执行查询
                    that.$axios.post('getStatisticsInfo',that.watchMac).then(successReslut =>{
                        let data = successReslut.data['all']
                        that.displayInfo.splice(0,that.displayInfo.length)
                        var setf = new Set()
                        for(var key in data){
                            var frequencyNum = data[key]['frequencyNum']
                            for (var key1 in frequencyNum){
                                key1 = parseInt(key1)
                                setf.add(key1)
                            }
                        }
                        setf = Array.from(setf)
                        setf.sort((a,b)=>{ return a-b})
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
                            for(let i of setf.values()){
                                if (frequencyNum[i]==undefined||frequencyNum[i]==null){
                                    it[i] = 0
                                }else {
                                    it[i] = frequencyNum[i]
                                }
                            }

                            that.displayInfo.push(it)
                        }
                        that.frequency.splice(0,that.frequency.length)
                        for (let item of setf.values()){
                            that.frequency.push({
                                label:item+"",
                                prop:item+""
                            })
                        }
                        console.log(that.watchMac)
                        for (var i=0;i<that.watchMac.length;i++){
                            var mac = that.watchMac[i]
                            console.log(mac)
                            var info = successReslut.data[mac]
                            console.log(info)
                            if(info == undefined||info == null){
                                continue
                            }
                            var rssAvgMap = []
                            let rssMap = info['rssAvgMap']
                            for(var apMac in rssMap){
                                let rss = rssMap[apMac]
                                rssAvgMap.push({
                                    apMac:apMac,
                                    rss:rss.rssi[0].toFixed(2)
                                })
                            }

                            var messages = []
                            let ms = info['messages']

                            for(var i=0;i<ms.length;i++){
                                var m = ms[i]
                                var date = new Date(m['timestamp'])
                                messages.push({
                                    apMac:m['apMac'],
                                    frequency: m['frequency'],
                                    rssi:m['rssi'].toFixed(2),
                                    timestamp:date.getHours()+":"+date.getMinutes()+":"+date.getSeconds()
                                })
                            }

                            that.watchMacTableData[mac] = {
                                rssAvgMap:rssAvgMap,
                                messages:messages
                            }
                            console.log(that.watchMacTableData)
                        }

                    }).catch(error=>{
                        that.$message.error(error)
                    })
                },n*1000)
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
            open: function () {
                console.log("socket连接成功")
                this.send("hello ws...")
            },
            error: function () {
                console.log("连接错误")
            },
            getMessage: function (msg) {
                console.log(msg)
                let data = msg['all']
                if(data == undefined||data == null){
                    return
                }
                this.displayInfo.splice(0,this.displayInfo.length)
                var setf = new Set()
                for(var key in data){
                    var frequencyNum = data[key]['frequencyNum']
                    for (var key1 in frequencyNum){
                        key1 = parseInt(key1)
                        setf.add(key1)
                    }
                }
                setf = Array.from(setf)
                setf.sort((a,b)=>{ return a-b})
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
                    for(let i of setf.values()){
                        if (frequencyNum[i]==undefined||frequencyNum[i]==null){
                            it[i] = 0
                        }else {
                            it[i] = frequencyNum[i]
                        }
                    }

                    this.displayInfo.push(it)
                }
                this.frequency.splice(0,this.frequency.length)
                for (let item of setf.values()){
                    this.frequency.push({
                        label:item+"",
                        prop:item+""
                    })
                }
                for (var i=0;i<this.watchMac.length;i++){
                    var mac = this.watchMac[i]
                    var info = msg.data[mac]
                    if(info == undefined||info == null){
                        continue
                    }
                    var rssAvgMap = []
                    let rssMap = info['rssAvgMap']
                    for(var apMac in rssMap){
                        let rss = rssMap[apMac]
                        rssAvgMap.push({
                            apMac:apMac,
                            rss:rss.rssi[0].toFixed(2)
                        })
                    }

                    var messages = []
                    let ms = info['messages']

                    for(var i=0;i<ms.length;i++){
                        var m = ms[i]
                        var date = new Date(m['timestamp'])
                        messages.push({
                            apMac:m['apMac'],
                            frequency: m['frequency'],
                            rssi:m['rssi'].toFixed(2),
                            timestamp:date.getHours()+":"+date.getMinutes()+":"+date.getSeconds()
                        })
                    }

                    this.watchMacTableData[mac] = {
                        rssAvgMap:rssAvgMap,
                        messages:messages
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

</style>
