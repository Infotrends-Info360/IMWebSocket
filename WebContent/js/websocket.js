var ws; // websocket
var UserName; // 使用者名稱
var UserID_g;
var RoomID; // 接通後產生Group的ID
var RoomIDList = []; // 接通後產生Group的ID List
var RoomIDLinkedList = new SinglyList(); // 接通後產生Group的ID Linked List
var RoomIDAgentToAgentList = new SinglyList(); // 三方邀請送出時,一併建立私訊
var AgentID; // Agent的ID
var AgentName; // Agent的名稱
var ClientID; // 服務的Client的ID // 有可能會需要也改成List
var ClientName; // 服務的Client的名稱 // 有可能會需要也改成List
var layim; // Layim
var layimswitch = false; // layim開關參數，判斷是否開啟layim的時機
var isonline = false; // 判斷是否上線的開關

// refresh或關閉網頁時執行
function checktoLeave() {
	var UserID = document.getElementById('UserID').value;
	event.returnValue = "確定要離開當前頁面嗎？";
	LeaveType(UserID);
	Logoutaction(UserID);
	leaveRoom(UserID);
}

// 連上websocket
function Login() {
	// 登入使用者
	UserName = document.getElementById('UserName').value;
	if (null == UserName || "" == UserName) {
		alert("請輸入UserName");
	} else {
		// 連上websocket
		console.log("window.location.hostname: " + window.location.hostname);
		var hostname = window.location.hostname;
		ws = new WebSocket('ws://' + hostname + ':8888');

		// 當websocket連接建立成功時
		ws.onopen = function() {
			console.log('websocket 打開成功');
		};
		// 當收到服務端的消息時
		ws.onmessage = function(e) {
			// e.data 是服務端發來的資料
			if ("{" == e.data.substring(0, 1)) {
				var obj = jQuery.parseJSON(e.data);
				// 接收到Client邀請chat的event
				if ("findAgentEvent" == obj.Event) {
					ClientName = obj.fromName;
					ClientID = obj.from;
					document.getElementById("AcceptEvent").disabled = false;
					document.getElementById("RejectEvent").disabled = false;
					document.getElementById("Eventform").value = obj.from;
					document.getElementById("Event").innerHTML = obj.Event;
					// 接收到group訊息
				} else if ("roommessage" == obj.Event) {
					var UserID = document.getElementById('UserID').value;
					// 判斷是否有開啟layim與是否為自己傳送的訊息
					if (true == layimswitch && obj.from != UserID) {
						// 將收到訊息顯示到layim上
						getclientmessagelayim(obj.message, obj.from,
								obj.username);
						// 將訊息顯示到測試訊息框
						document.getElementById("chatContent").innerHTML += (obj.message + "&#13;&#10");
						
					}
					document.getElementById("text").innerHTML += obj.username
							+ ": " + obj.message + "<br>";
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

					
					//在此做三方/轉接Demo:
					var UserID = document.getElementById('UserID').value;
					console.log("UserID: "+UserID);
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
						if (agentID != UserID){
							console.log("a new Agent : " + agentID);
							document.getElementById("AgentID").value = agentID;
							document.getElementById("AgentID").innerHTML = agentID;
							document.getElementById("AgentName").value = agentName;
							document.getElementById("AgentName").innerHTML = agentName;
						}
//					    document.getElementById("updateAvailable_" + a[i]).style.visibility = "visible";
					}
					
					/*
					 * var UserID = document.getElementById('UserID').value; var
					 * FromArray = obj.from.trim().split(","); var UsernameArray =
					 * obj.username.trim().split(","); for(var i=0;i<FromArray.length;i++){
					 * layui.use('layim', function(layim){ layim.addList({ type:
					 * 'friend' ,avatar:
					 * "http://tp2.sinaimg.cn/5488749285/50/5719808192/1"
					 * ,username: UsernameArray[i] ,groupid: 1 ,id: FromArray[i]
					 * ,remark: FromArray[i] }); }); }
					 */
					// 接收到Client離開群組的訊息
				} else if ("createroomId" == obj.Event) {
					// RoomID = 'G'+document.getElementById('UserID').value;
					RoomID = obj.roomId; // 之後要改成local variable
					var myRoomID = obj.roomId;
					// SinglyList.prototype.add();
					RoomIDLinkedList.add(myRoomID);
					// GroupIDLinkedList.prototype.add("test"); // 錯
					console.log("GroupIDLinkedList._length: "
							+ RoomIDLinkedList._length);
					document.getElementById("group").value = RoomID;
					document.getElementById("Event").innerHTML = obj.Event;

					AcceptEventAction();
					addRoom(myRoomID); // 改AcceptEvent()架構的目的在這
					updateStatusAction("Established", "Established");

					layuiUse01(); // 先暫時這樣隔開,之後有需要細改再看此部分

					// 取得狀態
					getUserStatus();

					document.getElementById("roomonline").disabled = false;
					document.getElementById("ReleaseEvent").disabled = false;
					document.getElementById("AcceptEvent").disabled = true;
					document.getElementById("RejectEvent").disabled = true;
					document.getElementById("leaveRoom").disabled = false;
					document.getElementById("sendtoRoom").disabled = false;

					// 接收到Agent狀態更新的訊息
				} else if ("getUserStatus" == obj.Event) {
					console.log("onMessage(): getUserStatus called");
					document.getElementById("status").innerHTML = "狀態: "
							+ obj.Status + "<br>Reason: " + obj.Reason;
					// 接收到找尋Client的UserData的訊息
				} else if ("searchuserdata" == obj.Event) {
					console.log("onMessage - searchuserdata event");
					document.getElementById("userdata").innerHTML = JSON
							.stringify(obj.userdata);
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
				} else if ("refreshRoomList" == obj.Event) {
					document.getElementById("UserID").value = obj.from;
					document.getElementById("Event").innerHTML = obj.Event;
					console.log(obj.Event + "***********************");
					var roomList = obj.roomList;
					var roomListToUpdate = document.getElementById("roomList");
//					console.log("roomList: "+obj.roomList[0]);
					for (var i in roomList) {
						console.log("roomList[i]" + roomList[i]);
						roomListToUpdate.innerHTML += "<br>" + "(" + i + ") - " +  roomList[i];
					}
					//JSONArray groupList = obj.groupList;
//					document.getElementById("roomList").innerHTML = "Test - new group list here!!!";
				} else if ("inviteAgentThirdParty" == obj.Event){
					console.log("received inviteAgentThirdParty event");
					var tmpRoomID = obj.roomID;
					var fromAgentID = obj.fromAgentID;
					var invitedAgentID = obj.invitedAgentID;
					var inviteType = obj.inviteType;
					
					document.getElementById("invitedRoomID").innerHTML = tmpRoomID;
					RoomID = tmpRoomID;
					RoomIDLinkedList.add(tmpRoomID);
					
					document.getElementById("fromAgentID").innerHTML = fromAgentID;
					document.getElementById("invitedRoomID").style.visibility = "visible";					
//					document.getElementById("agentList").style.visibility = "visible";
					
					document.getElementById("inviteType").innerHTML = inviteType;
					
					
					// 判斷要寄送私訊的對方是誰(只要不是自己就對了)
					console.log("fromAgentID: " + fromAgentID);
					console.log("invitedAgentID: " + invitedAgentID);
					document.getElementById("sendA2A").value = fromAgentID;
					document.getElementById("sendA2A").innerHTML += " to - " + fromAgentID;
//					if (fromAgentID != UserID_g){
//						document.getElementById("sendA2A").value = fromAgentID;
//						document.getElementById("sendA2A").innerHTML += " to - " + fromAgentID;
//
//					}else if (invitedAgentID != UserID_g){
//						document.getElementById("sendA2A").value = invitedAgentID;						
//						document.getElementById("sendA2A").innerHTML += " to - " + invitedAgentID;						
//					}
					
//					sendjson.put("Event", "inviteAgentThirdParty");
//					sendjson.put("roomID", roomID);
//					sendjson.put("fromAgentID", fromAgentID);
//					sendjson.put("invitedAgentID", invitedAgentID);
//					sendjson.put("fromAgentName", fromAgentName);
				} else if ("responseThirdParty" == obj.Event){
					document.getElementById("currUsers").innerHTML = obj.roomMembers;
//					var fromAgentID = obj.fromAgentID;
//					var invitedAgentID = obj.invitedAgentID;

				} else if ("privateMsg" == obj.Event){
					console.log("onMessage - privateMsg");
					console.log("onMessage - privateMsg" + obj.UserName + ": " + obj.text + "&#13;&#10");
					document.getElementById("chatAgentContentHistory").innerHTML += obj.UserName + ": " + obj.text + "&#13;&#10";
					
//					type : "message",
//					text : aMessage,
//					id : UserID,
//					UserName : UserName,
//					sendto : aSendto,
				}
			// 非指令訊息
			}else {
				document.getElementById("text").innerHTML += e.data + "<br>";
			}
			console.log("onMessage(): " + e.data);
		};

		// 當websocket關閉時
		ws.onclose = function() {
			console.log("websocket 連接關閉");
		};

		// 當出現錯誤時
		ws.onerror = function() {
			console.log("出現錯誤");
		};

		// var UserID = document.getElementById('UserID').value;
		var now = new Date();
		// 向websocket送出登入指令
		var msg = {
			type : "login",
			// id: UserID,
			UserName : UserName,
			ACtype : "Agent",
			channel : "chat",
			date : now.getHours() + ":" + now.getMinutes() + ":"
					+ now.getSeconds()
		};
		setTimeout(function() {
			// 發送消息
			ws.send(JSON.stringify(msg));

			setTimeout(function() {
				var UserID = document.getElementById('UserID').value;
				document.getElementById("showUserID").innerHTML = UserID;
				document.getElementById("status").innerHTML = "狀態: Login";
				document.getElementById("Login").disabled = true;
				document.getElementById("Logout").disabled = false;
				document.getElementById("online").disabled = false;

				// 向websocket送出登入Agent列表指令
				var UserID = document.getElementById('UserID').value;
				var now = new Date();
				var msg = {
					type : "typein",
					ACtype : "Agent",
					id : UserID,
					UserName : UserName,
					channel : "chat",
					date : now.getHours() + ":" + now.getMinutes() + ":"
							+ now.getSeconds()
				};
				setTimeout(function() {
					// 發送消息
					ws.send(JSON.stringify(msg));

					setTimeout(function() {
						addlayim();
						document.getElementById("ready").disabled = false;
						// document.getElementById("send").disabled = false;
						isonline = true;
					}, 500);
				}, 500);
			}, 500);
		}, 500);

	}

}

