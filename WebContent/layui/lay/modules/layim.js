/**

 @Name：layim v3.0 Pro 商用版
 @Author：賢心
 @Site：http://layim.layui.com
 @License：LGPL
    
 */
 
layui.define(['layer', 'laytpl', 'upload'], function(exports){
  
  var v = '3.0 Pro';
  var $ = layui.jquery;
  var layer = layui.layer;
  var laytpl = layui.laytpl;
  var device = layui.device();
  
  var SHOW = 'layui-show', THIS = 'layim-this', MAX_ITEM = 20;
  
  //回檔
  var call = {};
  
  //對外API
  var LAYIM = function(){
    this.v = v;
    $('body').on('click', '*[layim-event]', function(e){
      var othis = $(this), methid = othis.attr('layim-event');
      events[methid] ? events[methid].call(this, othis, e) : '';
    });
  };
  
  //基礎配置
  LAYIM.prototype.config = function(options){
    var skin = [];
    layui.each(Array(5), function(index){
      skin.push(layui.cache.dir+'css/modules/layim/skin/'+ (index+1) +'.jpg')
    });
    options = options || {};
    options.skin = options.skin || [];
    layui.each(options.skin, function(index, item){
      skin.unshift(item);
    });
    options.skin = skin;
    options = $.extend({
      isfriend: !0
      ,isgroup: !0
      ,voice: 'default.wav'
    }, options);
    if(!window.JSON || !window.JSON.parse) return;
    init(options);
    return this;
  };
  
  //監聽事件
  LAYIM.prototype.on = function(events, callback){
    if(typeof callback === 'function'){
      call[events] ? call[events].push(callback) : call[events] = [callback];
    }
    return this;
  };

  //獲取所有緩存資料
  LAYIM.prototype.cache = function(){
    return cache;
  };
  
  //打開一個自訂的會話介面
  LAYIM.prototype.chat = function(data){
    if(!window.JSON || !window.JSON.parse) return;
    return popchat(data), this;
  };
  
  //設置聊天介面最小化
  LAYIM.prototype.setChatMin = function(){
    return setChatMin(), this;
  };
  
  //設置當前會話狀態
  LAYIM.prototype.setChatStatus = function(str){
    var thatChat = thisChat();
    if(!thatChat) return;
    var status = thatChat.elem.find('.layim-chat-status');
    return status.html(str), this;
  };
  
  //接受消息
  LAYIM.prototype.getMessage = function(data){
    return getMessage(data), this;
  };
  
  //桌面消息通知
  LAYIM.prototype.notice = function(data){
    return notice(data), this;
  };
  
  //打開添加好友/群組面板
  LAYIM.prototype.add = function(data){
    return popAdd(data), this;
  };
  
  //好友分組面板
  LAYIM.prototype.setFriendGroup = function(data){
    return popAdd(data, 'setGroup'), this;
  };
  
  //消息盒子的提醒
  LAYIM.prototype.msgbox = function(nums){
    return msgbox(nums), this;
  };
  
  //添加好友/群
  LAYIM.prototype.addList = function(data){
    return addList(data), this;
  };
  
  //刪除好友/群
  LAYIM.prototype.removeList = function(data){
    return removeList(data), this;
  };
  
  //設置好友線上/離線狀態
  LAYIM.prototype.setFriendStatus = function(id, type){
    var list = $('#layim-friend'+ id);
    list[type === 'online' ? 'removeClass' : 'addClass']('layim-list-gray');
  };

  //解析聊天內容
  LAYIM.prototype.content = function(content){
    return layui.data.content(content);
  };


  //主範本
  var listTpl = function(options){
    var nodata = {
      friend: "該分組下暫無好友"
      ,group: "暫無群組"
      ,history: "暫無歷史會話"
    };

    options = options || {};
    options.item = options.item || ('d.' + options.type);

    return ['{{# var length = 0; layui.each('+ options.item +', function(i, data){ length++; }}'
      ,'<li layim-event="chat" data-type="'+ options.type +'" data-index="{{ '+ (options.index||'i') +' }}" id="layim-'+ options.type +'{{ data.id }}" {{ data.status === "offline" ? "class=layim-list-gray" : "" }}><img src="{{ data.avatar }}"><span>{{ data.username||data.groupname||data.name||"佚名" }}</span><p>{{ data.remark||data.sign||"" }}</p></li>'
    ,'{{# }); if(length === 0){ }}'
      ,'<li class="layim-null">'+ (nodata[options.type] || "暫無數據") +'</li>'
    ,'{{# } }}'].join('');
  };

  var elemTpl = ['<div class="layui-layim-main">'
    ,'<div class="layui-layim-info">'
      ,'<div class="layui-layim-user">{{ d.mine.username }}</div>'
      ,'<div class="layui-layim-status">'
        ,'{{# if(d.mine.status === "online"){ }}'
        ,'<span class="layui-icon layim-status-online" layim-event="status" lay-type="show">&#xe617;</span>'
        ,'{{# } else if(d.mine.status === "hide") { }}'
        ,'<span class="layui-icon layim-status-hide" layim-event="status" lay-type="show">&#xe60f;</span>'
        ,'{{# } }}'
        ,'<ul class="layui-anim layim-menu-box">'
          ,'<li {{d.mine.status === "online" ? "class=layim-this" : ""}} layim-event="status" lay-type="online"><i class="layui-icon">&#xe618;</i><cite class="layui-icon layim-status-online">&#xe617;</cite>線上</li>'
          ,'<li {{d.mine.status === "hide" ? "class=layim-this" : ""}} layim-event="status" lay-type="hide"><i class="layui-icon">&#xe618;</i><cite class="layui-icon layim-status-hide">&#xe60f;</cite>隱身</li>'
        ,'</ul>'
      ,'</div>'
      ,'<input class="layui-layim-remark" placeholder="編輯簽名" value="{{ d.mine.remark||d.mine.sign||"" }}"></p>'
    ,'</div>'
    ,'<ul class="layui-unselect layui-layim-tab{{# if(!d.base.isfriend || !d.base.isgroup){ }}'
      ,' layim-tab-two'
    ,'{{# } }}">'
      ,'<li class="layui-icon'
      ,'{{# if(!d.base.isfriend){ }}'
      ,' layim-hide'
      ,'{{# } else { }}'
      ,' layim-this'
      ,'{{# } }}'
      ,'" title="連絡人" layim-event="tab" lay-type="friend">&#xe612;</li>'
      ,'<li class="layui-icon'
      ,'{{# if(!d.base.isgroup){ }}'
      ,' layim-hide'
      ,'{{# } else if(!d.base.isfriend) { }}'
      ,' layim-this'
      ,'{{# } }}'
      ,'" title="群組" layim-event="tab" lay-type="group">&#xe613;</li>'
      ,'<li class="layui-icon" title="歷史會話" layim-event="tab" lay-type="history">&#xe611;</li>'
    ,'</ul>'
    ,'<ul class="layui-unselect layim-tab-content {{# if(d.base.isfriend){ }}layui-show{{# } }} layim-list-friend">'
    ,'{{# layui.each(d.friend, function(index, item){ var spread = d.local["spread"+index]; }}'
      ,'<li>'
        ,'<h5 layim-event="spread" lay-type="{{ spread }}"><i class="layui-icon">{{# if(spread === "true"){ }}&#xe61a;{{# } else {  }}&#xe602;{{# } }}</i><span>{{ item.groupname||"未命名分組"+index }}</span><em>(<cite class="layim-count"> {{ (item.list||[]).length }}</cite>)</em></h5>'
        ,'<ul class="layui-layim-list {{# if(spread === "true"){ }}'
        ,' layui-show'
        ,'{{# } }}">'
        ,listTpl({
          type: "friend"
          ,item: "item.list"
          ,index: "index"
        })
        ,'</ul>'
      ,'</li>'
    ,'{{# }); if(d.friend.length === 0){ }}'
      ,'<li><ul class="layui-layim-list layui-show"><li class="layim-null">暫無連絡人</li></ul>'
    ,'{{# } }}'
    ,'</ul>'
    ,'<ul class="layui-unselect layim-tab-content {{# if(!d.base.isfriend && d.base.isgroup){ }}layui-show{{# } }}">'
      ,'<li>'
        ,'<ul class="layui-layim-list layui-show layim-list-group">'
        ,listTpl({
          type: 'group'
        })
        ,'</ul>'
      ,'</li>'
    ,'</ul>'
    ,'<ul class="layui-unselect layim-tab-content  {{# if(!d.base.isfriend && !d.base.isgroup){ }}layui-show{{# } }}">'
      ,'<li>'
        ,'<ul class="layui-layim-list layui-show layim-list-history">'
        ,listTpl({
          type: 'history'
        })
        ,'</ul>'
      ,'</li>'
    ,'</ul>'
    ,'<ul class="layui-unselect layim-tab-content">'
      ,'<li>'
        ,'<ul class="layui-layim-list layui-show" id="layui-layim-search"></ul>'
      ,'</li>'
    ,'</ul>'
    ,'<ul class="layui-unselect layui-layim-tool">'
      ,'<li class="layui-icon layim-tool-search" layim-event="search" title="搜索">&#xe615;</li>'
      ,'{{# if(d.base.msgbox){ }}'
      ,'<li class="layui-icon layim-tool-msgbox" layim-event="msgbox" title="消息盒子">&#xe645;<span class="layui-anim"></span></li>'
      ,'{{# } }}'
      ,'{{# if(d.base.find){ }}'
      ,'<li class="layui-icon layim-tool-find" layim-event="find" title="查找">&#xe608;</li>'
      ,'{{# } }}'
      ,'<li class="layui-icon layim-tool-skin" layim-event="skin" title="更換背景">&#xe61b;</li>'
      ,'{{# if(!d.base.copyright){ }}'
      ,'<li class="layui-icon layim-tool-about" layim-event="about" title="關於">&#xe60b;</li>'
      ,'{{# } }}'
    ,'</ul>'
    ,'<div class="layui-layim-search"><input><label class="layui-icon" layim-event="closeSearch">&#x1007;</label></div>'
  ,'</div>'].join('');
  
  //換膚模版
  var elemSkinTpl = ['<ul class="layui-layim-skin">'
  ,'{{# layui.each(d.skin, function(index, item){ }}'
    ,'<li><img layim-event="setSkin" src="{{ item }}"></li>'
  ,'{{# }); }}'
  ,'<li layim-event="setSkin"><cite>簡約</cite></li>'
  ,'</ul>'].join('');
  
  //聊天主範本
  var elemChatTpl = ['<div class="layim-chat layim-chat-{{d.data.type}}{{d.first ? " layui-show" : ""}}">'
    ,'<div class="layui-unselect layim-chat-title">'
      ,'<div class="layim-chat-other">'
        ,'<img src="{{ d.data.avatar }}"><span class="layim-chat-username" layim-event="{{ d.data.type==="group" ? \"groupMembers\" : \"\" }}">{{ d.data.name||"佚名" }} {{d.data.temporary ? "<cite>臨時會話</cite>" : ""}} {{# if(d.data.type==="group"){ }} <em class="layim-chat-members"></em><i class="layui-icon">&#xe61a;</i> {{# } }}</span>'
        ,'<p class="layim-chat-status"></p>'
      ,'</div>'
    ,'</div>'
    ,'<div class="layim-chat-main">'
      ,'<ul></ul>'
    ,'</div>'
    ,'<div class="layim-chat-footer">'
      ,'<div class="layui-unselect layim-chat-tool" data-json="{{encodeURIComponent(JSON.stringify(d.data))}}">'
        ,'<span class="layui-icon layim-tool-face" title="選擇表情" layim-event="face">&#xe60c;</span>'
        ,'{{# if(d.base && d.base.uploadImage){ }}'
        ,'<span class="layui-icon layim-tool-image" title="上傳圖片" layim-event="image">&#xe60d;<input type="file" name="file"></span>'
        ,'{{# }; }}'
        ,'{{# if(d.base && d.base.uploadFile){ }}'
        ,'<span class="layui-icon layim-tool-image" title="發送文件" layim-event="image" data-type="file">&#xe61d;<input type="file" name="file"></span>'
         ,'{{# }; }}'
         ,'{{# layui.each(d.base.tool, function(index, item){ }}'
        ,'<span class="layui-icon layim-tool-{{item.alias}}" title="{{item.title}}" layim-event="extend" lay-filter="{{ item.alias }}">{{item.icon}}</span>'
         ,'{{# }); }}'
        ,'{{# if(d.base && d.base.chatLog){ }}'
        ,'<span class="layim-tool-log" layim-event="chatLog"><i class="layui-icon">&#xe60e;</i>聊天記錄</span>'
        ,'{{# }; }}'
      ,'</div>'
      ,'<div class="layim-chat-textarea"><textarea></textarea></div>'
      ,'<div class="layim-chat-bottom">'
        ,'<div class="layim-chat-send">'
          ,'{{# if(!d.base.brief){ }}'
          ,'<span class="layim-send-close" layim-event="closeThisChat">關閉</span>'
          ,'{{# } }}'
          ,'<span class="layim-send-btn" layim-event="send">發送</span>'
          ,'<span class="layim-send-set" layim-event="setSend" lay-type="show"><em class="layui-edge"></em></span>'
          ,'<ul class="layui-anim layim-menu-box">'
            ,'<li {{d.local.sendHotKey !== "Ctrl+Enter" ? "class=layim-this" : ""}} layim-event="setSend" lay-type="Enter"><i class="layui-icon">&#xe618;</i>按Enter鍵發送消息</li>'
            ,'<li {{d.local.sendHotKey === "Ctrl+Enter" ? "class=layim-this" : ""}} layim-event="setSend"  lay-type="Ctrl+Enter"><i class="layui-icon">&#xe618;</i>按Ctrl+Enter鍵發送消息</li>'
          ,'</ul>'
        ,'</div>'
      ,'</div>'
    ,'</div>'
  ,'</div>'].join('');
  
  //添加好友群組模版
  var elemAddTpl = ['<div class="layim-add-box">'
    ,'<div class="layim-add-img"><img class="layui-circle" src="{{ d.data.avatar }}"><p>{{ d.data.name||"" }}</p></div>'
    ,'<div class="layim-add-remark">'
    ,'{{# if(d.data.type === "friend" && d.type === "setGroup"){ }}'
      ,'<p>選擇分組</p>'
    ,'{{# } if(d.data.type === "friend"){ }}'
    ,'<select class="layui-select" id="LAY_layimGroup">'
      ,'{{# layui.each(d.data.group, function(index, item){ }}'
      ,'<option value="{{ item.id }}">{{ item.groupname }}</option>'
      ,'{{# }); }}'
    ,'</select>'
    ,'{{# } }}'
    ,'{{# if(d.data.type === "group"){ }}'
      ,'<p>請輸入驗證資訊</p>'
    ,'{{# } if(d.type !== "setGroup"){ }}'
      ,'<textarea id="LAY_layimRemark" placeholder="驗證信息" class="layui-textarea"></textarea>'
    ,'{{# } }}'
    ,'</div>'
  ,'</div>'].join('');
  
  //補齊數位
  var digit = function(num){
    return num < 10 ? '0' + (num|0) : num;
  };
  
  //轉換時間
  layui.data.date = function(timestamp){
    var d = new Date(timestamp||new Date());
    return d.getFullYear() + '-' + digit(d.getMonth() + 1) + '-' + digit(d.getDate())
    + ' ' + digit(d.getHours()) + ':' + digit(d.getMinutes()) + ':' + digit(d.getSeconds());
  };
  
  //轉換內容
  layui.data.content = function(content){
    //支持的html標籤
    var html = function(end){
      return new RegExp('\\n*\\['+ (end||'') +'(pre|div|p|table|thead|th|tbody|tr|td|ul|li|ol|li|dl|dt|dd|h2|h3|h4|h5)([\\s\\S]*?)\\]\\n*', 'g');
    };
    content = (content||'').replace(/&(?!#?[a-zA-Z0-9]+;)/g, '&amp;')
    .replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/'/g, '&#39;').replace(/"/g, '&quot;') //XSS
    .replace(/@(\S+)(\s+?|$)/g, '@<a href="javascript:;">$1</a>$2') //轉義@
    .replace(/\s{2}/g, '&nbsp') //轉義空格
    .replace(/img\[([^\s]+?)\]/g, function(img){  //轉義圖片
      return '<img class="layui-layim-photos" src="' + img.replace(/(^img\[)|(\]$)/g, '') + '">';
    })
    .replace(/file\([\s\S]+?\)\[[\s\S]*?\]/g, function(str){ //轉義文件
      var href = (str.match(/file\(([\s\S]+?)\)\[/)||[])[1];
      var text = (str.match(/\)\[([\s\S]*?)\]/)||[])[1];
      if(!href) return str;
      return '<a class="layui-layim-file" href="'+ href +'" target="_blank"><i class="layui-icon">&#xe61e;</i><cite>'+ (text||href) +'</cite></a>';
    })
    .replace(/face\[([^\s\[\]]+?)\]/g, function(face){  //轉義表情
      var alt = face.replace(/^face/g, '');
      return '<img alt="'+ alt +'" title="'+ alt +'" src="' + faces[alt] + '">';
    }).replace(/a\([\s\S]+?\)\[[\s\S]*?\]/g, function(str){ //轉義連結
      var href = (str.match(/a\(([\s\S]+?)\)\[/)||[])[1];
      var text = (str.match(/\)\[([\s\S]*?)\]/)||[])[1];
      if(!href) return str;
      return '<a href="'+ href +'" target="_blank">'+ (text||href) +'</a>';
    }).replace(html(), '\<$1 $2\>').replace(html('/'), '\</$1\>') //轉移HTML代碼
    .replace(/\n/g, '<br>') //轉義換行 
    return content;
  };
  
  var elemChatMain = ['<li {{ d.mine ? "class=layim-chat-mine" : "" }}>'
    ,'<div class="layim-chat-user"><img src="{{ d.avatar }}"><cite>'
    ,'{{# if(d.mine){ }}'
      ,'<i>{{ layui.data.date(d.timestamp) }}</i>{{ d.username||"佚名" }}'
     ,'{{# } else { }}'
      ,'{{ d.username||"佚名" }}<i>{{ layui.data.date(d.timestamp) }}</i>'
     ,'{{# } }}'
      ,'</cite></div>'
    ,'<div class="layim-chat-text">{{ layui.data.content(d.content||"&nbsp") }}</div>'
  ,'</li>'].join('');
  
  var elemChatList = '<li class="layim-chatlist-{{ d.data.type }}{{ d.data.id }} layim-this" layim-event="tabChat"><img src="{{ d.data.avatar }}"><span>{{ d.data.name||"佚名" }}</span>{{# if(!d.base.brief){ }}<i class="layui-icon" layim-event="closeChat">&#x1007;</i>{{# } }}</li>';
  
  //Ajax
  var post = function(options, callback, tips){
    options = options || {};
    return $.ajax({
      url: options.url
      ,type: options.type || 'get'
      ,data: options.data
      ,dataType: options.dataType || 'json'
      ,cache: false
      ,success: function(res){
        res.code == 0 
          ? callback && callback(res.data||{})
        : layer.msg(res.msg || ((tips||'Error') + ': LAYIM_NOT_GET_DATA'), {
          time: 5000
        });
      },error: function(err, msg){
        window.console && console.log && console.error('LAYIM_DATE_ERROR：' + msg);
      }
    });
  };
  
  //處理初始化資訊
  var cache = {message: {}, chat: []}, init = function(options){
    var init = options.init || {}
     mine = init.mine || {}
    ,local = layui.data('layim')[mine.id] || {}
    ,obj = {
      base: options
      ,local: local
      ,mine: mine
      ,history: local.history || {}
    }, create = function(data){
      var mine = data.mine || {};
      var local = layui.data('layim')[mine.id] || {}, obj = {
        base: options //基礎配置資訊
        ,local: local //本地數據
        ,mine:  mine //我的使用者資訊
        ,friend: data.friend || [] //連絡人信息
        ,group: data.group || [] //群組信息
        ,history: local.history || {} //歷史會話資訊
      };
      cache = $.extend(cache, obj);
      popim(laytpl(elemTpl).render(obj));
      if(local.close || options.min){
        popmin();
      }
      layui.each(call.ready, function(index, item){
        item && item(obj);
      });
    };
    cache = $.extend(cache, obj);
    if(options.brief){
      return layui.each(call.ready, function(index, item){
        item && item(obj);
      });
    };
    init.url ? post(init, create, 'INIT') : create(init);
  };
  
  //顯示主面板
  var layimMain, popim = function(content){
    return layer.open({
      type: 1
      ,area: ['260px', '520px']
      ,skin: 'layui-box layui-layim'
      ,title: '&#8203;'
      ,offset: 'rb'
      ,id: 'layui-layim'
      ,shade: false
      ,anim: 2
      ,resize: false
      ,content: content
      ,success: function(layero){
        layimMain = layero;

        setSkin(layero);
        
        if(cache.base.right){
          layero.css('margin-left', '-' + cache.base.right);
        }
        if(layimClose){
          layer.close(layimClose.attr('times'));
        }

        //按最新會話重新排列
        var arr = [], historyElem = layero.find('.layim-list-history');
        historyElem.find('li').each(function(){
          arr.push($(this).prop('outerHTML'))
        });
        if(arr.length > 0){
          arr.reverse();
          historyElem.html(arr.join(''));
        }
        
        banRightMenu();
        events.sign();
      }
      ,cancel: function(index){
        popmin();
        var local = layui.data('layim')[cache.mine.id] || {};
        local.close = true;
        layui.data('layim', {
          key: cache.mine.id
          ,value: local
        });
        return false;
      }
    });
  };
  
  //遮罩主面板右鍵菜單
  var banRightMenu = function(){
    layimMain.on('contextmenu', function(event){
      event.cancelBubble = true 
      event.returnValue = false;
      return false; 
    });
    
    var hide = function(){
      layer.closeAll('tips');
    };
    
    //自訂歷史會話右鍵菜單
    layimMain.find('.layim-list-history').on('contextmenu', 'li', function(e){
      var othis = $(this);
      var html = '<ul data-id="'+ othis[0].id +'" data-index="'+ othis.data('index') +'"><li layim-event="menuHistory" data-type="one">移除該會話</li><li layim-event="menuHistory" data-type="all">清空全部會話清單</li></ul>';
      
      if(othis.hasClass('layim-null')) return;
      
      layer.tips(html, this, {
        tips: 1
        ,time: 0
        ,anim: 5
        ,fixed: true
        ,skin: 'layui-box layui-layim-contextmenu'
        ,success: function(layero){
          var stopmp = function(e){ stope(e); };
          layero.off('mousedown', stopmp).on('mousedown', stopmp);
        }
      });
      $(document).off('mousedown', hide).on('mousedown', hide);
      $(window).off('resize', hide).on('resize', hide);
      
    });
  }
  
  //主面板最小化狀態
  var layimClose, popmin = function(content){
    if(layimClose){
      layer.close(layimClose.attr('times'));
    }
    if(layimMain){
      layimMain.hide();
    }
    cache.mine = cache.mine || {};
    return layer.open({
      type: 1
      ,title: false
      ,id: 'layui-layim-close'
      ,skin: 'layui-box layui-layim-min layui-layim-close'
      ,shade: false
      ,closeBtn: false
      ,anim: 2
      ,offset: 'rb'
      ,resize: false
      ,content: '<img src="'+ (cache.mine.avatar||(layui.cache.dir+'css/pc/layim/skin/logo.jpg')) +'"><span>'+ (content||cache.base.title||'我的LayIM') +'</span>'
      ,move: '#layui-layim-close img'
      ,success: function(layero, index){
        layimClose = layero;
        if(cache.base.right){
          layero.css('margin-left', '-' + cache.base.right);
        }
        layero.on('click', function(){
          layer.close(index);
          layimMain.show();
          var local = layui.data('layim')[cache.mine.id] || {};
          delete local.close;
          layui.data('layim', {
            key: cache.mine.id
            ,value: local
          });
        });
      }
    });
  };
  
  //顯示聊天面板
  var layimChat, layimMin, chatIndex, To = {}, popchat = function(data){
    data = data || {};
    
    var chat = $('#layui-layim-chat'), render = {
      data: data
      ,base: cache.base
      ,local: cache.local
    };

    if(!data.id){
      return layer.msg('非法用戶');
    }

    if(chat[0]){
      var list = layimChat.find('.layim-chat-list');
      var listThat = list.find('.layim-chatlist-'+ data.type + data.id);
      var hasFull = layimChat.find('.layui-layer-max').hasClass('layui-layer-maxmin');
      var chatBox = chat.children('.layim-chat-box');
      
      //如果是最小化，則還原視窗
      if(layimChat.css('display') === 'none'){
        layimChat.show();
      }
      
      if(layimMin){
        layer.close(layimMin.attr('times'));
      }
      
      //如果出現多個聊天面板
      if(list.find('li').length === 1 && !listThat[0]){
        hasFull || layimChat.css('width', 800);
        list.css({
          height: layimChat.height()
        }).show();
        chatBox.css('margin-left', '200px');
      }
      
      //打開的是非當前聊天面板，則新增面板
      if(!listThat[0]){
        list.append(laytpl(elemChatList).render(render));
        chatBox.append(laytpl(elemChatTpl).render(render));
        resizeChat();
      }

      changeChat(list.find('.layim-chatlist-'+ data.type + data.id));
      listThat[0] || viewChatlog();
      setHistory(data);
      hotkeySend();
      
      return chatIndex;
    }
    
    render.first = !0;
    
    var index = chatIndex = layer.open({
      type: 1
      ,area: '600px'
      ,skin: 'layui-box layui-layim-chat'
      ,id: 'layui-layim-chat'
      ,title: '&#8203;'
      ,shade: false
      ,maxmin: true
      ,closeBtn: cache.base.brief ? false : 1
      ,content: laytpl('<ul class="layui-unselect layim-chat-list">'+ elemChatList +'</ul><div class="layim-chat-box">' + elemChatTpl + '</div>').render(render)
      ,success: function(layero){
        layimChat = layero;
        
        layero.css({
          'min-width': '500px'
          ,'min-height': '420px'
        });
        
        hotkeySend();
        setSkin(layero);
        setHistory(data);
        
        viewChatlog();
        showOffMessage();
        
        //聊天視窗的切換監聽
        layui.each(call.chatChange, function(index, item){
          item && item(thisChat());
        });
        
        //查看大圖
        layero.on('dblclick', '.layui-layim-photos', function(){
          var src = this.src;
          layer.close(popchat.photosIndex);
          layer.photos({
            photos: {
              data: [{
                "alt": "大圖模式",
                "src": src
              }]
            }
            ,shade: 0.01
            ,closeBtn: 2
            ,anim: 0
            ,resize: false
            ,success: function(layero, index){
               popchat.photosIndex = index;
            }
          });
        });
      }
      ,full: function(layero){
        layer.style(index, {
          width: '100%'
          ,height: '100%'
        }, true);
        resizeChat();
      }
      ,resizing: resizeChat
      ,restore: resizeChat
      ,min: function(){
        setChatMin();
        return false;
      }
      ,end: function(){
        layer.closeAll('tips');
        layimChat = null;
      }
    });
    return index;
  };
  
  //重置聊天窗口大小
  var resizeChat = function(){
    var list = layimChat.find('.layim-chat-list')
    ,chatMain = layimChat.find('.layim-chat-main')
    ,chatHeight = layimChat.height();
    list.css({
      height: chatHeight
    });
    chatMain.css({
      height: chatHeight - 20 - 80 - 158
    })
  };

  //設置聊天視窗最小化 & 新消息提醒
  var setChatMin = function(newMsg){
    var thatChat = newMsg || thisChat().data, base = layui.layim.cache().base;
    if(layimChat && !newMsg){
      layimChat.hide();
    }
    layer.close(setChatMin.index);    
    setChatMin.index = layer.open({
      type: 1
      ,title: false
      ,skin: 'layui-box layui-layim-min'
      ,shade: false
      ,closeBtn: false
      ,anim: thatChat.anim || 2
      ,offset: 'b'
      ,move: '#layui-layim-min'
      ,resize: false
      ,area: ['182px', '50px']
      ,content: '<img id="layui-layim-min" src="'+ thatChat.avatar +'"><span>'+ thatChat.name +'</span>'
      ,success: function(layero, index){
        if(!newMsg) layimMin = layero;
        
        if(base.minRight){
          layer.style(index, {
            left: $(window).width() - layero.outerWidth() - parseFloat(base.minRight)
          });
        }
        
        layero.find('.layui-layer-content span').on('click', function(){
          layer.close(index);
          newMsg ? layui.each(cache.chat, function(i, item){
            popchat(item);
          }) : layimChat.show();
          if(newMsg){
            cache.chat = [];
            chatListMore();
          }
        });
        layero.find('.layui-layer-content img').on('click', function(e){
          stope(e);
        });
      }
    });
  };
  
  //打開添加好友、群組面板、好友分組面板
  var popAdd = function(data, type){
    data = data || {};
    layer.close(popAdd.index);
    return popAdd.index = layer.open({
      type: 1
      ,area: '430px'
      ,title: {
        friend: '添加好友'
        ,group: '加入群組'
      }[data.type] || ''
      ,shade: false
      ,resize: false
      ,btn: type ? ['確認', '取消'] : ['發送申請', '關閉']
      ,content: laytpl(elemAddTpl).render({
        data: {
          name: data.username || data.groupname
          ,avatar: data.avatar
          ,group: data.group || parent.layui.layim.cache().friend || []
          ,type: data.type
        }
        ,type: type
      })
      ,yes: function(index, layero){
        var groupElem = layero.find('#LAY_layimGroup')
        ,remarkElem = layero.find('#LAY_layimRemark')
        if(type){
          data.submit && data.submit(groupElem.val(), index);
        } else {
          data.submit && data.submit(groupElem.val(), remarkElem.val(), index);
        }
      }
    });
  };
  
  //切換聊天
  var changeChat = function(elem, del){
    elem = elem || $('.layim-chat-list .' + THIS);
    var index = elem.index() === -1 ? 0 : elem.index();
    var str = '.layim-chat', cont = layimChat.find(str).eq(index);
    var hasFull = layimChat.find('.layui-layer-max').hasClass('layui-layer-maxmin');

    if(del){
      
      //如果關閉的是當前聊天，則切換聊天焦點
      if(elem.hasClass(THIS)){
        changeChat(index === 0 ? elem.next() : elem.prev());
      }
      
      elem.remove();
      cont.remove();
      
      var length = layimChat.find(str).length;
      
      //只剩下1個列表，隱藏左側區塊
      if(length === 1){
        layimChat.find('.layim-chat-list').hide();
        if(!hasFull){
          layimChat.css('width', '600px');
        }
        layimChat.find('.layim-chat-box').css('margin-left', 0);
      }
      
      //聊天清單不存在，則關閉聊天介面
      if(length === 0){      
        layer.close(chatIndex);
      }
      
      return false;
    }
    
    elem.addClass(THIS).siblings().removeClass(THIS);
    cont.addClass(SHOW).siblings(str).removeClass(SHOW);
    cont.find('textarea').focus();
    
    //聊天視窗的切換監聽
    layui.each(call.chatChange, function(index, item){
      item && item(thisChat());
    });
    showOffMessage();
  };
  
  //展示存在佇列中的消息
  var showOffMessage = function(){
    var thatChat = thisChat();
    var message = cache.message[thatChat.data.type + thatChat.data.id];
    if(message){
      //展現後，刪除佇列中消息
      delete cache.message[thatChat.data.type + thatChat.data.id];
    }
  };
  
  //獲取當前聊天面板
  var thisChat = function(){
    if(!layimChat) return;
    var index = $('.layim-chat-list .' + THIS).index();
    var cont = layimChat.find('.layim-chat').eq(index);
    var to = JSON.parse(decodeURIComponent(cont.find('.layim-chat-tool').data('json')));
    return {
      elem: cont
      ,data: to
      ,textarea: cont.find('textarea')
    };
  };
  
  //記錄初始背景
  var setSkin = function(layero){
    var local = layui.data('layim')[cache.mine.id] || {}
    ,skin = local.skin;
    layero.css({
      'background-image': skin ? 'url('+ skin +')' : function(){
        return cache.base.initSkin 
          ? 'url('+ (layui.cache.dir+'css/modules/layim/skin/'+ cache.base.initSkin) +')'
        : 'none';
      }()
    });
  };

  //記錄歷史會話
  var setHistory = function(data){
    var local = layui.data('layim')[cache.mine.id] || {};
    var obj = {}, history = local.history || {};
    var is = history[data.type + data.id];
    
    if(!layimMain) return;
    
    var historyElem = layimMain.find('.layim-list-history');

    data.historyTime = new Date().getTime();
    history[data.type + data.id] = data;
  
    local.history = history;
    
    layui.data('layim', {
      key: cache.mine.id
      ,value: local
    });

    if(is) return;

    obj[data.type + data.id] = data;
    var historyList = laytpl(listTpl({
      type: 'history'
      ,item: 'd.data'
    })).render({data: obj});
    historyElem.prepend(historyList);
    historyElem.find('.layim-null').remove();
  };
  
  //發送消息
  var sendMessage = function(){
    var data = {
      username: cache.mine ? cache.mine.username : '訪客'
      ,avatar: cache.mine ? cache.mine.avatar : (layui.cache.dir+'css/pc/layim/skin/logo.jpg')
      ,id: cache.mine ? cache.mine.id : null
      ,mine: true
    };
    var thatChat = thisChat(), ul = thatChat.elem.find('.layim-chat-main ul');
    var maxLength = cache.base.maxLength || 3000;
    data.content = thatChat.textarea.val();
    if(data.content.replace(/\s/g, '') !== ''){
      
      if(data.content.length > maxLength){
        return layer.msg('內容最長不能超過'+ maxLength +'個字元')
      }
      
      ul.append(laytpl(elemChatMain).render(data));
      
      var param = {
        mine: data
        ,to: thatChat.data
      }, message = {
        username: param.mine.username
        ,avatar: param.mine.avatar
        ,id: param.to.id
        ,type: param.to.type
        ,content: param.mine.content
        ,timestamp: new Date().getTime()
        ,mine: true
      };
      pushChatlog(message);
      
      layui.each(call.sendMessage, function(index, item){
        item && item(param);
      });
    }
    chatListMore();
    thatChat.textarea.val('').focus();
  };
  
  //桌面消息提醒
  var notice = function(data){
    data = data || {};
    if (window.Notification){
      if(Notification.permission === 'granted'){
        var notification = new Notification(data.title||'', {
          body: data.content||''
          ,icon: data.avatar||'http://tp2.sinaimg.cn/5488749285/50/5719808192/1'
        });
      }else {
        Notification.requestPermission();
      };
    }
  };
  
  //消息聲音提醒
  var voice = function() {
    if(device.ie && device.ie < 9) return;
    var audio = document.createElement("audio");
    audio.src = layui.cache.dir+'css/modules/layim/voice/'+ cache.base.voice;
    audio.play();
  };
  
  //接受消息
  var messageNew = {}, getMessage = function(data){
    data = data || {};
    
    var elem = $('.layim-chatlist-'+ data.type + data.id);
    var group = {}, index = elem.index();
    
    data.timestamp = data.timestamp || new Date().getTime();
    data.system || pushChatlog(data);
    messageNew = JSON.parse(JSON.stringify(data));
    
    if(cache.base.voice){
      voice();
    }
    
    if((!layimChat && data.content) || index === -1){
      if(cache.message[data.type + data.id]){
        cache.message[data.type + data.id].push(data)
      } else {
        cache.message[data.type + data.id] = [data];
        
        //記錄聊天面板佇列
        if(data.type === 'friend'){
          var friend;
          layui.each(cache.friend, function(index1, item1){
            layui.each(item1.list, function(index, item){
              if(item.id == data.id){
                item.type = 'friend';
                item.name = item.username;
                cache.chat.push(item);
                return friend = true;
              }
            });
            if(friend) return true;
          });
          if(!friend){
            data.name = data.username;
            data.temporary = true; //臨時會話
            cache.chat.push(data);
          }
        } else if(data.type === 'group'){
          var isgroup;
          layui.each(cache.group, function(index, item){
            if(item.id == data.id){
              item.type = 'group';
              item.name = item.groupname;
              cache.chat.push(item);
              return isgroup = true;
            }
          });
          if(!isgroup){
            data.name = data.groupname;
            cache.chat.push(data);
          }
        } else {
          data.name = data.name || data.username || data.groupname;
          cache.chat.push(data);
        }
      }
      if(data.type === 'group'){
        layui.each(cache.group, function(index, item){
          if(item.id == data.id){
            group.avatar = item.avatar;
            return true;
          }
        });
      }
      if(!data.system){
        if(cache.base.notice){
          notice({
            title: '來自 '+ data.username +' 的消息'
            ,content: data.content
            ,avatar: group.avatar || data.avatar
          });
        }
        return setChatMin({
          name: '收到新消息'
          ,avatar: group.avatar || data.avatar
          ,anim: 6
        });
      }
    }

    if(!layimChat) return;
    
    //接受到的消息不在當前Tab
    var thatChat = thisChat();
    if(thatChat.data.type + thatChat.data.id !== data.type + data.id){
      elem.addClass('layui-anim layer-anim-06');
      setTimeout(function(){
        elem.removeClass('layui-anim layer-anim-06')
      }, 300);
    }
    
    var cont = layimChat.find('.layim-chat').eq(index);
    var ul = cont.find('.layim-chat-main ul');
    
    //系統消息
    if(data.system){
      if(index !== -1){
        ul.append('<li class="layim-chat-system"><span>'+ data.content +'</span></li>');
      }
    } else if(data.content.replace(/\s/g, '') !== ''){
      ul.append(laytpl(elemChatMain).render(data));
    }
    
    chatListMore();
  };
  
  //消息盒子的提醒
  var ANIM_MSG = 'layui-anim-loop layer-anim-05', msgbox = function(num){
    var msgboxElem = layimMain.find('.layim-tool-msgbox');
    msgboxElem.find('span').addClass(ANIM_MSG).html(num);
  };
  
  //存儲最近MAX_ITEM條聊天記錄到本地
  var pushChatlog = function(message){
    var local = layui.data('layim')[cache.mine.id] || {};
    var chatlog = local.chatlog || {};
    if(chatlog[message.type + message.id]){
      chatlog[message.type + message.id].push(message);
      if(chatlog[message.type + message.id].length > MAX_ITEM){
        chatlog[message.type + message.id].shift();
      }
    } else {
      chatlog[message.type + message.id] = [message];
    }
    local.chatlog = chatlog;
    layui.data('layim', {
      key: cache.mine.id
      ,value: local
    });
  };
  
  //渲染本地最新聊天記錄到相應面板
  var viewChatlog = function(){
    var local = layui.data('layim')[cache.mine.id] || {};
    var thatChat = thisChat(), chatlog = local.chatlog || {};
    var ul = thatChat.elem.find('.layim-chat-main ul');
    layui.each(chatlog[thatChat.data.type + thatChat.data.id], function(index, item){
      ul.append(laytpl(elemChatMain).render(item));
    });
    chatListMore();
  };

  //添加好友或群
  var addList = function(data){
    var obj = {}, has, listElem = layimMain.find('.layim-list-'+ data.type);
    
    if(cache[data.type]){
      if(data.type === 'friend'){
        layui.each(cache.friend, function(index, item){
          if(data.groupid == item.id){
            //檢查好友是否已經在列表中
            layui.each(cache.friend[index].list, function(idx, itm){
              if(itm.id == data.id){
                return has = true
              }
            });
            if(has) return layer.msg('好友 ['+ (data.username||'') +'] 已經存在列表中',{anim: 6});
            cache.friend[index].list = cache.friend[index].list || [];
            obj[cache.friend[index].list.length] = data;
            data.groupIndex = index;
            cache.friend[index].list.push(data); //在cache的friend裡面也增加好友
            return true;
          }
        });
      } else if(data.type === 'group'){
        //檢查群組是否已經在列表中
        layui.each(cache.group, function(idx, itm){
          if(itm.id == data.id){
            return has = true
          }
        });
        if(has) return layer.msg('您已是 ['+ (data.groupname||'') +'] 的群成員',{anim: 6});
        obj[cache.group.length] = data;
        cache.group.push(data);
      }
    }
    
    if(has) return;

    var list = laytpl(listTpl({
      type: data.type
      ,item: 'd.data'
      ,index: data.type === 'friend' ? 'data.groupIndex' : null
    })).render({data: obj});

    if(data.type === 'friend'){
      var li = listElem.find('>li').eq(data.groupIndex);
      li.find('.layui-layim-list').append(list);
      li.find('.layim-count').html(cache.friend[data.groupIndex].list.length); //刷新好友數量
      //如果初始沒有好友
      if(li.find('.layim-null')[0]){
        li.find('.layim-null').remove();
      }
    } else if(data.type === 'group'){
      listElem.append(list);
      //如果初始沒有群組
      if(listElem.find('.layim-null')[0]){
        listElem.find('.layim-null').remove();
      }
    }
  };
  
  //移出好友或群
  var removeList = function(data){
    var listElem = layimMain.find('.layim-list-'+ data.type);
    var obj = {};
    if(cache[data.type]){
      if(data.type === 'friend'){
        layui.each(cache.friend, function(index1, item1){
          layui.each(item1.list, function(index, item){
            if(data.id == item.id){
              var li = listElem.find('>li').eq(index1);
              var list = li.find('.layui-layim-list>li');
              li.find('.layui-layim-list>li').eq(index).remove();
              cache.friend[index1].list.splice(index, 1); //從cache的friend裡面也刪除掉好友
              li.find('.layim-count').html(cache.friend[index1].list.length); //刷新好友數量  
              //如果一個好友都沒了
              if(cache.friend[index1].list.length === 0){
                li.find('.layui-layim-list').html('<li class="layim-null">該分組下已無好友了</li>');
              }
              return true;
            }
          });
        });
      } else if(data.type === 'group'){
        layui.each(cache.group, function(index, item){
          if(data.id == item.id){
            listElem.find('>li').eq(index).remove();
            cache.group.splice(index, 1); //從cache的group裡面也刪除掉資料
            //如果一個群組都沒了
            if(cache.group.length === 0){
              listElem.html('<li class="layim-null">暫無群組</li>');
            }
            return true;
          }
        });
      }
    }
  };
  
  //查看更多記錄
  var chatListMore = function(){
    var thatChat = thisChat(), chatMain = thatChat.elem.find('.layim-chat-main');
    var ul = chatMain.find('ul'); 
    
    if(ul.find('li').length >= MAX_ITEM){
      var first = ul.find('li').eq(0);
      if(!ul.prev().hasClass('layim-chat-system')){
        ul.before('<div class="layim-chat-system"><span layim-event="chatLog">查看更多記錄</span></div>');
      }
      first.remove();
    }
    chatMain.scrollTop(chatMain[0].scrollHeight + 1000);
    chatMain.find('ul li:last').find('img').load(function(){
      chatMain.scrollTop(chatMain[0].scrollHeight);
    });
  };
  
  //快速鍵發送
  var hotkeySend = function(){
    var thatChat = thisChat(), textarea = thatChat.textarea;
    textarea.focus();
    textarea.off('keydown').on('keydown', function(e){
      var local = layui.data('layim')[cache.mine.id] || {};
      var keyCode = e.keyCode;
      if(local.sendHotKey === 'Ctrl+Enter'){
        if(e.ctrlKey && keyCode === 13){
          sendMessage();
        }
        return;
      }
      if(keyCode === 13){
        if(e.ctrlKey){
          return textarea.val(textarea.val()+'\n');
        }
        if(e.shiftKey) return;
        e.preventDefault();
        sendMessage();
      }
    });
  };
  
  //表情庫
  var faces = function(){
    var alt = ["[微笑]", "[嘻嘻]", "[哈哈]", "[可愛]", "[可憐]", "[挖鼻]", "[吃驚]", "[害羞]", "[擠眼]", "[閉嘴]", "[鄙視]", "[愛你]", "[淚]", "[偷笑]", "[親親]", "[生病]", "[太開心]", "[白眼]", "[右哼哼]", "[左哼哼]", "[噓]", "[衰]", "[委屈]", "[吐]", "[哈欠]", "[抱抱]", "[怒]", "[疑問]", "[饞嘴]", "[拜拜]", "[思考]", "[汗]", "[困]", "[睡]", "[錢]", "[失望]", "[酷]", "[色]", "[哼]", "[鼓掌]", "[暈]", "[悲傷]", "[抓狂]", "[黑線]", "[陰險]", "[怒駡]", "[互粉]", "[心]", "[傷心]", "[豬頭]", "[熊貓]", "[兔子]", "[ok]", "[耶]", "[good]", "[NO]", "[贊]", "[來]", "[弱]", "[草泥馬]", "[神馬]", "[囧]", "[浮雲]", "[給力]", "[圍觀]", "[威武]", "[奧特曼]", "[禮物]", "[鐘]", "[話筒]", "[蠟燭]", "[蛋糕]"], arr = {};
    layui.each(alt, function(index, item){
      arr[item] = layui.cache.dir + 'images/face/'+ index + '.gif';
    });
    return arr;
  }();
  
  
  var stope = layui.stope; //組件事件冒泡
  
  //在焦點處插入內容
  var focusInsert = function(obj, str){
    var result, val = obj.value;
    obj.focus();
    if(document.selection){ //ie
      result = document.selection.createRange(); 
      document.selection.empty(); 
      result.text = str; 
    } else {
      result = [val.substring(0, obj.selectionStart), str, val.substr(obj.selectionEnd)];
      obj.focus();
      obj.value = result.join('');
    }
  };
  
  //事件
  var anim = 'layui-anim-upbit', events = {
    //線上狀態
    status: function(othis, e){
      var hide = function(){
        othis.next().hide().removeClass(anim);
      };
      var type = othis.attr('lay-type');
      if(type === 'show'){
        stope(e);
        othis.next().show().addClass(anim);
        $(document).off('click', hide).on('click', hide);
      } else {
        var prev = othis.parent().prev();
        othis.addClass(THIS).siblings().removeClass(THIS);
        prev.html(othis.find('cite').html());
        prev.removeClass('layim-status-'+(type === 'online' ? 'hide' : 'online'))
        .addClass('layim-status-'+type);
        layui.each(call.online, function(index, item){
          item && item(type);
        });
      }
    }
    
    //編輯簽名
    ,sign: function(){
      var input = layimMain.find('.layui-layim-remark');
      input.on('change', function(){
        var value = this.value;
        layui.each(call.sign, function(index, item){
          item && item(value);
        });
      });
      input.on('keyup', function(e){
        var keyCode = e.keyCode;
        if(keyCode === 13){
          this.blur();
        }
      });
    }
    
    //大分組切換
    ,tab: function(othis){
      var index, main = '.layim-tab-content';
      var tabs = layimMain.find('.layui-layim-tab>li');
      typeof othis === 'number' ? (
        index = othis
        ,othis = tabs.eq(index)
      ) : (
        index = othis.index()
      );
      index > 2 ? tabs.removeClass(THIS) : (
        events.tab.index = index
        ,othis.addClass(THIS).siblings().removeClass(THIS)
      )
      layimMain.find(main).eq(index).addClass(SHOW).siblings(main).removeClass(SHOW);
    }
    
    //展開連絡人分組
    ,spread: function(othis){
      var type = othis.attr('lay-type');
      var spread = type === 'true' ? 'false' : 'true';
      var local = layui.data('layim')[cache.mine.id] || {};
      othis.next()[type === 'true' ? 'removeClass' : 'addClass'](SHOW);
      local['spread' + othis.parent().index()] = spread;
      layui.data('layim', {
        key: cache.mine.id
        ,value: local
      });
      othis.attr('lay-type', spread);
      othis.find('.layui-icon').html(spread === 'true' ? '&#xe61a;' : '&#xe602;');
    }

    //搜索
    ,search: function(othis){
      var search = layimMain.find('.layui-layim-search');
      var main = layimMain.find('#layui-layim-search');
      var input = search.find('input'), find = function(e){
        var val = input.val().replace(/\s/);
        if(val === ''){
          events.tab(events.tab.index|0);
        } else {
          var data = [], friend = cache.friend || [];
          var group = cache.group || [], html = '';
          for(var i = 0; i < friend.length; i++){
            for(var k = 0; k < (friend[i].list||[]).length; k++){
              if(friend[i].list[k].username.indexOf(val) !== -1){
                friend[i].list[k].type = 'friend';
                friend[i].list[k].index = i;
                friend[i].list[k].list = k;
                data.push(friend[i].list[k]);
              }
            }
          }
          for(var j = 0; j < group.length; j++){
            if(group[j].groupname.indexOf(val) !== -1){
              group[j].type = 'group';
              group[j].index = j;
              group[j].list = j;
              data.push(group[j]);
            }
          }
          if(data.length > 0){
            for(var l = 0; l < data.length; l++){
              html += '<li layim-event="chat" data-type="'+ data[l].type +'" data-index="'+ data[l].index +'" data-list="'+ data[l].list +'"><img src="'+ data[l].avatar +'"><span>'+ (data[l].username || data[l].groupname || '佚名') +'</span><p>'+ (data[l].remark||data[l].sign||'') +'</p></li>';
            }
          } else {
            html = '<li class="layim-null">無搜索結果</li>';
          }
          main.html(html);
          events.tab(3);
        }
      };
      if(!cache.base.isfriend && cache.base.isgroup){
        events.tab.index = 1;
      } else if(!cache.base.isfriend && !cache.base.isgroup){
        events.tab.index = 2;
      }
      search.show();
      input.focus();
      input.off('keyup', find).on('keyup', find);
    }

    //關閉搜索
    ,closeSearch: function(othis){
      othis.parent().hide();
      events.tab(events.tab.index|0);
    }
    
    //消息盒子
    ,msgbox: function(){
      var msgboxElem = layimMain.find('.layim-tool-msgbox');
      layer.close(events.msgbox.index);
      msgboxElem.find('span').removeClass(ANIM_MSG).html('');
      return events.msgbox.index = layer.open({
        type: 2
        ,title: '消息盒子'
        ,shade: false
        ,maxmin: true
        ,area: ['600px', '520px']
        ,skin: 'layui-box layui-layer-border'
        ,resize: false
        ,content: cache.base.msgbox
      });
    }
    
    //彈出查找頁面
    ,find: function(){
      layer.close(events.find.index);
      return events.find.index = layer.open({
        type: 2
        ,title: '查找'
        ,shade: false
        ,maxmin: true
        ,area: ['1000px', '520px']
        ,skin: 'layui-box layui-layer-border'
        ,resize: false
        ,content: cache.base.find
      });
    }
    
    //彈出更換背景
    ,skin: function(){
      layer.open({
        type: 1
        ,title: '更換背景'
        ,shade: false
        ,area: '300px'
        ,skin: 'layui-box layui-layer-border'
        ,id: 'layui-layim-skin'
        ,zIndex: 66666666
        ,resize: false
        ,content: laytpl(elemSkinTpl).render({
          skin: cache.base.skin
        })
      });
    }
    
    //關於
    ,about: function(){
      layer.alert('版本： '+ v + '<br>版權所有：<a href="http://layim.layui.com" target="_blank">layim.layui.com</a>', {
        title: '關於 LayIM'
        ,shade: false
      });
    }
    
    //生成換膚
    ,setSkin: function(othis){
      var src = othis.attr('src');
      var local = layui.data('layim')[cache.mine.id] || {};
      local.skin = src;
      if(!src) delete local.skin;
      layui.data('layim', {
        key: cache.mine.id
        ,value: local
      });
      try{
        layimMain.css({
          'background-image': src ? 'url('+ src +')' : 'none'
        });
        layimChat.css({
          'background-image': src ? 'url('+ src +')' : 'none'
        });
      } catch(e) {}
    }
    
    //彈出聊天面板
    ,chat: function(othis){
      var local = layui.data('layim')[cache.mine.id] || {};
      var type = othis.data('type'), index = othis.data('index');
      var list = othis.attr('data-list') || othis.index(), data = {};
      if(type === 'friend'){
        data = cache[type][index].list[list];
      } else if(type === 'group'){
        data = cache[type][list];
      } else if(type === 'history'){
        data = (local.history || {})[index] || {};
      }
      data.name = data.name || data.username || data.groupname;
      if(type !== 'history'){
        data.type = type;
      }
      popchat(data);
    }
    
    //切換聊天
    ,tabChat: function(othis){
      changeChat(othis);
    }
    
    //關閉聊天列表
    ,closeChat: function(othis){
      changeChat(othis.parent(), 1);
    }, closeThisChat: function(){
      changeChat(null, 1);
    }
    
    //展開群組成員
    ,groupMembers: function(othis, e){
      var icon = othis.find('.layui-icon'), hide = function(){
        icon.html('&#xe61a;');
        othis.data('down', null);
        layer.close(events.groupMembers.index);
      }, stopmp = function(e){stope(e)};
      
      if(othis.data('down')){
        hide();
      } else {
        icon.html('&#xe619;');
        othis.data('down', true);
        events.groupMembers.index = layer.tips('<ul class="layim-members-list"></ul>', othis, {
          tips: 3
          ,time: 0
          ,anim: 5
          ,fixed: true
          ,skin: 'layui-box layui-layim-members'
          ,success: function(layero){
            var members = cache.base.members || {}, thatChat = thisChat()
            ,ul = layero.find('.layim-members-list'), li = '', membersCache = {}
            ,hasFull = layimChat.find('.layui-layer-max').hasClass('layui-layer-maxmin')
            ,listNone = layimChat.find('.layim-chat-list').css('display') === 'none';
            if(hasFull){
              ul.css({
                width: $(window).width() - 22 - (listNone || 200)
              });
            }
            members.data = $.extend(members.data, {
              id: thatChat.data.id
            });
            post(members, function(res){
              layui.each(res.list, function(index, item){
                li += '<li data-uid="'+ item.id +'"><a href="javascript:;"><img src="'+ item.avatar +'"><cite>'+ item.username +'</cite></a></li>';
                membersCache[item.id] = item;
              });
              ul.html(li);
              
              //獲取群員
              othis.find('.layim-chat-members').html(res.members||(res.list||[]).length + '人');
              
              //私聊
              ul.find('li').on('click', function(){
                var uid = $(this).data('uid'), info = membersCache[uid]
                popchat({
                  name: info.username
                  ,type: 'friend'
                  ,avatar: info.avatar
                  ,id: info.id
                });
                hide();
              });
              
              layui.each(call.members, function(index, item){
                item && item(res);
              });
            });
            layero.on('mousedown', function(e){
              stope(e);
            });
          }
        });
        $(document).off('mousedown', hide).on('mousedown', hide);
        $(window).off('resize', hide).on('resize', hide);
        othis.off('mousedown', stopmp).on('mousedown', stopmp);
      }
    }
    
    //發送聊天內容
    ,send: function(){
      sendMessage();
    }
    
    //設置發送聊天快速鍵
    ,setSend: function(othis, e){
      var box = events.setSend.box = othis.siblings('.layim-menu-box')
      ,type = othis.attr('lay-type');
      
      if(type === 'show'){
        stope(e);
        box.show().addClass(anim);
        $(document).off('click', events.setSendHide).on('click', events.setSendHide);
      } else {
        othis.addClass(THIS).siblings().removeClass(THIS);
        var local = layui.data('layim')[cache.mine.id] || {};
        local.sendHotKey = type;
        layui.data('layim', {
          key: cache.mine.id
          ,value: local
        });
        events.setSendHide(e, othis.parent());
      }
    }, setSendHide: function(e, box){
      (box || events.setSend.box).hide().removeClass(anim);
    }
    
    //表情
    ,face: function(othis, e){
      var content = '', thatChat = thisChat();

      for(var key in faces){
        content += '<li title="'+ key +'"><img src="'+ faces[key] +'"></li>';
      }
      content = '<ul class="layui-clear layim-face-list">'+ content +'</ul>';
     
     events.face.index = layer.tips(content, othis, {
        tips: 1
        ,time: 0
        ,fixed: true
        ,skin: 'layui-box layui-layim-face'
        ,success: function(layero){
          layero.find('.layim-face-list>li').on('mousedown', function(e){
            stope(e);
          }).on('click', function(){
            focusInsert(thatChat.textarea[0], 'face' +  this.title + ' ');
            layer.close(events.face.index);
          });
        }
      });
      
      $(document).off('mousedown', events.faceHide).on('mousedown', events.faceHide);
      $(window).off('resize', events.faceHide).on('resize', events.faceHide);
      stope(e);
      
    } ,faceHide: function(){
      layer.close(events.face.index);
    }
    
    //圖片或一般檔
    ,image: function(othis){
      var type = othis.data('type') || 'images', api = {
        images: 'uploadImage'
        ,file: 'uploadFile'
      }
      ,thatChat = thisChat(), upload = cache.base[api[type]] || {};
  
      layui.upload({
        url: upload.url || ''
        ,method: upload.type
        ,elem: othis.find('input')[0]
        ,unwrap: true
        ,type: type
        ,success: function(res){
          if(res.code == 0){
            res.data = res.data || {};
            if(type === 'images'){
              focusInsert(thatChat.textarea[0], 'img['+ (res.data.src||'') +']');
            } else if(type === 'file'){
              focusInsert(thatChat.textarea[0], 'file('+ (res.data.src||'') +')['+ (res.data.name||'下載檔案') +']');
            }
            sendMessage();
          } else {
            layer.msg(res.msg||'上傳失敗');
          }
        }
      });
    }
    
    //擴展工具列
    ,extend: function(othis){
      var filter = othis.attr('lay-filter')
      ,thatChat = thisChat();
      
      layui.each(call['tool('+ filter +')'], function(index, item){
        item && item(function(content){
          focusInsert(thatChat.textarea[0], content)
        });;
      });
    }
    
    //聊天記錄
    ,chatLog: function(othis){
      var thatChat = thisChat();
      if(!cache.base.chatLog){
        return layer.msg('未開啟更多聊天記錄');
      }
      layer.close(events.chatLog.index);
      return events.chatLog.index = layer.open({
        type: 2
        ,maxmin: true
        ,title: '與 '+ thatChat.data.name +' 的聊天記錄'
        ,area: ['450px', '100%']
        ,shade: false
        ,offset: 'rb'
        ,skin: 'layui-box'
        ,anim: 2
        ,id: 'layui-layim-chatlog'
        ,content: cache.base.chatLog + '?id=' + thatChat.data.id + '&type=' + thatChat.data.type
      });
    }
    
    //歷史會話右鍵功能表操作
    ,menuHistory: function(othis, e){
      var local = layui.data('layim')[cache.mine.id] || {};
      var parent = othis.parent(), type = othis.data('type');
      var hisElem = layimMain.find('.layim-list-history');
      var none = '<li class="layim-null">暫無歷史會話</li>'

      if(type === 'one'){
        var history = local.history;
        delete history[parent.data('index')];
        local.history = history;
        layui.data('layim', {
          key: cache.mine.id
          ,value: local
        });
        $('#'+parent.data('id')).remove();
        if(hisElem.find('li').length === 0){
          hisElem.html(none);
        }
      } else if(type === 'all') {
        delete local.history;
        layui.data('layim', {
          key: cache.mine.id
          ,value: local
        });
        hisElem.html(none);
      }
      
      layer.closeAll('tips');
    }
    
  };
  
  //暴露介面
  exports('layim', new LAYIM());

}).addcss(
  'modules/layim/layim.css?v=3.0Pro'
  ,'skinlayimcss'
);
