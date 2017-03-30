package restful.servlet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;



import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.AbstractMap;
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

import com.Info360.bean.Activitydata;
import com.Info360.bean.CFG_person;
import com.Info360.bean.CaseComments;
import com.Info360.bean.Cfg_CaseStatus;
import com.Info360.bean.ContactData;
import com.Info360.bean.Interaction;
import com.Info360.bean.Rpt_Activitylog;
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/detailQuery")
public class detailQuery_Servlet {
	

	
	@POST
	@Produces("application/json")
	public Response PostFromPath(
		
			@FormParam("ixnid") String ixnid
			
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		
		Interaction interaction = new Interaction();
		Rpt_Activitylog rpt_activitylog = new Rpt_Activitylog();
		Activitydata activitydata = new Activitydata();
		CFG_person cfg_person = new CFG_person();
		ContactData contactdata = new ContactData();

		CaseComments casecomments = new CaseComments(); 
		Cfg_CaseStatus cfg_casestatus = new Cfg_CaseStatus(); 
		
		interaction.setIxnid(ixnid);
		
		
		MaintainService maintainservice = new MaintainService();		
		List<Interaction> interactionlist = maintainservice.Selcet_Detail_interaction(interaction);
		
		JSONArray PersonJsonArray = new JSONArray();
		JSONArray testArray = new JSONArray();

  	    	String name = "";
  	    	System.out.println("Ixn: "+interactionlist.get(0).getIxnid());
  	    	rpt_activitylog.setInteractionid(interactionlist.get(0).getIxnid());
  	    	List<Rpt_Activitylog> rpt_activityloglist = maintainservice.Selcet_activitylog(rpt_activitylog);
  	    	
	  	    	for(int g = 0; g < rpt_activityloglist.size(); g++){
	  	    		if(rpt_activityloglist.get(g).getActivitydataid()!=null &&
	  					!rpt_activityloglist.get(g).getActivitydataid().equals("") &&
	  						!rpt_activityloglist.get(g).getActivitydataid().equals("null")){
	  	    			
	  	    	    	System.out.println("Activitydataid: "+Integer.valueOf(rpt_activityloglist.get(g).getActivitydataid()));

	  	    				activitydata.setDbid(Integer.valueOf(rpt_activityloglist.get(g).getActivitydataid()));
	  	  	    			List<Activitydata> activitydatalist = maintainservice.IXN_activitydata(activitydata);
	  	  	    			if(activitydatalist.size()>0){
	  	  	    				name+=activitydatalist.get(0).getCodename()+",";
	  	  	    			}else{
	  	  	    				name+=rpt_activityloglist.get(g).getActivitydataid()+"[無此代碼],";
	  	  	    			}
	  	    		}
	  	    	}	
	  		
	  	    	
	  	    	
	  	    	
	  	    	if(interactionlist.get(0).getAgentid()!=null &&
		  					!interactionlist.get(0).getAgentid().equals("") &&
		  						!interactionlist.get(0).getAgentid().equals("null")
//		  						
		  				&&interactionlist.get(0).getContactid()!=null &&
				  					!interactionlist.get(0).getContactid().equals("") &&
				  						!interactionlist.get(0).getContactid().equals("null")		
	  	    			
	  	    			){	
	  	    		
  	    			System.out.println("Ixnid:  "+interactionlist.get(0).getIxnid());
  	    			
		    		Integer bb = Integer.valueOf(interactionlist.get(0).getAgentid());
					cfg_person.setDbid(bb);
					
			    	List<CFG_person> cfg_personlist = maintainservice.query_Person_DBID(cfg_person);
							JSONObject cfg_personObject = new JSONObject();
							cfg_personObject.put("username", cfg_personlist.get(0).getUser_name());
							PersonJsonArray.put(cfg_personObject);
							
							
							
								String Contactid = interactionlist.get(0).getContactid().trim();
					    		contactdata.setContactid(Contactid);
					    		System.out.println("Contactid: "+Contactid);
					    		
					    			Map<String, String> contactidmap = maintainservice.Query_Contactdata(Contactid);
//						    		JSONObject contactdataObject = new JSONObject();
//						    		contactdataObject.put("userdata", contactidmap);
//						    		ContactDataArray.put(contactdataObject);
					    			System.out.println(interactionlist.get(0));
					    			JSONArray structuredtextarray;
					    			if(interactionlist.get(0).getStructuredtext()!=null){
					    				structuredtextarray = new JSONArray(interactionlist.get(0).getStructuredtext());
					    			}else{
					    				structuredtextarray = new JSONArray();
					    			}
					    			
					    			
					    			
							
							JSONObject testobj = new JSONObject();
							testobj.put("Agentname", cfg_personlist.get(0).getUser_name());
							testobj.put("Startdate", interactionlist.get(0).getStartdate().substring(0, 19));
							testobj.put("Enddate", interactionlist.get(0).getEnddate().substring(0, 19));
							testobj.put("BasicINF", contactidmap);
							
							
							testobj.put("Structuredtext", structuredtextarray);
							
							JSONArray commentsarray = new JSONArray();
							
							casecomments.setIxnid(ixnid);
		  	    			List<CaseComments> cfg_casecommentslist = maintainservice.Select_IXN_casecomments(casecomments);
		  	    			
		  	    			for(int i = 0; i<cfg_casecommentslist.size() ; i++){
		  	    				//System.out.println("status:  "+cfg_casecommentslist.get(i).getStatus());
			  	    			cfg_casestatus.setDbid(Integer.valueOf(cfg_casecommentslist.get(i).getStatus()));
			  	    			List<Cfg_CaseStatus> cfg_casestatuslist = maintainservice.Select_IXN_cfg_casestatus(cfg_casestatus);
				    			//System.out.println("statusname:  "+cfg_casestatuslist.get(0).getStatusName());
				    			
				    			JSONObject jsonobject = new JSONObject();
								jsonobject.put("comment", cfg_casecommentslist.get(i).getComment());
								
								cfg_person.setDbid(Integer.valueOf(cfg_casecommentslist.get(i).getAgentid()));
						    	List<CFG_person> cfg_personlist2 = maintainservice.query_Person_DBID(cfg_person);
								
								jsonobject.put("agent", cfg_personlist2.get(0).getUser_name());
								jsonobject.put("datetime", cfg_casecommentslist.get(i).getDatetime().substring(0, 19));
								jsonobject.put("statusname", cfg_casestatuslist.get(0).getStatusName());
								
								commentsarray.put(jsonobject);
		  	    			}
		  	    			
		  	    			if(interactionlist.get(0).getThecomment()!=null&&interactionlist.get(0).getThecomment()!=""&&!interactionlist.get(0).getThecomment().equals("null")){
//								testobj.put("Thecomment", interactionlist.get(0).getThecomment());
								
								JSONObject jsonobject = new JSONObject();
								String comment = interactionlist.get(0).getThecomment();
								if(comment==null){
									comment = "";
								}
								jsonobject.put("comment", comment);
								jsonobject.put("agent", cfg_personlist.get(0).getUser_name());
								jsonobject.put("datetime", interactionlist.get(0).getEnddate().substring(0, 19));
								jsonobject.put("statusname", "備註");
								
								commentsarray.put(jsonobject);
							}
		  	    			
			    			
			    			
			    			
			    			
			    			testobj.put("Thecomment", commentsarray);
							
							JSONObject ServiceNameCachejsonObj;
							try {
								ServiceNameCachejsonObj = GetServiceNameCache("A");
//				    			jsonObject.put("mapping", ServiceNameCachejsonObj);
				    			
								testobj.put("mapping", ServiceNameCachejsonObj);

							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
//							testobj.put("pp",);


							if(name.length()>0){
								testobj.put("Codename",name.substring(0, name.length()-1));
							}else{
								testobj.put("Codename",name);
							}
							testobj.put("ixnid", interactionlist.get(0).getIxnid());
							if(interactionlist.get(0).getEntitytypeid().equals("2")){
								testobj.put("src", "chat");
							}
							testArray.put(testobj);
					
		    		
	  	    	}
	  	    	
  	    	
  	    jsonObject.put("data", testArray);

  	  
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

