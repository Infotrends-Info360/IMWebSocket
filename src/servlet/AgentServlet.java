package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class AgentServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
//		super.doPost(req, resp);
		//Util.getConsoleLogger().debug("content" + req.getParameter("content"));
		//req.getSession().setAttribute("content", req.getParameter("content"));
		req.getRequestDispatcher("/Agent.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
//		Util.getConsoleLogger().debug("content" + req.getParameter("content"));
//		req.getSession().setAttribute("content", req.getParameter("content"));
//		req.getRequestDispatcher("/Agent.jsp").forward(req, resp);
		super.doGet(req, resp);
	}
}