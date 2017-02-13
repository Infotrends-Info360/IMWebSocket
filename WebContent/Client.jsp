<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<title>Client</title>
<!-- bootstrap v3.3.6 -->
<script src="js/jquery.min.js"></script>
<link href="boostrap/bootstrap.css" rel="stylesheet" />
<link href="boostrap/bootstrap-theme.css" rel="stylesheet" />
<script src="boostrap/bootstrap.js"></script>

<!-- websocket -->
<script type="text/javascript" src="js/swfobject.js"></script> <!-- for IE8 websocket compatibility -->
<script type="text/javascript" src="js/web_socket.js"></script> <!-- for IE8 websocket compatibility -->
<script type="text/javascript" src="js/websocket-util.js"></script>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/websocket-client.js"></script>
<script type="text/javascript"> <!-- for IE8 websocket compatibility -->
// Let the library know where WebSocketMain.swf is:
WEB_SOCKET_SWF_LOCATION = "WebSocketMain.swf";	
</script>

<style>
.center {
	margin: auto;
	text-align: center;
}

.left {
	margin: auto;
	text-align: left;
}

/* .chatDialogue{ */
/* 	max-height:100px; */
/* } */

.nopadding {
   padding: 0 !important;
   margin: 0 !important;
}

/* html, body {height: 100%;} */

.chatmaxHeight
{
    height: 400px;
}
.logmaxHeight
{
    height: 470px;
}​

.hidden
{
	visibility: hidden;
}


</style>
</head>
<body onload="onloadFunction()">
	<!-- 標題 -->
	<div class="row no-gutter" style="border-bottom: 1px solid #c0c0c0; width: 100%;"> <!-- width: 100% 讓螢幕寬度符合螢幕大小 -->
		<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 center">
			<h3>Client</h3>
		</div>
	</div>

	
	<div class="row" style="width: 100%;"> <!-- width: 100% 讓螢幕寬度符合螢幕大小 -->
		<!-- UserData -->
		<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 col-xs-3 panel panel-default nopadding"
			 >
			<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 panel-heading center nopadding">
				<h3>UserData:</h3>
			</div>
<!-- 			<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1"></div> -->
<!-- 			<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2"></div> -->
			<div class="col-xs-8 col-sm-8 col-md-8 col-lg-8 panel-body"
				 >
				<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 container">
					<b>UserName</b>: 
					<input type="text" id="UserName" value="A123456789"><br> 
					<b>UserID</b>:<br>
					<span id="UserID">default</span><br> 
					<b>Status:</b><br>
					<span id="Status">default</span><br>
					<b>RoomID:</b><br> 
					<span id="RoomID">default</span><br> 
					<b>AgentIDs</b>:<br> 
					<span id="AgentIDs">default</span><br> 
					<b>AgentNames:</b><br> 
					<span id="AgentNames">default</span><br> 
					<b>Event:</b><br> 
					<span id="Event">default</span><br>
					<br>
				</div>		
				<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">
					<button class="btn btn-primary btn-sm" id="openChat" onclick="Login();">openChat</button>
				</div>						
				<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">
					<button class="btn btn-primary btn-sm" id="closeChat" onclick="Logout();">closeChat</button>
				</div>	
			</div>	
		</div>
		<!-- Chat Dialogue -->
		<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 center hidden" id="chatDialogue">
			<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 container panel panel-default center nopadding ">
				<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 panel-heading nopadding">
					<h3>Chat Dialogue:</h3>
				</div>
						<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 container" style="padding-top: 20px;">
							<div class="col-xs-10 col-sm-10 col-md-10 col-lg-10">
								<input class="form-control" id="message">
							</div>						
							<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
								<button class="btn btn-primary btn-sm" id="sendToRoom" onclick="sendtoRoom();">SEND</button>
							</div>						
						</div>
				<div class="pre-scrollable col-xs-12 col-sm-12 col-md-12 col-lg-12 panel-body left chatmaxHeight" id="chatroom">
<!-- 					someone says .... <br> -->
<!-- 					someone says .... <br> -->
<!-- 					someone says .... <br> -->
<!-- 					someone says .... <br> -->
<!-- 					someone says .... <br> -->
<!-- 					someone says .... <br> -->
<!-- 					someone says .... <br> -->
<!-- 					someone says .... <br> -->
<!-- 					someone says .... <br> -->
<!-- 					someone says .... <br> -->
<!-- 					someone says .... <br>  -->
<!-- 					someone says .... <br> -->
<!-- 					someone says .... <br> -->
<!-- 					someone says .... <br> -->
<!-- 					someone says .... <br> -->
<!-- 					someone says .... <br> -->
<!-- 					someone says .... <br> -->
<!-- 					someone says .... <br> -->
<!-- 					someone says .... <br> -->
<!-- 					someone says .... <br> -->
<!-- 					someone says .... <br> -->
<!-- 					someone says .... <br> -->
				</div>
			</div> <!-- end of container -->
		</div>
		<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 center" id="chatDialogueReverse"></div>

		<!-- Log -->
		<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 panel panel-default center nopadding">
			<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 panel-heading nopadding">
			<h3>Log:</h3>
			</div>
			<div class="pre-scrollable col-xs-12 col-sm-12 col-md-12 col-lg-12 panel-body left logmaxHeight" id="text">
<!-- 				some log ... <br> some log ... <br> some log ... <br> some log ... <br> some log ... <br> -->
<!-- 				some log ... <br> some log ... <br> some log ... <br> some log ... <br> some log ... <br> -->
<!-- 				some log ... <br> some log ... <br> some log ... <br> some log ... <br> some log ... <br> -->
<!-- 				some log ... <br> some log ... <br> some log ... <br> some log ... <br> some log ... <br> -->
<!-- 				some log ... <br> some log ... <br> some log ... <br> some log ... <br> some log ... <br> -->
<!-- 				some log ... <br> some log ... <br> some log ... <br> some log ... <br> some log ... <br> -->
<!-- 				some log ... <br> some log ... <br> some log ... <br> some log ... <br> some log ... <br> -->
<!-- 				some log ... <br> some log ... <br> some log ... <br> some log ... <br> some log ... <br> -->
			</div>
		</div>
	</div> <!-- end of row -->

</body>
</html>
