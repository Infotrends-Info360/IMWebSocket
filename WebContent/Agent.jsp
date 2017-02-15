<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>
<!-- bootstrap v3.3.6 -->
<script src="js/jquery.min.js"></script>
<link href="boostrap/bootstrap.css" rel="stylesheet" />
<link href="boostrap/bootstrap-theme.css" rel="stylesheet" />
<script src="boostrap/bootstrap.js"></script>

<script type="text/javascript" src="js/websocket-agent.js" charset="utf-8"></script>


<!-- <script src="Scripts/jquery-2.1.4.js"></script> -->
<!-- <script src="Scripts/bootstrap.js"></script> -->
<!-- <script src="Scripts/OffCanvas/jasny-bootstrap.min.js"></script> -->
<!-- <link href="Scripts/OffCanvas/jasny-bootstrap.min.css" rel="stylesheet" /> -->
<!-- <link href="Scripts/OffCanvas/navmenu-push.css" rel="stylesheet" /> -->
<!-- <script src="Scripts/jquery.signalR-2.2.0.min.js"></script> -->
<!-- <script src="Scripts/server.js"></script> -->
<style>
.page-header {
	margin: 0px 20px 20px;
}
.disabled {
    pointer-events:none; /* This makes it not clickable */
    opacity:0.2;         /* This grays it out to look disabled */
}
<
div class ="container"> <h2>Form control: textarea </h2> <p>The form below contains a textarea for comments:
	</p> <form> <div class ="form-group"> <label for ="comment">Comment:
	</label> <textarea class ="form-control" rows ="5" id ="comment"> </textarea>
	</div> </form> </div>.vcenter {
	/*
            display: inline-block;
            vertical-align: middle;
            float: none;
            */
	margin-top: 20px;
}
</style>
</head>
<body onload="onloadFunction();">
	<div class="row" style="border-bottom: 1px solid #c0c0c0; width: 100%;">
		<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
			<h3>Info360 - Agent</h3>
		</div>
		<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 text-center">
			<h3>
				<label id="PageTitle"></label>
			</h3>
		</div>
		<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 vcenter">
			<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 disabled">
				<label class="checkbox-inline">
					<input type="checkbox" id="voiceStatus" disabled>Voice
				</label> 
				<label class="checkbox-inline">
					<input type="checkbox" id="chatStatus" disabled>Chat
				</label> 
				<label class="checkbox-inline">
					<input type="checkbox" id="emailStatus" disabled>Email
				</label>
			</div>
			<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
               	<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">
	               	<h4 class="pull-right">Status: </h4>
               	</div>
               	<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">
<!-- 	                <select id="status" class="form-control"> -->
<!-- 	                    <option value="status01">status01</option> -->
<!-- 	                    <option value="status02">status02</option> -->
<!-- 	                    <option value="status03">status03</option> -->
<!-- 	                </select> -->
                        <div class="form-group">
                            <input type="text" id="status" class="form-control" value="status01" />
                        </div>
               	</div>				
			</div>
		</div>
	</div>
	<div class="row" style="width: 100%;">
		<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
			<!--<div class="navmenu navmenu-default navmenu-fixed-left">-->
			<!--  offcanvas -->
			<ul class="nav navmenu-nav">
				<li><a href="#" class="Page">Login</a></li>
				<li><a href="#" class="Page">Chat</a></li>
				<li><a href="#" class="Page disabled">Voice</a></li>
				<li><a href="#" class="Page disabled">Email</a></li>
				<li><a href="#" class="Page disabled">Stat</a></li>
				<li><a href="#" class="Page disabled">Query</a></li>
				<!--<li class="divider"></li>
            <li class="dropdown-header">Status</li>
            <li><a href="#" class="Page">Status 1</a></li>
            <li><a href="#" class="Page">Status 2</a></li>
            <li><a href="../navmenu/">Slide in</a></li>-->
			</ul>
			<!--</div>-->
		</div>

		<!--<div class="navbar navbar-default navbar-fixed-top">
        <button type="button" class="navbar-toggle" data-toggle="offcanvas" data-target=".navmenu" data-canvas="body">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
    </div>-->
		<div class="col-xs-10 col-sm-10 col-md-10 col-lg-10"
			style="border-right: 1px solid #000000;">
			<!--<div class="page-header">
            <h1 id="header">Off Canvas Push Menu Template</h1>
        	</div>-->
			<!--<iframe frameborder="0" id="MainFrame"></iframe>-->
			<iframe frameborder="0" id="LoginFrame" src="AgentLogin.jsp"
				style="width: 100%"></iframe>
			<iframe frameborder="0" id="ChatFrame" src="AgentChat.jsp"
				style="width: 100%"></iframe>
