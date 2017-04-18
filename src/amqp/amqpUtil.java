package amqp;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

import util.Util;

public class amqpUtil {
//	public final static String QUEUE_NAME_WORKERS = "durable_queue_fairDispatcher";
	public final static String RABBIT_MQSERVER = "localhost";
	public final static String ACCOUNT = "spring"; 
	public final static String PASSWORD = "123456";
	public static CachingConnectionFactory cachingConnectionFactory;
	
	public static class QUEUE_NAME{
		// Consumers - backUend (distributed system design)
		public static final String CHANNEL_TO_BACKEND_QUEUE01 = "CHANNEL_TO_BACKEND_QUEUE01";
//		public static final String CHANNEL_TO_BACKEND_QUEUE02 = "CHANNEL_TO_BACKEND_QUEUE02";
		
		// Consumers - channel
		public static final String BACKEND_TO_WEBCHAT_QUEUE = "BACKEND_TO_WEBCHAT_QUEUE";
		public static final String BACKEND_TO_LINE_QUEUE = "BACKEND_TO_LINE_QUEUE";
		public static final String BACKEND_TO_WECHAT_QUEUE = "BACKEND_TO_WECHAT_QUEUE";
		public static final String BACKEND_TO_VOICE_QUEUE = "BACKEND_TO_VOICE_QUEUE";
		
		// test js 
		public static final String BACKEND_TO_JS_QUEUE = "BACKEND_TO_JS_QUEUE";
	
	}
	
	public static void sendReqToQueue(String aChannelName, String aMsg){
		switch(aChannelName){
		case "chat":
			Util.getTemplate().convertSendAndReceive(QUEUE_NAME.BACKEND_TO_WEBCHAT_QUEUE, aMsg);
		}
	}
	
	
//	private static CachingConnectionFactory getCachingConnectionFactory(){
//		if (cachingConnectionFactory == null){
//			Util.cachingConnectionFactory = new CachingConnectionFactory();
//			Util.cachingConnectionFactory.setUsername("spring");
//			Util.cachingConnectionFactory.setPassword("123456");
//		}
//		return Util.cachingConnectionFactory;
//	}
	
//	public static AmqpTemplate getAmqpTemplate(){
//		return new RabbitTemplate(Util.getCachingConnectionFactory());
//	}
	
	
//	public static enum EXCHANGE_TYPE {
//		FANOUT, DIRECT;
//		
//		public String getV(){
//			return this.name().toLowerCase();
//		}
//	};
}
