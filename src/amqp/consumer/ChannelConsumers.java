package amqp.consumer;

import java.io.UnsupportedEncodingException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.Exchange;

import amqp.amqpUtil.QUEUE_NAME;
import static amqp.amqpUtil.*;

@Component 
public class ChannelConsumers {
        
  
    
    @RabbitListener(queues = QUEUE_NAME.BACKEND_TO_LINE_QUEUE)
    public void process_BACKEND_TO_LINE_QUEUE(String data) throws UnsupportedEncodingException {
    	// ��byte - string �ഫ���D
    	System.out.println("process_BACKEND_TO_LINE_QUEUE() called - [x] Received -  data: " + data);
//    	String words = new String(data.getBytes(), "UTF-8");
//    	System.out.println("words: " + words);
    }

    @RabbitListener(queues = QUEUE_NAME.BACKEND_TO_WECHAT_QUEUE)
    public void process_BACKEND_TO_WECHAT_QUEUE(String data) throws UnsupportedEncodingException {
    	// ��byte - string �ഫ���D
    	System.out.println("process_BACKEND_TO_WECHAT_QUEUE() called - [x] Received -  data: " + data);
//    	String words = new String(data.getBytes(), "UTF-8");
//    	System.out.println("words: " + words);
    }

    @RabbitListener(queues = QUEUE_NAME.BACKEND_TO_VOICE_QUEUE)
    public void process_BACKEND_TO_VOICE_QUEUE(String data) throws UnsupportedEncodingException {
    	// ��byte - string �ഫ���D
    	System.out.println("process_BACKEND_TO_VOICE_QUEUE() called - [x] Received -  data: " + data);
//    	String words = new String(data.getBytes(), "UTF-8");
//    	System.out.println("words: " + words);
    } 

    
    
}
