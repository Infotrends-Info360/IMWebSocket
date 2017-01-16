package restful.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.Info360.bean.ContactData;
import com.Info360.service.MaintainService;
import com.Info360.util.IsError;
import com.Info360.util.Variable;

/**
 * RESTful ServiceEntry
 * @author Lin
 */

@Path("/ContactData")
public class ContactDataServlet {
	
	@POST
	@Produces("application/json")
	public Response PostFromPath(//@FormParam("contactid")String contactid,
			@FormParam("searchkey")String searchkey,
			@FormParam("pkey")String pkey,
			@FormParam("date")String date,
			@FormParam("userdata")String userdata
			) throws IOException {
			
		JSONObject userdatajsonobject= new JSONObject(userdata);
		
		JSONObject jsonObject = new JSONObject();
		ContactData contactdata = new ContactData();
		contactdata.setSearchkey(searchkey);
		contactdata.setSearchvalue(userdatajsonobject.getString(searchkey));
		contactdata.setPkey(pkey);
		contactdata.setPvalue(userdatajsonobject.getString(pkey));
		jsonObject.put("status", Variable.POST_STATUS);
		
		try{
			MaintainService maintainService = new MaintainService();
			String contactID = maintainService.select_ContactData(contactdata);
			
			if(contactID==null||"".equals(contactID)){
				contactID = java.util.UUID.randomUUID().toString();
				jsonObject.put("contactID", contactID);
				jsonObject.put("contactIDflag", 0);
				contactdata.setCreatedate(date);
				contactdata.setContactid(contactID);
				Map<String,String> userdatamap = new HashMap<String,String>();
				//JSONObject userdatajsonobject= new JSONObject(userdata);
				Set<String> keySet = userdatajsonobject.keySet();
					synchronized (keySet) {
						for (String key : keySet) {
							String value = userdatajsonobject.getString(key);
							userdatamap.put(key, value);
							//jsonObject.put(key, value);
						}
					}
					contactdata.setUserdata(userdatamap);
				int insertcontactdataInt = maintainService.insert_ContactData(contactdata);
				jsonObject.put("insertcontactdatacount", insertcontactdataInt);
			}else{
				jsonObject.put("contactID", contactID);
				jsonObject.put("contactIDflag", 1);
				contactdata.setModifieddate(date);
				contactdata.setContactid(contactID);
				Map<String,String> userdatamap = new HashMap<String,String>();
				//JSONObject userdatajsonobject= new JSONObject(userdata);
				Set<String> keySet = userdatajsonobject.keySet();
					synchronized (keySet) {
						for (String key : keySet) {
							String value = userdatajsonobject.getString(key);
							userdatamap.put(key, value);
							//jsonObject.put(key, value);
						}
					}
					contactdata.setUserdata(userdatamap);
				int updatecontactdataInt = maintainService.update_ContactData(contactdata);
				jsonObject.put("updatecontactdatacount", updatecontactdataInt);
			}
			
	        	
		} catch (Exception e) {
			if(IsError.GET_EXCEPTION != null)
				jsonObject.put("error", IsError.GET_EXCEPTION);
			else
				jsonObject.put("error", e.getMessage());
		}
		
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
