<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<form action="record.do" method="post" enctype="multipart/form-data">
	<input type="hidden" name="service" value="add"/>
	<p>File:<input type="file" name="file"/></p>
	<p><input type="submit" value="Submit"/></p>
</form>
</body>
</html>