package com.javainuse.controller;

import com.javainuse.dao.UserDao;
import com.javainuse.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {

    @Autowired
    private UserDao daoUser;

    @RequestMapping(value = "/deposit", method = RequestMethod.POST)
    public ResponseEntity<?> insertCredit(@RequestBody DepositRequest depositRequest, Principal principal) {

        DAOUser user = daoUser.findByUsername(principal.getName());

        user.setBalance(user.getBalance() + depositRequest.getAmount());
        daoUser.save(user);

        DepositResponse depositResponse = new DepositResponse();
        depositResponse.setMessage("Successfully added " + depositRequest.getAmount() + " to balance!");

        return new ResponseEntity<>(depositResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<?> insertCredit(Principal principal) {

        DAOUser user = daoUser.findByUsername(principal.getName());

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
