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

import com.Info360.bean.Activitydata;
import com.Info360.bean.Cfg_AgentReason;
import com.Info360.bean.CommonLink;
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/AData_Sortup")
public class AData_Sortup_Servlet {
	
	@POST
	@Produces("application/json")
	public Response PostFromPath(
	
			@FormParam("dbid") int dbid
		
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		Activitydata activitydata = new Activitydata();
		
		List<Integer> ActivityData_DBID_list = new ArrayList<Integer>();
		List<String> ActivityData_SORT_list = new ArrayList<String>();
	
		activitydata.setDbid(dbid);
		
		MaintainService maintainservice = new MaintainService();	
			List<Activitydata> activitydatalist = maintainservice.Query_AData_DBID(activitydata);
		
			ActivityData_DBID_list.add(Integer.valueOf(activitydatalist.get(0).getDbid()));
				

				int a = Integer.valueOf(activitydatalist.get(0).getSort())-1;
		
				String aa = String.valueOf(a);
	
				
				activitydata.setSort(aa);
		List<Activitydata> activitydatalist2 = maintainservice.Query_AData_Sort(activitydata);
		
		ActivityData_DBID_list.add(Integer.valueOf(activitydatalist2.get(0).getDbid()));
		
		ActivityData_SORT_list.add(activitydatalist2.get(0).getSort());
		ActivityData_SORT_list.add(activitydatalist.get(0).getSort());

		
//		System.out.println("dbid: "+ ActivityData_DBID_list);
//		System.out.println("sort: "+ ActivityData_SORT_list);

		
		activitydata.setDbid(ActivityData_DBID_list.get(0));
		activitydata.setSort(ActivityData_SORT_list.get(0));
		
		int update = maintainservice.AData_Sort(activitydata);
		
//		System.out.println("DBIDget(1): "+ActivityData_DBID_list.get(1));
//		System.out.println("SORTget(1): "+ActivityData_SORT_list.get(1));
		activitydata.setDbid(ActivityData_DBID_list.get(1));
		activitydata.setSort(ActivityData_SORT_list.get(1));
		
		int update2 = maintainservice.AData_Sort(activitydata);
    
	    	jsonObject.put("AData_Sort", update);
	    	jsonObject.put("AData_Sort2", update2);
	    	
	    	
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
