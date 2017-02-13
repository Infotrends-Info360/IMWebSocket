var ws;
var layimswitch = false; // layim開關參數
var layim;
var UserName_g;
var roomID;
var contactID;
var isonline = false;
var startdate = new Date();
var ixnstatus = 0;
var ixnactivitycode;
var waittingAgent = false;
var waittingAgentID = "none";
var UserID_g;

/*********** type list (送出的) ************/
/* "login", "Exit", "online", 
 * "leaveRoom", "messagetoRoom", "roomonline",
 * "findAgent", "typeonline", "findAgentEvent",
 * "senduserdata", 'setinteraction', 
 * "entrylog", 
 * 
 * */

/*********** type list (接收的) ************/
/* "AcceptEvent", "RejectEvent", "findAgent", 
 * "messagetoRoom", "senduserdata", "userjoin",
 * "responseThirdParty", "removeUserinroom", 
 * 
 * */


// 控制例外離開動作(如: 刷新頁面、關閉視窗)
function checktoLeave() {
	console.log('leave');
	// 顯示是否要離開網頁
	event.returnValue = "Leave?";
	// 離開WebSocket Pool列表
	Logoutaction(UserID_g); // 這邊會全部清: Group, Type, User conn
}

// 連上websocket
function Login() {
	// startdate = new Date();
	UserName_g = document.getElementById('UserName').value;
	if (null == UserName_g || "" == UserName_g) {
		alert("請輸入UserName");
	} else {
		// 開啟WebSocket的通道
		console.log("window.location.hostname: " + window.location.hostname);
		var hostname = window.location.hostname;
		ws = new WebSocket('ws://' + hostname +':8888');
		// 當websocket連接建立成功時
		ws.onopen = function() {
			console.log('websocket 打開成功');
			/** 登入 **/
			var now = new Date();
			// 組成登入WebSocket Pool列表JSON指令
			var msg = {
				type : "login",
				UserName : UserName_g,
				ACtype : "Client",
				channel : "chat",
				date : now.getHours() + ":" + now.getMinutes() + ":"
						+ now.getSeconds()
			};

			// 發送消息給WebSocket
			ws.send(JSON.stringify(msg));			
			
			
		};
		// 當收到服務端的消息時
		ws.onmessage = function(e) {
			// e.data 是服務端發來的資料

			// 收到指令時是Json Object格式
			if ("{" == e.data.substring(0, 1)) {
				var obj = jQuery.parseJSON(e.data);
				// 收到同意交談指令
				if ("AcceptEvent" == obj.Event) {
					var myUpdateStatusJson = new updateStatusJson("Client", UserID_g, UserName_g, "chat", "chatting");
					ws.send(JSON.stringify(myUpdateStatusJson));
					waittingAgent = false;
					waittingAgentID = "none";

					// 控制前端傳值
					document.getElementById("RoomID").value = roomID;
					document.getElementById("Event").value = obj.Event;
					// 收到拒絕交談指令
				} else if ("RejectEvent" == obj.Event) {
					findingAgent(UserID_g, UserName_g);
					// 控制前端傳值
					document.getElementById("Event").value = obj.Event;
					document.getElementById("Eventfrom").value = obj.from;
					// 收到尋找Agent的指令
				} else if ("findAgent" == obj.Event) {
					// 寫入log
					// 增加此判斷式,避免一直呼叫senduserdata方法
					if (contactID == null){
						senduserdata(UserID_g, UserName_g, obj.Agent); 
					}

					if ("null" == obj.Agent || null == obj.Agent) {
						if (isonline) {
							console.log(UserName_g + " is looking for an agent ... ");
							findingAgent(UserID_g, UserName_g);
						}
					} else {
						// 控制前端傳值
						// RoomID = 'G'+obj.Agent;
						document.getElementById("AgentIDs").innerHTML = obj.Agent + " ";
						// document.getElementById("group").value = RoomID;
						document.getElementById("Event").innerHTML = obj.Event;
//						document.getElementById("Eventfrom").value = obj.from;
//						var UserID = document.getElementById('UserID').value;
						
						waittingAgent = true;
						waittingAgentID = obj.Agent;
						// 寫入log(告知雙方彼此的資訊)
						senduserdata(UserID_g, UserName_g, obj.Agent);
						console.log("senduserdata done ******************* ");						

						
						// 找尋Agent
						find();
					}
					// 收到群組訊息
				} else if ("messagetoRoom" == obj.Event) {
//					var UserID = document.getElementById('UserID').value;
					// 判斷是否有開啟layim與是否為自己傳送的訊息
					if (true == layimswitch && obj.id != UserID_g) {
						// 將收到訊息顯示到layim上
						getmessagelayim(obj.text, obj.id, obj.UserName);
					}
					// 控制前端傳值
					document.getElementById("text").innerHTML += obj.UserName
							+ ": " + obj.text + "<br>";

				} else if ("senduserdata" == obj.Event) {
					if (obj.userdata.SetContactLog != null){
						contactID = obj.userdata.SetContactLog.contactID;
						setinteractionDemo(ixnstatus, ixnactivitycode);						
					}else{
						console.log("senduserdata - " + "contactID not found! ");
					}
					console.log("senduserdata done from WS ******************* ");	
					// alert(obj.userdata.SetContactLog.contactID);
					// document.getElementById("userdata").innerHTML =
					// JSON.stringify(obj.userdata);
				} else if ("userjoin" == obj.Event) {
					// 控制前端傳值
//					document.getElementById("UserID").value = obj.from;
					document.getElementById("UserID").innerHTML = obj.from;
					document.getElementById("Event").innerHTML = obj.Event;
					UserID_g = obj.from;
					/** 成功登入後,開始找尋Agent **/
					var now = new Date();
					// 組成找尋Agent JSON指令
					var findAgentmsg = {
						type : "findAgent",
						id : UserID_g,
						UserName : UserName_g,
						channel : "chat",
						// Event: "findAgent",
						date : now.getHours() + ":" + now.getMinutes() + ":"
								+ now.getSeconds()
					};

					// 發送消息給WebSocket
					ws.send(JSON.stringify(findAgentmsg));
					
					/** 寫入Log **/
					now = new Date();
					// 組成寫入entry log指令
					var entrylogmsg = {
						type : "entrylog",
						userid : UserID_g,
						username : UserName_g,
						ipaddress : '127.0.0.1',
						browser : 'IE',
						platfrom : 'Windows',
						channel : 'Web', // 使用管道
						language : 'chiname',
						enterkey : 'Phone'
					};

					// 發送消息給WebSocket
					ws.send(JSON.stringify(entrylogmsg));
					
					/** 更新前端資料 **/
					document.getElementById("status").innerHTML = "Login";
					document.getElementById("openChat").disabled = true;
					document.getElementById("closeChat").disabled = false;

					isonline = true;
					
				}  else if ("responseThirdParty" == obj.Event){
					document.getElementById("currRoomID").innerHTML = obj.roomID;
					document.getElementById("currUsers").innerHTML = obj.roomMembers;
					
				}  else if ("removeUserinroom" == obj.Event){
					document.getElementById("currRoomID").innerHTML = obj.roomID;
					document.getElementById("currUsers").innerHTML = obj.roomMembers;
					alert(obj.result);
				}
			} else {
				// 控制前端傳值
				document.getElementById("text").innerHTML += e.data + "<br>";
			}
			console.log("onMessage() - " + e.data);
		};// end of ws.onMessage()

		// 當websocket關閉時
		ws.onclose = function() {
			console.log("websocket 連接關閉");
			// ixnstatus = 3;
			// ixnactivitycode = "異常離開: websocket unlink";
			// checktoLeave();
		};

		// 當websocket出現錯誤時
		ws.onerror = function() {
			console.log("出現錯誤");
		};
	}

}

