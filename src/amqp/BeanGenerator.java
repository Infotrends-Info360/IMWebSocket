package amqp;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanGenerator {
	
	// 設定server IP, username, password
	@Bean
	public CachingConnectionFactory connectionFactory() {
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(amqpUtil.RABBIT_MQSERVER);
		cachingConnectionFactory.setUsername(amqpUtil.ACCOUNT);
		cachingConnectionFactory.setPassword(amqpUtil.PASSWORD);
		return cachingConnectionFactory;
	}	    
    
}
