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


        <title>Chat頁面</title>

        <link href="css/bootstrap.min.css?v=3.3.6" rel="stylesheet">
        <link href="css/font-awesome.css?v=4.4.0" rel="stylesheet">
        <link href="css/animate.css" rel="stylesheet">
        <link href="css/style.css?v=4.1.0" rel="stylesheet">
        <link href="layui/css/layui.css" rel="stylesheet">
        	<!-- jQuery -->
    	<script src="js/jquery.min.js"></script>
<!--         <script type="text/javascript" src="js/Query_Interaction.js"></script> -->

    </head>
    <body class="gray-bg">
        <div class="widget">
            <!-- 左側資訊區 Start-->
            <div class="col-lg-4">
                <div class="panel panel-primary">
                    <div class="panel panel-heading">
                        <h3><i class="fa fa-angle-double-right"></i>客戶資料</h3>
                    </div>

                    <ul class="nav nav-pills" id="rightTab">
                        <li class="active"><a href="#leftTab_1_1" data-toggle="tab">基本資料</a></li>
                        <li><a href="#leftTab_1_2" data-toggle="tab">常用連結</a></li>
                        <li><a href="#leftTab_1_3" data-toggle="tab">案件資訊</a></li>
                    </ul>

                    <div class="tab-content rightTab">
                        <div class="tab-pane fade active in" id="leftTab_1_1">
                            <div class="portlet">
                                <div class="list-group">
                                    <a href="#" class="list-group-item gray-bg">
                                        <h3>INFORMATION</h3>
                                    </a>
                                    <a href="#" class="list-group-item">
                                        <h3>姓名</h3>
                                        <h3>林智凱</h3>
                                    </a>
                                    <a href="#" class="list-group-item">
                                        <h3>住址</h3>
                                        <h3>新北市</h3>
                                    </a>
                                    <a href="#" class="list-group-item">
                                        <h3>專屬人員</h3>
                                        <h3>Alex</h3>
                                    </a>
                                    <a href="#" class="list-group-item">
                                        <h3>註冊地</h3>
                                        <h3>台北分公司</h3>
                                    </a>
                                    <a href="#" class="list-group-item">
                                        <h3>帳號狀態</h3>
                                        <h3>啟用</h3>
                                    </a>
                                    <a href="#" class="list-group-item">
                                        <h3>身份證字號</h3>
                                        <h3>A123456789</h3>
                                    </a>
                                    <a href="#" class="list-group-item">
                                        <h3>性別</h3>
                                        <h3>男</h3>
                                    </a>
                                </div>
                            </div>
                        </div>
                        <div class="tab-pane fade" id="leftTab_1_2">
                            <div class="portlet">
                                <div class="portlet-body" >
                                    <div class="widget">
                                        <div class="fa-tree-list">
                                            <span>
                                                <i class="fa fa-fw fa-folder-open"></i>
                                                特別提示
                                            </span>

                                            <ul style="list-style-type:none;margin-left:20px;">
                                                <li>
                                                    <span>
                                                        <i class="fa fa-fw fa-folder-open"></i>
                                                        最新專案
                                                    </span>
                                                    <ul style="list-style-type:none;margin-left:20px;">
                                                        <li onclick="clickLink1()"><i class="fa fa-fw fa-file-text-o"></i>優惠方案</li>
                                                        <li onclick="clickLink2()"><i class="fa fa-fw fa-file-text-o"></i>週年慶大放送</li>
                                                    </ul>
                                                </li>
                                                <li class="active">
                                                    <span>
                                                        <i class="fa fa-fw fa-folder-open"></i>
                                                        軟體操作
                                                    </span>
                                                    <ul style="list-style-type:none;margin-left:20px;">
                                                        <li><i class="fa fa-fw fa-file-text-o"></i>知識管理</li>
                                                        <li><i class="fa fa-fw fa-file-text-o"></i>考試系統</li>
                                                        <li><i class="fa fa-fw fa-file-text-o"></i>客服代表應用</li>
                                                    </ul>
                                                </li>
                                                <li>
                                                    <span>
                                                        <i class="fa fa-fw fa-folder"></i>
                                                        硬體故障簡易處理
                                                    </span>

                                                    <ul style="list-style-type:none;display:none;margin-left:20px;">
                                                        <li><i class="fa fa-fw fa-file-text-o"></i>個人電腦</li>
                                                        <li><i class="fa fa-fw fa-file-text-o"></i>交換機</li>
                                                    </ul>
                                                </li>
                                            </ul>
                                        </div>
                                    </div>

                                </div>
                            </div>
                        </div>
                        <div class="tab-pane fade" id="leftTab_1_3">
                            <div class="portlet">
                                <div class="portlet-body" >

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- 左側資訊區 End-->

            <!-- 右側主要功能區 Start-->
            <div class="col-lg-8">
                <div class="panel panel-primary">
                    <div class="panel-body">
                        <button class="btn-sm btn-success" onclick="showHistoryQuery()">
                            <i class="fa fa-lg fa-user"></i>
                            <span>歷史資料</span>
                        </button>
                        <button class="btn-sm btn-primary" style="display:none;" id="linkButton1" onclick="showLink1()">
                            <span>優惠方案</span>
                        </button>
                        <button class="btn-sm btn-primary" style="display:none;" id="linkButton2" onclick="showLink2()">
                            <span>週年慶大放送</span>
                        </button>

                        <div class="panel-body" id="historyQuery">
                            <!-- 客戶資料Start -->
                            <div class="row ibox">
                                <div class="col-lg-6 col-md-6">
                                    <div class="input-group">
                                        <span class="input-group-addon" id="basic-addon1">日期</span>
                                        <div class="input-daterange input-group" id="datepicker">
                                  
                                            <input type="text" class="input-sm form-control" name="start" value="2017-01-01" id="startdate">
                                          
                                            <span class="input-group-addon">到</span>
                                            <input type="text" class="input-sm form-control" name="end" value="2017-01-22" id="enddate">
                                         
                                        </div>
                                    </div>
                                </div>
                                <input value="0" id="Page" type="hidden">
                                <div class="col-lg-3 col-md-3">
                                    <div class="input-group">
                                        <span class="input-group-addon" id="basic-addon1">處理人</span>
                                        <input type="text" id="agentid" class="form-control" placeholder="" aria-describedby="basic-addon1" value="">
                                    </div>
                                </div>
                                <div class="col-lg-3 col-md-3">
                                    <div class="input-group">
                                        <span class="input-group-addon" id="basic-addon1">客戶ID</span>
                                        <input type="text" class="form-control" placeholder="請輸入ID" aria-describedby="basic-addon1">
                                    </div>
                                </div>
                            </div>
                            <div class="row ibox">
                                <div class="col-lg-9 col-md-9">
                                    <div class="input-group">
                                        <span class="input-group-addon" id="basic-addon1">主旨</span>
                                        <input type="text" class="form-control" placeholder="請輸入" aria-describedby="basic-addon1">
                                    </div>
                                </div>
                                <div class="col-lg-3 col-md-3">
                                    <button class="btn-sm btn-success" onclick="serch()">搜尋</button>
                                    <button class="btn-sm btn-danger">取消</button>
                                </div>
                            </div>

                            <div class="row ibox">
                                <div class="col-lg-12 col-md-12">
            <div id=div>
                                
       <table id="table">
  
    <thead >
        <tr >
			 <th data-checkbox="true"></th>
			 <th id="contactid" data-sortable="true"></th>
			 <th id="activitycode" data-sortable="true"></th>
			 <th id="subject" data-sortable="true"></th>
			 <th id="startdate" data-sortable="true"></th>
			 <th id="structuredmimetype" data-sortable="true"></th>
			 <th id="enddate" data-sortable="true"></th>
			 <th id="structuredtext" data-sortable="true"></th>
			 <th id="entitytypeid" data-sortable="true"></th>
			 <th id="text" data-sortable="true"></th>
			 <th id="status" data-sortable="true"></th>
			 <th id="typeid" data-sortable="true"></th>
			 <th id="dbid" data-sortable="true"></th>
			 <th id="ixnid" data-sortable="true"></th>
			 <th id="stoppedreason" data-sortable="true"></th>
			 <th id="thecomment" data-sortable="true"></th>
        </tr>
          
    </thead>
