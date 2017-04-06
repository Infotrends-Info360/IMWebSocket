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
import java.util.Set;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import util.Util;

import com.Info360.bean.Activitydata;
import com.Info360.bean.CFG_person;
import com.Info360.bean.ContactData;
import com.Info360.bean.Interaction;
import com.Info360.bean.Rpt_Activitylog;
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/Query")
public class Query_Servlet {
	
	@POST
	@Produces("application/json")
	public Response PostFromPath(
			
			@FormParam("startdate") String startdate,
			@FormParam("enddate") String enddate,
			@FormParam("agentid") String agentid,
			@FormParam("contactid") String contactid,
			@FormParam("inputcontactdata") String inputcontactdata

			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		
		Interaction interaction = new Interaction();
		Rpt_Activitylog rpt_activitylog = new Rpt_Activitylog();
		Activitydata activitydata = new Activitydata();
		CFG_person cfg_person = new CFG_person();
		ContactData contactdata = new ContactData();

		interaction.setStartdate(startdate);
		interaction.setEnddate(enddate);
		
		List<String> contactidlist = new ArrayList<String>();
		MaintainService maintainservice = new MaintainService();
//		System.out.println("start");

			if(inputcontactdata!=null && !inputcontactdata.isEmpty()){
				System.out.println("inputcontactdata if");
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

						contactdata.setContactid(Contactkey);
						Map<String, String> contactidmap = maintainservice.Query_Contactdata(Contactkey);
//    			System.out.println(contactidmap);
						JSONObject datajsonObj = new JSONObject(contactidmap);
						JSONObject inputjsonObj = new JSONObject(inputcontactdata);
						String[] inputjsonObjkeys = JSONObject.getNames(inputjsonObj);
						String[] datajsonObjkeys = JSONObject.getNames(inputjsonObj);

//    		    System.out.println("inputjsonObj: "+inputjsonObj);   
							int count=0;
							for(int i = 0; i<inputjsonObjkeys.length;i++){
								if(inputjsonObj.has(inputjsonObjkeys[i])&&datajsonObj.has(datajsonObjkeys[i])){
//    		    			if(inputjsonObj.get(inputjsonObjkeys[i]).equals(datajsonObj.get(inputjsonObjkeys[i]))){
//    		    				count++;
//    		    			}
									int x =0;
									x = datajsonObj.get(inputjsonObjkeys[i]).toString().indexOf(inputjsonObj.get(inputjsonObjkeys[i]).toString());
									if(x>=0){
    		    				count++;
									}
								}
							} 

							if(count>0){
    							System.out.println("x count  :  "+count);

    							System.out.println("Contactkey:  "+Contactkey);

								contactidlist.add(Contactkey);
//    		  	  				contactdataObject.put("contactid", Contactkey);
//    							interaction.setContactid(Contactkey);
							}
					}
			
			
			
		}

