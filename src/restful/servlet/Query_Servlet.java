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

import com.Info360.bean.Activitydata;
import com.Info360.bean.CFG_person;
import com.Info360.bean.Interaction;
import com.Info360.bean.Rpt_Activitylog;
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/Query")
public class Query_Servlet {
	

	
	@POST
	@Produces("application/json")
	public Response PostFromPath(
			
			@FormParam("startdate") String startdate,
			@FormParam("enddate") String enddate,
			@FormParam("agentid") String agentid
			
			
//			@FormParam("interactionid") String interactionid
			
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		
		Interaction interaction = new Interaction();
		Rpt_Activitylog rpt_activitylog = new Rpt_Activitylog();
		Activitydata activitydata = new Activitydata();
		CFG_person cfg_person = new CFG_person();
		
		interaction.setStartdate(startdate);
		interaction.setEnddate(enddate);
		if(agentid!=null){
			interaction.setAgentid(agentid);
		}
		
		
		MaintainService maintainservice = new MaintainService();		
		List<Interaction> interactionlist = maintainservice.Selcet_interaction(interaction);
		
		JSONArray InteractionJsonArray = new JSONArray();
		JSONArray Rpt_ActivitylogJsonArray = new JSONArray();
		JSONArray ActivitydataJsonArray = new JSONArray();
		JSONArray PersonJsonArray = new JSONArray();
		JSONArray testArray = new JSONArray();
		
  	    	for(int a = 0; a < interactionlist.size(); a++){

   	    	JSONObject InteractionJsonObject = new JSONObject();
  	    		
  	    	InteractionJsonObject.put("agentid", interactionlist.get(a).getAgentid());
  	    	InteractionJsonObject.put("enddate", interactionlist.get(a).getEnddate());
  	    	InteractionJsonObject.put("entitytypeid", interactionlist.get(a).getEntitytypeid());
  	    	InteractionJsonObject.put("ixnid", interactionlist.get(a).getIxnid());
  	    	InteractionJsonObject.put("startdate", interactionlist.get(a).getStartdate());
  	    	InteractionJsonObject.put("thecomment", interactionlist.get(a).getThecomment());
  	    	InteractionJsonObject.put("entitytypeid", interactionlist.get(a).getEntitytypeid());

  	    	InteractionJsonArray.put(InteractionJsonObject);
  	    	
  	    	
  	    	rpt_activitylog.setInteractionid(interactionlist.get(a).getIxnid());
  	    	List<Rpt_Activitylog> rpt_activityloglist = maintainservice.Selcet_activitylog(rpt_activitylog);
  	    	
  	    	for(int g = 0; g < rpt_activityloglist.size(); g++){
  	    		JSONObject rpt_activitylogJsonObject = new JSONObject();
  	    		
  	    		rpt_activitylogJsonObject.put("interactionid", rpt_activityloglist.get(g).getInteractionid());
  	    		rpt_activitylogJsonObject.put("activitydataid", rpt_activityloglist.get(g).getActivitydataid());
  	    		rpt_activitylogJsonObject.put("datetime", rpt_activityloglist.get(g).getDatetime());
  	    		
  	    		Rpt_ActivitylogJsonArray.put(rpt_activitylogJsonObject);
  	    	   		
  	    		
  	    		if(rpt_activityloglist.get(g).getActivitydataid()!=null &&!rpt_activityloglist.get(g).getActivitydataid().equals("")){
  	    			
  	    				Integer aa = Integer.valueOf(rpt_activityloglist.get(g).getActivitydataid());
  	    		
  	    				activitydata.setDbid(aa);
  	    				List<Activitydata> activitydatalist = maintainservice.IXN_activitydata(activitydata);
  	  	    
  	    				for(int b = 0; b < activitydatalist.size(); b++){
  	    			
  	    					JSONObject activitydataObject = new JSONObject();
  	    					activitydataObject.put("activitygroupsid", activitydatalist.get(b).getActivitygroupsid());
  	    					activitydataObject.put("codename", activitydatalist.get(b).getCodename());
  	    					activitydataObject.put("deleteflag", activitydatalist.get(b).getDeleteflag());
        			
  	    					ActivitydataJsonArray.put(activitydataObject);
  	    					
  	    						Integer bb = Integer.valueOf(interactionlist.get(a).getAgentid());
  	    							cfg_person.setDbid(bb);
  	    								List<CFG_person> cfg_personlist = maintainservice.query_Person_DBID(cfg_person);
  	    									for(int d = 0; d < cfg_personlist.size(); d++){
  	    											JSONObject cfg_personObject = new JSONObject();
  	    											cfg_personObject.put("username", cfg_personlist.get(d).getUser_name());

  	    											PersonJsonArray.put(cfg_personObject);
  	    											
  	    											
  	    											JSONObject testobj = new JSONObject();

  	    											testobj.put("User_name", cfg_personlist.get(d).getUser_name());
  	    											testobj.put("Thecomment", interactionlist.get(a).getThecomment());
  	    											testobj.put("Startdate", interactionlist.get(a).getStartdate());
  	    											testobj.put("Enddate", interactionlist.get(a).getEnddate());
  	    											testobj.put("Codename", activitydatalist.get(b).getCodename());
  	    											
  	    											if(interactionlist.get(a).getEntitytypeid().equals("2")){
  	    												testobj.put("src", "chat");
  	  	    											
  	    											}
  	    											
  	    										
  	    											testArray.put(testobj);
  	    									}
  	    					
  	    				}
  	    		}
  	    	
  	    	}
  	    	
  	    	}
//  	    jsonObject.put("Interaction", InteractionJsonArray);
//  	    jsonObject.put("Rpt_Activitylog", Rpt_ActivitylogJsonArray);
//  	    jsonObject.put("Activitydata", ActivitydataJsonArray);
//  	    jsonObject.put("Person", PersonJsonArray);
  	    jsonObject.put("data", testArray);

  	  
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
