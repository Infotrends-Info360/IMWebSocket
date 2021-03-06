
var ws_g; // websocket
var UserName_g; // 使用者名稱全域變數
var UserID_g; // 使用者ID全域變數
var agentIDMap_g = new Map(); // 用於私訊部分, 更新現在在線的Agent清單
var agentNameMap_g = new Map(); // 用於私訊部分, 更新現在在線的Agent清單
var waittingClientIDList_g = []; // 用處: 當LOGOUT時,告知所有有寄給此Agent請求的其他Client不用再等了
var waittingAgentIDList_g = []; // 用處: 當LOGOUT時,告知所有有寄給此Agent請求的其他Agents不用再等了
var systemParam_g;
/** 狀態相關 **/
var isonline_g = false; // 判斷是否上線的開關
var status_g; // 狀態全域變數
var notreadyreason_dbid_g = 0; //not ready Reason - 於皆換成NOTREADY狀態時傳入StatusEnum.updateState()方法中
/** room相關 **/
var RoomID_g; // 此為第一個加入的RoomID, 僅為開發過程使用, 不符合目前架構, 為過度開發期間保留的全域變數
var roomInfoMap_g = new Map(); // 用於房間列表,存著目前已開啟的通話相關資訊,詳細屬性成員請見util.js - RoomInfo
var maxRoomCount_g; // 每個Agent最多可接通話數
var currRoomCount_g = 0; //每個Agent現在已接通話數
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
	
		
	$("#A2AContent").keypress(function(e) {
	    if(e.which == 13) {
	       //alert('You pressed enter!');
	//	    	alert("$('#sendToRoom')[0].roomID: " + $('#sendToRoom')[0].roomID);
	    	sendA2A($('#privateMsg')[0].agentID);
		    }
	});
	
	// get systemParam , ex. protocol, hostname, port
	systemParam_g = JSON.parse( document.getElementById('systemParam').value );

	
	
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

//帳號密碼驗證
function loginValidate() {
	console.log("document.getElementById('systemParam').value: " + document.getElementById('systemParam').value);
	var systemParam = JSON.parse( document.getElementById('systemParam').value );
	var url = systemParam_g.RESTful.protocol + "//" + systemParam_g.RESTful.hostname + ":" + systemParam_g.RESTful.port + systemParam_g.RESTful.project;
//	alert("url: " + url);
	var account = document.getElementById('Account').value;
	var password = document.getElementById('Password').value;
	$
			.ajax({
				url : url + "/RESTful/Login",
				data : {
					account : account,
					password : password
				},
				type : "POST",
				dataType : 'json',
				error : function(e) {
					console.log("請重新整理");
				},
				success : function(data) {
					console.log("login", data)

					// 測試用必驗證過
					// 							doConnect();

					if (account == "" || password == "") {
						// 未輸入帳號與密碼
						console.log(data.error)
						$("#loginDialogButton").trigger("click");
					} else if (data.error != null) {
						// 其他可能錯誤
						console.log(data.error);
						$("#loginDialogButton").trigger("click");
					} else {
						// 驗證通過
						//console.log(JSON.stringify(data));
						maxRoomCount_g = "" + data.person[0].max_count;
						console.log("maxRoomCount_g: " + maxRoomCount_g);
						//console.log(data.person[0].max_count);
						document.getElementById('UserName').value = data.person[0].user_name;
						document.getElementById('UserID').value = data.person[0].dbid;
						parent.UserID_g = data.person[0].dbid;
						parent.UserName_g = data.person[0].user_name;

						// Step-1 載入時連線ws
						Login();
					}

				},
				beforeSend : function() {
					// 					$('#loading').show();
				},
				complete : function() {
					// 					$('#loading').hide();

				}
			});
}


