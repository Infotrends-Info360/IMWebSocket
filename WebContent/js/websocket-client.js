var ws_g;
var UserID_g;
var UserName_g;
var RoomID_g;
var contactID_g;
var isonline_g = false;
var startdate_g = new Date();
var ixnstatus_g = 0;
var ixnactivitycode_g;
var waittingAgent_g = false;
var waittingAgentID_g = "none";

var AgentIDList_g; // 後端資料處理已經沒在使用,僅前端顯示用
var RoomOwnerAgentID_g; // 紀錄當下Room的Owner,當三方為transfer時,owner才會轉變

var systemParam_g; // 系統參數,主要為URL相關資訊

/** layim **/
var layimswitch = false; // layim開關參數
var layim;


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
 * "", "removeUserinroom", 
 * 
 * */

// 網頁載入後第一個動作
function onloadFunctionClient(){
	console.log("onloadFunction() called");

	
	/** 建立傳送訊息enter事件 **/
	$("#message").keypress(function(e) {
	    if(e.which == 13) {
	       //alert('You pressed enter!');
	    	sendtoRoom();
	    }
	});
	
	/** 拿取systemParam - ex. IP address, port **/
	systemParam_g = JSON.parse( document.getElementById('systemParam').value );
	
}

// 控制例外離開動作(如: 刷新頁面、關閉視窗)(此方法暫時沒使用)
function checktoLeave() {
	// 離開WebSocket Pool列表
	Logoutaction(); // 這邊會全部清: Group, Type, User conn
}

