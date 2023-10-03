package com.example.helpme.model;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class User {

    private String id;
    private String username;
    private String imageURL;
    private Map<String,String> contacts;
    private String phoneNumber;

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", contacts=" + contacts +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    public User(String id, String username, String imageURL, Map<String,String> contacts, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.contacts=  contacts;
        this.phoneNumber = phoneNumber;
    }


    public ArrayList<User> getUsersFromContacts(DataSnapshot ref){
        ArrayList<User> users = new ArrayList();

        if(contacts!=null){
            for (String contactId:contacts.values()) {
                User u = ref.child(contactId).getValue(User.class);
                users.add(u);
            }
        }

        return users;
    }

    public User() {
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Map<String,String> getContacts(){return contacts;}
    public void setContacts( Map<String,String> contacts){this.contacts = contacts;}
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }


}