</table>
</div>
				<div class="btn-group">
			
				
				<div id="form1">
				</div>
        		
                                </div>
                            </div>
                        </div>
                        <!-- 客戶資料End -->

                        <!-- 常用連結專區 Start-->
                        <div class="panel-body" id="link1" style="display:none;">
                            <iframe src="http://www.crm.com.tw/" style="width:100%;min-height:400px;"></iframe>
                        </div>

                        <div class="panel-body" id="link2" style="display:none;">
                            <iframe src="http://www.pchome.com" style="width:100%;min-height:400px;"></iframe>
                        </div>
                        <!-- 常用連結專區 End-->
                    </div>
                </div>
            </div>
        </div>
    </body>
 
  <script>
function Pageright() {
	 var value = document.getElementById('Page').value;
	    value++;
	    document.getElementById('Page').value = value+9;
}
</script>
  <script>
function Pageleft() {
	 var value = document.getElementById('Page').value;
	    value--;
	    document.getElementById('Page').value = value-9;
}
</script>



    <!-- 全局js -->
    <script src="js/jquery.min.js?v=2.1.4"></script>
    <script src="js/bootstrap.min.js?v=3.3.6"></script>

    <!-- Data Tables -->
    <script src="js/plugins/dataTables/jquery.dataTables.js"></script>
    <script src="js/plugins/dataTables/dataTables.bootstrap.js"></script>

    <!-- DataPicker -->
    <script src="js/plugins/datapicker/bootstrap-datepicker.js"></script>

    <!-- bootstrap_treeview -->
    <script src="js/plugins/treeview/bootstrap-treeview.js"></script>

    <!-- Flot -->
    <script src="js/plugins/flot/jquery.flot.js"></script>
    <script src="js/plugins/flot/jquery.flot.tooltip.min.js"></script>
    <script src="js/plugins/flot/jquery.flot.resize.js"></script>

    <!-- ChartJS-->
    <script src="js/plugins/chartJs/Chart.min.js"></script>

    <!-- Peity -->
    <script src="js/plugins/peity/jquery.peity.min.js"></script>

    <!-- Peity demo -->
    <script src="js/demo/peity-demo.js"></script>
 


  

    <!-- Bootstrap Core JavaScript -->
     
	<script src="js/plugins/bootstrap-table/bootstrap-table.min.js"></script>			  <!-- TABLE相關 -->
	
	
	
	<script type="text/javascript">
	function serch(){
		
		$('#table tr').empty();
		
		$('#form1').empty();

	var ss = $('#startdate').val();
	var ee = $('#enddate').val();
	var aa = $('#agentid').val();
	var pp = $('#page').val();
	
	
	var myMap = new Map();
	 $.post("RESTful/Query_Interaction",
			   	{
			    	 "startdate":ss,
			    	 "enddate":ee,
			    	 "agentid":aa,
			    	 "page":pp
			   	 },
			   	
			       function(data){
			   		 $("#div").html($('#table').bootstrapTable({
			   	        columns: [{
			   	            field: 'box',
			   	            title: 'box'
			   	        }, {
			   	            field: 'contactid',
			   	            title: 'contactid'
			   	        }, {
			   	            field: 'activitycode',
			   	            title: 'activitycode'
			   	        }, {
			   	            field: 'subject',
			   	            title: 'subject'
			   	        },{
			   	            field: 'startdate',
			   	            title: 'startdate'
			   	        },
			   	        {
			   	            field: 'structuredmimetype',
			   	            title: 'structuredmimetype'
			   	        },
			   	        {
			   	            field: 'enddate',
			   	            title: 'enddate'
			   	        },
			   	        {
			   	            field: 'structuredtext',
			   	            title: 'structuredtext'
			   	        }, {
			   	            field: 'entitytypeid',
			   	            title: 'entitytypeid'
			   	        }, {
			   	            field: 'text',
			   	            title: 'text'
			   	        }, {
			   	            field: 'status',
			   	            title: 'status'
			   	        }, {
			   	            field: 'typeid',
			   	            title: 'typeid'
			   	       }, {
			   	            field: 'dbid',
			   	            title: 'dbid'
			   	        }, {
			   	            field: 'ixnid',
			   	            title: 'ixnid'
			   	        }, {
			   	            field: 'stoppedreason',
			   	            title: 'stoppedreason'
			   	        }, {
			   	            field: 'thecomment',
			   	            title: 'thecomment'
			   	        }],
			   			 
			   			 data:data.Interaction}));
			   		 //alert(JSON.stringify(data.Interaction));
			    	//$('#table').bootstrapTable('refresh', {data:data.Interaction});
			    }, "json");
	
	
	 
	  $.ajax({                              
          url:"/IMWebSocket/RESTful/QueryAll_Interaction",
	       
	         data:{
	        	 startdate:ss,
	        	 enddate:ee,
	        	 agentid:aa
	        	 },
	            
	         type : "POST",                                                                    
	         dataType:'json', 
	         error:function(e){                                                                 
	         alert("失敗");
	         callback(data);
	         },
	         success:function(all){                                                           
	        	
	        	 console.log("ALL",all)
	        	 
                 var Totalcount="<h4>總共筆數:"+all.Interaction+"筆     共"+Math.ceil(all.Interaction/10)+"頁</h4><br>";
                   	document.getElementById("form1").insertAdjacentHTML("BeforeEnd",Totalcount);
                  	
                var left="<button type='button' class='btn btn-white' id='Pageleft'"+
            	"onclick='Pageleft()'>"+"<i class='fa fa-chevron-left'></i></button>"
            	document.getElementById("form1").insertAdjacentHTML("BeforeEnd",left); 
            	        			
	        	for(var i=1; i<=Math.ceil(all.Interaction/10); i++){
	        	
	        		
	        		    var str= "<button id='bu"+i+"' class='btn btn-white' onclick='page("+i+")' value='"+i+"'>"+i+"</button>"; 
	        		    document.getElementById("form1").insertAdjacentHTML("BeforeEnd",str); 
	        	}
	        	
	        	var right="<button type='button' class='btn btn-white' id='Pageright'"+
    			"onclick='Pageright()'>"+"<i class='fa fa-chevron-right'></i></button>"
    			document.getElementById("form1").insertAdjacentHTML("BeforeEnd",right);
	        	
	         },
	      
	     }); 
	 
	 
	 
	};
	</script>
	
	
	
	
	<script type="text/javascript">

	function page(i){
		$('#table tr').empty();
		
		//$('#table').bootstrapTable('refresh', {url: '../json/data1.json'});  

	
		var r = (i-1)*10;	
		var ss = $('#startdate').val();
		var ee = $('#enddate').val();
		var aa = $('#agentid').val();
		var pp = r;
		
		var myMap = new Map();
		 $.post("RESTful/Query_Interaction",
				   	{
				    	 "startdate":ss,
				    	 "enddate":ee,
				    	 "agentid":aa,
				    	 "page":pp
				   	 },
				   	
				       function(data){
	
				   		 $("#div").html($('#table').bootstrapTable({
				   	        columns: [{
				   	            field: 'box',
				   	            title: 'box'
				   	        }, {
				   	            field: 'contactid',
				   	            title: 'contactid'
				   	        }, {
				   	            field: 'activitycode',
				   	            title: 'activitycode'
				   	        }, {
				   	            field: 'subject',
				   	            title: 'subject'
				   	        },{
				   	            field: 'startdate',
				   	            title: 'startdate'
				   	        },
				   	        {
				   	            field: 'structuredmimetype',
				   	            title: 'structuredmimetype'
				   	        },
				   	        {
				   	            field: 'enddate',
				   	            title: 'enddate'
				   	        },
				   	        {
				   	            field: 'structuredtext',
				   	            title: 'structuredtext'
				   	        }, {
				   	            field: 'entitytypeid',
				   	            title: 'entitytypeid'
				   	        }, {
				   	            field: 'text',
				   	            title: 'text'
				   	        }, {
				   	            field: 'status',
				   	            title: 'status'
				   	        }, {
				   	            field: 'typeid',
				   	            title: 'typeid'
				   	       }, {
				   	            field: 'dbid',
				   	            title: 'dbid'
				   	        }, {
				   	            field: 'ixnid',
				   	            title: 'ixnid'
				   	        }, {
				   	            field: 'stoppedreason',
				   	            title: 'stoppedreason'
				   	        }, {
				   	            field: 'thecomment',
				   	            title: 'thecomment'
				   	        }],
				   			 
				   			 data:data.Interaction}));
				   		 //alert(JSON.stringify(data.Interaction));
				    	//$('#table').bootstrapTable('refresh', {data:data.Interaction});
				    }, "json");


	}
	</script>
	
	
	
    <script>

        $(document).ready(function(){
            // init datepicker
            $("#datepicker" ).datepicker();
            // init datatable
            $("#queryTable").DataTable();

        });

        function clickLink1() {
            $("#linkButton1").show();
            showLink1();
        }
        
        function clickLink2() {
            $("#linkButton2").show();
            showLink2();
        }


        function showHistoryQuery() {
            $("#historyQuery").show();
            $("#link1").hide();
            $("#link2").hide();
        }

        function showLink1() {
            $("#historyQuery").hide();
            $("#link1").show();
            $("#link2").hide();
        }

        function showLink2() {
            $("#historyQuery").hide();
            $("#link1").hide();
            $("#link2").show();
        }

        /* tree list controll */
        $(".fa-tree-list>span").on("click",function(){
            if ( $(".fa-tree-list>span>i").hasClass("fa-folder-open")) {
                // toggle icon
                $(".fa-tree-list>span>i").removeClass("fa-folder-open");
                $(".fa-tree-list>span>i").addClass("fa-folder");

                $(".fa-tree-list ul").hide();
            } else if ($(".fa-tree-list>span>i").hasClass("fa-folder")){
                // toggle icon
                $(".fa-tree-list>span>i").removeClass("fa-folder");
                $(".fa-tree-list>span>i").addClass("fa-folder-open");

                $(".fa-tree-list ul").show();
                $(".fa-tree-list>ul>li>span").find("i").removeClass("fa-folder-open");
                $(".fa-tree-list>ul>li>span").find("i").addClass("fa-folder");
                $(".fa-tree-list ul li").find("ul").hide();
            }
        });

        $(".fa-tree-list>ul>li>span").on("click",function(){
            //console.log($(this));
            if ( $(this).find("i").hasClass("fa-folder-open")) {
                // toggle icon
                $(this).find("i").removeClass("fa-folder-open");
                $(this).find("i").addClass("fa-folder");

                $(this).parent().find("ul").hide();

            } else if ($(this).find("i").hasClass("fa-folder")){
                // toggle icon
                $(this).find("i").removeClass("fa-folder");
                $(this).find("i").addClass("fa-folder-open");

                $(this).parent().find("ul").show();
            }

        });
    </script>
  

</html>