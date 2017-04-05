<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

    <head>

        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">


        <title>「設定」頁面</title>

       <script src="js/jquery.min.js"></script>
        <link href="css/bootstrap.min.css?v=3.3.6" rel="stylesheet">
        <link href="css/font-awesome.css?v=4.4.0" rel="stylesheet">
        <link href="css/animate.css" rel="stylesheet">
        <link href="layui/css/layui.css" rel="stylesheet">
        <link href="css/plugins/datapicker/datepicker3.css" rel="stylesheet">
        <link href="css/style.css?v=4.1.0" rel="stylesheet">
       
         
		<link href="css/plugins/bootstrap-table/bootstrap-table.min.css" rel="stylesheet">
        <link href="css/plugins/toastr/toastr.min.css" rel="stylesheet">
        <style>
            label.required:after {content: " *"; color: red;}
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
                            <button class="btn-sm btn-primary manage" onclick="showManage()"><i class="fa fa-fw fa-user"></i>啟用列表</button>
                            <button class="btn-sm btn-success ban" onclick="showBan()"><i class="fa fa-fw fa-user"></i>停用列表</button>
                            <button class="btn-sm btn-success addMember" style="display:none;"><span onclick="showAddMember()">新增人員</span> <i class="fa fa-times" onclick="closeAddMember()"></i></button>
                            <button class="btn-sm btn-success editMember" style="display:none;"><span onclick="showEditMember()">更新人員</span> <i class="fa fa-times" onclick="closeEditMember()"></i></button>
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
                                <div class="col-lg-12 col-md-12" id="manageTable_div">
                                
   <table  id="manageTable"
       data-pagination="true"
       data-search="true"
       data-click-to-select="true"
  	   data-toolbar="#toolbar"
		 >  
		<thead>
				<tr >
		 	 		<th id="statusname" data-sortable="true"></th>
			 		<th id="statusname_cn" data-sortable="true"></th>
			 		<th id="statusname_en" data-sortable="true"></th>
			 		<th id="statusname_tw" data-sortable="true"></th>
			 		<th id="description" data-sortable="true"></th>
			 		<th id="alarmduration" data-sortable="true"></th>
			 		<th id="alarmcolor" data-sortable="true"></th>
					<th id="flag" data-sortable="true"></th>
        		</tr>
      </thead>
</table>
   
                 
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
                                <div class="col-lg-12 col-md-12" id="banTable_div">
                                
   <table  id="banTable"
       data-pagination="true"
       data-search="true"
       data-click-to-select="true"
  	   data-toolbar="#toolbar"
		 >  
		<thead>
				<tr >
				
					 <th id="user_name" data-sortable="true"></th>		 	
			 		 <th id="emailaddress" data-sortable="true"></th>
			 		 <th id="state" data-sortable="true"></th>
			 		 <th id="account" data-sortable="true"></th>
			 		 <th id="employee_id" data-sortable="true"></th>
			 		 <th id="dn" data-sortable="true"></th>
				</tr>
     </thead>
    
</table>
   
                                </div>
                            </div>
                        </div>
<!-- 新增 --> 
                        <div id="addMemberContent" style="display:none;">
                            <div class="widget">