// 登出
function Logout() {
	// 關閉socket
	// ws.close()
	var UserID = document.getElementById('UserID').value;
	// 離開Agent列表
//	LeaveType(UserID);
	// 執行登出
	Logoutaction(UserID); // onClose()會全部清: Group, Type, User conn
	// 離開Group
//	leaveRoom(UserID);
	// 關閉上線開關
	isonline = false;

	document.getElementById("status").innerHTML = "狀態: Logout";
	document.getElementById("Logout").disabled = true;
	document.getElementById("Login").disabled = false;
	document.getElementById("online").disabled = true;
}

// 執行登出
function Logoutaction(UserID) {
	// 向websocket送出登出指令
	var now = new Date();
	var msg = {
		type : "Exit",
		// text: message,
		id : UserID,
		UserName : UserName,
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息
	ws.send(JSON.stringify(msg));
}

// 送出私訊
function send() {
	var sendto = document.getElementById('sendto').value;	
	var message = document.getElementById('message').value;
	send01(sendto,message);

}

function send01(aSendto,aMessage){
	var UserID = document.getElementById('UserID').value;
	// 向websocket送出私訊指令
	var now = new Date();
	var msg = {
		type : "message",
		text : aMessage,
		id : UserID,
		UserName : UserName,
		sendto : aSendto,
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};
	// 發送消息
	ws.send(JSON.stringify(msg));	
}

function sendA2A(aSendto){
	var msg = document.getElementById("chatAgentContent").value;
	console.log("sendA2A() - msg: " + msg);
	send01(aSendto,msg);
}


// 查詢線上人數
function online() {
	// 向websocket送出查詢線上人數指令
	var now = new Date();
	var msg = {
		type : "online",
		UserName : UserName,
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};
	// 發送消息
	ws.send(JSON.stringify(msg));
}

// 加入群組
function addRoom(aRoomID) {
	var UserID = document.getElementById('UserID').value;
	// 向websocket送出加入群組指令
	var now = new Date();
	var msg = {
		type : "addRoom",
		roomID : aRoomID,
		id : UserID,
		ACtype : "Agent",
		UserName : UserName,
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息
	ws.send(JSON.stringify(msg));

	document.getElementById("groupstatus").innerHTML = "加入" + RoomID + "群組";
	document.getElementById("leaveRoom").disabled = false;
	document.getElementById("addRoom").disabled = true;
	document.getElementById("roomonline").disabled = false;
}

// 離開群組
function leaveRoom(UserID) {
	// 向websocket送出離開群組指令
	var now = new Date();
	var msg = {
		type : "leaveRoom",
		roomID : RoomID,
		id : UserID,
		UserName : UserName,
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息
	ws.send(JSON.stringify(msg));

	document.getElementById("groupstatus").innerHTML = "離開" + RoomID + "群組";
	document.getElementById("leaveRoom").disabled = true;
	document.getElementById("roomonline").disabled = true;
}

// 送出訊息至群組
function sendtoRoom() {
	var message = document.getElementById('message').value;
	var UserID = document.getElementById('UserID').value;
	// 向websocket送出送至訊息群組指令
	var now = new Date();
	var msg = {
		type : "messagetoRoom",
		text : message,
		id : UserID,
		UserName : UserName,
		roomID : RoomID,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息
	ws.send(JSON.stringify(msg));
}

// 查詢群組線上人數
function roomonline() {
	// 向websocket送出查詢群組線上人數指令
	var now = new Date();
	var msg = {
		type : "roomonline",
		roomID : RoomID,
		UserName : UserName,
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息
	ws.send(JSON.stringify(msg));
}

// Agent準備就緒
function ready() {
	var UserID = document.getElementById('UserID').value;
	// 向websocket送出變更狀態至準備就緒指令
	var now = new Date();
	var updateAgentStatusmsg = {
		type : "updateStatus",
		ACtype : "Agent",
		id : UserID,
		UserName : UserName,
		status : "ready",
		reason : "ready",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息
	ws.send(JSON.stringify(updateAgentStatusmsg));

	// 取得狀態
	getUserStatus();

	document.getElementById("ready").disabled = true;
	document.getElementById("notready").disabled = false;
	document.getElementById("Clientonline").disabled = false;
	document.getElementById("Agentonline").disabled = false;
}

// 查詢client線上人數
function Clientonline() {
	// 向websocket送出查詢client線上人數指令
	var now = new Date();
	var msg = {
		type : "typeonline",
		ACtype : "Client",
		UserName : UserName,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};
	// 發送消息
	ws.send(JSON.stringify(msg));
}

// 查詢agent線上人數
function Agentonline() {
	// 向websocket送出查詢agent線上人數指令
	var now = new Date();
	var msg = {
		type : "typeonline",
		ACtype : "Agent",
		UserName : UserName,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};
	// 發送消息
	ws.send(JSON.stringify(msg));
}

// 離開Agent列表
function LeaveType(UserID) {
	// 向websocket送出離開Agent列表指令
	var now = new Date();
	var Clientmsg = {
		type : "typeout",
		ACtype : "Agent",
		id : UserID,
		UserName : UserName,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息
	ws.send(JSON.stringify(Clientmsg));
}

// 同意與Client交談
function AcceptEventInit() {
	// 向websocket送出產生groupId指令
	var now = new Date();
	var createroomIdmsg = {
		type : "createroomId",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息
	ws.send(JSON.stringify(createroomIdmsg));
}

function AcceptEventAction() {
	var UserID = document.getElementById('UserID').value;
	var Eventform = document.getElementById('Eventform').value;
	// 向websocket送出同意與Client交談指令
	var now = new Date();
	var msg = {
		type : "AcceptEvent",
		ACtype : "Agent",
		id : UserID,
		UserName : UserName,
		sendto : Eventform,
		roomID : RoomID,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};
	// 發送消息
	ws.send(JSON.stringify(msg));
}

// 拒絕交談
function RejectEvent() {
	var UserID = document.getElementById('UserID').value;
	var Eventform = document.getElementById('Eventform').value;
	// 向websocket送出拒絕交談指令
	var now = new Date();
	var msg = {
		type : "RejectEvent",
		ACtype : "Agent",
		id : UserID,
		UserName : UserName,
		sendto : Eventform,
		channel : "chat",
		// Event: "RejectEvent",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};
	// 發送消息
	ws.send(JSON.stringify(msg));

	document.getElementById("AcceptEvent").disabled = true;
	document.getElementById("RejectEvent").disabled = true;
}

// 關閉交談
function ReleaseEvent() {
	// 切換為未就緒
	// 更新狀態
	updateStatus("not ready","no reason");
	// 取得狀態
	getUserStatus();
	
	// 將group移除layim列表
	layui.use('layim', function(layim) {
		layim.removeList({
			type : 'group' // 或者group
			,
			id : RoomID
		// 好友或者群组ID
		});
	});
	
	// 離開群組
	leaveRoom(UserID); // 重點是這行
	document.getElementById("ReleaseEvent").disabled = true;	
	
	// 更新.jsp
	document.getElementById("status").innerHTML = "狀態: not ready";
	document.getElementById("ready").disabled = false;
	document.getElementById("notready").disabled = true;
	document.getElementById("Clientonline").disabled = true;
	document.getElementById("Agentonline").disabled = true;

}

// 取得Agent狀態
function getUserStatus() {
	// 向websocket送出取得Agent狀態指令
	var UserID = document.getElementById("UserID").value;
	var now = new Date();
	var Eventmsg = {
		type : "getUserStatus",
		ACtype : "Agent",
		id : UserID,
		UserName : UserName,
		channel : 'chat',
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};
	// 發送消息
	ws.send(JSON.stringify(Eventmsg));
}

// 傳送群組訊息至layim視窗上
function sendtoRoomonlay(text) {
	// 暫時保留此方法,以後若要讓Agent能同時開多個視窗,則不能再用RoomID此全域變數
	sendtoRoomonlay01(text, RoomID);
}

function sendtoRoomonlay01(text, aRoomID) {
	var UserID = document.getElementById('UserID').value;
	var now = new Date();
	// 組成傳送群組訊息至layim視窗上的JSON指令
	var msg = {
		type : "messagetoRoom",
		text : text,
		id : UserID,
		UserName : UserName,
		roomID : aRoomID,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息給WebSocket
	ws.send(JSON.stringify(msg));
}

// layim取得訊息
function getclientmessagelayim(text, UserID, UserName) {
	// 組成傳送群組訊息至layim視窗上的JSON指令
	obj = {
		username : UserName // 消息來源用戶名
		,
		avatar : './layui/images/git.jpg' // 消息來源使用者頭像
		,
		id : RoomID // 聊天視窗來源ID（如果是私聊，則是用戶id，如果是群聊，則是群組id）
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
	var UserID = document.getElementById('UserID').value;
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
				url : '/IMWebSocket/RESTful/LayimInit?username=' + UserName
						+ '&id=' + UserID + '&sign=' + UserID,
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
				var UserID = document.getElementById('UserID').value;
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

function updateStatusAction(aStatus, aReason) {
	var UserID = document.getElementById('UserID').value;
	var now = new Date();
	var updateAgentStatusmsg = {
		type : "updateStatus",
		ACtype : "Agent",
		id : UserID,
		UserName : UserName,
		status : aStatus,
		reason : aReason,
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息
	ws.send(JSON.stringify(updateAgentStatusmsg));
}

function layuiUse01() {
	// 叫用layim
	var UserID = document.getElementById('UserID').value;
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
														+ UserName
														+ '&id='
														+ UserID
														+ '&sign='
														+ UserID,
												data : {}
											}
											// 成員列表
											,
											members : {
												url : '/IMWebSocket/RESTful/LayimMembers'
														+ '?username='
														+ UserName
														+ '&id='
														+ UserID
														+ '&sign='
														+ UserID
														+ '&addusername='
														+ ClientName
														+ '&addid='
														+ ClientID
														+ '&addsign='
														+ ClientID,
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
											groupname : 'Client: ' + ClientName
													+ ', Agent: ' + UserName,
											id : RoomID,
											members : 0
										}).chat(
										{
											name : 'Client: ' + ClientName
													+ ', Agent: ' + UserName,
											type : 'group' // 群组类型
											,
											avatar : "./layui/images/git.jpg",
											id : RoomID // 定义唯一的id方便你处理信息
											,
											members : 0
										// 成员数，不好获取的话，可以设置为0
										});
					});
}

// 給Group ID List用 - 之後試著把這段獨立出去
// website:
// https://code.tutsplus.com/articles/data-structures-with-javascript-singly-linked-list-and-doubly-linked-list--cms-23392
function Node(data) {
	this.data = data;
	this.next = null;
}

function SinglyList() {
	this._length = 0;
	this.head = null;
}

SinglyList.prototype.add = function(value) {
	var node = new Node(value), currentNode = this.head;

	// 1st use-case: an empty list
	if (!currentNode) {
		this.head = node;
		this._length++;

		return node;
	}

	// 2nd use-case: a non-empty list
	while (currentNode.next) {
		currentNode = currentNode.next;
	}

	currentNode.next = node;

	this._length++;

	return node;
};

SinglyList.prototype.searchNodeAt = function(position) {
	var currentNode = this.head, length = this._length, count = 1, message = {
		failure : 'Failure: non-existent node in this list.'
	};

	// 1st use-case: an invalid position
	if (length === 0 || position < 1 || position > length) {
		throw new Error(message.failure);
	}

	// 2nd use-case: a valid position
	while (count < position) {
		currentNode = currentNode.next;
		count++;
	}

	return currentNode;
};

SinglyList.prototype.remove = function(position) {
	var currentNode = this.head, length = this._length, count = 0, message = {
		failure : 'Failure: non-existent node in this list.'
	}, beforeNodeToDelete = null, nodeToDelete = null, deletedNode = null;

	// 1st use-case: an invalid position
	if (position < 0 || position > length) {
		throw new Error(message.failure);
	}

	// 2nd use-case: the first node is removed
	if (position === 1) {
		this.head = currentNode.next;
		deletedNode = currentNode;
		currentNode = null;
		this._length--;

		return deletedNode;
	}

	// 3rd use-case: any other node is removed
	while (count < position) {
		beforeNodeToDelete = currentNode;
		nodeToDelete = currentNode.next;
		count++;
	}

	beforeNodeToDelete.next = nodeToDelete.next;
	deletedNode = nodeToDelete;
	nodeToDelete = null;
	this._length--;

	return deletedNode;
};

function updateStatus(aStatus, aReason){
	var UserID = document.getElementById('UserID').value;
	// 向websocket送出變更狀態至未就緒指令
	var now = new Date();
	var updateAgentStatusmsg = {
		type : "updateStatus",
		ACtype : "Agent",
		id : UserID,
		UserName : UserName,
		status : aStatus,
		reason : aReason,
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};
	// 發送消息
	ws.send(JSON.stringify(updateAgentStatusmsg));
}

function inviteAgentThirdParty(aInviteType){
	var myInvitedAgentID = document.getElementById("AgentID").value;
//	var UserID = document.getElementById('UserID').value;
	console.log("inviteAgentThirdParty() called - myInvitedAgentID: " + myInvitedAgentID);
	
	if (RoomID == null){
		console.log("there is no roomID for this agent");
		return;
	}
	console.log("inviteAgentThirdParty(): UserID_g: " + UserID_g);
	
	/**** 寄出邀請 *****/
	var inviteAgent3waymsg = {
			type : "inviteAgentThirdParty",
			ACtype : "Agent",
			roomID : RoomID, //先預設目前每個Agent最多也就只有一個RoomID,之後會再調整
			fromAgentID : UserID_g, // 使用全域變數
			invitedAgentID : myInvitedAgentID,
			fromAgentName : UserName,
			inviteType: aInviteType
		};
		// 發送消息
		ws.send(JSON.stringify(inviteAgent3waymsg));

	/**** 建立私訊 *****/
	document.getElementById("sendA2A").value = myInvitedAgentID;
	document.getElementById("sendA2A").innerHTML += " to - " + myInvitedAgentID;	


}

function responseThirdParty(aResponse){
	console.log("aResponse: " + aResponse);
	var roomID = document.getElementById("invitedRoomID").innerHTML;
	var myfromAgentID = document.getElementById("fromAgentID").innerHTML;
	var inviteType = document.getElementById("inviteType").innerHTML;
	var responseThirdPartyMsg = {
			type : "responseThirdParty",
			ACtype : "Agent",
			roomID : roomID, 
			fromAgentID : myfromAgentID, 
			invitedAgentID : UserID_g,
			response: aResponse,
			inviteType: inviteType
//			fromAgentName : UserName
		};
	// 發送消息
	ws.send(JSON.stringify(responseThirdPartyMsg));	
	
}

// 測試按鈕
function test() {
	console.log("test method called");
	// 切換為未就緒
	// notready();
	// var UserID = document.getElementById('UserID').value;
	// //向websocket送出變更狀態至party remove指令
	// var now = new Date();
	var testmsg = {
		type : "test"
	// ,
	// ACtype: "Agent",
	// id: UserID,
	// UserName: UserName,
	// status: "party remove",
	// reason: "no reason",
	// date: now.getHours()+":"+now.getMinutes()+":"+now.getSeconds()
	// date: Date.now()
	};

	// 發送消息
	ws.send(JSON.stringify(testmsg));
	//	
	// //向websocket送出關閉對談指令
	// var Eventform = document.getElementById('Eventform').value;
	// var Eventmsg = {
	// type: "ReleaseEvent",
	// ACtype: "Agent",
	// id: UserID,
	// UserName: UserName,
	// sendto: Eventform,
	// channel: "chat",
	// // Event: "ReleaseEvent",
	// date: now.getHours()+":"+now.getMinutes()+":"+now.getSeconds()
	// };
	// //發送消息
	// ws.send(JSON.stringify(Eventmsg));
	//	
	// //離開群組
	// leaveRoom(UserID);
	//	
	// document.getElementById("ReleaseEvent").disabled = true;
}
