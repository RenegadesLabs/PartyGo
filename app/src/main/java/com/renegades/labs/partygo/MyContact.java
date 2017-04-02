package com.renegades.labs.partygo;

/**
 * Created by Виталик on 04.12.2016.
 */

public class MyContact{
    private String name;
    private String phone;
    private boolean isChecked;
    private int priority;
    private int id;

    public MyContact() {
        this.priority = 0;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MyContact){
            MyContact contact = (MyContact) obj;
            if (this.name.equals(contact.getName())){
                if (this.phone.equals(contact.getPhone())){
                    return true;
                }
            }
        }

        return false;
    }
}
