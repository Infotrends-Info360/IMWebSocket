<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">
<title>Infotrends ViewKPI Mode</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">

<script type="text/javascript" src="js/jquery.min.js"></script>

</head>
<body onbeforeunload="checktoLeave()"> 
	<button type="submit" onclick="online();" id="online">查詢在線人數</button>
	<button type="submit" onclick="grouponline();" id="grouponline">查詢群組在線人數</button>
	<input type="text" name="group" id="group">
	<br>
	<button type="submit" onclick="Clientonline();" id="Clientonline">查詢Client在線人數</button>
	<button type="submit" onclick="Agentonline();" id="Agentonline">查詢Agent在線人數</button>
	<span id="typeonline"> </span>
	<span id="onlineview"> </span>
	
	<br>訊息: <br>
	<span id="text"> </span>
	
	
<script type="text/javascript">
var UserName = "kpimanager";
var UserID;
var now = new Date();
//連上websocket
ws = new WebSocket('ws://127.0.0.1:8888')

//當websocket連接建立成功時
ws.onopen = function() {
    console.log('websocket 打開成功');    
};
//當收到服務端的消息時
ws.onmessage = function(e) {
    //e.data 是服務端發來的資料
	if("{"==e.data.substring(0,1)){
		var obj = jQuery.parseJSON(e.data);
		if("onlineinTYPE"==obj.Event){
			console.log('AgentID: '+obj.from);
			console.log('AgentName: '+obj.username);
		}else if("userjoin"==obj.Event){
			UserID = obj.from;
			//document.getElementById("Event").innerHTML = obj.Event;
		}
	}
	//非指令訊息
	else{
		document.getElementById("text").innerHTML += e.data + "<br>";
	}
    console.log(e.data);
};

// 當websocket關閉時
ws.onclose = function() {
    console.log("websocket 連接關閉");
};

// 當出現錯誤時
ws.onerror = function() {
    console.log("出現錯誤");
};
/*
now = new Date();
//向websocket送出登入指令
var msg = {
	    type: "login",
		UserName: UserName,
		ACtype: "Agent",
		channel: "chat",
	    date: now.getHours()+":"+now.getMinutes()+":"+now.getSeconds()
	  };
setTimeout(function(){
	//發送消息 
	ws.send(JSON.stringify(msg));
	
	setTimeout(function(){
		
		//向websocket送出登入Agent列表指令
		var now = new Date();
		var msg = {
			    type: "typein",
			    ACtype: "Agent",
			    id: UserID,
				UserName: UserName,
				channel: "chat",
			    date: now.getHours()+":"+now.getMinutes()+":"+now.getSeconds()
			  };
		setTimeout(function(){
			//發送消息 
			ws.send(JSON.stringify(msg));
		}, 500);
	}, 500);
}, 500);
*/
//查詢線上人數
function getkpi() {
	//向websocket送出查詢線上人數指令
	var now = new Date();
	var msg = {
		    type: "getkpi",
		    date: now.getHours()+":"+now.getMinutes()+":"+now.getSeconds()
		  };
	//發送消息 
	ws.send(JSON.stringify(msg));
}

//查詢線上人數
function online() {
	//向websocket送出查詢線上人數指令
	var now = new Date();
	var msg = {
		    type: "online",
		    id: UserID,
		    date: now.getHours()+":"+now.getMinutes()+":"+now.getSeconds()
		  };
	//發送消息 
	ws.send(JSON.stringify(msg));
}

//查詢群組線上人數
function grouponline() {
	var GroupID = document.getElementById('group').value;
	//向websocket送出查詢群組線上人數指令
	var now = new Date();
	var msg = {
		    type: "grouponline",
		    group: GroupID,
		    id: UserID,
		    date: now.getHours()+":"+now.getMinutes()+":"+now.getSeconds()
		  };
	
	//發送消息 
	ws.send(JSON.stringify(msg));
}

//查詢client線上人數
function Clientonline() {
	//向websocket送出查詢client線上人數指令
	var now = new Date();
	var msg = {
		    type: "typeonline",
		    ACtype: "Client",
			channel: "chat",
			id: UserID,
		    date: now.getHours()+":"+now.getMinutes()+":"+now.getSeconds()
		  };
	//發送消息 
	ws.send(JSON.stringify(msg));
}

//查詢agent線上人數
function Agentonline() {
	//向websocket送出查詢agent線上人數指令
	var now = new Date();
	var msg = {
		    type: "typeonline",
		    ACtype: "Agent",
			channel: "chat",
			id: UserID,
		    date: now.getHours()+":"+now.getMinutes()+":"+now.getSeconds()
		  };
	//發送消息 
	ws.send(JSON.stringify(msg));
}

//function thread1(value, maxValue){
function thread1(value){
    var me = this;
    document.getElementById("onlineview").innerHTML = value;
    value++;
    setTimeout(function () { me.thread1(value); }, 1000);
  /*
    if(value<=maxValue)
        setTimeout(function () { me.thread1(value, maxValue); }, 1000);
  */
}
setTimeout(function () { online(); }, 1000);
thread1(0);
</script>

</body>
</html>