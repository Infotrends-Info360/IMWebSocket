package restful.servlet;

import java.io.IOException;



import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.Info360.bean.Activitygroups;
import com.Info360.bean.Activitymenu;
import com.Info360.bean.Agentstatus;
import com.Info360.bean.CommonLink;
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/Update_ActivityGroup")
public class Update_ActivityGroup_Servlet {
	

	@POST
	@Produces("application/json")
	public Response PostFromPath(
			@FormParam("activitymenuid") int activitymenuid,
			@FormParam("groupname") 	 String groupname,
			@FormParam("deletedatetime") String deletedatetime,
			@FormParam("dbid") 			 int dbid,
			@FormParam("sort") 			 String sort
		
			
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		Activitygroups activitygroups = new Activitygroups();
		
		activitygroups.setDbid(dbid);
		activitygroups.setActivitymenuid(activitymenuid);
		activitygroups.setDeletedatetime(deletedatetime);
		activitygroups.setGroupname(groupname);
		activitygroups.setSort(sort);
		
	
		MaintainService maintainservice = new MaintainService();		
		int Update = maintainservice.Update_activitygroups(activitygroups);
	    
    		jsonObject.put("activitygroups", Update);
  
  	  
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
