package restful.servlet;

import java.io.IOException;



import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.Info360.bean.Cfg_AgentReason;
import com.Info360.bean.CommonLink;
import com.Info360.bean.Interaction;
import com.Info360.bean.Rpt_Activitylog;
import com.Info360.bean.Rpt_AgentStatus;
import com.Info360.service.MaintainService;


/**
 * RESTful Interaction
 * @author Lin
 */

@Path("/Insert_rpt_activitylog")
public class Rpt_Activitylog_Insert_Servlet {
	

	/**
	 * @return
	 * @throws IOException
	 */
	@POST
	@Produces("application/json")
	public Response PostFromPath(
			@FormParam("interactionid") String interactionid,
			@FormParam("activitydataids") String activitydataids,
			@FormParam("comment") String comment
			//@FormParam("startdatetime") String startdatetime
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		Rpt_Activitylog activitylog = new Rpt_Activitylog();
		
		activitylog.setInteractionid(interactionid);
		
		String[] activitydataidArray = activitydataids.split(",");
		
		MaintainService maintainservice = new MaintainService();
		
		for(int i = 0; i < activitydataidArray.length ;i++){
			activitylog.setActivitydataid(activitydataidArray[i]);
			int insert = maintainservice.Insert_rpt_activitylog(activitylog);
			jsonObject.put("activitydataids_insertcount["+activitydataidArray[i]+"]", insert);
			
		}
		Interaction interaction = new Interaction();
		interaction.setIxnid(interactionid);
		interaction.setThecomment(comment);
		int update = maintainservice.update_Interaction_comment(interaction);
		jsonObject.put("comment_updatecount", update);
  
  	  
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
