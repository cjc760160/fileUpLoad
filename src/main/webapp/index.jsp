<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>文件上传</title>
</head>
<body>
<form action="${pageContext.request.contextPath}/servlet/UploadHandleServlet"
      enctype="multipart/form-data" method="post">
    上传用户：<input type="text" name="username"><br>
    上传文件1：<input type="file" name="file1"><br>
    上传文件2：<input type="file" name="file2"><br>
    <input type="submit" value="提交">

</form>
</body>
</html>--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page language="java"  pageEncoding="utf-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>文件下载显示页面</title>
</head>
<body>

<c:forEach var="me" items="${fileNameMap}">

    <c:url value="/servlet/DownLoadServlet" var="downurl">
        <c:param name="filename" value="${me.key}"></c:param>
    </c:url>
    ${me.value}<a href="${downurl}">下载</a>
    <br/>
</c:forEach>
</body>
</html>