// 連上websocket
function Login() {
	/** 將全域變數皆設回預設值 **/
	ws_g = null;
	UserID_g = null;
	UserName_g = null;
	RoomID_g = null;
	contactID_g = null;
	isonline_g = false;
	startdate_g = new Date();
	ixnstatus_g = 0;
	ixnactivitycode_g = null;
	waittingAgent_g = false;
	waittingAgentID_g = "none";

	AgentIDList_g = null; // 後端資料處理已經沒在使用,僅前端顯示用
	RoomOwnerAgentID_g = null; // 紀錄當下Room的Owner,當三方為transfer時,owner才會轉變

//	var systemParam_g = null; // 系統參數,主要為URL相關資訊

	layimswitch = false; // layim開關參數
	layim = null;
	
	
	// startdate = new Date();
	UserName_g = document.getElementById('UserName').value;
	
	/** 更新前端頁面(防呆機制) **/
	document.getElementById("openChat").disabled = true;
	document.getElementById("closeChat").disabled = false;
	
	if (null == UserName_g || "" == UserName_g) {
		alert("請輸入UserName");
	} else {
		// 開啟WebSocket的通道
		// 開啟WebSocket的通道
		var url = systemParam_g.websocket.protocol + "//" + systemParam_g.websocket.hostname + ":" + systemParam_g.websocket.port;
		ws_g = new WebSocket(url);
//		alert("url: " + url);
//		console.log("window.location.hostname: " + window.location.hostname);
//		var hostname = window.location.hostname;
//		ws_g = new WebSocket('ws://' + hostname +':8888');
		// 當websocket連接建立成功時
		ws_g.onopen = function() {
			console.log('websocket 打開成功');
			/** 登入 **/
			var now = new Date();
			// 組成登入WebSocket Pool列表JSON指令
			var msg = {
				type : "login",
				UserName : UserName_g,
				MaxCount: '0',
				ACtype : "Client",
				channel : "chat",
				date : now.getHours() + ":" + now.getMinutes() + ":"
						+ now.getSeconds()
			};

			// 發送消息給WebSocket
			ws_g.send(JSON.stringify(msg));			
			
			
		};
		// 當收到服務端的消息時
		ws_g.onmessage = function(e) {
			// e.data 是服務端發來的資料

			// 收到指令時是Json Object格式
			if ("{" == e.data.substring(0, 1)) {
				var obj = jQuery.parseJSON(e.data);
				// 收到同意交談指令
				if ("userjoin" == obj.Event) {
//						alert("obj.chatRoomMsg: " + obj.chatRoomMsg);
						var chatRoomMsg = obj.chatRoomMsg; // 接收系統訊息
						
						// 控制前端傳值
//						document.getElementById("UserID").value = obj.from;
						document.getElementById("UserID").innerHTML = obj.from;
						document.getElementById("Event").innerHTML = obj.Event;
						document.getElementById("chatroom").innerHTML += chatRoomMsg + "<br>";
						
						UserID_g = obj.from;
						/** 成功登入後,先寫上第一筆log **/
						// 去server抓取user資訊
						senduserdata(); // 此處不須傳入AgentID
						
						/** 成功登入後,開始找尋Agent **/
						// 組成找尋Agent JSON指令,並發送消息給WebSocket
						var myFindAgentJson = new findAgentJson(UserID_g, UserName_g);
						ws_g.send(JSON.stringify(myFindAgentJson));
						
						/** 更新前端資料 **/
						document.getElementById('Status').innerHTML = StatusEnum.FIND_AGENT;
						document.getElementById("openChat").disabled = true;
						document.getElementById("closeChat").disabled = false;

						parent.isonline_g = true;
						
				} else if ("senduserdata" == obj.Event) {
//						alert("obj.userdata.SetContactLog: "+JSON.stringify(obj.userdata.SetContactLog));
						// 調成senduserdata,讓其可銜接客戶端client.js版本
						if (obj.userdata.SetContactLog != null){
//						if (obj.userdata.SetContactLog != null && obj.userdata.SetContactLog[0] != null){
//							alert("obj.userdata.SetContactLog[0].contactID: " + obj.userdata.SetContactLog[0].contactID);
							contactID_g = obj.userdata.SetContactLog.contactID;
//							setinteractionDemo(ixnstatus, ixnactivitycode);	
//							alert("contactID_g1: " + contactID_g);
							setinteraction(ixnstatus_g, ixnactivitycode_g);
						}else{
							console.log("senduserdata - " + "contactID not found! ");
//							alert("contactID_g2: " + contactID_g);
							contactID_g = undefined;
							setinteraction(ixnstatus_g, ixnactivitycode_g); 
						}
						/** 寫入Log **/
						// 組成寫入entry log指令,發送消息給WebSocket
						var myEntrylogJson = new entrylogJson(contactID_g, UserID_g, UserName_g);
						ws_g.send(JSON.stringify(myEntrylogJson));
						
						console.log("senduserdata done from WS ******************* ");	
						// alert(obj.userdata.SetContactLog.contactID);
						// document.getElementById("userdata").innerHTML =
						// JSON.stringify(obj.userdata);
				} else if ("findAgent" == obj.Event) {

						// 若仍未找到Agent, 則再找
						if ("null" == obj.Agent || null == obj.Agent) {
//							//20170308 - 此段可省略 - 後端已改為ReadyAgentQueue機制,只需發出一次請求即可
							//只有找到Agent才會收到此"findAgent"事件,因此不再有找不到的狀況
							if (parent.isonline_g) {
								console.log(UserName_g + " is looking for an agent ... ");
								findingAgent();
							}
						// 若找到Agent, 則進入等待Agent回應狀態	
						} else {
							var chatRoomMsg = obj.chatRoomMsg; // 接收系統訊息
							// 控制前端傳值
							document.getElementById("AgentIDs").innerHTML = obj.Agent;
							document.getElementById("AgentNames").innerHTML = obj.AgentName;
							document.getElementById("Event").innerHTML = obj.Event;
							document.getElementById("chatroom").innerHTML += chatRoomMsg + "<br>";
							
							waittingAgent_g = true;
							waittingAgentID_g = obj.Agent;

							// 更新資訊
							switchStatus(StatusEnum.WAIT_AGENT);
							// 寫入log(告知雙方彼此的資訊)
//							senduserdata(obj.Agent); // 交由Server處理
//							console.log("senduserdata done ******************* ");						
							
							// 告知Agent, Client這邊知道找到一個Agent了
							find(waittingAgentID_g); // 已可省略 - senduserdata已經能做到這件事情
						}
						// 收到群組訊息
				} else if ("AcceptEvent" == obj.Event) {
//					var myUpdateStatusJson = new updateStatusJson("Client", UserID_g, UserName_g, "chat", "chatting");
//					ws_g.send(JSON.stringify(myUpdateStatusJson));
					var chatRoomMsg = obj.chatRoomMsg; // 接收系統訊息
					waittingAgent_g = false;
					waittingAgentID_g = "none";
					
					// 更新AgentIDList:
					console.log("AcceptEvent: obj.from: " + obj.from);
					console.log("AcceptEvent: obj.fromName: " + obj.fromName);
					AgentIDList_g = [];
					AgentIDList_g.push(obj.from);
					RoomOwnerAgentID_g = obj.from; // 開啟房間後更新roomOwner
//					alert("RoomOwnerAgentID_g: " + RoomOwnerAgentID_g);

					// 顯現對話視窗
//					document.getElementById("chatDialogue").classList.remove("hidden");
//					document.getElementById("chatDialogueReverse").classList.add("hidden");

					// 控制前端傳值
					RoomID_g = obj.roomID;
					document.getElementById("RoomID").innerHTML = RoomID_g;
					document.getElementById("Event").innerHTML = obj.Event;
					document.getElementById("Status").innerHTML = StatusEnum.JOIN_ROOM;
					document.getElementById("chatroom").innerHTML += chatRoomMsg + "<br>";
					
					// 將focus移到寄送訊息欄
					$('#message').focus();
					
				// 收到拒絕交談指令
				} else if ("RejectEvent" == obj.Event) {
					alert("Agent reject");
					var chatRoomMsg = obj.chatRoomMsg; // 接收系統訊息
					// 更新前端畫面
					document.getElementById("chatroom").innerHTML += chatRoomMsg + "<br>";
					
					switchStatus(StatusEnum.FIND_AGENT);
					findingAgent();
					// 控制前端傳值
//					document.getElementById("Event").value = obj.Event;
//					document.getElementById("Eventfrom").value = obj.from;
				// 收到尋找Agent的指令
				} else if ("ringTimeout" == obj.Event){
					alert("ringTimeout");
					var chatRoomMsg = obj.chatRoomMsg; // 接收系統訊息
					// 更新前端畫面
					document.getElementById("chatroom").innerHTML += chatRoomMsg + "<br>";
					
					switchStatus(StatusEnum.FIND_AGENT);
					findingAgent();
				} else if ("agentLeft" == obj.Event ){
					alert("agent left!");
					switchStatus(StatusEnum.FIND_AGENT);
					findingAgent();

				} else if ("messagetoRoom" == obj.Event) {
//					var UserID = document.getElementById('UserID').value;
					var msgToShow = obj.UserName + ": " + obj.text;
					document.getElementById("chatroom").innerHTML += msgToShow + "<br>";
					
				} else if ("removeUserinroom" == obj.Event){
					var chatRoomMsg = obj.chatRoomMsg; // 接收系統訊息
					var leftRoomMsg = chatRoomMsg.leftRoomMsg;
					var closedRoomMsg = chatRoomMsg.closedRoomMsg;
					updateAgentInfo(obj.roomMemberIDs, obj.roomMembers, obj.roomSize); //格式為[agentid, clientid]
					if (obj.roomSize == 0){
						Logout();
					}
					
					document.getElementById("chatroom").innerHTML += leftRoomMsg + "<br>";
					if (closedRoomMsg != undefined){
						document.getElementById("chatroom").innerHTML += closedRoomMsg + "<br>";
					}
				} else if ("addUserInRoom" == obj.Event){
					console.log("obj.roomMemberIDs: " + obj.roomMemberIDs);
					console.log("obj.roomMembers: " + obj.roomMembers);
					updateAgentInfo(obj.roomMemberIDs, obj.roomMembers, obj.roomSize);
				} else if ("responseThirdParty" == obj.Event){
					console.log("obj.invitedAgentID: " + obj.invitedAgentID);
					var chatRoomMsg = obj.chatRoomMsg; // 接收系統訊息
					
					AgentIDList_g.push(obj.invitedAgentID);
					if (obj.inviteType == "transfer"){
						RoomOwnerAgentID_g = obj.invitedAgentID; // 若為轉接,更新roomOwner
					}
					// 更新前端畫面
					document.getElementById("chatroom").innerHTML += chatRoomMsg + "<br>"; // 更新系統訊息
				}  
			} else {
				// 控制前端傳值
				document.getElementById("text").innerHTML += e.data + "<br>";
			}
			console.log("onMessage() - " + e.data);
		};// end of ws.onMessage()

		// 當websocket關閉時
		ws_g.onclose = function() {
//			alert("websocket 連接關閉");
			console.log("websocket 連接關閉");
			// checktoLeave();
		};

		// 當websocket出現錯誤時
		ws_g.onerror = function() {
			alert("出現錯誤");
			console.log("出現錯誤");
		};
	}

}

