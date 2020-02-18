<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>file list page</title>
</head>
<body>
	<form action="record.do" method="get">
		<input type="hidden" name="service" value="list"/>
		<p>file name:<input name="name"/><input type="submit" value="search"/></p>
	</form>
<a href="add.jsp">add file</a>

<hr>
<table>
	<tr>
		<th>ID</th>
		<th>name</th>
		<th>image</th>
		<th>download</th>
	</tr>
	<c:forEach items="${record}" var="record">
		<tr>
			<td>${record.id}</td>
			<td>${record.name}</td>
			<td>
				<img  src="${pageContext.request.contextPath}${record.url}" height="50px">
			</td>
			<td>
				<a href="record.do?service=download&id=${record.id}">下载</a>
			</td>
		</tr>
	</c:forEach>
</table>

</body>
</html>