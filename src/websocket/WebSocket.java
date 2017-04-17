package websocket;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;






import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import filter.SystemListener;
import filter.startFilter;
import util.StatusEnum;
import util.Util;
import websocket.bean.RoomInfo;
import websocket.bean.SystemInfo;
import websocket.bean.UpdateStatusBean;
import websocket.bean.UserInfo;
import websocket.function.AgentFunction;
import websocket.function.ClientFunction;
import websocket.function.CommonFunction;
import websocket.pools.WebSocketRoomPool;
import websocket.pools.WebSocketUserPool;
import websocket.thread.findAgent.FindAgentCallable;
import websocket.thread.findAgent.FindAgentThread;

public class WebSocket extends WebSocketServer {
		
	public WebSocket(InetSocketAddress address) {
		super(address);
		Util.getConsoleLogger().info("WebSocket IP address initialized: " + address);
		Util.getFileLogger().info("WebSocket IP address initialized: " + address);
	}

	public WebSocket(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
		Util.getConsoleLogger().info("WebSocket Port initialized: " + port);
		Util.getFileLogger().info("WebSocket Port initialized: " + port);
		
		//啟動FindAgentThread
		Thread findAgentThread = new FindAgentThread();
		findAgentThread.start();
		Util.getFileLogger().info("findAgentThread started");
		
	}

	/** * trigger close Event */
	@Override
	public void onClose(org.java_websocket.WebSocket conn, int message,
			String reason, boolean remote) {
		Util.getFileLogger().info("onClose() called - userName: " + WebSocketUserPool.getUserNameByKey(conn));
		Util.getConsoleLogger().info("onClose() called - userName: " + WebSocketUserPool.getUserNameByKey(conn));
		CommonFunction.onCloseHelper(conn, reason);
	}

	/** * trigger Exception Event */
	@Override
	public void onError(org.java_websocket.WebSocket conn, Exception message) {
		Util.getConsoleLogger().warn("On Error: Socket Exception:" + message.toString());
		Util.getFileLogger().warn("On Error: Socket Exception:" + message.toString());
		
		message.printStackTrace();
		e++;
	}

	/** * trigger link Event */
	@Override
	public void onOpen(org.java_websocket.WebSocket conn,
			ClientHandshake handshake) {
		Util.getConsoleLogger().info( conn + " is connected");
		Util.getFileLogger().info( conn + " is connected");
		l++;
	}

	/** * The event is triggered when the client sends a message to the server */
	int j = 0;
	int h = 0;
	int e = 0;
	int l = 0;

