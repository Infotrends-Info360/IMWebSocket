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


import com.Info360.bean.Activitygroups;

import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/AGroup_Sortup")
public class AGroup_Sortup_Servlet {
	
	@POST
	@Produces("application/json")
	public Response PostFromPath(
	
			@FormParam("dbid") int dbid
		
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		Activitygroups activitygroups = new Activitygroups();
		
		List<Integer> ActivityGroup_DBID_list = new ArrayList<Integer>();
		List<String> ActivityGroup_SORT_list = new ArrayList<String>();
	
		activitygroups.setDbid(dbid);
		
		MaintainService maintainservice = new MaintainService();	
			List<Activitygroups> activitygroupslist = maintainservice.Query_AGroup_DBID(activitygroups);
		
			ActivityGroup_DBID_list.add(Integer.valueOf(activitygroupslist.get(0).getDbid()));
				

				int a = Integer.valueOf(activitygroupslist.get(0).getSort())-1;
		
				String aa = String.valueOf(a);
	
				
				activitygroups.setSort(aa);
		List<Activitygroups> activitygroupslist2 = maintainservice.Query_AGroup_Sort(activitygroups);
		
		ActivityGroup_DBID_list.add(Integer.valueOf(activitygroupslist2.get(0).getDbid()));
		
		ActivityGroup_SORT_list.add(activitygroupslist2.get(0).getSort());
		ActivityGroup_SORT_list.add(activitygroupslist.get(0).getSort());

		
//		System.out.println("dbid: "+ ActivityData_DBID_list);
//		System.out.println("sort: "+ ActivityData_SORT_list);

		
		activitygroups.setDbid(ActivityGroup_DBID_list.get(0));
		activitygroups.setSort(ActivityGroup_SORT_list.get(0));
		
		int update = maintainservice.AGroup_Sort(activitygroups);
		
//		System.out.println("DBIDget(1): "+ActivityData_DBID_list.get(1));
//		System.out.println("SORTget(1): "+ActivityData_SORT_list.get(1));
		activitygroups.setDbid(ActivityGroup_DBID_list.get(1));
		activitygroups.setSort(ActivityGroup_SORT_list.get(1));
		
		int update2 = maintainservice.AGroup_Sort(activitygroups);
    
	    	jsonObject.put("AGroup_Sort", update);
	    	jsonObject.put("AGroup_Sort2", update2);
	    	
	    	
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
