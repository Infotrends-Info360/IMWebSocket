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

import com.Info360.bean.CommonLink;
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/Query_commonlink")
public class CommonLink_Query_Servlet {
	

	@POST
	@Produces("application/json")
	public Response PostFromPath(
		
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		CommonLink commonlink = new CommonLink();
		
		MaintainService maintainservice = new MaintainService();		
		List<CommonLink> commonlinklist = maintainservice.Select_commonlink(commonlink);
		for(int a = 0; a < commonlinklist.size(); a++){
			jsonObject.accumulate(String.valueOf(commonlinklist.get(a).getParnetid()), String.valueOf(commonlinklist.get(a).getNodeid()));
		}
		/*
		JSONArray jsonarray = parentjsonObject.getJSONArray("0");
		//System.out.println(jsonarray);
		
		JSONObject jsonObject = new JSONObject();
		JSONObject jsonObject1 = new JSONObject();
		for(int a = 0; a < jsonarray.length(); a++){
			if(parentjsonObject.has(jsonarray.getString(a))){
				JSONObject jsonObject2 = new JSONObject();
				String jsonarray2_String = parentjsonObject.get(jsonarray.getString(a)).toString();
				
				if(jsonarray2_String.indexOf("[") > -1){
					JSONArray jsonarray2 = new JSONArray(jsonarray2_String);
					//System.out.println(jsonarray2);
					for(int b = 0; b < jsonarray2.length(); b++){
						if(parentjsonObject.has(jsonarray2.getString(b))){
							JSONObject jsonObject3 = new JSONObject();
							String jsonarray3_String = parentjsonObject.get(jsonarray2.getString(b)).toString();
							if(jsonarray3_String.indexOf("[") > -1){
								JSONArray jsonarray3 = new JSONArray(jsonarray2_String);
								for(int c = 0; c < jsonarray3.length(); c++){
									if(parentjsonObject.has(jsonarray3.getString(c))){
										JSONObject jsonObject4 = new JSONObject();
										String jsonarray4_String = parentjsonObject.get(jsonarray3.getString(c)).toString();
										if(jsonarray4_String.indexOf("[") > -1){
											JSONArray jsonarray4 = new JSONArray(jsonarray3_String);
											for(int d = 0; d < jsonarray4.length(); d++){
												if(parentjsonObject.has(jsonarray4.getString(d))){
													JSONObject jsonObject5 = new JSONObject();
													String jsonarray5_String = parentjsonObject.get(jsonarray4.getString(d)).toString();
													jsonObject5.put(jsonarray4.getString(d), jsonarray5_String);
													System.out.println(jsonObject5);
												}else{
													jsonObject4.put(jsonarray3.getString(c), jsonarray4_String);
													System.out.println(jsonObject4);
												}
											}
										}else{
											
										}
										jsonObject4.put(jsonarray3.getString(c), jsonarray4_String);
										//System.out.println(jsonObject4);
									}else{
										
									}
									
									
								}
								
							}else{
								jsonObject3.put(jsonarray2.getString(b), jsonarray3_String);
							}
							//System.out.println(jsonObject3);
							
						}else{
							jsonObject2.put(jsonarray.getString(a), jsonarray2.getString(b));
						}
						
					}
				}else{
					jsonObject1.put(jsonarray.getString(a), jsonarray2_String);
				}
			}
			
			jsonObject.put("root", jsonObject1);
		}
		*/
		
	    /*
  	  JSONArray TreeJsonArray = new JSONArray();  	  
  	
    	for(int a = 0; a < commonlinklist.size(); a++){

	    	JSONObject TreeJsonObject = new JSONObject();
	    
	    	if(commonlinklist.get(a).getParnetid()==0){
	    	
	    	    	for(int b = 0; b < TreeJsonArray.length(); b++){
	    	    		
	    	    		JSONObject TreeJsonObject2 = TreeJsonArray.getJSONObject(b);
	    	    		
	    		     	if(commonlinklist.get(a).getParnetid() == TreeJsonObject2.getInt("id")){
	    		     		
	    		    	  	JSONArray Tree2JsonArray = new JSONArray();

	    			    	JSONObject Tree2JsonObject = new JSONObject();
	    			    	
	    		    	  	for(int c = 0; c < TreeJsonArray.length(); c++){

	    		    	  		JSONObject TreeJsonObject3 = TreeJsonArray.getJSONObject(b);
	    		    	  		
	    	    		    	
	    	    		     	if(commonlinklist.get(b).getParnetid() == TreeJsonObject3.getInt("id")){
	    	    		     		
	    	    		    	  	JSONArray Tree3JsonArray = new JSONArray();
	    	    			    	JSONObject Tree3JsonObject = new JSONObject();

	    	    		    	  	for(int d = 0; d < TreeJsonArray.length(); d++){

	    	    		    	  		JSONObject TreeJsonObject4 = TreeJsonArray.getJSONObject(b);
	    	    		    	  		
	    	    	    		    	
	    	    	    		     	if(commonlinklist.get(c).getParnetid() == TreeJsonObject4.getInt("id")){
	    	    	    		    	  
	    	    	    		     		JSONArray Tree4JsonArray = new JSONArray();
	    	    	    			    	JSONObject Tree4JsonObject = new JSONObject();

	    	    	    			    	Tree4JsonObject.put("id", commonlinklist.get(d).getNodeid());
	    	    	    			    	Tree4JsonObject.put("text", commonlinklist.get(d).getNodetext());
	    	    	    			    	Tree4JsonObject.put("createuser", commonlinklist.get(d).getCreateuserid());
	    	    	    			    	Tree4JsonObject.put("chird", Tree4JsonArray);

	    	    	    			    	Tree3JsonArray.put(Tree4JsonObject);
	    	    	    		    	}
	    	    	    	    	}
	    	    		    	  	Tree3JsonObject.put("id", commonlinklist.get(c).getNodeid());
	    	    			    	Tree3JsonObject.put("text", commonlinklist.get(c).getNodetext());
	    	    			    	Tree3JsonObject.put("createuser", commonlinklist.get(c).getCreateuserid());
	    	    			    	Tree3JsonObject.put("chird", Tree3JsonArray);
	    	    			    	Tree2JsonArray.put(Tree3JsonObject);
	    	    		    	}
	    	    	    	}
	    		    	  	Tree2JsonObject.put("id", commonlinklist.get(b).getNodeid());
	    			    	Tree2JsonObject.put("text", commonlinklist.get(b).getNodetext());
	    			    	Tree2JsonObject.put("createuser", commonlinklist.get(b).getCreateuserid());
	    			    	Tree2JsonObject.put("chird", Tree2JsonArray);
	    			    	TreeJsonArray.put(Tree2JsonObject);
	    		    	}
	    	    	}
	    	}
	    	TreeJsonObject.put("id", commonlinklist.get(a).getNodeid());
	    	TreeJsonObject.put("text", commonlinklist.get(a).getNodetext());
	    	TreeJsonObject.put("parent", commonlinklist.get(a).getParnetid());
	    	TreeJsonObject.put("createuser", commonlinklist.get(a).getCreateuserid());
	    	TreeJsonObject.put("chird", TreeJsonArray);
	    	jsonObject.put("Tree", TreeJsonObject);
	    	
  	}
    	*/
	
    	
    	
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