<!-- 			<iframe frameborder="0" id="VoiceFrame" src="Pages/Voice.html" -->
<!-- 				style="width: 100%"></iframe> -->
<!-- 			<iframe frameborder="0" id="EmailFrame" src="Pages/Voice.html" -->
<!-- 				style="width: 100%"></iframe> -->
<!-- 			<iframe frameborder="0" id="StatFrame" src="Pages/Stat.html" -->
<!-- 				style="width: 100%"></iframe> -->
<!-- 			<iframe frameborder="0" id="QueryFrame" src="Pages/Query.html" -->
<!-- 				style="width: 100%"></iframe> -->
		</div>
	</div>

	<script type="text/javascript">
		var activeFrame = null; // 目前在畫面上的frame
		var AgentId = ""; // 目前登入的 Agent ID
		var DN = ""; // 目前登入的 DN
		var PlaceId = ""; // 目前登入的座位
		var NickName = ""; // 目前登入的 DN
		var IsLoggedin = false; // 是否已登入

		//  SignalR 連接狀態
		var hubState = 0; // 0: 'connecting', 1: 'connected', 2: 'reconnecting', 4: 'disconnected'

		//var isExapnd = true;

		//// 設定frame高度
		//function SetFrameHeight() {
		//    document.body.style.overflow = "hidden";
		//    //var viewportWidth = $(window).width();
		//    var viewportHeight = window.height; // $(window).height();
		//    document.body.style.overflow = "";
		//    //$("#mainFrame").css("min-height", (viewportHeight - $(".navbar").height() - $(".main-footer").height() - 40) + 'px'); // .$("#main").css("min-height"); // $("body").height();
		//    //$("#mainFrame").css("height", "auto !important");
		//    //var iframe = $("#mainFrame")[0];
		//    var iframe = parent.document.getElementById('MainFrame');
		//    var iframeWin = iframe.contentWindow || iframe.contentDocument.parentWindow;
		//    if (iframeWin.document.body) {
		//        iframe.height = iframeWin.document.documentElement.scrollHeight || iframeWin.document.body.scrollHeight;
		//        //iframe.height += 200;
		//    }
		//}

		//function setWidth() {
		//    if (isExapnd)
		//        return ($("body").css("width").replace("px", "") - $('.navmenu').css("width").replace("px", "")) + "px";
		//    else
		//        return ($("body").css("width").replace("px", "") - 100) + "px";
		//}

		// set channel status
		setStatus = function(voiceStatus, chatStatus, emailStatus) {
			if (voiceStatus)
				$('#voiceStatus').attr('checked', 'checked');
			else
				$('#voiceStatus').removeAttr('checked');

			if (chatStatus)
				$('#chatStatus').attr('checked', 'checked');
			else
				$('#chatStatus').removeAttr('checked');

			if (emailStatus)
				$('#emailStatus').attr('checked', 'checked');
			else
				$('#emailStatus').removeAttr('checked');
		};

		$(document).ready(
				function() {
					//$('.navbar-toggle').trigger('click');
					//$(".navbar-toggle").on("click", function () {
					//    //SetFrameHeight();
					//    isExapnd = !isExapnd;
					//    $("#MainFrame").attr({
					//        //'height': ($(this).attr('data-height') || document.body.scrollHeight),
					//        'width': setWidth()
					//    });
					//});
					//$('.navbar-toggle').hide();

					// 顯示 Login Frame
					var defaultPageId = "LoginFrame";
					$("iframe").hide();
					$("#" + defaultPageId).show(); // 為了開發方便,先轉成ChatFrame為預設頁面
					var height = $(this).attr('data-height')
							|| document.body.scrollHeight;
					height = screen.height * 0.8;
// 					alert("height: " + height);
// 					alert("screen.height: " + screen.height); // 720, 720, 720, 720
// 					alert("$(window).height(): " + $(window).height()); // 238, 684, 167, 537
// 					alert("$(document).height(): " + $(document).height()); // 292, 684, 292, 537 
// 					alert("document.body.scrollHeight: " + document.body.scrollHeight); // 292, 684, 292, 537
					
// 					$(window).height();   // returns height of browser viewport
// 					$(document).height(); // returns height of HTML document (same as pageHeight in screenshot)
// 					$(window).width();   // returns width of browser viewport
// 					$(document).width(); // returns width of HTML document (same as pageWidth in screenshot)

					//var width = $(this).attr('data-width') || setWidth();
					$("#" + defaultPageId).attr({  // 為了開發方便,先轉成ChatFrame為預設頁面
						'height' : height,
					//'width': width
					});

					// 點選 frame超連結 的事件
					$('a.Page').on(
							'click',
							function(e) {
								$("#header").text($(this).text());
								var height = $(this).attr('data-height')
										|| document.body.scrollHeight;
								//var width = $(this).attr('data-width') || setWidth();
			
								var height = screen.height * 0.8;
								// 顯示超連結的frame，設定屬性
								$("iframe").hide();
								$("#" + $(this).text() + "Frame").show();
								$("#" + $(this).text() + "Frame").contents()
										.find('#AgentId').text(AgentId);
								$("#" + $(this).text() + "Frame").attr({
									'height' : height,
								//'width': width
								});
								$("#PageTitle").text($(this).text());
// 								alert("height: " + height);
							});
				});

		/* ------------- SignalR Hub Functions --------------------------- */