// 連上websocket
// Login()成功呼叫後:
// 會收到"userjoin"事件回應
function Login() {
	console.log("Agent Login() function");
	
	// 清空waittingClientIDList_g
	waittingClientIDList_g = [];
	// 清空waittingClientIDList_g		
	waittingAgentIDList_g = [];
	
	
	// 連上websocket
	var url = systemParam_g.websocket.protocol + "//" + systemParam_g.websocket.hostname + ":" + systemParam_g.websocket.port;
	parent.ws_g = new WebSocket(url);
	
	// 當websocket連接建立成功時,送出login請求
	parent.ws_g.onopen = function() {
		console.log('websocket 打開成功');
		/** 登入 **/
		var now = new Date();
		// 向websocket送出登入指令
		var msg = {
			type : "login",
			id: parent.UserID_g,
			UserName : parent.UserName_g,
			maxCount: maxRoomCount_g, // 從loginValidate取得
			ACtype : "Agent",
			channel : "chat",
			date : now.getHours() + ":" + now.getMinutes() + ":"
					+ now.getSeconds()
		};			
		// 發送消息
		parent.ws_g.send(JSON.stringify(msg));
	};// end of parent.ws_g.onopen
		
	// 當收到服務端的消息時
	parent.ws_g.onmessage = function(e) {
		// e.data 是服務端發來的資料
		if ("{" == e.data.substring(0, 1)) {
			var obj = jQuery.parseJSON(e.data);
			// 接收到Client邀請chat的event
			if ("findAgentEvent" == obj.Event) {
				// (此區塊已改由"senduserdata"區塊作替代)
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
//				console.log("AcceptEvent: *****");
				var chatRoomMsg = obj.chatRoomMsg; // 接收系統訊息
				
				// 建立RoomInfo資訊
				RoomID_g = obj.roomID; // 之後要改成local variable
				var tmpRoomInfo = new RoomInfo(
						obj.roomID,
						$('#Accept')[0].userdata,
						'' // aText
				);
				tmpRoomInfo.text += chatRoomMsg + "<br>"; // 加入系統訊息
				roomInfoMap_g.set(obj.roomID, tmpRoomInfo);
				
				console.log("roomInfoMap_g.size: " + roomInfoMap_g.size);
				updateRoomIDList(obj.roomID);
								
			}else if ("RejectEvent" == obj.Event){
//					alert("RejectEvent received");
				// 將此clientID從waittingClientIDList_g中去除
				var index_remove;
				for (var index in waittingClientIDList_g) {
					clientIDJson = waittingClientIDList_g[index];
					var clientID = clientIDJson.clientID;
					if (currClientID == clientID){
						index_remove = index;
					}
//						console.log("clietIDJson.clientID: " + clientIDJson.clientID);
				}
				waittingClientIDList_g.splice(index_remove,1);
//					console.log("waittingClientIDList_g.length: " + waittingClientIDList_g.length);		
				
				//更新狀態
//					StatusEnum.ring_dbid = StatusEnum.updateStatus(StatusEnum.RING, "end", StatusEnum.ring_dbid);
			}else if ("getUserStatus" == obj.Event) {
//					console.log("onMessage(): getUserStatus called");
//					document.getElementById("status").innerHTML = "狀態: "
//							+ obj.Status + "<br>Reason: " + obj.Reason; 
				// 接收到找尋Client的UserData的訊息
			} else if ("senduserdata" == obj.Event) {
				// (在這邊取代原本findAgentEvent事件所做的事情)
//					console.log("senduserdata - ")
//					console.log("obj.clientName: " + obj.clientName);
//					console.log("obj.clientID: " + obj.clientID);
				var clientID = obj.clientID; 
				var clientName = obj.clientName; 
				waittingClientIDList_g.push(obj.userdata.id);
				
				/*** 動態增加request list ***/
			    var tr = document.createElement('tr');   
			    tr.setAttribute("style","height: 60px;");

			    var td1 = document.createElement('td');
			    var td2 = document.createElement('td');
			    var td3 = document.createElement('td');

			    var text1 = document.createTextNode('通話');
			    var text2 = document.createTextNode(obj.clientName);
			    var text3 = document.createTextNode(JSON.stringify(obj.userdata));
			    var div3 = document.createElement('div');
//				    div3.setAttribute("style", "height: 10px; overflow-y: hidden; width: 100%;");
			    div3.setAttribute("style", "max-height: 60px; word-wrap: break-word;; max-width: 260px;");
			    div3.setAttribute("class", "pre-scrollable");
			    
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
			    
//				    document.getElementById("requestTable").appendChild(tr);
			    document.getElementById("requestTable_tbody").appendChild(tr);
			    
			    // 設定 AcceptEventInit
			    tr.onclick = function(e){ 
					$('#Accept')[0].disabled = false;
					$('#Accept')[0].reqType = 'Client';
					$('#Accept')[0].userID = this.getAttribute("userID");
					$('#Accept')[0].userdata = this.getAttribute("userdata");

					$('#Reject')[0].disabled = false;
					$('#Reject')[0].reqType = 'Client';
					$('#Reject')[0].userID = this.getAttribute("userID");
//						$('#Reject')[0].userdata = this.getAttribute("userdata");
				}; 
			    				
			} else if ("userjointoTYPE" == obj.Event) {
				// 此區塊已沒再使用
				
				// 接收到有人登入的訊息
			} else if ("userjoin" == obj.Event) {
//					alert('userjoin'); // win
//				alert("isLoggedIn: " + obj.isLoggedIn);
				if (obj.isLoggedIn){
					alert(obj.isLoggedInText);
				}
//					console.log("userjoin!");
				// 拿取參數
				parent.UserID_g = obj.from;
//				maxRoomCount_g = obj.MaxCount; // 正式用 // 已於loginValidate取得maxRoomCount_g
//					maxRoomCount_g = 2; // 測試用
				var statusList = obj.statusList;
				var reasonList = obj.reasonList;
				
				// 更新maxCount畫面
//				$('#maxRoomCount')[0].innerHTML = currRoomCount_g + " / " + maxRoomCount_g;
				
				//20170222 Lin
				for(var key in reasonList){
					var new_option = new Option(reasonList[key].statusname_tw, reasonList[key].dbid);
					document.getElementById('reasonList').options.add(new_option)
				}
				
				document.getElementById("reasonList").addEventListener("change", myFunction);

				function myFunction() {
				    var x = document.getElementById("reasonList");
				    console.log("Not Ready Reason --- "+ x.options[x.selectedIndex].text);
				    console.log("Not Ready Reason Value --- "+ x.value);
				    notreadyreason_dbid_g = x.value;
				}
				//20170222 Lin
				
				// 更新statusList - enum
				// 格式: {Login={description=登入, dbid=1}, Ring={description=響鈴, dbid=6}
				console.log("***Enum - 更新enum: ");
				jQuery.each(statusList, function(key, val) {
					var tmpStatusEnum = StatusEnum.getStatusEnum(key);
					tmpStatusEnum.dbid = val.dbid;
					tmpStatusEnum.description = val.description;
				});
				
				
			} else if ("userjoinAgain" == obj.Event) {
//				alert("someone logged in again");
				alert(obj.text);
				switchStatus(StatusEnum.LOGOUT);
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
				var userdata = JSON.stringify( obj.userdata );
				var text = obj.text;
				
				waittingAgentIDList_g.push( new function(){
					this.agentID = fromAgentID
				});
				
//					alert("inviteAgentThirdParty - obj.text: " + obj.text);
//					alert("inviteAgentThirdParty - obj.text: " + JSON.stringify( obj.text ));

				//在這邊動態新增request
			    var tr = document.createElement('tr');   

			    var td1 = document.createElement('td');
			    var td2 = document.createElement('td');
			    var td3 = document.createElement('td');

			    var text1 = document.createTextNode(inviteType);
			    var text2 = document.createTextNode(fromAgentID);
			    var text3 = document.createTextNode('');
			    var div3 = document.createElement('div');
			    div3.setAttribute("style", "height: 20px; overflow-y: hidden;");
			    
			    td1.appendChild(text1);
			    td2.appendChild(text2);
			    div3.appendChild(text3);
			    td3.appendChild(div3);
			    
			    tr.appendChild(td1);
			    tr.appendChild(td2);
			    tr.appendChild(td3);

			    tr.setAttribute("id", fromAgentID);
			    tr.setAttribute("userID", fromAgentID);
			    tr.setAttribute("roomID", tmpRoomID);
			    tr.setAttribute("userdata", userdata);
			    tr.setAttribute("text", text);
//				    tr.setAttribute("userdata", userdata);
			    
//				    document.getElementById("requestTable").appendChild(tr);
			    document.getElementById("requestTable_tbody").appendChild(tr);
			    
			    tr.onclick = function(e){ 
					$('#Accept')[0].disabled = false;
					$('#Accept')[0].reqType = inviteType;
					$('#Accept')[0].userID = this.getAttribute("userID");
					$('#Accept')[0].roomID = this.getAttribute("roomID");
					$('#Accept')[0].userdata = this.getAttribute("userdata");
					$('#Accept')[0].text = this.getAttribute("text");
					
					$('#Reject')[0].disabled = false;
					$('#Reject')[0].reqType = inviteType;
					$('#Reject')[0].userID = this.getAttribute("userID");
					$('#Reject')[0].roomID = this.getAttribute("roomID");
				}; // 設定 AcceptEventInit

			} else if ("responseThirdParty" == obj.Event){
//				alert("responseThirdParty - obj: " + JSON.stringify( obj ));
				var chatRoomMsg = obj.chatRoomMsg; // 接收系統訊息
				var userdata = JSON.stringify( obj.userdata );
				var text = obj.text + chatRoomMsg + "<br>";
				var inviteType = obj.inviteType;
				var fromAgentID = obj.fromAgentID;
				var roomID = obj.roomID;
				var response = obj.response;
				var invitedAgentID = obj.invitedAgentID;
				
				// 若回應為拒絕,則不需再往下進行頁面更新
				if("timeout" == response){
					alert( "Agent " + invitedAgentID + " timeout " + inviteType +  " invitation");
					return;
				}				
				
				// 若回應為拒絕,則不需再往下進行頁面更新
				if("reject" == response){
					alert( "Agent " + invitedAgentID + " rejected " + inviteType +  " invitation");
					return;
				}
				
				// 如果轉接成功,則自己將已經被移出房間,現在是後端要求前端更新頁面
				if ("transfer" == inviteType && fromAgentID == parent.UserID_g){
					alert("transfer");
					var roomInfo = roomInfoMap_g.get(roomID);
					roomInfo.close = true;
					console.log("roomInfo: " + roomInfo);
//						console.log("JSON.parse(roomInfo): " + JSON.parse(roomInfo));
					console.log("JSON.stringify(roomInfo): " + JSON.stringify(roomInfo));
					// 若為當前頁面,則更新roomInfo
					var currRoomID = $('#roomList').val();
					console.log("currRoomID: " + currRoomID);
					console.log("currRoomID: " + currRoomID);
					console.log("obj.roomID: " + roomID);
					
					if (currRoomID == roomID){
						alert("matched!");
						updateRoomInfo(roomInfo);
					}
					return;
				}				
//				console.log("userdata: " + userdata);

				// 在這邊興建roomList與其room bean
				RoomID_g = roomID; // 之後要改成local variable
				var tmpRoomInfo = new RoomInfo(
						obj.roomID,
						userdata, // userdata
						text // text
				);
				roomInfoMap_g.set(roomID, tmpRoomInfo);
				console.log("roomInfoMap_g.size: " + roomInfoMap_g.size);
				// 1. 研究一下這個map的json長什麼樣 2. 看怎麼拿值
				updateRoomIDList(roomID);
				// 更新roomIDList

			} else if ("privateMsg" == obj.Event){
//					console.log("onMessage - privateMsg" + obj.UserName + ": " + obj.text + "&#13;&#10");
				// 最新的訊息再最上面
				document.getElementById("text").innerHTML = obj.UserName + ": " + obj.text + "&#13;&#10" + "<br>" + document.getElementById("text").innerHTML;
				
			} else if ("removeUserinroom" == obj.Event){
//					alert(obj.result);
				var fromUserID = obj.fromUserID;
				var roomInfo = roomInfoMap_g.get(obj.roomID);
				// 只收取他人所產生的系統訊息
				if (fromUserID != parent.UserID_g){
					var chatRoomMsg = obj.chatRoomMsg; // 接收系統訊息
					var leftRoomMsg = chatRoomMsg.leftRoomMsg;
					var closedRoomMsg = chatRoomMsg.closedRoomMsg;
//						alert("obj.chatRoomMsg.leftRoomMsg: " + obj.chatRoomMsg.leftRoomMsg);
//						alert("obj.chatRoomMsg.closedRoomMsg: " + obj.chatRoomMsg.closedRoomMsg);
					roomInfo.text += leftRoomMsg + "<br>"; // 更新系統訊息
					if (closedRoomMsg != undefined)
						roomInfo.text += closedRoomMsg + "<br>"; // 更新系統訊息
					
					var currRoomID = $('#roomList').val();
					if (currRoomID == obj.roomID){
						updateRoomInfo(roomInfo);
					}
				}// end of if 
				
				// 如果還沒關,就不往下走(要注意!) ****** 
				if (obj.roomSize != 0) return;
				
				// 若此房間已經關了, 則更新roomInfo
				// 將對應到的roomInfo標示為close
				roomInfo.close = true;
					// 若為當前頁面,則更新roomInfo
				var currRoomID = $('#roomList').val();
				if (currRoomID == obj.roomID){
					updateRoomInfo(roomInfo);
				}
				
			} else if ("clientLeft" == obj.Event){
				// 在這邊進行一連串的善後處理
				alert("Client left : " + obj.from);
				ready();
			} else if ("refreshAgentList" == obj.Event){
//					alert(obj.fromAgentID + " logined!");
				console.log("refreshAgentList - agentList: " + obj.agentList);
				var agentListJsonArray = obj.agentList;
			    agentIDMap_g.clear();
			    agentNameMap_g.clear();
			    var i;			
				for (i = 0; i < agentListJsonArray.length; i++) {
					var agentUserInfo = agentListJsonArray[i];
					var agentID = agentUserInfo.userid;
					var agentName = agentUserInfo.username;
					var isReady = agentUserInfo.ready; // "true" or "false" // 可用來判斷是否加入list, demo版本上未使用
//					alert("isReady: " + isReady);
					// ID
					console.log("agentID: " + agentID);
					console.log("parent.UserID_g: " + parent.UserID_g);
					if (agentID == parent.UserID_g) continue;
					agentIDMap_g.set(agentID, agentID);
					// Name
					console.log("agentName: " + agentName);
					console.log("parent.UserName_g: " + parent.UserName_g);
					if (agentName == parent.UserName_g) continue;
					agentNameMap_g.set(agentID, agentName);
					
				}
				console.log("refreshAgentList - agentIDMap_g: " + agentIDMap_g);
				console.log("refreshAgentList - agentNameMap_g: " + agentNameMap_g);
				// 更新頁面
				updateAgentIDList();
			} else if ("agentLeftThirdParty" == obj.Event){
				alert("agentLeftThirdParty - agent " + obj.id + " left ");
			} else if ("updateStatus" == obj.Event){
				console.log("obj.maxCountReached: " + obj.maxCountReached);
				if (obj.maxCountReached){
					alert("maxCountReached!");
				}
//				alert("obj.currStatusEnum: " + obj.currStatusEnum);
				var startORend = obj.startORend;
				var currStatusEnum = StatusEnum.getStatusEnum(obj.currStatusEnum);
				switchStatus(currStatusEnum); // 更新畫面
				
			} else if ("ringTimeout" == obj.Event){
				alert("ringTimeout");
				var currClientID = obj.clientID;
				console.log("obj.clientID: " + obj.clientID); 
				/*** 清理畫面 ***/
				// 1. 將此請求從request list中去除掉	
				$('#' + currClientID).remove(); // <tr>的id
				// 2. 將此clientID從waittingClientIDList_g中去除
				var index_remove;
				for (var index in waittingClientIDList_g) {
					clientIDJson = waittingClientIDList_g[index];
					var clientID = clientIDJson.clientID;
					if (currClientID == clientID){
						index_remove = index;
					}
				}
				console.log("before - waittingClientIDList_g.length: " + waittingClientIDList_g.length);
				waittingClientIDList_g.splice(index_remove,1);
				console.log("after - waittingClientIDList_g.length: " + waittingClientIDList_g.length);
				
				
			} else if ("Exit" == obj.Event){
//					alert("Exit");
//					console.log("ws 連線關閉。");
//					parent.ws_g.close(); // 在這邊關閉websocket,要特別注意會不會牽連到其他人

			}
		// 非指令訊息
		// (Billy哥部分)
		}  else if ("{" != e.data.substring(0, 1)) {
			console.log(e);
			// 非指令訊息
			if (e.data.indexOf("Offline") > 0 && e.data.indexOf(parent.UserName_g) > 0) {
				// 關閉websocket
//					console.log("ws 連線關閉。");
//					parent.ws_g.close(); // 在這邊關閉websocket,要特別注意會不會牽連到其他人
			}
		} else {
			document.getElementById("text").innerHTML += e.data + "<br>";
		}
		console.log("onMessage(): " + e.data);
	}; // end of parent.ws_g.onmessage

	// 當websocket關閉時
	parent.ws_g.onclose = function() {
		console.log("websocket 連接關閉");
	}; // end of parent.ws_g.onclose

	// 當出現錯誤時
	parent.ws_g.onerror = function() {
		console.log("出現錯誤");
	}; // end of parent.ws_g.onerror
}

