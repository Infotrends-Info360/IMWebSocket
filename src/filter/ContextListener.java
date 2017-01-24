package filter;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
 
import org.apache.log4j.PropertyConfigurator;
 
@WebListener("application context listener")
public class ContextListener implements ServletContextListener {
 
    /**
     * Initialize log4j when the application is being started
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
//         initialize log4j here
    	System.out.println("contextInitialized() called");
        ServletContext context = event.getServletContext();
        String log4jConfigFile = context.getInitParameter("log4j-config-location");
        String fullPath = context.getRealPath("") + File.separator + log4jConfigFile;
//         example path: C:\Users\sam\git\IMWebSocket\WebContent\WEB-INF/log4j.properties
//        System.out.println("Log4j path: "+ fullPath);
         
        PropertyConfigurator.configure(fullPath);
         
    }
     
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // do nothing
    }  
}