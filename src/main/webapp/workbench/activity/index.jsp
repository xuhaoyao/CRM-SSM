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

	<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
	<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
	<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>


	<script type="text/javascript">

	$(function(){

		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});

		$("#addBtn").click(function(){
			//首先 获取所有者下拉框的数据
			$.ajax({
				url : "workbench/activity/getUserList.do",
				type : "get",
				dataType : "json",
				success : function(data){
					var html = "";
					$.each(data,function(i,n){
						html += "<option value='"+n.id+"'>"+n.name+"</option>";
					})
					$("#create-owner").html(html);
					/*
						注意:下面的操作要写在success函数里面 不能写在success函数外面!!
							原因:ajax发送的异步请求,数据传到后台执行是比较慢的,如果下面的操作写在success函数
								外面,那么success函数还没来得及执行,
								$("#create-owner").val(id); 此行代码已经先执行了。
								因而显示不出默认下拉框默认选中的效果。

							解决方法:
								1、把ajax 的请求参数加上async:false  同步操作的话可以解决默认选中
								2、把代码放在success函数里面
					 */
					//在js中使用el表达式,el表达式要套用在字符串中
					var id = "${user.id}";
					//设置所有者下拉框的默认选中
					$("#create-owner").val(id);
					/*
                    操作模态窗口的方式:
                        需要操作的模态窗口的jquery对象，调用modal方法，传递参数
                        "show":打开模态窗口
                        "hide":关闭模态窗口
                      */
					$("#createActivityModal").modal("show");
				}
			})
		})

		$("#saveBtn").click(function(){
			$.ajax({
				url : "workbench/activity/save.do",
				data : {
					"owner" : $.trim($("#create-owner").val()),
					"name": $.trim($("#create-name").val()),
					"startDate": $.trim($("#create-startDate").val()),
					"endDate": $.trim($("#create-endDate").val()),
					"cost": $.trim($("#create-cost").val()),
					"description": $.trim($("#create-description").val())
				},
				type : "post",
				dataType : "json",
				success : function(data){
					if(data.success){
						alert("添加市场活动成功");
						pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
								,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

						/*
							注意:
								拿到了form表单的jquery对象
								对于表单的jquery对象,提供了submit()方法来提交表单
								但是表单的jquery对象没有提供reset()方法来重置表单
								这时得将jquery对象转换为原生dom对象
								$("#activityAddFrom").reset(); 无效方法
								jquery对象 --> dom对象
									jquery对象[下标]
								dom对象	  -->  jquery对象
									$(dom)
						 */
						//$("#activityAddFrom")[0].reset();

						//关闭模态窗口
						$("#createActivityModal").modal("hide");
					}else{
						alert(data.errMsg);
					}
				}
			})
		})

		<%
			String pageNo = request.getParameter("pageNo");
		%>
		if("<%=pageNo%>" != "null") {
			<%
				String pageSize = request.getParameter("pageSize");
				String name = request.getParameter("name");
				String owner = request.getParameter("owner");
				String startDate = request.getParameter("startDate");
				String endDate = request.getParameter("endDate");
			%>
			pageList("<%=pageNo%>", "<%=pageSize%>");   //上次操作的停留页面
			$("#search-name").val("<%=name%>");
			$("#search-owner").val("<%=owner%>");
			$("#search-startDate").val("<%=startDate%>");
			$("#search-endDate").val("<%=endDate%>");
		}
		else
			pageList(1,3);  //默认

		$("#searchBtn").click(function(){

			$("#hidden-name").val($.trim($("#search-name").val()));
			$("#hidden-owner").val($.trim($("#search-owner").val()));
			$("#hidden-startDate").val($.trim($("#search-startDate").val()));
			$("#hidden-endDate").val($.trim($("#search-endDate").val()));

			pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
					,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

		})

		//为全选的复选框绑定事件
		$("#choices").click(function(){
			/*
				prop() 方法设置或返回被选元素的属性和值。
				返回属性的值：
				$(selector).prop(property)
				设置属性和值：
				$(selector).prop(property,value)
			 */
			$("input[name=choice]").prop("checked",this.checked);
		})

