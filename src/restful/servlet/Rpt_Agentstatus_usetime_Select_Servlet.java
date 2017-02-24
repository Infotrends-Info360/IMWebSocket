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

@Path("/Select_rpt_agentstatus_usetime")
public class Rpt_Agentstatus_usetime_Select_Servlet {
	

	/**
	 * @return
	 * @throws IOException
	 */
	@POST
	@Produces("application/json")
	public Response PostFromPath(
			@FormParam("person_dbid") 	String person_dbid,
			@FormParam("status_dbid") String status_dbid
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		Rpt_AgentStatus agentstatus = new Rpt_AgentStatus();
		
		agentstatus.setPerson_dbid(person_dbid);
		agentstatus.setStatus_dbid(status_dbid);
		 
//		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.sss");
//		Date date = new Date();
//		String strDate = sdFormat.format(date);
//		agentstatus.setStartdatetime(strDate);
		
		if(person_dbid != null && !person_dbid.equals("") && status_dbid != null && !status_dbid.equals("")){
			MaintainService maintainservice = new MaintainService();		
			int insert = maintainservice.Select_rpt_agentstatus_usetime(agentstatus);
			jsonObject.put("second", insert);
		}else{
			jsonObject.put("second", 0);
		}
		
  
  	  
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
