<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">
<title>Infotrends Agent Mode</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">

<link rel="stylesheet" href="layui/css/layui.css">

<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/websocket.js"></script>
<script src="layui/layui.js"></script>
</head>
<body onbeforeunload="checktoLeave()"> 
	UserName : <input type="text" name="UserName" id="UserName" value="agent01">
	UserID : <input type="text" name="UserID" id="UserID" disabled>
	<br>
	<button type="submit" onclick="Login();" id="Login">Login</button>
	<button type="submit" onclick="Logout();" id="Logout" disabled>Logout</button>
	<br>
	<button type="submit" id="ready" onclick="ready();" disabled>ready</button>
	<button type="submit" id="notready" onclick="notready();" disabled>notready</button>
	<br>
	用戶ID : <span id="showUserID"> </span>
	<span id="status"> </span>
	<br>
	<button type="submit" onclick="online();" id="online" disabled>查詢在線人數</button>
	<button type="submit" onclick="grouponline();" id="grouponline" disabled>查詢群組在線人數</button>
	<br>
	<button type="submit" onclick="Clientonline();" id="Clientonline" disabled>查詢Client在線人數</button>
	<button type="submit" onclick="Agentonline();" id="Agentonline" disabled>查詢Agent在線人數</button>
	<span id="typeonline"> </span>
	<br>
	<span id="userdata"> </span>
	<br>
<!-- 	私訊 : <input type="text" name="sendto" id="sendto"> -->
<!-- 	<button type="submit" onclick="send();" id="send" disabled>send</button> -->
	<br>
	群組 : <input type="text" name="group" id="group" disabled>
	<button type="submit" onclick="addRoom();" id="addRoom" >加入群組</button>
	<button type="submit" onclick="leaveRoom();" id="leaveRoom" disabled>離開群組</button>
	<span id="groupstatus"> </span>
	Event : <span id="Event"> </span>
	<br>
	<button type="submit" onclick="AcceptEventInit();" id="AcceptEvent" disabled>Accept</button>
	<button type="submit" onclick="RejectEvent();" id="RejectEvent" disabled>Reject</button>
	<button type="submit" onclick="ReleaseEvent();" id="ReleaseEvent" disabled>Release</button>
	Event From : <input type="text" name="Eventform" id="Eventform" disabled>
	<br>
	message : <input type="text" id="message">
	<button type="submit" onclick="sendtoRoom();" id="sendtoGroup" disabled>send to Group</button>
	<button type="submit" onclick="test();" id="TestHere">TestHere</button>
	<span id="span"> </span>
	<br>三方/轉接: <br>
	線上請求Clients:
	<table border='2' id='clientList'>
		<tr><td>Client名稱</td><td>動作(Event)</td></tr>
		<tr><td>C1</td><td>Accept</td></tr>
		<tr><td>C2</td><td>Accept</td></tr>
	</table>
	<b>現有Group群組</b>: <span id="currGroups"></span>	
	<table border='2' id='groupListTable'>
	<span id="groupList"></span>
<!-- 		<tr><td>Group Id</td><td>成員</td></tr> -->
<!-- 		<tr><td>G1</td><td>C1,A1,A2</td></tr> -->
<!-- 		<tr><td>G2</td><td>C2,A2</td></tr> -->
	</table>	
	<br>訊息: <br>
	<span id="text"> </span>
</body>
</html>