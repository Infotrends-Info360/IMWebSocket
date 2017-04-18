package amqp.channel;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;

import amqp.amqpUtil;
import amqp.amqpUtil.QUEUE_NAME;
import util.Util;
import BackendService.thread.findAgent.FindAgentThread;

@Component
public class WebChatChannel extends WebSocketServer {
	
	// <WebSocket, UserID>
	private static BiMap<String, WebSocket> connANDuserIDBiMap = HashBiMap.create();
	
	public WebChatChannel() throws UnknownHostException {
		super();
	}

	public WebChatChannel(InetSocketAddress address) {
		super(address);
		Util.getConsoleLogger().info(
				"WebSocket IP address initialized: " + address);
		Util.getFileLogger().info(
				"WebSocket IP address initialized: " + address);
	}

	public WebChatChannel(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
		Util.getConsoleLogger().info("WebSocket Port initialized: " + port);
		Util.getFileLogger().info("WebSocket Port initialized: " + port);

		// 啟動FindAgentThread
		Thread findAgentThread = new FindAgentThread();
		findAgentThread.start();
		Util.getFileLogger().info("findAgentThread started");

	}

	/** * trigger close Event */
	@Override
	public void onClose(WebSocket conn, int message,
			String reason, boolean remote) {
		
	}

	/** * trigger Exception Event */
	@Override
	public void onError(WebSocket conn, Exception message) {
		Util.getConsoleLogger().warn(
				"On Error: Socket Exception:" + message.toString());
		Util.getFileLogger().warn(
				"On Error: Socket Exception:" + message.toString());
		message.printStackTrace();
	}

	/** * trigger link Event */
	@Override
	public void onOpen(WebSocket conn,
			ClientHandshake handshake) {
		Util.getConsoleLogger().info(conn + " is connected");
		Util.getFileLogger().info(conn + " is connected");
	}

	@Override
	public void onMessage(final WebSocket conn,
			final String message) {
		Util.getConsoleLogger().debug("onMessage() - message: " + message);
		Util.getConsoleLogger().debug("onMessage() - Thread.currentThread().getName(): " +Thread.currentThread().getName());
		// 1. 接收從JS來的reqeust
		JsonObject jsonIn = Util.getGJsonObject(message);
		String userID = Util.getGString(jsonIn, "UserID");
		Util.getConsoleLogger().debug("onMessage() - userID: " + userID);
		// 1.5 保留userid - conn map (其他channel ID給什麼之後再想)
		connANDuserIDBiMap.put(userID, conn);
		
		// 2. 送去RabbitMQ queue
		Util.getTemplate().convertAndSend(amqpUtil.QUEUE_NAME.CHANNEL_TO_BACKEND_QUEUE01, jsonIn.toString());
		

	}
	
	
    /** BACKEND -> CHANNEL **/
    @RabbitListener(queues = QUEUE_NAME.BACKEND_TO_WEBCHAT_QUEUE)
    public void process_BACKEND_TO_WEBCHAT_QUEUE(String aMsg) throws UnsupportedEncodingException {
    	// ��byte - string �ഫ���D
    	Util.getConsoleLogger().debug("process_BACKEND_TO_WEBCHAT_QUEUE() called - [x] Received -  aMsg: " + aMsg);
    	Util.getConsoleLogger().debug("Thread.currentThread().getName(): " +Thread.currentThread().getName());
    	
    	// 3. 拿從RabbitMQ queue回來的資料
		JsonObject jsonIn = Util.getGJsonObject(aMsg);
		if (jsonIn.get("test") != null && jsonIn.get("test").getAsBoolean()){
			return;
		}
		String UserID = Util.getGString(jsonIn, "UserID");	
		WebSocket conn = connANDuserIDBiMap.get(UserID);
		// 4. 送回給當初寄過來的Client(使用WebSocket.conn.send(...))
		Util.getConsoleLogger().debug("jsonIn.toString(): " + jsonIn.toString());
		conn.send(jsonIn.toString());
    } 

}