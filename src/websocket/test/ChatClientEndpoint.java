package websocket.test;

import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.google.gson.JsonObject;

import util.Util;


@ClientEndpoint
public class ChatClientEndpoint {
	private Session userSession = null;
	private MessageHandler messageHandler;
	private AtomicBoolean isClosed = new AtomicBoolean(true);
	private static AtomicInteger openCount = new AtomicInteger(0);
	private static AtomicInteger closeCount = new AtomicInteger(0);
//	private boolean isClosed = true;

	public ChatClientEndpoint(final URI endpointURI) {
		try {
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			container.connectToServer(this, endpointURI);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@OnOpen
	public void onOpen(final Session userSession) {
		System.out.println("connect to WS server successfully! - " + openCount.incrementAndGet());
		this.userSession = userSession;
		this.isClosed.set(false);
		
	}

	@OnClose
	public void onClose(final Session userSession, final CloseReason reason) {
		this.userSession = null;
		this.isClosed.set(true);
//		System.out.println("this.isClosed: " + this.isClosed);
		System.out.println(userSession.getId() + " is closed - " + closeCount.incrementAndGet());
	}

	@OnMessage
	public void onMessage(final String message) {
		if (messageHandler != null) {
			messageHandler.handleMessage(message);
		}
	}

	// MessageHandler is a interaface, we need to inject an instance to let this clientEndpoint to work
	public void addMessageHandler(final MessageHandler msgHandler) {
		messageHandler = msgHandler;
	}

	public void sendMessage(final String message) {
		userSession.getAsyncRemote().sendText(message);
	}
	
	public static interface MessageHandler {
		public void handleMessage(String message);
	}

	public boolean isClosed() {
		return this.isClosed.get();
	}
	
	
	
	
}