		if(agentid!=null&& !agentid.isEmpty()){
			System.out.println("agentid if");
			interaction.setAgentid(agentid);
		}
		if(contactid!=null&& !contactid.isEmpty()){
			System.out.println("contactid if");
			interaction.setContactid(contactid);
		}
		int oo = 0;
		JSONArray PersonJsonArray = new JSONArray();
		JSONArray testArray = new JSONArray();
		if(contactidlist.size()>0){
			System.out.println("contactidlist: "+contactidlist);
				for(int i = 0; i<contactidlist.size();i++){
					
					interaction.setContactid(contactidlist.get(i));
//					System.out.println("contactid: "+contactidlist.get(i));

					List<Interaction> interactionlist = maintainservice.Selcet_interaction(interaction);

			  	    	for(int a = 0; a < interactionlist.size(); a++){
//			  	    		System.out.println("interactionlist if");
			  	    	String name = "";
			  	    		
			  	    	rpt_activitylog.setInteractionid(interactionlist.get(a).getIxnid());
			  	    	List<Rpt_Activitylog> rpt_activityloglist = maintainservice.Selcet_activitylog(rpt_activitylog);
			  	    	
				  	    	for(int g = 0; g < rpt_activityloglist.size(); g++){
				  	    		if(rpt_activityloglist.get(g).getActivitydataid()!=null &&
				  					!rpt_activityloglist.get(g).getActivitydataid().equals("") &&
				  						!rpt_activityloglist.get(g).getActivitydataid().equals("null")){
				  	    				activitydata.setDbid(Integer.valueOf(rpt_activityloglist.get(g).getActivitydataid()));
				  	  	    			List<Activitydata> activitydatalist = maintainservice.IXN_activitydata(activitydata);
				  	  	    			if(activitydatalist.size()>0){
				  	  	    				name+=activitydatalist.get(0).getCodename()+",";
				  	  	    			}else{
				  	  	    				name+=rpt_activityloglist.get(g).getActivitydataid()+"[無此代碼],";
				  	  	    			}
				  	    		}
				  	    	}	
				  	    	
//				  	    	String Contactid = interactionlist.get(a).getContactid().trim();
//				    		contactdata.setContactid(Contactid);
//				    		System.out.println("Contactid: "+Contactid);
//				    			Map<String, String> contactidmap = maintainservice.Query_Contactdata(Contactid);

				    		
				    			
				  	    	
				  	    	if(interactionlist.get(a).getAgentid()!=null &&
					  					!interactionlist.get(a).getAgentid().equals("") &&
					  						!interactionlist.get(a).getAgentid().equals("null")&&
					  						interactionlist.get(a).getIxnid()!=null&&
					  						!interactionlist.get(a).getIxnid().equals("")&&
					  						!interactionlist.get(a).getIxnid().equals("null")){	
					    		Integer bb = Integer.valueOf(interactionlist.get(a).getAgentid());
								cfg_person.setDbid(bb);
								

						    	List<CFG_person> cfg_personlist = maintainservice.query_Person_DBID(cfg_person);
								//for(int d = 0; d < cfg_personlist.size(); d++){
										JSONObject cfg_personObject = new JSONObject();
										cfg_personObject.put("username", cfg_personlist.get(0).getUser_name());
										PersonJsonArray.put(cfg_personObject);
										JSONObject testobj = new JSONObject();
										if(cfg_personlist.get(0).getUser_name()!=""&&cfg_personlist.get(0).getUser_name()!=null){
											testobj.put("Agentname", cfg_personlist.get(0).getUser_name());
										}else{
											testobj.put("Agentname", "");
										}
										
										
										if(interactionlist.get(a).getThecomment()!=null&&interactionlist.get(a).getThecomment()!=""){
											testobj.put("Thecomment", interactionlist.get(a).getThecomment());
										}else{
											testobj.put("Thecomment", "");
										}
										
										if(interactionlist.get(a).getStartdate()!=""&&interactionlist.get(a).getStartdate()!=null){
											testobj.put("Startdate", interactionlist.get(a).getStartdate().substring(0, 19));
										}else{
											testobj.put("Startdate", "");
										}
										
										if(interactionlist.get(a).getStartdate()!=""&&interactionlist.get(a).getStartdate()!=null){
											testobj.put("Enddate", interactionlist.get(a).getEnddate().substring(0, 19));
										}else{
											testobj.put("Enddate", "");
										}
										if(name.length()>0){
											testobj.put("Codename",name.substring(0, name.length()-1));
										}else{
											testobj.put("Codename",name);
										}
										testobj.put("ixnid", interactionlist.get(a).getIxnid());
										if(interactionlist.get(a).getEntitytypeid().equals("2")){
											testobj.put("src", "chat");
										}else{
											testobj.put("src", "");
										}
										
//						    			testobj.put("BasicINF", contactidmap);

										testArray.put(testobj);
								//}
					    		
				  	    	}
				  	    	oo++;
			  	    	}
				}
		}else{
	    		System.out.println("else");

		List<Interaction> interactionlist = maintainservice.Selcet_interaction(interaction);

  	    	for(int a = 0; a < interactionlist.size(); a++){
  	    	String name = "";
//	    		System.out.println("interactionlist");

  	    	rpt_activitylog.setInteractionid(interactionlist.get(a).getIxnid());
  	    	List<Rpt_Activitylog> rpt_activityloglist = maintainservice.Selcet_activitylog(rpt_activitylog);
  	    	
	  	    	for(int g = 0; g < rpt_activityloglist.size(); g++){
	  	    		if(rpt_activityloglist.get(g).getActivitydataid()!=null &&
	  					!rpt_activityloglist.get(g).getActivitydataid().equals("") &&
	  						!rpt_activityloglist.get(g).getActivitydataid().equals("null")){
	  	    				activitydata.setDbid(Integer.valueOf(rpt_activityloglist.get(g).getActivitydataid()));
	  	  	    			List<Activitydata> activitydatalist = maintainservice.IXN_activitydata(activitydata);
	  	  	    			if(activitydatalist.size()>0){
	  	  	    				name+=activitydatalist.get(0).getCodename()+",";
	  	  	    			}else{
	  	  	    				name+=rpt_activityloglist.get(g).getActivitydataid()+"[無此代碼],";
	  	  	    			}
	  	    		}
	  	    	}	
	  	    	
//	  	    	String Contactid = interactionlist.get(a).getContactid().trim();
//	    		contactdata.setContactid(Contactid);
//	    		System.out.println("Contactid: "+Contactid);
//	    			Map<String, String> contactidmap = maintainservice.Query_Contactdata(Contactid);

	  	    	if(interactionlist.get(a).getAgentid()!=null &&
		  					!interactionlist.get(a).getAgentid().equals("") &&
		  						!interactionlist.get(a).getAgentid().equals("null")&&
		  						interactionlist.get(a).getIxnid()!=null&&
		  						!interactionlist.get(a).getIxnid().equals("")&&
		  						!interactionlist.get(a).getIxnid().equals("null")){	
		    		Integer bb = Integer.valueOf(interactionlist.get(a).getAgentid());
					cfg_person.setDbid(bb);
					

			    	List<CFG_person> cfg_personlist = maintainservice.query_Person_DBID(cfg_person);
					//for(int d = 0; d < cfg_personlist.size(); d++){
							JSONObject cfg_personObject = new JSONObject();
							cfg_personObject.put("username", cfg_personlist.get(0).getUser_name());
							PersonJsonArray.put(cfg_personObject);
							JSONObject testobj = new JSONObject();
							if(cfg_personlist.get(0).getUser_name()!=""&&cfg_personlist.get(0).getUser_name()!=null){
								testobj.put("Agentname", cfg_personlist.get(0).getUser_name());
							}else{
								testobj.put("Agentname", "");
							}
							
							
							if(interactionlist.get(a).getThecomment()!=null&&interactionlist.get(a).getThecomment()!=""){
								testobj.put("Thecomment", interactionlist.get(a).getThecomment());
							}else{
								testobj.put("Thecomment", "");
							}
							
							if(interactionlist.get(a).getStartdate()!=""&&interactionlist.get(a).getStartdate()!=null){
								testobj.put("Startdate", interactionlist.get(a).getStartdate().substring(0, 19));
							}else{
								testobj.put("Startdate", "");
							}
							
							if(interactionlist.get(a).getStartdate()!=""&&interactionlist.get(a).getStartdate()!=null){
								testobj.put("Enddate", interactionlist.get(a).getEnddate().substring(0, 19));
							}else{
								testobj.put("Enddate", "");
							}
							if(name.length()>0){
								testobj.put("Codename",name.substring(0, name.length()-1));
							}else{
								testobj.put("Codename",name);
							}
							testobj.put("ixnid", interactionlist.get(a).getIxnid());
							if(interactionlist.get(a).getEntitytypeid().equals("2")){
								testobj.put("src", "chat");
							}else{
								testobj.put("src", "");
							}
							
//			    			testobj.put("BasicINF", contactidmap);

							testArray.put(testobj);
					//}
		    		
	  	    	}
	  	    	oo++;
  	    	}
  	    	
		}
			jsonObject.put("data", testArray);
	  	    System.out.println(oo);
  	  
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
