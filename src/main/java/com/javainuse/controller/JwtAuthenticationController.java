package com.javainuse.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javainuse.dao.UserDao;
import com.javainuse.model.*;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.javainuse.service.JwtUserDetailsService;


import com.javainuse.config.JwtTokenUtil;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

	@Value("${spring.datasource.url}")
	private String datasourceUrl;

	@Value("${spring.datasource.username}")
	private String datasourceUsername;

	@Value("${spring.datasource.password}")
	private String datasourcePassword;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService userDetailsService;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

		final UserDetails userDetails = userDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());

		final String token = jwtTokenUtil.generateToken(userDetails);
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setToken(token);
		responseMessage.setMessage("Successfully logged in!");

		String result = printObjecto(responseMessage);
		return ResponseEntity.ok(result);
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> saveUser(@RequestBody UserDTO user) throws Exception {
		String result ="";
		String token = null;
		DAOUser userInfo = new DAOUser();
		ResponseMessage responseMessage = new ResponseMessage();
		int count = 0;
		boolean isValid = false;
		try
		{
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			// create our mysql database connection
			String myDriver = "org.gjt.mm.mysql.Driver";
			String myUrl = this.datasourceUrl;
			//	Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, this.datasourceUsername, this.datasourcePassword);

			// our SQL SELECT query.
			// if you only need a few columns, specify them by name instead of using "*"
			String query = "SELECT * FROM user";

			// create the java statement
			Statement st = conn.createStatement();

			// execute the query, and get a java resultset
			ResultSet rs = st.executeQuery(query);

			List<String> allUsernamesFromdb = new ArrayList<>();
			while (rs.next()){
				allUsernamesFromdb.add(rs.getString("username"));
			}
			st.close();

			if(user.getUsername().equals("") || user.getPassword().equals("")) {
				responseMessage.setError("Invalid username or password");
				responseMessage.setToken("null");
				result += printObjecto(responseMessage);
				return ResponseEntity.ok(result);
			}
			else if(allUsernamesFromdb.contains(user.getUsername())) {
				responseMessage.setError("This user has already been registered!");
				responseMessage.setToken("null");
				result = printObjecto(responseMessage);
				return ResponseEntity.ok(result);
			} else {
				userInfo = userDetailsService.save(user);
				token = jwtTokenUtil.generateToken(new org.springframework.security.core.userdetails.User(
						userInfo.getUsername(),
						userInfo.getPassword(),
						new ArrayList<>()
				));
				responseMessage.setToken(token);
				responseMessage.setMessage("Successfully registered!");
				result = printObjecto(responseMessage);
				return ResponseEntity.ok(result);
			}

		}
		catch (Exception e)
		{
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}

		return ResponseEntity.ok(result);
	}

	private void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			String result = "";
			ResponseMessage responseMessage = new ResponseMessage();
			responseMessage.setToken("null");
			responseMessage.setMessage("Wrong credentials!");
			result = printObjecto(responseMessage);
			ResponseEntity.ok(result);
			e.getMessage();
		}
	}

	public String printObjecto(Object object) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String result;
		result = gson.toJson(object);
		return result;
	}
}