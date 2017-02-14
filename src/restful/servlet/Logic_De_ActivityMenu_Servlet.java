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
import com.Info360.bean.Cfg_AgentReason;
import com.Info360.bean.CommonLink;
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/LogicDelete_activitymenu")
public class Logic_De_ActivityMenu_Servlet {
	
	@POST
	@Produces("application/json")
	public Response PostFromPath(
	
			@FormParam("menuname") String menuname,
			@FormParam("deletedatetime") String deletedatetime,
			@FormParam("deleteflag") String deleteflag
			
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		Activitymenu activitymenu = new Activitymenu();
		
		activitymenu.setMenuname(menuname);
		activitymenu.setDeleteflag(deleteflag);
		
		
		if(activitymenu.getDeleteflag().equals("1")){
			
			SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.sss");
			Date date = new Date();
			String strDate = sdFormat.format(date);
			activitymenu.setDeletedatetime(strDate);
			
			}else{
			
				activitymenu.setDeletedatetime(null);
			}
		MaintainService maintainservice = new MaintainService();		
		int update = maintainservice.LogicDelete_activitymenu(activitymenu);
	    
  	  JSONArray activitymenuJsonArray = new JSONArray();
  	 
	    	JSONObject activitymenuObject = new JSONObject();
	    
	    	activitymenuJsonArray.put(activitymenuObject);
  		
    		jsonObject.put("activitymenu", update);
  
  	  
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
