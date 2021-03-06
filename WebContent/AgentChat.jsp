﻿﻿<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8" %>




<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Agent - Chat</title>
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
.form-group {
    margin-bottom: 10px;
}

#ChatLog {
    overflow-y: auto;
    overflow-x: hidden;
    border: 1px solid #d2dbe3;
    font-size: smaller;
    height: 460px;
}

#ChatDialog {
    overflow-y: auto;
    overflow-x: hidden;
    border: 1px solid #d2dbe3;
    font-size: smaller;
    height: 80px;
}
.marginDefault{
	margin: 5px;
}

.marginBottom {
	margin-bottom: 5px;
}

.nopadding {
   padding: 0 !important;
/*    margin: 0 !important; */
}

.nomargin {
	margin: 0px;
}

.center {
	margin: auto;
/* 	text-align: center; */
}

hr {
  border: 0;
  clear:both;
  display:block;
  width: 96%;               
  background-color:#aaaaaa;
  height: 1px;
}

<!-- add space vertically -->
.spacer5 { height: 5px; width: 100%; font-size: 0; margin: 0; padding: 0; border: 0; display: block; }
.spacer10 { height: 10px; width: 100%; font-size: 0; margin: 0; padding: 0; border: 0; display: block; }
.spacer15 { height: 15px; width: 100%; font-size: 0; margin: 0; padding: 0; border: 0; display: block; }
.spacer20 { height: 20px; width: 100%; font-size: 0; margin: 0; padding: 0; border: 0; display: block; }
.spacer25 { height: 25px; width: 100%; font-size: 0; margin: 0; padding: 0; border: 0; display: block; }
.spacer30 { height: 30px; width: 100%; font-size: 0; margin: 0; padding: 0; border: 0; display: block; }
.spacer35 { height: 35px; width: 100%; font-size: 0; margin: 0; padding: 0; border: 0; display: block; }
.spacer40 { height: 40px; width: 100%; font-size: 0; margin: 0; padding: 0; border: 0; display: block; }
.spacer45 { height: 45px; width: 100%; font-size: 0; margin: 0; padding: 0; border: 0; display: block; }
.spacer50 { height: 50px; width: 100%; font-size: 0; margin: 0; padding: 0; border: 0; display: block; }
.spacer100 { height: 100px; width: 100%; font-size: 0; margin: 0; padding: 0; border: 0; display: block; }
.spacer200 { height: 200px; width: 100%; font-size: 0; margin: 0; padding: 0; border: 0; display: block; }


.wrapTextBtn{
	white-space: normal;
}

td {
    padding: 15px;
}

</style>
</head>
<body onload="onloadFunctionAgent();">
    <div style="width:90%">
    	<!-- 標題列 -->
        <div class="row" style="margin-top:5px;">
            <div class="col-xs-8 col-sm-8 col-md-8 col-lg-8">
            		<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
						<div class="form-group">
						    <input type="text" id="Account" class="form-control" value="Holylin" placeholder="Account"/>
						</div>
		            </div>
		            <div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
						<div class="form-group">
						    <input type="text" id="Password" class="form-control" value="info@1111" placeholder="Password"/>
						</div>
		            </div>
		            <div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
						<div class="form-group">
						    <input type="text" id="UserID" class="form-control" value="" placeholder="UserID" disabled/>
						</div>	            
					</div>
		            <div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
						<div class="form-group">
						    <input type="text" id="UserName" class="form-control" value="" placeholder="UserName" disabled/>
						</div>
		            </div>
		            <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
<!-- 		                <button id="Login" class="btn btn-primary btn-sm" onclick="Login();">Login</button> -->
		                <button id="Login" class="btn btn-primary btn-sm" onclick="loginValidate()" value="">Login</button>
		                <button id="Logout" class="btn btn-primary btn-sm" onclick="Logout();" disabled>Logout</button>
		                <button id="account01" class="btn btn-info btn-sm" onclick="account01();"> account01 </button>
		                <button id="account02" class="btn btn-info btn-sm" onclick="account02();"> account02 </button>
		            </div>
		            
		            
		    </div>
            <div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">
                <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                	<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">
                		<button id="ready" class="btn btn-primary marginBottom" style="width:100px" disabled onclick="ready();">Ready</button>
                	</div>	                	
                	<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">
                		MaxRoomCount: 
                		<div id="maxRoomCount" class="marginBottom" style="width:100px">  </div>
                	</div>	                	
                </div>	
                <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                	<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">
                        <button id="notready" class="btn btn-primary" style="width:100px" disabled onclick="notready();">Not Ready</button>
                	</div>
                	<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">
                        <select id="reasonList" class="form-control">
                        		<option value="0">請選擇</option>
