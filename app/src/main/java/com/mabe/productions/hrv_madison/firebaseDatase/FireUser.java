package com.mabe.productions.hrv_madison.firebaseDatase;

/**
 * Created by Benas on 8/7/2018.
 */

public class FireUser {

    private String id;
    private String identifier;
    private String email;
    private String password;
    private boolean doneInitial;

    public FireUser(String id, String identifier, String email, String password, boolean doneInitial) {
        this.id = id;
        this.identifier = identifier;
        this.email = email;
        this.password = password;
        this.doneInitial = doneInitial;
    }


    public FireUser(){
    }

    public String getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isDoneInitial() {
        return doneInitial;
    }
}
