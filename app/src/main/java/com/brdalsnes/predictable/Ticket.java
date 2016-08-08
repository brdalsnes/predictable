package com.brdalsnes.predictable;

/**
 * Created by Bjornar on 14/08/2015.
 */
public class Ticket {

    public String type;
    public int number;

    public Ticket(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Ticket(String type, int number){
        this.type = type;
        this.number = number;
    }
}
