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
import com.Info360.bean.Interaction;
import com.Info360.bean.Rpt_Activitylog;
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/Query_Allperson")
public class Query_Allperson_Servlet {
	

	
	@POST
	@Produces("application/json")
	public Response PostFromPath(
			
			@FormParam("state") int state
//			@FormParam("dbid") int dbid

			
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		CFG_person cfg_person = new CFG_person();
		
		List<String> personname = new ArrayList<String>();
		List<Integer> persondbid = new ArrayList<Integer>();

		JSONArray PersonJsonArray = new JSONArray();
		MaintainService maintainservice = new MaintainService();		

		cfg_person.setState(state);
		List<CFG_person> personlist = maintainservice.Query_PersonInfo_STATE(cfg_person);
	    	for(int a = 0; a < personlist.size(); a++){
				JSONObject cfg_personObject = new JSONObject();

	    		personname.add(personlist.get(a).getAccount());
	    		persondbid.add(personlist.get(a).getDbid());
				cfg_personObject.put("username", personlist.get(a).getAccount());
				cfg_personObject.put("dbid", personlist.get(a).getDbid());

				PersonJsonArray.put(cfg_personObject);
	    	}
		
//	    	for(int b = 0; b<persondbid.size();b++){
//	    		List<CFG_person> personlist = maintainservice.Query_PersonInfo_STATE(cfg_person);
//
//	    	}

  	    jsonObject.put("allperson", PersonJsonArray);

  	  
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
