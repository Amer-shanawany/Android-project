package com.ap.fietskorier;

public class User {



    private String user_id;
    private String username;//not implemented in the sign up screen so i'll use a default value when creating an instance
    private String email;
    private int balance = 100 ;


    public User(String user_id, String username, String email){
        this.user_id = user_id;
        this.username = username;
        this.email = email;

    }
    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }



    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
 }
