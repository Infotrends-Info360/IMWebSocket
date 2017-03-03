package filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
 












import util.StatusEnum;

















import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.PropertyConfigurator;

import com.Info360.bean.Cfg_AgentStatus;
import com.Info360.service.MaintainService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
        systemParam.put("MaxRingTime", maxRingTime);
        systemParam.put("AfterCallStatus", afterCallStatus);
        systemParam.put("EstablishedStatus", establishedStatus);
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
			Util.getConsoleLogger().warn("property file '" + propFileName + "' not found in the classpath");
			Util.getFileLogger().warn("property file '" + propFileName + "' not found in the classpath");
		}
		
		String  websocket_protocol = prop.getProperty("websocket.protocol");
		String  websocket_hostname = prop.getProperty("websocket.hostname");
		String  websocket_port = prop.getProperty("websocket.port");
		String  IMWebSocket_protocol = prop.getProperty("IMWebSocket.protocol");
		String  IMWebSocket_hostname = prop.getProperty("IMWebSocket.hostname");
		String  IMWebSocket_port = prop.getProperty("IMWebSocket.port");
		String  info360_protocol = prop.getProperty("info360.protocol");
		String  info360_hostname = prop.getProperty("info360.hostname");
		String  info360_port = prop.getProperty("info360.port");
		String  Info360_Setting_protocol = prop.getProperty("Info360_Setting.protocol");
		String  Info360_Setting_hostname = prop.getProperty("Info360_Setting.hostname");
		String  Info360_Setting_port = prop.getProperty("Info360_Setting.port");
		String  Upload_Files_protocol = prop.getProperty("Upload_Files.protocol");
		String  Upload_Files_hostname = prop.getProperty("Upload_Files.hostname");
		String  Upload_Files_port = prop.getProperty("Upload_Files.port");
		String  ServiceNameCache_protocol = prop.getProperty("ServiceNameCache.protocol");
		String  ServiceNameCache_hostname = prop.getProperty("ServiceNameCache.hostname");
		String  ServiceNameCache_port = prop.getProperty("ServiceNameCache.port");
		String  infoacd_protocol = prop.getProperty("infoacd.protocol");
		String  infoacd_hostname = prop.getProperty("infoacd.hostname");
		String  infoacd_port = prop.getProperty("infoacd.port");

//		Util.getConsoleLogger().debug("websocket_protocol: " + websocket_protocol);
//		Util.getConsoleLogger().debug("websocket_hostname: " + websocket_hostname);
//		Util.getConsoleLogger().debug("websocket_port: " + websocket_port);
//		Util.getConsoleLogger().debug("info360_protocol: " + info360_protocol);
//		Util.getConsoleLogger().debug("info360_hostname: " + info360_hostname);
//		Util.getConsoleLogger().debug("info360_port: " + info360_port);
//		Util.getConsoleLogger().debug("Info360_Setting_protocol: " + Info360_Setting_protocol);
//		Util.getConsoleLogger().debug("Info360_Setting_hostname: " + Info360_Setting_hostname);
//		Util.getConsoleLogger().debug("Info360_Setting_port: " + Info360_Setting_port);
//		Util.getConsoleLogger().debug("Upload_Files_protocol: " + Upload_Files_protocol);
//		Util.getConsoleLogger().debug("Upload_Files_hostname: " + Upload_Files_hostname);
//		Util.getConsoleLogger().debug("Upload_Files_port: " + Upload_Files_port);
//		Util.getConsoleLogger().debug("ServiceNameCache_protocol: " + ServiceNameCache_protocol);
//		Util.getConsoleLogger().debug("ServiceNameCache_hostname: " + ServiceNameCache_hostname);
//		Util.getConsoleLogger().debug("ServiceNameCache_port: " + ServiceNameCache_port);
		
		systemParam.put("websocket_protocol", websocket_protocol);
		systemParam.put("websocket_hostname", websocket_hostname);
		systemParam.put("websocket_port", websocket_port);
		systemParam.put("IMWebSocket_protocol", IMWebSocket_protocol);
		systemParam.put("IMWebSocket_hostname", IMWebSocket_hostname);
		systemParam.put("IMWebSocket_port", IMWebSocket_port);		
		systemParam.put("info360_protocol", info360_protocol);
		systemParam.put("info360_hostname", info360_hostname);
		systemParam.put("info360_port", info360_port);
		systemParam.put("Info360_Setting_protocol", Info360_Setting_protocol);
		systemParam.put("Info360_Setting_hostname", Info360_Setting_hostname);
		systemParam.put("Info360_Setting_port", Info360_Setting_port);
		systemParam.put("Upload_Files_protocol", Upload_Files_protocol);
		systemParam.put("Upload_Files_hostname", Upload_Files_hostname);
		systemParam.put("Upload_Files_port", Upload_Files_port);
		systemParam.put("ServiceNameCache_protocol", ServiceNameCache_protocol);
		systemParam.put("ServiceNameCache_hostname", ServiceNameCache_hostname);
		systemParam.put("ServiceNameCache_port", ServiceNameCache_port);
		systemParam.put("infoacd_protocol", infoacd_protocol);
		systemParam.put("infoacd_hostname", infoacd_hostname);
		systemParam.put("infoacd_port", infoacd_port);
		
        Util.setSystemParam(systemParam);

//        Properties properties = ...;

        Map<String, Object> propertiesMap = convertProperties2TreeMap(prop);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String propertiesJson = gson.toJson(propertiesMap);
//        System.out.println("Ready, converts " + prop.size() + " entries.");        
        System.out.println("propertiesJson: " + propertiesJson );        
        context.setAttribute("systemParam", propertiesJson); // 將此物件放入大廳中

		
		
        
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
//				 Util.getConsoleLogger().debug("statusname: " + statusname);
//				 Util.getConsoleLogger().debug("StatusEnum.READY.toString(): " + StatusEnum.READY.toString());
				 updateStatusEnum(statusname, dbid, description);
			 }
		 }
		 Util.setAgentStatus(agentstatusmap);
		 Util.getConsoleLogger().debug("agentstatusmap: "+agentstatusmap);
		 
		  
		 //測試區
//		 Util.getConsoleLogger().debug("TimerTaskRingHeartBeat test: ");
//		 UserInfo agentUserInfo = new UserInfo();
//		 RingCountDownTask timertask = new RingCountDownTask(null, "", agentUserInfo);
//		 timertask.operate();
		 
//		 try {
//			Thread.sleep(5000);
//			agentUserInfo.setStopRing(true);
//		} catch (InterruptedException e) {
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
}
