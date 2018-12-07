package com.mabe.productions.hrv_madison.firebase;

public class FireGlobalUser {

    private String displayname;
    private String email;

    public FireGlobalUser(){

    }

    public FireGlobalUser(String displayname, String email) {
        this.displayname = displayname;
        this.email = email;
    }

    public String getDisplayname() {
        return displayname;
    }

    public String getEmail() {
        return email;
    }
}
