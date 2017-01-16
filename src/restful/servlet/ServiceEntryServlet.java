package restful.servlet;

import java.io.IOException;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.Info360.bean.ServiceEntry;
import com.Info360.service.MaintainService;
import com.Info360.util.IsError;
import com.Info360.util.Variable;

/**
 * RESTful ServiceEntry
 * @author Lin
 */

@Path("/ServiceEntry")
public class ServiceEntryServlet {
	
	@POST
	@Produces("application/json")
	public Response PostFromPath(@FormParam("userid")String userid,
			@FormParam("username")String username,
			@FormParam("entertime")String entertime,
			@FormParam("ipaddress")String ipaddress,
			@FormParam("browser")String browser,
			@FormParam("platfrom")String platfrom,
			@FormParam("channel")String channel,
			@FormParam("language")String language,
			@FormParam("enterkey")String enterkey
			) throws IOException {
			
		JSONObject jsonObject = new JSONObject();
		ServiceEntry serviceentry = new ServiceEntry();
		serviceentry.setUserid(userid);
		serviceentry.setUsername(username);
		serviceentry.setEntertime(entertime);
		serviceentry.setIpaddress(ipaddress);
		serviceentry.setBrowserversion(browser);
		serviceentry.setPlatfrom(platfrom);
		serviceentry.setChanneltype(channel);
		serviceentry.setLanguage(language);
		serviceentry.setEnterkey(enterkey);
		//serviceentry.setContactid("123");
		jsonObject.put("status", Variable.POST_STATUS);
		
		try{
			MaintainService maintainService = new MaintainService();
	        int serviceentryInt = maintainService.insert_ServiceEntry(serviceentry);
	        jsonObject.put("insertcount", serviceentryInt);
	        	
		} catch (Exception e) {
			if(IsError.GET_EXCEPTION != null)
				jsonObject.put("error", IsError.GET_EXCEPTION);
			else
				jsonObject.put("error", e.getMessage());
		}
		
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
