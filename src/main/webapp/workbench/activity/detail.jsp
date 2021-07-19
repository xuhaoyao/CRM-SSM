<%@ page import="com.scnu.crm.settings.domain.User" %>
<%@ page import="com.scnu.crm.workbench.domain.Activity" %>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

<script type="text/javascript">

	//默认情况下取消和保存按钮是隐藏的
	var cancelAndSaveBtnDefault = true;
	
	$(function(){

        $(".time").datetimepicker({
            minView: "month",
            language:  'zh-CN',
            format: 'yyyy-mm-dd',
            autoclose: true,
            todayBtn: true,
            pickerPosition: "bottom-left"
        });

		$("#remark").focus(function(){
			if(cancelAndSaveBtnDefault){
				//设置remarkDiv的高度为130px
				$("#remarkDiv").css("height","130px");
				//显示
				$("#cancelAndSaveBtn").show("2000");
				cancelAndSaveBtnDefault = false;
			}
		});
		
		$("#cancelBtn").click(function(){
			//显示
			$("#cancelAndSaveBtn").hide();
			//设置remarkDiv的高度为130px
			$("#remarkDiv").css("height","90px");
			cancelAndSaveBtnDefault = true;
		});
		
		$(".remarkDiv").mouseover(function(){
			$(this).children("div").children("div").show();
		});
		
		$(".remarkDiv").mouseout(function(){
			$(this).children("div").children("div").hide();
		});
		
		$(".myHref").mouseover(function(){
			$(this).children("span").css("color","red");
		});
		
		$(".myHref").mouseout(function(){
			$(this).children("span").css("color","#E6E6E6");
		});

		showRemarkList();

		$("#remarkBody").on("mouseover",".remarkDiv",function(){
			$(this).children("div").children("div").show();
		})
		$("#remarkBody").on("mouseout",".remarkDiv",function(){
			$(this).children("div").children("div").hide();
		})

		$("#saveRemarkBtn").click(function(){
			var text = $.trim($("#remark").val());
			if(text == ""){
				alert("备注信息不能为空.");
			}
			else{
				$.ajax({
					url : "workbench/activity/saveRemark.do",
					data : {
						"noteContent" : text,
						"activityId" : "${a.id}"
					},
					type : "post",
					dataType : "json",
					success : function(data){
						/*
							data
								{"success" : true/false,"ar" : {备注信息}}
						 */
						if(data.success){
							var html = "";
							html += '<div id='+data.ar.id+' class="remarkDiv" style="height: 60px;">';
							html += '<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">';
                            html += '<div style="position: relative; top: -40px; left: 40px;" >';
                            html += '<h5 id="note'+data.ar.id+'">'+data.ar.noteContent+'</h5>';
                            html += '<font color="gray">市场活动</font> <font color="gray">-</font> <b class="activityNames">'+$("#activity-name").html()+'</b> <small style="color: gray;" id="small'+data.ar.id+'"> '+(data.ar.createTime)+' 由'+(data.ar.createBy)+'</small>';
                            html += '<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">';
                            html += '<a class="myHref" href="javascript:void(0);" onclick="editRemark(\''+data.ar.id+'\')"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #FF0000;"></span></a>';
                            html += '&nbsp;&nbsp;&nbsp;&nbsp;';
                            html += '<a class="myHref" href="javascript:void(0);" onclick="deleteRemark(\''+data.ar.id+'\')"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #FF0000;"></span></a>';
                            html += '</div>';
                            html += '</div>';
                            html += '</div>';
                            $("#remarkDiv").before(html);
                            $("#remark").val("");
					    }
                        else
                            alert(data.errMsg);
					}
				})
			}
		})

		$("#updateRemarkBtn").click(function(){
			$.ajax({
				url : "workbench/activity/updateRemark.do",
				data : {
					"id" : $("#remarkId").val(),
					"noteContent" : $.trim($("#noteContent").val())
				},
				type : "post",
				dataType : "json",
				success : function(data){
					/*
						data
							{"success" : true/false,"ar":{备注信息}}
 					 */
					if(data.success){
						$("#note"+data.ar.id).html(data.ar.noteContent);
						$("#small"+data.ar.id).html(data.ar.editTime + " 由" + data.ar.editBy);
						$("#editRemarkModal").modal("hide");
					}else
						alert(data.errMsg);
				}
			})
		})

		$("#editActivityBtn").click(function(){

			//获取用户下拉框
			$.ajax({
				url : "workbench/activity/getUserList.do",
				type : "get",
				dataType : "json",
				success : function(data){
					var html = "";
					$.each(data,function(i,n){
						html += "<option value="+n.id+">"+n.name+"</option>";
					})
					$("#edit-owner").html(html);
					//遍历select 找到所有者
                    var owner = $("#activity-owner").html();
                    $("#edit-owner option").each(function(){
                        /*
                        在each代码块内不能使用break和continue,要实现break和continue的功能的话，
                        要使用以下的方式：
                        break----用return false;
                        continue --用return true;
                         */
                        if(owner == $(this).text()){
                            $(this).prop("selected",true);
                            return false;
                        }
                    })
				}
			})

            //初始化现有的市场活动数据(这里的标签是<b> 不是<input> 所以用html 而不是val)
            $("#edit-owner").val($("#activity-owner").html());
            $("#edit-name").val($("#activity-name").html());
            $("#edit-startDate").val($("#activity-startDate").html());
            $("#edit-endDate").val($("#activity-endDate").html());
            $("#edit-cost").val($("#activity-cost").html());
            $("#edit-description").val($("#activity-description").html());

			$("#editActivityModal").modal("show");
		})

        $("#updateActivityBtn").click(function(){
            $.ajax({
                url : "workbench/activity/updateActivity.do",
                data : {
                    "id" : "${a.id}",
                    "owner" : $.trim($("#edit-owner").val()),
                    "name": $.trim($("#edit-name").val()),
                    "startDate": $.trim($("#edit-startDate").val()),
                    "endDate": $.trim($("#edit-endDate").val()),
                    "cost": $.trim($("#edit-cost").val()),
                    "description": $.trim($("#edit-description").val())
                },
                type : "post",
                dataType : "json",
                success : function(data){
                    if(data.success) {
                        //更新成功后,更改原有数据
                        $("#activity-owner").html($("#edit-owner option:selected").text());
                        $("#activity-name").html($("#edit-name").val());
                        $("#activity-startDate").html($("#edit-startDate").val());
                        $("#activity-endDate").html($("#edit-endDate").val());
                        $("#activity-cost").html($("#edit-cost").val());
                        $("#activity-description").html($("#edit-description").val());
                        $("#activity-editBy").html("${user.name}&nbsp;&nbsp");
                        $("#activity-editTime").html(data.a.editTime);
                        $("#activityTitle").html("市场活动-"+data.a.name+" <small>"+data.a.startDate+" ~ "+data.a.endDate+"</small></h3>");
                        $(".activityNames").html(data.a.name);
                        $("#editActivityModal").modal("hide");
                    }
                    else
                        alert(data.errMsg);
                }
            })
        })

        $("#deleteActivityBtn").click(function(){
            if(confirm("确定要删除这场市场活动吗？")){
                $.ajax({
                    url : "workbench/activity/delete.do?id=${a.id}",
                                type : "post",
                            dataType : "json",
                            success : function(data){
                            if(data.success)
                                window.location.href = "workbench/activity/index.jsp";
                        else
                            alert(data.errMsg);
                    }
                })
            }
        })

	});

	function showRemarkList(){
		$.ajax({
			url : "workbench/activity/getRemarkList.do",
			data : {
				"activityId" : "${a.id}"
			},
			type : "get",
			dataType : "json",
			success : function(data){
				/*
					data
						[{备注1},{备注2}...]
				 */
				var html = "";
				$.each(data,function(i,n){
					html += '<div id='+n.id+' class="remarkDiv" style="height: 60px;">';
					html += '<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">';
					html += '<div style="position: relative; top: -40px; left: 40px;" >';
					html += '<h5 id=note'+n.id+'>'+n.noteContent+'</h5>';
					html += '<font color="gray">市场活动</font> <font color="gray">-</font> <b class="activityNames">${a.name}</b> <small style="color: gray;" id=small'+n.id+'> '+(n.editFlag==0?n.createTime:n.editTime)+' 由'+(n.editFlag==0?n.createBy:n.editBy)+'</small>';
					html += '<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">';
                    html += '<a class="myHref" href="javascript:void(0);" onclick="editRemark(\''+n.id+'\')"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #FF0000;"></span></a>';
                    html += '&nbsp;&nbsp;&nbsp;&nbsp;';
					html += '<a class="myHref" href="javascript:void(0);" onclick="deleteRemark(\''+n.id+'\')"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #FF0000;"></span></a>';
					html += '</div>';
					html += '</div>';
					html += '</div>';
				})
				$("#remarkDiv").before(html);

			}
		})
	}

	function deleteRemark(id){
		if(!confirm("确定删除该条备注吗?")) return false;
		$.ajax({
			url : "workbench/activity/deleteRemark.do",
			data : {
				"id" : id
			},
			type : "post",
			dataType : "json",
			success : function(data){
				if(data.success)
					$("#"+id).remove();
				else
					alert(data.errMsg);
			}
		})
	}

	function editRemark(id){
		$("#editRemarkModal").modal("show");
		$("#remarkId").val(id);
		$("#noteContent").val($("#note" + id).html());
	}
	
