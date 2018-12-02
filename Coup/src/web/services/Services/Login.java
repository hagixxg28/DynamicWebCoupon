package web.services.Services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Cookie;

import b.exceptions.CouponSystemException;
import e.enums.ClientType;
import j.facade.CouponClientFacade;
import k.couponSystem.CouponSystem;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private CouponSystem sys = CouponSystem.getInstance();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Long id = Long.valueOf(request.getParameter("id"));
		String password = request.getParameter("password");
		String type = request.getParameter("type");
		ClientType parseType = null;
		switch (type) {
		case "customer":
			parseType = ClientType.CUSTOMER;
			break;
		case "company":
			parseType = ClientType.COMPANY;
			break;
		case "admin":
			parseType = ClientType.ADMIN;
			break;

		default:
			return;
		}
		try {

			CouponClientFacade facade = sys.login(id, password, parseType);
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.invalidate();
			}
			session = request.getSession();
			session.setAttribute("facade", facade);
			javax.ws.rs.core.Cookie cookie = new Cookie("facade", "test");

		} catch (CouponSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

}
