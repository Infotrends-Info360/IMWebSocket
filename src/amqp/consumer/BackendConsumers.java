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
public class BackendConsumers {

//	/** CHANNEL -> BACKEND **/
//    @RabbitListener(queues = QUEUE_NAME.CHANNEL_TO_BACKEND_QUEUE01)
//    public void process_CHANNEL_TO_BACKEND_QUEUE01(String data) throws UnsupportedEncodingException {
//    	// ��byte - string �ഫ���D
//    	System.out.println("process_CHANNEL_TO_BACKEND_QUEUE01() called - [x] Received -  data: " + data);
////    	String words = new String(data.getBytes(), "UTF-8");
////    	System.out.println("words: " + words);
//    }
        


    
    
}
