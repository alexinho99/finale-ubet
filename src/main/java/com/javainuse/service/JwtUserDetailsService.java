package com.javainuse.service;

import java.util.ArrayList;

import com.javainuse.controller.FootballEvent;
import com.javainuse.dao.MatchDao;
import com.javainuse.model.MatchesEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.javainuse.dao.UserDao;
import com.javainuse.model.DAOUser;
import com.javainuse.model.UserDTO;

@Service
public class JwtUserDetailsService implements UserDetailsService{
	
	@Autowired
	private UserDao userDao;

	@Autowired
	private MatchDao matchDao;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		DAOUser user = userDao.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				new ArrayList<>());
	}
	
	public DAOUser save(UserDTO user) {
		DAOUser newUser = new DAOUser();
		newUser.setUsername(user.getUsername());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		return userDao.save(newUser);
	}

//	public MatchesEntity saveMatch(FootballEvent event)  {
//		MatchesEntity matchesEntity = new MatchesEntity();
//		matchesEntity.setHomeTeam(event.getHomeTeam());
//		matchesEntity.setAwayTeam(event.getAwayTeam());
//		matchesEntity.setFirstTeamToWin(event.getFirstTeamToWin());
//		matchesEntity.setDraw(event.getDraw());
//		matchesEntity.setSecondTeamToWin(event.getSecondTeamToWin());
//		matchesEntity.setHref(event.getHref());
//		return matchDao.save(matchesEntity);
//	}
}