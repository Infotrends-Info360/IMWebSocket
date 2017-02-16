var ws_g; // websocket
var UserName_g; // 使用者名稱全域變數
var UserID_g; // 使用者ID全域變數
var RoomID_g; // 此為第一個加入的RoomID, 僅為開發過程使用, 不符合目前架構, 為過度開發期間保留的全域變數
var ClientID_g; // 現在準備要服務的Client的ID 
var ClientName_g; // 現在準備要服務的Client的名稱
var isonline_g = false; // 判斷是否上線的開關
//var RoomIDList = []; // 接通後產生Group的ID List
//var RoomIDLinkedList = new SinglyList(); // 接通後產生Group的ID Linked List

/** layui相關 **/
var AgentID; // Agent的ID // layim使用, 以及onlineinTYPE使用, 之後可考慮精簡
var AgentName; // Agent的名稱 // layim使用, 以及onlineinTYPE使用, 之後可考慮精簡
var layim; // Layim
var layimswitch = false; // layim開關參數，判斷是否開啟layim的時機


//onloadFunction
function onloadFunction(){
	// for debugging only
	console.log("onloadFunction() called");
	if (Math.random() < 0.5){
		document.getElementById('UserName').value = "agent01";
	}else{
		document.getElementById('UserName').value = "agent02";		
	}
}


