package com.javainuse.controller;

import com.javainuse.model.JwtResponse;

public class ResponseMessage {

    private String message;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ResponseMessage() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
