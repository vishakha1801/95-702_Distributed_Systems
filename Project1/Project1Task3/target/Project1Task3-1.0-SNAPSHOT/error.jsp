<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Error</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            text-align: center;
            padding: 50px;
            background-color: #f8d7da;
            color: #721c24;
        }
        .error-box {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            padding: 20px;
            display: inline-block;
            border-radius: 10px;
        }
        a {
            display: inline-block;
            margin-top: 20px;
            padding: 10px 15px;
            background-color: #721c24;
            color: #ffffff;
            text-decoration: none;
            border-radius: 5px;
        }
        a:hover {
            background-color: #5a1216;
        }
    </style>
</head>
<body>
<div class="error-box">
    <h2>Oops! An error occurred.</h2>
    <p><%= request.getAttribute("error") != null ? request.getAttribute("error") : "An unexpected error occurred." %></p>
    <a href="index.jsp">Go Back</a>
</div>
</body>
</html>
