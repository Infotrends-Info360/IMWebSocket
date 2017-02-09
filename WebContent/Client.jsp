<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>


<!-- 取得IP -->
<%   String ip = request.getHeader("X-Forwarded-For");  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_CLIENT_IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getRemoteAddr();  
        }
       
        %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">
<title>Infotrends Client Mode</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">

<link rel="stylesheet" href="layui/css/layui.css">

<script type="text/javascript" src="js/swfobject.js"></script> <!-- for IE8 websocket compatibility -->
<script type="text/javascript" src="js/web_socket.js"></script> <!-- for IE8 websocket compatibility -->
<script type="text/javascript" src="js/websocket-util.js"></script>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/websocket-client.js"></script>

<!-- for IE8 websocket compatibility -->
<script type="text/javascript">
// Let the library know where WebSocketMain.swf is:
WEB_SOCKET_SWF_LOCATION = "WebSocketMain.swf";	
</script>


<script src="layui/layui.js"></script>
<!-- <script src="js/layimClient.js"></script> -->
</head>
<body onbeforeunload="checktoLeave()"> 
	UserName : <input type="text" name="UserName" id="UserName" value="A123456789">
	UserID : <input type="text" name="UserID" id="UserID" disabled>
	<!--sendto : <input type="text" name="sendto" id="sendto">-->
	<button type="submit" onclick="Login();" id="Login">Login(進入即時訊息頁面)</button>
	<button type="submit" onclick="Logout();" id="Logout" disabled>Logout(離開即時訊息頁面)</button>
<!-- 	<button type="submit" id="ready" onclick="ready();" >ready</button> -->
<!-- 	<button type="submit" id="notready" onclick="notready();" disabled>notready</button> -->
	<span id="status"> </span>
	用戶ID : <span id="showUserID"> </span>
	<br>
	<button type="submit" onclick="online();" id="online" disabled>查詢在線人數</button>
	<button type="submit" onclick="roomonline();" id="roomonline" disabled>查詢群組在線人數</button>
	<br>
	<button type="submit" onclick="Clientonline();" id="Clientonline" disabled>查詢Client在線人數</button>
	<button type="submit" onclick="Agentonline();" id="Agentonline" disabled>查詢Agent在線人數</button>
	<span id="typeonline"> </span>
	<br>
	群組 : <input type="text" name="roomID" id="roomID" disabled>
<!-- 	<button type="submit" onclick="addRoom();" id="addRoom" >加入群組</button> -->
	<button type="submit" onclick="leaveRoom();" id="leaveRoom" disabled>離開群組</button>
	<span id="groupstatus"> </span>
	<br>
	Invite Agent : <input type="text" name="findAgent" id="findAgent">
	<!--<button type="submit" onclick="find();" id="find" >邀請</button>-->
	<br>
	Event From : <input type="text" name="Eventfrom" id="Eventfrom" disabled>
	Event  : <input type="text" name="Event" id="Event" disabled>
	<br>
	<!--message : <input type="text" id="message">-->
	<!--<button type="submit" onclick="send();">send</button>-->
	<!--<button type="submit" onclick="sendtoRoom();" id="sendtoGroup">send to Group</button>-->
	<span id="span"> </span>
	<br>訊息: <br>
	<span id="text"> </span>
	<input name="Login_time" id="Login_time" value="" type="hidden" >
	<input name="web_language" id="web_language" value="" type="hidden" >
	<input name="Browser_Version" id="Browser_Version" value="" type="hidden">
	<input name="Channeltype" id="Channeltype" value="" type="hidden">
	<input name="Platfrom" id="Platfrom" value="" type="hidden">
	<input name="Cli_IP" id="Cli_IP" value="<%out.println(ip);%>" type="hidden">
	
	<!-- 三方轉接 -->
	<table border='2' id='invitedRoomListTable'>
		<tr> 
			<td>
			房間ID: <span id="currRoomID"></span><br>
			</td>
			<td>
			房間成員: <span id="currUsers"></span><br>
			</td>
		</tr>
	</table>
	
<!--  取得時間  -->	
<script language="javascript">
var Today=new Date();
document.getElementById("Login_time").value= Today.getFullYear()+"/"+ (Today.getMonth()+1) +"/"+ Today.getDate()+" "+ Today.getHours()+":"+ Today.getMinutes()+":"+ Today.getSeconds();

