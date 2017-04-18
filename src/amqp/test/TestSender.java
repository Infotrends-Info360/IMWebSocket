package amqp.test;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import amqp.amqpUtil;

@Component
public class TestSender implements CommandLineRunner{
	private final AmqpTemplate template;
	
	@Autowired
	public TestSender(AmqpTemplate template){
		System.out.println("TestSender() called");
		this.template = template;
	}

	public void run(String... arg0) throws Exception {
		System.out.println("TestSender - CommandLineRunner() called - 開始測試");
		if(template == null){
			System.out.println("template is null");
		}
		
		String data = "foo 中文 123";
		JsonObject jsonOut = new JsonObject();
		jsonOut.addProperty("data", data);
		jsonOut.addProperty("test", true);
		System.out.println("jsonOut.toString()" + jsonOut.toString());
		template.convertAndSend(amqpUtil.QUEUE_NAME.CHANNEL_TO_BACKEND_QUEUE01, jsonOut.toString());
//    	System.out.println("CHANNEL_TO_BACKEND_QUEUE01 - [o] Sent -  data: " + data);
		template.convertAndSend(amqpUtil.QUEUE_NAME.BACKEND_TO_WEBCHAT_QUEUE, jsonOut.toString());
//		System.out.println("BACKEND_TO_WEBCHAT_QUEUE - [o] Sent -  data: " + data);
		template.convertAndSend(amqpUtil.QUEUE_NAME.BACKEND_TO_LINE_QUEUE, jsonOut.toString());
//		System.out.println("BACKEND_TO_LINE_QUEUE - [o] Sent -  data: " + data);
		template.convertAndSend(amqpUtil.QUEUE_NAME.BACKEND_TO_WECHAT_QUEUE, jsonOut.toString());
//		System.out.println("BACKEND_TO_WECHAT_QUEUE - [o] Sent -  data: " + data);
		template.convertAndSend(amqpUtil.QUEUE_NAME.BACKEND_TO_VOICE_QUEUE, jsonOut.toString());
//		System.out.println("BACKEND_TO_VOICE_QUEUE - [o] Sent -  data: " + data);
		template.convertAndSend(amqpUtil.QUEUE_NAME.BACKEND_TO_JS_QUEUE, jsonOut.toString());
		
	}

}