// 離開WebSocket
function Logout() {
	// 關閉socket
	// ws.close()
	isonline = false; // 給"Clientclosegroup" -> .java - "Clientclosegroup" -> .js - ReleaseEvent() -> notready -> "Agentclosegroup" -> .java -> client.js - if(isonlone){} 
						// 也給"ReleaseEvent"用
	// 離開WebSocket Pool列表
	Logoutaction(UserID_g); // 這邊會全部清: Group, Type, User conn
	// 控制前端傳值
	document.getElementById("status").innerHTML = "Logout";
	document.getElementById("Logout").disabled = true;
	document.getElementById("Login").disabled = false;
	document.getElementById("online").disabled = true;
	document.getElementById("Clientonline").disabled = true;
	document.getElementById("Agentonline").disabled = true;
}


// 離開WebSocket Pool列表
function Logoutaction(aUserID) {
//	Logoutaction01(aUserID, "none");
	// 在登出前,將要存入的log資訊先放到userallconnections中,此方法最後會呼叫adduserinteraction() 
	setinteractionDemo(ixnstatus, ixnactivitycode, 'client');
	
	// 組成離開WebSocket Pool列表JSON指令
	var msg = {
		type : "Exit",
		id : aUserID,
		UserName : UserName_g,
		channel: "chat",
		waittingAgent: waittingAgent, // global variable 
		waittingAgentID: waittingAgentID // global variable 
	};
	
//	console.log("Logoutaction() - msg.waittingAgent: " + msg.waittingAgent);
//	console.log("Logoutaction() - msg.waittingAgentID: " + msg.waittingAgentID);
	// 發送消息給WebSocket
	ws.send(JSON.stringify(msg));
}

