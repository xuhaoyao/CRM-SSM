<%--
  Created by IntelliJ IDEA.
  User: xuhaoyao
  Date: 2021/5/16
  Time: 11:37
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
                url : "workbench/tran/showEcharts1.do",
                type : "get",
                dataType : "json",
                success : function(data){
                    /*
                        data.name
                        data.value
                     */
                    // 基于准备好的dom，初始化echarts实例
                    var myChart = echarts.init(document.getElementById('main'));

                    // 指定图表的配置项和数据
                    var option = {
                        title: {
                            text: '交易阶段'
                        },
                        tooltip: {},
                        legend: {
                            data:['数量']
                        },
                        xAxis: {
                            data: data.name,
                            axisLabel : {
                                interval : 0 //横轴信息全部显示
                            }
                        },
                        yAxis: {},
                        series: [{
                            name: '数量',
                            type: 'bar',
                            data: data.value
                        }]
                    };

                    // 使用刚指定的配置项和数据显示图表。
                    myChart.setOption(option);
                }
            })
        })
    </script>

</head>
<body>
    <div id="main" style="width: 800px;height:400px;"></div>
</body>
</html>
