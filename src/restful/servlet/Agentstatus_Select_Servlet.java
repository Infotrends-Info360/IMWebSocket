package restful.servlet;

import java.io.IOException;



import java.math.BigDecimal;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.Info360.bean.Agentstatus;
import com.Info360.bean.CommonLink;
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/Select_agentstatus")
public class Agentstatus_Select_Servlet {
	

	@POST
	@Produces("application/json")
	public Response PostFromPath(
			@FormParam("flag") int flag
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		Agentstatus agentstatus = new Agentstatus();
		
		agentstatus.setFlag(flag);
		 agentstatus.getFlag();
		 
		 
		MaintainService maintainservice = new MaintainService();		
		List<Agentstatus> agentstatuslist = maintainservice.Select_agentstatus(agentstatus);
	    
  	  JSONArray AgentstatusJsonArray = new JSONArray();
  	  	
  	  	//System.out.println("agentstatuslist: "+agentstatuslist.size());

  	 if(flag==0){
  		 for(int a = 0; a < agentstatuslist.size(); a++){
    		
	    	JSONObject AgentstatusObject = new JSONObject();
	    
	    	AgentstatusObject.put("dbid", agentstatuslist.get(a).getDbid());
	    	AgentstatusObject.put("statusname", agentstatuslist.get(a).getStatusname());
	    	AgentstatusObject.put("statusname_cn", agentstatuslist.get(a).getStatusname_cn());
	    	AgentstatusObject.put("statusname_en", agentstatuslist.get(a).getStatusname_en());
	    	AgentstatusObject.put("statusname_tw", agentstatuslist.get(a).getStatusname_tw());
	    	AgentstatusObject.put("description", agentstatuslist.get(a).getDescription());
	    	AgentstatusObject.put("alarmduration", agentstatuslist.get(a).getAlarmduration());
	    	AgentstatusObject.put("alarmcolor", agentstatuslist.get(a).getAlarmcolor());
	    	AgentstatusObject.put("flag", "啟用");
	    	AgentstatusObject.put("createdatetime", agentstatuslist.get(a).getCreatedatetime());
	    	AgentstatusObject.put("createuserid", agentstatuslist.get(a).getCreateuserid());
	    	
	    		AgentstatusJsonArray.put(AgentstatusObject);
  		 }
  		System.out.println("set FLAG==0");
  	 }
  	  if(flag==1){
    	for(int a = 0; a < agentstatuslist.size(); a++){
    		
	    	JSONObject AgentstatusObject = new JSONObject();
	    
	    	AgentstatusObject.put("dbid", agentstatuslist.get(a).getDbid());
	    	AgentstatusObject.put("statusname", agentstatuslist.get(a).getStatusname());
	    	AgentstatusObject.put("statusname_cn", agentstatuslist.get(a).getStatusname_cn());
	    	AgentstatusObject.put("statusname_en", agentstatuslist.get(a).getStatusname_en());
	    	AgentstatusObject.put("statusname_tw", agentstatuslist.get(a).getStatusname_tw());
	    	AgentstatusObject.put("description", agentstatuslist.get(a).getDescription());
	    	AgentstatusObject.put("alarmduration", agentstatuslist.get(a).getAlarmduration());
	    	AgentstatusObject.put("alarmcolor", agentstatuslist.get(a).getAlarmcolor());
	    	AgentstatusObject.put("flag", "停用");
	    	AgentstatusObject.put("createdatetime", agentstatuslist.get(a).getCreatedatetime());
	    	AgentstatusObject.put("createuserid", agentstatuslist.get(a).getCreateuserid());

	    		AgentstatusJsonArray.put(AgentstatusObject);
  	}
    	System.out.println("set FLAG==1");
  	  }else{
  		  System.out.println("set FLAG異位");
  	  }
    		jsonObject.put("agentstatus", AgentstatusJsonArray);
  
  	  
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
