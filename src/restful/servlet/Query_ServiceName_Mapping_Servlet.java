package restful.servlet;

import java.io.IOException;



import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.Info360.bean.Activitydata;
import com.Info360.bean.CFG_person;
import com.Info360.bean.Cfg_ServiceName_Mapping;
import com.Info360.bean.Interaction;
import com.Info360.bean.Rpt_Activitylog;
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/Query_Service_Mapping")
public class Query_ServiceName_Mapping_Servlet {
	

	
	@POST
	@Produces("application/json")
	public Response PostFromPath(
			
			@FormParam("searchflag") String searchflag,
			@FormParam("typeid") String typeid

			
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		Cfg_ServiceName_Mapping cfg_servicename_mapping = new Cfg_ServiceName_Mapping();

		JSONArray MappingJsonArray = new JSONArray();
		MaintainService maintainservice = new MaintainService();		
		
		searchflag.trim();typeid.trim();
		cfg_servicename_mapping.setSearchflag(searchflag);
		cfg_servicename_mapping.setTypeid(typeid);
		
		if(typeid!=null&&typeid!=""&&searchflag!=null&&searchflag!=""){
			List<Cfg_ServiceName_Mapping> cfg_servicename_mappinglist = maintainservice.Query_Cfg_ServiceName_MappingInfo(cfg_servicename_mapping);
	    		for(int a = 0; a < cfg_servicename_mappinglist.size(); a++){
	    			JSONObject MappingObject = new JSONObject();

	    			MappingObject.put("typeid", cfg_servicename_mappinglist.get(a).getTypeid());
	    			MappingObject.put("searchflag", cfg_servicename_mappinglist.get(a).getSearchflag());
	    			MappingObject.put("dbid", cfg_servicename_mappinglist.get(a).getDbid());
	    			MappingObject.put("chiname", cfg_servicename_mappinglist.get(a).getChiname());
	    			MappingObject.put("engname", cfg_servicename_mappinglist.get(a).getEngname());
	    			MappingObject.put("mappingkey", cfg_servicename_mappinglist.get(a).getMappingkey());
	    			MappingObject.put("sort", cfg_servicename_mappinglist.get(a).getSort());

	    			MappingJsonArray.put(MappingObject);
	    		}

	    		jsonObject.put("mapping", MappingJsonArray);
		}
  	  
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
