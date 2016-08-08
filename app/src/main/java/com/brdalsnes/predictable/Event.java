package com.brdalsnes.predictable;

import java.io.Serializable;

/**
 * Created by Bjornar on 05/08/2015.
 */
public class Event implements Serializable {

    private String objectId;
    private String ticker;
    private String name;
    private String description;
    private String category;
    private double marketValue;
    private int tickets;
    private int dateCreated;
    private int dateDeadline;
    private String image;
    private int yes;
    private int no;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getYes() {
        return yes;
    }

    public void setYes(int yes) {
        this.yes = yes;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getValue() {
        return marketValue;
    }

    public void setValue(double marketValue) {
        this.marketValue = marketValue;
    }

    public int getTickets() {
        return tickets;
    }

    public void setTickets(int tickets) {
        this.tickets = tickets;
    }

    public int getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(int dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getDateDeadline() {
        return dateDeadline;
    }

    public void setDateDeadline(int dateDeadline) {
        this.dateDeadline = dateDeadline;
    }

}