<!--                                  <form class="form-horizontal"> -->
                                    <div class="form-group col-sm-6">
                                        <label for="inputAccount" class="col-sm-2 control-label">狀態名:</label>
                                        <div class="col-sm-8">
                                            <input type="text" class="form-control" id="Insert_statusname" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputAccount" class="col-sm-2 control-label">狀態名_CN:</label>
                                        <div class="col-sm-8">
                                            <input type="text" class="form-control" id="Insert_statusname_cn" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputAccount" class="col-sm-2 control-label">狀態名_EN:</label>
                                        <div class="col-sm-8">
                                            <input type="text" class="form-control" id="Insert_statusname_en" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputAccount" class="col-sm-2 control-label">狀態名_TW:</label>
                                        <div class="col-sm-8">
                                            <input type="text" class="form-control" id="Insert_statusname_tw" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputAccount" class="col-sm-2 control-label">告警顏色:</label>
                                        <div class="col-sm-8">
                                            <input type="text" class="form-control" id="Insert_alarmcolor" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputAccount" class="col-sm-2 control-label">告警時間:</label>
                                        <div class="col-sm-8">
                                            <input type="text" class="form-control" id="Insert_alarmduration" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputAccount" class="col-sm-2 control-label">註解:</label>
                                        <div class="col-sm-8">
                                            <input type="text" class="form-control" id="Insert_description" placeholder="">
                                        </div>
                                    </div>
                                     <div class="form-group col-sm-6">
                                        <label for="inputAccount" class="col-sm-2 control-label">建立者:</label>
                                        <div class="col-sm-8">
                                            <input type="text" class="form-control" id="Insert_createuserid" placeholder="">
                                        </div>
                                    </div>
                                     <div class="form-group col-sm-6">
                                        <label for="inputConfirmPassword" class="col-sm-2 control-label">啟用狀態:</label>
                                        <div class="col-sm-8">
                                            <select class="form-control" id="Insert_flag">
      											<option value="0">啟用</option>
	  											<option value="1">停用</option>
      										</select>
                                        </div>
                                    </div>
                                      
                                    <div class="form-group">
                                        <div class="col-sm-offset-9 col-sm-3">
                                            <button class="btn btn-primary" onclick="sendAddMember()">儲存</button>
                                            <button class="btn btn-default">取消</button>
                                        </div>
                                    </div>


<!--                                 </form> -->

                            </div>
                        </div>
<!-- 新增END --> 
                       
<!-- 更新 -->
                       <div id="editMemberContent" style="display:none;">
                            <div class="widget">
									<div class="form-group col-sm-6">
                                        <label for="inputAccount" class="col-sm-2 control-label required">DBID</label>
                                        <div class="col-sm-8">
                                            <input type="number" class="form-control" id="Update_id" >
                                        </div>
                                    </div>			
                                    <div class="form-group col-sm-6">
                                        <label for="inputAccount" class="col-sm-2 control-label">告警顏色:</label>
                                        <div class="col-sm-8">
                                            <input type="text" class="form-control" id="alarmcolor" >
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputName" class="col-sm-2 control-label">告警時間:</label>
                                        <div class="col-sm-8">
                                            <input type="text" class="form-control" id="alarmduration" placeholder="">
                                        </div>
                                    </div>

                                    <div class="form-group col-sm-6">
                                        <label for="inputLastName" class="col-sm-2 control-label">註解:</label>
                                        <div class="col-sm-8">
                                            <input type="text" class="form-control" id="description" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputFirstName" class="col-sm-2 control-label">狀態名:</label>
                                        <div class="col-sm-8">
                                            <input type="text" class="form-control" id="statusname" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputEmployNumber" class="col-sm-2 control-label">狀態名_CN:</label>
                                        <div class="col-sm-8">
                                            <input type="text" class="form-control" id="statusname_cn" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputPhoneNumber" class="col-sm-2 control-label">狀態名_EN:</label>
                                        <div class="col-sm-8">
                                            <input type="text" class="form-control" id="statusname_en" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputPassword" class="col-sm-2 control-label">狀態名_TW:</label>
                                        <div class="col-sm-8">
                                            <input type="text" class="form-control" id="statusname_tw" placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="inputConfirmPassword" class="col-sm-2 control-label">啟用狀態:</label>
                                        <div class="col-sm-8">
                                            <select class="form-control" id="Update_flag">
      											<option value="0">啟用</option>
	  											<option value="1">停用</option>
      										</select>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-sm-offset-9 col-sm-3">
                                            <button class="btn btn-primary" onclick="sendEditMember()">更新</button>
                                            <button class="btn btn-default">取消</button>
                                        </div>
                                    </div>

                            </div>
                        </div>
