package com.javainuse.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javainuse.dao.BetDao;
import com.javainuse.dao.MatchDao;
import com.javainuse.dao.UserDao;
import com.javainuse.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.concurrent.locks.ReadWriteLock;

@RestController
public class BetController {

    @Autowired
    private BetDao betDao;

    @Autowired
    private MatchDao matchDao;

    @Autowired
    private UserDao userDao;

    @RequestMapping(value = "/bet", method = RequestMethod.POST)
    public ResponseEntity<?> createBet(@RequestBody BetRequest betRequest, Principal principal) throws Exception {
        BetResponse betResponse = new BetResponse();

        if (betRequest.getTeam() == null || betRequest.getOdd() == null || betRequest.getAmount() == null) {
            betResponse.setMessage("All fields are required.");
            return new ResponseEntity<>(betResponse, HttpStatus.BAD_REQUEST);
        }

        if (!matchDao.findById(betRequest.getMatchId()).isPresent()) {
            betResponse.setMessage("No match found with this id.");
            return new ResponseEntity<>(betResponse, HttpStatus.BAD_REQUEST);
        }

        DAOUser user = userDao.findByUsername(principal.getName());

        try {
            BetEntity bet = new BetEntity();
            bet.setUserId(user.getId());
            bet.setMatchId(betRequest.getMatchId());
            bet.setOdd(betRequest.getOdd());
            bet.setTeam(betRequest.getTeam());
            bet.setAmount(betRequest.getAmount());

            betDao.save(bet);

            betResponse.setMessage("Bet created successfully");
            return new ResponseEntity<>(betResponse, HttpStatus.OK);
        } catch (Exception e) {
            betResponse.setMessage("Server error!");
            return new ResponseEntity<>(betResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/bets", method = RequestMethod.GET)
    public ResponseEntity<?> getAllBets(Principal principal) throws Exception{
        DAOUser user = userDao.findByUsername(principal.getName());

        return new ResponseEntity<>(betDao.getAllByUserId(user.getId()), HttpStatus.OK);
    }

    @RequestMapping(value = "/bet/{matchId}", method = RequestMethod.GET)
    public ResponseEntity<?> getBetById(@PathVariable Integer matchId) throws Exception{
        return new ResponseEntity<>(betDao.getByMatchId(matchId), HttpStatus.OK);
    }

    public String printObject(Object object) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String result;
        result = gson.toJson(object);
        return result;
    }
}