// 		var hub = $.connection.voiceHub; // 指定 signalR server 的 hub class
// 		var IsManualStop = false; // 是否每隔5秒自動連接
// 		var IsStartComplete = false; // $.connection.hub.start() 動作是否完成
// 		var StartResult = false; // $.connection.hub.start() 結果(true:成功, false:失敗)
// 		var ConnectApiServerResult = false; // $.connection.hub.start() 結果(true:成功, false:失敗)
// 		$.connection.hub.logging = true; // 是否要顯示 signalR 事件在 browser's console 上
// 		$.connection.hub.stateChanged(connectionStateChanged); // 設定 signalR connection 的狀態監控事件

// 		// set timeout 120 seconds
// 		var t = new Date();
// 		t.setSeconds(t.getSeconds() + 120);
// 		$.connection.DeadlockErrorTimeout = t;

// 		var CheckApiServer = true; // Login時是否檢查 API Server
// 		var CurrentSignalR_Server_Url = ""; // 目前的 signalR server URL

// 		// signalR connection 的狀態監控事件
// 		function connectionStateChanged(state) {
// 			// 0: 'connecting', 1: 'connected', 2: 'reconnecting', 4: 'disconnected'
// 			hubState = state.newState;
// 			console.log('*** Hub State is changed to ' + hubState)
// 		}

// 		// 設定 signalR 錯誤事件記錄
// 		$.connection.hub.error(function(error) {
// 			console.log('*** SignalR error: ' + error)
// 		});

// 		// 連接 signalR server
// 		function StartSignalR(SignalR_Server_Url, SignalR_Server_Name,
// 				autoLogin) {
// 			// 若已連接 signalR server，則退出
// 			if (hubState == 1) {
// 				$("#LoginFrame")[0].contentWindow.addSimpleLog('Reconnect '
// 						+ SignalR_Server_Url + '.');
// 				$.connection.hub.stop();
// 				//return;
// 			}

// 			$.connection.hub.url = SignalR_Server_Url;
// 			$.connection.hub
// 					.start()
// 					.done(
// 							function() {
// 								CurrentSignalR_Server_Url = $.connection.hub.url;
// 								IsManualStop = false; // 避免每隔5秒自動連接
// 								$("#LoginFrame")[0].contentWindow
// 										.addSimpleLog('Connect '
// 												+ SignalR_Server_Name
// 												+ ' OK !!');
// 								StartResult = true; // 成功
// 								IsStartComplete = true; // 已完成
// 								console.log("*** Hub started !!")
// 								// 啟動 login
// 								if (autoLogin) {
// 									console.log("*** Auto Login !!")
// 									CheckApiServer = false;
// 									$("#LoginFrame")[0].contentWindow.Login();
// 								}
// 							})
// 					.fail(
// 							function(error) { // 連接失敗
// 								IsManualStop = false;
// 								$("#LoginFrame")[0].contentWindow
// 										.addSimpleLog('Connect '
// 												+ SignalR_Server_Name
// 												+ ' failed. Please check Server Url and Server Status.');
// 								StartResult = false;
// 								IsStartComplete = true;
// 								sleep(5000);
// 								//SignalR_Server_Url = $("#LoginFrame")[0].contentWindow.getNextServerUrl();
// 								//if (SignalR_Server_Url != null && SignalR_Server_Url != "") 
// 								//    StartSignalR(SignalR_Server_Url, $("#LoginFrame")[0].contentWindow.getNextServerName());
// 							});
// 		}

