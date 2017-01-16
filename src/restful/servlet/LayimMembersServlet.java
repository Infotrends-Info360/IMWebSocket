package restful.servlet;

import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Layim Members initialization RESTful
 * @author Lin
 */

@Path("/LayimMembers")
public class LayimMembersServlet {
	
	@GET
	public Response GetFromPath(@QueryParam("username") String username
			,@QueryParam("id") String id
			,@QueryParam("sign") String sign
			,@QueryParam("addusername") String addusername
			,@QueryParam("addid") String addid
			,@QueryParam("addsign") String addsign
			) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", 0);
		jsonObject.put("msg", "");
			JSONObject datajsonObject = new JSONObject();
				JSONObject ownerjsonObject = new JSONObject();
				ownerjsonObject.put("username", username);
				ownerjsonObject.put("id", id);
				ownerjsonObject.put("sign", sign);
				ownerjsonObject.put("avatar", "./layui/images/git.jpg");
			datajsonObject.put("owner", ownerjsonObject);
				JSONArray listjsonarray = new JSONArray();
				JSONObject userlistjsonObject = new JSONObject();
				userlistjsonObject.put("username", username);
				userlistjsonObject.put("id", id);
				userlistjsonObject.put("sign", sign);
				userlistjsonObject.put("avatar", "./layui/images/git.jpg");
				listjsonarray.put(userlistjsonObject);
				JSONObject listjsonObject = new JSONObject();
				listjsonObject.put("username", addusername);
				listjsonObject.put("id", addid);
				listjsonObject.put("sign", addsign);
				listjsonObject.put("avatar", "./layui/images/git.jpg");
				listjsonarray.put(listjsonObject);
			datajsonObject.put("list", listjsonarray);
		jsonObject.put("data", datajsonObject);
		
		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
