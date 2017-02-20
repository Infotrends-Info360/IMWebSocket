package filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
 





import org.apache.log4j.PropertyConfigurator;

import com.Info360.bean.Cfg_AgentStatus;
import com.Info360.service.MaintainService;

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
        Map<String, String> systemParam = new HashMap<String, String>();
        systemParam.put("MaxRingTime", maxRingTime);
        systemParam.put("AfterCallStatus", afterCallStatus);
        Util.setSystemParam(systemParam);
        Cfg_AgentStatus agentstatus = new Cfg_AgentStatus();
        MaintainService maintainservice = new MaintainService();		
		List<Cfg_AgentStatus> agentstatuslist = maintainservice.Select_cfg_agentstatus(agentstatus);
		Map<String, Map<String, String>> agentstatusmap = new HashMap<String, Map<String, String>>();
		 for(int a = 0; a < agentstatuslist.size(); a++){
			 if( agentstatuslist.get(a).getMediatypeid().equals("2")){
				 Map<String, String> agentstatusmapinfo = new HashMap<String, String>();
				 agentstatusmapinfo.put("statusname", agentstatuslist.get(a).getStatusname());
				 agentstatusmapinfo.put("description", agentstatuslist.get(a).getDescription());
				 agentstatusmap.put(String.valueOf(agentstatuslist.get(a).getDbid()), agentstatusmapinfo);
			 }
		 }
		 Util.setAgentStatus(agentstatusmap);
		 
//		 System.out.println("agentstatusmap: "+agentstatusmap);
		 
//		 System.out.println("agentstatusmap: "+Util.getAgentStatus());
//        System.out.println("MaxRingTime: "+Util.getMaxRingTime());
//        System.out.println("AfterCallStatus: "+Util.getAfterCallStatus());
    }
     
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // do nothing
    }  
}
