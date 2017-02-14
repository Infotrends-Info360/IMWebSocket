package restful.servlet;

import java.io.IOException;


import java.text.SimpleDateFormat;
import java.util.Date;

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


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/LogicDelete_ActivityData")
public class Logic_De_ActivityData_Servlet {
	
	@POST
	@Produces("application/json")
	public Response PostFromPath(
	
			@FormParam("codename") String codename,
			@FormParam("deleteflag") String deleteflag,
			@FormParam("deletedatetime") String deletedatetime
			
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		Activitydata activitydata = new Activitydata();
		
		deleteflag.trim();
		activitydata.setCodename(codename);
		activitydata.setDeleteflag(deleteflag);
		
		
		if(activitydata.getDeleteflag().equals("1")){
		
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.sss");
		Date date = new Date();
		String strDate = sdFormat.format(date);
		activitydata.setDeletedatetime(strDate);
		
		}else{
		
			activitydata.setDeletedatetime(null);
		}
		
		MaintainService maintainservice = new MaintainService();		
		int update = maintainservice.LogicDelete_ActivityData(activitydata);
	    
  	  JSONArray activitydataJsonArray = new JSONArray();
  	 
	    	JSONObject activitydataObject = new JSONObject();
	    
	    	activitydataJsonArray.put(activitydataObject);
	    	jsonObject.put("activitydata", update);
		
  		
    		
  
  	  
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
