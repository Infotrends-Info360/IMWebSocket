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

import com.Info360.bean.Agentstatus;
import com.Info360.bean.CommonLink;
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/Update_agentstatus")
public class Agentstatus_Update_Servlet {
	

	/**
	 * @param statusname
	 * @param statusname_cn
	 * @param statusname_en
	 * @param statusname_tw
	 * @param description
	 * @param alarmduration
	 * @param alarmcolor
	 * @param flag
	 * @param dbid
	 * @return
	 * @throws IOException
	 */
	@POST
	@Produces("application/json")
	public Response PostFromPath(
			@FormParam("statusname") 	String statusname,
			@FormParam("statusname_cn") String statusname_cn,
			@FormParam("statusname_en") String statusname_en,
			@FormParam("statusname_tw") String statusname_tw,
			@FormParam("description") 	String description,
			@FormParam("alarmduration") String alarmduration,
			@FormParam("alarmcolor") 	String alarmcolor,
			@FormParam("flag") int flag,
			@FormParam("dbid") int dbid
			
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		Agentstatus agentstatus = new Agentstatus();
		
		agentstatus.setFlag(flag);
		agentstatus.setDbid(dbid);
		
		agentstatus.setAlarmcolor(alarmcolor);
		agentstatus.setAlarmduration(alarmduration);
		agentstatus.setDescription(description);
		agentstatus.setStatusname(statusname);
		agentstatus.setStatusname_cn(statusname_cn);
		agentstatus.setStatusname_en(statusname_en);
		agentstatus.setStatusname_tw(statusname_tw);
		 
		 
		MaintainService maintainservice = new MaintainService();		
		int update = maintainservice.Update_agentstatus(agentstatus);
	    
  	  JSONArray AgentstatusJsonArray = new JSONArray();
  	 
	    	JSONObject AgentstatusObject = new JSONObject();
	    
	    		AgentstatusJsonArray.put(AgentstatusObject);
  		 
  	
    		jsonObject.put("agentstatus", update);
  
  	  
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