// 		// 切斷 SignalR 連接
// 		function StopSignalR() {
// 			// michael 20161021 move addSimpleLog from last line to first line, to show message FIRST 
// 			$("#LoginFrame")[0].contentWindow
// 					.addSimpleLog("Application Server Connection is closed !!");
// 			IsManualStop = true;
// 			$.connection.hub.stop();
// 		}

// 		// 斷線處理
// 		$.connection.hub
// 				.disconnected(function() {
// 					// 斷線時將通話中的資訊回存Server
// 					$("#VoiceFrame")[0].contentWindow
// 							.SaveNormalCall(CurrentSignalR_Server_Url
// 									.toLowerCase().replace("/signalr", "")
// 									+ '/Home/SaveUnabnormalCall');
// 					$("#VoiceFrame")[0].contentWindow
// 							.SaveTransferCall(CurrentSignalR_Server_Url
// 									.toLowerCase().replace("/signalr", "")
// 									+ '/Home/SaveUnabnormalCall');

// 					if (!IsManualStop) {
// 						$("#LoginFrame")[0].contentWindow
// 								.addSimpleLog("Application Server 已斷線, 隔5秒後自動重新連接 !!");
// 					}
// 					// 每隔5秒自動連接
// 					setTimeout(
// 							function() {
// 								if (!IsManualStop && hubState == 4) { // 非人工強制切斷，且目前為斷線
// 									// 重新連接
// 									var SignalR_Server_Url = $("#LoginFrame")[0].contentWindow
// 											.getNextServerUrl();
// 									if (SignalR_Server_Url != null
// 											&& SignalR_Server_Url != "") {
// 										$("#LoginFrame")[0].contentWindow
// 												.addSimpleLog($("#LoginFrame")[0].contentWindow
// 														.getNextServerName()
// 														+ " 重新連接中 ...");
// 										StartSignalR(
// 												SignalR_Server_Url,
// 												$("#LoginFrame")[0].contentWindow
// 														.getNextServerName(),
// 												true);
// 									} else { // 由第一個 application 連接
// 										$("#LoginFrame")[0].contentWindow
// 												.ResetServerNo();
// 										$("#LoginFrame")[0].contentWindow
// 												.addSimpleLog($("#LoginFrame")[0].contentWindow
// 														.getNextServerName()
// 														+ " 重新連接中 ...");
// 									}
// 									//IsManualStop = true;
// 								}
// 							}, 5000); // Restart connection after 5 seconds.
// 				});

// 		// sleep n 毫秒
// 		function sleep(milliseconds) {
// 			var start = new Date().getTime();
// 			for (var i = 0; i < 1e7; i++) {
// 				if ((new Date().getTime() - start) > milliseconds) {
// 					break;
// 				}
// 			}
// 		}

// 		$(document)
// 				.ready(
// 						function() {
// 							/*************************** hub.client. 定義 SignalR Server 呼叫的事件 **************/
// 							// signalR action 的執行結果接收事件
// 							hub.client.addSimpleLog = function(MediaType,
// 									Result, Message) {
// 								ConnectApiServerResult = Result;
// 								$("#" + MediaType + "Frame")[0].contentWindow
// 										.addSimpleLog(Message);
// 							}

// 							// signalR action 的執行結果接收事件
// 							hub.client.addLog = function(CommandObject,
// 									ShowDetailMessage) {
// 								if (CommandObject.Action == "CheckApiServerOK") {
// 									ConnectApiServerResult = (CommandObject.Result == "");
// 								}

