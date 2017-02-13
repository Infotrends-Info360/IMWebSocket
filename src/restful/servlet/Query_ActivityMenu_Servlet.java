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
import com.Info360.bean.Agentstatus;
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
    			
        		Activitydata activitydata = new Activitydata();
        		JSONArray ActivitydataJsonArray = new JSONArray();
        		JSONArray TitleflagJsonArray = new JSONArray();

        		activitygroups.setActivitymenuid(activitymenu.getDbid());
        		List<Activitygroups> activitygroupslist = maintainservice.Select_activitygroups(activitygroups);
        		
        		for(int a = 0; a < activitygroupslist.size(); a++){
    			
        	  	  	JSONObject activitygroupsObject = new JSONObject();
        	  	activitygroupsObject.put("dbid", activitygroupslist.get(a).getDbid());
        	  	activitygroupsObject.put("createdatetime", activitygroupslist.get(a).getCreatedatetime());
        	  	activitygroupsObject.put("deletedatetime", activitygroupslist.get(a).getDeletedatetime());
        	  	activitygroupsObject.put("activitymenuid", activitygroupslist.get(a).getActivitymenuid());
        	  	activitygroupsObject.put("groupname", activitygroupslist.get(a).getGroupname());
        	  	activitygroupsObject.put("sort", activitygroupslist.get(a).getSort());
        	  	
        	  	ActivitygroupsJsonArray.put(activitygroupsObject);
        	  	
        	 // 	System.out.println("GroupDbid: "+activitygroupslist.get(a).getDbid());
        	  	
        	  	activitydata.setActivitygroupsid(activitygroupslist.get(a).getDbid());
    	  		List<Activitydata> activitydatalist = maintainservice.Select_activitydata(activitydata);
    	  		
        	  	for(int g = 0; g < activitydatalist.size(); g++){
        	  		
        	  		JSONObject activitydataObject = new JSONObject();
        			activitydataObject.put("dbid", activitydatalist.get(g).getDbid());
        			activitydataObject.put("createdatetime", activitydatalist.get(g).getCreatedatetime());
        			activitydataObject.put("deletedatetime", activitydatalist.get(g).getDeletedatetime());
        			activitydataObject.put("activitygroupsid", activitydatalist.get(g).getActivitygroupsid());
        			activitydataObject.put("codename", activitydatalist.get(g).getCodename());
        			activitydataObject.put("color", activitydatalist.get(g).getColor());
        			activitydataObject.put("deleteflag", activitydatalist.get(g).getDeleteflag());
        			activitydataObject.put("titlegroup", activitydatalist.get(g).getTitlegroup());
        			activitydataObject.put("titleflag", activitydatalist.get(g).getTitleflag());
        			activitydataObject.put("sort", activitydatalist.get(g).getSort());
        					
        			
        			if(activitydatalist.get(g).getTitleflag().trim().equals("0")){
        				ActivitydataJsonArray.put(activitydataObject);
                		
        			}else{
        				TitleflagJsonArray.put(activitydataObject);
        			}
        			
        	  	}

    		}
        		jsonObject.put("TitleFlag", TitleflagJsonArray);
        		jsonObject.put("activitydata", ActivitydataJsonArray);
        		jsonObject.put("activitygroups", ActivitygroupsJsonArray);
    		}


		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
