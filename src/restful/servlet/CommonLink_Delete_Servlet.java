package restful.servlet;

import java.io.IOException;



import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import util.Util;

import com.Info360.bean.CommonLink;
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/Delete_commonlink")
public class CommonLink_Delete_Servlet {
	

	@POST
	@Produces("application/json")
	public Response PostFromPath(
		
			@FormParam("nodeid") int nodeid,
			@FormParam("children_list") String children_list
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		
		CommonLink commonlink = new CommonLink();
		
		List<Integer> children_list2 = new ArrayList<Integer>();

		if(children_list.length()>0){
				String [] dd = children_list.split(",");
				for(int i=0 ;i<dd.length;i++){
					Util.getConsoleLogger().debug(dd[i]);			
					children_list2.add(Integer.valueOf(dd[i]));
				}
				commonlink.setChildren_list(children_list2);
			}
		
		
		commonlink.setNodeid(nodeid);


		MaintainService maintainservice = new MaintainService();		
	  
  	  //JSONArray TreeJsonArray = new JSONArray();
  	  
  	  int serviceentry = maintainservice.Delete_commonlink(commonlink);
 
  	  jsonObject.put("Tree", serviceentry);
  
    	
  	  
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
