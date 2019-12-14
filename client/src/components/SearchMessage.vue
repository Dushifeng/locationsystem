<template>
  <el-container>
    <el-header align="center">
      <h1>嗅探记录数据检索</h1>
    </el-header>
    <el-main>
      <el-row style="height: 100px">
        <el-form :inline="true">
          <el-col :span="6">
            <el-form-item>
              <el-row>
                <el-col :span="4">
                  嗅探时间
                </el-col>
                <el-col :span="20">
                  <el-date-picker
                    v-model="dateTimeStr"
                    type="datetimerange"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    placeholder="选择开始日期时间"
                    :picker-options="pickerOptions">
                  </el-date-picker>
                </el-col>
              </el-row>
            </el-form-item>
          </el-col>
          <el-col :span="4" :offset="1">
            <el-form-item label="设备Mac">
              <el-input v-model="devMac"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="AP Mac">
              <el-input v-model="apMac"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="频段号">
              <el-input v-model="frequency"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="5" align="center">
            <el-button @click="submitQuery">查询</el-button>
            <el-button @click="clearUp">清空</el-button>
            <el-button @click="download">下载</el-button>
          </el-col>
        </el-form>
      </el-row>
      <el-row >
        <el-table
          :data="resultList"
          stripe
          style="width: 100%;min-height: 400px"
          id="outTable">
          <el-table-column
            prop="id"
            label="id">
          </el-table-column>
          <el-table-column
            prop="devMac"
            label="设备MAC">
          </el-table-column>
          <el-table-column
            prop="apMac"
            label="探针MAC">
          </el-table-column>
          <el-table-column
            prop="frequency"
            label="频段号">
          </el-table-column>
          <el-table-column
            prop="rssi"
            label="RSSI">
          </el-table-column>

          <el-table-column
            prop="timestamp"
            label="嗅探时间">
          </el-table-column>

        </el-table>
      </el-row>
      <el-row align="center">
        <el-col :span="24" align="center">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="currentPage"
            :page-sizes="[5, 10, 20, 30,40,50]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="totalNum">
          </el-pagination>
        </el-col>

      </el-row>
    </el-main>
  </el-container>
</template>

<script>
    export default {
        name: "SearchMessage",
        data(){
          return {
            resultList:[],
            pickerOptions: {
              shortcuts: [{
                text: '最近一周',
                onClick(picker) {
                  const end = new Date();
                  const start = new Date();
                  start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                  picker.$emit('pick', [start, end]);
                }
              }, {
                text: '最近一个月',
                onClick(picker) {
                  const end = new Date();
                  const start = new Date();
                  start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                  picker.$emit('pick', [start, end]);
                }
              }, {
                text: '最近三个月',
                onClick(picker) {
                  const end = new Date();
                  const start = new Date();
                  start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                  picker.$emit('pick', [start, end]);
                }
              }]
            },
            dateTimeStr: [],
            totalNum:0,
            currentPage:1,
            pageSize:5,
            devMac:'',
            apMac:'',
            frequency:'',

          }
        },
      mounted() {
      },
      methods:{

        submitQuery:function () {
          /**
           *     @RequestMapping("searchMessage")
           public Map<String,Object> searchMessage(@RequestParam(required = false,name = "start") Date start,
           @RequestParam(required = false,name = "end") Date end,
           @RequestParam(required = false,name = "devMac") String devMac,
           @RequestParam(required = false,name = "apMac") String apMac,
           @RequestParam(required = false,name = "frequency") Integer frequency,
           @RequestParam(required = false,name = "pageNo") Integer pageNo,
           @RequestParam(required = false,name = "pageSize") Integer pageSize)
           */

            var that = this
            let fd = new FormData()
            console.log(this.dateTimeStr)
            if (this.dateTimeStr.length == 2){
              fd.append("start",this.dateTimeStr[0])
              fd.append("end",this.dateTimeStr[1])
            }
            if (this.devMac!=''){
              fd.append("devMac",this.devMac)
            }

            if(this.apMac!=''){
              fd.append("apMac",this.apMac)
            }
            if (this.frequency!=''){
              try{
                let f = parseInt(this.frequency)
                fd.append("frequency",f)
              }catch (e) {

              }
            }
          fd.append("pageNo",that.currentPage)
          fd.append("pageSize",that.pageSize)

          this.$axios.post("searchMessage",fd).then(function (result) {
            var ans = result.data
            console.log(ans)
            that.resultList.splice(0,that.resultList.length)
            that.totalNum = ans.num
            for (let i=0,len=ans.records.length;i<len;i++){
              that.resultList.push(ans.records[i])
            }
          }).catch(e=>{
            console.log(e)
          })
        },

        handleSizeChange(val) {
          this.pageSize = val
          this.submitQuery()
        },
        handleCurrentChange(val) {
          this.currentPage = val
          this.submitQuery()
        },
        clearUp:function(){
          this.dateTimeStr = []
          this.devMac='',
          this.apMac='',
          this.frequency=''
        },
        download:function () {

        }
      }
    }
</script>

<style scoped>

</style>
