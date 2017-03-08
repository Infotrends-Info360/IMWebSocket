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

import com.Info360.bean.Activitymenu;
import com.Info360.bean.Cfg_AgentReason;
import com.Info360.bean.CommonLink;
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/AMenu_Sort")
public class AMenu_Sort_Servlet {
	
	@POST
	@Produces("application/json")
	public Response PostFromPath(
	
			@FormParam("dbid") int dbid
		
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		Activitymenu activitymenu = new Activitymenu();
		
		List<Integer> ActivityMenu_DBID_list = new ArrayList<Integer>();
		List<String> ActivityMenu_SORT_list = new ArrayList<String>();
	
		activitymenu.setDbid(dbid);
		
		MaintainService maintainservice = new MaintainService();	
			List<Activitymenu> activitymenulist = maintainservice.Query_AMenu_DBID(activitymenu);
		
				ActivityMenu_DBID_list.add(Integer.valueOf(activitymenulist.get(0).getDbid()));
				

				int a = Integer.valueOf(activitymenulist.get(0).getSort())+1;
		
				String aa = String.valueOf(a);
	
				
			activitymenu.setSort(aa);
		List<Activitymenu> activitymenulist2 = maintainservice.Query_AMenu_Sort(activitymenu);
		
		ActivityMenu_DBID_list.add(Integer.valueOf(activitymenulist2.get(0).getDbid()));
		
		ActivityMenu_SORT_list.add(activitymenulist2.get(0).getSort());
		ActivityMenu_SORT_list.add(activitymenulist.get(0).getSort());

		
		System.out.println("dbid: "+ ActivityMenu_DBID_list);
		System.out.println("sort: "+ ActivityMenu_SORT_list);
		
		 activitymenu.setDbid(ActivityMenu_DBID_list.get(0));
		 activitymenu.setSort(ActivityMenu_SORT_list.get(0));
		
		int update = maintainservice.AMenu_Sort(activitymenu);
		
		 activitymenu.setDbid(ActivityMenu_DBID_list.get(1));
		 activitymenu.setSort(ActivityMenu_SORT_list.get(1));
		
		int update2 = maintainservice.AMenu_Sort(activitymenu);
    
	    	jsonObject.put("AMenu_Sort", update);
	    	jsonObject.put("AMenu_Sort", update2);
	    	
	    	
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