<!--                             <option value="reason01">reason01</option> -->
<!--                             <option value="reason02">reason02</option> -->
<!--                             <option value="reason03">reason03</option> -->
                        </select>
                	</div>
                       
                </div>
            </div>
        </div>
        <!-- 主頁 -->
        <div class="row" style="margin-top:5px;border-top: 1px solid #000000;">
        	<!-- 左方bar區塊(由index.jsp負責產生) -->
        	<!-- 中間區塊 -->
            <div class="col-xs-8 col-sm-8 col-md-8 col-lg-8">
            	<!-- Request list -->
            	<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 panel panel-default center nopadding">
<!-- <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 panel panel-default center nopadding"> -->
<!-- <div class="pre-scrollable col-xs-12 col-sm-12 col-md-12 col-lg-12 panel-body" id="userdata" style="height: 80px;"> -->
	                <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 panel-heading center">
	                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12" style="padding-left: 10px;">Request List: </div>
	                </div>
	                <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 panel-body center" style="height: 200px;">
						<div class="pre-scrollable row col-xs-12 col-sm-12 col-md-12 col-lg-12 panel panel-default center" style="height: 70%;">
							<table class="col-xs-12 col-sm-12 col-md-12 col-lg-12 table table-hover nomargin" id="requestTable">
								<thead>
									<tr>
										<th>種類</th>
										<th>請求Name</th>
										<th>其他(userdata)</th>
									</tr>
								</thead>
								<tbody id="requestTable_tbody">
<!-- 									<tr style="height: 60px;"> -->
<!-- 										<td >通話</td> -->
<!-- 										<td >Client01</td> -->
<!-- 										<td style=""> -->
<!-- 											<div class="pre-scrollable" style="max-height: 60px; overflow-x: hidden; max-width: 260px;"> -->
<!-- 											{"id":"ae56b50b-6280-4423-89f6-17a67f9c3ef4","Phone":"A123456789","searchtype":"A","service1":"service one","service2":"service two","pkey":"id","mapping":{"Message":{"Name":{"chiname":"姓名","sort":"2","engname":"Name"},"id":{"chiname":"身分證字號","sort":"3","engname":"id"},"birthday":{"chiname":"生日","sort":"4","engname":"birthday"},"Phone":{"chiname":"電話","sort":"1","engname":"Phone"},"Fax":{"chiname":"傳真","sort":"7","engname":"Fax"},"LevelValue":{"chiname":"客戶等級","sort":"9","engname":"LevelValue"},"Tel1":{"chiname":"住家電話","sort":"5","engname":"Tel1"},"CUSTNAM":{"chiname":"住址","sort":"10","engname":"CUSTNAM"},"Tel2":{"chiname":"手機號碼","sort":"6","engname":"Tel2"},"Mail":{"chiname":"電子郵件","sort":"8","engname":"Mail"}},"method":"get","step":109},"searchkey":"Phone","lang":"chiname"} -->
<!-- 											</div> -->
<!-- 										</td> -->
<!-- 									</tr> -->
								</tbody>
							</table>	
						</div> 
		                <div class="row">
		                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
		                        	<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
		            	                <button class="btn btn-primary marginDefault pull-right" id="Reject"  onclick="RejectEvent()" disabled>Reject</button>
		        	                    <button class="btn btn-primary marginDefault pull-right" id="Accept"  onclick="AcceptEventInit();" disabled>Accept</button>
		                        	</div>
		                    </div>
		                </div> 					
	                </div>
            	</div><!-- end of request list -->	

				<!-- Room Info -->
            	<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 panel panel-default center nopadding">
	                <div class="row col-xs-12 col-sm-12 col-md-12 col-lg-12 panel-heading center">
	                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12" style="padding-left: 10px;">Room Info: </div>	                	
	                </div>
	                <div class="row col-xs-12 col-sm-12 col-md-12 col-lg-12 panel-body center">
	                	<!-- room list & comment -->
	                	<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">
	                	<h4>room list:</h4>
                        <select id="roomList" class="form-control">
<!--                             <option value="roomID01">roomID01</option> -->
<!--                             <option value="roomID02">roomID02</option> -->
<!--                             <option value="roomID03">roomID03</option> -->
                        </select>
	                	</div>
	                	<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">
	                		<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
			                	<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">
			                		<h4>comment:</h4>
			                	</div>
			                	<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">
			                        <button id="leaveRoom" class="btn btn-primary" disabled onclick="leaveRoom(this.roomID);">離開房間</button>
			                	</div>	                	
	                		</div>
	                		<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
			                	<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 ">
			                        <input type="text" id="commentContent" class="form-control" value="" disabled/>
			                	</div>
			                	<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">
			                        <button id="commentSend" class="btn btn-primary" onclick="sendComment();" disabled>SEND</button>
			                	</div>	                		                		
	                		</div>
	                	</div>
						<div class='col-xs-12 col-sm-12 col-md-12 col-lg-12'><hr></div> <!-- 分隔線 -->

		                <!-- userdata -->
	 	            	<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
	 	            		<h4>Userdata:</h4>
							<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 panel panel-default center nopadding">
								<div class="pre-scrollable col-xs-12 col-sm-12 col-md-12 col-lg-12 panel-body" id="userdata" style="height: 80px;">
