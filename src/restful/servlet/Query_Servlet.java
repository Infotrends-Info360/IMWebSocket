package restful.servlet;

import java.io.IOException;



import java.util.ArrayList;
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
		
		JSONArray PersonJsonArray = new JSONArray();
		JSONArray testArray = new JSONArray();
		
//			List<String> name_list = new ArrayList<String>();
		String name = "";

  	    	for(int a = 0; a < interactionlist.size(); a++){

  	    	
  	    	rpt_activitylog.setInteractionid(interactionlist.get(a).getIxnid());
  	    	List<Rpt_Activitylog> rpt_activityloglist = maintainservice.Selcet_activitylog(rpt_activitylog);
  	    	
  	    	for(int g = 0; g < rpt_activityloglist.size(); g++){

  	    		
//  	    		System.out.println(rpt_activityloglist.get(g).getActivitydataid());
  	    		
  	    		if(rpt_activityloglist.get(g).getActivitydataid()!=null &&
  					!rpt_activityloglist.get(g).getActivitydataid().equals("") &&
  						!rpt_activityloglist.get(g).getActivitydataid().equals("null")){
  	    		
  	    			//for(int j =0; j<rpt_activityloglist.size();j++){
  	    				//if(rpt_activityloglist.get(g).getActivitydataid()!=null && !rpt_activityloglist.get(g).getActivitydataid().equals("")){
  	    				activitydata.setDbid(Integer.valueOf(rpt_activityloglist.get(g).getActivitydataid()));
  	  	    			
  	  	    			List<Activitydata> activitydatalist = maintainservice.IXN_activitydata(activitydata);
  	  	    			//activitydatalist.get(0).getCodename();
  	  	    			name+=activitydatalist.get(0).getCodename()+",";
//  	  	    			name_list.add(activitydatalist.get(0).getCodename());
  	    				//}
  	  	    			
  	    			//}
  	    		}
  	    		
  	    	}	
  	    	
  	    	
  	    	
  	    	
  	    		if(interactionlist.get(a).getAgentid()!=null &&
  	  					!interactionlist.get(a).getAgentid().equals("") &&
  	  						!interactionlist.get(a).getAgentid().equals("null")){	
  	    		Integer bb = Integer.valueOf(interactionlist.get(a).getAgentid());
					cfg_person.setDbid(bb);
  	    		}
						List<CFG_person> cfg_personlist = maintainservice.query_Person_DBID(cfg_person);
							for(int d = 0; d < cfg_personlist.size(); d++){
									JSONObject cfg_personObject = new JSONObject();
									cfg_personObject.put("username", cfg_personlist.get(d).getUser_name());

									PersonJsonArray.put(cfg_personObject);
									
									
									JSONObject testobj = new JSONObject();

									testobj.put("Agentname", cfg_personlist.get(d).getUser_name());
									testobj.put("Thecomment", interactionlist.get(a).getThecomment());
									testobj.put("Startdate", interactionlist.get(a).getStartdate());
									testobj.put("Enddate", interactionlist.get(a).getEnddate());
									if(name.length()>0){
										testobj.put("Codename",name.substring(0, name.length()-1));
									}else{
										testobj.put("Codename",name);
									}
									
									testobj.put("ixnid", interactionlist.get(a).getIxnid());
									
									if(interactionlist.get(a).getEntitytypeid().equals("2")){
										testobj.put("src", "chat");
										
									}
									
								
									testArray.put(testobj);
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
