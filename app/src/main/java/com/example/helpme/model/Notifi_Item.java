package com.example.helpme.model;

import java.util.Date;

public class Notifi_Item {
    public boolean isActive;
   public String id;
   public User user;
   public Date currentDate;
   public int stat;

    public Notifi_Item() {
    }

    public Notifi_Item(boolean isActive, String id, User user, Date currentDate, int ic_baseline_person_pin_circle_24) {
        this.isActive = isActive;
        this.id = id;
        this.user = user;
        this.currentDate = currentDate;
        this.stat = ic_baseline_person_pin_circle_24;
    }
}