</script>

</head>
<body>
	
	<!-- 修改市场活动备注的模态窗口 -->
	<div class="modal fade" id="editRemarkModal" role="dialog">
		<%-- 备注的id --%>
		<input type="hidden" id="remarkId">
        <div class="modal-dialog" role="document" style="width: 40%;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">×</span>
                    </button>
                    <h4 class="modal-title" id="myModalLabel">修改备注</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal" role="form">
                        <div class="form-group">
                            <label for="edit-describe" class="col-sm-2 control-label">内容</label>
                            <div class="col-sm-10" style="width: 81%;">
                                <textarea class="form-control" rows="3" id="noteContent"></textarea>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button type="button" class="btn btn-primary" id="updateRemarkBtn">更新</button>
                </div>
            </div>
        </div>
    </div>

    <!-- 修改市场活动的模态窗口 -->
    <div class="modal fade" id="editActivityModal" role="dialog">
        <div class="modal-dialog" role="document" style="width: 85%;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">×</span>
                    </button>
                    <h4 class="modal-title" id="myModalLabel">修改市场活动</h4>
                </div>
                <div class="modal-body">

                    <form class="form-horizontal" role="form">

                        <div class="form-group">
                            <label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <select class="form-control" id="edit-owner">
                                </select>
                            </div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-name">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control time" id="edit-startDate">
                            </div>
                            <label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control time" id="edit-endDate">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-cost">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-describe" class="col-sm-2 control-label">描述</label>
                            <div class="col-sm-10" style="width: 81%;">
                                <textarea class="form-control" rows="3" id="edit-description"></textarea>
                            </div>
                        </div>

                    </form>

                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button type="button" class="btn btn-primary" id="updateActivityBtn">更新</button>
                </div>
            </div>
        </div>
    </div>

	<!-- 返回按钮 -->
	<div style="position: relative; top: 35px; left: 10px;">
		<a href="javascript:void(0);" onclick="window.location.href='workbench/activity/index.jsp?pageNo=${pageNo}&pageSize=${pageSize}&name=${name}&owner=${owner}&startDate=${startDate}&endDate=${endDate}';"><span class="glyphicon glyphicon-arrow-left" style="font-size: 20px; color: #DDDDDD"></span></a>
	</div>
	
	<!-- 大标题 -->
	<div style="position: relative; left: 40px; top: -30px;">
		<div class="page-header">
			<h3 id="activityTitle">市场活动-${a.name} <small id="DateInterval">${a.startDate} ~ ${a.endDate}</small></h3>
		</div>
		<div style="position: relative; height: 50px; width: 250px;  top: -72px; left: 700px;">
			<!--data-toggle="modal" data-target="#editActivityModal"-->
			<button type="button" class="btn btn-default" id="editActivityBtn"><span class="glyphicon glyphicon-edit"></span> 编辑</button>
			<button type="button" class="btn btn-danger" id="deleteActivityBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
		</div>
	</div>
	
	<!-- 详细信息 -->
	<div style="position: relative; top: -70px;">
		<div style="position: relative; left: 40px; height: 30px;">
			<div style="width: 300px; color: gray;">所有者</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b id="activity-owner">${a.owner}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">名称</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b id="activity-name">${a.name}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>

		<div style="position: relative; left: 40px; height: 30px; top: 10px;">
			<div style="width: 300px; color: gray;">开始日期</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b id="activity-startDate">${a.startDate}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">结束日期</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b id="activity-endDate">${a.endDate}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 20px;">
			<div style="width: 300px; color: gray;">成本</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b id="activity-cost">${a.cost eq "" ? "0" : a.cost}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 30px;">
			<div style="width: 300px; color: gray;">创建者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${a.createBy}&nbsp;&nbsp;</b><small style="font-size: 10px; color: gray;">${a.createTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 40px;">
			<div style="width: 300px; color: gray;">修改者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;" ><b id="activity-editBy">${a.editBy eq null ? "暂无修改" : a.editBy}&nbsp;&nbsp;</b><small style="font-size: 10px; color: gray;" id="activity-editTime">${a.editTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 50px;">
			<div style="width: 300px; color: gray;">描述</div>
			<div style="width: 630px;position: relative; left: 200px; top: -20px;"><b id="activity-description">${a.description eq "" ? "暂无描述" : a.description}</b></div>
			<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
	</div>
	
	<!-- 备注 -->
	<div id="remarkBody" style="position: relative; top: 30px; left: 40px;">
		<div class="page-header">
			<h4>备注</h4>
		</div>
		
<%--		<!-- 备注1 -->
		<div class="remarkDiv" style="height: 60px;">
			<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
			<div style="position: relative; top: -40px; left: 40px;" >
				<h5>哎呦！</h5>
				<font color="gray">市场活动</font> <font color="gray">-</font> <b>发传单</b> <small style="color: gray;"> 2017-01-22 10:10:10 由zhangsan</small>
				<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
				</div>
			</div>
		</div>
		
		<!-- 备注2 -->
		<div class="remarkDiv" style="height: 60px;">
			<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
			<div style="position: relative; top: -40px; left: 40px;" >
				<h5>呵呵！</h5>
				<font color="gray">市场活动</font> <font color="gray">-</font> <b>发传单</b> <small style="color: gray;"> 2017-01-22 10:20:10 由zhangsan</small>
				<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
				</div>
			</div>
		</div>--%>
		
		<div id="remarkDiv" style="background-color: #E6E6E6; width: 870px; height: 90px;">
			<form role="form" style="position: relative;top: 10px; left: 10px;">
				<textarea id="remark" class="form-control" style="width: 850px; resize : none;" rows="2"  placeholder="添加备注..."></textarea>
				<p id="cancelAndSaveBtn" style="position: relative;left: 737px; top: 10px; display: none;">
					<button id="cancelBtn" type="button" class="btn btn-default">取消</button>
					<button type="button" class="btn btn-primary" id="saveRemarkBtn">保存</button>
				</p>
			</form>
		</div>
	</div>
	<div style="height: 200px;"></div>
</body>
</html>