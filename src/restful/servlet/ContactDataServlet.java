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

import util.Util;

import com.Info360.bean.ContactData;
import com.Info360.service.MaintainService;
import com.Info360.util.IsError;
import com.Info360.util.Variable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
		Util.getFileLogger().info("ContactDataServlet userdata: " + userdata);
		Util.getConsoleLogger().info("ContactDataServlet userdata: " + userdata);
		JsonObject userdatajsonobject= Util.getGJsonObject(userdata);
		
		JSONObject jsonObject = new JSONObject();
		ContactData contactdata = new ContactData();
		contactdata.setSearchkey(searchkey);
		contactdata.setSearchvalue( Util.getGString(userdatajsonobject, searchkey));
		contactdata.setPkey(pkey);
		contactdata.setPvalue( Util.getGString(userdatajsonobject, pkey));
		jsonObject.put("status", Variable.POST_STATUS);
		
		try{
			// 先透過 searchkey, searchkeyValue 以及 pkey, pkeyValue去DB的ContactData table找有沒有對到的資料(根據sql指令)
			// 如果有找到,則會回傳contactID。沒有則回傳null
			MaintainService maintainService = new MaintainService();
			String contactID = maintainService.select_ContactData(contactdata);
			Util.getConsoleLogger().debug("ContactDataServlet contactID: " + contactID);

			// 將傳入的userdata以map型態放入到contactdata, mybatis於新增時會到裡面取值來用(不論為新增/修改,皆須進行此動作)
			Map<String,String> userdatamap = new HashMap<String,String>();
			Set<Map.Entry<String,JsonElement>> entrySet = userdatajsonobject.entrySet();
			synchronized (entrySet) {
				for (Map.Entry<String,JsonElement> entry : entrySet) {
					userdatamap.put(entry.getKey(), entry.getValue().getAsString());
					//jsonObject.put(key, value);
				}
			}
			contactdata.setUserdata(userdatamap);
			
			// 若找不到contactID,則新增一筆進入DB
			if(contactID == null || "".equals(contactID)){
				// 新增contactID
				contactID = java.util.UUID.randomUUID().toString();
				contactdata.setContactid(contactID);
				// 修改日期
				contactdata.setCreatedate(date);
				// 開始進行新增
				int insertcontactdataInt = maintainService.insert_ContactData(contactdata);
				Util.getConsoleLogger().info("***** ContactDataServlet insertcontactdataInt: " + insertcontactdataInt);
				Util.getFileLogger().info("***** ContactDataServlet insertcontactdataInt: " + insertcontactdataInt);
				// 放入回傳JsonObject
				jsonObject.put("contactID", contactID);
				jsonObject.put("contactIDflag", 0);
				jsonObject.put("insertcontactdatacount", insertcontactdataInt);
			// 若已經有contactID,則進行修改DB	
			}else{
				// 放入已有在DB的contactID
				contactdata.setContactid(contactID);
				// 修改日期
				contactdata.setModifieddate(date);
				// 開始進行更新
				int updatecontactdataInt = maintainService.update_ContactData(contactdata);
				Util.getFileLogger().info("***** ContactDataServlet updatecontactdataInt: " + updatecontactdataInt);
				// 放入回傳JsonObject
				jsonObject.put("contactID", contactID);
				jsonObject.put("contactIDflag", 1);
				jsonObject.put("updatecontactdatacount", updatecontactdataInt);
			}
			
	        	
		} catch (Exception e) {
			e.printStackTrace();
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
