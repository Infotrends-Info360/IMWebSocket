<!DOCTYPE html>
<html><head>
  <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
  <script src="//cdn.jsdelivr.net/sockjs/1.0.3/sockjs.min.js"></script>
  <script src="stomp.js"></script>
  <style>
      .box {
          width: 440px;
          float: left;
          margin: 0 20px 0 20px;
      }

      .box div, .box input {
          border: 1px solid;
          -moz-border-radius: 4px;
          border-radius: 4px;
          width: 100%;
          padding: 5px;
          margin: 3px 0 10px 0;
      }

      .box div {
          border-color: grey;
          height: 300px;
          overflow: auto;
      }

      div code {
          display: block;
      }

      #first div code {
          -moz-border-radius: 2px;
          border-radius: 2px;
          border: 1px solid #eee;
          margin-bottom: 5px;
      }

      #second div {
          font-size: 0.8em;
      }
  </style>
  <title>RabbitMQ Web STOMP Examples : Temporary Queue</title>
  <link href="main.css" rel="stylesheet" type="text/css"/>
</head><body lang="en">
    <h1><a href="index.html">RabbitMQ Web STOMP Examples</a> > Temporary Queue</h1>

    <p>When you type text in the form's input, the application will send a message to the <code>/queue/test</code> destination
      with the <code>reply-to</code> header set to <code>/temp-queue/foo</code>.</p>
    <p>The STOMP client sets a default <code>onreceive</code> callback to receive messages from this temporary queue and display the message's text.</p>
    <p>Finally, the client subscribes to the <code>/queue/test</code> destination. When it receives message from this destination, it reverses the message's
       text and reply by sending the reversed text to the destination defined by the message's <code>reply-to</code> header.</p>

    <div id="first" class="box">
      <h2>Received</h2>
      <div></div>
      <form><input autocomplete="off" placeholder="Type here..."></input></form>
    </div>

    <div id="second" class="box">
      <h2>Logs</h2>
      <div></div>
    </div>

    <script>
	  // how to test : http://localhost:8080/IMWebSocket/index.jsp?ws - 記得加上?ws
      if (location.search == '?ws') {
          var ws = new WebSocket('ws://' + window.location.hostname + ':15674/ws');
          console.log('ws://' + window.location.hostname + ':15674/ws');
      } else {
          var ws = new SockJS('http://' + window.location.hostname + ':15674/stomp');
          console.log('ws://' + window.location.hostname + ':15674/stomp');
      }
      var client = Stomp.over(ws);

      client.debug = function(e) {
        $('#second div').append($("<code>").text(e));
      };

      // default receive callback to get message from temporary queues
      client.onreceive = function(m) {
    	console.log("onreceive");
    	console.log(m);
        $('#first div').append($("<code>").text(m.body));
      }

      var on_connect = function(x) {
          id = client.subscribe("/queue/BACKEND_TO_JS_QUEUE", function(m) {
        	  console.log("subscribe - /queue/BACKEND_TO_JS_QUEUE");
        	  console.log(m);
        	  
            // reply by sending the reversed text to the temp queue defined in the "reply-to" header
            var reversedText = m.body.split("").reverse().join("");
            client.send(m.headers['reply-to'], {"content-type":"text/plain"}, reversedText);
            
            // ±N¨Æ¥óÅÞ¿è©ñ¦b¤U­±
//             var obj = JSON.parse(m.body);
//             var event = obj.event;
//             if (event == "userjoing"){
//             	// ......
//             }
//             if (event == "userjoing"){
//             	// ......
//             }
            
            
          });
      };
      var on_error =  function() {
        console.log('error');
      };
      client.connect('spring', '123456', on_connect, on_error, '/');

      $('#first form').submit(function() {
        var text = $('#first form input').val();
        if (text) {
          // '/temp-queue/foo' -> «Ø¥ß°_¤@­Ó¼È®Éªºqueue,µ¹¦¬¨ì¦¹°T®§ªº¤H¥Î,¬Ý¥L·Q¤£·Q¦^ÂÐ¦^¨Ó
          client.send('/queue/BACKEND_TO_JS_QUEUE', {'reply-to': '/temp-queue/foo'}, text);
            $('#first form input').val("");
          }
          return false;
      });
    </script>
</body></html>
