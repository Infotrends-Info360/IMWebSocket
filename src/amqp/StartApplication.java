
package amqp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"amqp","util"})
public class StartApplication extends SpringBootServletInitializer{
	
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    	System.out.println("StartApplication configure() called ***************************** ");
        return application.sources(StartApplication.class);
    }	
	
	public static void main(String[] args){
		SpringApplication.run(StartApplication.class, args);
		System.out.println("StartApplication main() called ***************************** ");
	}   
	
}
