package com.javainuse.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javainuse.dao.UserDao;
import com.javainuse.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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

	@Autowired
	private UserDao daoUser;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
		} catch (Exception e) {
			String result = "";
			ResponseMessage responseMessage = new ResponseMessage();
			responseMessage.setToken(null);
			responseMessage.setMessage("Wrong credentials!");
			result = printObject(responseMessage);
			return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
		}

		final UserDetails userDetails = userDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());

		final String token = jwtTokenUtil.generateToken(userDetails);
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setToken(token);
		responseMessage.setMessage("Successfully logged in!");

		String result = printObject(responseMessage);
		return ResponseEntity.ok(result);
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> saveUser(@RequestBody UserDTO user) throws Exception {
		String result ="";
		String token = null;
		DAOUser userInfo = new DAOUser();
		ResponseMessage responseMessage = new ResponseMessage();
		try
		{
//			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
//			String myDriver = "org.gjt.mm.mysql.Driver";
//			String myUrl = this.datasourceUrl;
//			Connection conn = DriverManager.getConnection(myUrl, this.datasourceUsername, this.datasourcePassword);
//			String query = "SELECT * FROM user";
//			Statement st = conn.createStatement();
//			ResultSet rs = st.executeQuery(query);
//
//			List<String> allUsernamesFromdb = new ArrayList<>();
//			while (rs.next()){
//				allUsernamesFromdb.add(rs.getString("username"));
//			}
//			st.close();

			if(user.getUsername().equals("") || user.getPassword().equals("")) {
				responseMessage.setMessage("Invalid username or password");
				responseMessage.setToken(null);
				result += printObject(responseMessage);
				return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
			}
			else if(daoUser.findByUsername(user.getUsername()) != null) {
				responseMessage.setMessage("This user has already been registered!");
				responseMessage.setToken(null);
				result = printObject(responseMessage);
				return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
			} else {
				userInfo = userDetailsService.save(user);
				token = jwtTokenUtil.generateToken(new org.springframework.security.core.userdetails.User(
						userInfo.getUsername(),
						userInfo.getPassword(),
						new ArrayList<>()
				));
				responseMessage.setToken(token);
				responseMessage.setMessage("Successfully registered!");
				result = printObject(responseMessage);
				return ResponseEntity.ok(result);
			}

		}
		catch (Exception e)
		{
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}

		responseMessage.setToken(null);
		responseMessage.setMessage("Server error!");
		result = printObject(responseMessage);
		return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public String printObject(Object object) {
		Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
		String result;
		result = gson.toJson(object);
		return result;
	}
}