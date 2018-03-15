package com.parivartan.medico.model;

/**
 * Created by user on 14-Mar-18.
 */

public class UserFills {
    boolean profile;
    boolean history;
    public UserFills(){
        profile=false;
        history=false;
    }

    public void setProfile(boolean profile) {
        this.profile = profile;
    }

    public void setHistory(boolean history) {
        this.history = history;
    }

    public boolean getProfile() {
        return profile;
    }
    public boolean getHistory() {
        return history;
    }
}