// 								// login or logout，MediaType改為login，將結果記錄在login frame內
// 								if (CommandObject.Action == "Login"
// 										|| CommandObject.Action == "Logout") {
// 									//CommandObject.EventName = CommandObject.EventName + (CommandObject.MediaType == "Login" ? "" : "(" + CommandObject.MediaType + ")");
// 									if (CommandObject.EventName.indexOf("("
// 											+ CommandObject.MediaType + ")") < 0)
// 										CommandObject.EventName = CommandObject.EventName
// 												+ "("
// 												+ CommandObject.MediaType
// 												+ ")";
// 									$("#LoginFrame")[0].contentWindow
// 											.addLog(CommandObject);
// 								} else
// 									// 依 MediaType 值，將執行結果顯示在對應的 frame 內 
// 									$("#" + CommandObject.MediaType + "Frame")[0].contentWindow
// 											.addLog(CommandObject);

// 								if (CommandObject.Action == "Login") {
// 									IsLoggedin = true;
// 									//$("#VoiceFrame")[0].contentWindow.setLogin();
// 									//DN = CommandObject.DN;
// 								} else if (CommandObject.Action == "Logout") {
// 									IsLoggedin = false;
// 									//$("#VoiceFrame")[0].contentWindow.setLogout();
// 									//DN = "";
// 								}
// 							};

// 							//hub.client.addLog = function (MediaType, AgentId, Event, DN, ReferenceId, message) {
// 							//    $("#" + MediaType + "Frame")[0].contentWindow.addLog(AgentId, Event, DN, ReferenceId, message);
// 							//    if (Event == "Login") {
// 							//        IsLoggedin = true;
// 							//        $("#VoiceFrame")[0].contentWindow.setLogin();
// 							//    } else if (Event == "Logout") {
// 							//        IsLoggedin = false;
// 							//        $("#VoiceFrame")[0].contentWindow.setLogout();
// 							//    }
// 							//};

// 							// 依 MediaType 值，將交談內容顯示在對應的 frame 內 
// 							hub.client.addDialog = function(CommandObject) {
// 								$("#" + CommandObject.MediaType + "Frame")[0].contentWindow
// 										.addDialog(CommandObject);
// 							};

// 							// 增加一個連線(Interaction)
// 							hub.client.addInteraction = function(CommandObject) {
// 								$("#" + CommandObject.MediaType + "Frame")[0].contentWindow
// 										.addInteraction(CommandObject);
// 							};

// 							// 刪除一個連線(Interaction)
// 							hub.client.removeInteraction = function(
// 									CommandObject) {
// 								$("#" + CommandObject.MediaType + "Frame")[0].contentWindow
// 										.removeInteraction(CommandObject);
// 							};

// 							// 一般action指令執行錯誤的處理
// 							hub.client.errorHandler = function(CommandObject) {
// 								if (CommandObject.Action == "Login"
// 										|| CommandObject.Action == "Logout") {
// 									$("#LoginFrame")[0].contentWindow
// 											.errorHandler(CommandObject);
// 								} else
// 									// 依 MediaType 值，將執行結果顯示在對應的 frame 內 
// 									$("#" + CommandObject.MediaType + "Frame")[0].contentWindow
// 											.errorHandler(CommandObject);
// 							};

// 							// Voice action指令執行錯誤的處理
// 							hub.client.eventHandler_Voice = function(
// 									CommandObject, ShowDetailMessage) {
// 								if (CommandObject.Action == "Login"
// 										|| CommandObject.Action == "Logout") {
// 									$("#LoginFrame")[0].contentWindow
// 											.addLog(CommandObject);
// 								} else
// 									// 依 MediaType 值，將執行結果顯示在對應的 frame 內 
// 									$("#VoiceFrame")[0].contentWindow
// 											.eventHandler_Voice(CommandObject,
// 													ShowDetailMessage);
// 							};

// 							// Chat action指令執行錯誤的處理
// 							hub.client.eventHandler_Chat = function(
// 									CommandObject, ShowDetailMessage) {
// 								if (CommandObject.Action == "Login"
// 										|| CommandObject.Action == "Logout") {
// 									$("#LoginFrame")[0].contentWindow
// 											.addLog(CommandObject);
// 								} else
// 									// 依 MediaType 值，將執行結果顯示在對應的 frame 內 
// 									$("#ChatFrame")[0].contentWindow
// 											.eventHandler_Chat(CommandObject,
// 													ShowDetailMessage);
// 							};

