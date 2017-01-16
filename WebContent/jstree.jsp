<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" > 
	<head > 
	    <title>未命名頁面</title> 
	    
	   <link rel="stylesheet" href="jstree/style.min.css" />

		<script src="js/jquery.min.js"></script>
	
	    <script type="text/javascript" src="jstree/jstree.min.js"></script>
	    
	    <link href="css/bootstrap.min.css?v=3.3.6" rel="stylesheet">
	    
	    <script src="js/plugins/bootstrap-table/bootstrap-table.min.js"></script>
	</head> 
	
<body> 


<div id="tree" onclick="link()"></div>

<button onclick="page()"> ~長樹~ </button>
</body> 
	
	 <script type="text/javascript">
	 
	 function page(){
		 var a = 0;
		  $.ajax({                              
	          url:"/IMWebSocket/RESTful/Select_commonlink",
		         data:{
		        	 sort:a
		        	 },
		            
		         type : "POST",                                                                    
		         dataType:'json',
		         
		         error:function(e){                                                                 
		         alert("失敗");
		         callback(data);
		         },
		         success:function(data){
		        	 console.log("Tree",data);
		        	 
		        	 $('#tree').jstree({
		 				'core' : {
		 					'data' : data.Tree
		 				},
		        	 "plugins" : [ "themes", "json_data","ui"]
		 			})
		 			
		 			$('#tree').bind('select_node.jstree', function(e,data) { 
		 			    window.location.href = data.instance.get_node(data.node, true).children('a').attr('href');
		 			});
		        	
		         },
		     });
		  
	 		}; 
	 		
	 		function link(){
	 			
	 			 $('#tree').jstree.bind("select_node.jstree", function (e, data) {
	 				var href = data.node.a_attr.href;
	 				var parentId = data.node.a_attr.parent_id;
	 				if(href == '#')
	 				return '';

	 				window.open(href);
	 			})
	 			
	 		}
		 </script>
		 
		 
		
		 
		 </html> 