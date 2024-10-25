package com.app.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.app.utils.DbConnection;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/regform")
public class Register extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		PrintWriter out = resp.getWriter();
		resp.setContentType("text/html");

		String name = req.getParameter("name");
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		String city = req.getParameter("city");

		try {
			Connection con = DbConnection.getConnection();

			String insertSqlQuery = "INSERT into register VALUES (?,?,?,?)";
			PreparedStatement stmt = con.prepareStatement(insertSqlQuery);

			stmt.setString(1, name);
			stmt.setString(2, email);
			stmt.setString(3, password);
			stmt.setString(4, city);

			int count = stmt.executeUpdate();

			if (count > 0) {
				out.println("<h3 style= 'color :green'> Registered Successfully");

				RequestDispatcher rd = req.getRequestDispatcher("/login.html");

				rd.include(req, resp);
			} else {
				out.println("<h3 style= 'color :green'> User not registered due to some error");

				RequestDispatcher rd = req.getRequestDispatcher("/register.html");

				rd.include(req, resp);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