/*
		此方法无效 因为动态生成的元素不能够以普通绑定事件的形式来进行操作。
		$("input[name=choice]").click(function(){
			alert(111);
		})*/

		/*
			动态生成的元素要用on方法的形式来触发事件
			语法:
				$(需要绑定元素的有效的外层元素).on(绑定的事件，需要绑定的jquery对象(可不写)，回调函数);
				这里有效 是指 不是动态生成的元素
		*/
		$("#activityBody").on("click",$("input[name=choice]"),function(){
			$("#choices").prop("checked",$("input[name=choice]").length === $("input[name=choice]:checked").length);
		})

		$("#deleteBtn").click(function(){
			var $obj = $("input[name=choice]:checked");
			if($obj.length == 0){
				alert("请先选择要删除的活动.");
			}
			else{
				if(confirm("确定要删除活动吗?")){
					var parm = "";
					for(var i = 0;i < $obj.length;i++){
						parm += "id=" + $obj[i].value;
						if(i != $obj.length - 1)
							parm += "&";
					}
					$.ajax({
						url : "workbench/activity/delete.do",
						data : parm,
						type : "post",
						dataType : "json",
						success : function(data){
							if(data.success){
								//删除之后,可能当前页全删了,这时候默认返回第一页
								pageList(	1
										,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

							}
							else{
								alert(data.errMsg);
							}
						}
					})
				}
			}
		})

		$("#editBtn").click(function(){

			var $obj = $("input[name=choice]:checked");
			if($obj.length==0)
				alert("请选择要修改的活动项");
			else if($obj.length > 1)
				alert("一次只能修改一条活动项.");
			else {
				var id = $obj.val();
				$.ajax({
					url : "workbench/activity/getUserListAndActivity.do",
					data : {
						"id" : id
					},
					type : "get",
					dataType : "json",
					success : function(data){
						/*
							data:
								{"users" : [{user1},{user2},...],"a" : {市场活动}}
						 */
						html = "<option></option>";
						$.each(data.users,function(i,n){
							html += "<option value='"+n.id+"'>"+n.name+"</option>";
						})
						$("#edit-owner").html(html);

						$("#edit-id").val(data.a.id);
						$("#edit-owner").val(data.a.owner);
						$("#edit-name").val(data.a.name);
						$("#edit-startDate").val(data.a.startDate);
						$("#edit-endDate").val(data.a.endDate);
						$("#edit-cost").val(data.a.cost);
						$("#edit-description").val(data.a.description);
					}
				})

				$("#editActivityModal").modal("show");
			}
		})

		//更新操作可以试着复制添加操作的代码
		$("#updateBtn").click(function(){
			$.ajax({
				url : "workbench/activity/update.do",
				data : {
					"id" : $("#edit-id").val(),
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
					if(data.success){
						pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
								,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

						/*
							注意:
								拿到了form表单的jquery对象
								对于表单的jquery对象,提供了submit()方法来提交表单
								但是表单的jquery对象没有提供reset()方法来重置表单
								这时得将jquery对象转换为原生dom对象
								$("#activityAddFrom").reset(); 无效方法
								jquery对象 --> dom对象
									jquery对象[下标]
								dom对象	  -->  jquery对象
									$(dom)
						 */

						//关闭模态窗口
						$("#editActivityModal").modal("hide");
					}else{
						alert(data.errMsg);
					}
				}
			})
		})

	});

	function pageList(pageNo,pageSize){
		/*
			每次刷新页面之前,将全选复选框的勾去掉
			不然会有bug:用户在全选之后,点击下一页,全选的勾还在
			注意复选框全选与取消全选的代码,是在id=activityBody的元素(一个tbody)中进行的,
			点击翻页操作,不会涉及到tbody,因此写的全选与取消全选的代码没有执行,
			故这里得加一句$("#choices").prop("checked",false);
			此处还用在:用户全选完点击删除,被选中的元素删掉了,故全选勾也应该去掉。
		 */
		$("#choices").prop("checked",false);

		var name = $.trim($("#search-name").val());
		var owner = $.trim($("#search-owner").val());
		var startDate = $.trim($("#search-startDate").val());
		var endDate = $.trim($("#search-endDate").val());
		$.ajax({
			url : "workbench/activity/pageList.do",
			data : {
				"pageNo" : pageNo,
				"pageSize" : pageSize,
				"name" : name,
				"owner" : owner,
				"startDate" : startDate,
				"endDate" : endDate
			},
			type : "get",
			dataType : "json",
			success : function(data){
				/*
					data:
						{"total" : 50,"dataList":[{市场活动1},{市场活动2}...]}
				 */
				var html = "";
				//var parm = "&pageNo="+pageNo+"&pageSize="+pageSize+"&name="+name+"&owner="+owner+"&startDate="+startDate+"&endDate="+endDate+"";
				$.each(data.dataList,function(i,n){
					html += '<tr class="active">';
					html += '<td><input name="choice" type="checkbox" value="'+n.id+'"/></td>';
					//注意下面一行 gotoDetail的参数形式
					html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="gotoDetail(\''+n.id+'\',\''+pageNo+'\',\''+pageSize+'\');">'+n.name+'</a></td>';
					html += '<td>'+n.owner+'</td>';
					html += '<td>'+n.startDate+'</td>';
					html += '<td>'+n.endDate+'</td>';
					html += '</tr>';
				})
				$("#activityBody").html(html);

				var totalPages = data.total % pageSize == 0 ? data.total / pageSize : parseInt(data.total / pageSize) + 1;
				$("#activityPage").bs_pagination({
					currentPage: pageNo, // 页码
					rowsPerPage: pageSize, // 每页显示的记录条数
					maxRowsPerPage: 10, // 每页最多显示的记录条数
					totalPages: totalPages, // 总页数
					totalRows: data.total, // 总记录条数

					visiblePageLinks: 3, // 显示几个卡片

					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,

					onChangePage : function(event, data){

						$("#search-name").val($.trim($("#hidden-name").val()));
						$("#search-owner").val($.trim($("#hidden-owner").val()));
						$("#search-startDate").val($.trim($("#hidden-startDate").val()));
						$("#search-endDate").val($.trim($("#hidden-endDate").val()));

						pageList(data.currentPage , data.rowsPerPage);

					}
				});

			}
		})
	}

	function gotoDetail(id,pageNo,pageSize){
		var name = $.trim($("#search-name").val());
		var owner = $.trim($("#search-owner").val());
		var startDate = $.trim($("#search-startDate").val());
		var endDate = $.trim($("#search-endDate").val());
		var parm = "&pageNo="+pageNo+"&pageSize="+pageSize+"&name="+name+"&owner="+owner+"&startDate="+startDate+"&endDate="+endDate+"";
		window.location.href="workbench/activity/detail.do?id="+id+""+parm+"";
	}
	
