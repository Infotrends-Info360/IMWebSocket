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

import com.Info360.bean.Activitydata;
import com.Info360.bean.Activitygroups;
import com.Info360.bean.Activitymenu;
import com.Info360.bean.Cfg_AgentReason;
import com.Info360.bean.CommonLink;
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/Query_ActivityMenu")
public class Query_ActivityMenu_Servlet {
	

	@POST
	@Produces("application/json")
	public Response PostFromPath(
			@FormParam("dbid") int dbid
			
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		Activitymenu activitymenu = new Activitymenu();
		
		activitymenu.setDbid(dbid);
	
		MaintainService maintainservice = new MaintainService();	
		
		JSONArray ActivitymenuJsonArray = new JSONArray();
  	 
		List<Activitymenu> activitymenulist = maintainservice.Select_activitymenu(activitymenu);
	    
  	  for (int i = 0; i < activitymenulist.size(); i++) {
  		  	JSONObject activitymenuObject = new JSONObject();
  	  		activitymenuObject.put("dbid", activitymenulist.get(i).getDbid());
  	  		activitymenuObject.put("createdatetime", activitymenulist.get(i).getCreatedatetime());
  	  		activitymenuObject.put("deletedatetime", activitymenulist.get(i).getDeletedatetime());
  	  		activitymenuObject.put("deleteflag", activitymenulist.get(i).getDeleteflag());
  	  		activitymenuObject.put("menuname", activitymenulist.get(i).getMenuname());
  	  		activitymenuObject.put("sort", activitymenulist.get(i).getSort());
  	  	
  	  		ActivitymenuJsonArray.put(activitymenuObject);
  	  }
    		jsonObject.put("activitymenu", ActivitymenuJsonArray);
  	  	
    		if(dbid!=0){
    			
    			Activitygroups activitygroups = new Activitygroups();
    			JSONArray ActivitygroupsJsonArray = new JSONArray();
    			JSONArray Flag0groupsJsonArray = new JSONArray();
    			JSONArray Flag1groupsJsonArray = new JSONArray();
        		

        		activitygroups.setActivitymenuid(activitymenu.getDbid());
        		List<Activitygroups> activitygroupslist = maintainservice.Select_activitygroups(activitygroups);
        		
        		for(int a = 0; a < activitygroupslist.size(); a++){
    			
        	  	  	JSONObject activitygroupsObject = new JSONObject();
        	  	activitygroupsObject.put("dbid", activitygroupslist.get(a).getDbid());
//        	  	activitygroupsObject.put("createdatetime", activitygroupslist.get(a).getCreatedatetime());
        	  	activitygroupsObject.put("activitymenuid", activitygroupslist.get(a).getActivitymenuid());
        	  	activitygroupsObject.put("groupname", activitygroupslist.get(a).getGroupname());
        	  	activitygroupsObject.put("sort", activitygroupslist.get(a).getSort());
        	  	activitygroupsObject.put("deleteflag", activitygroupslist.get(a).getDeleteflag());
        	  	
        	  	ActivitygroupsJsonArray.put(activitygroupsObject);
        	  	
        	  	if(activitygroupslist.get(a).getDeleteflag().equals("0")){
        	  		
        	  		if(activitygroupslist.get(a).getCreatedatetime()!=null && activitygroupslist.get(a).getCreatedatetime()!=""){
        	  			activitygroupsObject.put("createdatetime",activitygroupslist.get(a).getCreatedatetime());
        	  			Flag0groupsJsonArray.put(activitygroupsObject);
        					
        			}else {
        				activitygroupsObject.put("createdatetime","");
        						Flag0groupsJsonArray.put(activitygroupsObject);
        					}
        	  		
        	  	}
        	  	
        	  	
        	  	
        	  	if(activitygroupslist.get(a).getDeleteflag().equals("1")){
        	  		if(activitygroupslist.get(a).getDeletedatetime()!=null && activitygroupslist.get(a).getDeletedatetime()!=""){
        	  			activitygroupsObject.put("deletedatetime",activitygroupslist.get(a).getDeletedatetime());
        	  			Flag1groupsJsonArray.put(activitygroupsObject);
        					
        			}else {
        				activitygroupsObject.put("deletedatetime","");
        				Flag1groupsJsonArray.put(activitygroupsObject);
        					}
        	  	}
        	  	
    		}
        		jsonObject.put("flag1_group", Flag1groupsJsonArray);
        		jsonObject.put("flag0_group", Flag0groupsJsonArray);
        		
        		jsonObject.put("activitygroups", ActivitygroupsJsonArray);
    		}


		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
