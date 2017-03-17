package restful.servlet;

import java.io.IOException;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.Info360.bean.Activitydata;
import com.Info360.bean.CFG_person;
import com.Info360.bean.ContactData;
import com.Info360.bean.Interaction;
import com.Info360.bean.Rpt_Activitylog;
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/detailQuery")
public class detailQuery_Servlet {
	

	
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
		ContactData contactdata = new ContactData();
		
		interaction.setStartdate(startdate);
		interaction.setEnddate(enddate);
		if(agentid!=null){
			interaction.setAgentid(agentid);
		}
		
		
		MaintainService maintainservice = new MaintainService();		
		List<Interaction> interactionlist = maintainservice.Selcet_Detail_interaction(interaction);
		
		JSONArray PersonJsonArray = new JSONArray();
		JSONArray testArray = new JSONArray();
		JSONArray ContactDataArray = new JSONArray();
		
		

  	    	String name = "";
  	    	System.out.println("Ixn: "+interactionlist.get(0).getIxnid());
  	    	rpt_activitylog.setInteractionid(interactionlist.get(0).getIxnid());
  	    	List<Rpt_Activitylog> rpt_activityloglist = maintainservice.Selcet_activitylog(rpt_activitylog);
  	    	
	  	    	for(int g = 0; g < rpt_activityloglist.size(); g++){
	  	    		if(rpt_activityloglist.get(g).getActivitydataid()!=null &&
	  					!rpt_activityloglist.get(g).getActivitydataid().equals("") &&
	  						!rpt_activityloglist.get(g).getActivitydataid().equals("null")){
	  	    			
	  	    	    	System.out.println("Activitydataid: "+Integer.valueOf(rpt_activityloglist.get(g).getActivitydataid()));

	  	    				activitydata.setDbid(Integer.valueOf(rpt_activityloglist.get(g).getActivitydataid()));
	  	  	    			List<Activitydata> activitydatalist = maintainservice.IXN_activitydata(activitydata);
	  	  	    			if(activitydatalist.size()>0){
	  	  	    				name+=activitydatalist.get(0).getCodename()+",";
	  	  	    			}else{
	  	  	    				name+=rpt_activityloglist.get(g).getActivitydataid()+"[無此代碼],";
	  	  	    			}
	  	    		}
	  	    	}	
	  	    	
	  	    	
	  	    	
	  	    	if(interactionlist.get(0).getAgentid()!=null &&
		  					!interactionlist.get(0).getAgentid().equals("") &&
		  						!interactionlist.get(0).getAgentid().equals("null")
//		  						
		  				&&interactionlist.get(0).getContactid()!=null &&
				  					!interactionlist.get(0).getContactid().equals("") &&
				  						!interactionlist.get(0).getContactid().equals("null")		
	  	    			
	  	    			){	
		    		Integer bb = Integer.valueOf(interactionlist.get(0).getAgentid());
					cfg_person.setDbid(bb);
					
			    	List<CFG_person> cfg_personlist = maintainservice.query_Person_DBID(cfg_person);
							JSONObject cfg_personObject = new JSONObject();
							cfg_personObject.put("username", cfg_personlist.get(0).getUser_name());
							PersonJsonArray.put(cfg_personObject);
							
							
							
								String Contactid = interactionlist.get(0).getContactid().trim();
					    		contactdata.setContactid(Contactid);
					    		System.out.println("Contactid: "+Contactid);
					    		
					    			Map<String, String> contactidmap = maintainservice.Query_Contactdata(Contactid);
//						    		JSONObject contactdataObject = new JSONObject();
//						    		contactdataObject.put("userdata", contactidmap);
//						    		ContactDataArray.put(contactdataObject);
						
							
							
							
							JSONObject testobj = new JSONObject();
							testobj.put("Agentname", cfg_personlist.get(0).getUser_name());
							testobj.put("Thecomment", interactionlist.get(0).getThecomment());
							testobj.put("Startdate", interactionlist.get(0).getStartdate().substring(0, 19));
							testobj.put("Enddate", interactionlist.get(0).getEnddate().substring(0, 19));
							testobj.put("BasicINF", contactidmap);
							testobj.put("Structuredtext", interactionlist.get(0).getStructuredtext());
//							testobj.put("pp",);


							if(name.length()>0){
								testobj.put("Codename",name.substring(0, name.length()-1));
							}else{
								testobj.put("Codename",name);
							}
							testobj.put("ixnid", interactionlist.get(0).getIxnid());
							if(interactionlist.get(0).getEntitytypeid().equals("2")){
								testobj.put("src", "chat");
							}
							testArray.put(testobj);
					
		    		
	  	    	}
	  	    	
  	    	
  	    jsonObject.put("data", testArray);

  	  
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