// 離開WebSocket
function Logout() {
	Logoutaction(); // 這邊會全部清: Group, Type, User conn
//	// 關閉socket
//	 ws.close()
}


// 離開WebSocket Pool列表
function Logoutaction() {
	// 使用流程: 給"Clientclosegroup" -> .java - "Clientclosegroup" -> .js - ReleaseEvent() -> notready -> "Agentclosegroup" -> .java -> client.js - if(isonlone){} 
	parent.isonline_g = false; 
	// 更新狀態
	switchStatus(StatusEnum.LOGOUT);

	// 在登出前,將要存入的log資訊先放到userallconnections中,此方法最後會呼叫adduserinteraction() 
//	setinteractionDemo(ixnstatus, ixnactivitycode, 'client');
	setinteraction(ixnstatus_g, ixnactivitycode_g, 'client');
	// 組成離開WebSocket Pool列表JSON指令,並發送消息給WebSocket
	var myExitJson = new exitJson(UserID_g, UserName_g, waittingAgent_g, waittingAgentID_g);
	ws_g.send(JSON.stringify(myExitJson));
}

function findingAgent() {
//	alert('findingAgent() called');
	setTimeout(function(){
		if (!parent.isonline_g) return;

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
		ws_g.send(JSON.stringify(findAgentmsg));
		
	}, 1000); // 先執行,後延遲 -> 目的是讓 Logout()時能乾淨的取消掉findAgent排程 

}

