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

<script type="text/javascript" src="js/swfobject.js"></script> <!-- for IE8 websocket compatibility -->
<script type="text/javascript" src="js/web_socket.js"></script> <!-- for IE8 websocket compatibility -->
<script type="text/javascript" src="js/websocket-util.js"></script>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/websocket-agentLayim.js"></script>
<script src="layui/layui.js"></script>

 <!-- for IE8 websocket compatibility -->
<script type="text/javascript">
// Let the library know where WebSocketMain.swf is:
WEB_SOCKET_SWF_LOCATION = "WebSocketMain.swf";	
</script>

</head>
<body onbeforeunload="checktoLeave()" onload="onloadFunction()"> 
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
	<button type="submit" onclick="roomonline();" id="roomonline" disabled>查詢群組在線人數</button>
	<br>
	<button type="submit" onclick="Clientonline();" id="Clientonline" disabled>查詢Client在線人數</button>
	<button type="submit" onclick="Agentonline();" id="Agentonline" disabled>查詢Agent在線人數</button>
	<span id="typeonline"> </span>
	<br>
	請求ClientID: 
	<span id="clientID"></span>
	<br>
	<span id="userdata"> </span>
	<br>
<!-- 	私訊 : <input type="text" name="sendto" id="sendto"> -->
<!-- 	<button type="submit" onclick="send();" id="send" disabled>send</button> -->
	<br>
	群組 : <input type="text" name="group" id="group" disabled>
<!-- 	<button type="submit" onclick="addRoom();" id="addRoom" >加入群組</button> -->
	<button type="submit" onclick="leaveRoom();" id="leaveRoom" disabled>離開群組</button>
	<span id="groupstatus"> </span>
	Event : <span id="Event"> </span>
	<br>
	<button type="submit" onclick="AcceptEventInit();" id="AcceptEvent" disabled>Accept</button>
	<button type="submit" onclick="RejectEvent();" id="RejectEvent" disabled>Reject</button>
	<button type="submit" onclick="ReleaseEvent();" id="ReleaseEvent" disabled>Release(Leave this room)</button>
	Event From : <input type="text" name="Eventfrom" id="Eventfrom" disabled>
	<br>
	message : <input type="text" id="message">
	<button type="submit" onclick="sendtoRoom();" id="sendtoRoom">send to Group</button>
	<button type="submit" onclick="test();" id="TestHere">TestHere</button>
	<span id="span"> </span>
	<br>三方/轉接: <br>
<!-- 	<b>線上請求Clients:</b> -->
<!-- 	<table border='2' id='clientList'> -->
<!-- 		<tr><td>Client名稱</td><td>動作(Event)</td></tr> -->
<!-- 		<tr><td>C1</td><td>Accept</td></tr> -->
<!-- 		<tr><td>C2</td><td>Accept</td></tr> -->
<!-- 	</table> -->
	<b>線上Agents:</b>
	<button type="submit" onclick="Agentonline();" id="inviteAgentThirdParty">show Agent List</button>
	<table border='2' id='agentList' style="visibility: hidden;" >
		<tr>
			<td>Agent名稱</td>
			<td>AgentID</td>
			<td>動作(Event)</td>
			<td>動作(Event)</td>
		</tr>
		<tr>
			<td id="AgentName">A2</td>
			<td id="AgentID" value=""></td>
			<td><button type="submit" onclick="inviteAgentThirdParty(this.value);" value="thirdParty">邀請加入(三方)</button></td>
			<td><button type="submit" onclick="inviteAgentThirdParty(this.value);" value="transfer">邀請加入(轉接)</button></td>
		</tr>
	</table>
	<b>現有Room房間</b>: <span id="currRooms"></span>	
	<button type="submit" onclick="RefreshRoomList();"> Refresh room list </button>
	<table border='2' id='roomListTable'>
	<tr><th>RoomID</th><th>RoomMems</th><th>SendMsgContent</th><th>SendMsg</th><th>RoomContent</th><th>三方/轉接邀請</th></tr>
	
	</table>	
	<br>
	<b>受邀加入Room房間(三方)</b>: <span id="invitedRooms"  style="visibility: hidden;"></span>	
	<table border='2' id='invitedRoomListTable'>
		<tr>
			<td>RoomId</td>
			<td>邀請者</td>
			<td>類型</td>
			<td>動作(Event)</td>
			<td>動作(Event)</td>
		</tr>
		<tr>
			<td id="invitedRoomID" value=""></td>
			<td id="fromAgentID" value=""></td>
			<td id="inviteType" value=""></td>
			<td><button type="submit" onclick="responseThirdParty(this.value);" id="acceptThirdParty" value="accept">接受</button></td>
			<td><button type="submit" onclick="responseThirdParty(this.value);" id="rejectThirdParty" value="reject">拒絕</button></td>
		</tr>
		<tr> 
			<td colspan="4">
			房間成員: <span id="currUsers"></span><br>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<textarea rows="15" cols="40" id="chatContent"></textarea>
			</td>
			<td>
				Agent talks to Agent
				<button type="submit" onclick="sendA2A(this.value);" id="sendA2A" value="">send</button>
				<input type="text" name="chatAgentContent" id="chatAgentContent">
<!-- 				<textarea rows="15" cols="40" id="chatAgentContent"></textarea> -->
			</td>
			<td>
				<textarea rows="15" cols="40" id="chatAgentContentHistory"></textarea>
			</td>
<!-- 			<td colspan="2"> -->
<!-- 				<tr> -->
<!-- 					<td>Agent to Agent</td> -->
<!-- 				</tr> -->
<!-- 			</td>			 -->
		</tr>
	</table>	
	
	
	<br>訊息: <br>
	<span id="text"> </span>
</body>
</html>