	/**
	 * WebSocket GetMessage
	 * 
	 * message login online Exit addRoom leaveRoom messagetoRoom roomonline
	 * typeonline typein typeout AcceptEvent
	 * Event updateStatus getUserStatus findAgent createroomId senduserdata
	 */
	@Override
	public void onMessage(final org.java_websocket.WebSocket conn, final String message) {
		Util.getConsoleLogger().trace("WebSocket :\n " +message);
		Util.getFileLogger().trace("WebSocket :\n " +message);
		JSONObject obj = new JSONObject(message);
		// if fromSrc = "WeChat";
		// List<MsgWrapper> msgWrapperList // 拿取各方法回傳物件(包含 傳給誰+回傳資訊)
		switch (obj.getString("type").trim()) {
		case "message":
			CommonFunction.getMessage(message.toString(), conn);
			// we will get MSG + sendToID			
			break;
		case "login":
			CommonFunction.userjoin(message.toString(), conn);
			break;
		case "online":
			CommonFunction.online(message.toString(), conn);
			break;
		case "Exit":
			CommonFunction.userExit(message.toString(), conn);
			break;
//		case "addRoom":
//			CommonFunction.userjointoRoom(message.toString(), conn);
//			break;
		case "leaveRoom":
			CommonFunction.userExitfromRoom(message.toString(), conn);
			break;
		case "messagetoRoom":
			CommonFunction.getMessageinRoom(message.toString(), conn);
			break; 
		case "roomonline":
			CommonFunction.onlineinRoom(message.toString(), conn);
			break;
		case "typeonline":
			CommonFunction.onlineinTYPE(message.toString(), conn);
			break;
//		case "typein":
//			CommonFunction.userjointoTYPE(message.toString(), conn);
//			break;
//		case "typeout":
//			CommonFunction.userExitfromTYPE(message.toString(), conn);
//			break;
		case "AcceptEvent":
			AgentFunction.AcceptEvent(message.toString(), conn);
			break;
		case "RejectEvent":
			AgentFunction.RejectEvent(message.toString(), conn);
			break;
		case "findAgent":
			//確保client進線找Agent的順序性 ,將requesy物件化後放入queue中處理
			FindAgentCallable task = new FindAgentCallable(message,conn);
			BlockingQueue<FindAgentCallable> queue = WebSocketUserPool.getClientfindagentqueue();
			Util.getFileLogger().info("findAgent - add task to queue - " + "before queue size: " + queue.size());
			// 將請求放入queue
			if(queue.offer(task)){
				Util.getFileLogger().info("findAgent - add task to queue - succeed - " + " queue after size: " + queue.size());
			}else{;
				Util.getFileLogger().info("findAgent - add task to queue - failed - " + " queue after size: " + queue.size());			
			}
			// 將此請求放入Client UserInfo中,onClose()清理時使用
			UserInfo userInfo = WebSocketUserPool.getUserInfoByKey(conn);
			userInfo.setFindAgentCallable(task);
			
			break;
		case "findAgentEvent":
			ClientFunction.findAgentEvent(message.toString(), conn);
			break;
		case "updateStatus":
			JsonObject tmpObj = Util.getGJsonObject(message);
			String status_dbid = Util.getGString(tmpObj, "status");
			if (StatusEnum.READY.getDbid().equals(status_dbid))
				Util.getStatusFileLogger().info("###### [User request] ######");
			if (StatusEnum.NOTREADY.getDbid().equals(status_dbid))
				Util.getStatusFileLogger().info("###### [User request] ######");
			
			CommonFunction.updateStatus(message.toString(), conn);
			break;
//		case "getUserStatus":
//			AgentFunction.getUserStatus(message.toString(), conn);
//			break;
		case "createroomId":
			AgentFunction.createRoomId(message.toString(), conn);
			break;
		case "senduserdata":
			ClientFunction.senduserdata(message.toString(), conn);			
			break;
		case "entrylog":
			ClientFunction.entrylog(message.toString(), conn);
			break;
		case "interactionlog":
			ClientFunction.interactionlog(message.toString(), conn);
			break;
		case "setinteraction":
			ClientFunction.setinteraction(message.toString(), conn);
			break;
		case "inviteAgentThirdParty":
			AgentFunction.inviteAgentThirdParty(message.toString(), conn);
			break; 
		case "responseThirdParty":
			AgentFunction.responseThirdParty(message.toString(), conn);			
			break;			
		case "refreshRoomList":
			CommonFunction.refreshRoomList(conn);
			break;
		case "addRoomForMany":
			AgentFunction.addRoomForMany(message.toString(), conn);
			break;
		case "sendComment":
			AgentFunction.sendComment(message.toString(), conn);
			break;
		case "updateClientContactID":
			AgentFunction.updateClientContactID(message.toString(), conn);
			break;
		case "refreshAgentList":
			AgentFunction.refreshAgentList();
			break;
		case "test":
			this.test();
			break;
		}
		
		// if fromSrc.equals("WeChat") -> use WeChatAPI to send msg
		// if fromSrc.equals("js") -> use the same old way 

		
	}


	/** * user leave websocket (Demo) */
	// 此方法沒有用到,先放著,並不會影響到主流程
//	public void userLeave(org.java_websocket.WebSocket conn) {
//		String user = WebSocketUserPool.getUserByKey(conn);
//		boolean b = WebSocketUserPool.removeUser(conn);
//		if (b) {
//			WebSocketUserPool.sendMessage(user.toString());
//			String joinMsg = "[Server]" + user + "Offline";
//			WebSocketUserPool.sendMessage(joinMsg);
//		}
//	}
	
	private void test() {
		Util.getConsoleLogger().debug("test() called");
	}
	
}


