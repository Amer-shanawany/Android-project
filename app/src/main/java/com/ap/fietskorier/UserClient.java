package com.ap.fietskorier;

import android.app.Application;

public class UserClient extends Application {
    //this class is help full to instantiate User object
    // in order to be able to use it globally
    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