// 連上websocket
function Login() {
	console.log("Agent Login() function");
	// 登入使用者
	UserName_g = document.getElementById('UserName').value;
	if (null == UserName_g || "" == UserName_g) {
		alert("請輸入UserName");
	} else {
		// 連上websocket
		console.log("window.location.hostname: " + window.location.hostname);
		var hostname = window.location.hostname;
		ws_g = new WebSocket('ws://' + hostname + ':8888');

		// 當websocket連接建立成功時
		ws_g.onopen = function() {
			console.log('websocket 打開成功');
			/** 登入 **/
			var now = new Date();
			// 向websocket送出登入指令
			var msg = {
				type : "login",
				// id: UserID_g,
				UserName : UserName_g,
				ACtype : "Agent",
				channel : "chat",
				date : now.getHours() + ":" + now.getMinutes() + ":"
						+ now.getSeconds()
			};			
			// 發送消息
			ws_g.send(JSON.stringify(msg));
			
			
			
		};
		// 當收到服務端的消息時
		ws_g.onmessage = function(e) {
			// e.data 是服務端發來的資料
			if ("{" == e.data.substring(0, 1)) {
				var obj = jQuery.parseJSON(e.data);
				// 接收到Client邀請chat的event
				if ("findAgentEvent" == obj.Event) {
					ClientName_g = obj.fromName;
					ClientID_g = obj.from;
					document.getElementById("AcceptEvent").disabled = false;
					document.getElementById("RejectEvent").disabled = false;
					document.getElementById("Eventfrom").value = obj.from;
					document.getElementById("Event").innerHTML = obj.Event;
					// 接收到group訊息
				} else if ("messagetoRoom" == obj.Event) {
					// 判斷是否有開啟layim與是否為自己傳送的訊息
					if (true == layimswitch && obj.id != UserID_g) {
						// 將收到訊息顯示到layim上
						getclientmessagelayim(obj.text, obj.id,
								obj.UserName);
						// 將訊息顯示到測試訊息框
						document.getElementById("chatContent").innerHTML += (obj.text + "&#13;&#10");
						
					}
					document.getElementById("text").innerHTML += obj.UserName
							+ ": " + obj.text + "<br>";
					// 接收到離開Agent列表的訊息
				} else if ("userExitfromTYPE" == obj.Event) {
					layui.use('layim', function(layim) {
						layim.removeList({
							type : 'friend' // 或者group
							,
							id : obj.from
						// 好友或者群组ID
						});
					});
					document.getElementById("Event").innerHTML = obj.Event;
					// 接收到查詢Agent or Client的訊息
				} else if ("onlineinTYPE" == obj.Event) {
					AgentID = obj.from;
					AgentName = obj.username;
					console.log('AgentID: ' + AgentID); // 2d51031b-26e1-4ff4-98ad-fc7301e6c885, 9c802b70-998a-4552-9bfd-afe1426104ea
					console.log('AgentName: ' + AgentName); // agent01, agent02

					
					console.log("UserID_g: "+UserID_g);
					var agentIDList = AgentID.split(",")
					var agentNameList = AgentName.split(",")
				    var i;
					
					if (agentIDList.length > 1){
						document.getElementById("agentList").style.visibility = "visible";
					}

					for (i = 0; i < agentIDList.length; i++) {
						var agentID = agentIDList[i].trim();
						var agentName = agentNameList[i].trim();
						console.log("a[i]:" + agentID);
						if (agentID != UserID_g){
							console.log("a new Agent : " + agentID);
							document.getElementById("AgentID").value = agentID;
							document.getElementById("AgentID").innerHTML = agentID;
							document.getElementById("AgentName").value = agentName;
							document.getElementById("AgentName").innerHTML = agentName;
						}
//					    document.getElementById("updateAvailable_" + a[i]).style.visibility = "visible";
					}
					
					// 接收到Client離開群組的訊息
				} else if ("AcceptEvent" == obj.Event){
					// 拿取資料 + 為之後建立roomList做準備
					RoomID_g = obj.roomID; // 之後要改成local variable
					var myRoomID = obj.roomID;
					document.getElementById("group").value = RoomID_g;
					document.getElementById("Event").innerHTML = obj.Event;
					
					// 更新狀態
					var myUpdateStatusJson = new updateStatusJson("Agent", UserID_g, UserName_g, "Established", "Established");
					ws_g.send(JSON.stringify(myUpdateStatusJson));
					// 使用layui
					layuiUse01(); // 先暫時這樣隔開,之後有需要細改再看此部分

					// 取得狀態
					getStatus();

					document.getElementById("roomonline").disabled = false;
					document.getElementById("ReleaseEvent").disabled = false;
					document.getElementById("AcceptEvent").disabled = true;
					document.getElementById("RejectEvent").disabled = true;
					document.getElementById("leaveRoom").disabled = false;
					document.getElementById("sendtoRoom").disabled = false;
					
					document.getElementById("clientID").innerHTML = ""; // 到最後一步才清掉這個
				} else if ("getUserStatus" == obj.Event) {
					console.log("onMessage(): getUserStatus called");
					document.getElementById("status").innerHTML = "狀態: "
							+ obj.Status + "<br>Reason: " + obj.Reason;
					// 接收到找尋Client的UserData的訊息
				} else if ("senduserdata" == obj.Event) {
					console.log("onMessage - senduserdata event");
					document.getElementById("userdata").innerHTML = JSON
							.stringify(obj.userdata);
					document.getElementById("clientID").innerHTML = obj.clientID;
					//  obj.clientID // **************
					// 接收到Agent or Client加入列表的訊息
				} else if ("userjointoTYPE" == obj.Event) {
					if ("Agent" == obj.ACtype) {
						// Agentonline();
						setTimeout(
								function() {
									layui
											.use(
													'layim',
													function(layim) {
														var UserID = document
																.getElementById('UserID').value;
														if ("undefined" != obj.from
																&& UserID != obj.from) {
															layim
																	.addList({
																		type : 'friend',
																		avatar : "http://tp2.sinaimg.cn/5488749285/50/5719808192/1",
																		username : obj.username,
																		groupid : 1,
																		id : obj.from,
																		remark : obj.from
																	});
														}

													});
								}, 1500);
						document.getElementById("Event").innerHTML = obj.Event;
					}
					// 接收到有人登入的訊息
				} else if ("userjoin" == obj.Event) {
					console.log("userjoin - UserID: " + obj.from);
					document.getElementById("UserID").value = obj.from;
					UserID_g = obj.from;
					
					document.getElementById("Event").innerHTML = obj.Event;					
					document.getElementById("showUserID").innerHTML = UserID_g;
					document.getElementById("status").innerHTML = "狀態: Login";
					document.getElementById("Login").disabled = true;
					document.getElementById("Logout").disabled = false;
					document.getElementById("online").disabled = false;

					/** 開啟layui **/
					addlayim();
					document.getElementById("ready").disabled = false;
					// document.getElementById("send").disabled = false;
					isonline = true;
					
				} else if ("refreshRoomList" == obj.Event) {
					document.getElementById("Event").innerHTML = obj.Event;
					console.log(obj.Event + "***********************");
					var roomList = obj.roomList;
					var memberList = obj.memberList;
//					var roomListToUpdate = null;
//					console.log("roomList: "+obj.roomList[0]);
					// remove All childeNodes:
					var myNode = document.getElementById("roomListTable");
					// 永遠把第二個child刪除,只保留<tr><th>RoomID</th><th>RoomMems</th><th>RoomContent</th><th>SendMsg</th></tr>
					while (myNode.lastChild && myNode.children.length > 1) {
						console.log("myNode.children.length: " + myNode.children.length);
					    myNode.removeChild(myNode.lastChild);
					}
					
					for (var i in roomList) {
						console.log("roomList[i]" + roomList[i]);
						console.log("memberList[i]" + memberList[i]);
//						roomListToUpdate += "<br>" + "(" + i + ") - " +  roomList[i];
						// dynamically create <tr><td></td>....</tr>
						// <tr><td>RoomID</td><td>RoomMems</td><td>RoomContent</td><td>SendMsg</td></tr>
					    var trNode = document.createElement("tr");
					    	// 放入roomID
					    var tdNode = document.createElement("td");
					    var tdTextnode = document.createTextNode(roomList[i]);
					    tdNode.appendChild(tdTextnode);
					    trNode.appendChild(tdNode);
					    	// 放入members
					    var tdNode = document.createElement("td");
					    var tdTextnode = document.createTextNode(memberList[i]);
					    tdNode.appendChild(tdTextnode);
					    trNode.appendChild(tdNode);
					    	// 放入RoomContent
					    
					    	// 放入SendMsgIputbox
					    	// <input type="text" id="message">
					    var tdNode = document.createElement("td");
					    var inputNode = document.createElement("input");
					    inputNode.setAttribute("type", "text");
					    inputNode.setAttribute("id", "roomMsg" + i);
					    tdNode.appendChild(inputNode);
					    trNode.appendChild(tdNode);
					    	// 放入SendMsgButton
					    	// <button type="submit" onclick="sendtoRoom();" id="sendtoRoom">send to Group</button>
					    var buttonNode = document.createElement("button");
					    buttonNode.setAttribute("type", "submit");
					    buttonNode.value = roomList[i];
					    buttonNode.index = "roomMsg" + i;
						buttonNode.onclick= function(){
					    	var msg = document.getElementById(this.index).value;
					    	sendtoRoom(this.value, msg);
					    	} ;
					    var buttonTextnode = document.createTextNode("Send to Group");
					    buttonNode.appendChild(buttonTextnode);
					    trNode.appendChild(buttonNode);
					    document.getElementById("roomListTable").appendChild(trNode);
					}
//					document.getElementById("roomList").innerHTML = roomListToUpdate;
					//JSONArray groupList = obj.groupList;
//					document.getElementById("roomList").innerHTML = "Test - new group list here!!!";
				} else if ("inviteAgentThirdParty" == obj.Event){
					console.log("received inviteAgentThirdParty event");
					var tmpRoomID = obj.roomID;
					var fromAgentID = obj.fromAgentID;
					var invitedAgentID = obj.invitedAgentID;
					var inviteType = obj.inviteType;
					
					document.getElementById("invitedRoomID").innerHTML = tmpRoomID;
					RoomID_g = tmpRoomID;
					
					document.getElementById("fromAgentID").innerHTML = fromAgentID;
					document.getElementById("invitedRoomID").style.visibility = "visible";					
//					document.getElementById("agentList").style.visibility = "visible";
					
					document.getElementById("inviteType").innerHTML = inviteType;
					
					
					// 判斷要寄送私訊的對方是誰(只要不是自己就對了)
					console.log("fromAgentID: " + fromAgentID);
					console.log("invitedAgentID: " + invitedAgentID);
					document.getElementById("sendA2A").value = fromAgentID;
					document.getElementById("sendA2A").innerHTML += " to - " + fromAgentID;

				} else if ("responseThirdParty" == obj.Event){
					document.getElementById("currUsers").innerHTML = obj.roomMembers;

				} else if ("privateMsg" == obj.Event){
					console.log("onMessage - privateMsg" + obj.UserName + ": " + obj.text + "&#13;&#10");
					document.getElementById("chatAgentContentHistory").innerHTML += obj.UserName + ": " + obj.text + "&#13;&#10";
					
				} else if ("removeUserinroom" == obj.Event){
					document.getElementById("currUsers").innerHTML = obj.roomMembers;
					alert(obj.result);
				} else if ("clientLeft" == obj.Event){
					// 在這邊進行一連串的善後處理
					alert("Client left : " + obj.from);
					
					ClientName_g = "";
					ClientID_g = "";
					document.getElementById("AcceptEvent").disabled = true;
					document.getElementById("RejectEvent").disabled = true;
					document.getElementById("Eventfrom").value = obj.from;
					document.getElementById("Event").innerHTML = obj.Event;
					document.getElementById("userdata").innerHTML = ""; // 清掉userdata
					document.getElementById("clientID").innerHTML = "";
					
					ready();
				}
			// 非指令訊息
			}else {
				document.getElementById("text").innerHTML += e.data + "<br>";
			}
			console.log("onMessage(): " + e.data);
		};

		// 當websocket關閉時
		ws_g.onclose = function() {
			console.log("websocket 連接關閉");
		};

		// 當出現錯誤時
		ws_g.onerror = function() {
			console.log("出現錯誤");
		};
	}

}

