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

@Path("/Flag_ActivityMenu")
public class Flag_ActivityMenu_Servlet {
	

	@POST
	@Produces("application/json")
	public Response PostFromPath(
			@FormParam("deleteflag") String deleteflag
			
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		Activitymenu activitymenu = new Activitymenu();
		
		activitymenu.setDeleteflag(deleteflag);

		MaintainService maintainservice = new MaintainService();	
		
		JSONArray ActivitymenuJsonArray = new JSONArray();
  	 
		List<Activitymenu> activitymenulist = maintainservice.Flag_activitymenu(activitymenu);
	    
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
  	  	
    	

		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
