package com.planner;

import com.planner.model.User;

public class CurrentUser {
    private static User currentUser;

    public static void set(User user) {
        currentUser = user;
    }

    public static User get() {
        return currentUser;
    }
}