// 登出
function Logout() {
	// 關閉socket
	// ws.close()
	// 執行登出
	Logoutaction(); // onClose()會全部清: Group, Type, User conn
	// 關閉上線開關
	isonline = false;

	document.getElementById("status").innerHTML = "狀態: Logout";
	document.getElementById("Logout").disabled = true;
	document.getElementById("Login").disabled = false;
	document.getElementById("online").disabled = true;
}


// 執行登出
function Logoutaction() {
	// 向websocket送出登出指令
	var now = new Date();
	var msg = {
		type : "Exit",
		// text: message,
		id : UserID_g,
		UserName : UserName_g,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息
	ws_g.send(JSON.stringify(msg));
}

//refresh或關閉網頁時執行
function checktoLeave() {
	event.returnValue = "確定要離開當前頁面嗎？";
	Logoutaction();
}

//Agent準備就緒
function ready() {
	// 向websocket送出變更狀態至準備就緒指令
	var myUpdateStatusJson = new updateStatusJson("Agent", UserID_g, UserName_g, "ready", "ready");
	ws_g.send(JSON.stringify(myUpdateStatusJson));

	// 取得狀態
	getStatus();

	document.getElementById("ready").disabled = true;
	document.getElementById("notready").disabled = false;
	document.getElementById("Clientonline").disabled = false;
	document.getElementById("Agentonline").disabled = false;
}
// Agent尚未準備就緒
function notready() {
	// 更新狀態
	updateStatus("not ready","no reason");
	// 取得狀態
	getStatus();
	// 更新.jsp
	document.getElementById("status").innerHTML = "狀態: not ready";
	document.getElementById("ready").disabled = false;
	document.getElementById("notready").disabled = true;
}

//同意與Client交談
function AcceptEventInit() {
	// 一次將Agent與Client加入到room中
	var currClientID = document.getElementById("clientID").innerHTML;
		// 在此使用新的方法,將一個list的成員都加入到同一群組中
	var memberListToJoin = [];
	var mem1 = new myRoomMemberJsonObj(currClientID);
	var mem2 = new myRoomMemberJsonObj(UserID_g);
	memberListToJoin.push(mem1);
	memberListToJoin.push(mem2);
	addRoomForMany("none", memberListToJoin); // "none"是一個keyword, 會影響websocket server的邏輯判斷處理
	
	// 開啟ready功能:
	document.getElementById("ready").disabled = false;
	document.getElementById("notready").disabled = true;
}

// 拒絕交談
function RejectEvent() {
	var Eventfrom = document.getElementById('Eventfrom').value;
	// 向websocket送出拒絕交談指令
	var now = new Date();
	var msg = {
		type : "RejectEvent",
		ACtype : "Agent",
		id : UserID_g,
		UserName : UserName_g,
		sendto : Eventfrom,
		channel : "chat",
		// Event: "RejectEvent",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};
	// 發送消息
	ws_g.send(JSON.stringify(msg));

	document.getElementById("AcceptEvent").disabled = true;
	document.getElementById("RejectEvent").disabled = true;
}

// 關閉交談
function ReleaseEvent(aRoomID) {
	if (aRoomID === undefined) aRoomID = RoomID_g;
	// 切換為未就緒
	// 更新狀態
	updateStatus("not ready","no reason");
	// 取得狀態
	getStatus();
	
	// 將group移除layim列表
	layui.use('layim', function(layim) {
		layim.removeList({
			type : 'group' // 或者group
			,
			id : RoomID_g
		// 好友或者群组ID
		});
	});
	
	// 離開群組
	leaveRoom(aRoomID, UserID_g); // 重點是這行
	document.getElementById("ReleaseEvent").disabled = true;	
	
	// 更新.jsp
	document.getElementById("status").innerHTML = "狀態: not ready";
	document.getElementById("ready").disabled = false;
	document.getElementById("notready").disabled = true;
	document.getElementById("Clientonline").disabled = true;
	document.getElementById("Agentonline").disabled = true;

}

// 將多人同時加入房間
// aMemberListToJoin船入格式如下:
// [{"ID":"c8013217-2b20-46c4-ba2d-848fa430775e"},{"ID":"773bc9f4-3462-4360-8b11-e35be56b820a"}]
function addRoomForMany(aRoomID, aMemberListToJoin){
	if (aRoomID === undefined) aRoomID = RoomID_g; // 開發過渡期使用,之後會修掉
	//JSONObject jo = new JSONObject(aMemberListToJoin);
//	console.log(JSON.parse(aMemberListToJoin));
	console.log("addRoomForMany() - aMemberListToJoin"+ aMemberListToJoin);
	// 向websocket送出加入群組指令
	var now = new Date();
	var msg = {
		type : "addRoomForMany",
		roomID : aRoomID,
		memberListToJoin : aMemberListToJoin,
		ACtype : "Agent",
		UserName : UserName_g,
		channel: "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息
	ws_g.send(JSON.stringify(msg));

	document.getElementById("groupstatus").innerHTML = "加入" + RoomID_g + "群組";
	document.getElementById("leaveRoom").disabled = false;
//	document.getElementById("addRoom").disabled = true;
	document.getElementById("roomonline").disabled = false;	
}


// 離開房間
function leaveRoom(aRoomID, aUserID) {
	if (aUserID === undefined) aUserID = UserID_g;
	
	// 向websocket送出離開群組指令
	var now = new Date();
	var msg = {
		type : "leaveRoom",
		roomID : aRoomID,
		id : aUserID,
		UserName : UserName_g,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息
	ws_g.send(JSON.stringify(msg));

	document.getElementById("groupstatus").innerHTML = "離開" + RoomID_g + "群組";
	document.getElementById("leaveRoom").disabled = true;
	document.getElementById("roomonline").disabled = true;
}

//送出私訊
function send(aSendto,aMessage) {
	if (aSendto === undefined) aSendto = document.getElementById('sendto').value; 
	if (aMessage === undefined) aMessage = document.getElementById('message').value;
	
	// 向websocket送出私訊指令
	var now = new Date();
	var msg = {
		type : "message",
		text : aMessage,
		id : UserID_g,
		UserName : UserName_g,
		sendto : aSendto,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};
	// 發送消息
	ws_g.send(JSON.stringify(msg));
}
//送出Agent to Agent私訊
function sendA2A(aSendto){
	var msg = document.getElementById("A2AContent").value;
	console.log("sendA2A() - msg: " + msg);
	send(aSendto,msg);
}

// 送出訊息至群組
function sendtoRoom(aRoomID, aMessage){
	if (aRoomID === undefined) aRoomID = RoomID_g; // 開發過渡期使用,之後會修掉
	if (aMessage === undefined) aMessage = document.getElementById('message').value;
	
	var now = new Date();
	var msg = {
		type : "messagetoRoom",
		text : aMessage,
		id : UserID_g,
		UserName : UserName_g,
		roomID : aRoomID,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息
	ws_g.send(JSON.stringify(msg));	
}

// 取得Agent狀態
function getStatus() {
	// 向websocket送出取得Agent狀態指令
	var now = new Date();
	var Eventmsg = {
		type : "getUserStatus",
		ACtype : "Agent",
		id : UserID_g,
		UserName : UserName_g,
		channel : 'chat',
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};
	// 發送消息
	ws_g.send(JSON.stringify(Eventmsg));
}

// 只有在aStatus狀態為not ready時,才會傳入aReason參數
function updateStatus(aStatus, aReason){
	// 向websocket送出變更狀態至未就緒指令
	var now = new Date();
	var updateAgentStatusmsg = {
		type : "updateStatus",
		ACtype : "Agent",
		id : UserID_g,
		UserName : UserName_g,
		status : aStatus,
		reason : aReason,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};
	// 發送消息
	ws_g.send(JSON.stringify(updateAgentStatusmsg));
}

//邀請三方/轉接
// aInviteType可能傳入字串為: 
// 1. thirdParty(三方)
// 2. transfer(轉接)
function inviteAgentThirdParty(aInviteType, aRoomID ,aInvitedAgentID){
	if (aRoomID === undefined) aRoomID = RoomID_g; // 開發過渡期使用,之後會修掉
	if (aInvitedAgentID === undefined) aInvitedAgentID = document.getElementById("AgentID").value; // 開發過渡期使用,之後會修掉
	
	if (aRoomID == null){
		alert("開發模式: " + "inviteAgentThirdParty() - there is no roomID for this agent");
		return;
	}
	
	/**** 寄出邀請 *****/
	var inviteAgent3waymsg = {
			type : "inviteAgentThirdParty",
			ACtype : "Agent",
			roomID : aRoomID, 
			fromAgentID : UserID_g,
			invitedAgentID : aInvitedAgentID,
			fromAgentName : UserName_g,
			inviteType: aInviteType,
			channel : "chat"
		};
	// 發送消息
	ws_g.send(JSON.stringify(inviteAgent3waymsg));

	/**** 建立私訊 *****/
	document.getElementById("sendA2A").value = aInvitedAgentID;
	document.getElementById("sendA2A").innerHTML += " to - " + aInvitedAgentID;	


}

//回應三方/轉接
function responseThirdParty(aInviteType, aRoomID, aFromAgentID, aResponse){
	if (aInviteType === undefined) aInviteType = document.getElementById("inviteType").innerHTML; // 開發過渡期使用,之後會修掉
	if (aRoomID === undefined) aRoomID = document.getElementById("invitedRoomID").innerHTML; // 開發過渡期使用,之後會修掉
	if (aFromAgentID === undefined) aFromAgentID = document.getElementById("fromAgentID").innerHTML; // 開發過渡期使用,之後會修掉
	
	var responseThirdPartyMsg = {
			type : "responseThirdParty",
			ACtype : "Agent", 
			roomID : aRoomID, 
			fromAgentID : aFromAgentID, 
			invitedAgentID : UserID_g,
			response: aResponse,
			inviteType: aInviteType,
			channel : "chat"
//			fromAgentName : UserName
		};
	// 發送消息
	ws_g.send(JSON.stringify(responseThirdPartyMsg));	
	
}


function RefreshRoomList(){
	var msg = {
		    type: "refreshRoomList",
		    channel : "chat",	
		  };
	//發送消息 
	ws_g.send(JSON.stringify(msg));	
	
}

/** 現在未使用方法 **/
//查詢線上人數
function online() {
	// 向websocket送出查詢線上人數指令
	var now = new Date();
	var msg = {
		type : "online",
		UserName : UserName_g,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};
	// 發送消息
	ws_g.send(JSON.stringify(msg));
}

//查詢群組線上人數
function roomonline() {
	// 向websocket送出查詢群組線上人數指令
	var now = new Date();
	var msg = {
		type : "roomonline",
		roomID : RoomID_g,
		UserName : UserName_g,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息
	ws_g.send(JSON.stringify(msg));
}



// 查詢client線上人數
function Clientonline() {
	// 向websocket送出查詢client線上人數指令
	var now = new Date();
	var msg = {
		type : "typeonline",
		ACtype : "Client",
		UserName : UserName_g,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};
	// 發送消息
	ws_g.send(JSON.stringify(msg));
}

// 查詢agent線上人數
function Agentonline() {
	// 向websocket送出查詢agent線上人數指令
	var now = new Date();
	var msg = {
		type : "typeonline",
		ACtype : "Agent",
		UserName : UserName_g,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};
	// 發送消息
	ws_g.send(JSON.stringify(msg));
}


/** layim(以後會刪除方法) **/
// 傳送群組訊息至layim視窗上
function sendtoRoomonlay(text) {
	// 暫時保留此方法,以後若要讓Agent能同時開多個視窗,則不能再用RoomID此全域變數
	sendtoRoomonlay01(text, RoomID_g);
}

function sendtoRoomonlay01(aText, aRoomID) {
	// 組成傳送群組訊息至layim視窗上的JSON指令
	var myMessagetoRoomJson = new messagetoRoomJson("messagetoRoom", "Client", aText, UserID_g, UserName_g, aRoomID, "chat", "");
	// 發送消息給WebSocket	
	ws_g.send(JSON.stringify(myMessagetoRoomJson));
}

// layim取得訊息
function getclientmessagelayim(text, UserID, UserName) {
	// 組成傳送群組訊息至layim視窗上的JSON指令
	obj = {
		username : UserName // 消息來源用戶名
		,
		avatar : './layui/images/git.jpg' // 消息來源使用者頭像
		,
		id : RoomID_g // 聊天視窗來源ID（如果是私聊，則是用戶id，如果是群聊，則是群組id）
		,
		type : "group" // 聊天視窗來源類型，從發送消息傳遞的to裡面獲取
		,
		content : text // 消息內容
		// ,cid: 0 //消息id，可不傳。除非你要對消息進行一些操作（如撤回）
		// ,mine: false //是否我發送的消息，如果為true，則會顯示在右方
		// ,fromid: 100001 //消息來源者的id，可用於自動解決流覽器多視窗時的一些問題
		,
		timestamp : new Date().getTime()
	// 服務端動態時間戳記
	}

	// 發送消息給layim
	layim.getMessage(obj);
}

function addlayim() {
	Agentonline();

	console.log(AgentID);
	console.log(AgentName);

	layui.use('layim', function(elayim) {
		// 配置layim
		layim = layui.layim;
		// 基礎配置
		layim.config({
			// 初始化
			init : {
				url : '/IMWebSocket/RESTful/LayimInit?username=' + UserName_g
						+ '&id=' + UserID_g + '&sign=' + UserID_g,
				data : {}
			}
			// 簡約模式（不顯示主面板）
			// ,brief: true
			/*
			 * //查看群員介面 ,members: { url: './json/getMembers.json' ,data: {} }
			 */
			// ,uploadImage: {
			// url: '/IMWebSocket/RESTful/LayimUploadImage' //（返回的資料格式見下文）
			// //,type: '' //默認post
			// }
			// ,uploadFile: {
			// url: './json/uploadFile.json' //（返回的資料格式見下文）
			// //,type: '' //默認post
			// }
			// ,skin: ['aaa.jpg'] //新增皮膚
			// ,isfriend: false //是否開啟好友
			// ,isgroup: false //是否開啟群組
			// ,min: true //是否始終最小化主面板（默認false）
			,
			chatLog : './demo/chatlog.html' // 聊天記錄位址
			,
			find : './demo/find.html',
			copyright : true
		// 是否授權
		});

		// layim.setChatMin();

		// 監聽發送消息
		layim.on('sendMessage', function(data) {
			var To = data.to;
			console.log('sendMessage log');
			console.log(data);
			// 傳送群組訊息至layim視窗上
			sendtoRoomonlay(data.mine.content);
		});
		// 監聽線上狀態的切換事件
		layim.on('online', function(data) {
			console.log(data);
		});
		// layim建立就緒
		layim.on('ready', function(res) {
			console.log('Layim 建立就緒');
			console.log('AgentID: ' + AgentID);
			setTimeout(function() {
				if ("undefined" != AgentID.trim()) {
					var FromArray = AgentID.trim().split(",");
					var UsernameArray = AgentName.trim().split(",");
					for (var i = 0; i < FromArray.length; i++) {
						if ("undefined" != FromArray[i]) {
							layui.use('layim', function(layim) {
								layim.addList({
									type : 'friend',
									avatar : "./layui/images/git.jpg",
									username : UsernameArray[i],
									groupid : 1,
									id : FromArray[i],
									remark : FromArray[i]
								});
							});
						}
					}
				}
			}, 1500);
		});

		// 監聽查看群員
		layim.on('members', function(data) {
			console.log(data);
		});

		// 監聽聊天視窗的切換
		layim.on('chatChange', function(data) {
			console.log('chatChange log');
			console.log(data);
		});
		// 開啟傳送layim參數
		layimswitch = true;
	});
}

function layuiUse01() {
	// 叫用layim
	layui
			.use(
					'layim',
					function(layim) {
						// 基礎設置
						layim
								.config(
										{
											// 初始化
											init : {
												url : '/IMWebSocket/RESTful/LayimInit?username='
														+ UserName_g
														+ '&id='
														+ UserID_g
														+ '&sign='
														+ UserID_g,
												data : {}
											}
											// 成員列表
											,
											members : {
												url : '/IMWebSocket/RESTful/LayimMembers'
														+ '?username='
														+ UserName_g
														+ '&id='
														+ UserID_g
														+ '&sign='
														+ UserID_g
														+ '&addusername='
														+ ClientName_g
														+ '&addid='
														+ ClientID_g
														+ '&addsign='
														+ ClientID_g,
												data : {}
											},
											uploadImage : {
												url : 'http://ws.crm.com.tw:8080/JAXRS-FileUpload/rest/upload/images' // （返回的資料格式見下文）
											// ,type: '' //默認post
											},
											uploadFile : {
												url : 'http://ws.crm.com.tw:8080/JAXRS-FileUpload/rest/upload/files' // （返回的資料格式見下文）
											// ,type: '' //默認post
											}
										}).addList(
										{
											type : 'group',
											avatar : "./layui/images/git.jpg",
											groupname : 'Client: ' + ClientName_g
													+ ', Agent: ' + UserName_g,
											id : RoomID_g,
											members : 0
										}).chat(
										{
											name : 'Client: ' + ClientName_g
													+ ', Agent: ' + UserName_g,
											type : 'group' // 群组类型
											,
											avatar : "./layui/images/git.jpg",
											id : RoomID_g // 定义唯一的id方便你处理信息
											,
											members : 0
										// 成员数，不好获取的话，可以设置为0
										});
					});
}



// 測試按鈕
function test() {
	console.log("test method called");
	var testmsg = {
		    type: "test",
		    channel : "chat"
		  };
	//發送消息 
	ws_g.send(JSON.stringify(testmsg));
}

