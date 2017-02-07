var ws;
var layimswitch = false; // layim開關參數
var layim;
var UserName;
var roomID;
var contactID;
var isonline = false;
var startdate = new Date();
var ixnstatus = 0;
var ixnactivitycode;
var waittingAgent = false;
var UserID_g;

// 控制例外離開動作(如: 刷新頁面、關閉視窗)
function checktoLeave() {
	console.log('leave');
	var UserID = document.getElementById('UserID').value;
	// 顯示是否要離開網頁
	event.returnValue = "Leave?";
	// 離開Client列表
	LeaveType(UserID);
	// 離開WebSocket Pool列表
	Logoutaction(UserID);
	// 離開群組列表
	leaveRoom(UserID);
}

// 連上websocket
function Login() {
	// startdate = new Date();
	UserName = document.getElementById('UserName').value;
	if (null == UserName || "" == UserName) {
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
				UserName : UserName,
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
					updateStatusAction("Client", UserID_g, UserName, "chat", "chatting");
					
					waittingAgent = false;
					
					// 開啟layim
					roomID = obj.roomID;
					console.log("roomID: " + roomID);
					console.log("UserID_g: " + UserID_g);
//					addlayim(UserID, UserName);
					addlayim(UserID_g, UserName, roomID);

					// 控制前端傳值
					document.getElementById("roomID").value = roomID;
					document.getElementById("roomonline").disabled = false;
					document.getElementById("Event").value = obj.Event;
					document.getElementById("Eventform").value = obj.from;
					// 收到拒絕交談指令
				} else if ("RejectEvent" == obj.Event) {
					var UserID = document.getElementById('UserID').value;
					findingAgent(UserID, UserName);
					// 控制前端傳值
					document.getElementById("Event").value = obj.Event;
					document.getElementById("Eventform").value = obj.from;
					// 收到尋找Agent的指令
				} else if ("findAgent" == obj.Event) {
					// 寫入log
					// 增加此判斷式,避免一直呼叫senduserdata方法
					if (contactID == null){
						senduserdata(UserID, UserName, obj.Agent);
						console.log("senduserdata done ******************* ");						
					}

					if ("null" == obj.Agent || null == obj.Agent) {
						if (isonline) {
							var UserID = document.getElementById('UserID').value;
							console.log(UserName + " is looking for an agent ... ");
							findingAgent(UserID, UserName);
						}
					} else {
						// 控制前端傳值
						// RoomID = 'G'+obj.Agent;
						document.getElementById("findAgent").value = obj.Agent;
						// document.getElementById("group").value = RoomID;
						document.getElementById("Event").value = obj.Event;
						document.getElementById("Eventform").value = obj.from;
						var UserID = document.getElementById('UserID').value;
						
						waittingAgent = true;
						// 寫入log(告知雙方彼此的資訊)
						senduserdata(UserID, UserName, obj.Agent);
						console.log("senduserdata done ******************* ");						

						
						// 找尋Agent
						find();
					}
					// 收到群組訊息
				} else if ("messagetoRoom" == obj.Event) {
					var UserID = document.getElementById('UserID').value;
					// 判斷是否有開啟layim與是否為自己傳送的訊息
					if (true == layimswitch && obj.id != UserID) {
						// 將收到訊息顯示到layim上
						getmessagelayim(obj.text, obj.id, obj.UserName);
					}
					// 控制前端傳值
					document.getElementById("text").innerHTML += obj.UserName
							+ ": " + obj.text + "<br>";

				} else if ("senduserdata" == obj.Event) {
					contactID = obj.userdata.SetContactLog.contactID;
					setinteractionDemo(ixnstatus, ixnactivitycode);
					// alert(obj.userdata.SetContactLog.contactID);
					// document.getElementById("userdata").innerHTML =
					// JSON.stringify(obj.userdata);
				} else if ("userjoin" == obj.Event) {
					// 控制前端傳值
					document.getElementById("UserID").value = obj.from;
					UserID_g = obj.from;
					var UserID = document.getElementById('UserID').value;
					/** 成功登入後,開始找尋Agent **/
					var now = new Date();
					// 組成找尋Agent JSON指令
					var findAgentmsg = {
						type : "findAgent",
						id : UserID,
						UserName : UserName,
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
						userid : UserID,
						username : UserName,
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
					document.getElementById("showUserID").innerHTML = UserID;
					document.getElementById("status").innerHTML = "狀態: Login";
					document.getElementById("Login").disabled = true;
					document.getElementById("Logout").disabled = false;
					document.getElementById("online").disabled = false;
					document.getElementById("Clientonline").disabled = false;
					document.getElementById("Agentonline").disabled = false;

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
	Logoutaction(UserID); // 這邊會全部清: Group, Type, User conn
	// 控制前端傳值
	document.getElementById("status").innerHTML = "狀態: Logout";
	document.getElementById("Logout").disabled = true;
	document.getElementById("Login").disabled = false;
	document.getElementById("online").disabled = true;
	document.getElementById("Clientonline").disabled = true;
	document.getElementById("Agentonline").disabled = true;
}

// 離開WebSocket Pool列表
function Logoutaction(UserID) {
	// 在登出前,將要存入的log資訊先放到userallconnections中,此方法最後會呼叫adduserinteraction() 
	interactionLogDemo(ixnstatus, ixnactivitycode)
	
	var now = new Date();
	// 組成離開WebSocket Pool列表JSON指令
	var msg = {
		type : "Exit",
		id : UserID,
		UserName : UserName,
		channel: "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息給WebSocket
	ws.send(JSON.stringify(msg));
}

// 送出私訊
function send() {
	var message = document.getElementById('message').value;
	var UserID = document.getElementById('UserID').value;
	var sendto = document.getElementById('sendto').value;
	var now = new Date();
	// 組成送出私訊JSON指令
	var msg = {
		type : "message",
		text : message,
		id : UserID,
		UserName : UserName,
		sendto : sendto,
		channel: "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息給WebSocket
	ws.send(JSON.stringify(msg));
}

// 查詢WebSocket Pool線上人員
function online() {
	var now = new Date();
	// 組成查詢WebSocket Pool線上人員JSON指令
	var msg = {
		type : "online",
		UserName : UserName,
		channel: "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息給WebSocket
	ws.send(JSON.stringify(msg));
}

// 新增人員至group
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

// 離開group
function leaveRoom(UserID) {
	// var group = 'G'+document.getElementById('group').value;
	var now = new Date();
	// 組成離開group JSON指令
	var msg = {
		type : "leaveRoom",
		roomID : roomID,
		id : UserID,
		UserName : UserName,
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
function sendtoRoom() {
	var message = document.getElementById('message').value;
	sendtoRoom01(roomID, message);
}

function sendtoRoom01(aRoomID,aMessage){
	var UserID = document.getElementById('UserID').value;
	// var group = 'G'+document.getElementById('group').value;
	var now = new Date();
	// 組成傳送訊息至群組JSON指令
	var msg = {
		type : "messagetoRoom",
		text : aMessage,
		id : UserID,
		UserName : UserName,
		roomID : aRoomID,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息給WebSocket
	ws.send(JSON.stringify(msg));
} 

// 查詢群組內人員
function roomonline() {
	// var group = 'G'+document.getElementById('group').value;
	var now = new Date();
	// 組成查詢群組內人員JSON指令
	var msg = {
		type : "roomonline",
		roomID : roomID,
		UserName : UserName,
		channel: "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息給WebSocket
	ws.send(JSON.stringify(msg));
}

function findingAgent(UserID, UserName) {
	setTimeout(function() {
		now = new Date();
		// 組成找尋Agent JSON指令
		var findAgentmsg = {
			type : "findAgent",
			id : UserID,
			UserName : UserName,
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
function Clientonline() {
	var now = new Date();
	// 組成查詢Client列表內人員JSON指令
	var msg = {
		type : "typeonline",
		ACtype : "Client",
		UserName : UserName,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息給WebSocket
	ws.send(JSON.stringify(msg));
}

// 查詢Agent列表內人員
function Agentonline() {
	var now = new Date();
	// 組成查詢Agent列表內人員JSON指令
	var msg = {
		type : "typeonline",
		ACtype : "Agent",
		UserName : UserName,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息給WebSocket
	ws.send(JSON.stringify(msg));
}

// 離開Client列表
function LeaveType(UserID) {
	var now = new Date();
	
	if(waittingAgent){
		var UserID = document.getElementById('UserID').value;
		var updateAgentStatusmsg = {
			    type: "updateStatus",
			    ACtype: "Client",
			    id: UserID,
				UserName: UserName,
				status: "lose",
				reason: "Waitting Agent",
				channel: "chat",
			    date: now.getHours()+":"+now.getMinutes()+":"+now.getSeconds()
			  };
		
		//發送消息 
		ws.send(JSON.stringify(updateAgentStatusmsg));
	}
	
	// 組成離開Client列表JSON指令
	var Clientmsg = {
		type : "typeout",
		ACtype : "Client",
		id : UserID,
		UserName : UserName,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息給WebSocket
	ws.send(JSON.stringify(Clientmsg));
}

// 查詢要邀請的Agent
function find() {
	var UserID = document.getElementById('UserID').value;
	var findAgent = document.getElementById('findAgent').value;
	var now = new Date();
	// 組成查詢要邀請的Agent JSON指令
	var msg = {
		type : "findAgentEvent",
		ACtype : "Client",
		id : UserID,
		UserName : UserName,
		sendto : findAgent,
		channel : "chat",
		// Event: "findAgentEvent",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息給WebSocket
	ws.send(JSON.stringify(msg));
}

// Send userdata
function senduserdata(UserID, phone, sendto) {
	var now = new Date();
	// 組成userdata JSON
	var msg = {
		type : "senduserdata",
		ACtype : "Client",
		sendto : sendto,
		lang : "chiname",
		// searchkey: "Phone",
		// pkey: "id",
		searchtype : "A",
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds(),
		attributes : {
			attributenames : "Phone,id,service1,service2",
			Phone : phone,
			id : phone,
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
	var myMessagetoRoomJson = Object.create(messagetoRoomJson); // test
	myMessagetoRoomJson.buildup("messagetoRoom", "Client", aText, UserID_g, UserName, roomID, "chat", "");
//	myMessagetoRoomJson.type = "messagetoRoom";
//	myMessagetoRoomJson.roomID = roomID; // Client至多只會有一個roomID
//	myMessagetoRoomJson.ACtype = "Client";
//	myMessagetoRoomJson.id = UserID_g;
//	myMessagetoRoomJson.UserName = UserName;
//	myMessagetoRoomJson.text = aText;
//	myMessagetoRoomJson.channel = "chat";
//	myMessagetoRoomJson.date = "";	// 日期部分須到server端拿
	
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

function setinteractionDemo(status, activitycode) {
	var thecomment = 'thecomment';
	var stoppedreason = 'stoppedreason';
	// var activitycode = 'activitycode';
	var AgentID = document.getElementById('findAgent').value;
	setinteraction(contactID, roomID, AgentID, status, 'Inbound', 2,
			'InBound New', thecomment, stoppedreason,
			activitycode, startdate, 'default');
}

function interactionLogDemo(status, activitycode) {
	var thecomment = 'thecomment';
	var stoppedreason = 'stoppedreason';
	// var activitycode = 'activitycode';
	var AgentID = document.getElementById('findAgent').value;
	// 因為onClose()時就會呼叫interactionlog(),所以不用再這邊又呼叫一次
//	 interactionlog(contactID, roomID, AgentID, status, 'Inbound', 2,
//	 'InBound New', text, structuredtext, thecomment, stoppedreason,
//	 activitycode, startdate);
	// 此處可能會有"Exit"事件先發生,而此'setinteraction'會後發生,但因為目前有太多的全域變數,調動上過大,暫時先註解著(沒問題就先處理更重要的事項)
	setinteraction(contactID, roomID, AgentID, status, 'Inbound', 2,
			'InBound New', thecomment, stoppedreason,
			activitycode, startdate, 'client');
}

//保留此方法,暫時沒用到
/*
function interactionlog(contactid, ixnid, agentid, status, typeid,
		entitytypeid, subtypeid, thecomment,
		stoppedreason, activitycode, startdate) {
	// var now = new Date();
	// 組成interactionlog
	var msg = {
		type : 'interactionlog',
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
		closefrom : 'client',
		channel: "chat"
	};

	// 發送消息給WebSocket
	ws.send(JSON.stringify(msg));
}
*/
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
