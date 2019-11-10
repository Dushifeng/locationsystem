<template>
  <div class="container" style="margin: 20px">
    <canvas id="c"></canvas>


    <el-col :span="8">
      <div class="grid-content" align="center" style="vertical-align: center;line-height: 250px;font-size: 50px" >
        {{seconds}}
      </div>
    </el-col>
    <el-col :span="16">
      <div class="grid-content">
        <el-table
          :data="tableData"
          height="250"
          border
          style="width: 100%">
          <el-table-column
            prop="id"
            label="晶格编号"
            width="180">
          </el-table-column>
          <el-table-column
            prop="xPos"
            label="X坐标"
            width="180">
          </el-table-column>
          <el-table-column
            prop="yPos"
            label="Y坐标"
            width="180">
          </el-table-column>
          <el-table-column
            prop="num"
            label="总记录数"
            width="180">
          </el-table-column>
          <el-table-column
            prop="mac"
            label="采集设备Mac">
            <template slot-scope="scope">
              <el-input placeholder="请输入内容" v-model="scope.row.mac"></el-input>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-col>


    <el-button type="primary" round v-on:click="startCollection">开始采集</el-button>
    <el-button type="success" round v-on:click="stopCollection">结束采集</el-button>

  </div>
</template>

<script>
    export default {
        name: "collectionStart",
        data(){
          return {
            interval:7.45,
            k:44,
            seconds:0,
            tableData: []
          };
        },
        methods:{

          startCollection:function () {
            var _this = this;

            this.$axios.post('/collection/startCollection', _this.tableData)

          },
          stopCollection:function () {
            var _this = this;

            this.$axios.post('/collection/finishCollection')
              .then(successResponse => {
                this.responseResult = JSON.stringify(successResponse.data)
                for(var a = 0,len=_this.tableData.length; a < len; a++) {
                  var item = _this.tableData[a]

                  var map = JSON.parse(this.responseResult);
                  _this.tableData[a].num = map[item.id+""]
                  console.log(_this.tableData[a].num )
                }
              })
          }
        },
        mounted() {
          var _this = this;
          var canvasElement = document.getElementById("c");
          var canvas = new fabric.Canvas(canvasElement,{
            selection:false,
            width:2200,
            height:400
          })



          var car2 = new fabric.Rect({
            top:2*this.k,
            fill:'rgba(255, 255, 255, 0)',//填充的颜色
            width:18.3*this.k,//方形的宽度
            height:2.5*this.k,//方形的高度
            stroke: 'green',
            strokeWidth: 2,
            selectable:false
          });

          var car1 = new fabric.Rect({
            top:2*this.k,
            left:(18.3+this.interval)*this.k,
            fill:'rgba(255, 255, 255, 0)',//填充的颜色
            width:16.5*this.k,//方形的宽度
            height:2.5*this.k,//方形的高度
            stroke: 'green',
            strokeWidth: 2,
            selectable:false
          });

          // var head = new fabric.Path("M 1575.2 88 L 1619.2 88 a 40 55 0 1 1 0 110 L 1575.2 198 z")
          // head.set({fill:"#FFFFFF",stroke: 'green',strokeWidth: 2,selectable:false});

          canvas.add(car1)
          canvas.add(car2)
          // canvas.add(head)

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

          var baseIndex = 1
          var i,j;
          //1-90
          for(i = 0;i<17;i++){
            for( j =0;j<5;j++){

              var t = new GridRect({
                left:i*1*this.k,
                top:(j)*0.5*this.k+2*this.k,
                fill:'rgba(255, 255, 255, 0)',//填充的颜色
                width:1*this.k,//方形的宽度
                height:0.5*this.k,//方形的高度
                stroke: 'green',
                strokeDashArray:[1,2],
                strokeWidth: 1,
                label:baseIndex+"",
                selectable:false
              })

              t.on('mouse:down', function(options) {//选中监听事件
                console.log('selected a gridRect...');
              });

              canvas.add(t)
              baseIndex++;
            }
          }
          for( j =0;j<5;j++){
            canvas.add(new GridRect({
              left:17*1*this.k,
              top:(j)*0.5*this.k+2*this.k,
              fill:'rgba(255, 255, 255, 0)',//填充的颜色
              width:1.3*this.k,//方形的宽度
              height:0.5*this.k,//方形的高度
              stroke: 'green',
              strokeDashArray:[1,2],
              strokeWidth: 1,
              label:baseIndex+""
            }))
            baseIndex++;
          }

          //91-170
          for(i = 0;i<15;i++){
            for( j =0;j<5;j++){
              canvas.add(new GridRect({
                left:(i+18.3+this.interval)*1*this.k,
                top:(j)*0.5*this.k+2*this.k,
                fill:'rgba(255, 255, 255, 0)',//填充的颜色
                width:1*this.k,//方形的宽度
                height:0.5*this.k,//方形的高度
                stroke: 'green',
                strokeDashArray:[1,2],
                strokeWidth: 1,
                label:baseIndex+"",
                selectable:false
              }))
              baseIndex++;
            }
          }
          for( j =0;j<5;j++){
            canvas.add(new GridRect({
              left:(33.3+this.interval)*this.k,
              top:(j)*0.5*this.k+2*this.k,
              fill:'rgba(255, 255, 255, 0)',//填充的颜色
              width:1.5*this.k,//方形的宽度
              height:0.5*this.k,//方形的高度
              stroke: 'green',
              strokeDashArray:[1,2],
              strokeWidth: 1,
              label:baseIndex+""
            }))
            baseIndex++;
          }

          //171-206
          for(i = 0;i<17;i++){
            for( j =0;j<2;j++){
              canvas.add(new GridRect({
                left:i*1*this.k,
                top:j*1*this.k,
                fill:'rgba(255, 255, 255, 0)',//填充的颜色
                width:1*this.k,//方形的宽度
                height:1*this.k,//方形的高度
                stroke: 'green',
                strokeDashArray:[1,2],
                strokeWidth: 1,
                label:baseIndex+""
              }))
              baseIndex++;
            }
          }
          for( j =0;j<2;j++){
            canvas.add(new GridRect({
              left:17*1*this.k,
              top:j*1*this.k,
              fill:'rgba(255, 255, 255, 0)',//填充的颜色
              width:1.3*this.k,//方形的宽度
              height:1*this.k,//方形的高度
              stroke: 'green',
              strokeDashArray:[1,2],
              strokeWidth: 1,
              label:baseIndex+""
            }))
            baseIndex++;
          }

          //207-238
          for(i = 0;i<15;i++){
            for(j =0;j<2;j++){
              canvas.add(new GridRect({
                left:(i+18.3+this.interval)*1*this.k,
                top:j*1*this.k,
                fill:'rgba(255, 255, 255, 0)',//填充的颜色
                width:1*this.k,//方形的宽度
                height:1*this.k,//方形的高度
                stroke: 'green',
                strokeDashArray:[1,2],
                strokeWidth: 1,
                label:baseIndex+""
              }))
              baseIndex++;
            }
          }
          for( j =0;j<2;j++){
            canvas.add(new GridRect({
              left:(33.3+this.interval)*this.k,
              top:j*1*this.k,
              fill:'rgba(255, 255, 255, 0)',//填充的颜色
              width:1.5*this.k,//方形的宽度
              height:1*this.k,//方形的高度
              stroke: 'green',
              strokeDashArray:[1,2],
              strokeWidth: 1,
              label:baseIndex+""
            }))
            baseIndex++;
          }

          //239-274
          for(i = 0;i<17;i++){
            for( j =0;j<2;j++){
              canvas.add(new GridRect({
                left:i*1*this.k,
                top:j*1*this.k+4.5*this.k,
                fill:'rgba(255, 255, 255, 0)',//填充的颜色
                width:1*this.k,//方形的宽度
                height:1*this.k,//方形的高度
                stroke: 'green',
                strokeDashArray:[1,2],
                strokeWidth: 1,
                label:baseIndex+""
              }))
              baseIndex++;
            }
          }
          for( j =0;j<2;j++){
            canvas.add(new GridRect({
              left:17*1*this.k,
              top:j*1*this.k+4.5*this.k,
              fill:'rgba(255, 255, 255, 0)',//填充的颜色
              width:1.3*this.k,//方形的宽度
              height:1*this.k,//方形的高度
              stroke: 'green',
              strokeDashArray:[1,2],
              strokeWidth: 1,
              label:baseIndex+""
            }))
            baseIndex++;
          }

          //275-306
          for(i = 0;i<15;i++){
            for(j =0;j<2;j++){
              canvas.add(new GridRect({
                left:(i+18.3+this.interval)*1*this.k,
                top:j*1*this.k+4.5*this.k,
                fill:'rgba(255, 255, 255, 0)',//填充的颜色
                width:1*this.k,//方形的宽度
                height:1*this.k,//方形的高度
                stroke: 'green',
                strokeDashArray:[1,2],
                strokeWidth: 1,
                label:baseIndex+""
              }))
              baseIndex++;
            }
          }
          for( j =0;j<2;j++){
            canvas.add(new GridRect({
              left:(33.3+this.interval)*this.k,
              top:j*1*this.k+4.5*this.k,
              fill:'rgba(255, 255, 255, 0)',//填充的颜色
              width:1.5*this.k,//方形的宽度
              height:1*this.k,//方形的高度
              stroke: 'green',
              strokeDashArray:[1,2],
              strokeWidth: 1,
              label:baseIndex+""
            }))
            baseIndex++;
          }

          for( j =0;j<2;j++){
            canvas.add(new GridRect({
              left:18.3*this.k,
              top:j*1*this.k,
              fill:'rgba(255, 255, 255, 0)',//填充的颜色
              width:this.interval*this.k,//方形的宽度
              height:1*this.k,//方形的高度
              stroke: 'green',
              strokeDashArray:[1,2],
              strokeWidth: 1,
              label:baseIndex+""
            }))
            baseIndex++;
          }

          for( j =0;j<5;j++){
            canvas.add(new GridRect({
              left:18.3*this.k,
              top:j*0.5*this.k+2*this.k,
              fill:'rgba(255, 255, 255, 0)',//填充的颜色
              width:this.interval*this.k,//方形的宽度
              height:0.5*this.k,//方形的高度
              stroke: 'green',
              strokeDashArray:[1,2],
              strokeWidth: 1,
              label:baseIndex+""
            }))
            baseIndex++;
          }

          for( j =0;j<2;j++){
            canvas.add(new GridRect({
              left:18.3*this.k,
              top:(j+4.5)*this.k,
              fill:'rgba(255, 255, 255, 0)',//填充的颜色
              width:this.interval*this.k,//方形的宽度
              height:1*this.k,//方形的高度
              stroke: 'green',
              strokeDashArray:[1,2],
              strokeWidth: 1,
              label:baseIndex+""
            }))
            baseIndex++;
          }






          canvas.on('mouse:down', function(options) {//选中监听事件
            var objects = canvas.getObjects();
            for (var i = objects.length - 1; i >= 0; i--) {
              var object = objects[i];
              //判断该对象是否在鼠标点击处
              if (canvas.containsPoint(event, object) && object.__proto__.type == 'GridRect') {
                //选中该对象
                if (object.selectedGrid == false){
                  object.selectedGrid = true
                  object.set("fill", 'red');
                  _this.tableData.push({
                    id: object.label,
                    xPos: (object.width/2+object.left)/_this.k,
                    yPos: (object.height/2+object.top)/_this.k,
                    num:0
                  })
                }else {
                  object.selectedGrid = false
                  object.set("fill", 'rgba(255, 255, 255, 0)');
                  var index;


                  for(var a = 0,len=_this.tableData.length; a < len; a++) {
                    var item = _this.tableData[a]
                    if (parseInt(item.id) == parseInt(object.label)){
                      index = a
                    }
                  }
                  console.log(index)

                  _this.tableData.splice(index,1);

                }
                canvas.renderAll()
              }
            }
          });
        }
    }
</script>

<style scoped>

</style>
