package websocket.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import util.Util;

import com.google.gson.JsonObject;

public class PressureTest extends Thread {
	public static ExecutorService service = Executors.newCachedThreadPool();
	
	public static void main(String[] args) throws URISyntaxException, InterruptedException{
		
		// 哪取參數值
		int round = 2000;
		int loginDurationMilleSecond = 5000;
		for (int i = 0; i < args.length; i++){
			if (i == 0) round = Integer.parseInt(args[i]);
			if (i == 1) loginDurationMilleSecond = Integer.parseInt(args[i]);
		}
		PressureTest.service = Executors.newCachedThreadPool();
		// 每輪開啟一個websocket socket連線
		for (int i = 0; i < round; i++){
			System.out.println("使用者編號: " + i);
			PressureTest.service.submit(new LoginLogoutWrapper(loginDurationMilleSecond));
		}// end of for
		

	}

}

class LoginLogoutWrapper implements Runnable{
//	private static final String wsServerURI = "ws:" + "//" + "localhost" + ":" + "8888";
	private static final String wsServerURI = "ws:" + "//" + "ws.crm.com.tw" + ":" + "8888";
	private int loginDurationMilleSecond = 0;
	
	public LoginLogoutWrapper(int aLoginDurationMilleSecond){
		this.loginDurationMilleSecond = aLoginDurationMilleSecond;
	}

	@Override
	public void run() {
		try {
			/** 建立連線 **/
			final ChatClientEndpoint  clientEndPoint = new ChatClientEndpoint(new URI(wsServerURI));
			
			/** 處理接收訊息事件 **/
			clientEndPoint.addMessageHandler(new ChatClientEndpoint.MessageHandler(){
				@Override
				public void handleMessage(String message) {
					JsonObject jsonObject = Util.getGJsonObject(message);
					String event = Util.getGString(jsonObject, "Event");
					if ("userjoin".equals(event)){
//						System.out.println("here01");
						/** 登入後,每秒寄出一次訊息 **/
						try {
							MsgTask msgTask = new MsgTask(clientEndPoint);
							PressureTest.service.submit(msgTask).get();
						} catch (InterruptedException | ExecutionException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						/** 模擬持續連線中 **/
//							PressureTest.service.submit(new suspendTask(LoginLogoutWrapper.this.loginDurationMilleSecond)).get();
						
						/** 寄送請求訊息 - 模擬登出 **/
						try {
							PressureTest.service.submit(new LogoutTask(clientEndPoint)).get();
						} catch (InterruptedException | ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} // end of "userjoin"
//					System.out.println("message: " + message);
//					Util.getPressureTestFileLogger().info("message: " + message);
//			        System.out.println("clientEndPoint.isClosed()" + clientEndPoint.isClosed());

				} // end of handleMessage
			}); // end of clientEndPoint.addMessageHandler			

			/** 寄送請求訊息 - 模擬登入 **/
//			login(clientEndPoint);
			try {
				PressureTest.service.submit(new LoginTask(clientEndPoint)).get();
			} catch (InterruptedException | ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
						
			/** 若ws連線中斷,則結束此SE JVM的運行 **/
	        while(!clientEndPoint.isClosed()){
//	            System.out.println("clientEndPoint.isClosed()" + clientEndPoint.isClosed()); // 必須加上這個才能正常關閉JVM  	
	        }// end of while			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}// end of try-catch
		
	}// end of run()
	
}// end of class taskWrapper

class MsgTask implements Runnable{
	private ChatClientEndpoint clientEndPoint;
	private static AtomicInteger msgCountOverview = new AtomicInteger(0);
	public MsgTask(ChatClientEndpoint aClientEndPoint){
		this.clientEndPoint = aClientEndPoint;
	}
	@Override
	public void run() {
		int msgCountMax = 20;
		AtomicInteger msgCount = new AtomicInteger(0);
		int tmpMsgCountOverview = msgCountOverview.incrementAndGet();
		while(msgCount.get() < msgCountMax){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("type", "message");
			jsonObject.addProperty("text", new java.util.Date().toString() + " - " + msgCount.incrementAndGet() + "(" + tmpMsgCountOverview +")");
			jsonObject.addProperty("sendto", "100"); // 寫死,寄給HollyLin
			jsonObject.addProperty("channel", "chat");
			this.clientEndPoint.sendMessage(jsonObject.toString()); // 此與網路請求有關,建議另開一個Thread來處理,否則由同一個Thread同時寄出上百次網路請求,很有可能會停住
		}
		
	}
	
}

class LogoutTask implements Runnable{
	ChatClientEndpoint clientEndPoint;
	public LogoutTask(ChatClientEndpoint aClientEndPoint){
		this.clientEndPoint = aClientEndPoint;
	}

	@Override
	public void run() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", "Exit");
		jsonObject.addProperty("id", "");
		jsonObject.addProperty("UserName", "A123456789");
		jsonObject.addProperty("channel", "chat");
		jsonObject.addProperty("waittingAgent", "");
		jsonObject.addProperty("waittingAgentID", "");
		this.clientEndPoint.sendMessage(jsonObject.toString());
		
	}
	
}



class LoginTask implements Runnable{
	ChatClientEndpoint clientEndPoint;
	public LoginTask(ChatClientEndpoint aClientEndPoint){
		this.clientEndPoint = aClientEndPoint;
	}

	@Override
	public void run() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", "login");
		jsonObject.addProperty("UserName", "A123456789");
		jsonObject.addProperty("ACtype", "Client");
		jsonObject.addProperty("channel", "chat");
        clientEndPoint.sendMessage(jsonObject.toString()); // 此與網路請求有關,建議另開一個Thread來處理,否則由同一個Thread同時寄出上百次網路請求,很有可能會停住
	}
	
}

class suspendTask implements Runnable{
	int suspendSec;
	public suspendTask(int aSuspendSec){
		this.suspendSec = aSuspendSec;
	}

	@Override
	public void run() {
		try {
		    Thread.sleep(this.suspendSec);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	}
	
}
