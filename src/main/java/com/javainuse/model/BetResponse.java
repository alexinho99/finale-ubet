package com.javainuse.model;

import java.io.Serializable;

public class BetResponse implements Serializable {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
