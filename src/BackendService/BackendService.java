package BackendService;

import java.io.UnsupportedEncodingException;
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
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import BackendService.bean.RoomInfo;
import BackendService.bean.SystemInfo;
import BackendService.bean.UpdateStatusBean;
import BackendService.bean.UserInfo;
import BackendService.function.AgentFunction;
import BackendService.function.ClientFunction;
import BackendService.function.CommonFunction;
import BackendService.pools.WebSocketRoomPool;
import BackendService.pools.WebSocketUserPool;
import BackendService.thread.findAgent.FindAgentCallable;
import BackendService.thread.findAgent.FindAgentThread;
import amqp.amqpUtil.QUEUE_NAME;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import filter.SystemListener;
import filter.startFilter;
import util.StatusEnum;
import util.Util;

@Component
public class BackendService {
	
	
	/** CHANNEL -> BACKEND **/
    @RabbitListener(queues = QUEUE_NAME.CHANNEL_TO_BACKEND_QUEUE01)
    public void process_CHANNEL_TO_BACKEND_QUEUE01(String aMsg) throws UnsupportedEncodingException {
    	// ��byte - string �ഫ���D
    	Util.getConsoleLogger().debug("process_CHANNEL_TO_BACKEND_QUEUE01() - aMsg: " + aMsg);
		// 1. 接收從JS來的reqeust
		JsonObject jsonIn = Util.getGJsonObject(aMsg);
		String eventType = Util.getGString(jsonIn, "type");
		Util.getConsoleLogger().debug("process_CHANNEL_TO_BACKEND_QUEUE01() - eventType: " + eventType);
		
		if (jsonIn.get("test") != null && jsonIn.get("test").getAsBoolean()){
			return;
		}
		
		
		switch (eventType){
		case "login":
			CommonFunction.userjoin(jsonIn.toString());
			// we will get MSG + sendToID			
			break;
		}			
//		
//		switch (eventType){
//			case "login":
//				CommonFunction.userjoin(jsonIn.toString());
//				// we will get MSG + sendToID			
//				break;
//		}
    	
//    	String words = new String(data.getBytes(), "UTF-8");
//    	System.out.println("words: " + words);
    }
    
//	@Override
//	public void onMessage(final org.java_websocket.WebSocket conn, final String message) {
//		Util.getConsoleLogger().trace("WebSocket :\n " +message);
//		Util.getFileLogger().trace("WebSocket :\n " +message);
//		JSONObject obj = new JSONObject(message);
//		// if fromSrc = "WeChat";
//		// List<MsgWrapper> msgWrapperList // 拿取各方法回傳物件(包含 傳給誰+回傳資訊)
//		switch (obj.getString("type").trim()) {
//		case "message":
//			CommonFunction.getMessage(message.toString(), conn);
//			// we will get MSG + sendToID			
//			break;
//		case "login":
//			CommonFunction.userjoin(message.toString(), conn);
//			break;
//		case "online":
//			CommonFunction.online(message.toString(), conn);
//			break;
//		case "Exit":
//			CommonFunction.userExit(message.toString(), conn);
//			break;
////		case "addRoom":
////			CommonFunction.userjointoRoom(message.toString(), conn);
////			break;
//		case "leaveRoom":
//			CommonFunction.userExitfromRoom(message.toString(), conn);
//			break;
//		case "messagetoRoom":
//			CommonFunction.getMessageinRoom(message.toString(), conn);
//			break; 
//		case "roomonline":
//			CommonFunction.onlineinRoom(message.toString(), conn);
//			break;
//		case "typeonline":
//			CommonFunction.onlineinTYPE(message.toString(), conn);
//			break;
////		case "typein":
////			CommonFunction.userjointoTYPE(message.toString(), conn);
////			break;
////		case "typeout":
////			CommonFunction.userExitfromTYPE(message.toString(), conn);
////			break;
//		case "AcceptEvent":
//			AgentFunction.AcceptEvent(message.toString(), conn);
//			break;
//		case "RejectEvent":
//			AgentFunction.RejectEvent(message.toString(), conn);
//			break;
//		case "findAgent":
//			//確保client進線找Agent的順序性 ,將requesy物件化後放入queue中處理
//			FindAgentCallable task = new FindAgentCallable(message,conn);
//			BlockingQueue<FindAgentCallable> queue = WebSocketUserPool.getClientfindagentqueue();
//			Util.getFileLogger().info("findAgent - add task to queue - " + "before queue size: " + queue.size());
//			// 將請求放入queue
//			if(queue.offer(task)){
//				Util.getFileLogger().info("findAgent - add task to queue - succeed - " + " queue after size: " + queue.size());
//			}else{;
//				Util.getFileLogger().info("findAgent - add task to queue - failed - " + " queue after size: " + queue.size());			
//			}
//			// 將此請求放入Client UserInfo中,onClose()清理時使用
//			UserInfo userInfo = WebSocketUserPool.getUserInfoByKey(conn);
//			userInfo.setFindAgentCallable(task);
//			
//			break;
//		case "findAgentEvent":
//			ClientFunction.findAgentEvent(message.toString(), conn);
//			break;
//		case "updateStatus":
//			JsonObject tmpObj = Util.getGJsonObject(message);
//			String status_dbid = Util.getGString(tmpObj, "status");
//			if (StatusEnum.READY.getDbid().equals(status_dbid))
//				Util.getStatusFileLogger().info("###### [User request] ######");
//			if (StatusEnum.NOTREADY.getDbid().equals(status_dbid))
//				Util.getStatusFileLogger().info("###### [User request] ######");
//			
//			CommonFunction.updateStatus(message.toString(), conn);
//			break;
////		case "getUserStatus":
////			AgentFunction.getUserStatus(message.toString(), conn);
////			break;
//		case "createroomId":
//			AgentFunction.createRoomId(message.toString(), conn);
//			break;
//		case "senduserdata":
//			ClientFunction.senduserdata(message.toString(), conn);			
//			break;
//		case "entrylog":
//			ClientFunction.entrylog(message.toString(), conn);
//			break;
//		case "interactionlog":
//			ClientFunction.interactionlog(message.toString(), conn);
//			break;
//		case "setinteraction":
//			ClientFunction.setinteraction(message.toString(), conn);
//			break;
//		case "inviteAgentThirdParty":
//			AgentFunction.inviteAgentThirdParty(message.toString(), conn);
//			break; 
//		case "responseThirdParty":
//			AgentFunction.responseThirdParty(message.toString(), conn);			
//			break;			
//		case "refreshRoomList":
//			CommonFunction.refreshRoomList(conn);
//			break;
//		case "addRoomForMany":
//			AgentFunction.addRoomForMany(message.toString(), conn);
//			break;
//		case "sendComment":
//			AgentFunction.sendComment(message.toString(), conn);
//			break;
//		case "updateClientContactID":
//			AgentFunction.updateClientContactID(message.toString(), conn);
//			break;
//		case "refreshAgentList":
//			AgentFunction.refreshAgentList();
//			break;
//		case "test":
//			this.test();
//			break;
//		}
//		
//		// if fromSrc.equals("WeChat") -> use WeChatAPI to send msg
//		// if fromSrc.equals("js") -> use the same old way 
//
//		
//	}

	
}


