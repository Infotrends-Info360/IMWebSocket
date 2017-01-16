package restful.servlet;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.Info360.bean.Cfg_ServiceName_Setting;
import com.Info360.service.MaintainService;
import com.Info360.util.IsError;
import com.Info360.util.Variable;

/**
 * RESTful Test
 * @author Lin
 */

@Path("/Cfg_ServiceName_Setting")
public class Cfg_ServiceName_SettingServlet {
	
	@POST
	@Produces("application/json")
	public Response PostFromPath(@QueryParam("typeid")
	String typeid) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		Cfg_ServiceName_Setting cfg_servicename_setting = new Cfg_ServiceName_Setting();
		cfg_servicename_setting.setTypeid(typeid);
		jsonObject.put("status", Variable.POST_STATUS);
		
		try{
			MaintainService maintainService = new MaintainService();
	        List<Cfg_ServiceName_Setting> cfg_servicename_settinglist = maintainService.select_Cfg_ServiceName_Setting(cfg_servicename_setting);
	        jsonObject.put("uniquekey", cfg_servicename_settinglist.get(0).getUniquekey());
	        jsonObject.put("searchkey", cfg_servicename_settinglist.get(0).getSearchkey());
	        	
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
