<%@page import="com.app.models.User"%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

	<h2> Welcome</h2>
	<%
		User user = (User)session.getAttribute("session_user");
	%>

	<h3>Name : <%= 	user.getName() %> </h3>
	<h3>Email : <%= 	user.getEmail() %> </h3>
	<h3>City : <%= user.getCity() %></h3>
	
	<a href="logout">Logout</a>
</body>
</html>