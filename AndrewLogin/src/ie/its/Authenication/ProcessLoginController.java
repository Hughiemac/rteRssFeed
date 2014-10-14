package ie.its.Authenication;

import ie.its.loginuser.LoginUser;
import ie.its.loginuser.LoginUserDaoImpl;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class ProcessLoginController
 */
public class ProcessLoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ProcessLoginController() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();

		String username = request.getParameter("username");
		String password = request.getParameter("password");

		LoginUserDaoImpl dao = new LoginUserDaoImpl();

		// check credentials
		LoginUser loginUser=null;
		try {
			loginUser = dao.getValidUser(username, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		if (null == loginUser){
			// should send some error message as well
			response.sendRedirect("LoginForm.jsp?message=invalidUser");
		}else{
			session.setAttribute("loginUser", loginUser);

			response.sendRedirect("site/Welcome");
			
		}
	}

}
