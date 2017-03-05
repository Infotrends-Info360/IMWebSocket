package restful.servlet;

import java.io.IOException;



import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.Info360.bean.Activitydata;
import com.Info360.bean.Activitymenu;
import com.Info360.bean.Cfg_AgentReason;
import com.Info360.bean.CommonLink;
import com.Info360.service.MaintainService;
 

@Path("/Delete_AgentReason")
public class AgentReason_Delete_Servlet {
	
	@POST
	@Produces("application/json")
	public Response PostFromPath(
			
			@FormParam("dbid") int dbid,
    		@FormParam("Agentreason_DBID_list") String Agentreason_DBID_list

			
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		Cfg_AgentReason agentreason = new Cfg_AgentReason();
		
		List<Integer> Agentreason_DBID_list2 = new ArrayList<Integer>();

		
		if(Agentreason_DBID_list.length()>0){
			String [] dd = Agentreason_DBID_list.split(",");
			for(int i=0 ;i<dd.length;i++){
				Agentreason_DBID_list2.add(Integer.valueOf(dd[i]));
			}
			agentreason.setAgentreason_DBID_list(Agentreason_DBID_list2);
		}
		
		agentreason.setDbid(dbid);
		
		MaintainService maintainservice = new MaintainService();		
		int Delete = maintainservice.Delete_agentreason(agentreason);
	    
    		jsonObject.put("agentreason", Delete);
  
  	  
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