<!-- 更新END -->

                    </div>
                    <!-- 設定頁面內容頁 End-->
                </div>
            </div>
        </div>
        <input type='hidden' id="systemParam" value='${systemParam}' disabled>
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
                <div class="form-group col-sm-6">
                 <label for="inputAssignedDepartment" class="col-sm-2 control-label">ID:</label>
                 <div class="col-sm-4">
                    <input type="text" class="form-control" id="D_id" placeholder="">
                 </div>
                 </div>
                    <button type="button" class="btn btn-success" data-dismiss="modal" onclick="showToastSuccess('刪除成功')">確定</button>
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
                    <h3>是否解除/鎖定帳號？</h3>
                </div>
                <div class="modal-footer">
                 <div class="form-group col-sm-6">
                 <label for="inputAssignedDepartment" class="col-sm-2 control-label">ID:</label>
                 <div class="col-sm-4">
                    <input type="text" class="form-control" id="Delete_id" placeholder="">
                 </div>
                 </div>
                 <div class="form-group col-sm-6">
                <label for="inputAssignedDepartment" class="col-sm-2 control-label">Flag:</label>
                 <div class="col-sm-4">
                    <select class="form-control" id="Delete_flag">
      					<option value="0">啟用</option>
	  					<option value="1">停用</option>
      				</select>
                 </div>
                </div>
                 
                    <button type="button" class="btn btn-success" data-dismiss="modal" onclick="showToastError('鎖定/解鎖成功')">確定</button>
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
    
    <script src="js/plugins/bootstrap-table/bootstrap-table.min.js"></script>			  <!-- TABLE相關 -->
    <script src="js/plugins/bootstrap-table/bootstrap-table-mobile.min.js"></script>	  <!-- TABLE相關 -->
 	<script src="js/demo/bootstrap-table-demo.js"></script>

		
    <script>
    
    function play(callback) {
    	
   	 function state(){
   		
   			  $.ajax({                              
   		          url:"/IMWebSocket/RESTful/Select_agentreason",
   			         data:{
   			        	 flag:0
   			        	 },
   			            
   			         type : "POST",                                                                    
   			         dataType:'json',
   			         
   			         error:function(e){                                                                 
   			         alert("失敗");
   			         callback(data);
   			         },
   			         success:function(data){
   			        	 $('#manageTable tr').empty();
   	   		        	 $("#manageTable_div").html($('#manageTable').bootstrapTable({
   			         columns: [{
   			           
   			             field: 'statusname',
   			             title: '狀態名'
   			         }, {
   			             field: 'statusname_cn',
   			             title: '狀態名_CN'
   			         },{
   			             field: 'statusname_en',
   			             title: '狀態名_EN'
   			         },
   			         {
   			             field: 'statusname_tw',
   			             title: '狀態名_TW'
   			         },
   			         {
   			             field: 'description',
   			             title: '註解'
   			         },
   			         {
   			             field: 'alarmduration',
   			             title: '告警時間'
   			         }, {
   			             field: 'alarmcolor',
   			             title: '告警顏色'
   			         }, {
   			             field: 'flag',
   			             title: 'flag'
   			         }],
   			     	data:data.agentstatus
   			     	}));"json"
   			     	
   		     		console.log("Open",data);

   		     	  $.ajax({                              
   	   		          url:"/IMWebSocket/RESTful/Select_agentreason",
   	   			         data:{
   	   			        	 flag:1
   	   			        	 },
   	   			            
   	   			         type : "POST",                                                                    
   	   			         dataType:'json',
   	   			         error:function(e){                                                                 
   	   			         alert("失敗");
   	   			         callback(data);
   	   			         },
   	   			         success:function(data){
   	   			       		$('#banTable tr').empty();
     		        	 	$("#banTable_div").html($('#banTable').bootstrapTable({
   	   			         columns: [{
   	   			           
   	   			             field: 'statusname',
   	   			             title: '狀態名'
   	   			         }, {
   	   			             field: 'statusname_cn',
   	   			             title: '狀態名_CN'
   	   			         },{
   	   			             field: 'statusname_en',
   	   			             title: '狀態名_EN'
   	   			         },
   	   			         {
   	   			             field: 'statusname_tw',
   	   			             title: '狀態名_TW'
   	   			         },
   	   			         {
   	   			             field: 'description',
   	   			             title: '註解'
   	   			         },
   	   			         {
   	   			             field: 'alarmduration',
   	   			             title: '告警時間'
   	   			         }, {
   	   			             field: 'alarmcolor',
   	   			             title: '告警顏色'
   	   			         }, {
   	   			             field: 'flag',
   	   			             title: 'flag'
   	   			         }],
   	   			     	data:data.agentstatus
   	   			     	}));"json"
   	   			     	
   	   		     		console.log("Close",data);
   	   		     	callback();
   	   			     }
   	   			        
   	   			 }); 
   			     }
   			        
   			 }); 
   			  
   		 		};
   	 	state();
    	}
    	function play2() {
    	  $("#manageTable tbody tr,#banTable tbody tr").on("click",function(){
              var text = $(this).text();
              if (text && text != "") {
                  showEditMember();
              }
          });
    	}
    	play(play2);
    	
    
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
//             if (!validateAddMember()) {
//                 return;
//             }
            
            closeAddMember();
            
             var flag = document.getElementById('Insert_flag').value;
			 var alarmcolor = document.getElementById('Insert_alarmcolor').value;
			 var alarmduration = document.getElementById('Insert_alarmduration').value;
			 var description = document.getElementById('Insert_description').value;
			 var statusname = document.getElementById('Insert_statusname').value;
			 var statusname_cn = document.getElementById('Insert_statusname_cn').value;
			 var statusname_en = document.getElementById('Insert_statusname_en').value;
			 var statusname_tw = document.getElementById('Insert_statusname_tw').value;
			 var createuserid = document.getElementById('Insert_createuserid').value;
			
			 $.ajax({                              
		          url:"/IMWebSocket/RESTful/Insert_agentreason",
			         data:{
			        	 flag:flag,
			        	 alarmcolor:alarmcolor,
			        	 alarmduration:alarmduration,
			        	 description:description,
			        	 statusname:statusname,
			        	 statusname_cn:statusname_cn,
			        	 statusname_en:statusname_en,
			        	 statusname_tw:statusname_tw,
			        	 createuserid:createuserid
			        	 },
			            
			         type : "POST",                                                                    
			         dataType:'json',
			         
			         error:function(e){                                                                 
			         alert("失敗");
			         callback(data);
			         },
			         success:function(data){
			        	 play(play2);
			   		     }
			   		        
			   		 }); 
            
            showToastSuccess("新增成功");
        }

        function sendEditMember() {
        	
//             if (!validateEditMember()) {
            
//                 return;
//             }
            closeEditMember();
         
             var flag = document.getElementById('Update_flag').value;
			 var dbid = document.getElementById('Update_id').value;;
			 var alarmcolor = document.getElementById('alarmcolor').value;
			 var alarmduration = document.getElementById('alarmduration').value;
			 var description = document.getElementById('description').value;
			 var statusname = document.getElementById('statusname').value;
			 var statusname_cn = document.getElementById('statusname_cn').value;
			 var statusname_en = document.getElementById('statusname_en').value;
			 var statusname_tw = document.getElementById('statusname_tw').value;
		
			 $.ajax({                              
		          url:"/IMWebSocket/RESTful/Update_agentreason",
			         data:{
			        	 flag:flag,
			        	 dbid:dbid,
			        	 alarmcolor:alarmcolor,
			        	 alarmduration:alarmduration,
			        	 description:description,
			        	 statusname:statusname,
			        	 statusname_cn:statusname_cn,
			        	 statusname_en:statusname_en,
			        	 statusname_tw:statusname_tw
			        	 },
			            
			         type : "POST",                                                                    
			         dataType:'json',
			         
			         error:function(e){                                                                 
			         alert("失敗");
			         callback(data);
			         },
			         success:function(data){
			        	 play(play2);
			   		     }
			   		        
			   		 }); 

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
        	
        	var dbid = document.getElementById('D_id').value;
        	 
			 $.ajax({                              
		          url:"/IMWebSocket/RESTful/Delete_AgentReason",
			         data:{			        
			        	 dbid:dbid
			        	 },
			         type : "POST",                                                                    
			         dataType:'json',
			         error:function(e){                                                                 
			         alert("失敗");
			         callback(data);
			         },
			         success:function(data){
			        	 play(play2);
			        	
			     	}  
			 }); 
        	
            toastr.success(message);
        }

        function showToastError(message) {
        	
    			 var flag = document.getElementById('Delete_flag').value;
    			 var dbid = document.getElementById('Delete_id').value;
 
    			 $.ajax({                              
    		          url:"/IMWebSocket/RESTful/LogicDelete_agentreason",
    			         data:{
    			        	 flag:flag,
    			        	 dbid:dbid
    			        	 },
    			         type : "POST",                                                                    
    			         dataType:'json',
    			         error:function(e){                                                                 
    			         alert("失敗");
    			         callback(data);
    			         },
    			         success:function(data){
    			        	 play(play2);
    			        	
    			     	}  
    			 }); 
    			        
     
            toastr.error(message);
        }

