			
function SelectSumit(){ 
	
			var LoginTime = document.getElementById("Login_time").value;
			var browserversion = document.getElementById("Browser_Version").value;
			var Channeltype = document.getElementById("Channeltype").value;
			var language = document.getElementById("web_language").value;
			var platfrom = document.getElementById("Platfrom").value;
			var Cli_IP = document.getElementById("Cli_IP").value;
			
	//ServiceEntry console log
		     $.ajax({
		         //告訴程式表單要傳送到哪裡                                         
		         url:"/IMWebSocket/RESTful/ServiceEntry",
		         //需要傳送的資料
		         data:{
		        	 username:UserName,
		        	 userid:UserID,
		        	 browserversion:browserversion,
		        	 language:language,
		        	 entertime:LoginTime,
		        	 channeltype:Channeltype,
		        	 platfrom:platfrom,
		        	 ipaddress:Cli_IP
		        	 },
		          //使用POST方法     
		         type : "POST",                                                                    
		         //接收回傳資料的格式，在這個例子中，只要是接收true就可以了
		         dataType:'json', 
		          //傳送失敗則跳出失敗訊息          
		         error:function(e){                                                                 
		         alert("失敗");
		         callback(data);
		         },
		         success:function(data){                                                           
		        	 console.log("login",data)
	    
		         },
		      
		     }); 
		     
}