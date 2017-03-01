package restful.servlet;

import java.io.IOException;



import java.math.BigDecimal;
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

@Path("/Select_commonlink")
public class CommonLink_Select_Servlet {
	

	@POST
	@Produces("application/json")
	public Response PostFromPath(
		
			
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		
		CommonLink commonlink = new CommonLink();
		
		
		
		MaintainService maintainservice = new MaintainService();		
		List<CommonLink> commonlinklist = maintainservice.Select_commonlink(commonlink);
	    
  	  JSONArray TreeJsonArray = new JSONArray();
  	int count = commonlinklist.size();
  
    	for(int a = 0; a < commonlinklist.size(); a++){

	    	JSONObject TreeJsonObject = new JSONObject();
	    	JSONObject hrefJsonObject = new JSONObject();
	    	JSONObject colorJsonObject = new JSONObject();
	    	
	    	TreeJsonObject.put("id", commonlinklist.get(a).getNodeid());
	    	TreeJsonObject.put("text", commonlinklist.get(a).getNodetext());
	    	TreeJsonObject.put("parent", commonlinklist.get(a).getParnetid());
	    	TreeJsonObject.put("createuser", commonlinklist.get(a).getCreateuserid());
	    	
	    	hrefJsonObject.put("href", commonlinklist.get(a).getNodeurl());
	    	
	    	colorJsonObject.put("class", commonlinklist.get(a).getColor());
	    	String Pid =  Integer.toString(commonlinklist.get(a).getParnetid());
	    	String ppp = commonlinklist.get(a).getNodeurl();
	    	
	        if(Pid.equals("0")){
	    		String b = "#";
	    		Pid = b;
	    	}
	    		
	    	TreeJsonObject.put("a_attr", hrefJsonObject );
	    	TreeJsonObject.put("parent", Pid);
	    	TreeJsonObject.put("li_attr", colorJsonObject);
	    	TreeJsonArray.put(TreeJsonObject);
  	}
    	
    		jsonObject.put("Tree", TreeJsonArray);
    		jsonObject.put("count", count);
  	  
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
