/********** util *************/

// IE8 does not support trim() - this is a polyfill for IE8 
if(typeof String.prototype.trim !== 'function') {
  String.prototype.trim = function() {
    return this.replace(/^\s+|\s+$/g, ''); 
  };
}


/********** bean *************/
//目的: 將Json請求字串物件化 (持續進行中)

function loginJson(aUserID, aUserName, aACtype, aMaxCount){
	this.type = "login";
	this.UserID = aUserID;
	this.UserName = aUserName;
	this.maxCount = aMaxCount;
	this.ACtype = aACtype;
	this.channel = "chat";
}

function exitJson(aUserID, aUserName, aWaittingAgent_g, aWaittingAgentID_g){
	this.type = "Exit";
	this.id = aUserID;
	this.UserName = aUserName;
	this.channel = "chat";
	this.waittingAgent = aWaittingAgent_g;
	this.waittingAgentID = aWaittingAgentID_g;
} 

function findAgentJson(aUserID, aUserName){
		this.type = "findAgent";
		this.id = aUserID;
		this.UserName = aUserName;
		this.channel = "chat";
}

function entrylogJson(aContactID, aUserID, aUserName){
	this.type = "entrylog";
	this.contactid = aContactID;
	this.userid = aUserID;
	this.username = aUserName;
	this.ipaddress = '127.0.0.1';
	this.browser = 'IE';
	this.platfrom = 'Windows';
	this.channel = 'Web'; // 使用管道
	this.language = 'chiname';
	this.enterkey = 'Phone';  
}

function RoomInfo(aRoomID, aUserdata, aText){
	this.roomID = aRoomID;
	this.userdata = aUserdata;
	this.text = aText;
	this.close = false;
	this.isAfterCallWorkDone = false;
}

function messagetoRoomJson(aType, aACtype, aText, aId, aUserName, aRoomID, aChannel, aDate) {  // Method which will display type of Animal
	this.type = aType;
	this.ACtype = aACtype;
	this.text = aText;
	this.id = aId;
	this.UserName = aUserName;
	this.roomID = aRoomID;
	this.channel = "chat";
	this.channel = aChannel;
	this.date = aDate;
}		

function sendCommentJson(aInteractionid, aActivitydataids, aComment){
	this.type = "sendComment";
	this.interactionid = aInteractionid;
	this.activitydataids = aActivitydataids;
	this.comment = aComment;
}

function updateStatusJson(aACType, aID, aUserName, aStatus, aReason, aStartORend, aDbid, aRoomID, aClientID, aReason_dbid){
	this.type = "updateStatus";
	this.ACtype = aACType;
	this.id = aID;
	this.UserName = aUserName;
	this.status = aStatus;
	this.reason = aReason;
	this.startORend = aStartORend,
	this.dbid = aDbid,
	this.channel = "chat";
	this.roomID = aRoomID;
	this.clientID = aClientID;
	this.reason_dbid = aReason_dbid;
	var now = new Date();
	this.date = "" + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds();
}

// addRoomForMany
function myRoomMemberJsonObj(aID){
	this.ID = aID;
}


// reference:
// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Working_with_Objects
// Animal properties and method encapsulation
// var Animal = {
//		type: "Invertebrates", // Default value of properties
//		displayType : function() { // Method which will display type of Animal
//			console.log(this.type);
//		}
// }
//
// // Create new animal type called animal1
// var animal1 = Object.create(Animal);
// animal1.displayType(); // Output:Invertebrates
//
// // Create new animal type called Fishes
// var fish = Object.create(Animal);
// fish.type = "Fishes";
// fish.displayType(); // Output:Fishes



/******** debugging *********/
function seeAllKV(obj){
	Object.keys(obj).forEach(function (key) {
	    var val = obj[key];
	    console.log(key + " : " + val);
	});
}



/******** SinglyList *********/
//給Group ID List用 - 之後試著把這段獨立出去
//website:
//https://code.tutsplus.com/articles/data-structures-with-javascript-singly-linked-list-and-doubly-linked-list--cms-23392
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







