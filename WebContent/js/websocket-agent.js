var ws_g; // websocket
var UserName_g; // 使用者名稱全域變數
var UserID_g; // 使用者ID全域變數
var RoomID_g; // 此為第一個加入的RoomID, 僅為開發過程使用, 不符合目前架構, 為過度開發期間保留的全域變數
var ClientID_g; // 現在準備要服務的Client的ID 
var ClientName_g; // 現在準備要服務的Client的名稱
var isonline_g = false; // 判斷是否上線的開關
var status_g;
var roomInfoMap_g = new Map();
var count = 0;
//var RoomIDList = []; // 接通後產生Group的ID List
//var RoomIDLinkedList = new SinglyList(); // 接通後產生Group的ID Linked List

/** layui相關 **/
var AgentID; // Agent的ID // layim使用, 以及onlineinTYPE使用, 之後可考慮精簡
var AgentName; // Agent的名稱 // layim使用, 以及onlineinTYPE使用, 之後可考慮精簡
var layim; // Layim
var layimswitch = false; // layim開關參數，判斷是否開啟layim的時機


//onloadFunction
function onloadFunctionAgent(){
	// for debugging only
	console.log("onloadFunction() called");
	$("#message").keypress(function(e) {
	    if(e.which == 13) {
	       //alert('You pressed enter!');
//	    	alert("$('#sendToRoom')[0].roomID: " + $('#sendToRoom')[0].roomID);
	    	sendtoRoom($('#sendToRoom')[0].roomID);
	    }
	});
	
//	$('#roomList').change(function() {alert("hey"); });
	
//	if (Math.random() < 0.5){
//		document.getElementById('UserName').value = "agent01";
//	}else{
//		document.getElementById('UserName').value = "agent02";		
//	}
	
	// test 
//	console.log("StatusEnum.LOGIN: " + StatusEnum.LOGIN);
//	console.log("StatusEnum.convertToChinese(StatusEnum.LOGIN): " + StatusEnum.toChinese(StatusEnum.LOGIN));

	
	
}


