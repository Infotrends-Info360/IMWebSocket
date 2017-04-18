package filter;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
 


import util.StatusEnum;








import org.apache.commons.lang.StringEscapeUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;

import BackendService.bean.SystemInfo;
import BackendService.bean.UserInfo;
import amqp.channel.WebChatChannel;

import com.Info360.bean.Cfg_AgentStatus;
import com.Info360.bean.SystemCfg;
import com.Info360.dao.SystemCfgDao;
import com.Info360.service.MaintainService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import util.Util;
 
@WebListener("application context listener")
public class SystemListener implements ServletContextListener {
 
    /**
     * Initialize log4j when the application is being started
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
//         initialize log4j here
    	Util.getConsoleLogger().debug("System Config - contextInitialized() called");
    	
    	
    	
        ServletContext context = event.getServletContext();
        String maxRingTime = context.getInitParameter("MaxRingTime");
        String afterCallStatus = context.getInitParameter("AfterCallStatus");
        String establishedStatus = context.getInitParameter("EstablishedStatus");

//        Util.getConsoleLogger().debug("MaxRingTime: "+maxRingTime);
//        Util.getConsoleLogger().debug("AfterCallStatus: "+afterCallStatus);
//        Util.getConsoleLogger().debug("EstablishedStatus: "+establishedStatus);
        
        // 更新系統參數
        Map<String, String> systemParam = new HashMap<String, String>();
//        systemParam.put("MaxRingTime", maxRingTime);
//        systemParam.put("AfterCallStatus", afterCallStatus);
//        systemParam.put("EstablishedStatus", establishedStatus);
        	// 讀取IP, port的properties
		Properties prop = new Properties();

		String propFileName = "config.properties";

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

		if (inputStream != null) {
			try {
				prop.load(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
//			Util.getConsoleLogger().warn("property file '" + propFileName + "' not found in the classpath");
//			Util.getFileLogger().warn("property file '" + propFileName + "' not found in the classpath");
		}
		
		for (final String name: prop.stringPropertyNames()){
//			Util.getConsoleLogger().debug("name: " + name);
//			Util.getConsoleLogger().debug("prop.getProperty(name): " + prop.getProperty(name));
			systemParam.put(name.replace(".", "_"), prop.getProperty(name));
		}		
		
		 // 從DB更新系統參數
		 SystemCfgDao dao = new SystemCfgDao();
		 List<SystemCfg> SystemCfgs = dao.selectAll_SystemCfg();
//		 Util.getConsoleLogger().debug("SystemCfgs.size(): " + SystemCfgs.size());
//		 Map<String, String> sysMsgsMap = SystemInfo.getSysMsgsMap();
		 for (SystemCfg systemCfg : SystemCfgs){
			 systemParam.put(systemCfg.getParameter(), systemCfg.getValue());
//			 if (systemCfg.getParameter().indexOf("Sys") >= 0 ) {
//				 // 更新SystemInfo
////				 sysMsgsMap.put(systemCfg.getParameter(), systemCfg.getValue());
//				 systemParam.put(systemCfg.getParameter(), systemCfg.getValue());
//			 }else if(systemCfg.getParameter().indexOf("EstablishedStatus") >= 0){
//				 systemParam.put("EstablishedStatus", systemCfg.getValue());
//			 }else if(systemCfg.getParameter().indexOf("AfterCallStatus") >= 0){
//				 systemParam.put("AfterCallStatus", systemCfg.getValue());
//			 }else if(systemCfg.getParameter().indexOf("MaxRingTime") >= 0){
//				 systemParam.put("MaxRingTime", systemCfg.getValue());
//			 }
//			 Util.getConsoleLogger().debug("systemCfg.getParameter(): " + systemCfg.getParameter());
//			 Util.getConsoleLogger().debug("systemCfg.getValue(): " + systemCfg.getValue());
		 }// end of for

		
		
        Util.setSystemParam(systemParam);

        // 將systemParam(ip,port) 資訊轉換為JSON格式,傳給前端頁面
        Map<String, Object> propertiesMap = convertProperties2TreeMap(prop);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String propertiesJson = gson.toJson(propertiesMap);
//        System.out.println("Ready, converts " + prop.size() + " entries.");        
//        Util.getConsoleLogger().debug("propertiesJson: " + propertiesJson );        
        Util.getFileLogger().info("propertiesJson: " + propertiesJson );        
        context.setAttribute("systemParam", propertiesJson); // 將此物件放入大廳中,讓前端使用者可取得相關URL資訊

        // 更新statusList
        Cfg_AgentStatus agentstatus = new Cfg_AgentStatus(); 
        MaintainService maintainservice = new MaintainService();		
		List<Cfg_AgentStatus> agentstatuslist = maintainservice.Select_cfg_agentstatus(agentstatus);
//		Util.getConsoleLogger().debug("agentstatuslist: " + agentstatuslist);
		for (Cfg_AgentStatus agentStatus: agentstatuslist){
//			Util.getConsoleLogger().debug("agentStatus.getStatusname(): " + agentStatus.getStatusname());
		}
		
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
//				 Util.getConsoleLogger().debug("statusname: " + statusname);
//				 Util.getConsoleLogger().debug("StatusEnum.READY.toString(): " + StatusEnum.READY.toString());
				 updateStatusEnum(statusname, dbid, description);
			 }
		 }
		 Util.setAgentStatus(agentstatusmap);
//		 Util.getConsoleLogger().debug("agentstatusmap: "+agentstatusmap);
		 
		 
		 /** 建立websocket物件，並設定port **/
	    startWebsocketOnline();
		  		 
    }
     
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // do nothing
//    	Util.getFileLogger().info("contextDestroyed() called");
//    	Map<WebSocket, UserInfo> agentConnsMap = WebSocketTypePool.getTypeconnections().get(WebSocketTypePool.AGENT);
//    	Set<WebSocket> agebtConns = agentConnsMap.keySet();
//    	for (WebSocket conn: agebtConns){
//    		conn.close();
//    	}
    }  
    
    
    private void updateStatusEnum(String aStatusname, String aDbid, String aDescription){
    	StatusEnum currStatusEnum = StatusEnum.getStatusEnumByDescription(aStatusname);
    	currStatusEnum.setDbid(aDbid);
    	currStatusEnum.setDescription(aDescription);
//    	Util.getConsoleLogger().debug("updateStatusEnum() - " + currStatusEnum);
    }
    
    
    private Map<String, Object> convertProperties2TreeMap(Properties aProp){
        Map<String, Object> map = new TreeMap<>();

        for (Object key : aProp.keySet()) {
            List<String> keyList = Arrays.asList(((String) key).split("\\."));
            Map<String, Object> valueMap = createTree(keyList, map); // another method below
            String value = aProp.getProperty((String) key);
            value = StringEscapeUtils.unescapeHtml(value); // common lang package
            valueMap.put(keyList.get(keyList.size() - 1), value);
        }
        return map;
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> createTree(List<String> keys, Map<String, Object> map) {
        Map<String, Object> valueMap = (Map<String, Object>) map.get(keys.get(0));
        if (valueMap == null) {
            valueMap = new HashMap<String, Object>();
        }
        map.put(keys.get(0), valueMap);
        Map<String, Object> out = valueMap;
        if (keys.size() > 2) {
            out = createTree(keys.subList(1, keys.size()), valueMap);
        }
        return out;
    }
    
    
	/** * Start Socket Service */
	public void startWebsocketOnline() {

		Util.getConsoleLogger().info("Starting websocket");
		WebSocketImpl.DEBUG = false;
		int port = Integer.parseInt(Util.getSystemParam().get("websocket_port"));
//		Util.getConsoleLogger().debug("WebSocket port: " + port);
//		int port = 8888;
		WebChatChannel s = null;
		try {
			s = new WebChatChannel(port);
			s.start();
		} catch (UnknownHostException e) {
			Util.getConsoleLogger().error("Starting websocket failed！");
			Util.getConsoleLogger().error(e.getMessage());
			e.printStackTrace();
		}
		Util.getConsoleLogger().info("Starting websocket success！");

//		Log4jDemo();

	}
    
}