// 登出
function Logout() {
	// 執行登出
	Logoutaction(); // onClose()會全部清: Group, Type, User conn
	
}


// 執行登出
function Logoutaction() {	
	// 向websocket送出登出指令
	// 關閉上線開關
//	status_g = StatusEnum.LOGOUT;
	switchStatus(StatusEnum.LOGOUT);
//	console.log("StatusEnum.login_dbid: " + StatusEnum.login_dbid);
//	console.log("StatusEnum.notready_dbid: " + StatusEnum.notready_dbid);
//	console.log("StatusEnum.ready_dbid: " + StatusEnum.ready_dbid);
//	console.log("StatusEnum.ring_dbid: " + StatusEnum.ring_dbid);
//	console.log("StatusEnum.iestablished_dbid: " + StatusEnum.iestablished_dbid);
//	StatusEnum.login_dbid = StatusEnum.updateStatus(StatusEnum.LOGOUT, "end", StatusEnum.login_dbid);
//	StatusEnum.notready_dbid = StatusEnum.updateStatus(StatusEnum.NOTREADY, "end", StatusEnum.notready_dbid);
//	StatusEnum.ready_dbid = StatusEnum.updateStatus(StatusEnum.READY, "end", StatusEnum.ready_dbid);
//	StatusEnum.ring_dbid = StatusEnum.updateStatus(StatusEnum.RING, "end", StatusEnum.ring_dbid);
	// 下列改由後端處理 - WebSocketRoomPool.removeUserinroom()
//	StatusEnum.ring_dbid = StatusEnum.updateStatus(StatusEnum.IESTABLISHED, "end",JSON.stringify(StatusEnum.iestablished_dbid)); // 須用list
//	alert("waittingClientIDList_g.length: " + waittingClientIDList_g.length);
	// 登出動作
	var now = new Date();
	var msg = {
		type : "Exit",
		// text: message,
		id : parent.UserID_g,
		UserName : parent.UserName_g,
		channel : "chat",
		waittingClientIDList : waittingClientIDList_g, // 告訴寄出要求的clients不用等了
		waittingAgentIDList : waittingAgentIDList_g, // 告訴寄出三方/轉接邀請的Agents不用等了
		login_dbid : $('#Login')[0].getAttribute("login_dbid"),
		notready_dbid : $('#notready')[0].getAttribute("notready_dbid"),
		// waitingAgentRoomIDList : waitingAgentRoomIDList_g; //  告訴寄出三方/轉接邀請的Agents不用等了 - 若有需要,再考慮增加是對應到哪個roomID
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};
 
	// 發送消息
	parent.ws_g.send(JSON.stringify(msg));


	
//	// 關閉websocket
//	parent.ws_g.close();
}

