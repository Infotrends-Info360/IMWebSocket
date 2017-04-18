package BackendService.thread.findAgent;

import java.util.concurrent.Callable;

import BackendService.function.ClientFunction;
import BackendService.pools.WebSocketUserPool;
import util.Util;

public class FindAgentCallable implements Callable<Object>{
	org.java_websocket.WebSocket clientConn;
	String msg;
	
	public FindAgentCallable(String aMsg, org.java_websocket.WebSocket aConn){
		this.msg = aMsg;
		this.clientConn = aConn;
	}
	
	@Override
	public Object call() throws Exception {
		// TODO Auto-generated method stub
		String userID = WebSocketUserPool.getUserID(this.clientConn);
		Util.getConsoleLogger().debug(FindAgentThread.TAG + userID + " is looking for an Agent ... ");
		Util.getFileLogger().debug(FindAgentThread.TAG + userID + " is looking for an Agent ... ");
		ClientFunction.findAgent(this.msg, this.clientConn);
		return null;
	}
	
	public org.java_websocket.WebSocket getClientConn(){
		return this.clientConn;
	}

	public String getMsg() {
		return msg;
	}
	
};