package com.brdalsnes.predictable;

/**
 * Created by Bjornar on 16/07/2016.
 */
public class User {
    public String username;
    public String email;
    public double money;
    public double netWorth;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, double money) {
        this.username = username;
        this.email = email;
        this.money = money;
        this.netWorth = money; //Net worth is initialized equal to money
    }
}
