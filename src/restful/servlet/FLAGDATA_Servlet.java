package restful.servlet;

import java.io.IOException;



import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.Info360.bean.Activitydata;
import com.Info360.bean.Activitygroups;
import com.Info360.bean.Activitymenu;
import com.Info360.bean.Cfg_AgentReason;
import com.Info360.bean.CommonLink;
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/FLAGDATA")
public class FLAGDATA_Servlet {
	

	@POST
	@Produces("application/json")
	public Response PostFromPath(
			@FormParam("dbid") int dbid
			
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		Activitygroups activitygroups = new Activitygroups();
		
		activitygroups.setDbid(dbid);
	
		MaintainService maintainservice = new MaintainService();	
		
    			JSONArray ActivitygroupsJsonArray = new JSONArray();
    			
        		Activitydata activitydata = new Activitydata();
        		JSONArray ActivitydataJsonArray = new JSONArray();
        		JSONArray flagJsonArray = new JSONArray();
        		
        		JSONArray flag0JsonArray = new JSONArray();
        		JSONArray flag1JsonArray = new JSONArray();

        		activitygroups.setActivitymenuid(dbid);
        		List<Activitygroups> activitygroupslist = maintainservice.Select_activitygroups(activitygroups);
        
        		for(int a = 0; a < activitygroupslist.size(); a++){
    			
        	  	  	JSONObject activitygroupsObject = new JSONObject();
        	  	activitygroupsObject.put("dbid", activitygroupslist.get(a).getDbid());
        	  	activitygroupsObject.put("createdatetime", activitygroupslist.get(a).getCreatedatetime());
        	  	activitygroupsObject.put("deletedatetime", activitygroupslist.get(a).getDeletedatetime());
        	  	activitygroupsObject.put("activitymenuid", activitygroupslist.get(a).getActivitymenuid());
        	  	activitygroupsObject.put("groupname", activitygroupslist.get(a).getGroupname());
        	  	activitygroupsObject.put("sort", activitygroupslist.get(a).getSort());
        		
        	  	
        	  	ActivitygroupsJsonArray.put(activitygroupsObject);
        	  	
        	 // 	Util.getConsoleLogger().debug("GroupDbid: "+activitygroupslist.get(a).getDbid());
        	  	
        	  	if(dbid!=0){	
        	  	activitydata.setActivitygroupsid(activitygroupslist.get(a).getDbid());
    	  		List<Activitydata> activitydatalist = maintainservice.Select_activitydata(activitydata);
    	  		
        	  	for(int g = 0; g < activitydatalist.size(); g++){
        	  		
        	  		JSONObject activitydataObject = new JSONObject();
        			activitydataObject.put("dbid", activitydatalist.get(g).getDbid());
        			activitydataObject.put("createdatetime", activitydatalist.get(a).getCreatedatetime());
        			activitydataObject.put("deletedatetime", activitydatalist.get(a).getDeletedatetime());
        			activitydataObject.put("activitygroupsid", activitydatalist.get(g).getActivitygroupsid());
        			activitydataObject.put("codename", activitydatalist.get(g).getCodename());
        			activitydataObject.put("color", activitydatalist.get(g).getColor());
        			activitydataObject.put("deleteflag", activitydatalist.get(g).getDeleteflag());
        			activitydataObject.put("titlegroup", activitydatalist.get(g).getTitlegroup());
        			activitydataObject.put("titleflag", activitydatalist.get(g).getTitleflag());
        			activitydataObject.put("sort", activitydatalist.get(g).getSort());
        					
        			
        			if(activitydatalist.get(g).getTitlegroup()==0){
        				ActivitydataJsonArray.put(activitydataObject);
                		
        			}else{
        				flagJsonArray.put(activitydataObject);
        			}
        			
        	  	}
        	  	
        	  	
        	  	for(int g = 0; g < activitydatalist.size(); g++){
        	  		
        	  		JSONObject activitydataObject = new JSONObject();
        			activitydataObject.put("dbid", activitydatalist.get(g).getDbid());
        			activitydataObject.put("activitygroupsid", activitydatalist.get(g).getActivitygroupsid());
        			activitydataObject.put("codename", activitydatalist.get(g).getCodename());
        			activitydataObject.put("color", activitydatalist.get(g).getColor());
        			activitydataObject.put("deleteflag", activitydatalist.get(g).getDeleteflag());
        			activitydataObject.put("titlegroup", activitydatalist.get(g).getTitlegroup());
        			activitydataObject.put("titleflag", activitydatalist.get(g).getTitleflag());
        			activitydataObject.put("sort", activitydatalist.get(g).getSort());
        			
        			
        			if(activitydatalist.get(g).getDeleteflag().equals("0")){
        				
        				if(activitydatalist.get(g).getCreatedatetime()!=null || activitydatalist.get(g).getCreatedatetime()!= ""){
        					activitydataObject.put("createdatetime", activitydatalist.get(g).getCreatedatetime());
            				flag0JsonArray.put(activitydataObject);
        				}else if(activitydatalist.get(g).getCreatedatetime()==null && activitydatalist.get(g).getCreatedatetime()!= ""){
        					activitydataObject.put("createdatetime", "");

            				flag0JsonArray.put(activitydataObject);
        				}
            			
                		
        			}else{
        				if(activitydatalist.get(g).getDeletedatetime()!=null || activitydatalist.get(g).getDeletedatetime()!= ""){
        					activitydataObject.put("deletedatetime", activitydatalist.get(g).getDeletedatetime());
            				flag1JsonArray.put(activitydataObject);
        				}else{
        					activitydataObject.put("deletedatetime","");
            				flag1JsonArray.put(activitydataObject);
        				}
        
        			}
        			
        	  	}
        	  	

    		}
        	  	jsonObject.put("Flag0", flag0JsonArray);
        	  	jsonObject.put("Flag1", flag1JsonArray);
        		jsonObject.put("Flag", flagJsonArray);
        		jsonObject.put("activitydata", ActivitydataJsonArray);
        		jsonObject.put("activitygroups", ActivitygroupsJsonArray);
    		}


		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
