package restful.servlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import util.Util;

import com.Info360.bean.Interaction;
import com.Info360.service.MaintainService;
import com.Info360.util.IsError;
import com.Info360.util.Variable;

/**
 * RESTful ServiceEntry
 * @author Lin
 */

@Path("/Interaction")
public class InteractionServlet {
	
	@POST
	@Produces("application/json;charset=UTF-8")
	public Response PostFromPath(@FormParam("contactid")String contactid, //客戶ID
			@FormParam("userid")String userid, //Room ID
			@FormParam("ixnid")String ixnid, //Room ID
			@FormParam("agentid")String agentid, //Agent的ID
			@FormParam("startdate")String startdate, 
			@FormParam("enddate")String enddate,
			@FormParam("status")String status, //結束狀態
			@FormParam("typeid")String typeid, //Inbound,outbound
			@FormParam("entitytypeid")String entitytypeid, //0-Inbound,1-outbound,2-chat
			@FormParam("subtypeid")String subtypeid, //InBound New, OutBound Reply, OutBound Redirect
			@FormParam("subject")String subject, //主題(Email)
			@FormParam("text")String text,//文字格式對話內容
			@FormParam("structuredtext")String structuredtext,//JSON格式對話內容
			@FormParam("structuredmimetype")String structuredmimetype,//標籤(Email)
			@FormParam("thecomment")String thecomment,//備註
			@FormParam("stoppedreason")String stoppedreason,//異常理由(server)
			@FormParam("activitycode")String activitycode //處理原因
			) throws IOException {
		
		Util.getFileLogger().info("interaction.text: " + text);
		Util.getFileLogger().info("interaction.structuredtext: " + structuredtext);
		Util.getFileLogger().info("interaction.stoppedreason: " + stoppedreason);
		Util.getFileLogger().info("interaction.userid: " + userid);
		
//		text = URLDecoder.decode(text, "UTF-8");
//		structuredtext = URLDecoder.decode(structuredtext, "UTF-8");
//		thecomment = URLDecoder.decode(thecomment, "UTF-8");
//		subject = URLDecoder.decode(subject, "UTF-8");
		
		
		JSONObject jsonObject = new JSONObject();
		Interaction interaction = new Interaction();
		Map<String,String> interactionlist = new HashMap<String,String>();
		if(userid!=null&&!"".equals(userid)){
			interaction.setUserid(userid);
			interactionlist.put("userid", userid);
		}
		if(contactid!=null&&!"".equals(contactid)){
			interaction.setContactid(contactid);
			interactionlist.put("contactid", contactid);
		}
		if(ixnid!=null&&!"".equals(ixnid)){
			interaction.setIxnid(ixnid);
			interactionlist.put("ixnid", ixnid);
		}
		if(agentid!=null&&!"".equals(agentid)){
			interaction.setAgentid(agentid);
			interactionlist.put("agentid", agentid);
		}
		if(startdate!=null&&!"".equals(startdate)){
			interaction.setStartdate(startdate);
			interactionlist.put("startdate", startdate);
		}
		if(enddate!=null&&!"".equals(enddate)){
			interaction.setEnddate(enddate);
			interactionlist.put("enddate", enddate);
		}
		if(status!=null&&!"".equals(status)){
			interaction.setStatus(status);
			interactionlist.put("status", status);
		}
		if(typeid!=null&&!"".equals(typeid)){
			interaction.setTypeid(typeid);
			interactionlist.put("typeid", typeid);
		}
		if(entitytypeid!=null&&!"".equals(entitytypeid)){
			interaction.setEntitytypeid(entitytypeid);
			interactionlist.put("entitytypeid", entitytypeid);
		}
		if(subtypeid!=null&&!"".equals(subtypeid)){
			interaction.setSubtypeid(subtypeid);
			interactionlist.put("subtypeid", subtypeid);
		}
		if(subject!=null&&!"".equals(subject)){
			interaction.setSubject(subject);
			interactionlist.put("subject", subject);
		}
		if(text!=null&&!"".equals(text)){
			interaction.setText(text);
			interactionlist.put("text", text);
		}
		if(structuredtext!=null&&!"".equals(structuredtext)){
			interaction.setStructuredtext(structuredtext);
			interactionlist.put("structuredtext", structuredtext);
		}
		if(structuredmimetype!=null&&!"".equals(structuredmimetype)){
			interaction.setStructuredmimetype(structuredmimetype);
			interactionlist.put("structuredmimetype", structuredmimetype);
		}
		if(thecomment!=null&&!"".equals(thecomment)){
			interaction.setThecomment(thecomment);
			interactionlist.put("thecomment", thecomment);
		}
		if(stoppedreason!=null&&!"".equals(stoppedreason)){
			interaction.setStoppedreason(stoppedreason);
			interactionlist.put("stoppedreason", stoppedreason);
		}
		if(activitycode!=null&&!"".equals(activitycode)){
			interaction.setActivitycode(activitycode);
			interactionlist.put("activitycode", activitycode);
		}
		interaction.setInteractionlist(interactionlist);
		jsonObject.put("interactionlist", interaction.getInteractionlist());
		jsonObject.put("status", Variable.POST_STATUS);
		
		
		Util.getFileLogger().info("interaction.getUserid(): " + interaction.getUserid());
		Util.getFileLogger().info("interactionlist.get(\"userid\"): " + interactionlist.get("userid"));
		Util.getFileLogger().info("interaction.getText(): " + interaction.getText());
		Util.getFileLogger().info("interaction.getStructuredtext(): " + interaction.getStructuredtext());
		
		try{ 
			MaintainService maintainService = new MaintainService();
			int interactionInt = maintainService.insert_Interaction(interaction);
//			Util.getConsoleLogger().debug("interaction insertcount: "+interactionInt);
			jsonObject.put("insertcount", interactionInt);
	        	
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