//refresh或關閉網頁時執行
function checktoLeave() {
	event.returnValue = "確定要離開當前頁面嗎？";
	Logoutaction();
}

//Agent準備就緒
function ready() {
	// 更新頁面
	switchStatus(StatusEnum.READY);
	StatusEnum.updateStatus(StatusEnum.READY, "start");
	
}
// Agent尚未準備就緒
function notready() {
	// 更新頁面
	switchStatus(StatusEnum.NOTREADY);
	StatusEnum.updateStatus(StatusEnum.NOTREADY, "start", null, null, null, notreadyreason_dbid_g);
}

//同意與Client交談
function AcceptEventInit() {
	console.log("AcceptEventInit(): ");
	var reqType = $('#Accept')[0].reqType;
	if (reqType == 'thirdParty' || reqType == 'transfer'){
		var roomID = $('#Accept')[0].roomID;
		var fromAgentID = $('#Accept')[0].userID;
		responseThirdParty(reqType, roomID, fromAgentID, 'accept');
	// Client to Agent 請求	
	}else{
		// 一次將Agent與Client加入到room中
		var currClientID = $('#Accept')[0].userID;
//		var userdata = $('#Accept')[0].userdata;
			// 在此使用新的方法,將一個list的成員都加入到同一群組中
		var memberListToJoin = [];
		var mem1 = new myRoomMemberJsonObj(currClientID);
		var mem2 = new myRoomMemberJsonObj(parent.UserID_g);
		memberListToJoin.push(mem1);
		memberListToJoin.push(mem2);
		// addRoomForMany成功呼叫後,會收到"AcceptEvent"事件回應
		addRoomForMany("none", memberListToJoin); // "none"是一個keyword, 會影響websocket server的邏輯判斷處理
		
		// 將此clientID從waittingClientIDList_g中去除
		var index_remove;
		for (var index in waittingClientIDList_g) {
			clientIDJson = waittingClientIDList_g[index];
			var clientID = clientIDJson.clientID;
			if (currClientID == clientID){
				index_remove = index;
			}
//			console.log("clietIDJson.clientID: " + clientIDJson.clientID);
		}
		waittingClientIDList_g.splice(index_remove,1);
//		console.log("waittingClientIDList_g.length: " + waittingClientIDList_g.length);
	}// end of if
	
	// 將此請求從request list中去除掉
	var userID = $('#Accept')[0].userID;
	$('#' + userID).remove(); // <tr>的id
	
	// 開啟ready功能:
//	switchStatus(StatusEnum.NOTREADY); //
}

