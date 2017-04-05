<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
        <link href="css/plugins/datapicker/datepicker3.css" rel="stylesheet">

    </head>
    <body class="gray-bg">
        <div class="widget">
            <!-- 左側資訊區 Start-->
            <div id="leftTab" style="width:220px;float:left;margin-left:5px;">
                <div class="panel panel-success" style="height:780px;">
                    <div class="panel panel-heading">
                        <h3><i class="fa fa-angle-double-right"></i>客戶資料</h3>
                    </div>

                    <ul class="nav nav-pills default" id="rightTab">
                        <li class="active"><a href="#leftTab_1_1" data-toggle="tab">基本資料</a></li>
                        <li>
                            <a href="#leftTab_1_2" data-toggle="tab">常用連結

                            </a>
                        </li>
                        <li>
                            <a href="#leftTab_1_3" data-toggle="tab">案件資訊

                            </a>
                        </li>
                    </ul>

                    <div class="tab-content rightTab">
                        <div class="tab-pane fade active in" id="leftTab_1_1">
                            <div class="portlet">
                                <div class="list-group customerInfo">
                                    <a href="#" class="list-group-item gray-bg">
                                        <h4>INFORMATION</h4>
                                    </a>
                                    <a href="#" class="list-group-item">
                                        <h4>姓名</h4>
                                        <h4>林智凱</h4>
                                    </a>
                                    <a href="#" class="list-group-item">
                                        <h4>住址</h4>
                                        <h4>新北市</h4>
                                    </a>
                                    <a href="#" class="list-group-item">
                                        <h4>專屬人員</h4>
                                        <h4>Alex</h4>
                                    </a>
                                    <a href="#" class="list-group-item">
                                        <h4>註冊地</h4>
                                        <h4>台北分公司</h4>
                                    </a>
                                    <a href="#" class="list-group-item">
                                        <h4>帳號狀態</h4>
                                        <h4>啟用</h4>
                                    </a>
                                    <a href="#" class="list-group-item">
                                        <h4>身份證字號</h4>
                                        <h4>A123456789</h4>
                                    </a>
                                    <a href="#" class="list-group-item">
                                        <h4>性別</h4>
                                        <h4>男</h4>
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

                                            <ul style="list-style-type:none;margin-left:-20px;">
                                                <li>
                                                    <span>
                                                        <i class="fa fa-fw fa-folder-open"></i>
                                                        最新專案
                                                    </span>
                                                    <ul style="list-style-type:none;margin-left:-20px;">
                                                        <li onclick="clickLink1()"><i class="fa fa-fw fa-file-text-o"></i>優惠方案</li>
                                                        <li onclick="clickLink2()"><i class="fa fa-fw fa-file-text-o"></i>週年慶大放送</li>
                                                    </ul>
                                                </li>
                                                <li class="active">
                                                    <span>
                                                        <i class="fa fa-fw fa-folder-open"></i>
                                                        軟體操作
                                                    </span>
                                                    <ul style="list-style-type:none;margin-left:-20px;">
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

                                                    <ul style="list-style-type:none;display:none;margin-left:-20px;">
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
                                <div class="portlet-body">
                                    <div class="input-group">
                                        <div class="col-xs-12">案件編號</div>
                                        <div class="col-xs-12"><input type="text" value="C_201506041009"></div></div>
                                    <div class="input-group">
                                        <div class="col-xs-12">處理人</div>
                                        <div class="col-xs-12"><input type="text" value="HolyLin"></div>
                                    </div>
                                    <div class="input-group">
                                        <div class="col-xs-12">案件類別</div>
                                        <div class="col-xs-12">
                                            <select class="selectpicker" style="width:127px;" id="activityMenu">
											</select>

                                            <button class="btn-success" onclick="showCaseInfo()">註記</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- 左側資訊區 End-->

            <!-- 右側主要功能區 Start-->
            <div class="row" id="rightContentTab" style="margin-left:235px;margin-right:5px;">

                <div class="col-sm-12 col-lg-12 col-md-12 panel panel-success" style="height:780px;">
                    <div>
                        <button class="btn-sm btn-warning" style="float:left;margin-left:-5px;margin-right:5px;"  onclick="leftTabToggle()" id="tabToggleButton">
                            <i class="fa fa-lg fa-arrow-left" id="leftTabToggleButton"></i>
                        </button>
                        <button class="btn-sm btn-success" onclick="showHistoryQuery()" id="queryButton">
                            <i class="fa fa-lg fa-user"></i>
                            <span>歷史資料</span>
                        </button>
                        <button class="btn-sm btn-success" style="display:none;" id="linkButton1" >
                            <span onclick="showLink1()">優惠方案</span>
                            <i class="fa fa-times" onclick="closeLink(1)"></i>
                        </button>

                        <button class="btn-sm btn-success" style="display:none;" id="linkButton2">
                            <span onclick="showLink2()">週年慶大放送</span>
                            <i class="fa fa-times" onclick="closeLink(2)"></i>
                        </button>
                        <button class="btn-sm btn-success" style="display:none;" id="caseInfoButton">
                            <span onclick="showCaseInfo()">服務代碼</span>
                            <i class="fa fa-times" onclick="closeCaseInfo()"></i>
                        </button>
                    </div>

                    <div class="panel-body" id="historyQuery">
                        <!-- 客戶資料Start -->
                        <div class="row">
                            <div class="col-lg-6 col-md-6 col-xs-12">
                                <div class="input-group">
                                    <span class="input-group-addon" id="basic-addon1">日期</span>
                                    <div class="input-daterange input-group" id="datepicker">
                                        <input type="text" class="input-sm form-control" name="start" value="2014-11-11">
                                        <span class="input-group-addon">到</span>
                                        <input type="text" class="input-sm form-control" name="end" value="2014-11-17">
                                    </div>
                                </div>
                            </div>
                            <div class="col-lg-3 col-md-3 col-xs-6">
                                <div class="input-group">
                                    <span class="input-group-addon" id="basic-addon1">處理人</span>
                                    <input type="text" class="form-control" placeholder="請輸入處理人" aria-describedby="basic-addon1">
                                </div>
                            </div>
                            <div class="col-lg-3 col-md-3 col-xs-6">
                                <div class="input-group">
                                    <span class="input-group-addon" id="basic-addon1">客戶ID</span>
                                    <input type="text" class="form-control" placeholder="請輸入ID" aria-describedby="basic-addon1">
                                </div>
                            </div>

                            <div class="col-lg-9 col-md-9 col-xs-8">
                                <div class="input-group">
                                    <span class="input-group-addon" id="basic-addon1">主旨</span>
                                    <input type="text" class="form-control" placeholder="請輸入" aria-describedby="basic-addon1">
                                </div>
                            </div>
                            <div class="col-lg-3 col-md-3 col-xs-4">
                                <button class="btn-sm btn-success">搜尋</button>
                                <button class="btn-sm btn-danger">取消</button>
                            </div>

                            <div class="col-lg-12 col-md-12 col-xs-12">
                                <table class="table table-striped table-bordered table-hover" id="queryTable">
                                    <thead>
                                        <tr>
                                            <td>欄位1</td>
                                            <td>欄位2</td>
                                            <td>欄位3</td>
                                            <td>欄位4</td>
                                            <td>欄位5</td>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <td>Chrome</td>
                                            <td>IE</td>
                                            <td>FireFox</td>
                                            <td>Safari</td>
                                            <td>Netscape</td>
                                        </tr>
                                        <tr>
                                            <td>Chrome</td>
                                            <td>IE</td>
                                            <td>FireFox</td>
                                            <td>Safari</td>
                                            <td>Netscape</td>
                                        </tr>
                                        <tr>
                                            <td>Chrome</td>
                                            <td>IE</td>
                                            <td>FireFox</td>
                                            <td>Safari</td>
                                            <td>Netscape</td>
                                        </tr>
                                        <tr>
                                            <td>Chrome</td>
                                            <td>IE</td>
                                            <td>FireFox</td>
                                            <td>Safari</td>
                                            <td>Netscape</td>
                                        </tr>
                                        <tr>
                                            <td>Chrome</td>
                                            <td>IE</td>
                                            <td>FireFox</td>
                                            <td>Safari</td>
                                            <td>Netscape</td>
                                        </tr>
                                        <tr>
                                            <td>Chrome</td>
                                            <td>IE</td>
                                            <td>FireFox</td>
                                            <td>Safari</td>
                                            <td>Netscape</td>
                                        </tr>
                                        <tr>
                                            <td>Chrome</td>
                                            <td>IE</td>
                                            <td>FireFox</td>
                                            <td>Safari</td>
                                            <td>Netscape</td>
                                        </tr>
                                        <tr>
                                            <td>Chrome</td>
                                            <td>IE</td>
                                            <td>FireFox</td>
                                            <td>Safari</td>
                                            <td>Netscape</td>
                                        </tr>
                                        <tr>
                                            <td>Chrome</td>
                                            <td>IE</td>
                                            <td>FireFox</td>
                                            <td>Safari</td>
                                            <td>Netscape</td>
                                        </tr>
                                        <tr>
                                            <td>Chrome</td>
                                            <td>IE</td>
                                            <td>FireFox</td>
                                            <td>Safari</td>
                                            <td>Netscape</td>
                                        </tr>
                                        <tr>
                                            <td>Chrome</td>
                                            <td>IE</td>
                                            <td>FireFox</td>
                                            <td>Safari</td>
                                            <td>Netscape</td>
                                        </tr>
                                    </tbody>
                                </table>
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

                    <!-- 案件資訊專區 Start -->
                    <div class="panel-body panel-success" id="caseInfo" style="display:none;">
                        <div id="exTab1" class="tab-container">	
                            <ul  class="nav nav-tabs" id="caseInfo_li">
                                 
                            </ul>
							
                            <div class="tab-content clearfix" id="data_table">
                               
                               
                                       
                                    
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- 案件資訊專區 End-->

                </div>
            </div>
        </div>

    </body>



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




    <script>

        $(document).ready(function(){
            // init datepicker
            $("#datepicker" ).datepicker();
            // init datatable
            $("#queryTable").DataTable();
            $("#queryTable").css("width","100%");
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
            hideAll();
            $("#historyQuery").show();

            clearLinkStyle();
            $("#queryButton").removeClass("btn-success");
            $("#queryButton").addClass("btn-primary");
        }

        function showLink1() {
            hideAll();
            $("#link1").show();

            clearLinkStyle();
            $("#linkButton1").removeClass("btn-success");
            $("#linkButton1").addClass("btn-primary");
        }

        function showLink2() {
            hideAll();
            $("#link2").show();

            clearLinkStyle();
            $("#linkButton2").removeClass("btn-success");
            $("#linkButton2").addClass("btn-primary");
        }

        function showCaseInfo() {
            hideAll();
            $("#caseInfoButton").show();
            $("#caseInfo").show();
            groupsdata();
            
            clearLinkStyle();
            $("#caseInfoButton").removeClass("btn-success");
            $("#caseInfoButton").addClass("btn-primary");
            
            
        }

        function clearLinkStyle() {
            $("#queryButton").removeClass("btn-primary");
            $("#queryButton").addClass("btn-success");
            $("#linkButton1").removeClass("btn-primary");
            $("#linkButton1").addClass("btn-success");
            $("#linkButton2").removeClass("btn-primary");
            $("#linkButton2").addClass("btn-success");
            $("#caseInfoButton").removeClass("btn-primary");
            $("#caseInfoButton").addClass("btn-success");
        }

        function closeLink(number) {
            if(number == '1') {
                $("#linkButton1").hide();
                $("#link1").hide();
            } else if(number == '2') {
                $("#linkButton2").hide();
                $("#link2").hide();
            }

            clearLinkStyle();
            $("#historyQuery").show();
        }

        function closeCaseInfo() {
            $("#caseInfoButton").hide();
            $("#caseInfo").hide();

            
            clearLinkStyle();
            $("#historyQuery").show();
        }

        function hideAll() {
            $("#historyQuery").hide();
            $("#link1").hide();
            $("#link2").hide();
            $("#caseInfo").hide();
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

        $(".customerInfo a").on("click",function(){
            var copyText = $(this).find(':nth-child(2)').text();
            var $temp = $("<input>");

            $("body").append($temp);
            $temp.val(copyText).select();
            document.execCommand("copy");
            $temp.remove();
        });

        $("#tabToggleButton").on("mouseover",function(){
            //leftTabToggle();
        });

        function leftTabToggle() {
            var isOpen = $("#leftTabToggleButton").hasClass("fa-arrow-left");

            if (isOpen) {
                $("#leftTabToggleButton").removeClass("fa-arrow-left");
                $("#leftTabToggleButton").addClass("fa-arrow-right");

                $("#rightContentTab").animate({"margin-left":'5px'});

                $("#leftTab").animate({"margin-left":'-230px'});
            } else {
                $("#leftTabToggleButton").removeClass("fa-arrow-right");
                $("#leftTabToggleButton").addClass("fa-arrow-left");

                $("#rightContentTab").animate({"margin-left":'235px'});

                $("#leftTab").animate({"margin-left":'5px'});
            }
        }
    </script>

	<script type="text/javascript">

	 $.ajax({                              
         url:"http://ws.crm.com.tw:8080/IMWebSocket/RESTful/Query_ActivityMenu",
	         data:{
	        	 dbid:0
	        	 },
	         type : "POST",                                                                    
	         dataType:'json',
	         
	         error:function(e){                                                                 
	         alert("失敗");
	         callback(data);
	         },
	         success:function(data){
	        	 console.log("ActivityMenu",data);
	        		
	        	 for(var i=0; i<data.activitymenu.length; i++){
	        		// alert(JSON.stringfl(data.activitymenu[i].dbid))
	        		 var MENULIST = "<option value='"+data.activitymenu[i].dbid+"'>"+data.activitymenu[i].menuname+"</option>" 
	        		    document.getElementById("activityMenu").insertAdjacentHTML("BeforeEnd",MENULIST); 
	        	}

	         }
	   		 }); 
	 
	</script>
	
	<script type="text/javascript">
	
	function groupsdata(){
		
	var dbid = document.getElementById("activityMenu").value
 $.ajax({                              
   url:"http://ws.crm.com.tw:8080/IMWebSocket/RESTful/Query_ActivityMenu",
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
        	 console.log("GroupData",data);	
        	 
        	
        	 
        	 
        	 for(var i=0; i<data.activitygroups.length; i++){
        		 
        		 if(i==0){
        			 var groupsli1 = "<li class='active'><a  href=#caseInfo"+data.activitygroups[i].dbid+" data-toggle='tab'>"+data.activitygroups[i].groupname+"</a></li>"
                	 document.getElementById("caseInfo_li").insertAdjacentHTML("BeforeEnd",groupsli1);
        			 
        		 }else{
        			 var groupsli = "<li><a  href=#caseInfo"+data.activitygroups[i].dbid+" data-toggle='tab'>"+data.activitygroups[i].groupname+"</a></li>"
             	 	 document.getElementById("caseInfo_li").insertAdjacentHTML("BeforeEnd",groupsli);
        		 }
       
        	 }	 
        	
//         	 for(var i=0; i<data.TitleFlag.length; i++){
//         		 if(i==0){
//         		 var div1 ="<div class='tab-pane active' id='caseInfo"+data.TitleFlag[i].activitygroupsid+"'><div class='row'><div class='col-xs-3'><table class='table table-striped table-bordered table-hover'><thead><tr><td><b class='text-success'>"+data.TitleFlag[i].codename+"</b></td></tr></thead><tbody id="+data.TitleFlag[i].titleflag+"><tr><td><input type='checkbox'>C1.無法登入</td></tr></tbody></table></div></div><div class='row' style='font-size:16px;'><div class='col-xs-2'><input type='checkbox'> 離席外撥</div><div class='col-xs-2'><input type='checkbox'> 來電提示</div><div class='col-xs-2'><input type='checkbox'> 抱怨</div><div class='col-xs-2'><input type='checkbox'> 離席</div><div class='col-xs-2'><input type='checkbox'> 黑名單</div><div class='col-xs-2'><button class='btn btn-success' onclick='closeCaseInfo()'>確定</button></div></div></div>"
//             	 document.getElementById("data_table").insertAdjacentHTML("BeforeEnd",div1); 
//         		 }else{

//         		 var div1 ="<div class='tab-pane' id='caseInfo"+data.TitleFlag[i].activitygroupsid+"'><div class='row'><div class='col-xs-3'><table class='table table-striped table-bordered table-hover'><thead><tr><td><b class='text-success'>"+data.TitleFlag[i].codename+"</b></td></tr></thead><tbody id="+data.TitleFlag[i].titleflag+"><tr><td><input type='checkbox'>C2.無法登入</td></tr></tbody></table></div></div><div class='row' style='font-size:16px;'><div class='col-xs-2'><input type='checkbox'> 離席外撥</div><div class='col-xs-2'><input type='checkbox'> 來電提示</div><div class='col-xs-2'><input type='checkbox'> 抱怨</div><div class='col-xs-2'><input type='checkbox'> 離席</div><div class='col-xs-2'><input type='checkbox'> 黑名單</div><div class='col-xs-2'><button class='btn btn-success' onclick='closeCaseInfo()'>確定</button></div></div></div>"
//         		 document.getElementById("data_table").insertAdjacentHTML("BeforeEnd",div1); 
//         	 	}
        		 
//         		 var data ="<tr><td><input type='checkbox'>C1.無法登入</td></tr>"
//         		 document.getElementById("data_table").insertAdjacentHTML("BeforeEnd",div1);
//         	 }
        	 
         }
   		 }); 
	}
	</script>

</html>