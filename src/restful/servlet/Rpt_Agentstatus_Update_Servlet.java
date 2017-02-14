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

import com.Info360.bean.Cfg_AgentReason;
import com.Info360.bean.CommonLink;
import com.Info360.bean.Rpt_AgentStatus;
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/Update_rpt_agentstatus")
public class Rpt_Agentstatus_Update_Servlet {
	

	/**
	 * @return
	 * @throws IOException
	 */
	@POST
	@Produces("application/json")
	public Response PostFromPath(
			//@FormParam("duration") 	String duration,
			@FormParam("dbid") int dbid
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		Rpt_AgentStatus agentstatus = new Rpt_AgentStatus();
		
		agentstatus.setDbid(dbid);
		//agentstatus.setDuration(duration);
		
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.sss");
		Date date = new Date();
		String strDate = sdFormat.format(date);
		agentstatus.setEnddatetime(strDate);
		 
		MaintainService maintainservice = new MaintainService();		
		int update = maintainservice.Update_rpt_agentstatus(agentstatus);
	    
  	  JSONArray AgentstatusJsonArray = new JSONArray();
  	 
	    	JSONObject AgentstatusObject = new JSONObject();
	    
	    		AgentstatusJsonArray.put(AgentstatusObject);
  		 
  	
    		jsonObject.put("count", update);
  
  	  
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
