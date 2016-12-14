package com.renegades.labs.partygo;

/**
 * Created by Виталик on 04.12.2016.
 */

public class MyContact{
    private String name;
    private String phone;
    private int id;

    public MyContact() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public int getId() {
        return id;
    }
}