// 							// Email action指令執行錯誤的處理
// 							hub.client.eventHandler_Email = function(
// 									CommandObject, ShowDetailMessage) {
// 								if (CommandObject.Action == "Login"
// 										|| CommandObject.Action == "Logout") {
// 									$("#LoginFrame")[0].contentWindow
// 											.addLog(CommandObject);
// 								} else
// 									// 依 MediaType 值，將執行結果顯示在對應的 frame 內 
// 									$("#ChatFrame")[0].contentWindow
// 											.eventHandler_Email(CommandObject,
// 													ShowDetailMessage);
// 							};

// 							// 交談內容處理 -- 多訊息 (以確保訊息先後順序)
// 							hub.client.addDialogArray = function(
// 									CommandObjectArray) {
// 								for (var i = 0; i < CommandObjectArray.length; i++) {
// 									var CommandObject = CommandObjectArray[i];
// 									$("#" + CommandObject.MediaType + "Frame")[0].contentWindow
// 											.addDialog(CommandObject);
// 								}
// 							};

// 							// 交談內容處理
// 							hub.client.addDialog = function(CommandObject) {
// 								$("#" + CommandObject.MediaType + "Frame")[0].contentWindow
// 										.addDialog(CommandObject);
// 							};

// 							// Email內容處理
// 							hub.client.displayEmailContent = function(
// 									CommandObject) {
// 								$("#" + CommandObject.MediaType + "Frame")[0].contentWindow
// 										.displayEmailContent(CommandObject,
// 												true);
// 								if (CommandObject.MediaType == "Email")
// 									$("#EmailFrame")[0].contentWindow
// 											.saveEmailContent(CommandObject);
// 							};

// 							// display Email Body
// 							// 2016/07/23 added StartDate
// 							//hub.client.displayEmailBody = function (MediaType, Action, EmailObject, Id, TheComment, StartDate, Cc, Bcc) {
// 							//    $("#" + MediaType + "Frame")[0].contentWindow.displayEmailBody(Action, EmailObject, Id, TheComment, StartDate, Cc, Bcc);
// 							hub.client.displayEmailBody = function(
// 									CommandObject) {
// 								$("#" + CommandObject.MediaType + "Frame")[0].contentWindow
// 										.displayEmailBody(CommandObject); // Action, EmailObject, Id, TheComment, StartDate, Cc, Bcc);
// 							};

// 							// display Email Body
// 							// 2016/07/23 added StartDate
// 							//hub.client.displayInteractionEmailBody = function (MediaType, Action, EmailObject, Id, TheComment, StartDate, Cc, Bcc) {
// 							//    $("#" + MediaType + "Frame")[0].contentWindow.displayInteractionEmailBody(Action, EmailObject, Id, TheComment, StartDate, Cc, Bcc);
// 							hub.client.displayInteractionEmailBody = function(
// 									CommandObject) {
// 								$("#" + CommandObject.MediaType + "Frame")[0].contentWindow
// 										.displayInteractionEmailBody(CommandObject); // Action, EmailObject, Id, TheComment, StartDate, Cc, Bcc);
// 							};

// 							// Agent狀態改變的處理
// 							hub.client.informCurrentAgentStatus = function(
// 									CommandObject) {
// 								$("#EmailFrame")[0].contentWindow
// 										.informCurrentAgentStatus(CommandObject);
// 							};

// 							// 在Workbin加入一個inInteraction的處理
// 							hub.client.addEmailWorkbinInteraction = function(
// 									Interactions) {
// 								$("#EmailFrame")[0].contentWindow
// 										.addEmailWorkbinInteraction(Interactions);
// 							};

// 							// 顯示 Contact list
// 							hub.client.displayContact = function(CommandObject) {
// 								$("#" + CommandObject.MediaType + "Frame")[0].contentWindow
// 										.displayContact(CommandObject,
// 												CommandObject.Action);
// 							};

// 							// 因同一Agent ID在其他地方Login，強迫離線
// 							hub.client.forceDisconnect = function() {
// 								// StopSignalR()，以避免產生多個 alert 
// 								StopSignalR();

// 								alert("因同一Agent ID在其他地方Login，強迫離線 !!")

// 								$("iframe").hide();
// 								$("#LoginFrame").show();
// 							};

// 						});
	</script>

</body>
</html>