// 拒絕交談
function RejectEvent() {
	console.log("RejectEvent(): ");
	var reqType = $('#Reject')[0].reqType;
	if (reqType == 'thirdParty' || reqType == 'transfer'){
		//here
		var roomID = $('#Reject')[0].roomID;
		var fromAgentID = $('#Reject')[0].userID;
		responseThirdParty(reqType, roomID, fromAgentID, 'reject');
	// Client to Agent 請求	
	}else{
		var currClientID = $('#Accept')[0].userID;
		var now = new Date();
		var msg = {
			type : "RejectEvent",
			ACtype : "Agent",
			id : parent.UserID_g,
			UserName : parent.UserName_g,
			sendto : currClientID,
			channel : "chat",
			// Event: "RejectEvent",
			date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
		};
		// 發送消息
		parent.ws_g.send(JSON.stringify(msg));		
	}
	
	// 將此請求從request list中去除掉
	var userID = $('#Accept')[0].userID;
	console.log("userID: " + userID);
	$('#' + userID).remove(); // <tr>的id
	
}

// 將多人同時加入房間
// aMemberListToJoin船入格式如下:
// [{"ID":"c8013217-2b20-46c4-ba2d-848fa430775e"},{"ID":"773bc9f4-3462-4360-8b11-e35be56b820a"}]
// addRoomForMany成功呼叫後:
// 會收到"AcceptEvent"事件回應
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
	if (aSendto === undefined){ 
		alert("Please select an agent"); 
		return;
	}
	if (aMessage === undefined) aMessage = document.getElementById('message').value;
	
	// 向websocket送出私訊指令
	var msg = {
		type : "message",
		text : aMessage,
		sendto : aSendto,
		channel : "chat"
	};
	// 發送消息
	parent.ws_g.send(JSON.stringify(msg));
}
//送出Agent to Agent私訊
function sendA2A(aSendto){
	var msg = document.getElementById("A2AContent").value;
	console.log("sendA2A() - msg: " + msg);
	send(aSendto,msg);
	document.getElementById("A2AContent").value = ''; // 清掉
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
	if (aRoomID === undefined) aRoomID = $('#roomList').val(); 
	if (aInvitedAgentID === undefined) aInvitedAgentID = $('#agentList').val(); 
	var userdata = JSON.parse( $('#userdata').html() );
	var roomInfo = roomInfoMap_g.get(aRoomID);
	var text = roomInfo.text;
	
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
			userdata : userdata,
			text: text,
			channel : "chat"
		};
	// 發送消息
	parent.ws_g.send(JSON.stringify(inviteAgent3waymsg));

	/**** 建立私訊 *****/
