<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<html>
<head>
    <base href="<%=basePath%>">
    <title>error</title>
</head>
<body>
<h1>${errMsg}</h1>
<a href="workbench/clue/index.jsp">返回</a>
</body>
</html>
