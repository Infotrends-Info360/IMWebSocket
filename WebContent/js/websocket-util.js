

function updateStatusAction(aACType, aUserID, aUserName, aStatus, aReason) {
	console.log("updateStatusAction() called");
	var UserID = document.getElementById('UserID').value;
	var now = new Date();
	var updateAgentStatusmsg = {
		type : "updateStatus",
		ACtype : aACType,
		id : aUserID,
		UserName : aUserName,
		status : aStatus,
		reason : aReason,
		date : now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()
	};

	// 發送消息
	ws.send(JSON.stringify(updateAgentStatusmsg));
}