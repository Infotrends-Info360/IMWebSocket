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
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/Insert_agentreason")
public class AgentReason_Insert_Servlet {
	

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
			@FormParam("createdatetime") String createdatetime,
			@FormParam("createuserid") String createuserid
			
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		Cfg_AgentReason agentreason = new Cfg_AgentReason();
		
		agentreason.setCreateuserid(createuserid);
		agentreason.setAlarmcolor(alarmcolor);
		agentreason.setAlarmduration(alarmduration);
		agentreason.setDescription(description);
		agentreason.setStatusname(statusname);
		agentreason.setStatusname_cn(statusname_cn);
		agentreason.setStatusname_en(statusname_en);
		agentreason.setStatusname_tw(statusname_tw);
		agentreason.setFlag(flag);
		 
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.sss");
		Date date = new Date();
		String strDate = sdFormat.format(date);
		//System.out.println(strDate);
		agentreason.setCreatedatetime(strDate);
		
		MaintainService maintainservice = new MaintainService();		
		int insert = maintainservice.Insert_agentreason(agentreason);
	    
    		jsonObject.put("agentreason", insert);
  
  	  
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
