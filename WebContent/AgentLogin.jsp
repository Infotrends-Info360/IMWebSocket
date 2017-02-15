<%-- <%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%> --%>
<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Agent</title>
<!-- bootstrap v3.3.6 -->
<script src="js/jquery.min.js"></script>
<link href="boostrap/bootstrap.css" rel="stylesheet" />
<link href="boostrap/bootstrap-theme.css" rel="stylesheet" />
<script src="boostrap/bootstrap.js"></script>

<script type="text/javascript" src="js/swfobject.js"></script> <!-- for IE8 websocket compatibility -->
<script type="text/javascript" src="js/web_socket.js"></script> <!-- for IE8 websocket compatibility -->
<script type="text/javascript" src="js/websocket-util.js"></script>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/websocket-agent.js"></script>

 <!-- for IE8 websocket compatibility -->
<script type="text/javascript">
// Let the library know where WebSocketMain.swf is:
WEB_SOCKET_SWF_LOCATION = "WebSocketMain.swf";	
</script>    
   <style>
       #ChatLog {
           overflow-y: auto;
           overflow-x: hidden;
           border: 1px solid #d2dbe3;
           font-size: smaller;
           height: 450px;
       }
.disabled {
    pointer-events:none; /* This makes it not clickable */
    opacity:0.2;         /* This grays it out to look disabled */
}        
       
   </style>
</head>
<body>
    <div class="container-fluid" style="margin-top: 0px;">
		<div class="row" style="border-bottom: 1px solid #c0c0c0;">
			<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 text-center">
				<h3>
					<label id="PageTitle"></label>
				</h3>
			</div>
		</div>        
        <div class="row" style="margin-top:0px;">
        <!-- UserData -->
            <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">
                <div class="row disabled" style="margin-top:20px;">
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                        <label>Server Url:</label>
                        <select id="ServerUrl" class="form-control">
                            <option value="http://192.168.10.47/applicationServer/signalr">Application Server 1</option>
                            <option value="http://localhost:59339/signalr">Application Server 2</option>
                        </select>
                        <!--<input type="text" id="ServerUrl" class="form-control" value="http://localhost:59339/signalr" />
                        <input type="text" id="ServerUrl" class="form-control" value="http://192.168.10.47/applicationServer/signalr" />-->
                    </div>
                </div>
                <div class="row disabled" style="margin-top:5px;">
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                        <button id="ButtonConnect" class="btn btn-primary btn-sm">Connect</button>
                        <button id="ButtonDisconnect" class="btn btn-primary btn-sm">Disconnect</button>
                    </div>
                </div>
                <div class="row">
                    <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 disabled">
		                <div class="row" style="margin-top:10px;">
		                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
		                        <label>Agent ID: </label>
		                    </div>
		                </div>
                        <div class="form-group">
                            <input type="text" id="UserID" class="form-control" value="" />
                        </div>
                    </div>
                    <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">
		                <div class="row" style="margin-top:10px;">
		                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
		                        <label>Agent Name: </label>
		                    </div>
		                </div>
                        <div class="form-group">
                            <input type="text" id="UserName" class="form-control" value="agent01" />
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 disabled">
                        <div class="form-group">
                            <label>Nick Name: </label><input type="text" id="NickName" class="form-control" value="1111" />
                        </div>
                    </div>
                </div>
                <div class="row disabled">
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                        <div class="form-group">
                            <label>DN: </label><input type="text" id="DN" class="form-control" value="6002" />
                       </div>
                    </div>
                </div>
                <div class="row disabled">
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                        <div class="form-group">
                            <label>Place ID: </label><input type="text" id="PlaceId" class="form-control" value="1001" />
                        </div>
                    </div>
                </div>
                <div class="row disabled" style="margin-top:10px;">
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                        <label>
                            <input type="checkbox" id="VoiceEnabled" value="voice" />
                            Voice
                        </label>
                        <label>
                            <input type="checkbox" id="ChatEnabled" value="chat" checked />
                            Chat
                        </label>
                        <label>
                            <input type="checkbox" id="EmailEnabled" value="email" />
                            Email
                        </label>
                    </div>
                </div>
                <div class="row">
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                        <button id="Login" class="btn btn-primary btn-sm" onclick="Login();">Login</button>
                        <button id="Logout" class="btn btn-primary btn-sm">Logout</button>
                    </div>
                </div>
            </div>
            <!-- Event -->
            <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6" style="border-left: 1px solid #000000;">
                <div>
                    <label>Event :</label>&nbsp;&nbsp;&nbsp;&nbsp;<button id="ClearLog" type="button" class="btn btn-sm btn-primary">Clear</button>
                </div>
                <div>
                    <div id="text"></div>
                </div>
            </div>
            </div>
    </div>
</body>
</html>
