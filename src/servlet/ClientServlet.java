package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class ClientServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
//		super.doPost(req, resp);
		System.out.println("content" + req.getParameter("content"));
		req.getSession().setAttribute("content", req.getParameter("content"));
		req.getRequestDispatcher("/Client.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
//		System.out.println("content" + req.getParameter("content"));
//		req.getSession().setAttribute("content", req.getParameter("content"));
//		req.getRequestDispatcher("/Agent.jsp").forward(req, resp);
		super.doGet(req, resp);
	}
}