<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Agent - Chat</title>
<title>Agent</title>
<!-- bootstrap v3.3.6 -->
<script src="js/jquery.min.js"></script>
<link href="boostrap/bootstrap.css" rel="stylesheet" />
<link href="boostrap/bootstrap-theme.css" rel="stylesheet" />
<script src="boostrap/bootstrap.js"></script>

<script type="text/javascript" src="js/swfobject.js"></script> <!-- for IE8 websocket compatibility -->
<script type="text/javascript" src="js/web_socket.js"></script> <!-- for IE8 websocket compatibility -->
<script type="text/javascript" src="js/websocket-util.js"></script>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/websocket-agent.js"></script>

 <!-- for IE8 websocket compatibility -->
<script type="text/javascript">
// Let the library know where WebSocketMain.swf is:
WEB_SOCKET_SWF_LOCATION = "WebSocketMain.swf";	
</script>

    <style>
        .form-group {
            margin-bottom: 10px;
        }

        #ChatLog {
            overflow-y: auto;
            overflow-x: hidden;
            border: 1px solid #d2dbe3;
            font-size: smaller;
            height: 460px;
        }

        #ChatDialog {
            overflow-y: auto;
            overflow-x: hidden;
            border: 1px solid #d2dbe3;
            font-size: smaller;
            height: 80px;
        }
    </style>