//         function validateAddMember() {
//             var account = $("#inputAccount", "#addMemberContent" ).val();
//             var name = $("#inputName", "#addMemberContent").val();
//             var passowrd = $("#inputPassword", "#addMemberContent").val();
//             var confirmPassword = $("#inputConfirmPassword", "#addMemberContent").val();
//             var email = $("#inputEmail", "#addMemberContent").val();

//             if (!account || account == '') {
//                 toastr.error("請輸入帳號");
//                 return false;
//             }

//             if (!name || name == '') {
//                 toastr.error("請輸入姓名");
//                 return false;
//             }

//             if (!passowrd || passowrd == '') {
//                 toastr.error("請輸入密碼");
//                 return false;
//             }

//             if (email != '' && !isValidEmail(email)) {
//                 toastr.error("請輸入正確的Email格式");
//                 return false;
//             }

//             if (!confirmPassword || confirmPassword == '') {
//                 toastr.error("請輸入確認密碼");
//                 return false;
//             }

//             if (passowrd != confirmPassword) {
//                 toastr.error("密碼與確認密碼不同，請重新輸入");
//                 return false;
//             }

//             return true;
//         }

//         function validateEditMember() {
//         	var account = $("#updateAccount", "#editMemberContent" ).val();
//             var name = $("#updateName", "#editMemberContent").val();
//             var passowrd = $("#updatePassword", "#editMemberContent").val();
//             var confirmPassword = $("#updateConfirmPassword", "#editMemberContent").val();
//             var email = $("#updateEmail", "#editMemberContent").val();

//             if (!name || name == '') {
//                 toastr.error("請輸入姓名");
//                 return false;
//             }

//             if (!passowrd || passowrd == '') {
//                 toastr.error("請輸入密碼");
//                 return false;
//             }

//             if (!confirmPassword || confirmPassword == '') {
//                 toastr.error("請輸入確認密碼");
//                 return false;
//             }

//             if (email != '' && !isValidEmail(email)) {
//                 toastr.error("請輸入正確的Email格式");
//                 return false;
//             }

//             if (passowrd != confirmPassword) {
//                 toastr.error("密碼與確認密碼不同，請重新輸入");
//                 return false;
//             }

//             return true;
//         }

        function isValidEmail(email) {
            var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            return re.test(email);
        }
    </script>
  
  
		
		 
</html>


