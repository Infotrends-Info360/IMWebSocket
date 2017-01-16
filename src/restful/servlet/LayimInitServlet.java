package restful.servlet;

import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Layim initialization RESTful
 * @author Lin
 */

@Path("/LayimInit")
public class LayimInitServlet {
	
	@GET
	public Response GetFromPath(@QueryParam("username") String username
			,@QueryParam("id") String id
			,@QueryParam("sign") String sign
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", 0);
		jsonObject.put("msg", "");
			JSONObject datajsonObject = new JSONObject();
				JSONObject minejsonObject = new JSONObject();
				minejsonObject.put("username", username);
				minejsonObject.put("id", id);
				minejsonObject.put("status", "online");
				minejsonObject.put("sign", sign);
				minejsonObject.put("avatar", "./layui/images/git.jpg");
			datajsonObject.put("mine", minejsonObject);
				JSONArray friendjsonarray = new JSONArray();
				JSONObject friendjsonObject = new JSONObject();
				friendjsonObject.put("groupname", "Agent");
				friendjsonObject.put("id", 1);
				friendjsonObject.put("online", 2);
					JSONArray listjsonarray = new JSONArray();
				friendjsonObject.put("list", listjsonarray);
				friendjsonarray.put(friendjsonObject);
			datajsonObject.put("friend", friendjsonarray);
				JSONArray groupjsonarray = new JSONArray();
			datajsonObject.put("group", groupjsonarray);
		jsonObject.put("data", datajsonObject);
		
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