//告知Agent,Client自己也已經知道Agent接通了此通通話了
function find(aAgentFound) {
	if (aAgentFound === undefined) aAgentFound = AgentIDList_g[0]; // 開發過渡期使用
	
	document.getElementById('Status').innerHTML = StatusEnum.WAIT_AGENT;
	var now = new Date();
	// 組成查詢要邀請的Agent JSON指令
	var msg = {
		type : "findAgentEvent",
		ACtype : "Client",
		id : UserID_g,
		UserName : UserName_g,
		sendto : aAgentFound,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息給WebSocket
	ws_g.send(JSON.stringify(msg));
}


//Send userdata (已廢棄)
function senduserdata(aSendto) {
	if (aSendto === undefined) aSendto = "";
	
	// 組成userdata JSON
	var msg = {
		type : "senduserdata",
		sendto : aSendto,
		lang : "chiname",
		searchtype : "A",
		channel : "chat",
	};

	// 發送消息給WebSocket
	ws_g.send(JSON.stringify(msg));
}


// 傳送訊息至群組
function sendtoRoom(aRoomID,aMessage) {
	if (aRoomID === undefined) aRoomID = RoomID_g; 
	if (aMessage === undefined) aMessage = document.getElementById('message').value; // <input> tag - 所以用.value
	console.log("sendtoRoom() - aMessage: " + aMessage);
	
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
	ws_g.send(JSON.stringify(msg));	
	
	// 清空訊息欄
	document.getElementById('message').value = '';

}


//離開group
function leaveRoom(aRoomID, aUserID) {
	if (aRoomID === undefined) aRoomID = RoomID_g;  // client至多只會有一個RoomID
	if (aUserID === undefined) aUserID = UserID_g; 
	
	if (aRoomID === undefined) {
		alert("開發模式: " + "leaveRoom() - no roomID for this client");
		return;
	}
	
	// var group = 'G'+document.getElementById('group').value;
	var now = new Date();
	// 組成離開group JSON指令
	var msg = {
		type : "leaveRoom",
		roomID : aRoomID,
		id : aUserID,
		UserName : UserName_g,
		channel: "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息給WebSocket
	ws_g.send(JSON.stringify(msg));

	// 控制前端傳值
	document.getElementById("groupstatus").innerHTML = "離開" + RoomID_g + "群組";
	// document.getElementById("addRoom").disabled = false;
	document.getElementById("leaveRoom").disabled = true;
	document.getElementById("roomonline").disabled = true;
}

function updateAgentInfo(aRoomMemberIDs, aRoomMembers, aRoomSize){
	document.getElementById("AgentIDs").innerHTML = ''; // 先清再加
	document.getElementById("AgentNames").innerHTML = ''; // 先清再加
	var roomMemberIDs = aRoomMemberIDs.slice(1,-1);
	roomMemberIDs = roomMemberIDs.split(",");
	var roomMembers = aRoomMembers.slice(1,-1);
	roomMembers = roomMembers.split(",");
	for (var index in roomMemberIDs){
		if (UserID_g == roomMemberIDs[index].trim()) continue;
		// 若有第二輪, 則加上 ',' + "<br>"
		if (document.getElementById("AgentIDs").innerHTML != ''){
			document.getElementById("AgentNames").innerHTML += ',' + "<br>" // 
			document.getElementById("AgentIDs").innerHTML += ',' + "<br>" // 			
		}
		
		document.getElementById("AgentNames").innerHTML += roomMembers[index].trim() // 先這樣, 還不用做太細
		document.getElementById("AgentIDs").innerHTML += roomMemberIDs[index].trim() // 先這樣, 還不用做太細
	}
	
	if (aRoomSize == 0){
		document.getElementById("RoomID").innerHTML = '';
		
		switchStatus(StatusEnum.LOGOUT);
	}
}

function setinteraction(aStatus, aActivitycode, aClosefrom, aThecomment, aStoppedreason) {
//	if (aClosefrom === undefined) aClosefrom = 'default';
//	if (aThecomment === undefined) aThecomment = 'thecomment';
//	if (aStoppedreason === undefined) aStoppedreason = 'stoppedreason';
	
	// 更新interactionlog
	var msg = {
		type : 'setinteraction',
		contactid : contactID_g,
		ixnid : RoomID_g,
		agentid : RoomOwnerAgentID_g,
		status : aStatus,
		typeid : 'Inbound',
		entitytypeid : 2,
		subtypeid : 'InBound New',
		thecomment : aThecomment,
		stoppedreason : aStoppedreason,
		activitycode : aActivitycode,
		startdate : startdate_g,
		closefrom : aClosefrom,
		channel: "chat"
	};

	// 發送消息給WebSocket
	ws_g.send(JSON.stringify(msg));
}

function switchStatus(aStatus){
	// 統一更新狀態資訊
	
	switch(aStatus) {
    case StatusEnum.FIND_AGENT:
        document.getElementById("Status").innerHTML = StatusEnum.FIND_AGENT;
		// 控制前端傳值
		document.getElementById("AgentIDs").innerHTML = '';
		document.getElementById("AgentNames").innerHTML = '';
		document.getElementById("Event").innerHTML = '';

		waittingAgent_g = false;
		waittingAgentID_g = 'none';

		//code block
    	break;
	case StatusEnum.WAIT_AGENT:
        document.getElementById("Status").innerHTML = StatusEnum.WAIT_AGENT;
        // code block
        break;
    case StatusEnum.JOIN_ROOM:
        document.getElementById("Status").innerHTML = StatusEnum.JOIN_ROOM;
        //code block
        break;
    case StatusEnum.LOGOUT:
//      alert('StatusEnum.LOGOUT matched');
      document.getElementById("Status").innerHTML = StatusEnum.LOGOUT;
		// 顯現對話視窗
//		document.getElementById("chatDialogue").classList.add("hidden");
//		document.getElementById("chatDialogueReverse").classList.remove("hidden");
		// 啟用openChat功能
		document.getElementById("openChat").disabled = false;
		document.getElementById("closeChat").disabled = true;

      break;    	
//    case StatusEnum.:
//    	//code block
//    	break;
    default:
        break;
	}
}

/********** enum *************/
var StatusEnum = Object.freeze({
	LOGOUT: 'LOGOUT', 
	WAIT_AGENT: 'WAIT_AGENT', 
	JOIN_ROOM: 'JOIN_ROOM',
	FIND_AGENT: 'FIND_AGENT'
		
});



/** 現在未使用方法 **/
//查詢WebSocket Pool線上人員
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
	ws_g.send(JSON.stringify(msg));
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
	ws_g.send(JSON.stringify(msg));	
}


//查詢群組內人員
function roomonline() {
	// var group = 'G'+document.getElementById('group').value;
	var now = new Date();
	// 組成查詢群組內人員JSON指令
	var msg = {
		type : "roomonline",
		roomID : RoomID_g,
		UserName : UserName_g,
		channel: "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息給WebSocket
	ws_g.send(JSON.stringify(msg));
}




/** layim(以後會刪除方法) **/
// 傳送群組訊息至layim視窗上
function sendtoRoomonlay(aText) {	
	// 組成傳送群組訊息至layim視窗上的JSON指令
	var myMessagetoRoomJson = new messagetoRoomJson("messagetoRoom", "Client", aText, UserID_g, UserName_g, RoomID_g, "chat", "");
	// 發送消息給WebSocket	
	ws_g.send(JSON.stringify(myMessagetoRoomJson));
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
		id : RoomID_g // 定義唯一的id方便你處理資訊
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
		ixnstatus_g = 1;
		// ixnactivitycode = ""; 
	});  
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