<!-- 									some log ... <br> some log ... <br> some log ... <br> some log ... <br> some log ... <br> -->
								</div>
							</div>
	 	            	</div>
						<div class='col-xs-12 col-sm-12 col-md-12 col-lg-12'><hr></div> <!-- 分隔線 -->	                	
		                
		                <!-- chatDialogue: content, msg, send-->
						<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12" id="chatDialogue">
							<h4>ChatDialogue:</h4>
								<div class="pre-scrollable col-xs-12 col-sm-12 col-md-12 col-lg-12 panel panel-default" id="chatroom" style="height: 80px;">
<!-- 									someone says .... <br>	someone says .... <br>	someone says .... <br>	someone says .... <br>	someone says .... <br> -->
								</div>
								<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 nopadding">
									<div class="col-xs-10 col-sm-10 col-md-10 col-lg-10 nopadding">
										<input class="form-control" id="message" disabled>
									</div>						
									<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
										<button class="btn btn-primary" id="sendToRoom" onclick="sendtoRoom(this.roomID);" disabled>SEND</button>
									</div>						
								</div>								
						</div>	 <!-- end of chatDialogue -->
						<div class='col-xs-12 col-sm-12 col-md-12 col-lg-12'><hr></div> <!-- 分隔線 -->						
						
						
		                <!-- 三方與轉接 -->
						<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
							<button class="btn btn-primary pull-right marginDefault" id="inviteTransfer" style="width:100px" disabled onclick="inviteAgentThirdParty(this.value)" value="transfer">轉接邀請</button>
							<button class="btn btn-primary pull-right marginDefault" id="inviteThirdParty" style="width:100px" disabled onclick="inviteAgentThirdParty(this.value)" value="thirdParty">三方邀請</button>
						</div>
						
	                </div>
				</div><!-- end of RoomInfo -->
            </div><!-- end of 中間區塊 -->
            <!-- 右方區塊 --> <!-- here -->
<!--             <div class="col-xs-4 col-sm-4 col-md-4 col-lg-4" > -->
           	<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4 panel panel-default center nopadding">
                <div class="row col-xs-12 col-sm-12 col-md-12 col-lg-12 panel-heading center">
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12" style="padding-left: 10px;">Event: </div>
                </div>
                <div class="row col-xs-12 col-sm-12 col-md-12 col-lg-12 panel-body center" style="height: 150px;">
	                <div>
	                    <button id="ClearLog" type="button" class="btn btn-primary">Clear</button>
	                </div>
	                <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 spacer10"></div>
	                <div class="pre-scrollable row col-xs-12 col-sm-12 col-md-12 col-lg-12 panel panel-default center" style="height: 70%;">
	                    <div id="text"></div>
	                </div>    
				</div>            
                
                <!-- 私訊 -->
            	<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 panel panel-default center nopadding">
	                <div class="row col-xs-12 col-sm-12 col-md-12 col-lg-12 panel-heading center">
	                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12" style="padding-left: 10px;">私訊: </div>
	                </div>
	                <div class="row col-xs-12 col-sm-12 col-md-12 col-lg-12 panel-body center">
	                	<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 ">
	                        <select id="agentList" class="form-control">
<!-- 		                        <option disabled selected value> -- select an agent -- </option> -->
<!-- 	                            <option value="agent01">agent01</option> -->
<!-- 	                            <option value="agent02">agent02</option> -->
<!-- 	                            <option value="agent03">agent03</option> -->
	                        </select>
	                	</div>
	                	<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 ">
	                		<button id="refreshAgentList_request" class="btn btn-primary" onclick="refreshAgentList_request(this.agentID);">REFRESH</button>
	                	</div>	                	
		                <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 spacer10"></div>
                		<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
		                	<div class="col-xs-9 col-sm-9 col-md-9 col-lg-9 nopadding">
		                        <input type="text" id="A2AContent" class="form-control" value=""/>
		                	</div>
		                	<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 nopadding">
		                	</div>
		                	<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2 nopadding">
		                        <button id="privateMsg" class="btn btn-primary" onclick="sendA2A(this.agentID);">SEND</button>
		                	</div>	                		                		
                		</div>	                		                	
	                </div>
                
                
                
                
            </div>
        </div><!-- end of 右方區塊 -->
    </div>
<input type='hidden' id="systemParam" value='${systemParam}' disabled>

</body>
</html>

<!-- for dynamically get hostname address -->
<script>
//EL test
// var systemParam = ;
// alert("systemParam: " + systemParam);
// $('#Login')[0].value = systemParam;
// alert("systemPraram.IMWebSocket.hostname: " + systemPraram.IMWebSocket.hostname);
</script>

