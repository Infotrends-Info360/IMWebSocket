package filter;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.java_websocket.WebSocketImpl;

import util.Util;
import websocket.WebSocket;

public class startFilter implements Filter {
	/** * initialization */
	public void init(FilterConfig fc) throws ServletException {
		this.startWebsocketOnline();
	}

	/** * Start Socket Service */
	public void startWebsocketOnline() {

		Util.getConsoleLogger().info("Starting websocket");
		WebSocketImpl.DEBUG = false;
		int port = Integer.parseInt(Util.getSystemParam().get("websocket_port"));
//		Util.getConsoleLogger().debug("WebSocket port: " + port);
//		int port = 8888;
		WebSocket s = null;
		try {
			s = new WebSocket(port);
			s.start();
		} catch (UnknownHostException e) {
			Util.getConsoleLogger().error("Starting websocket failed！");
			Util.getConsoleLogger().error(e.getMessage());
			e.printStackTrace();
		}
		Util.getConsoleLogger().info("Starting websocket success！");

//		Log4jDemo();

	}

	// 為Log4j熟悉API用,之後可刪除
//	private void Log4jDemo() {
//		// PropertyConfigurator
//		// .configure("/WEB-INF/classes/websocket/Log4j.properties");
////		PropertyConfigurator
////				.configure("build/classes/demo/log4j/Log4j.properties");
//		PropertyConfigurator
//		.configure("WEB-INF/log4j.properties");
//		
//		// 使用
//		Logger logger = Logger.getLogger(startFilter.class);
//
//		// 對應的 Log4j.properties 設定要在等級 Info 之上才會顯示，所以logger.debug 不會出現
//		logger.debug("Hello Log4j, this is debug message");
//
//		// 以下的訊息會出現在 console 和 log file 中
//		logger.info("Hi Log4j, this will appear in console and log file");
//		logger.error("This is error message!!!");
//	}

	public void destroy() {
	}

	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		
	}

}
