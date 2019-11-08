package com.mabe.productions.pr_ipulsus_running.firebase;

/**
 * This class represents the database structure of a the global user table row.
 * See documentation for more info.
 */

public class FireGlobalUser {

    private String displayname;
    private String email;

    public FireGlobalUser() {

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