</script>
</head>
<body>

	<!--
		隐藏域 用于查询操作
	-->
	<input type="hidden" id="hidden-name"/>
	<input type="hidden" id="hidden-owner"/>
	<input type="hidden" id="hidden-startDate"/>
	<input type="hidden" id="hidden-endDate"/>

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form" id="activityAddFrom">
					
						<div class="form-group">
							<label class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-owner">

								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-name">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startDate">
							</div>
							<label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endDate">
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<!--
						data-dismiss="modal" 关闭模态窗口
					-->
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveBtn">保存</button>
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
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">
						<input type="hidden" id="edit-id"/>
					
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
					<button type="button" class="btn btn-primary" id="updateBtn">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control time" type="text" id="search-startDate" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control time" type="text" id="search-endDate">
				    </div>
				  </div>
				  
				  <button id="searchBtn" type="button" class="btn btn-default">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
					<!--
						模态窗口 原本写法 将id属性替换为data-toggle="modal" data-target="#createActivityModal"
						data-toggle="modal"：
							表示触发该按钮 将要打开一个模态窗口
						data-target
							表示要打开哪个模态窗口,通过#id的形式找到该窗口
						而这种写法,不能做另外的一些操作，只能单纯开一个模态窗口，因此写成按钮点击事件
					-->
				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="choices"/></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="activityBody">
<%--						<tr class="active">
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
							<td>2020-10-10</td>
							<td>2020-10-20</td>
						</tr>
                        <tr class="active">
                            <td><input type="checkbox" /></td>
                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
                            <td>2020-10-10</td>
                            <td>2020-10-20</td>
                        </tr>--%>
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;">
				<div id="activityPage"></div>
				<%--<div>
					<button type="button" class="btn btn-default" style="cursor: default;">共<b>50</b>条记录</button>
				</div>
				<div class="btn-group" style="position: relative;top: -34px; left: 110px;">
					<button type="button" class="btn btn-default" style="cursor: default;">显示</button>
					<div class="btn-group">
						<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
							10
							<span class="caret"></span>
						</button>
						<ul class="dropdown-menu" role="menu">
							<li><a href="#">20</a></li>
							<li><a href="#">30</a></li>
						</ul>
					</div>
					<button type="button" class="btn btn-default" style="cursor: default;">条/页</button>
				</div>
				<div style="position: relative;top: -88px; left: 285px;">
					<nav>
						<ul class="pagination">
							<li class="disabled"><a href="#">首页</a></li>
							<li class="disabled"><a href="#">上一页</a></li>
							<li class="active"><a href="#">1</a></li>
							<li><a href="#">2</a></li>
							<li><a href="#">3</a></li>
							<li><a href="#">4</a></li>
							<li><a href="#">5</a></li>
							<li><a href="#">下一页</a></li>
							<li class="disabled"><a href="#">末页</a></li>
						</ul>
					</nav>
				</div>--%>
			</div>
			
		</div>
		
	</div>
</body>
</html>