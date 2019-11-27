<template>
  <div class="container" style="margin: 20px;">
    <el-alert
      title="未加载晶格文件配置，请先加载定位配置"
      type="error"
      effect="dark"
      v-show="show.girdmap"
      >
    </el-alert>

    <el-row>
      <div id="hm">
        <canvas id="c"></canvas>
      </div>
    </el-row>
    <el-row style="margin-top: 10px">
      <el-col :span="4">
        <el-card class="box-card">
          <el-form ref="form" :model="option1" label-width="80px">
            <el-form-item label="开始定位">
              <el-switch
                v-model="locationSwith"
                active-color="#13ce66"
                inactive-color="#ff4949">
              </el-switch>
            </el-form-item>
            <el-form-item label="图表类型">
              <el-checkbox v-model="option1.dwt">点位图</el-checkbox>
              <el-checkbox v-model="heatmap">热力图</el-checkbox>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card class="box-card">
          <el-form ref="form" :model="option2" label-width="80px">
            <el-form-item label="Mac标注">
              <el-row>
                <el-col :span="16">
                  <el-table
                    :data="option2.colorMap"
                    style="width: 100%">
                    <el-table-column
                      label="Mac">
                      <template slot-scope="scope">
                        <el-input
                          v-model="scope.row.mac"
                          :disabled="true">
                        </el-input>
                      </template>
                    </el-table-column>
                    <el-table-column label="颜色">
                      <template slot-scope="scope">
                        <div class="block">
                          <el-color-picker v-model="scope.row.color"></el-color-picker>
                        </div>
                      </template>
                    </el-table-column>
                  </el-table>
                </el-col>
                <el-col :span="8">
                  <el-button @click="option2.macAllowDialog = true">添加</el-button>
                  <el-button @click="option2.colorMap = []">清空</el-button>
                  <el-switch
                    v-model="option2.macAllow"
                    active-color="#13ce66"
                    inactive-color="#ff4949"
                    active-text="开启过滤"
                    inactive-text="关闭过滤">
                  </el-switch>
                </el-col>
              </el-row>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card class="box-card">
          <el-form ref="form" :model="option3" label-width="120px">
            <el-form-item label="被定位设备数">
              <el-input
                v-model="option3.num"
                :disabled="true">
              </el-input>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>


    <el-dialog title="添加Mac匹配" :visible.sync="option2.macAllowDialog" :close-on-click-modal="false" width="20%">
      <el-form >
        <el-form-item label="Mac"  label-width="120px">
          <el-input autocomplete="off" v-model="option2.dialogContent.mac"></el-input>
        </el-form-item>
        <el-form-item label="颜色"  label-width="120px">
          <el-color-picker v-model="option2.dialogContent.color"></el-color-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="option2.macAllowDialog = false">取 消</el-button>
        <el-button type="primary" @click="addMacMap">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
    import Heatmap from 'heatmap.js'
    export default {
        name: "LocationShowPage",
        data(){
          return {
            heatmapInstance:"",
            locTimeOutId:null,
            locationSwith:false,
            heatmap:false,
            option1:{
                dwt:true,
                heatmap:false
            },
            option2:{
                macAllowDialog:false,
                macAllow:false,
                colorMap:[],
                dialogContent:{
                  mac:"",
                  color:"#409EFF"
                },
            },
            option3:{
              num:0
            },
            show:{
              girdmap:false,
            },
            canvasParams:{
                width:1800,
                height:500,
                canvas:''
            },
            gridMap:{
                left:0.0,
                right:0.0,
                top:0.0,
                buttom:0.0,

                k:1,
                x:[],
                y:[],
            },
            locpos:[],
            baseUrl:'locationSys/'
          };
      },
      watch:{
          heatmap(val,oldVal){
              this.heatmapInstance.configure({
                  visible: val
              })
          },
          locpos(val,oldVal) {
            var objects = this.canvasParams.canvas.getObjects();
            for (var i = objects.length - 1; i >= 0; i--) {
              var object = objects[i];
              if(object.type == 'circle'){
                this.canvasParams.canvas.remove(object)
              }
            }

            for(var i=0,len = oldVal.length;i<len;i++){
              this.canvasParams.canvas.remove(oldVal[i])
            }

            for (var i=0,len = val.length;i<len;i++){
              this.canvasParams.canvas.add(val[i])
            }
          },
          locationSwith(val,oldVal){
            if (val == true){
              this.startLoc()
            } else {
              this.stopLoc()
            }
         }
      },
      methods:{
        addMacMap(){
            this.option2.macAllowDialog = false
            this.option2.colorMap.push({
              mac:this.option2.dialogContent.mac,
              color:this.option2.dialogContent.color,
            })
            this.option2.dialogContent.mac=""
            this.option2.dialogContent.color="#409EFF"
        },
        startLoc(){
          var that = this
          this.$axios.get(that.baseUrl+"startLocation")
          this.locTimeOutId=setInterval(() => {
            that.getLocInfo()
          },1000)
        },
        getLocInfo(){
          let self = this;
          self.$axios.get(self.baseUrl+'getLocationInfo').then(successResponse => {
            var data = JSON.stringify(successResponse.data)
            var map = JSON.parse(data);
            self.locpos.splice(0,self.locpos.length);
            var locPoint = map.locationResultMap;
            if (locPoint.length==0){
                self.option3.num = 0
                self.heatmapInstance._renderer._clear()
                return
            }
            var num = 0;
            for (var devMac in locPoint){
              num++;
              var x = locPoint[devMac].pos.x-that.gridMap.left
              var y = locPoint[devMac].pos.y-that.gridMap.top
              //过滤Mac
              let color = 'red'
              let contain = false
              for (var i=0;i<self.option2.colorMap.length;i++){
                if (self.option2.colorMap[i].mac == devMac){
                  color = self.option2.colorMap[i].color
                  contain =true
                }
              }
              if (self.option2.macAllow == true&&contain ==false){
                 continue
              }
              //颜色
              self.locpos.push(
                  new fabric.Circle({
                    radius:6,
                    fill:color,
                    left:x*self.gridMap.k-6,
                    top:y*self.gridMap.k-6
                  })
              )
            }
            self.option3.num = num

            if (self.heatmap == true){
                var heatmapdata = map.deviceDensity;
                var points = [];
                var max = 0;
                var len = self.gridMap.x.length;
                var xPos = self.gridMap.x
                var yPos = self.gridMap.y
                var k = self.gridMap.k
                for (var i =0;i<len;i++) {
                    var val = heatmapdata.deviceDensity[i]
                    max = Math.max(max, val);
                    var point = {
                        x: Math.floor(xPos[i] * k),
                        y: Math.floor(yPos[i] * k),
                        // x: Math.floor(Math.random() * 1800),
                        // y: Math.floor(Math.random() * 400),
                        value: val
                    };
                    points.push(point);
                }
                var data = {
                    max: max,
                    data: points
                }
                console.log(data)
                self.heatmapInstance.setData(data)
            }else {
                self.heatmapInstance._renderer._clear()
            }
          })
        },
        stopLoc(){
          this.$axios.get(this.baseUrl+'stopLocation')
          clearInterval(this.locTimeOutId)
          this.locTimeOutId = null
        },
        buildGridMap(){
            var that = this
            that.canvasParams.canvas = new fabric.Canvas(document.getElementById("c"),{selection:false,width:that.canvasParams.width,height:that.canvasParams.height})
            this.$axios.get(that.baseUrl+"getGridMapData").then(function (result) {
                var gridmap = result.data
              if (gridmap.gridNum == 0){
                that.show.girdmap == true
                return
              }
                that.gridMap.top = gridmap.y[0]-gridmap.height[0]/2
                that.gridMap.left = gridmap.x[0]-gridmap.width[0]/2
                that.gridMap.buttom = gridmap.y[0]+gridmap.height[0]/2
                that.gridMap.right = gridmap.x[0]+gridmap.width[0]/2
                for (var i=1;i<gridmap.gridNum;i++){
                    that.gridMap.top = that.gridMap.top<(gridmap.y[i]-gridmap.height[i]/2)?that.gridMap.top:(gridmap.y[i]-gridmap.height[i]/2)
                    that.gridMap.left = that.gridMap.left<(gridmap.x[i]-gridmap.width[i]/2)?that.gridMap.left:(gridmap.x[i]-gridmap.width[i]/2)
                    that.gridMap.buttom = that.gridMap.buttom>(gridmap.y[i]+gridmap.height[i]/2)?that.gridMap.buttom:(gridmap.y[i]+gridmap.height[i]/2)
                    that.gridMap.right = that.gridMap.right>(gridmap.x[i]+gridmap.width[i]/2)?that.gridMap.right:(gridmap.x[i]+gridmap.width[i]/2)
                }
                that.gridMap.x = gridmap.x;
                that.gridMap.y = gridmap.y;
               let a = (that.canvasParams.width-10)/(that.gridMap.right-that.gridMap.left)
               let b = (that.canvasParams.height-10)/(that.gridMap.buttom-that.gridMap.top)
               that.gridMap.k = a>b?b:a;
               let k = that.gridMap.k
               var GridRect = fabric.util.createClass(fabric.Rect,{
                type:'GridRect',
                selectedGrid:false,
                //width height
                initialize: function(options) {
                  options || (options = { });
                  options.selectable = false
                  this.callSuper('initialize', options);
                  this.set('label', options.label || '');
                },
                toObject: function() {
                  return fabric.util.object.extend(this.callSuper('toObject'), {
                    label: this.get('label')
                  });
                },

                _render: function(ctx) {
                  this.callSuper('_render', ctx);
                  ctx.font = '10px Helvetica';
                  ctx.fillStyle = '#333';
                  ctx.fillText(this.label, -this.width/2, -this.height/2+10);
                }
              })
               for (var i = 0;i<gridmap.gridNum;i++){
                  let left = (gridmap.x[i]-gridmap.width[i]/2-that.gridMap.left)*k;
                  let top = (gridmap.y[i] - gridmap.height[i]/2-that.gridMap.top)*k;
                  let width = (gridmap.width[i])*k;
                  let height = (gridmap.height[i])*k;

                  that.canvasParams.canvas.add(new GridRect({
                    left:left,
                    top:top,
                    width:width,
                    height:height,
                    fill:'rgba(255, 255, 255, 0)',//填充的颜色
                    stroke: 'green',
                    strokeDashArray:[1,2],
                    strokeWidth: 1,
                    label:(i+1),
                    selectable:false
                  }))
               }
            })

        },
      },
      created(){},
      mounted() {
        this.buildGridMap()
          this.heatmapInstance = Heatmap.create({
              container: document.getElementById('hm'),
              visible:this.heatmap
          })
      }
    }
</script>

<style scoped>
  .el-card {
    background: #F2F2F2;
    margin: 0px 10px 0px 2px;
    height: 25vh;
  }

</style>
