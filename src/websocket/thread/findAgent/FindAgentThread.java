package websocket.thread.findAgent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import util.Util;
import websocket.bean.UserInfo;
import websocket.function.ClientFunction;
import websocket.pools.WebSocketUserPool;

// ReadyAgentQueue background service
// 此背景執行緒目的:
// ->讓Client的findAgent請求有順序性
// 這邊會有兩層的Thread:
// 第一層為 等待著要找Agent的Client隊伍:
// -> 此隊伍會一個接一個去找Agent
// -> 第二層為 找Agent會在開啟另一個Thread
// -> 我們會確保每次第二層透過Future.get()和第一層"同步"
// ->-> 最後都能確保第一層結束前,第二層一定也已經結束
// 此Thread會搭配readyAgentQueue進行
public class FindAgentThread extends Thread{
	public static final String TAG = "findAgent - ";
	@Override
	public void run() {
		BlockingQueue<FindAgentCallable> queue = WebSocketUserPool.getClientfindagentqueue();
		UserInfo userInfo = null;
		while(true){
			try {
				// 從queue中拿取queue
				Util.getFileLogger().info(FindAgentThread.TAG + "retrieve task from queue - " + "before queue size: " + queue.size());				
				FindAgentCallable currTask = queue.take(); // it might block here
				Util.getFileLogger().info(FindAgentThread.TAG + "retrieve task from queue - " + "after queue size: " + queue.size());				
				
				// 執行task
				ExecutorService service = Executors.newCachedThreadPool();
				Future<Object> taskResult = service.submit(currTask); // execute it. However, if no one is currently in ReadyAgentQueue, it blocks.
				userInfo = WebSocketUserPool.getUserInfoByKey(currTask.getClientConn());
				userInfo.setFindAgentTaskResult(taskResult);
				
				// 確保此請求已經找到Agent了,才處理下一個請求
				taskResult.get(); // it might block here too or it might be cancelled when client logs out
			} catch (Exception e) {
				Util.getFileLogger().debug(FindAgentThread.TAG + "Client " + userInfo.getUsername() + " cancelled findAgentTask");
				e.printStackTrace();
			}			
			// while單次程序結束條件:  有Client在queue中 && 此為Client已經找到Agent
		}// end of while 
	}// end of run()
}
