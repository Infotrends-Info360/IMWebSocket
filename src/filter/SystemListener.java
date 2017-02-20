package filter;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
 

import org.apache.log4j.PropertyConfigurator;

import util.Util;
 
@WebListener("application context listener")
public class SystemListener implements ServletContextListener {
 
    /**
     * Initialize log4j when the application is being started
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
//         initialize log4j here
    	System.out.println("System Config called");
        ServletContext context = event.getServletContext();
        String maxRingTime = context.getInitParameter("MaxRingTime");
        String afterCallStatus = context.getInitParameter("AfterCallStatus");

//        System.out.println("MaxRingTime: "+maxRingTime);
//        System.out.println("AfterCallStatus: "+afterCallStatus);
        
        //Set in WebSocket
        Util.setMaxRingTime(maxRingTime);
        Util.setAfterCallStatus(afterCallStatus);
        
//        System.out.println("MaxRingTime: "+Util.getMaxRingTime());
//        System.out.println("AfterCallStatus: "+Util.getAfterCallStatus());
    }
     
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // do nothing
    }  
}
