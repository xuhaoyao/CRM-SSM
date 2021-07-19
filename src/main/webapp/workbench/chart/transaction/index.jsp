<%--
  Created by IntelliJ IDEA.
  User: xuhaoyao
  Date: 2021/5/16
  Time: 9:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<html>
<head>
    <base href="<%=basePath%>">
    <title>Title</title>

<script type="text/javascript" src="ECharts/echarts.min.js"></script>
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript">
        $(function(){

           $.ajax({
               url : "workbench/tran/showEcharts.do",
               type : "get",
               dataType : "json",
               success : function(data){
                   /*
                        data
                            max dataList
                    */
                   // 基于准备好的dom，初始化echarts实例
                   var myChart = echarts.init(document.getElementById('main'));
                   var option = {
                       title: {
                           text: '漏斗图',
                           subtext: '交易统计图表'
                       },
                       tooltip: {
                           trigger: 'item',
                           formatter: "{a} <br/>{b} : {c}"
                       },
                       toolbox: {
                           feature: {
                               dataView: {readOnly: false},
                               restore: {},
                               saveAsImage: {}
                           }
                       },

                       series: [
                           {
                               name:'漏斗图',
                               type:'funnel',
                               left: '10%',
                               top: 60,
                               //x2: 80,
                               bottom: 60,
                               width: '80%',
                               // height: {totalHeight} - y - y2,
                               min: 0,
                               max: data.max,
                               minSize: '0%',
                               maxSize: '100%',
                               sort: 'descending',
                               gap: 2,
                               label: {
                                   show: true,
                                   position: 'inside'
                               },
                               labelLine: {
                                   length: 10,
                                   lineStyle: {
                                       width: 1,
                                       type: 'solid'
                                   }
                               },
                               itemStyle: {
                                   borderColor: '#fff',
                                   borderWidth: 1
                               },
                               emphasis: {
                                   label: {
                                       fontSize: 20
                                   }
                               },
                               data: data.dataList
/*                                   [
                                   {value: 60, name: '访问'},
                                   {value: 40, name: '咨询'},
                                   {value: 20, name: '订单'},
                                   {value: 80, name: '点击'},
                                   {value: 100, name: '展现'}
                               ]*/
                           }
                       ]
                   };
                   myChart.setOption(option);
               }
           })

        })
    </script>

</head>
<body>
    <div id="main" style="width: 700px;height:450px;"></div>
</body>
</html>
