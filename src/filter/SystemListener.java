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
 






import util.StatusEnum;











import org.apache.log4j.PropertyConfigurator;

import com.Info360.bean.Cfg_AgentStatus;
import com.Info360.service.MaintainService;

import util.StatusEnum;
import util.Util;
import websocket.bean.RingCountDownTask;
import websocket.bean.UserInfo;
import websocket.function.AgentFunction;
 
@WebListener("application context listener")
public class SystemListener implements ServletContextListener {
 
    /**
     * Initialize log4j when the application is being started
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
//         initialize log4j here
    	System.out.println("System Config - contextInitialized() called");
        ServletContext context = event.getServletContext();
        String maxRingTime = context.getInitParameter("MaxRingTime");
        String afterCallStatus = context.getInitParameter("AfterCallStatus");
        String establishedStatus = context.getInitParameter("EstablishedStatus");

//        System.out.println("MaxRingTime: "+maxRingTime);
//        System.out.println("AfterCallStatus: "+afterCallStatus);
//        System.out.println("EstablishedStatus: "+establishedStatus);
        
        // 更新系統參數
        Map<String, String> systemParam = new HashMap<String, String>();
        systemParam.put("MaxRingTime", maxRingTime);
        systemParam.put("AfterCallStatus", afterCallStatus);
        systemParam.put("EstablishedStatus", establishedStatus);
        Util.setSystemParam(systemParam);
        
        // 更新statusList
        Cfg_AgentStatus agentstatus = new Cfg_AgentStatus(); 
        MaintainService maintainservice = new MaintainService();		
		List<Cfg_AgentStatus> agentstatuslist = maintainservice.Select_cfg_agentstatus(agentstatus);
		Map<String, Map<String, String>> agentstatusmap = new HashMap<String, Map<String, String>>();
		 for(int a = 0; a < agentstatuslist.size(); a++){
			 if( agentstatuslist.get(a).getMediatypeid().equals("2")){
				 String dbid = String.valueOf(agentstatuslist.get(a).getDbid());
				 String description = agentstatuslist.get(a).getDescription();
				 String statusname = agentstatuslist.get(a).getStatusname();
				 // 給Util.setAgentStatus使用
				 Map<String, String> agentstatusmapinfo = new HashMap<String, String>();
				 agentstatusmapinfo.put("dbid", dbid);
				 agentstatusmapinfo.put("description", description);
				 agentstatusmap.put(statusname, agentstatusmapinfo);
				 
				 // 更新enum:
//				 System.out.println("statusname: " + statusname);
//				 System.out.println("StatusEnum.READY.toString(): " + StatusEnum.READY.toString());
				 updateStatusEnum(statusname, dbid, description);
			 }
		 }
		 Util.setAgentStatus(agentstatusmap);
		 System.out.println("agentstatusmap: "+agentstatusmap);
		 
		  
		 //測試區
//		 System.out.println("TimerTaskRingHeartBeat test: ");
//		 UserInfo agentUserInfo = new UserInfo();
//		 RingCountDownTask timertask = new RingCountDownTask(null, "", agentUserInfo);
//		 timertask.operate();
		 
//		 try {
//			Thread.sleep(5000);
//			agentUserInfo.setStopRing(true);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		 
    }
     
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // do nothing
    }  
    
    
    private void updateStatusEnum(String aStatusname, String aDbid, String aDescription){
    	StatusEnum currStatusEnum = StatusEnum.getStatusEnumByDescription(aStatusname);
    	currStatusEnum.setDbid(aDbid);
    	currStatusEnum.setDescription(aDescription);
    	System.out.println("updateStatusEnum() - " + currStatusEnum);
    }
    
    
}
