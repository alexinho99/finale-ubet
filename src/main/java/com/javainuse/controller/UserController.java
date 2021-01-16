package com.javainuse.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javainuse.dao.UserDao;
import com.javainuse.model.DAOUser;
import com.javainuse.model.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserDao daoUser;

    @RequestMapping(value = "/deposit", method = RequestMethod.POST)
     public ResponseEntity<?> insertCredit(@RequestBody UserDTO user) {

        String result;
        ResponseMessage responseMessage;
        DAOUser client = null;


        if(daoUser.findByUsername(user.getUsername()) != null){

            client = daoUser.findByUsername(user.getUsername());
            double currentAmount = client.getBalance();
            client.setBalance(user.getBalance() + currentAmount);
            daoUser.save(client);
             responseMessage = new ResponseMessage();
            responseMessage.setMessage("Successfully added: " + user.getBalance());
        } else{
            responseMessage  = new ResponseMessage();
            responseMessage.setMessage("There is not such registered user!");
        }

        result = printObject(client);
        result = result + printObject(responseMessage);
        return ResponseEntity.ok(result);
    }

    public String printObject(Object object) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String result;
        result = gson.toJson(object);
        return result;
    }
}
