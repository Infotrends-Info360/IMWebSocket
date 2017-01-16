package filter;

import java.io.IOException;
import java.net.UnknownHostException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.java_websocket.WebSocketImpl;
import websocket.WebSocket;

public class startFilter implements Filter {
	/** * initialization */
	public void init(FilterConfig fc) throws ServletException {
		this.startWebsocketOnline();
	}

	/** * Start Socket Service */
	public void startWebsocketOnline() {
		
		System.out.println("Starting websocket");
		WebSocketImpl.DEBUG = false;
		int port = 8888;
		WebSocket s = null;
		try {
			s = new WebSocket(port);
			s.start();
		} catch (UnknownHostException e) {
			System.out.println("Start websocket fail！");
			e.printStackTrace();
		}
		System.out.println("Start websocket success！");
		
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
	}
}
