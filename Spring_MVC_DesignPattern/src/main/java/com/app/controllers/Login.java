package com.app.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.app.models.User;
import com.app.utils.DbConnection;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/loginform")
public class Login extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		PrintWriter out = resp.getWriter();
		resp.setContentType("text/html");

		String email = req.getParameter("email");
		String password = req.getParameter("password");

		try {
			Connection con = DbConnection.getConnection();

			String selectSqlQuery = "select * from register where email = ? AND password = ?";
			PreparedStatement stmt = con.prepareStatement(selectSqlQuery);

			stmt.setString(1, email);
			stmt.setString(2, password);

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				
				User user = new User();
				
				user.setName(rs.getString("name"));
				user.setEmail(rs.getString("email"));
				user.setCity(rs.getString("city"));
				
				HttpSession session = req.getSession();
				
				session.setAttribute("session_user", user);
				
				RequestDispatcher rd = req.getRequestDispatcher("/profile.jsp");

				rd.include(req, resp);
			} else {
				out.println("<h3 style= 'color :red'> Email id and Password did not match </h3>");

				RequestDispatcher rd = req.getRequestDispatcher("/login.html");

				rd.include(req, resp);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
}
