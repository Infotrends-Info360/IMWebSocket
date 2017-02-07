/********** util *************/
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

/********** bean *************/
//配合Object.create()使用,可收inheritance的效果
var messagetoRoomJson = {
		type : "messagetoRoom",
		ACtype : "",
		text : "",
		id : "",
		UserName : "",
		roomID : "",
		channel : "chat",
		date : "",
		buildup : function(aType, aACtype, aText, aId, aUserName, aRoomID, aChannel, aDate) {  // Method which will display type of Animal
			this.type = aType;
			this.ACtype = aACtype;
			this.text = aText;
			this.id = aId;
			this.UserName = aUserName;
			this.roomID = aRoomID;
			this.channel = aChannel;
			this.date = aDate;
		}			
}

// addRoomForMany
function myRoomMemberJsonObj(aID){
	this.ID = aID;
}

// reference:
// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Working_with_Objects
// Animal properties and method encapsulation
 var Animal = {
		type: "Invertebrates", // Default value of properties
		displayType : function() { // Method which will display type of Animal
			console.log(this.type);
		}
 }
//
// // Create new animal type called animal1
// var animal1 = Object.create(Animal);
// animal1.displayType(); // Output:Invertebrates
//
// // Create new animal type called Fishes
// var fish = Object.create(Animal);
// fish.type = "Fishes";
// fish.displayType(); // Output:Fishes