// 連上websocket
function Login() {
	console.log("Agent Login() function");
	// 登入使用者
	parent.UserName_g = document.getElementById('UserName').value;
	if (null == parent.UserName_g || "" == parent.UserName_g) {
		alert("請輸入UserName");
	} else {
		// 連上websocket
		console.log("window.location.hostname: " + window.location.hostname);
		var hostname = window.location.hostname;
		parent.ws_g = new WebSocket('ws://' + hostname + ':8888');

		// 當websocket連接建立成功時
		parent.ws_g.onopen = function() {
			console.log('websocket 打開成功');
			/** 登入 **/
			var now = new Date();
			// 向websocket送出登入指令
			var msg = {
				type : "login",
				// id: parent.UserID_g,
				UserName : parent.UserName_g,
				ACtype : "Agent",
				channel : "chat",
				date : now.getHours() + ":" + now.getMinutes() + ":"
						+ now.getSeconds()
			};			
			// 發送消息
			parent.ws_g.send(JSON.stringify(msg));
			
			
			
		};
		// 當收到服務端的消息時
		parent.ws_g.onmessage = function(e) {
			// e.data 是服務端發來的資料
			if ("{" == e.data.substring(0, 1)) {
				var obj = jQuery.parseJSON(e.data);
				// 接收到Client邀請chat的event
				if ("findAgentEvent" == obj.Event) {
//					ClientName_g = obj.fromName;
//					ClientID_g = obj.from;
//					document.getElementById("AcceptEvent").disabled = false;
//					document.getElementById("RejectEvent").disabled = false;
//					document.getElementById("Eventfrom").value = obj.from;
//					document.getElementById("Event").innerHTML = obj.Event;
					// 接收到group訊息
				} else if ("messagetoRoom" == obj.Event) {
					var roomInfo = roomInfoMap_g.get(obj.roomID);
					var msgToShow = obj.UserName + ": " + obj.text + "<br>";
					roomInfo.text += msgToShow;
					// 判斷若當前頁面就是這個訊息所傳的room, 則馬上更新
					var currRoomID = $('#roomList').val();
					if (currRoomID == obj.roomID){
						updateRoomInfo(roomInfo);	
					}
						
					

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

					
					console.log("parent.UserID_g: "+ parent.UserID_g);
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
						if (agentID != parent.UserID_g){
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
					console.log("AcceptEvent: *****");
//					seeAllKV(obj);
				
					// 更新狀態
					status_g = StatusEnum.I_ESTABLISHED;
					switchStatus(StatusEnum.I_ESTABLISHED);
					
					// 在這邊興建roomList與其room bean
					RoomID_g = obj.roomID; // 之後要改成local variable
					var tmpRoomInfo = new RoomInfo(
							obj.roomID,
							$('#Accept')[0].userdata,
							''
					);
					roomInfoMap_g.set(obj.roomID, tmpRoomInfo);
					console.log("roomInfoMap_g.size: " + roomInfoMap_g.size);
					// 1. 研究一下這個map的json長什麼樣 2. 看怎麼拿值
					updateRoomIDList(obj.roomID);
					

					// 更新前端頁面
					document.getElementById("Accept").disabled = true;
					document.getElementById("Reject").disabled = true;
//					document.getElementById("sendtoRoom").disabled = false;
					
				} else if ("getUserStatus" == obj.Event) {
//					console.log("onMessage(): getUserStatus called");
//					document.getElementById("status").innerHTML = "狀態: "
//							+ obj.Status + "<br>Reason: " + obj.Reason; 
					// 接收到找尋Client的UserData的訊息
				} else if ("senduserdata" == obj.Event) {
					// 在這邊取代原本findAgentEvent事件所做的事情
					console.log("obj.clientName: " + obj.clientName);
					console.log("obj.clientID: " + obj.clientID);
					ClientName_g = obj.clientName;
					ClientID_g = obj.clientID;
					
				    var tr = document.createElement('tr');   

				    var td1 = document.createElement('td');
				    var td2 = document.createElement('td');
				    var td3 = document.createElement('td');

				    var text1 = document.createTextNode('通話');
				    var text2 = document.createTextNode(obj.clientName);
				    var text3 = document.createTextNode(JSON.stringify(obj.userdata));
				    var div3 = document.createElement('div');
				    div3.setAttribute("style", "height: 20px; overflow-y: hidden;");
				    
				    td1.appendChild(text1);
				    td2.appendChild(text2);
				    div3.appendChild(text3);
				    td3.appendChild(div3);
				    
				    tr.appendChild(td1);
				    tr.appendChild(td2);
				    tr.appendChild(td3);

				    tr.setAttribute("id", obj.userdata.id);
				    tr.setAttribute("userID", obj.userdata.id);
				    tr.setAttribute("userdata", JSON.stringify(obj.userdata));
				    
				    document.getElementById("requestTable").appendChild(tr);
				    
				    tr.onclick = function(e){
						$('#Accept')[0].disabled = false;
						$('#Reject')[0].disabled = false;
						$('#Accept')[0].reqType = 'Client';
						$('#Accept')[0].userID = this.getAttribute("userID");
						$('#Accept')[0].userdata = this.getAttribute("userdata");
					}; // 設定 AcceptEventInit
				    				    
					// 在這邊動態增加request list
					
				} else if ("userjointoTYPE" == obj.Event) {

					// 接收到有人登入的訊息
				} else if ("userjoin" == obj.Event) {
					console.log("userjoin - UserID: " + obj.from);
					parent.UserID_g = obj.from;
					// 更新狀態
					status_g = StatusEnum.LOGIN;
					switchStatus(status_g);
										
				} else if ("refreshRoomList" == obj.Event) {
					// debug: 確認全部key-value:
					console.log("refreshRoomList");
					seeAllKV(obj);
					
					
					

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
//					document.getElementById("currUsers").innerHTML = obj.roomMembers;
					// 若此房間已經關了, 則更新roomInfo
					if (obj.roomSize == 0){
						var roomInfo = roomInfoMap_g.get(obj.roomID);
						roomInfo.close = true;
						// 若為當前頁面,則更新roomInfo
						var currRoomID = $('#roomList').val();
						if (currRoomID == obj.roomID){
							updateRoomInfo(roomInfo);
						}
					}
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
		parent.ws_g.onclose = function() {
			console.log("websocket 連接關閉");
		};

		// 當出現錯誤時
		parent.ws_g.onerror = function() {
			console.log("出現錯誤");
		};
	}

}

// 登出
function Logout() {
	// 執行登出
	Logoutaction(); // onClose()會全部清: Group, Type, User conn
	
}


// 執行登出
function Logoutaction() {
	// 關閉上線開關
	status_g = StatusEnum.LOGOUT;
	switchStatus(status_g);	
	
	// 向websocket送出登出指令
	var now = new Date();
	var msg = {
		type : "Exit",
		// text: message,
		id : parent.UserID_g,
		UserName : parent.UserName_g,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息
	parent.ws_g.send(JSON.stringify(msg));
	
	// 關閉websocket
	parent.ws_g.close();
}

//refresh或關閉網頁時執行
function checktoLeave() {
	event.returnValue = "確定要離開當前頁面嗎？";
	Logoutaction();
}

//Agent準備就緒
function ready() {
	// 更新頁面
	status_g = StatusEnum.READY;
	switchStatus(status_g);
	
}
// Agent尚未準備就緒
function notready() {
	// 更新狀態
	status_g = StatusEnum.NOT_READY;
	switchStatus(StatusEnum.NOT_READY);	
}

//同意與Client交談
function AcceptEventInit() {
	console.log("AcceptEventInit(): ");
	
	var reqType = $('#Accept')[0].reqType;
	// 一次將Agent與Client加入到room中
	var currClientID = $('#Accept')[0].userID;
//	var userdata = $('#Accept')[0].userdata;
	
		// 在此使用新的方法,將一個list的成員都加入到同一群組中
	var memberListToJoin = [];
	var mem1 = new myRoomMemberJsonObj(currClientID);
	var mem2 = new myRoomMemberJsonObj(parent.UserID_g);
	memberListToJoin.push(mem1);
	memberListToJoin.push(mem2);
	addRoomForMany("none", memberListToJoin); // "none"是一個keyword, 會影響websocket server的邏輯判斷處理
	
	// 將此請求從request list中去除掉
	var userID = $('#Accept')[0].userID;
	console.log("userID: " + userID);
//	alert("userID: " + userID);
	$('#' + userID).remove(); // <tr>的id
	
	// 開啟ready功能:
	switchStatus(StatusEnum.NOT_READY); // 這邊之後要用全域變數來控制不同的工作模式-是否要在established之後變成not ready
//	document.getElementById("ready").disabled = false;
//	document.getElementById("notready").disabled = true;
}

// 拒絕交談
function RejectEvent() {
	var Eventfrom = document.getElementById('Eventfrom').value;
	// 向websocket送出拒絕交談指令
	var now = new Date();
	var msg = {
		type : "RejectEvent",
		ACtype : "Agent",
		id : parent.UserID_g,
		UserName : parent.UserName_g,
		sendto : Eventfrom,
		channel : "chat",
		// Event: "RejectEvent",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};
	// 發送消息
	parent.ws_g.send(JSON.stringify(msg));

	document.getElementById("AcceptEvent").disabled = true;
	document.getElementById("RejectEvent").disabled = true;
}

// 關閉交談
function ReleaseEvent(aRoomID) {
	if (aRoomID === undefined) aRoomID = RoomID_g;
	// 切換為未就緒
	// 更新狀態
	updateStatus("not ready");
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
	leaveRoom(aRoomID, parent.UserID_g); // 重點是這行
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
		UserName : parent.UserName_g,
		channel: "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息
	parent.ws_g.send(JSON.stringify(msg));
}


// 離開房間
function leaveRoom(aRoomID, aUserID) {
	if (aUserID === undefined) aUserID = parent.UserID_g;
	var roomInfo = roomInfoMap_g.get(aRoomID);
	roomInfo.close = true;
	updateRoomInfo(roomInfo);
	
	// 向websocket送出離開群組指令
	var now = new Date();
	var msg = {
		type : "leaveRoom",
		roomID : aRoomID,
		id : aUserID,
		UserName : parent.UserName_g,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息
	parent.ws_g.send(JSON.stringify(msg));
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
		id : parent.UserID_g,
		UserName : parent.UserName_g,
		sendto : aSendto,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};
	// 發送消息
	parent.ws_g.send(JSON.stringify(msg));
}
//送出Agent to Agent私訊
function sendA2A(aSendto){
	var msg = document.getElementById("A2AContent").value;
	console.log("sendA2A() - msg: " + msg);
	send(aSendto,msg);
}

// 送出訊息至群組
function sendtoRoom(aRoomID, aMessage){
//	alert("sendtoRoom - aRoomID: " + aRoomID);
	if (aRoomID === undefined) aRoomID = RoomID_g; // 開發過渡期使用,之後會修掉
	if (aMessage === undefined) aMessage = document.getElementById('message').value;
	
	var now = new Date();
	var msg = {
		type : "messagetoRoom",
		text : aMessage,
		id : parent.UserID_g,
		UserName : parent.UserName_g,
		roomID : aRoomID,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息
	parent.ws_g.send(JSON.stringify(msg));	
	
	// 清空訊息欄
	$('#message')[0].value = '';
//	document.getElementById('message').value = '';

}

// 取得Agent狀態
function getStatus() {
	// 向websocket送出取得Agent狀態指令
	var now = new Date();
	var Eventmsg = {
		type : "getUserStatus",
		ACtype : "Agent",
		id : parent.UserID_g,
		UserName : parent.UserName_g,
		channel : 'chat',
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};
	// 發送消息
	parent.ws_g.send(JSON.stringify(Eventmsg));
}

// 只有在aStatus狀態為not ready時,才會傳入aReason參數
function updateStatus(aStatus, aReason){
	if (aReason === undefined) aReason = 'no reason'; 
	// 向websocket送出變更狀態至未就緒指令
	var now = new Date();
	var updateAgentStatusmsg = {
		type : "updateStatus",
		ACtype : "Agent",
		id : parent.UserID_g,
		UserName : parent.UserName_g,
		status : aStatus,
		reason : aReason,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};
	// 發送消息
	parent.ws_g.send(JSON.stringify(updateAgentStatusmsg));
//	ws_g.send(JSON.stringify(updateAgentStatusmsg));
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
			fromAgentID : parent.UserID_g,
			invitedAgentID : aInvitedAgentID,
			fromAgentName : parent.UserName_g,
			inviteType: aInviteType,
			channel : "chat"
		};
	// 發送消息
	parent.ws_g.send(JSON.stringify(inviteAgent3waymsg));

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
			invitedAgentID : parent.UserID_g,
			response: aResponse,
			inviteType: aInviteType,
			channel : "chat"
//			fromAgentName : UserName
		};
	// 發送消息
	parent.ws_g.send(JSON.stringify(responseThirdPartyMsg));	
	
}


function RefreshRoomList(){
	var msg = {
		    type: "refreshRoomList",
		    channel : "chat",	
		  };
	//發送消息 
	parent.ws_g.send(JSON.stringify(msg));	
	
}


/** 2017/02/15 - 新增方法 **/
function switchStatus(aStatus){
	// 更新狀態資訊
	parent.document.getElementById("status").value = StatusEnum.toChinese(aStatus);
	// 去server更新狀態
	var myUpdateStatusJson = new updateStatusJson("Agent", parent.UserID_g, parent.UserName_g, aStatus, "no reason");
	parent.ws_g.send(JSON.stringify(myUpdateStatusJson));
//	updateStatus("ready");
	// 從server取得狀態
	getStatus();

	switch(aStatus) {
    case StatusEnum.LOGIN:
		var frames = window.parent.frames; // or // var frames = window.parent.frames;
		for (var i = 0; i < frames.length; i++) { 
		  // do something with each subframe as frames[i]
//			alert(frames[i].name);
			if ("ChatFrame" == frames[i].name){
				frames[i].document.getElementById("ready").disabled = false;
				frames[i].document.getElementById("notready").disabled = true;
			}
//			frames[i].document.body.style.background = "red";
		}
		isonline = true;
		
		document.getElementById("UserID").value = parent.UserID_g;
		document.getElementById("Login").disabled = true;
		document.getElementById("Logout").disabled = false;


        break;
    case StatusEnum.LOGOUT:
		var frames = window.parent.frames; // or // var frames = window.parent.frames;
		for (var i = 0; i < frames.length; i++) { 
		  // do something with each subframe as frames[i]
//			alert(frames[i].name);
			if ("ChatFrame" == frames[i].name){
				frames[i].document.getElementById("ready").disabled = true;
				frames[i].document.getElementById("notready").disabled = true;
			}
//			frames[i].document.body.style.background = "red";
		}
		isonline = false;
		
		document.getElementById("Logout").disabled = true;
		document.getElementById("Login").disabled = false;
		document.getElementById("UserID").value = '';

        // code block
        break;
    case StatusEnum.READY:

    	document.getElementById("ready").disabled = true;
    	document.getElementById("notready").disabled = false;
        //code block
        break;
    case StatusEnum.NOT_READY:
    	//code block
    	document.getElementById("ready").disabled = false;
    	document.getElementById("notready").disabled = true;
    	break;
    case StatusEnum.AFTER_CALL_WORK:
    	//code block
    	break;
    case StatusEnum.RING:
    	//code block
    	break;
    case StatusEnum.I_ESTABLISHED:
    	//code block
    	break;
    case StatusEnum.O_ESTABLISHED:
    	//code block
    	break;
//    case StatusEnum.:
//    	//code block
//    	break;
    default:
        break;
	}
}
var StatusEnum = Object.freeze({
	LOGIN: '1', 
	LOGOUT: '2', 
	READY: '3',
	NOT_READY: '4',
	AFTER_CALL_WORK: '5',
	RING: '6',
	I_ESTABLISHED: '7',
	O_ESTABLISHED: '8',
	
	toChinese : function(aStatusEnumIndex) { // Method which will display type of Animal
//		console.log(this.type);
//		console.log("aStatusEnumIndex: " + aStatusEnumIndex);
		aStatusEnumIndex -= 1; // 為了符合array起始為零
//						 0	    1      2        3      4        5      6        7		
		var converter = ["登入", "登出", "準備就緒", "離席", "文書處理", "響鈴", "進線通話", "外撥通話"];
//		alert(converter[aStatusEnumIndex]);
//		alert(converter[2]);
//		console.log("converter[aStatusEnumIndex]: " + converter[aStatusEnumIndex]);
		return converter[aStatusEnumIndex];
	}	
	
});

var Animal = {
		type: "Invertebrates", // Default value of properties
		displayType : function() { // Method which will display type of Animal
			console.log(this.type);
		}
}






/** 現在未使用方法 **/
//查詢線上人數
function online() {
	// 向websocket送出查詢線上人數指令
	var now = new Date();
	var msg = {
		type : "online",
		UserName : parent.UserName_g,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};
	// 發送消息
	parent.ws_g.send(JSON.stringify(msg));
}

//查詢群組線上人數
function roomonline() {
	// 向websocket送出查詢群組線上人數指令
	var now = new Date();
	var msg = {
		type : "roomonline",
		roomID : RoomID_g,
		UserName : parent.UserName_g,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息
	parent.ws_g.send(JSON.stringify(msg));
}



// 查詢client線上人數
function Clientonline() {
	// 向websocket送出查詢client線上人數指令
	var now = new Date();
	var msg = {
		type : "typeonline",
		ACtype : "Client",
		UserName : parent.UserName_g,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};
	// 發送消息
	parent.ws_g.send(JSON.stringify(msg));
}

// 查詢agent線上人數
function Agentonline() {
	// 向websocket送出查詢agent線上人數指令
	var now = new Date();
	var msg = {
		type : "typeonline",
		ACtype : "Agent",
		UserName : parent.UserName_g,
		channel : "chat",
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};
	// 發送消息
	parent.ws_g.send(JSON.stringify(msg));
}

function updateRoomIDList(aNewRoomID){
	// 先清空原list
	$("#roomList").empty();
	// 開始更新roomList
	var rows = "";
	roomInfoMap_g.forEach(function(roomInfo, roomID) {
		  console.log(roomID + ' = ' + roomInfo);
		  rows += '<option value=' + '"'+ roomID +'"' + '>' + '"'+ roomID +'"' + '</option>';
	});
	$( rows ).appendTo( "#roomList" );
	// 建立select onchange事件, 一選取之後則將room info所有欄位更新
	$('#roomList').unbind('change').change(function() { 
		var tmpRoomID = $('#roomList').val();
		updateRoomInfo(roomInfoMap_g.get(tmpRoomID));
	});
	// 主要trigger onchange事件
	$("#roomList").val(aNewRoomID).change();
	
//	$('#roomList:last-child').append(
//			'<option value=' + '"someValue"' + '>' + 'someText' + '</option>'
//		);
//	var rows = "";
//	$.each(items, function(){
//	    rows += '<option value=' + '"someValue"' + '>' + 'someText' + '</option>';
//	});
//
//	$( rows ).appendTo( "#roomList" );
}

function updateRoomInfo(aRoomInfo){
	console.log("updateRoomInfo()");
//	seeAllKV(aRoomInfo);
	
	$('#userdata')[0].innerHTML = aRoomInfo.userdata;
	$('#chatroom')[0].innerHTML = aRoomInfo.text;
	$('#sendToRoom')[0].roomID = aRoomInfo.roomID;
	$('#leaveRoom')[0].roomID = aRoomInfo.roomID;
	// 開啟按鈕
//	alert("!aRoomInfo.close: " + !aRoomInfo.close);
	if (!aRoomInfo.close){
		$('#leaveRoom')[0].disabled = false;
		$('#sendToRoom')[0].disabled = false;
		$('#inviteTransfer')[0].disabled = false;
		$('#inviteThirdParty')[0].disabled = false;		
		$('#message')[0].disabled = false;		
	}else{
		$('#leaveRoom')[0].disabled = true;
		$('#sendToRoom')[0].disabled = true;
		$('#inviteTransfer')[0].disabled = true;
		$('#inviteThirdParty')[0].disabled = true;				
		$('#message')[0].disabled = true;				
	}

}

// 測試按鈕
function test() {
	console.log("test method called");
	var testmsg = {
		    type: "test",
		    channel : "chat"
		  };
	//發送消息 
	parent.ws_g.send(JSON.stringify(testmsg));
}

