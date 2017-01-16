package restful.servlet;

import java.io.IOException;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * RESTful Test
 * @author Lin
 */

@Path("/test")
public class TestServlet {
	
	@GET
	//@Produces("application/json")
	public Response GetFromPath(@QueryParam("value")
	String value) throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("test", value+" Success");

		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
