<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2018-02-07
  Time: 14:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page language="java" import="java.util.*" pageEncoding="utf-8" %>
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
