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

import com.Info360.bean.Cfg_AgentReason;
import com.Info360.bean.Cfg_AgentStatus;
import com.Info360.bean.CommonLink;
import com.Info360.service.MaintainService;



@Path("/Select_cfg_agentstatus")
public class Cfg_AgentStatus_Select_Servlet {
	

	@POST
	@Produces("application/json")
	public Response PostFromPath(
			//@FormParam("flag") int flag
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		Cfg_AgentStatus agentstatus = new Cfg_AgentStatus();
		
		//agentreason.setFlag(flag);
			 
		MaintainService maintainservice = new MaintainService();		
		List<Cfg_AgentStatus> agentstatuslist = maintainservice.Select_cfg_agentstatus(agentstatus);
	    
  	  JSONArray agentstatusJsonArray = new JSONArray();
  	  	
  	  	//System.out.println("agentstatuslist: "+agentstatuslist.size());

  	 
  		 for(int a = 0; a < agentstatuslist.size(); a++){
    		
	    	JSONObject agentstatusObject = new JSONObject();
	    
	    	agentstatusObject.put("dbid", agentstatuslist.get(a).getDbid());
	    	agentstatusObject.put("statusname", agentstatuslist.get(a).getStatusname());
	    	agentstatusObject.put("mediatypeid", agentstatuslist.get(a).getMediatypeid());
	    	agentstatusObject.put("description", agentstatuslist.get(a).getDescription());
	    	
	    	agentstatusJsonArray.put(agentstatusObject);
  		 }
    		jsonObject.put("agentstatus", agentstatusJsonArray);
  
  	  
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
