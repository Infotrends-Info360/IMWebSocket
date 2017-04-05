package restful.servlet;

import java.io.BufferedReader;
import java.io.IOException;



import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import util.Util;

import com.Info360.bean.Cfg_AgentReason;
import com.Info360.bean.CommonLink;
import com.Info360.bean.ContactData;
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/Query_contactdata")
public class QueryContactData_Servlet {
	
	@POST
	@Produces("application/json")
	public Response PostFromPath(
		
			@FormParam("contactid") String Contactid,
			@FormParam("inputcontactdata") String inputcontactdata

			) throws IOException {
		
		System.out.println("Contactid: " + Contactid);
		System.out.println("inputcontactdata: " + inputcontactdata);
		
		JSONObject jsonObject = new JSONObject();
		
		ContactData contactdata = new ContactData();
		MaintainService maintainservice = new MaintainService();		
		JSONArray contactdataArray = new JSONArray();
		
		
		//select全部contactdataID
		List<String> allcontactdata = new ArrayList<String>();
		List<ContactData> contactdatalist = maintainservice.Query_All_Contactdata(contactdata);
			for(int i = 0;i<contactdatalist.size();i++){
				allcontactdata.add(contactdatalist.get(i).getContactid().trim());
			}
			
				//帶出全部contactdataID的資訊
				for(int a = 0; a<allcontactdata.size(); a++){
					JSONObject contactdataObject = new JSONObject();
					String Contactkey = allcontactdata.get(a).trim();
    				System.out.println("Contactkey:  "+Contactkey);

	    			contactdata.setContactid(Contactkey);
	    			Map<String, String> contactidmap = maintainservice.Query_Contactdata(Contactkey);
	    			System.out.println(contactidmap);
	    			
//	    			contactdataObject.put("BasicINF", contactidmap);
//	    			contactdataArray.put(contactdataObject);
	    			
	    		    JSONObject datajsonObj = new JSONObject(contactidmap);
	    		    JSONObject inputjsonObj = new JSONObject(inputcontactdata);
	    		    String[] inputjsonObjkeys = JSONObject.getNames(inputjsonObj);
	    		    String[] datajsonObjkeys = JSONObject.getNames(inputjsonObj);

	    		    System.out.println("inputjsonObj: "+inputjsonObj);
//	    		    System.out.println("datajsonObj: "+datajsonObj);
	    		    
	    		    int count=0;
	    		    for(int i = 0; i<inputjsonObjkeys.length;i++){
	    		    		if(inputjsonObj.has(inputjsonObjkeys[i])&&datajsonObj.has(datajsonObjkeys[i])){
//	    		    			System.out.println(inputjsonObj.get(inputjsonObjkeys[i]));
//	    		    			System.out.println(datajsonObj.get(datajsonObjkeys[i]));

	    		    			if(inputjsonObj.get(inputjsonObjkeys[i]).equals(datajsonObj.get(inputjsonObjkeys[i]))){
	    		    				count++;
	    		    			}
		    		    	
	    		    		}
	    		    } 
	    		    
	    		    
	    		    System.out.println("count: "+count);
	    		    System.out.println("inputjsonObjkeys: "+inputjsonObjkeys.length);

	    		    if(count==inputjsonObjkeys.length){
	    		  	  contactdataObject.put("contactid", Contactkey);
				  	  contactdataArray.put(contactdataObject);
	    		  	    jsonObject.put("contactid", contactdataArray);
	    		    }

				}



				System.out.println("jsonObject.toString(): " + jsonObject.toString());
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}

	public JSONObject GetServiceNameCache(String searchtype) throws Exception {
		StringBuilder responseSB = null;
		// Encode the query
		String GetData = "typeid=" + searchtype + "&method=get" + "&key=all";

		// Connect to URL
		String hostURL = Util.getHostURLStr("ServiceNameCache");
		Util.getConsoleLogger().debug("hostURL(ServiceNameCache): " + hostURL);
		URL url = new URL( hostURL + "/ServiceNameCache/RESTful/datacache?"+ GetData);
//		URL url = new URL(
//				"http://ws.crm.com.tw:8080/ServiceNameCache/RESTful/datacache?"
//						+ GetData);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("GET");
		// connection.setRequestProperty("Content-Type",
		// "application/x-www-form-urlencoded");
		// connection.setRequestProperty("Content-Length",
		// String.valueOf(postData.length()));

		// Write data
		// OutputStream os = connection.getOutputStream();
		// os.write(postData.getBytes());

		// Read response
		responseSB = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				connection.getInputStream(), "UTF-8"));

		String line;
		while ((line = br.readLine()) != null)
			responseSB.append(line.trim());

		// Close streams
		br.close();
		// os.close();

		// Util.getConsoleLogger().debug("responseSB: "+responseSB.toString().trim());
		JSONObject ServiceNameCachejsonObj = new JSONObject(
				responseSB.toString());
		return ServiceNameCachejsonObj;
	}
	
	
}