//	document.getElementById("sendA2A").value = aInvitedAgentID;
//	document.getElementById("sendA2A").innerHTML += " to - " + aInvitedAgentID;	

}

//回應三方/轉接
/* aResponse可能參數字串:
 * 1. accept
 * 2. reject
 */
function responseThirdParty(aInviteType, aRoomID, aFromAgentID, aResponse){
	if (aInviteType === undefined) aInviteType = document.getElementById("inviteType").innerHTML; // 開發過渡期使用,之後會修掉
	if (aRoomID === undefined) aRoomID = document.getElementById("invitedRoomID").innerHTML; // 開發過渡期使用,之後會修掉
	if (aFromAgentID === undefined) aFromAgentID = document.getElementById("fromAgentID").innerHTML; // 開發過渡期使用,之後會修掉

	var userdata;
	var text;
	if ("accept" == aResponse){
		userdata = JSON.parse( $('#Accept')[0].userdata );
		text = $('#Accept')[0].text;
	}else if ("reject" == aResponse) {
		userdata = JSON.parse( '{}' );
		text = "";
	}
	
//	alert("******* text: " + text);
	
	var responseThirdPartyMsg = {
			type : "responseThirdParty",
			ACtype : "Agent", 
			roomID : aRoomID, 
			fromAgentID : aFromAgentID, 
			invitedAgentID : parent.UserID_g,
			response: aResponse,
			inviteType: aInviteType,
			userdata : userdata,
			text: text,
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
function switchStatus(aStatusEnum){
	// 更新狀態資訊
	parent.document.getElementById("status").value = aStatusEnum.description;	
	
	switch(aStatusEnum) {
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
		// 改為離線
		isonline = true;
		// 將現有roomCount歸零
		currRoomCount_g = 0;
		
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
		// 改為離線
		isonline = false;
		// 將現有roomCount歸零
		currRoomCount_g = 0;
		
		// 控制前端頁面
		document.getElementById("Logout").disabled = true;
		document.getElementById("Login").disabled = false;
		document.getElementById("UserID").value = '';
		document.getElementById("Accept").disabled = true;
		document.getElementById("Reject").disabled = true;
		
		// 清空request list
		var myNode = document.getElementById("requestTable_tbody");
		while (myNode.firstChild) {
		    myNode.removeChild(myNode.firstChild);
		}
		
		// 清空AgentList
		$("#agentList").empty();
		
//		// 清空waittingClientIDList_g
//		waittingClientIDList_g = [];
//		// 清空waittingClientIDList_g		
//		waittingAgentIDList_g = [];

        // code block
        break;
    case StatusEnum.READY:

    	document.getElementById("ready").disabled = true;
    	document.getElementById("notready").disabled = false;
        //code block
        break;
    case StatusEnum.NOTREADY:
    	//code block
    	document.getElementById("ready").disabled = false;
    	document.getElementById("notready").disabled = true;
    	break;
    case StatusEnum.AFTERCALLWORK:
    	//code block
    	break;
    case StatusEnum.RING: // 當有RING事件時,同時也會切換為NOTREADY,故畫面更新同NOTREADY
    	switchStatus(StatusEnum.NOTREADY);
    	//code block
    	break;
    case StatusEnum.IESTABLISHED:
    	//code block
		document.getElementById("Accept").disabled = true;
		document.getElementById("Reject").disabled = true;
    	break;
    case StatusEnum.OESTABLISHED:
    	//code block
    	break;
//    case StatusEnum.:
//    	//code block
//    	break;
    default:
        break;
	}
}
//var StatusEnum = Object.freeze({
var StatusEnum = {
	LOGIN: { statusname : 'LOGIN', dbid : '0',description : '中文'}, 
	LOGOUT: { statusname : 'LOGOUT', dbid : '0',description : '中文'}, 
	READY: { statusname : 'READY', dbid : '0',description : '中文'}, 
	NOTREADY: { statusname : 'NOTREADY', dbid : '0',description : '中文'}, 
	AFTERCALLWORK: { statusname : 'AFTERCALLWORK', dbid : '0',description : '中文'}, 
	RING: { statusname : 'RING', dbid : '0',description : '中文'}, 
	IESTABLISHED: { statusname : 'IESTABLISHED', dbid : '0',description : '中文'}, 
	OESTABLISHED: { statusname : 'OESTABLISHED', dbid : '0',description : '中文'}, 
	REJECT: { statusname : 'REJECT', dbid : '0',description : '中文'}, 
	
	currStatusEnum : '',
	
	login_dbid : null,
	logout_dbid :  null,
	ready_dbid : null,
	notready_dbid : null,
	paperwork_dbid : null,
	ring_dbid : null,
	iestablished_dbid : [],
	oestablished_dbid : null,
	reject_dbid : null,
	
	getStatusEnum : function(aStatusname){
		aStatusname = aStatusname.toUpperCase();
		
		if (StatusEnum.LOGIN.statusname == aStatusname){
			return StatusEnum.LOGIN;
		}else if (StatusEnum.LOGOUT.statusname == aStatusname){
			return StatusEnum.LOGOUT;
		}else if (StatusEnum.READY.statusname == aStatusname){
			return StatusEnum.READY;
		}else if (StatusEnum.NOTREADY.statusname == aStatusname){
			return StatusEnum.NOTREADY;
		}else if (StatusEnum.AFTERCALLWORK.statusname == aStatusname){
			return StatusEnum.AFTERCALLWORK;
		}else if (StatusEnum.RING.statusname == aStatusname){
			return StatusEnum.RING;
		}else if (StatusEnum.IESTABLISHED.statusname == aStatusname){
			return StatusEnum.IESTABLISHED;
		}else if (StatusEnum.OESTABLISHED.statusname == aStatusname){
			return StatusEnum.OESTABLISHED;
		}else if (StatusEnum.REJECT.statusname == aStatusname){
			return StatusEnum.REJECT;
		}
//		Util.getConsoleLogger().debug("StatusEnmu - getStatusEnum: " + " no match");
		return null;
	},
	
	updateDbid : function(aObj){
		if (aObj.login_dbid != null)
			StatusEnum.login_dbid = aObj.login_dbid; 
		if (aObj.logout_dbid != null)
			StatusEnum.logout_dbid = aObj.logout_dbid; 
		if (aObj.ready_dbid != null)
			StatusEnum.ready_dbid = aObj.ready_dbid; 
		if (aObj.notready_dbid != null)
			StatusEnum.notready_dbid = aObj.notready_dbid; 
		if (aObj.aftercallwork_dbid != null)
			StatusEnum.aftercallwork_dbid = aObj.aftercallwork_dbid; 
		if (aObj.ring_dbid != null)
			StatusEnum.ring_dbid = aObj.ring_dbid; 
		if (aObj.iestablished_dbid != null)
			StatusEnum.iestablished_dbid.push(aObj.iestablished_dbid); 
		if (aObj.oestablished_dbid != null)
			StatusEnum.oestablished_dbid = aObj.oestablished_dbid; 
		if (aObj.reject_dbid != null)
			StatusEnum.reject_dbid = aObj.reject_dbid;
	},
	
	updateStatus : function( aStatusEnum , aStartORend, aDbid, aRoomID, aClientID, aReason_dbid){
		//更新現有狀態
		StatusEnum.currStatusEnum = aStatusEnum;
		 
		if ('end' == aStartORend && aDbid === undefined) {
			console.log("updateStatus - end - " + aStatusEnum.description + " aDbid not found");
			return;
		}
		
		// 更新狀態資訊
		parent.document.getElementById("status").value = aStatusEnum.description;
		// 去server更新狀態
		var myUpdateStatusJson = new updateStatusJson("Agent", parent.UserID_g, parent.UserName_g, 
														aStatusEnum.dbid, "no reason", aStartORend, 
														aDbid, aRoomID, aClientID,
														aReason_dbid);
		parent.ws_g.send(JSON.stringify(myUpdateStatusJson));
		
		if ('end' == aStartORend){
			return null;
		}
//		// 從server取得狀態
//		getStatus();
	}
	
};

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
	
	// 將focus移到寄送訊息欄
	$('#message').focus();
	
}

function updateAgentIDList(){
	// 保留著原本選取之選項
	var selectedAgentID = $("#agentList").val();
//	alert("updateAgentIDList() - selected: " + selectedAgentID);
	// 先清空原list
	$("#agentList").empty();
	// 開始更新roomList
	var rows = "<option disabled selected value> -- select an agent -- </option>";
	
	agentIDMap_g.forEach(function(value, AgentID) {
		if (selectedAgentID != AgentID){
			rows += '<option value=' + '"'+ AgentID +'"' + '>' + '"'+ agentNameMap_g.get(AgentID) +'"' + '</option>';
		}else{
			rows += '<option value=' + '"'+ AgentID +'"' + ' selected>' + '"'+ agentNameMap_g.get(AgentID) +'"' + '</option>';			
		}
		
	});		
	
	$( rows ).appendTo( "#agentList" );
	// 建立select onchange事件, 一選取之後則將room info所有欄位更新
	$('#agentList').unbind('change').change(function() { 
		var tmpAgentID = $('#agentList').val();
		$('#privateMsg')[0].agentID = tmpAgentID;
		// 將focus移到寄送訊息欄
		$('#A2AContent').focus();
//		alert("$('#privateMsg')[0].agentID: \n" + $('#privateMsg')[0].agentID);
//		updateRoomInfo(roomInfoMap_g.get(tmpRoomID));
	});
		
	// 主要trigger onchange事件
//	$("#agentList").val(aNewAgentID).change();
	
	
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
		$('#commentContent')[0].disabled = true; // ACW :  此為room關閉才開啟的功能
		$('#commentSend')[0].disabled = true; // ACW :  此為room關閉才開啟的功能
		
	}else{
		$('#leaveRoom')[0].disabled = true;
		$('#sendToRoom')[0].disabled = true;
		$('#inviteTransfer')[0].disabled = true;
		$('#inviteThirdParty')[0].disabled = true;				
		$('#message')[0].disabled = true;				
		
		if (!aRoomInfo.isAfterCallWorkDone){
			$('#commentContent')[0].disabled = false; //ACW :  此為room關閉才開啟的功能
			$('#commentSend')[0].disabled = false; // ACW :  此為room關閉才開啟的功能
		}else if(aRoomInfo.isAfterCallWorkDone){
			$('#commentContent')[0].disabled = true;
			$('#commentSend')[0].disabled = true;
		}
		
	}

}