</head>
<body>
    <div style="width:90%">
        <div class="row" style="margin-top:5px;">
            <div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">
                <div>
                    <label>Agent:</label>
                    <label id="AgentId"></label>
                </div>
            </div>
            <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
            </div>
            <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
                <button id="AgentReady" class="btn btn-primary btn-sm" style="width:100px">Ready</button>
            </div>
            <div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">
                <div>
                    <div id="NotReadyDiv">
                        <label>Reason: </label>
                        <select id="reason">
                            <option value="" selected="selected"></option>
                            <option value="WC">WC</option>
                            <option value="Business">Business</option>
                            <option value="Meeting">Meeting</option>
                        </select>
                        <button id="AgentNotReady" class="btn btn-primary btn-sm" style="width:100px">Not Ready</button>
                    </div>
                </div>
            </div>
        </div>
        <div class="row" style="margin-top:5px;border-top: 1px solid #000000;">
            <div class="col-xs-8 col-sm-8 col-md-8 col-lg-8">
                <div class="row" style="margin-top:5px">
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12" style="text-decoration: underline;">Interaction</div>
                </div>
                <div class="row">
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                        <div class="form-group">
                            <select id="Interactions" class="form-control" style="width:100%" size=5></select>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                        <div class="form-group">
                            <button class="btn btn-primary btn-sm" id="Accept" style="width:100px">Accept</button>
                            <button class="btn btn-primary btn-sm" id="Reject" style="width:100px">Reject</button>
                            <button class="btn btn-primary btn-sm" id="Release" style="width:100px">Release</button>
                            <button class="btn btn-primary btn-sm" id="Done" style="width:100px;visibility:hidden">Done</button>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                        <div class="form-group">
                            Message:&nbsp;&nbsp;<input type="text" id="Message" style="width:440px" />
                            <button class="btn btn-primary btn-sm" id="SendMessage" style="width:100px">Send Message</button>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                        <div class="form-group">
                            Agent Id:&nbsp;&nbsp;<input type="text" id="TargetAgentId" style="width:100px" />
                            Mode:
                            <select id="VisibilityMode">
                                <option value="1" selected="selected">Conference</option>
                                <option value="2">Monitor</option>
                                <option value="3">Coach</option>
                            </select>
                            <button class="btn btn-primary btn-sm" id="Conference" style="width:100px">Conference</button>
                            <button class="btn btn-primary btn-sm" id="Transfer" style="width:100px">Transfer</button>
                            <!--<button class="btn btn-primary btn-sm" id="Done" style="width:120px">Stop Processing</button>-->
                        </div>
                    </div>
                </div>
                <!--<div class="row">
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                        <div class="form-group">
                            <button class="btn btn-primary btn-sm" id="SendPausedTypingNotice" style="width:200px">Send Paused Typing Notice</button>
                            <button class="btn btn-primary btn-sm" id="SendTypingNotice" style="width:200px">Send Typing Notice</button>
                        </div>
                    </div>
                </div>-->
                <div class="row">
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                        <div class="form-group">
                            <labe>User Data:</labe>
                            <textarea style="width:100%" id="UserData" class="form-control" rows="3"></textarea>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                        <div class="form-group">
                            <labe>Chat Dialog:</labe>
                            <div id="ChatDialog" class="form-control"></div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-xs-4 col-sm-4 col-md-4 col-lg-4" style="border-left: 1px solid #000000;">
                <div>
                    <label>Event :</label>&nbsp;&nbsp;&nbsp;&nbsp;<button id="ClearLog" type="button" class="btn btn-sm btn-primary">Clear</button>
                </div>
                <div>
                    <div id="ChatLog"></div>
                </div>
            </div>
        </div>
    </div>


    <script type="text/javascript">
        // ChatDialog array -- 儲存每一interaction的對談記錄
        var arrayChatDialog = new Object();
        var previousInteraction = "";

        // 將action內容包成一個CommandObject，
        function CreateCommandObject(id) {
            var CommandObject = {};
            CommandObject.AgentId = parent.AgentId;
            CommandObject.PlaceId = parent.PlaceId;
            CommandObject.DN = parent.DN;
            CommandObject.NickName = parent.NickName;
            CommandObject.MediaType = "Chat";
            CommandObject.Action = id;
            CommandObject.Control = '';
            CommandObject.Result = "";

            // tmp solution
            CommandObject.OwnerId = "123";

            if (id == "SendMessage" && $('#Message').val() != "") CommandObject.Message = $('#Message').val();
            if ((id == "Conference" || id == "Transfer") && $('#TargetAgentId').val() != "") CommandObject.TargetAgentId = $('#TargetAgentId').val();

            if ($('#Interactions').find(":selected").length>0 && $('#Interactions').find(":selected").val() != "") {
                arrayInteractions = $('#Interactions').find(":selected").val().split(',');
                CommandObject.InteractionId = arrayInteractions[0];
                CommandObject.TicketId = arrayInteractions[1];
                if (arrayInteractions.length > 2) CommandObject.ContactId = GetContactId(arrayInteractions[2]);
            } else { // 檢查是否指定 interaction 
                if ($("#Interactions option").length > 0) {
                    arrayInteractions = $('#Interactions option:first').val().split(',');
                    CommandObject.InteractionId = arrayInteractions[0];
                    CommandObject.TicketId = arrayInteractions[1];
                    if (arrayInteractions.length > 2) CommandObject.ContactId = GetContactId(arrayInteractions[2]);
                }
            }
            CommandObject.VisibilityMode = $('#VisibilityMode').find(":selected").val();
            // michael 20161221 changed to allow Status Change
            //if (id == "AgentNotReady") CommandObject.Reason = $('#reason').find(":selected").val();
            if (id == "AgentNotReady") {
                CommandObject.Reason = $('#reason').find(":selected").val();
                // Reason change if Ready Button is visible
                if ($("#AgentReady").is(":visible")) {
                    CommandObject.Action = "ChangeMediaStateReason";
                }
            }

            return CommandObject;
        }

        $(document).ready(function () {
            // 預設 hide "AgentNotReady" button 
            // michael 20161221 marked to allow Reason Change
            //$("#AgentNotReady, #NotReadyDiv").hide();


            // 偵測目前是否在打字
            var IsTypingMessage = false; // 是否還在持續打字
            var Message_LastTime = ""; // 上次的訊息內容，以作比較，檢查是否還在持續打字
            var Timer = null;            // timer, 檢查是否還在持續打字
            $('#Message').keyup(function (e) {
                // 檢查是否有連線記錄
                if ($("#Interactions option").length <= 0) return;

                if (e.which == 13) { // press ENTER
                    IsTypingMessage = false;
                } else {
                    if (IsTypingMessage == false) { // 剛開始打字時
                        IsTypingMessage = true;
                        parent.hub.server.sendChatCommand(CreateCommandObject("SendTypingNotice"));
                        Message_LastTime = $('#Message').val();

                        // check if same Message content after 2 seconds
                        Timer = setTimeout(function () {
                            if ($("#Interactions option").length <= 0) return;
                            if ($('#Message').val() != "" && $('#Message').val() == Message_LastTime) {
                                parent.hub.server.sendChatCommand(CreateCommandObject("SendPausedTypingNotice"));
                                IsTypingMessage = false;
                            }
                        }, 10000);
                    } else { // key is pressed again in 2 seconds, reset timer
                        Message_LastTime = $('#Message').val();
                        if (Timer != null) {
                            // reset timer
                            clearTimeout(Timer);
                            // check if same Message content after 2 seconds
                            Timer = setTimeout(function () {
                                if ($('#Message').val() != "" && $('#Message').val() == Message_LastTime) {
                                    parent.hub.server.sendChatCommand(CreateCommandObject("SendPausedTypingNotice"));
                                    IsTypingMessage = false;
                                }
                            }, 10000);
                        }
                    }
                }
            });



            // 所有按鈕的事件處理
            $('button').on('click', function () {
                if ($(this).prop("id") == "ClearLog") {
                    $('#ChatLog').empty();
                    return;
                }

                if (parent.hubState != 1) {
                    alert("SignalR is not connected !!");
                    return;
                }

                // 【接受派件】按鈕的處理
                if ($(this).prop("id") == "Accept") {
                    //// check if agent ready
                    //if ($("#AgentReady").is(":hidden")==false) {
                    //    alert("Please press READY button first !!");
                    //    return;
                    //}

                    var selectedItem = $("#Interactions").find(":selected");
                    if (selectedItem != null && selectedItem.length > 0) {
                        var arrayFields = selectedItem.text().split("/");
                        // 檢查 interaction 狀態是否為 "Invited"
                        if (arrayFields.length <= 1 || arrayFields[1] != "Invited") {
                            alert("Please select an INVITED interaction !!");
                            return;
                        }
                    } else { // 檢查是否指定 interaction 
                        if ($("#Interactions option").length > 0) {
                            $('#Interactions option:first').prop('selected', true);
                            var arrayFields = $('#Interactions option:first').text().split("/");
                            // 檢查 interaction 狀態是否為 "Invited"
                            if (arrayFields.length <= 1 || arrayFields[1] != "Invited") {
                                alert("Please select an INVITED interaction !!");
                                return;
                            }

                            // parse first Interaction相關訊息
                            ParseFirstInteraction();

                        } else {
                            alert("Please select an interaction !!");
                            return;
                        }
                    }


                    // 【傳送訊息】按鈕的處理
                } else if ($(this).prop("id") == "SendMessage") {
                    // 檢查是否有連線記錄
                    if ($("#Interactions option").length <= 0) {
                        alert("No interactions exist !!");
                        return;
                    }
                    if ($("#Message").val() == "") {
                        alert("Please input message !!");
                        return;
                    }
                    IsTypingMessage = false;
                    Message_LastTime = "";
                }

                // 在 ChatLog 加入一筆記錄
                $('<div>&nbsp;</div>').appendTo($('#ChatLog'));
                parent.hub.server.sendChatCommand(CreateCommandObject($(this).attr('id'))).fail(function (error) {
                    if (error == null || error == "")
                        $('<div>' + CommandObject.AgentId + ":" + CommandObject.Action + '</div>').appendTo($('#ChatLog'));
                    else
                        $('<div>' + CommandObject.AgentId + ":" + CommandObject.Action + " -- " + error + '</div>').appendTo($('#ChatLog'));
                    $('#ChatLog').scrollTop($('#ChatLog')[0].scrollHeight);
                });

                // 清除訊息欄位
                if ($(this).prop("id") == "SendMessage") $("#Message").val("");
            });


            // 選擇某一Interaction時，顯示Interaction相關訊息
            $("#Interactions").change(function () {
                console.log("previousInteraction:" + previousInteraction);
                console.log(arrayChatDialog[previousInteraction]);
                if ($('#Interactions option').length <= 0) {
                    $("#ChatDialog").empty(); $("#UserData").val("");
                    return;
                }

                // parse Interaction相關訊息
                var selectedText = "";
                var selectedItem = $(this).find(":selected");
                if (selectedItem.length <= 0) {
                    $('#Interactions option:first').prop('selected', true);
                    selectedText = $('#Interactions option:first').val();
                } else
                    selectedText = $(this).find(":selected").val();
                ParseInteraction(selectedText);
                var arr = selectedText.split(",");

                // 切換 對談記錄(ChatDialog)
                if (previousInteraction != "" && previousInteraction != arr[0] && $('#ChatDialog').html() != "")
                    arrayChatDialog[previousInteraction] = $('#ChatDialog').html();

                console.log("current Interactions");
                console.log(arrayChatDialog[arr[0]]);
                $('#ChatDialog').html(arrayChatDialog[arr[0]]);
                $('#ChatDialog').scrollTop($('#ChatDialog')[0].scrollHeight);

                previousInteraction = arr[0];

            });
        });

        /* -------------------------------------------------------------------------------------   */
        // 在 ChatLog 加入一筆記錄
        addLog = function (CommandObject, ShowDetailMessage) {
            if (ShowDetailMessage && CommandObject.Result != null && CommandObject.Result != "")
                $('<div>' + CommandObject.AgentId + ":" + CommandObject.EventName + '=' + CommandObject.Result + '</div>').appendTo($('#ChatLog'));
            else
                $('<div>' + CommandObject.AgentId + ":" + CommandObject.EventName + '</div>').appendTo($('#ChatLog'));

            $('#ChatLog').scrollTop($('#ChatLog')[0].scrollHeight);

            // 接受分派後，將 Interaction 狀態改為 已接受(Accepted)
            if (CommandObject.EventName == "AcceptComplete") {
                // 完成後，將 Interaction 移除並清除Interaction相關訊息
                //$("#Reject, #Done").prop("disabled", false);
                var selectedItem = $("#Interactions").find(":selected");
                if (selectedItem != null) {
                    var arrayFields = selectedItem.text().split("/");
                    selectedItem.text(arrayFields[0] + "/Accepted");
                }
                arrayChatDialog[CommandObject.InteractionId] = $("#ChatDialog").html();

            } else if (CommandObject.EventName == "RejectComplete") {
                // 拒絕分派後，將 Interaction 移除並清除Interaction相關訊息
                $("#ChatDialog").empty(); $("#UserData").val("");
                //$("#Interactions").find(":selected").remove();
                $('#Interactions option').filter(function () { return $(this).html() == (CommandObject.InteractionId + "/Invited"); }).remove();

                // 重讀 chat dialog
                $('#Interactions').trigger("change");

            } else if (CommandObject.EventName == "ReleaseComplete" || CommandObject.EventName == "DoneComplete"
                || CommandObject.EventName == "TransferComplete" || CommandObject.EventName.indexOf("ChatPartyRemoved")>=0) {
                // 結束交談
                $("#ChatDialog").empty(); $("#UserData").val("");
                //$("#Interactions").find(":selected").remove();
                if (CommandObject.InteractionId != null && CommandObject.InteractionId != "")
                    $('#Interactions option').filter(function () { return $(this).html() == (CommandObject.InteractionId + "/Accepted"); }).remove();
                else
                    $('#Interactions option:selected').remove(); // remove selected option

                if (previousInteraction == CommandObject.InteractionId) previousInteraction = "";

                // 重讀 chat dialog
                $('#Interactions').trigger("change");

            } else if (CommandObject.EventName == "Revoked" || CommandObject.EventName == "AgentNotReady") { // Timeout for invitation
                $("#AgentReady").show();
                // michael 20161221 marked to allow Reason Change
                //$("#AgentNotReady, #NotReadyDiv").hide();
                $('#UserData').val("");
                //$("#Interactions").find(":selected").remove();
                $('#Interactions option').filter(function () { return $(this).html() == (CommandObject.InteractionId + "/Invited"); }).remove();
                $('#Interactions option').filter(function () { return $(this).html() == (CommandObject.InteractionId + "/Accepted"); }).remove();


                // 重讀 chat dialog
                $('#Interactions').trigger("change");

                // 將所有 Interaction 移除
                //clearAllInteractions();
            }

            // 切換 AgentReady/AgentNotReady 按鈕
            if (CommandObject.Action == "AgentReady") {
                //$("#AgentReady").attr("disabled", true); // disable
                //$("#AgentNotReady").attr("disabled", false); // enable
                $("#AgentReady").hide(); // disable
                $("#AgentNotReady, #NotReadyDiv").show(); // enable
            } else if (CommandObject.Action == "AgentNotReady") {
                //$("#AgentNotReady").attr("disabled", true); // disable
                //$("#AgentReady").attr("disabled", false); // enable
                $("#AgentReady").show(); 
                // michael 20161221 marked to allow Reason Change
                //$("#AgentNotReady, #NotReadyDiv").hide();
            }
        };

        // Chat 錯誤處理
        eventHandler_Chat = function (CommandObject, ShowDetailMessage) {
            if (CommandObject.EventName == "") return;
            //var CommandObject = JSON.parse(CommandObjectString);
            if (ShowDetailMessage && CommandObject.Result != null && CommandObject.Result != "")
                $('<div>' + CommandObject.AgentId + ":" + CommandObject.EventName + '=' + CommandObject.Result + '</div>').appendTo($('#ChatLog'));
            else
                $('<div>' + CommandObject.AgentId + ":" + CommandObject.EventName + '</div>').appendTo($('#ChatLog'));

            $('#ChatLog').scrollTop($('#ChatLog')[0].scrollHeight);
        };

        // 一般錯誤處理
        errorHandler = function (CommandObject) {
            // 有時未正常結束，login後agent處於 ready 狀態，必須顯示 AgentNotReady
            if (CommandObject.Action == "AgentReady" && CommandObject.Result.search("(73)") > 0) {
                //$("#AgentNotReady").trigger("click"); 
                $("#AgentReady").hide();
                $("#AgentNotReady, #NotReadyDiv").show();
            } else if (CommandObject.Action == "AgentNotReady" && CommandObject.Result.search("(74)") > 0) {
                //$("#AgentReady").trigger("click");
                $("#AgentReady").show();
                // michael 20161221 marked to allow Reason Change
                //$("#AgentNotReady, #NotReadyDiv").hide();
            } else if (CommandObject.Result.search("(8199)") > 0) { // error: (8199) Text [str] = "No such session exists"
                var selectedItem = $('#Interactions').find(":selected");
                if (selectedItem.length <= 0) return;
                var selectedText = selectedItem[0].value;
                var arr = selectedText.split(",");
                CommandObject.InteractionId = arr[0];
                clearInteractionsData(arr[0]);

                // to stop abnormal Interaction;
                //$("Done").trigger("click"); // not work
                $('<div>&nbsp;</div>').appendTo($('#ChatLog'));
                parent.hub.server.sendChatCommand(CreateCommandObject("Done")).fail(function (error) {
                    if (error == null || error == "")
                        $('<div>' + CommandObject.AgentId + ":" + CommandObject.Action + '</div>').appendTo($('#ChatLog'));
                    else
                        $('<div>' + CommandObject.AgentId + ":" + CommandObject.Action + " -- " + error + '</div>').appendTo($('#ChatLog'));
                    $('#ChatLog').scrollTop($('#ChatLog')[0].scrollHeight);
                });

                removeInteraction(CommandObject);
            }

            if (CommandObject.Result == null || CommandObject.Result == "")
                $('<div>' + CommandObject.AgentId + ":" + CommandObject.Action + '</div>').appendTo($('#ChatLog'));
            else
                $('<div>' + CommandObject.AgentId + ":" + CommandObject.Action + " -- " + CommandObject.Result + '</div>').appendTo($('#ChatLog'));
            $('#ChatLog').scrollTop($('#ChatLog')[0].scrollHeight);
        };

        // 在ChatDialog加入一筆記錄
        addDialog = function (CommandObject) {
            // get selected value
            var selectedItem = $('#Interactions').find(":selected");
            if (selectedItem.length <= 0) return;
            var selectedText = selectedItem[0].value;
            var arr = selectedText.split(",");
            if (arr[0] == CommandObject.InteractionId) {
                if (CommandObject.Message == null || CommandObject.Message == "") return;
                if (CommandObject.Role == null || CommandObject.Role == "")
                    $('<div>' + getTaipeiTime(CommandObject.OccurredAt) + " " + CommandObject.Message + '</div>').appendTo($('#ChatDialog'));
                else
                    $('<div>' + getTaipeiTime(CommandObject.OccurredAt) + " " + CommandObject.NickName + "(" + CommandObject.Role + "): " + CommandObject.Message + '</div>').appendTo($('#ChatDialog'));

                $('#ChatDialog').scrollTop($('#ChatDialog')[0].scrollHeight);
            } else {
                var newMeesage = "";
                if (CommandObject.Role == null || CommandObject.Role == "")
                    newMeesage = '<div>' + getTaipeiTime(CommandObject.OccurredAt) + " " + CommandObject.Message + '</div>';
                else
                    newMeesage = '<div>' + getTaipeiTime(CommandObject.OccurredAt) + " " + CommandObject.NickName + "(" + CommandObject.Role + "): " + CommandObject.Message + '</div>';
                arrayChatDialog[CommandObject.InteractionId] = arrayChatDialog[CommandObject.InteractionId] + newMeesage;
            }


            // add to chat log too
            eventHandler_Chat(CommandObject);
        };

        // 增加一個連線(Interaction)
        addInteraction = function (CommandObject) {
            if ($('#Interactions option').filter(function () { return $(this).html() == (CommandObject.InteractionId + "/Invited"); }).length <= 0)
                $("#Interactions").append($("<option></option>").attr("value", CommandObject.InteractionId + "," + CommandObject.TicketId + "," + CommandObject.InteractionUserData).text(CommandObject.InteractionId + "/Invited"));

            // 增加一筆對談記錄
            arrayChatDialog[CommandObject.InteractionId] = "";
            if ($("#Interactions option").length == 1) ParseFirstInteraction();
        };

        // 解析第一個連線(Interaction)資料
        ParseFirstInteraction = function () {
            if ($("#Interactions option").length > 0) {
                $('#Interactions option:first').prop('selected', true);
                var arrayFields = $('#Interactions option:first').text().split("/");
                previousInteraction = arrayFields[0];
                // 檢查 interaction 狀態是否為 "Invited"
                //if (arrayFields.length <= 1 || arrayFields[1] != "Invited") {
                //    alert("Please select an INVITED interaction !!");
                //    return;
                //}

                // parse first Interaction相關訊息
                ParseInteraction($('#Interactions option:first').val());
            }
        };

        // 解析連線(Interaction)的UserData
        ParseInteraction = function (selectedText) {
            if (selectedText == null || selectedText == "") {
                $('#UserData').val("");
                return;
            }
            var arr = selectedText.split(",");
            //$('#InteractionId').text(arr[0]);
            //$('#TicketId').text(arr[1]);
            if (arr[2] != undefined && arr[2] != "") {
                var lines = arr[2].split(';');
                var formatedUserData = "";
                for (var i = 0; i < lines.length; i++) formatedUserData += lines[i] + "\n";
                $('#UserData').val(formatedUserData);
            } else
                $('#UserData').val("");
            //$('#ParentInteractionId').text(arr[2]);
        };

        // 解析連線(Interaction)的UserData
        GetContactId = function (userData) {
            if (userData == null || userData == "") {
                return "";
            }
            var lines = userData.split(';');
            for (var i = 0; i < lines.length; i++) {
                var fields = lines[i].split(':');
                if (fields[0] == "ContactId" && fields.length > 1)
                    return fields[1];
            }
            return "";
        };

        // 刪除一個連線(Interaction)
        removeInteraction = function (CommandObject) {
            if ($('#Interactions option').filter(function () { return $(this).html() == (CommandObject.InteractionId + "/Accepted"); }).length > 0)
                $('#Interactions option').filter(function () { return $(this).html() == (CommandObject.InteractionId + "/Accepted"); }).remove();
            else if ($('#Interactions option').filter(function () { return $(this).html() == (CommandObject.InteractionId + "/Invited"); }).length > 0)
                $('#Interactions option').filter(function () { return $(this).html() == (CommandObject.InteractionId + "/Invited"); }).remove();
            if (CommandObject.InteractionId == previousInteraction) 
                clearInteractionsData(CommandObject.InteractionId);
            if (previousInteraction == CommandObject.InteractionId) previousInteraction = "";
        };

        // 清除所有連線(Interaction)
        clearAllInteractions = function () {
            $('#Interactions, #ChatDialog').empty();
            $("#UserData").val("");
        };

        // 清除所有連線(Interaction)
        clearInteractionsData = function (InteractionId) {
            if (InteractionId == previousInteraction) {
                $("#ChatDialog").empty(); $("#UserData").val("");
            }
            delete arrayChatDialog[InteractionId];
        };


        // clear screen
        clearScreen = function (IsLogin) {
            $('#ChatLog, #ChatDialog, #Interactions').empty();
            $('#ChatLog').scrollTop($('#ChatLog')[0].scrollHeight);
            if (IsLogin) {
                $("#AgentReady").show();
                // michael 20161221 marked to allow Reason Change
                //$("#AgentNotReady, #NotReadyDiv").hide();
            }
        };

        // get Current Time 
        getCurrentTime = function (IsLogin) {
            var currentdate = new Date();
            return ((currentdate.getHours() < 10) ? "0" : "") + currentdate.getHours() + ":" + ((currentdate.getMinutes() < 10) ? "0" : "") + currentdate.getMinutes() + ":" + ((currentdate.getSeconds() < 10) ? "0" : "") + currentdate.getSeconds();
        };

        // convert GW Time to Taipei time
        getTaipeiTime = function (OccurredAt) {
            var currentdate = new Date(OccurredAt);

            return ((currentdate.getHours() < 10)?"0":"") + currentdate.getHours() +":"+ ((currentdate.getMinutes() < 10)?"0":"") + currentdate.getMinutes() +":"+ ((currentdate.getSeconds() < 10)?"0":"") + currentdate.getSeconds();
        };
    </script>

</body>
</html>
