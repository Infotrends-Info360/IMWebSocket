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

import com.Info360.bean.Activitydata;
import com.Info360.bean.Activitymenu;
import com.Info360.bean.Cfg_AgentReason;
import com.Info360.bean.CommonLink;
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/Insert_ActivityData")
public class Insert_ActivityData_Servlet {
	

	@POST
	@Produces("application/json")
	public Response PostFromPath(
			@FormParam("activitygroupsid") 	int activitygroupsid,
			@FormParam("codename") 			String codename,
			@FormParam("color") 			String color,
			@FormParam("titleflag") 		int titleflag,
			@FormParam("titlegroup") 		int titlegroup,
			@FormParam("deleteflag") 		String deleteflag,
			@FormParam("createdatetime") 	String createdatetime,
			@FormParam("sort") 				String sort
			
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		Activitydata activitydata = new Activitydata();
		
		activitydata.setActivitygroupsid(activitygroupsid);
		activitydata.setCodename(codename);
		activitydata.setColor(color);
		activitydata.setTitleflag(titleflag);
		activitydata.setDeleteflag(deleteflag);
		activitydata.setSort(sort);
		activitydata.setTitlegroup(titlegroup);
		
		 
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.sss");
		Date date = new Date();
		String strDate = sdFormat.format(date);
		//Util.getConsoleLogger().debug(strDate);
		activitydata.setCreatedatetime(strDate);
		
		
		MaintainService maintainservice = new MaintainService();		
		int insert = maintainservice.Insert_activitydata(activitydata);
	    
    		jsonObject.put("activitydata", insert);
  
  	  
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
