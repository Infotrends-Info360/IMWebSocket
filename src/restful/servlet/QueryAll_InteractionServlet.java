package restful.servlet;

import java.io.IOException;



import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import util.Util;

import com.Info360.bean.Interaction;
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/QueryAll_Interaction")
public class QueryAll_InteractionServlet {
	

	@POST
	@Produces("application/json")
	public Response PostFromPath(
			
			@FormParam("agentid") String agentid,
			@FormParam("startdate") String startdate,
			@FormParam("enddate") String enddate
			
			
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		
		Interaction interaction = new Interaction();
		
		interaction.setAgentid(agentid);
		interaction.setStartdate(startdate);
		interaction.setEnddate(enddate);
		

		MaintainService maintainservice = new MaintainService();		
		List<Interaction> interactionlist = maintainservice.SelcetAll_interaction(interaction);
		JSONArray InteractionJsonArray = new JSONArray();
		
		Util.getConsoleLogger().debug(interactionlist.size());
		
		InteractionJsonArray.put(interactionlist.size());
//  	    	for(int a = 0; a < interactionlist.size(); a++){
//
//   	    	JSONObject InteractionJsonObject = new JSONObject();
//  	    		
//  	    	InteractionJsonObject.put("dbid", interactionlist.get(a).getDbid());
//  	    	InteractionJsonObject.put("status", interactionlist.get(a).getStatus());
//  	    	InteractionJsonObject.put("activitycode", interactionlist.get(a).getActivitycode());
//  	    	InteractionJsonObject.put("agentid", interactionlist.get(a).getAgentid());
//  	    	InteractionJsonObject.put("contactid", interactionlist.get(a).getContactid());
//  	    	InteractionJsonObject.put("enddate", interactionlist.get(a).getEnddate());
//  	    	InteractionJsonObject.put("entitytypeid", interactionlist.get(a).getEntitytypeid());
//  	    	InteractionJsonObject.put("ixnid", interactionlist.get(a).getIxnid());
//  	    	InteractionJsonObject.put("startdate", interactionlist.get(a).getStartdate());
//  	    	InteractionJsonObject.put("stoppedreason", interactionlist.get(a).getStoppedreason());
//  	    	InteractionJsonObject.put("structuredmimetype", interactionlist.get(a).getStructuredmimetype());
//  	    	InteractionJsonObject.put("structuredtext", interactionlist.get(a).getStructuredtext());
//  	    	InteractionJsonObject.put("subject", interactionlist.get(a).getSubject());
//  	    	InteractionJsonObject.put("subtypeid", interactionlist.get(a).getSubtypeid());
//  	    	InteractionJsonObject.put("text", interactionlist.get(a).getText());
//  	    	InteractionJsonObject.put("thecomment", interactionlist.get(a).getThecomment());
//  	    	InteractionJsonObject.put("typeid", interactionlist.get(a).getTypeid());
//  			
//  	    	InteractionJsonArray.put(InteractionJsonObject);
//  	  	}
	
		
  	    jsonObject.put("Interaction", interactionlist.size());
  	    
  	  
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