</script>
<!--  判斷使用者語言  -->
<script language="javascript">
document.getElementById("web_language").value = window.navigator.language;
</script>

<!--  判斷使用的瀏覽器  -->
<script type="text/javascript">
    var IE = navigator.userAgent.search("MSIE") > -1;
    var IE6 = navigator.userAgent.search("MSIE 6.0") > -1;
    var IE7 = navigator.userAgent.search("MSIE 7.0") > -1;
    var IE8 = navigator.userAgent.search("MSIE 8.0") > -1;
    var IE9 = navigator.userAgent.search("MSIE 9.0") > -1;
    var IE10 = navigator.userAgent.search("MSIE 10.0") > -1;
    var IE11 = navigator.userAgent.search("rv:11.0") > -1;
    var Firefox = navigator.userAgent.search("Firefox") > -1;
    var Opera = navigator.userAgent.search("Opera") > -1;
    var Safari = navigator.userAgent.search("Safari") > -1;//Google瀏覽器是用這核心
    
    if (IE) {
    	document.getElementById("Browser_Version").value="IE"; 
    }
    if (IE6) {
    	document.getElementById("Browser_Version").value="IE6";  
    }
    if (IE7) {
    	document.getElementById("Browser_Version").value="IE7";    
    }
    if (IE8) {
    	document.getElementById("Browser_Version").value="IE8";   
    }
    if (IE9) {
    	document.getElementById("Browser_Version").value="IE9";  
    }
    if (IE10) {
    	document.getElementById("Browser_Version").value="IE10";    
    }
    if (IE11) {
    	document.getElementById("Browser_Version").value="IE11";
    }
    if (Firefox) {
    	document.getElementById("Browser_Version").value="Firefox";
    }
    if (Opera) {
    	document.getElementById("Browser_Version").value="Opera"; 
    }
    if (Safari) {
    	document.getElementById("Browser_Version").value="Safari or Chrome";
    }
    
</script>

<!-- 判斷ChannelType -->
<script type="text/javascript">
	var mobiles = new Array
			(
				"midp", "j2me", "avant", "docomo", "novarra", "palmos", "palmsource",
				"240x320", "opwv", "chtml", "pda", "windows ce", "mmp/",
				"blackberry", "mib/", "symbian", "wireless", "nokia", "hand", "mobi",
				"phone", "cdm", "up.b", "audio", "sie-", "sec-", "samsung", "htc",
				"mot-", "mitsu", "sagem", "sony", "alcatel", "lg", "eric", "vx",
				"NEC", "philips", "mmm", "xx", "panasonic", "sharp", "wap", "sch",
				"rover", "pocket", "benq", "java", "pt", "pg", "vox", "amoi",
				"bird", "compal", "kg", "voda", "sany", "kdd", "dbt", "sendo",
				"sgh", "gradi", "jb", "dddi", "moto", "iphone", "android",
				"iPod", "incognito", "webmate", "dream", "cupcake", "webos",
				"s8000", "bada", "googlebot-mobile"
			)

	var ua = navigator.userAgent.toLowerCase();
	var isMobile = false;
	for (var i = 0; i < mobiles.length; i++) {
		if (ua.indexOf(mobiles[i]) > 0) {
			isMobile = true;
			break;
		}
	}
if(isMobile=true){
	document.getElementById("Channeltype").value="Web";
}else{
	document.getElementById("Channeltype").value="Phone";
}
</script>

<!--  判斷使用的系統  -->
<script type="text/javascript">
    var Windows  = navigator.userAgent.search("Windows") > -1;
    var Linux = navigator.userAgent.search("Linux") > -1;
    var Android  = navigator.userAgent.search("Android") > -1;
    var OS = navigator.userAgent.search("OS") > -1;

    if (Windows) {
    	document.getElementById("Platfrom").value="Windows";
    }
    if (Linux) {
    	document.getElementById("Platfrom").value="Linux";
    }
    if (Android) {
    	document.getElementById("Platfrom").value="Android"; 
    }
    if (OS) {
    	document.getElementById("Platfrom").value="iOS";
    }
</script>
</body>
</html>