function sendComment(aInteractionid, aActivitydataids, aComment){
//	alert("sendComment()");
	// 寄送請求至WS
	if (aInteractionid === undefined) aInteractionid = $('#roomList').val();
	if (aActivitydataids === undefined) aActivitydataids = '0';
	if (aComment === undefined) aComment = $('#commentContent').val();
	
//	alert("aComment: " + aComment);
	var mySendCommentJson = new sendCommentJson(aInteractionid, aActivitydataids, aComment);
	parent.ws_g.send(JSON.stringify(mySendCommentJson));	
	
	// 更新AFTERCALLWORK狀態結束時間
//	alert("StatusEnum.aftercallwork_dbid: " + StatusEnum.aftercallwork_dbid);
//	StatusEnum.aftercallwork_dbid = StatusEnum.updateStatus(StatusEnum.AFTERCALLWORK, "end", StatusEnum.aftercallwork_dbid);
	
	// 關閉ACW按鈕功能
	var roomInfo = roomInfoMap_g.get(aInteractionid);
	roomInfo.isAfterCallWorkDone = true;
	// 判斷若當前頁面就是這個訊息所傳的room, 則馬上更新
	var currRoomID = $('#roomList').val();
	if (currRoomID == roomInfo.roomID){
		updateRoomInfo(roomInfo);	
	}
}

function refreshAgentList_request(){
	
	var msg = {
			type : "refreshAgentList"
		};
	// 發送消息
	parent.ws_g.send(JSON.stringify(msg));	
	
	
}

// 測試按鈕
function account01(){
	$('#Account')[0].value = 'Holylin';
	$('#Password')[0].value = 'info@1111';
}

function account02(){
	$('#Account')[0].value = 'lintsai';
	$('#Password')[0].value = '1111';
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

