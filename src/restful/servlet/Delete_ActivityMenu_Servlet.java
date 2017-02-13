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

import com.Info360.bean.Activitymenu;
import com.Info360.bean.Agentstatus;
import com.Info360.bean.CommonLink;
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/Delete_ActivityMenu")
public class Delete_ActivityMenu_Servlet {
	

	@POST
	@Produces("application/json")
	public Response PostFromPath(

			@FormParam("menuname") 	String menuname
		
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		Activitymenu activitymenu = new Activitymenu();
		
		

		activitymenu.setMenuname(menuname);

	
		
		MaintainService maintainservice = new MaintainService();		
		int Delete = maintainservice.Delete_activitymenu(activitymenu);
	    
    		jsonObject.put("activitymenu", Delete);
  
  	  
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
