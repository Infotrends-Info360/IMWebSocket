<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>

    <head>

        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">


        <title>「設定」頁面</title>
		<link rel="stylesheet" href="jstree/style.min.css" />
		<script src="js/jquery.min.js"></script>
		
        <link href="css/bootstrap.min.css?v=3.3.6" rel="stylesheet">
        <link href="css/font-awesome.css?v=4.4.0" rel="stylesheet">
        <link href="css/animate.css" rel="stylesheet">
        <link href="layui/css/layui.css" rel="stylesheet">
        <link href="css/plugins/datapicker/datepicker3.css" rel="stylesheet">
        <link href="css/style.css?v=4.1.0" rel="stylesheet">
        <link rel="stylesheet" href="jstree/style.min.css" />
        <script type="text/javascript" src="jstree/jstree.min.js"></script>
         

        <link href="css/plugins/toastr/toastr.min.css" rel="stylesheet">
        <style>
            label.required:after {content: " *"; color: red;}
        </style>
       
       <style type="text/css">
       .red { color:#FF5511 !important; }
       .yellow { color:#FFBB00 !important; }
       .blue { color:#0000CC !important; }
       .black { color:#666666 !important; }
       </style>
       
    </head>
    <body class="gray-bg">
        <div class="widget">
            <div class="col-lg-2 col-md-3">
                <div class="panel panel-success">
                    <div class="panel-heading">
                        <h3><i class="fa fa-angle-double-right"></i>  設定</h3>
                    </div>
                    <div class="panel-body">
                        <div class="ibox">
                            <div class="fa-tree-list">
                                <ul style="list-style-type:none;margin-left:0px;">
                                    <li>
                                        <span>
                                            <i class="fa fa-fw fa-folder-open"></i>
                                            使用者管理
                                        </span>
                                        <ul style="list-style-type:none;margin-left:20px;">
                                            <li onclick=""><i class="fa fa-fw fa-file-text-o"></i>人員管理</li>
                                            <li onclick=""><i class="fa fa-fw fa-file-text-o"></i>部門權限管理</li>
                                            <li onclick=""><i class="fa fa-fw fa-file-text-o"></i>值機狀態管理</li>
                                        </ul>
                                    </li>
                                    <li class="active">
                                        <span>
                                            <i class="fa fa-fw fa-folder-open"></i>
                                            管道管理
                                        </span>
                                        <ul style="list-style-type:none;margin-left:20px;">
                                            <li><i class="fa fa-fw fa-file-text-o"></i>分派小組管理</li>
                                            <li><i class="fa fa-fw fa-file-text-o"></i>Chat管道設定</li>
                                        </ul>
                                    </li>
                                    <li>
                                        <span>
                                            <i class="fa fa-fw fa-folder-open"></i>
                                            案件管理
                                        </span>

                                        <ul style="list-style-type:none;margin-left:20px;">
                                            <li><i class="fa fa-fw fa-file-text-o"></i>常用連結管理</li>
                                            <li><i class="fa fa-fw fa-file-text-o"></i>服務代碼管理</li>
                                        </ul>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="col-lg-10 col-md-9">
                <div class="panel panel-success">
                    <!-- 設定頁面內容頁 Start-->
                    <div class="panel-body" id="settingContent">
                        
                        <!-- 人員管理切換頁籤Start -->
                        <div id="hrTabControlButton">
                            <button class="btn-sm btn-primary manage" onclick="showManage()"><i class="fa fa-fw fa-user"></i>人員管理</button>
                            <button class="btn-sm btn-success ban" onclick="showBan()"><i class="fa fa-fw fa-user"></i>停用列表</button>
                            <button class="btn-sm btn-success addMember" style="display:none;"><span onclick="showAddMember()">新增人員</span> <i class="fa fa-times" onclick="closeAddMember()"></i></button>
                            <button class="btn-sm btn-success editMember" style="display:none;"><span onclick="showEditMember()">HolyLin</span> <i class="fa fa-times" onclick="closeEditMember()"></i></button>
                        </div>
                        <!-- 人員管理切換頁籤End -->

                        <div id="manageContent">
                            <div>
                                <ul class="pagination">
                                    <li onclick="showAddMember()"><a href="#"><i class="fa fa-fw fa-plus"></i></a></li>
                                    <li onclick="unlockAccount()"><a href="#"><i class="fa fa-fw fa-unlock-alt"></i></a></li>
                                    <li onclick="confirmBan()"><a href="#"><i class="fa fa-fw fa-ban"></i></a></li>
                                </ul>

                                <ul class="pagination" style="float:right;" >
                                    <li>
                                        <input type="text" id="manageSearch" placeholder="搜索"
                                               style="background-color: #FFFFFF;
                                                      border: 1px solid #DDDDDD;
                                                      color: inherit;
                                                      float: left;
                                                      line-height: 1.42857;
                                                      margin-left: -1px;
                                                      padding: 4px 10px;
                                                      position: relative;
                                                      text-decoration: none;">
                                    </li>

                                    <li><a href="#"><i class="fa fa-fw fa-refresh"></i></a></li>
                                </ul>
                            </div>

                            <div class="row ibox">
                                <div class="col-lg-12 col-md-12">
                                  
<div class="col-lg-3 col-md-3">                          
                                  
<div id="tree" >
	
</div>

 <script type="text/javascript">
 window.onload = select;
 var cot =  0;
 var insertornot = false;
 var Uinsertornot = false;
 
	 function select(){

        	Uinsertornot = false;
        	insertornot = false;
        	$('#nodeLINKlist').empty();
        	$('#nodeLINKlistU').empty();
        	$('#insert_color').empty();
        	$('#update_color').empty();
		 
		  $.ajax({                              
	          url:"/IMWebSocket/RESTful/Select_commonlink",
	          
		         data:{
		        	 },
		            
		         type : "POST",                                                                    
		         dataType:'json',
		         
		         error:function(e){                                                                 
		         alert("失敗");
		         callback(data);
		         },
		         success:function(data){
		        	 $('#tree').jstree("destroy").empty();
		        	 console.log("Tree",data);
		        	 
		        	 $('#tree').jstree({
		 				'core' : {
		 					'data' : data.Tree,	
		 				},
		 				
		        	 "plugins" : [ "themes", "json_data","ui"],
		        	
		 			}).bind("loaded.jstree", function (event, data) {
		 		        $(this).jstree("open_all")});
		 			
// 		 			$('#tree').bind('select_node.jstree', function(e,data) { 
// 		 			    window.location.href = data.instance.get_node(data.node, true).children('a').attr('href');
// 		 			});

		        		$("#tree").on("select_node.jstree",
			 				     function(evt, data){
			 				         $('#name').text(data.node.original.text);
			 				         $('#delete_name').text("名稱: "+data.node.original.text);
			 				         $('#Update_myModalLabel').text("更新的節點名稱: "+data.node.original.text);
			 				         $('#INlist').text(data.node.original.id);
			 				     	 $('#UPlist').text(data.node.original.id);
			 				    	 $('#url').text(data.node.original.a_attr.href);
		 				        	 $('#path').text(data.instance.get_path(data.selected[0],"/",0));
		 				       	 	 $('#createuser').text(data.node.original.createuser);
		 				  	  		 $('#delete_number').text("編號:"+data.node.original.id);
	 				         		 $('#pass').text(data.node.original.id);
	 				        		 $('#type').text(data.node.original.parent);
	 				        		 $('#children').text(data.node.children_d);
			 				     }); 
		        	
		        	 	$('#tree').on("select_node.jstree", function(e, data) {
		        	 		console.log("QQ",data.node);
		        	 		console.log("children_d.length",data.node.children_d.length);
		        	 		cot = data.node.parents.length;
		        	 		console.log("parents.length",data.node.parents.length);
		        	 		Upcot = data.node.children_d.length;
		        	 		console.log("id",data.node.id);
		        	 		console.log("text",data.node.text);
		        	 		
		        	 		if(cot<=3){
		        	 			insertornot = true;
		        	 	//		alert("Insert");
			        	 	}
		        	 		if(Upcot==0){
		        	 			Uinsertornot = true;
		        	 	//		alert("Update");
		        	 		}
		        	 	
		        	 	});
		        		var oo1 = "<option value="+0+">#</option>";
		        		var one = "<option  id='UPlist'></option>";
		        		var two = "<option  id='INlist'></option>";
		        		var color ="<option>blue</option><option>yellow</option><option>red</option>";
		        		document.getElementById("insert_color").insertAdjacentHTML("BeforeEnd",color);
		        		document.getElementById("update_color").insertAdjacentHTML("BeforeEnd",color);
		        		document.getElementById("nodeLINKlist").insertAdjacentHTML("BeforeEnd",one);
	        	 		document.getElementById("nodeLINKlistU").insertAdjacentHTML("BeforeEnd",two);
		         	 	for(var i=0 ;i<=data.count-1;i++){
		         	 		
		         	 		
		        	 		 var str= "<option value='"+data.Tree[i].id+"'>"+data.Tree[i].text+"</option>"; 
		        	 		document.getElementById("nodeLINKlist").insertAdjacentHTML("BeforeEnd",str);
		        	 		document.getElementById("nodeLINKlistU").insertAdjacentHTML("BeforeEnd",str);
		        	 		
		        	 	}
		        	 	document.getElementById("nodeLINKlist").insertAdjacentHTML("BeforeEnd",oo1);
	        	 		document.getElementById("nodeLINKlistU").insertAdjacentHTML("BeforeEnd",oo1);
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
		 <script type="text/javascript">
		 function INSERT(){
			 var a = 0;
			 if(insertornot){
				 	var nodeLINK = document.getElementById('nodeLINKlist').value;
			 		var nodeNAME = document.getElementById('nodeNAME').value;
			 		var nodeURL =  document.getElementById('nodeURL').value;
			 		var nodeUSER = document.getElementById('nodeUSER').value;
			 		var color = document.getElementById('insert_color').value;
			 	    var myMap = new Map();

			 	   $.ajax({                              
				          url:"RESTful/Insert_commonlink",
				          
					         data:{
			 				    	 "parnetid":nodeLINK,
			 				    	 "nodetext":nodeNAME,
			 				    	 "nodeurl":nodeURL,
			 				    	 "createuserid":nodeUSER,
			 				    	 "color":color
			 				   	 }, 
					         type : "POST",                                                                    
					         dataType:'json',
					         
					         error:function(e){                                                                 
					         alert("失敗");
					         callback(data);
					         },
					         success:function(data){
					        	select();					        
					         }
					         });
			 }else if(document.getElementById('nodeLINKlist').value==""){
				 	var nodeLINK = "0"
			 		var nodeNAME = document.getElementById('nodeNAME').value;
			 		var nodeURL =  document.getElementById('nodeURL').value;
			 		var nodeUSER = document.getElementById('nodeUSER').value;
			 		var color = document.getElementById('insert_color').value;
			 	    var myMap = new Map();

			 	   $.ajax({                              
				          url:"RESTful/Insert_commonlink",
				          
					         data:{
			 				    	 "parnetid":nodeLINK,
			 				    	 "nodetext":nodeNAME,
			 				    	 "nodeurl":nodeURL,
			 				    	 "createuserid":nodeUSER,
			 				    	 "color":color
			 				   	 }, 
					         type : "POST",                                                                    
					         dataType:'json',
					         
					         error:function(e){                                                                 
					         alert("失敗");
					         callback(data);
					         },
					         success:function(data){
					        	select();					        
					         }
					         });
			 }else{
				 alert("不可超過第四層");
			 } 
			 
		 	}
		
		 </script>
		 
		  <script type="text/javascript">
		 function UPDATE(){	
			 
			 var a=0;
			 if(Uinsertornot){
					var nodeIDu = document.getElementById('pass').innerHTML;
					var nodeLINKu = document.getElementById('nodeLINKlistU').value;
			 		var nodeNAMEu = document.getElementById('nodeNAMEu').value;
			 		var nodeURLu  = document.getElementById('nodeURLu').value;
			 		var nodeUSERu = document.getElementById('nodeUSERu').value;
			 		var color = document.getElementById('update_color').value;
			 	    var myMap = new Map();

			 		  $.ajax({                              
				          url:"RESTful/Update_commonlink",
				          
					         data:{
					        	 "parnetid":nodeLINKu,
						    	 "nodetext":nodeNAMEu,
						    	 "nodeurl":nodeURLu,
						    	 "createuserid":nodeUSERu,
					 			 "nodeid":nodeIDu,
					 			 "color":color
			 				   	 }, 
					         type : "POST",                                                                    
					         dataType:'json',
					         
					         error:function(e){                                                                 
					         alert("失敗");
					         callback(data);
					         },
					         success:function(data){
					        	select();
					         }
			 		 
			 		  });
			 }else{
				 alert("有子節點無法移動");
			 }
		
	 		 
	 		 
		 }
		 </script>
		 
		 <script type="text/javascript">
		 function DELETE(){	
			 var a = 0;
			var nodeIDd = document.getElementById('pass').innerHTML;
			var children_list =  document.getElementById('children').innerHTML;
				
	 	    var myMap = new Map();
	 	
	 		$.ajax({                              
	   	          url:"/IMWebSocket/RESTful/Delete_commonlink",
	   	          
	   		         data:{
	   		        	"nodeid":nodeIDd,
	   		        	"children_list":children_list
	   		        	 }, 
	   		         type : "POST",                                                                    
	   		         dataType:'json',
	   		         
	   		         error:function(e){                                                                 
	   		         alert("失敗");
	   		         callback(data);
	   		         },
	   		      success:function(data){
			        	select();
			         }
	 		});
	 	
	 		 
		 }
		 </script>
		 </div>
		 
		 
	   <div class="col-lg-9 col-md-9">
	   
	   <!-- INSERT -->
<button type="button" class="btn btn-primary btn-lg" data-toggle="modal" data-target="#myModal" style="">
新增
</button>

<!-- Modal -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
        <h4 class="modal-title" id="Insert_myModalLabel">建立新的節點</h4>
      </div>
      <div class="modal-body">
     
       <label >名稱:</label>
      <input class="form-control" id="nodeNAME" placeholder="請輸入節點名稱"><br>
       <label>連結:</label>
      <input class="form-control" id="nodeURL" placeholder="請輸入URL"><br>
       <label>建立者名稱:</label>
      <input class="form-control" id="nodeUSER" placeholder="請輸入建立者名稱"><br>
    
     <label for="nodeLINKlist">關聯節點:</label><br>
      <select  id="nodeLINKlist">	
      </select><br>
       <label for="insert_color">節點顏色:</label><br>
      <select  id="insert_color">
      	
      </select>
      
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal" onclick="INSERT()">建立</button>
      </div>
    </div>
  </div>
</div>
	  
<!-- UPDATE	   -->
<button type="button" class="btn btn-primary btn-lg" data-toggle="modal" data-target="#UPDATE">
 更新
</button>
<!-- Modal -->
<div class="modal fade" id="UPDATE" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
        <h4 class="modal-title" id="Update_myModalLabel" >更新節點:</h4>
      </div>
      <div class="modal-body">

       <label >名稱:</label>
      <input type="" class="form-control" id="nodeNAMEu" placeholder="請輸入節點名稱"><br>
       <label>連結:</label>
      <input type="" class="form-control" id="nodeURLu" placeholder="請輸入URL"><br>
       <label>建立者名稱:</label>
      <input type="" class="form-control" id="nodeUSERu" placeholder="請輸入建立者名稱"><br>

      <label for="nodeLINKlistU">關聯節點:</label><br>
      <select  id="nodeLINKlistU">

      </select><br>
       <label for="update_color">節點顏色:</label><br>
      <select  id="update_color">

      </select>
   
 

      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal" onclick="UPDATE()">更新</button>
      </div>
    </div>
  </div>
</div>	



<!-- DELETE	   -->
<button type="button" class="btn btn-primary btn-lg" data-toggle="modal" data-target="#DELETE" >
 刪除
</button>
<!-- Modal -->
<div class="modal fade" id="DELETE" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
        <h4 class="modal-title" id="myModalLabel">刪除節點:</h4>
      </div>
      <div class="modal-body">
   <h1>ps.相關聯子節點全數刪除</h1><br>
   <h3 id="delete_name">名稱:</h3>
   <h3 id="delete_number">編號:</h3>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal" onclick="DELETE()">刪除</button>
      </div>
    </div>
  </div>
</div>	



	<div id="demo">
      	 <table class="table table-bordered table-hover">
                  <thead>
                       <tr>
                            <td>名稱</td>
                            <td>編號</td>
                            <td>路徑</td>
                            <td>相關節點</td>
                            <td>狀態</td>
                            <td>URL</td>
                            <td>建立者</td>
                       </tr>
                  </thead>
      		<tbody>
      										<tr>
                                                <td id="name"></td>
                                                <td id="pass"></td>
                                                <td id="path"></td>
                                                <td id="children"></td>
                                                <td id="type"></td>
                                                <td id="url"></td>
                                              	<td id="createuser"></td>
                                            </tr>
      			
      		</tbody>
      	</table>
       </div>
                    </div>
                            </div>
                        </div>

                        <div id="banContent" style="display:none;">
                        
                            <div>
                                <ul class="pagination">
                                    <li onclick="showAddMember()"><a href="#"><i class="fa fa-fw fa-plus"></i></a></li>
                                    <li onclick="unlockAccount()"><a href="#"><i class="fa fa-fw fa-unlock-alt"></i></a></li>
                                    <li onclick="confirmBan()"><a href="#"><i class="fa fa-fw fa-ban"></i></a></li>
                                </ul>

                                <ul class="pagination" style="float:right;" >
                                    <li>
                                        <input type="text" id="banSearch" placeholder="搜索"
                                               style="background-color: #FFFFFF;
                                                      border: 1px solid #DDDDDD;
                                                      color: inherit;
                                                      float: left;
                                                      line-height: 1.42857;
                                                      margin-left: -1px;
                                                      padding: 4px 10px;
                                                      position: relative;
                                                      text-decoration: none;">
                                    </li>

                                    <li><a href="#"><i class="fa fa-fw fa-refresh"></i></a></li>
                                </ul>
                            </div>

                            <div class="row ibox">
                                <div class="col-lg-12 col-md-12">
                                
	 <div class="col-lg-3 col-md-3">
<div id="container"></div>


<script>
$(function() {
  $('#container').jstree({
    'core' : {
      'data' : [
          {
              "text" : "Root node","state" : {"opened" : true },"icon" : "//jstree.com/tree.png",
              
              "children" : [
                  {"text" : "Child node 1","icon" : "103.png"},
                  
                  { "text" : "Child node 2", "state" : { "disabled" : true } ,"icon" : "103.png"}
              ]
        }
      ]
    }
  });
});
</script>           
      </div>
      
      
                                </div>
                            </div>
                        </div>

                        <div id="addMemberContent" style="display:none;">
                            <div class="widget">
                                <form class="form-horizontal">
                                    <div class="form-group col-sm-6">
                                        <label for="inputAccount" class="col-sm-2 control-label required">帳號</label>
                                        <div class="col-sm-8">
                                            <input type="text" class="form-control" id="inputAccount" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputName" class="col-sm-2 control-label required">姓名</label>
                                        <div class="col-sm-8">
                                            <input type="text" class="form-control" id="inputName" placeholder="">
                                        </div>
                                    </div>

                                    <div class="form-group col-sm-6">
                                        <label for="inputLastName" class="col-sm-2 control-label">姓氏</label>
                                        <div class="col-sm-8">
                                            <input type="text" class="form-control" id="inputLastName" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputFirstName" class="col-sm-2 control-label">名字</label>
                                        <div class="col-sm-8">
                                            <input type="text" class="form-control" id="inputFirstName" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputEmployNumber" class="col-sm-2 control-label">員工編號</label>
                                        <div class="col-sm-8">
                                            <input type="number" class="form-control" id="inputEmployNumber" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputPhoneNumber" class="col-sm-2 control-label">分機號碼</label>
                                        <div class="col-sm-8">
                                            <input type="number" class="form-control" id="inputPhoneNumber" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputPassword" class="col-sm-2 control-label required">密碼</label>
                                        <div class="col-sm-8">
                                            <input type="password" class="form-control" id="inputPassword" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputConfirmPassword" class="col-sm-2 control-label required">確認密碼</label>
                                        <div class="col-sm-8">
                                            <input type="password" class="form-control" id="inputConfirmPassword" placeholder="">
                                        </div>
                                    </div>


                                    <div class="form-group col-sm-12">
                                        <label for="inputEmail" class="col-sm-1 control-label">Email</label>
                                        <div class="col-sm-9">
                                            <input type="email" class="form-control" id="inputEmail" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-12">
                                        <label for="inputDeskPhone" class="col-sm-1 control-label">分機</label>
                                        <div class="col-sm-4">
                                            <input type="number" class="form-control" id="inputDeskPhone" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-12">
                                        <label for="inputDepartment" class="col-sm-1 control-label">所屬部門</label>
                                        <div class="col-sm-4">
                                            <input type="text" class="form-control" id="inputDepartment" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-12">
                                        <label for="inputAssignedDepartment" class="col-sm-1 control-label">分派部門</label>
                                        <div class="col-sm-4">
                                            <input type="text" class="form-control" id="inputAssignedDepartment" placeholder="">
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-sm-offset-9 col-sm-3">
                                            <button class="btn btn-primary" onclick="sendAddMember()">儲存</button>
                                            <button class="btn btn-default">取消</button>
                                        </div>
                                    </div>


                                </form>
                            </div>
                        </div>

                        <div id="editMemberContent" style="display:none;">
                            <div class="widget">
                                <form class="form-horizontal">
                                    <div class="form-group col-sm-6">
                                        <label for="inputAccount" class="col-sm-2 control-label required">帳號</label>
                                        <div class="col-sm-8">
                                            <input type="text" class="form-control" id="inputAccount" value="HolyLin" readonly>
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputName" class="col-sm-2 control-label required">姓名</label>
                                        <div class="col-sm-8">
                                            <input type="text" class="form-control" id="inputName" placeholder="">
                                        </div>
                                    </div>

                                    <div class="form-group col-sm-6">
                                        <label for="inputLastName" class="col-sm-2 control-label">姓氏</label>
                                        <div class="col-sm-8">
                                            <input type="text" class="form-control" id="inputLastName" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputFirstName" class="col-sm-2 control-label">名字</label>
                                        <div class="col-sm-8">
                                            <input type="text" class="form-control" id="inputFirstName" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputEmployNumber" class="col-sm-2 control-label">員工編號</label>
                                        <div class="col-sm-8">
                                            <input type="number" class="form-control" id="inputEmployNumber" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputPhoneNumber" class="col-sm-2 control-label">分機號碼</label>
                                        <div class="col-sm-8">
                                            <input type="number" class="form-control" id="inputPhoneNumber" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputPassword" class="col-sm-2 control-label required">密碼</label>
                                        <div class="col-sm-8">
                                            <input type="password" class="form-control" id="inputPassword" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputConfirmPassword" class="col-sm-2 control-label required">確認密碼</label>
                                        <div class="col-sm-8">
                                            <input type="password" class="form-control" id="inputConfirmPassword" placeholder="">
                                        </div>
                                    </div>


                                    <div class="form-group col-sm-12">
                                        <label for="inputEmail" class="col-sm-1 control-label">Email</label>
                                        <div class="col-sm-9">
                                            <input type="email" class="form-control" id="inputEmail" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-12">
                                        <label for="inputDeskPhone" class="col-sm-1 control-label">分機</label>
                                        <div class="col-sm-4">
                                            <input type="number" class="form-control" id="inputDeskPhone" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-12">
                                        <label for="inputDepartment" class="col-sm-1 control-label">所屬部門</label>
                                        <div class="col-sm-4">
                                            <input type="text" class="form-control" id="inputDepartment" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-12">
                                        <label for="inputAssignedDepartment" class="col-sm-1 control-label">分派部門</label>
                                        <div class="col-sm-4">
                                            <input type="text" class="form-control" id="inputAssignedDepartment" placeholder="">
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-sm-10">
                                            <div class="checkbox">
                                                <span class="label label-primary" style="margin-right:10px;">
                                                    帳戶狀態：<span>正常</span>
                                                </span>

                                                <label class="">
                                                    <input type="checkbox">停用帳戶
                                                </label>
                                                <label class="">
                                                    <input type="checkbox">解除鎖定
                                                </label>
                                            </div>
                                        </div>
                                    </div>



                                    <div class="form-group">
                                        <div class="col-sm-offset-9 col-sm-3">
                                            <button class="btn btn-primary" onclick="sendEditMember()">儲存</button>
                                            <button class="btn btn-default">取消</button>
                                        </div>
                                    </div>


                                </form>
                            </div>
                        </div>


                    </div>
                    <!-- 設定頁面內容頁 End-->
                </div>
            </div>
        </div>
    </body>

    <!-- 彈跳對話視窗-->
    <!-- Modal -->
    <!-- Trigger the modal with a button -->
    <button type="button" class="btn btn-info btn-lg" data-toggle="modal" data-target="#confirmBan" style="display:none;" id="confirmBanButton">banDialog</button>

    <div id="confirmBan" class="modal fade" role="dialog">
        <div class="modal-dialog">

            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-body">
                    <h3>是否確定停用帳號？</h3>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-success" data-dismiss="modal" onclick="showToastSuccess('停用成功')">確定</button>
                    <button type="button" class="btn btn-danger" data-dismiss="modal">取消</button>
                </div>
            </div>

        </div>
    </div>

    <!-- Trigger the modal with a button -->
    <button type="button" class="btn btn-info btn-lg" data-toggle="modal" data-target="#unlockModal" style="display:none;" id="unlockButton">unlockDialog</button>

    <div id="unlockModal" class="modal fade" role="dialog">
        <div class="modal-dialog">

            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-body">
                    <h3>是否解除鎖定帳號？</h3>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-success" data-dismiss="modal" onclick="showToastError('鎖定失敗')">確定</button>
                    <button type="button" class="btn btn-danger" data-dismiss="modal">取消</button>
                </div>
            </div>

        </div>
    </div>

    <!-- 全局js -->
  
    <script src="js/bootstrap.min.js?v=3.3.6"></script>

    <!-- Data Tables -->
    <script src="js/plugins/dataTables/jquery.dataTables.js"></script>
    <script src="js/plugins/dataTables/dataTables.bootstrap.js"></script>

    <!-- DataPicker -->
    <script src="js/plugins/datapicker/bootstrap-datepicker.js"></script>

    <!-- toastStr -->
    <script src="js/plugins/toastr/toastr.min.js"></script>

    <!-- layui -->
    <script src="layui/layui.js"></script>

    <script>
        $(document).ready(function(){
            //init datatable
            $("#manageTable").DataTable();
            $("#manageTable").css("width","100%");
            $("#manageTable_filter").prop("style","float:right;");
            $("#manageTable_wrapper div:nth-child(1)").hide();

            $("#manageSearch").keyup(function(){
                var searchText = $("#manageSearch").val();

                $("input[aria-controls='manageTable']").val(searchText);
                $("input[aria-controls='manageTable']").trigger("keyup");
            });


            $("#banTable").DataTable();
            $("#banTable").css("width","100%");
            $("#banTable_filter").prop("style","float:right;");
            $("#banTable_wrapper div:nth-child(1)").hide();

            $("#banSearch").keyup(function(){

                var searchText = $("#banSearch").val();

                $("input[aria-controls='banTable']").val(searchText);
                $("input[aria-controls='banTable']").trigger("keyup");
            });
        });

        $("#manageTable tbody tr td,#banTable tbody tr td").on("click",function(){
            var text = $(this).text();

            if (text && text != "") {
                showEditMember();
            }
        });

        function showManage() {
            closeAllHrContent();
            $("#manageContent").show();

            $("button.manage").removeClass("btn-success");
            $("button.manage").addClass("btn-primary");
        }

        function showBan() {
            closeAllHrContent();
            $("#banContent").show();

            $("button.ban").removeClass("btn-success");
            $("button.ban").addClass("btn-primary");
        }

        function showAddMember() {
            closeAllHrContent();
            $("#addMemberContent").show();

            $("button.addMember").show();
            $("button.addMember").removeClass("btn-success");
            $("button.addMember").addClass("btn-primary");
        }

        function showEditMember() {
            closeAllHrContent();
            $("#editMemberContent").show();

            $("button.editMember").show();
            $("button.editMember").removeClass("btn-success");
            $("button.editMember").addClass("btn-primary");
        }

        function sendAddMember() {
            if (!validateAddMember()) {
                return;
            }
            
            closeAddMember();
            showToastSuccess("新增成功");
        }

        function sendEditMember() {
            if (!validateEditMember()) {
                return;
            }
            
            closeEditMember();
            showToastSuccess("修改成功");
        }

        function closeAddMember() {
            closeAllHrContent();
            $("button.addMember").hide();

            $("#manageContent").show();
            $("button.manage").removeClass("btn-success");
            $("button.manage").addClass("btn-primary");
        }

        function closeEditMember() {
            closeAllHrContent();
            $("button.editMember").hide();

            $("#manageContent").show();
            $("button.manage").removeClass("btn-success");
            $("button.manage").addClass("btn-primary");
        }

        function closeAllHrContent() {
            $("#manageContent").hide();
            $("#banContent").hide();
            $("#addMemberContent").hide();
            $("#editMemberContent").hide();

            $("#hrTabControlButton button").removeClass("btn-primary");
            $("#hrTabControlButton button").addClass("btn-success");
        }

        // 解鎖對話視窗
        function unlockAccount() {
            $("#unlockButton").trigger("click");

        }

        // 停用對話視窗
        function confirmBan() {
            $("#confirmBanButton").trigger("click");
        }

        /*show toastStr*/
        toastr.options = {
            "closeButton": false,
            "debug": false,
            "progressBar": true,
            "positionClass": "toast-top-right",
            "onclick": null,
            "showDuration": "2000",
            "timeOut": "2000",
            "showEasing": "swing",
            "hideEasing": "linear",
            "showMethod": "fadeIn",
            "hideMethod": "fadeOut"
        }

        function showToastSuccess(message) {
            toastr.success(message);
        }

        function showToastError(message) {
            toastr.error(message);
        }

        function validateAddMember() {
            var account = $("#inputAccount", "#addMemberContent" ).val();
            var name = $("#inputName", "#addMemberContent").val();
            var passowrd = $("#inputPassword", "#addMemberContent").val();
            var confirmPassword = $("#inputConfirmPassword", "#addMemberContent").val();
            var email = $("#inputEmail", "#addMemberContent").val();

            if (!account || account == '') {
                toastr.error("請輸入帳號");
                return false;
            }

            if (!name || name == '') {
                toastr.error("請輸入姓名");
                return false;
            }

            if (!passowrd || passowrd == '') {
                toastr.error("請輸入密碼");
                return false;
            }

            if (email != '' && !isValidEmail(email)) {
                toastr.error("請輸入正確的Email格式");
                return false;
            }

            if (!confirmPassword || confirmPassword == '') {
                toastr.error("請輸入確認密碼");
                return false;
            }

            if (passowrd != confirmPassword) {
                toastr.error("密碼與確認密碼不同，請重新輸入");
                return false;
            }

            return true;
        }

        function validateEditMember() {
            var account = $("#inputAccount", "#editMemberContent" ).val();
            var name = $("#inputName", "#editMemberContent").val();
            var passowrd = $("#inputPassword", "#editMemberContent").val();
            var confirmPassword = $("#inputConfirmPassword", "#editMemberContent").val();
            var email = $("#inputEmail", "#editMemberContent").val();

            if (!name || name == '') {
                toastr.error("請輸入姓名");
                return false;
            }

            if (!passowrd || passowrd == '') {
                toastr.error("請輸入密碼");
                return false;
            }

            if (!confirmPassword || confirmPassword == '') {
                toastr.error("請輸入確認密碼");
                return false;
            }

            if (email != '' && !isValidEmail(email)) {
                toastr.error("請輸入正確的Email格式");
                return false;
            }

            if (passowrd != confirmPassword) {
                toastr.error("密碼與確認密碼不同，請重新輸入");
                return false;
            }

            return true;
        }

        function isValidEmail(email) {
            var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

            return re.test(email);
        }
    </script>
    
    
    <script type="text/javascript">
    
   
    
    </script>
    
    
</html>


