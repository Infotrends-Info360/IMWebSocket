package restful.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * searchUserdata RESTful
 * @author Lin
 */

@Path("/searchUserdata")
public class searchUserdataServlet {
	
	JSONObject jsonObject = new JSONObject();
	
	@POST
	@Produces("application/json")
	public Response PostFromPath(//@FormParam("searchkey")String searchkey,
			//@FormParam("pkey")String pkey,
			@FormParam("searchtype")String searchtype,
			@FormParam("attributes")String attributes,
			@FormParam("attributenames")String attributenames,
			@FormParam("lang")String lang
			) throws IOException {
		/*** debug - 測試時間 ***/
		long startTime;
		long endTime;
		
		System.out.println("attributes: "+attributes);
		
		JSONObject CfgServiceNameSettingjsonObject = null;
		String searchkey = null;
		String pkey = null;
		try {
			CfgServiceNameSettingjsonObject = GetServiceNameSetting(searchtype);
			searchkey = CfgServiceNameSettingjsonObject.getString("searchkey");
			pkey = CfgServiceNameSettingjsonObject.getString("uniquekey");
		} catch(org.json.JSONException e){
			System.out.println("org.json.JSONException - " + "searchUserdataServlet.java");
//			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		jsonObject.put("searchkey", searchkey);
		jsonObject.put("pkey", pkey);
		
		JSONObject attributesjsonObject = new JSONObject(attributes);
		
		jsonObject.put("searchtype", searchtype);
		jsonObject.put("lang", lang);
		
		String[] attributenamesArray = attributenames.split(",");
		for(int i=0;i<attributenamesArray.length;i++){
			JSONArray CustomerLeveljsonarray = null;
			if(attributenamesArray[i].equals(searchkey)){
				//GetCustomerLevel
				try {
					//String sID = "A123456789";
					//String sCustLevel = "A";
					String bSelect = "false";
					CustomerLeveljsonarray = GetCustomerLevel(attributesjsonObject.get(attributenamesArray[i]).toString(), searchtype, bSelect);
					jsonObject.put("CustomerData", CustomerLeveljsonarray);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					jsonObject.put("error", e.getMessage());
				}
				
				//GetServiceNameCache
				try{
					JSONObject ServiceNameCachejsonObj = GetServiceNameCache(searchtype);
					jsonObject.put("mapping", ServiceNameCachejsonObj);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					jsonObject.put("error", e.getMessage());
				}
				
				//Set Contact Log
				try{
					for (int j = 0; j < CustomerLeveljsonarray.length(); j++) {
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
						Date now = new Date();
						String date=sdf.format(now);
						jsonObject.put("date", date);
						JSONObject SetContactLogjsonObject = SetContactLog(searchkey, pkey, date, CustomerLeveljsonarray.getJSONObject(j).toString());
						jsonObject.put("SetContactLog", SetContactLogjsonObject);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					jsonObject.put("error", e.getMessage());
				}
			}
			jsonObject.put(attributenamesArray[i], attributesjsonObject.get(attributenamesArray[i]));
		}

		return Response.status(200).entity(jsonObject.toString())
				.header("charser", "utf-8")
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
	public JSONObject GetServiceNameSetting(String typeid) throws Exception{
		StringBuilder responseSB = null;
		// Encode the query 
		String postData = "typeid="+typeid;
    			
        // Connect to URL
        URL url = new URL("http://127.0.0.1:8080/IMWebSocket/RESTful/Cfg_ServiceName_Setting");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length",  String.valueOf(postData.length()));
         
        // Write data
        OutputStream os = connection.getOutputStream();
        os.write(postData.getBytes());
         
        // Read response
        responseSB = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
          
        String line;
        while ( (line = br.readLine()) != null)
            responseSB.append(line.trim());
                 
        // Close streams
        br.close();
        os.close();
        
        //System.out.println("responseSB: "+responseSB.toString().trim());
        JSONObject CfgServiceNameSettingjsonObject = new JSONObject(responseSB.toString());
		return CfgServiceNameSettingjsonObject;
	}
	
 	public JSONArray GetCustomerLevel(String searchkeyvalue, String searchtype, String bSelect) throws Exception{
		StringBuilder responseSB = null;
		// Encode the query 
        String postData = "sID="+searchkeyvalue
        		+ "&sCustLevel="+searchtype
        		+ "&bSelect="+bSelect;
    			
        // Connect to URL
        // 小林哥表示: 須建立判斷機制log,設定timeout協助以後判斷問題來源: 
        URL url = new URL("http://192.168.10.7/infoacd/infoCenterWebService.asmx/GetCustomerLevel"); 
//        URL url = new URL("http://192.168.10.40:8080/infoacd/infoCenterWebService.asmx/GetCustomerLevel"); // 當192.168.10.7掛點時,使用此server
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length",  String.valueOf(postData.length()));
        //connection.setRequestProperty("Accept-Charset", "UTF-8");
        //connection.setRequestProperty("charset", "UTF-8");
        
        // Write data
        OutputStream os = connection.getOutputStream();
        os.write(postData.getBytes());
         
        // Read response
        responseSB = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
          
        String line;
        while ( (line = br.readLine()) != null)
        	//responseSB.append(new String(line.toString().getBytes(),"UTF-8"));
            responseSB.append(line.trim());
                 
        // Close streams
        br.close();
        os.close();
        
        //System.out.println("responseSB: "+responseSB.toString().trim());
        
        
        DocumentBuilderFactory factory_xpath = DocumentBuilderFactory
				.newInstance(); 
		Document doc = factory_xpath
				.newDocumentBuilder().parse(
						new InputSource(
								new StringReader(
										//new String(responseSB.toString().getBytes(),"UTF-8"))));
										responseSB.toString().trim())));
		XPath xpath = XPathFactory.newInstance()
				.newXPath();
		
		int count = ((Number)xpath.evaluate("count(DataSet/diffgram/NewDataSet/CustomerData)", doc, javax.xml.xpath.XPathConstants.NUMBER)).intValue();  
		//System.out.println("count: "+count);
		jsonObject.put("CustomerDataCount", count);
		
		JSONArray jsonarray = new JSONArray();
		JSONObject infojsonObject = new JSONObject();
		for(int j=0;j<count;j++){
			int c = j+1;
			int CustomerDataChildcount = ((Number)xpath.evaluate("count(DataSet/diffgram/NewDataSet/CustomerData["+c+"]/*)", doc, javax.xml.xpath.XPathConstants.NUMBER)).intValue();  
			for(int k=0;k<CustomerDataChildcount;k++){
				Object CustomerDatasO = (xpath.evaluate("DataSet/diffgram/NewDataSet/CustomerData["+c+"]/*", doc, javax.xml.xpath.XPathConstants.NODESET));  
				NodeList CustomerDatasn = (NodeList) CustomerDatasO;
				String CustomerDataskey = (String) CustomerDatasn.item(k)
						.getNodeName();
				String CustomerDatasvalue = (String) CustomerDatasn.item(k)
						.getTextContent();
				infojsonObject.put(CustomerDataskey, CustomerDatasvalue);
			}
			
			jsonarray.put(infojsonObject);
			
			return jsonarray;
		}
		return jsonarray;
	}

	public JSONObject GetServiceNameCache(String searchtype) throws Exception{
		StringBuilder responseSB = null;
		// Encode the query 
        String GetData = "typeid="+searchtype
        		+ "&method=get"
        		+ "&key=all";
    			
        // Connect to URL
        URL url = new URL("http://ws.crm.com.tw:8080/ServiceNameCache/RESTful/datacache?"+GetData);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("GET");
        //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        //connection.setRequestProperty("Content-Length",  String.valueOf(postData.length()));
         
        // Write data
        //OutputStream os = connection.getOutputStream();
        //os.write(postData.getBytes());
         
        // Read response
        responseSB = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
          
        String line;
        while ( (line = br.readLine()) != null)
            responseSB.append(line.trim());
                 
        // Close streams
        br.close();
        //os.close();
        
        //System.out.println("responseSB: "+responseSB.toString().trim());
        JSONObject ServiceNameCachejsonObj = new JSONObject(responseSB.toString());
		return ServiceNameCachejsonObj;
	}

	public JSONObject SetContactLog(String searchkey, String pkey, String date, String userdata) throws Exception{
		StringBuilder responseSB = null;
		// Encode the query 
		String postData = "searchkey="+searchkey
				+ "&pkey="+pkey
        		+ "&date="+date
        		+ "&userdata="+userdata;
    			
        // Connect to URL
        URL url = new URL("http://127.0.0.1:8080/IMWebSocket/RESTful/ContactData");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length",  String.valueOf(postData.length()));
         
        // Write data
        OutputStream os = connection.getOutputStream();
        os.write(postData.getBytes());
         
        // Read response
        responseSB = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
          
        String line;
        while ( (line = br.readLine()) != null)
            responseSB.append(line.trim());
                 
        // Close streams
        br.close();
        os.close();
        
        //System.out.println("responseSB: "+responseSB.toString().trim());
        JSONObject SetContactLogjsonObject = new JSONObject(responseSB.toString());
		return SetContactLogjsonObject;
	}
}