// 查詢WebSocket Pool線上人員
function online() {
	var now = new Date();
	// 組成查詢WebSocket Pool線上人員JSON指令
	var msg = {
		type : "online",
		UserName : UserName_g,
		channel: "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息給WebSocket
	ws.send(JSON.stringify(msg));
}


// 離開group
function leaveRoom(aUserID) {
	// var group = 'G'+document.getElementById('group').value;
	var now = new Date();
	// 組成離開group JSON指令
	var msg = {
		type : "leaveRoom",
		roomID : roomID,
		id : aUserID,
		UserName : UserName_g,
		channel: "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息給WebSocket
	ws.send(JSON.stringify(msg));

	// 控制前端傳值
	document.getElementById("groupstatus").innerHTML = "離開" + roomID + "群組";
	// document.getElementById("addRoom").disabled = false;
	document.getElementById("leaveRoom").disabled = true;
	document.getElementById("roomonline").disabled = true;
}

// 傳送訊息至群組
function sendtoRoom(aRoomID,aMessage) {
	if (aRoomID === undefined) aRoomID = roomID; 
	if (aMessage === undefined) aMessage = document.getElementById('message').value; 
	
//	var UserID = document.getElementById('UserID').value;
	// var group = 'G'+document.getElementById('group').value;
	var now = new Date();
	// 組成傳送訊息至群組JSON指令
	var msg = {
		type : "messagetoRoom",
		text : aMessage,
		id : UserID_g,
		UserName : UserName_g,
		roomID : aRoomID,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息給WebSocket
	ws.send(JSON.stringify(msg));	

//	var message = document.getElementById('message').value;
//	sendtoRoom01(roomID, message);
}

// 查詢群組內人員
function roomonline() {
	// var group = 'G'+document.getElementById('group').value;
	var now = new Date();
	// 組成查詢群組內人員JSON指令
	var msg = {
		type : "roomonline",
		roomID : roomID,
		UserName : UserName_g,
		channel: "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息給WebSocket
	ws.send(JSON.stringify(msg));
}

function findingAgent(aUserID, aUserName) {
	setTimeout(function() {
		now = new Date();
		// 組成找尋Agent JSON指令
		var findAgentmsg = {
			type : "findAgent",
			id : aUserID,
			UserName : aUserName,
			channel : "chat",
			// Event: "findAgent",
			date : now.getHours() + ":" + now.getMinutes() + ":"
					+ now.getSeconds()
		};

		// 發送消息給WebSocket
		ws.send(JSON.stringify(findAgentmsg));
	}, 1000);
}

// 查詢Client列表內人員
function Clientonline(aACType) {
	onlineAction(aACType);
}

// 查詢Agent列表內人員
function Agentonline(aACType) {
	onlineAction(aACType);
}

function onlineAction(aACType){
	var now = new Date();
	// 組成查詢Client列表內人員JSON指令
	var msg = {
		type : "typeonline",
		ACtype : aACType,
		UserName : UserName_g,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息給WebSocket
	ws.send(JSON.stringify(msg));	
}


// 告知Agent,Client自己也已經只到Agent接通了此通通話了
function find() {
//	var UserID = document.getElementById('UserID').value;
	var findAgent = document.getElementById('AgentIDs').value;
	var now = new Date();
	// 組成查詢要邀請的Agent JSON指令
	var msg = {
		type : "findAgentEvent",
		ACtype : "Client",
		id : UserID_g,
		UserName : UserName_g,
		sendto : findAgent,
		channel : "chat",
		// Event: "findAgentEvent",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息給WebSocket
	ws.send(JSON.stringify(msg));
}

// Send userdata
function senduserdata(aUserID, aPhone, sendto) {
	var now = new Date();
	// 組成userdata JSON
	var msg = {
		type : "senduserdata",
		ACtype : "Client",
		sendto : sendto,
		lang : "chiname",
		//searchkey: "Phone",
		//pkey: "id",
		searchtype : "A",
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds(),
		attributes : {
			attributenames : "Phone,id,service1,service2",
			Phone : aPhone,
			id : aUserID,
			service1 : "service one",
			service2 : "service two"
		}
	};

	// 發送消息給WebSocket
	ws.send(JSON.stringify(msg));
}

// 傳送群組訊息至layim視窗上
function sendtoRoomonlay(aText) {	
	// 組成傳送群組訊息至layim視窗上的JSON指令
	var myMessagetoRoomJson = new messagetoRoomJson("messagetoRoom", "Client", aText, UserID_g, UserName_g, roomID, "chat", "");
	// 發送消息給WebSocket	
	ws.send(JSON.stringify(myMessagetoRoomJson));
}

// layim取得訊息
function getmessagelayim(text, UserID, UserName) {
	// 組成傳送群組訊息至layim視窗上的JSON指令
	obj = {
		name : '線上客服' // 名稱
		,
		username : UserName // 我的昵稱
		,
		type : 'kefu' // 聊天類型
		,
		avatar : './layui/images/git.jpg' // 頭像
		,
		id : roomID // 定義唯一的id方便你處理資訊
		,
		content : text,
		timestamp : new Date().getTime()
	}

	// 發送消息給layim
	layim.getMessage(obj);
}

// 開啟layim
function addlayim(UserID, UserName, aRoomID) {

	console.log(UserID);
	console.log(UserName);
	console.log(aRoomID);

	// 開啟layui
	layui.use('layim', function(elayim) {
		// 配置layim
		layim = layui.layim;
		layim.config({
			init : {
				// 配置客戶資訊
				mine : {
					"username" : UserName // 我的暱稱
					,
					"id" : UserID // 我的ID
					,
					"status" : "online" // 線上狀態 online：線上、hide：隱身
					,
					"remark" : "Client" // 我的簽名
					,
					"avatar" : "./layui/images/git.jpg" // 我的頭像
				}
			}
			,uploadImage: {
		      url: 'http://ws.crm.com.tw:8080/JAXRS-FileUpload/rest/upload/images' //（返回的資料格式見下文）
		      //,type: '' //默認post
		    }
		    ,uploadFile: {
		      url: 'http://ws.crm.com.tw:8080/JAXRS-FileUpload/rest/upload/files' //（返回的資料格式見下文）
		      //,type: '' //默認post
		    }
			// 開啟客服模式
			,
			brief : true
		// ,copyright: true //是否授權
		});
		// 打開一個客服面板
		layim.chat({
			name : '線上客服' // 名稱
			,
			type : 'kefu' // 聊天類型
			,
			avatar : './layui/images/git.jpg' // 頭像
			,
			id : aRoomID
		// 定義唯一的id方便你處理資訊
		})
		// layim.setChatMin(); //收縮聊天面板

		// 監聽發送消息
		layim.on('sendMessage', function(data) {
			var To = data.to;
			console.log('sendMessage log');
			console.log(data);

			// 傳送群組訊息至layim視窗上
			sendtoRoomonlay(data.mine.content);

		});

		// 開啟傳送layim參數
		layimswitch = true;
		ixnstatus = 1;
		// ixnactivitycode = "";
	});

}

// 於登入以及登出錢更新interaction log
function setinteractionDemo(status, activitycode, aCloseFrom) {
	if (aCloseFrom === undefined) aCloseFrom = 'default';
	
	var thecomment = 'thecomment';
	var stoppedreason = 'stoppedreason';
	// var activitycode = 'activitycode';
	var AgentID = document.getElementById('AgentIDs').value;
	setinteraction(contactID, roomID, AgentID, status, 'Inbound', 2,
			'InBound New', thecomment, stoppedreason,
			activitycode, startdate, aCloseFrom);
}
function setinteraction(contactid, ixnid, agentid, status, typeid,
		entitytypeid, subtypeid, thecomment,
		stoppedreason, activitycode, startdate, closefrom) {
	var msg = {
		type : 'setinteraction',
		contactid : contactid,
		ixnid : ixnid,
		agentid : agentid,
		status : status,
		typeid : typeid,
		entitytypeid : entitytypeid,
		subtypeid : subtypeid,
		thecomment : thecomment,
		stoppedreason : stoppedreason,
		activitycode : activitycode,
		startdate : startdate,
		closefrom : closefrom,
		channel: "chat"
	};

	// 發送消息給WebSocket
	ws.send(JSON.stringify(msg));
}


/******* 暫時保留區 *******/
////此方法可刪除了
////登出後 寫入log
//function interactionLogDemo(status, activitycode) {
//	var thecomment = 'thecomment';
//	var stoppedreason = 'stoppedreason';
//	// var activitycode = 'activitycode';
//	var AgentID = document.getElementById('findAgent').value;
//	// 因為onClose()時就會呼叫interactionlog(),所以不用再這邊又呼叫一次
////	 interactionlog(contactID, roomID, AgentID, status, 'Inbound', 2,
////	 'InBound New', text, structuredtext, thecomment, stoppedreason,
////	 activitycode, startdate);
//	// 此處可能會有"Exit"事件先發生,而此'setinteraction'會後發生,但因為目前有太多的全域變數,調動上過大,暫時先註解著(沒問題就先處理更重要的事項)
//	setinteraction(contactID, roomID, AgentID, status, 'Inbound', 2,
//			'InBound New', thecomment, stoppedreason,
//			activitycode, startdate, 'client');
//}


//// 送出私訊
//function send() {
//	var message = document.getElementById('message').value;
//	var UserID = document.getElementById('UserID').value;
//	var sendto = document.getElementById('sendto').value;
//	var now = new Date();
//	// 組成送出私訊JSON指令
//	var msg = {
//		type : "message",
//		text : message,
//		id : UserID,
//		UserName : UserName,
//		sendto : sendto,
//		channel: "chat",
//		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
//	};
//
//	// 發送消息給WebSocket
//	ws.send(JSON.stringify(msg));
//}

////新增人員至group
//function addRoom(aRoomID) {
//	var UserID = document.getElementById('UserID').value;
//	// var group = 'G'+document.getElementById('group').value;
//	var now = new Date();
//	// 組成新增人員至group JSON指令
//	var msg = {
//		type : "addRoom",
//		roomID : aRoomID,
//		id : UserID,
//		ACtype : "Client",
//		UserName : UserName,
//		channel: "chat",
//		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
//	};
//
//	// 發送消息給WebSocket
//	ws.send(JSON.stringify(msg));
//
//	// 控制前端傳值
//	document.getElementById("groupstatus").innerHTML = "加入" + roomID + "群組";
//	document.getElementById("leaveRoom").disabled = false;
//	document.getElementById("addRoom").disabled = true;
//	document.getElementById("roomonline").disabled = false;
//}

////離開Client列表
//function LeaveType(UserID) {
//	var now = new Date();
//	
//	if(waittingAgent){
//		var UserID = document.getElementById('UserID').value;
//		var updateAgentStatusmsg = {
//			    type: "updateStatus",
//			    ACtype: "Client",
//			    id: UserID,
//				UserName: UserName,
//				status: "lose",
//				reason: "Waitting Agent",
//				channel: "chat",
//			    date: now.getHours()+":"+now.getMinutes()+":"+now.getSeconds()
//			  };
//		
//		//發送消息 
//		ws.send(JSON.stringify(updateAgentStatusmsg));
//	}
//	
//	// 組成離開Client列表JSON指令
//	var Clientmsg = {
//		type : "typeout",
//		ACtype : "Client",
//		id : UserID,
//		UserName : UserName,
//		channel : "chat",
//		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
//	};
//
//	// 發送消息給WebSocket
//	ws.send(JSON.stringify(Clientmsg));
//}