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

import com.Info360.bean.CommonLink;
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/Insert_commonlink")
public class CommonLink_Insert_Servlet {
	

	@POST
	@Produces("application/json")
	public Response PostFromPath(
			
			@FormParam("parnetid") int parnetid,
			@FormParam("nodetext") String nodetext,
			@FormParam("nodeurl") String nodeurl,
			@FormParam("createuserid") String createuserid,
			@FormParam("sort") int sort,
			@FormParam("createdatetime") String createdatetime,
			@FormParam("color") String color
			
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		
		CommonLink commonlink = new CommonLink();
		
		commonlink.setParnetid(parnetid);
		commonlink.setNodetext(nodetext);
		commonlink.setNodeurl(nodeurl);
		commonlink.setCreateuserid(createuserid);
		commonlink.setSort(0);
		commonlink.setColor(color);
		
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.sss");
		Date date = new Date();
		String strDate = sdFormat.format(date);
		//System.out.println(strDate);
		commonlink.setCreatedatetime(strDate);
		
		
		MaintainService maintainservice = new MaintainService();		
	  
  	  //JSONArray TreeJsonArray = new JSONArray();
  	  
  	  int serviceentry = maintainservice.Insert_commonlink(commonlink);
  	
  	
  	  jsonObject.put("Tree", serviceentry);
  
    	
  	  
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
